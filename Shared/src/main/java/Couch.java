import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.DesignDocument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// Created by Khashayar Abdouli on 8/21/14.
//
public class Couch {


    /**
     * Initializes a couchdb client
     * @return and instance of the couchdb client
     */
    public static CouchDbClient getClient()
    {
        CouchDbProperties properties = new CouchDbProperties()
                .setDbName("screen9")
                .setCreateDbIfNotExist(true)
                .setProtocol("https")
                .setHost("khnapster.cloudant.com")
                .setPort(443)
                .setUsername("khnapster")
                .setPassword("medopadmedopad")
                .setMaxConnections(100)
                .setConnectionTimeout(0);

        return new CouchDbClient(properties);
    }

    /**
     * Saves a toDoList to data best
     * @param toDoList the prepared List from user Input
     */
    public static void save(ToDoList toDoList) {
        CouchDbClient dbClient = getClient();
        dbClient.setGsonBuilder(new GsonBuilder().serializeNulls());
        dbClient.save(toDoList.toJson());
    }

    /**
     * Signs a user up with given username and password and a random ID generated
     * @param username given username
     * @param password given password
     * @param mail given mail
     */
    public static void signup(String username, String password, String mail){
        CouchDbClient dbClient = getClient();
        dbClient.setGsonBuilder(new GsonBuilder().serializeNulls());
        JsonObject json = new JsonObject();
        json.addProperty("user_id", UUID.randomUUID().toString());
        json.addProperty("username", username);
        json.addProperty("password", password);
        json.addProperty("mail", mail);
        dbClient.save(json);

    }

    /**
     * Checks if a given username exists in database or not
     * @param username given username
     * @return true if the username already exists and false in case of non-existing username
     */
    public static boolean checkUser(String username)
    {

        CouchDbClient dbClient = getClient();
        DesignDocument designDocument = new DesignDocument();
        designDocument.setId("_design/Users");
        designDocument.setLanguage("javascript");

        DesignDocument.MapReduce getLatestDoc = new DesignDocument.MapReduce();
        getLatestDoc.setMap("function(doc) {\n" +
                "var user = \""+ username + "\";\n" +
                "    if(doc.username.toUpperCase() == user.toUpperCase())\n" +
                "        emit(doc._id, doc.value);\n" +
                "}");

        Map<String, DesignDocument.MapReduce> view = new HashMap<String, DesignDocument.MapReduce>();
        view.put("valid_user", getLatestDoc);

        designDocument.setViews(view);

        dbClient.design().synchronizeWithDb(designDocument);

        List<JsonObject> list = dbClient.view("Users/valid_user").query(JsonObject.class);

        if (list.size() > 0){
            return true;
        }
        return false;
    }

    /**
     * Trys to sign in
     * @param username given username
     * @param password given password
     * @return the unique ID if the username and password is correct otherwise an empty String
     */
    public static String signIn(String username, String password)
    {

        CouchDbClient dbClient = getClient();
        DesignDocument designDocument = new DesignDocument();
        designDocument.setId("_design/Users");
        designDocument.setLanguage("javascript");

        DesignDocument.MapReduce getLatestDoc = new DesignDocument.MapReduce();
        getLatestDoc.setMap("function(doc) {\n" +
                "var user = \""+ username + "\";\n" +
                "var pass = \""+ password + "\";\n" +
                "    if(doc.username == user && doc.password == pass)\n" +
                "        emit(doc._id, doc.user_id);\n" +
                "}");

        Map<String, DesignDocument.MapReduce> view = new HashMap<String, DesignDocument.MapReduce>();
        view.put("check_user", getLatestDoc);

        designDocument.setViews(view);

        dbClient.design().synchronizeWithDb(designDocument);

        List<JsonObject> list = dbClient.view("Users/check_user").query(JsonObject.class);

        if (list.size() > 0){
            return list.get(0).get("value").getAsString();
        }

        return "";

    }

    /**
     * Fetches all the todoLists for a given User
     * @param userID given userID
     * @return List of all todoLists
     */
    public static Object[][] getList(String userID)
    {

        CouchDbClient dbClient = getClient();
        DesignDocument designDocument = new DesignDocument();
        designDocument.setId("_design/Lists");
        designDocument.setLanguage("javascript");

        DesignDocument.MapReduce getLatestDoc = new DesignDocument.MapReduce();
        getLatestDoc.setMap("function(doc) {\n" +
                "var user = \""+ userID + "\";\n" +
                "    if(doc.username == user)\n" +
                "        emit(doc._id, {\"title\" : doc.title, \"date\" : doc.date , \"location\" : doc.location});\n" +
                "}");

        Map<String, DesignDocument.MapReduce> view = new HashMap<String, DesignDocument.MapReduce>();
        view.put("view", getLatestDoc);

        designDocument.setViews(view);

        dbClient.design().synchronizeWithDb(designDocument);

        List<JsonObject> list = dbClient.view("Lists/view").query(JsonObject.class);

        Object[][] result = new Object[list.size()][3];

        int index = 0;
        for (JsonObject json : list){
            JsonObject entry = json.getAsJsonObject("value");
            result[index][0] = entry.get("title").getAsString();
            result[index][1] = entry.get("date").getAsString();
            result[index][2] = entry.get("location").getAsString();
            index++;
        }
        return result;

    }
}
