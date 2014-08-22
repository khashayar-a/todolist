import com.google.gson.JsonObject;

// Created by Khashayar Abdouli on 8/21/14.

public class ToDoList {
    private String username;
    private String title;
    private String description;
    private String date;
    private boolean alarm = false ;
    private String alert_before;
    private String location;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public String getAlert_before() {
        return alert_before;
    }

    public void setAlert_before(String alert_before) {
        this.alert_before = alert_before;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Converting the class to Json for easier use in DB
     * @return JsonObject containing the class info
     */
    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        if(username != null){
            json.addProperty("username" , username);
        }
        json.addProperty("title", title);
        json.addProperty("description", description);
        json.addProperty("date", date);
        json.addProperty("alarm", alarm);
        json.addProperty("alert_before", alert_before);
        json.addProperty("location", location);

        return json;
    }

    @Override
    public String toString() {
        return "ToDoList{" +
                "username='" + username + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", alarm=" + alarm +
                ", alert_before='" + alert_before + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
