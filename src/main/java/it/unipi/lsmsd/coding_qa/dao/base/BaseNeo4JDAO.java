package it.unipi.lsmsd.coding_qa.dao.base;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

public abstract class BaseNeo4JDAO {
    private static final String NEO4J_URI = "neo4j://localhost:7687";
    private static final String NEO4J_USER = "neo4j";
    private static final String NEO4J_PASSWORD = "nicolo";

    private static Driver Neo4jDriver;

    //method that returns the driver instance
    private static Driver getConnection(){
        return Neo4jDriver;
    }

    //method that return the session
    public static Session getSession(){
        return getConnection().session();
    }

    //method that initialize the Neo4j driver
    public static void initPool(){
        Neo4jDriver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, NEO4J_PASSWORD));
    }

    //method that closes the Neo4j connection
    public static void close(){
        Neo4jDriver.close();
    }

}
