package ir;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class XMLToTxt {

    public static void extractAndConvert(String path, String run_type) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);
            NodeList docList = doc.getElementsByTagName("DOC");
            Doc temp_Document = null;

            for (int i = 0; i < docList.getLength(); i++) {
                Node currentDoc = docList.item(i);
                if (currentDoc.getNodeType() == Node.ELEMENT_NODE) {
                    Element castedDoc = (Element) currentDoc;
                    NodeList docChildNodes = castedDoc.getChildNodes();
                    String previousDocID = "";

                    // Iterate through line item of the child nodes within the <DOC> tags
                    for (int x = 0; x < docChildNodes.getLength(); x++) {
                        Node currentChild = docChildNodes.item(x);
                        if (currentChild.getNodeType() == Node.ELEMENT_NODE) {
                            Element castedChild = (Element) currentChild;
                            String childTagName = castedChild.getTagName();

                            // Exclude <DOCID>
                            if (!childTagName.equalsIgnoreCase("docid")) {
                                // Check for DOCNO using the XML Tags <DOCNO>
                                if (childTagName.equalsIgnoreCase("docno")) {
                                    String docNo_Value = castedChild.getTextContent();
                                    // Check if it is the start of a new <DOC>
                                    if (!docNo_Value.equalsIgnoreCase(previousDocID)) {
                                        // Create new document object
                                        // Update "previousDocID" with current DOC ID
                                        if (temp_Document != null){
                                            // Convert document into file of words
                                            temp_Document.convertToFile();
                                        }
                                        temp_Document = new Doc(docNo_Value);
                                        previousDocID = docNo_Value;
                                    }
                                } else {
                                    // Add line item into the previous doc i.e. "Dressing to Excess"
                                    String childValue = castedChild.getTextContent();
                                    if (temp_Document != null) {
                                        temp_Document.setTermFrequency(childValue, extractRunNo(run_type), extractLanguage(run_type));
                                    }
                                }
                            }
                        }
                    }

                }
            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
