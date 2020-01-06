package ir;

public class Topic {
    private String topicID;
    private String title;


    public Topic(String docID) {
        this.topicID = docID;
    }

    public String getTopicID() {
        return topicID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
