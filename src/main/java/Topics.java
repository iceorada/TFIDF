import ir.Doc;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.IOException;
import java.util.ArrayList;

public class Topics {
    private String document_name;
    private ArrayList<Doc> docs;

    public Topics(String filename, String run_type) {
        this.document_name = filename;
        this.docs = extractAndPreprocess(filename, run_type);
    }

    public static ArrayList<Doc> extractAndPreprocess(String filename, String run_type) {
        ArrayList<Doc> docs = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            // Get the child from the <TOP> tags
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(filename);
            NodeList docList = doc.getElementsByTagName("top");
            Doc temp_Document = null;

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

                                    temp_Document = new Doc(topicNum_Value);
                                    previousTopicNum = topicNum_Value;
                                }
                            } else {
                                // Add title into the previous doc i.e. "Euro Inflation"
                                if (castedChild.getTagName().equalsIgnoreCase("title")) {
                                    String childValue = castedChild.getTextContent();
                                    if (temp_Document != null) {
                                        temp_Document.setTermFreq(childValue, extractRunNo(run_type), extractLanguage(run_type));
                                    }
                                }
                            }

                        }
                    }

                }
                // Add to document to ArrayList for return
                docs.add(temp_Document);
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return docs;
    }

    private static String extractLanguage(String run_type) throws Exception {
        int start = run_type.indexOf('_');
        int end = run_type.length();

        if (run_type.substring(start + 1, end).length() < 2) {
            throw new Exception("Language not found");
        }

        return run_type.substring(start + 1, end);
    }

    private static int extractRunNo(String runType) {
        int start = runType.indexOf('-');
        int end = runType.length() - 3;
        String s = runType.substring(start + 1, end);

        return Integer.parseInt(s);
    }

    public ArrayList<Doc> getDocuments() {
        return docs;
    }

    public void setDocuments(ArrayList<Doc> documents) {
        this.docs = documents;
    }

    public int getTotalFreq() {
        int totalCount = 0;
        for (Doc doc : docs) {
            totalCount += doc.getTotalFreq();
        }
        return totalCount;
    }

    public boolean isContain(String term){
        for(Doc currentDoc : docs){
            if (currentDoc.getTermFrequency(term) > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String message = "Document Name: " + document_name + "\n";
        for (Doc doc : docs) {
            message += doc + "\n";
        }
        message += "\n";
        return message;
    }
}
