package org.chats.server;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;
import org.chats.messenger.User;
import org.chats.messenger.Fields;

/**
 * Class contains all the necessary information about MongoDB instance
 * and "query methods" that implements messenger logics
 */
public class Mongo {

    public static final String MONGOUSER = "chatsuser";
    public static final String MONGOPASS = "1Qh56U78";
    public static final String MONGOHOST = "192.168.0.109";
    public static final String MONGOPORT = "27017";
    public static final String MONGODB = "chatsDB";

    public static final String USERSCOLL = "users";
    public static final String MESSCOLL = "messages";

    /**
     * The query is used for getting document by username
     * and password
     */
    public static Bson userQuery(String username, String password){
        return Filters.and(Filters.eq(User.USERNAME, username), Filters.eq(User.PASSWORD, password));
    }
    /**
     * The query is used for getting document by username
     */
    public static Bson userQuery(String username){
        return Filters.eq(User.USERNAME, username);
    }
    /**
     * The query is used for updating message state
     */
    public static Bson messUpdateState(String state){
        return Updates.set(Fields.STATE, state);
    }
    public static Bson undeliveredMessages(String username){
        return Filters.and(Filters.eq(Fields.RECIEVER, username), Filters.eq(Fields.STATE, MessState.QUEUED));
    }
    /**
     * The query is used for updating user state
     */
    public static Bson userUpdateState(String state){
        return Updates.set(User.STATUS, state);
    }
    /**
     * The query is used for getting all the messages related
     * with certain user
     */
    public static Bson messRelatedWithUser(String username){
        return Filters.or(Filters.eq(Fields.SENDER, username), Filters.eq(Fields.RECIEVER, username));
    }

    private Mongo(){}

}
