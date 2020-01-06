package ir;

import ir.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import utility.Utility;

import javax.xml.parsers.*;
import java.io.IOException;
import java.util.ArrayList;

public class Topics {
    private String document_name;
    private ArrayList<Topic> topics;

    public Topics(String filename, String run_type) throws Exception {
        this.document_name = filename;
        Stopwords stopwords = null;
        if (Utility.extractRunNo(run_type) == 1) {
            stopwords = new Stopwords("stopwords/stopwords-" + Utility.extractLanguage(run_type) + ".txt");
        }
        this.topics = extractAndIndex(filename, run_type, stopwords);
    }

    public static ArrayList<Topic> extractAndIndex(String filename, String run_type, Stopwords stopwords) {
        ArrayList<Topic> topics = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            // Get the child from the <TOP> tags
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(filename);
            NodeList docList = doc.getElementsByTagName("top");
            Topic temp_Topic = null;

            for (int i = 0; i < docList.getLength(); i++) {
                Node currentTopic = docList.item(i);
                if (currentTopic.getNodeType() == Node.ELEMENT_NODE) {
                    Element castedTopic = (Element) currentTopic;
                    NodeList topicChildNodes = castedTopic.getChildNodes();
                    String previousTopicNum = "";

                    // Iterate through line item of the child nodes within the <TOP> tags
                    for (int x = 0; x < topicChildNodes.getLength(); x++) {
                        Node currentChild = topicChildNodes.item(x);
                        if (currentChild.getNodeType() == Node.ELEMENT_NODE) {
                            Element castedChild = (Element) currentChild;
                            String childTagName = castedChild.getTagName();

                            // Check for num using the XML Tags <num>
                            if (childTagName.equalsIgnoreCase("num")) {
                                String topicNum_Value = castedChild.getTextContent();
                                System.out.println("Extracting " + topicNum_Value + "...");

                                // Check if it is the start of a new <num>
                                if (!topicNum_Value.equalsIgnoreCase(previousTopicNum)) {
                                    // Create new document object
                                    // Update "previousTopicNum" with current topic num

                                    temp_Topic = new Topic(topicNum_Value);
                                    previousTopicNum = topicNum_Value;
                                }
                            } else {
                                // Add title into the previous doc i.e. "Euro Inflation"
                                if (castedChild.getTagName().equalsIgnoreCase("title")) {
                                    String childValue = castedChild.getTextContent();
                                    if (temp_Topic != null) {
                                        temp_Topic.setTitle(childValue);
                                    }
                                }
                            }

                        }
                    }

                }
                // Add to document to ArrayList for return
                topics.add(temp_Topic);

                System.out.println("Indexed topic " + temp_Topic.getTopicID());
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return topics;
    }

    public ArrayList<Topic> getDocuments() {
        return topics;
    }

    public void setDocuments(ArrayList<Topic> documents) {
        this.topics = documents;
    }

    @Override
    public String toString() {
        String message = "Document Name: " + document_name + "\n";
        for (Topic topic : topics) {
            message += topic + "\n";
        }
        message += "\n";
        return message;
    }
}
