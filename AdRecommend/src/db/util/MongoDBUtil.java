package db.util;

import com.mongodb.*;
import java.net.UnknownHostException;

public class MongoDBUtil {
    private static final String MONGO_DB_URL = "210.42.123.27";
    private static final int MONGO_DB_PORT = 27017;
    private static final String MONGO_DB_DBNAME = "computer";

    public static DB getConn() throws UnknownHostException {
        Mongo mongo = new Mongo(MONGO_DB_URL, MONGO_DB_PORT);
        DB db = mongo.getDB(MONGO_DB_DBNAME);
        return db;
    }

}
