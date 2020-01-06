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

public class LuceneDocumentIndexer {

    public static ArrayList<org.apache.lucene.document.Document> extractAndIndex(String path, String run_type, Stopwords stopwords) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        ArrayList<org.apache.lucene.document.Document> luceneDocuments = new ArrayList<>();
        try {
            // Get the child from the <DOC> tags
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);
            NodeList docList = doc.getElementsByTagName("DOC");
            Doc temp_Doc = null;
            org.apache.lucene.document.Document luceneDocument = null;

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
                                        if (temp_Doc != null) {
                                            luceneDocument = temp_Doc.convertToLuceneDocument();
                                            luceneDocuments.add(luceneDocument);
                                            System.out.println("Converted " + luceneDocument.get("docID"));
                                        }

                                        temp_Doc = new Doc(docNo_Value);
                                        previousDocID = docNo_Value;
                                    }
                                } else {
                                    // Add line item into the previous doc i.e. "Dressing to Excess"
                                    String childValue = castedChild.getTextContent();
                                    if (temp_Doc != null) {
                                        temp_Doc.setTermFrequency(childValue, extractRunNo(run_type), extractLanguage(run_type), stopwords);
                                    }
                                }
                            }
                        }
                    }

                }
            }
        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            System.out.println("Document " + path + " is not formated properly.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return luceneDocuments;
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
