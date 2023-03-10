package it.unipi.lsmsd.coding_qa.dao.mongodb;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.InsertOneResult;
import it.unipi.lsmsd.coding_qa.dao.QuestionDAO;
import it.unipi.lsmsd.coding_qa.dao.base.BaseMongoDBDAO;
import it.unipi.lsmsd.coding_qa.dao.exception.DAOException;
import it.unipi.lsmsd.coding_qa.dto.*;
import it.unipi.lsmsd.coding_qa.model.*;
import it.unipi.lsmsd.coding_qa.utils.Constants;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

public class QuestionMongoDBDAO extends BaseMongoDBDAO implements QuestionDAO {
    @Override
    public void createQuestion(Question question) throws DAOException {
        Document docQuestion = new Document("title", question.getTitle())
                .append("body", question.getBody())
                .append("topic", question.getTopic())
                .append("author", question.getAuthor())
                .append("createdDate", question.getCreatedDate());
        try {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collectionQuestions = mongoDatabase.getCollection("questions");
            InsertOneResult result = collectionQuestions.insertOne(docQuestion);
            // Set the id of the question
            question.setId(result.getInsertedId().asObjectId().getValue().toHexString());
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    @Override
    public void deleteQuestion(String id) throws DAOException {
        try (ClientSession session = mongoClient.startSession()) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_NAME);

            TransactionBody txnBody = (TransactionBody<String>) () -> {
                MongoCollection<Document> collectionQuestions = mongoDatabase.getCollection("questions");
                MongoCollection<Document> collectionUsers = mongoDatabase.getCollection("users");
                Bson fields = fields(excludeId(), include("answers"));
                Document deletedQAnswers = collectionQuestions.findOneAndDelete(session, eq("_id", new ObjectId(id)), new FindOneAndDeleteOptions().projection(fields));
                // For each answer in the deleted question --> get score and update the score of the author of the answer
                if (deletedQAnswers != null && deletedQAnswers.containsKey("answers")) {
                    List<Document> answers = deletedQAnswers.getList("answers", Document.class);
                    for (Document answer : answers) {
                        // Update score of the author of the answer
                        if (answer.getInteger("score") != 0) {
                            collectionUsers.updateOne(session, eq("nickname", answer.getString("author")),
                                    Updates.inc("score", answer.getInteger("score") * (-1)));
                        }
                    }
                }
                return "";
            };
            try {
                session.withTransaction(txnBody, txnOptions);
            } catch (Exception ex) {
                throw new DAOException(ex);
            }
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    @Override
    public Question updateQuestion(Question question) throws DAOException {
        //Only title, body and topic can be updated with this method
        try {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collectionQuestions = mongoDatabase.getCollection("questions");
            Document doc = collectionQuestions.findOneAndUpdate(
                    eq("_id", new ObjectId(question.getId())),
                    Updates.combine(Updates.set("title", question.getTitle()), Updates.set("body", question.getBody()), Updates.set("topic", question.getTopic())),
                    new FindOneAndUpdateOptions().projection(fields(excludeId(), include("title", "body", "topic"))));
            Question oldQuestion = null;
            if (doc != null) {
                oldQuestion = new Question();
                oldQuestion.setId(question.getId());
                oldQuestion.setTitle(doc.getString("title"));
                oldQuestion.setBody(doc.getString("body"));
                oldQuestion.setTopic(doc.getString("topic"));
            }
            return oldQuestion;
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    @Override
    public void reportQuestion(String id, boolean report) throws DAOException {
        try {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collectionQuestions = mongoDatabase.getCollection("questions");
            collectionQuestions.updateOne(eq("_id", new ObjectId(id)), Updates.set("reported", report));
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    @Override
    public QuestionPageDTO getQuestionInfo(String id) throws DAOException {
        try {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collectionQuestions = mongoDatabase.getCollection("questions");
            QuestionPageDTO questionPageDTO = new QuestionPageDTO();
            Document doc = collectionQuestions.find(eq("_id", new ObjectId(id))).projection(fields(excludeId(), include("title", "body", "topic", "author", "createdDate"))).first();
            if (doc == null) return null;
            questionPageDTO.setId(id);
            questionPageDTO.setTitle(doc.getString("title"));
            questionPageDTO.setTopic(doc.getString("topic"));
            questionPageDTO.setBody(doc.getString("body"));
            questionPageDTO.setAuthor(doc.getString("author"));
            questionPageDTO.setCreatedDate(doc.getDate("createdDate"));
            return questionPageDTO;
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    @Override
    public PageDTO<QuestionDTO> getReportedQuestions(int page) throws DAOException {
        PageDTO<QuestionDTO> reportedQuestions = new PageDTO<>();
        List<QuestionDTO> reportedQ = new ArrayList<>();
        try {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collectionQuestions = mongoDatabase.getCollection("questions");

            collectionQuestions.find(eq("reported", true)).projection(fields(include("title", "author", "createdDate", "topic"))).sort(ascending("createdDate")).skip((page - 1) * Constants.PAGE_SIZE).limit(Constants.PAGE_SIZE).forEach(doc -> {
                QuestionDTO temp = new QuestionDTO(doc.getObjectId("_id").toString(),
                        doc.getString("title"),
                        doc.getDate("createdDate"),
                        doc.getString("topic"),
                        doc.getString("author"));
                reportedQ.add(temp);
            });
            reportedQuestions.setCounter(reportedQ.size());
            reportedQuestions.setEntries(reportedQ);
            return reportedQuestions;
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    @Override
    public PageDTO<QuestionDTO> searchQuestions(int page, String searchString, String topicFilter) throws DAOException {
        PageDTO<QuestionDTO> pageDTO = new PageDTO<>();
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        try {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collectionQuestions = mongoDatabase.getCollection("questions");
            int pageOffset = (page - 1) * Constants.PAGE_SIZE;
            TextSearchOptions options = new TextSearchOptions().caseSensitive(false);
            Bson eq = eq("topic", topicFilter);
            Bson text = text(searchString, options);
            Bson project = fields(include("title", "createdDate", "topic", "author"));
            collectionQuestions.find(and(eq, text)).projection(project).skip(pageOffset).limit(Constants.PAGE_SIZE).forEach(doc -> {
                QuestionDTO temp = new QuestionDTO(doc.getObjectId("_id").toString(), doc.getString("title"),
                        doc.getDate("createdDate"), doc.getString("topic"), doc.getString("author"));
                questionDTOList.add(temp);
            });
            pageDTO.setCounter(questionDTOList.size());
            pageDTO.setEntries(questionDTOList);
            return pageDTO;
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    public PageDTO<QuestionDTO> browseQuestions(int page) throws DAOException {
        PageDTO<QuestionDTO> pageDTO = new PageDTO<>();
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        try {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collectionQuestions = mongoDatabase.getCollection("questions");
            int pageOffset = (page - 1) * Constants.PAGE_SIZE;
            Bson project = fields(include("title", "createdDate", "topic", "author"));
            collectionQuestions.find().projection(project).sort(descending("createdDate")).skip(pageOffset).limit(Constants.PAGE_SIZE).forEach(doc -> {
                QuestionDTO temp = new QuestionDTO(doc.getObjectId("_id").toString(), doc.getString("title"),
                        doc.getDate("createdDate"), doc.getString("topic"), doc.getString("author"));
                questionDTOList.add(temp);
            });
            pageDTO.setCounter(questionDTOList.size());
            pageDTO.setEntries(questionDTOList);
            return pageDTO;
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }
}
