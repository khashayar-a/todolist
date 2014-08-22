import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.DesignDocument;

import java.util.*;

// Created by Khashayar Abdouli on 8/21/14.
//
public class Couch {


    /**
     * Initializes a couchdb client
     * @return and instance of the couchdb client
     */
    public static CouchDbClient getClient(){
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
    public static boolean checkUser(String username){

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
    public static String signIn(String username, String password){

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
    public static Object[][] getList(String userID){

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

    /**
     * fetching the mail address of the given user
     * @param user_id the unique id for a certain user
     * @return the specified mail or empty string if the user is not found
     */
    public static String fetchMail(String user_id){

        CouchDbClient dbClient = getClient();
        DesignDocument designDocument = new DesignDocument();
        designDocument.setId("_design/Users");
        designDocument.setLanguage("javascript");

        DesignDocument.MapReduce getLatestDoc = new DesignDocument.MapReduce();
        getLatestDoc.setMap("function(doc) {\n" +
                "    if(doc.user_id == \"" + user_id +"\")\n" +
                "        emit(doc._id, doc.mail);\n" +
                "}");

        Map<String, DesignDocument.MapReduce> view = new HashMap<String, DesignDocument.MapReduce>();
        view.put("mail", getLatestDoc);

        designDocument.setViews(view);

        dbClient.design().synchronizeWithDb(designDocument);

        List<JsonObject> list = dbClient.view("Users/mail").query(JsonObject.class);

        if(list.isEmpty())
            return "";
        else
            return list.get(0).get("value").getAsString();

    }

    /**
     * gets all the alerts that has to be send in a date range
     * @param minDate minimum of the date range
     * @param maxDate maximum of the date range
     * @return a hashmap containing the user_id as its key and the alert as the value
     */
    public static HashMap<String, JsonObject> fetchAlert(String minDate, String maxDate){

        CouchDbClient dbClient = getClient();
        DesignDocument designDocument = new DesignDocument();
        designDocument.setId("_design/Dates");
        designDocument.setLanguage("javascript");

        DesignDocument.MapReduce getLatestDoc = new DesignDocument.MapReduce();
        getLatestDoc.setMap("function(doc) {\n" +
                "    if (doc.date && doc.date < \""+ maxDate +"\" && doc.date > \"" + minDate +"\" && doc.alarm) {\n" +
                "        emit(doc.username,  " +
                "{\"title\" : doc.title, \"description\": doc.description, \"date\" : doc.date});\n" +
                "    }   \n" +
                "}");

        Map<String, DesignDocument.MapReduce> view = new HashMap<String, DesignDocument.MapReduce>();
        view.put("alert", getLatestDoc);

        designDocument.setViews(view);

        dbClient.design().synchronizeWithDb(designDocument);

        List<JsonObject> list = dbClient.view("Dates/alert").query(JsonObject.class);

        HashMap<String, JsonObject> alerts = new HashMap<String, JsonObject>();

        for (JsonObject reply: list){
            String key = reply.get("key").getAsString();
            JsonObject value = reply.getAsJsonObject("value");
            alerts.put(key,value);
        }

        return alerts;

    }

    /**
     * Gets the first entry after a certain date
     * @param date the given date
     * @return the next date as a String in format of "YYYY-MM-DD HH:mm:ss"
     */
    public static String getNextDate(String date){

        CouchDbClient dbClient = getClient();
        DesignDocument designDocument = new DesignDocument();
        designDocument.setId("_design/Dates");
        designDocument.setLanguage("javascript");

        DesignDocument.MapReduce getLatestDate = new DesignDocument.MapReduce();
        getLatestDate.setMap("function(doc) {\n" +
                "    if (doc.date && doc.date > \"" + date +" \") {\n" +
                "        emit(1, doc.date);\n" +
                "    }   \n" +
                "}");
        getLatestDate.setReduce("function(keys, values, rereduce) {\n" +
                "    var max = 0,\n" +
                "    ks = values;\n" +
                "    for (var i = 1, l = ks.length; i < l; ++i) {\n" +
                "        if (ks[max] > ks[i]) max = i;\n" +
                "    }\n" +
                "    return ks[max];\n" +
                "}");

        Map<String, DesignDocument.MapReduce> view = new HashMap<String, DesignDocument.MapReduce>();
        view.put("nextDate", getLatestDate);

        designDocument.setViews(view);

        dbClient.design().synchronizeWithDb(designDocument);

        ArrayList<JsonObject> list = (ArrayList<JsonObject>) dbClient.view("Dates/nextDate").reduce(true).groupLevel(1).query(JsonObject.class);
        if(list.isEmpty())
            return "2020-12-31 00:00:00";
        else
            return list.get(0).get("value").getAsString();
    }
}
