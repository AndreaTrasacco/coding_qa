package it.unipi.lsmsd.coding_qa.dao.base;

import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.*;

public abstract class BaseMongoDBDAO {
    protected static MongoClient mongoClient;
    private final static String URI = "mongodb://10.1.1.15:27020,10.1.1.16:27020,10.1.1.17:27020/?retryWrites=true&w=1&wtimeout=10000&readPreference=secondaryPreferred";
    protected static final String DB_NAME = "codingqa";

    public static MongoClient getConnection() {
        return MongoClients.create(URI);
    }

    public static void init() {
        mongoClient = MongoClients.create(URI);
    }

    public static TransactionOptions txnOptions = TransactionOptions.builder()
            .readPreference(ReadPreference.primary())
            .writeConcern(WriteConcern.W1)
            .build();

    public static void close() {
        mongoClient.close();
    }
}
