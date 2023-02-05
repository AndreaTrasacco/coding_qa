package it.unipi.lsmsd.coding_qa.dao;

import it.unipi.lsmsd.coding_qa.dto.PageDTO;
import it.unipi.lsmsd.coding_qa.dto.QuestionNodeDTO;
import it.unipi.lsmsd.coding_qa.model.Question;
import it.unipi.lsmsd.coding_qa.model.User;

import java.util.List;

public interface QuestionNodeDAO { // TODO ATTENZIONE USARE DETACH, ECCEZIONI, NON USARE "EDGE" riguarda un graph db
    void create(Question question);
    void update(Question question); // TODO CAPIRE SE SERVE DTO
    void delete(String id);
    //void deleteIngoingEdges(String id);
    void deleteAnsweredEdge(String questionId, String nickname); // CAMBIARE NOME (dipende da graph db)
    void close(String questionId);
    PageDTO<QuestionNodeDTO> viewCreatedAndAnsweredQuestions(String nickname); // TODO SEPARARE
    // fare create, update, delete di answer ???

}
