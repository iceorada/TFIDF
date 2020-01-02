import ir.Doc;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Docs {
    private String document_name;
    private ArrayList<Doc> docs;

    public Docs(String filename, String run_type) {
        this.document_name = filename;
        this.docs = list_to_docName(filename, run_type);
    }

    private static ArrayList<Doc> list_to_docName(String list_name, String run_type) {
        ArrayList<Doc> documents_list = new ArrayList<>();
        Scanner list_reader = null;
        try {
            String language_Code = extractLanguage(run_type);
            list_reader = new Scanner(new File(list_name));

            while (list_reader.hasNext()) {
                String filename = list_reader.next();
                System.out.println("Extracting " + filename + "...");

                String path = "documents_" + language_Code + "/" + filename;

                ArrayList<Doc> temp_documents = extract_and_compare(path, run_type);

                documents_list.addAll(temp_documents);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (list_reader != null) {
                list_reader.close();
            }
        }
        return documents_list;
    }

    private static ArrayList<Doc> extract_and_compare(String path, String run_type){
        ArrayList<Doc> temp_docs = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            // Get the child from the <DOC> tags
            DocumentBuilder builder = factory.newDocumentBuilder();
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
                                    System.out.println(docNo_Value);
                                    // Check if it is the start of a new <DOC>
                                    if (!docNo_Value.equalsIgnoreCase(previousDocID)) {
                                        // Create new document object
                                        // Update "previousDocID" with current DOC ID
                                        temp_docs.add(temp_Document);
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
        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            System.out.println("Document " + path + " is not formated properly.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        return temp_docs;
    }

    public ArrayList<Doc> getDocuments() {
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
}
