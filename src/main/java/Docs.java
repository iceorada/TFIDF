import ir.*;
import lucene.LuceneWriter;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utility.Utility;

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

    public Docs(Corpus corpus, String filename, String run_type) throws Exception {
        this.document_name = filename;
        Stopwords stopwords = null;
        if (Utility.extractRunNo(run_type) == 1) {
            stopwords = new Stopwords("stopwords-" + Utility.extractLanguage(run_type) + ".txt");
        }
        this.docs = list_to_docName(corpus, filename, run_type, stopwords);
    }

    public Docs(String filename, String run_type, Similarity similarity) throws Exception {
        this.document_name = filename;
        Stopwords stopwords = null;
        if (Utility.extractRunNo(run_type) == 1) {
            stopwords = new Stopwords("stopwords-" + Utility.extractLanguage(run_type) + ".txt");
        }
        listToLuceneDocuments(filename, run_type, stopwords, similarity);
    }

    private static ArrayList<Doc> list_to_docName(Corpus corpus, String list_name, String run_type, Stopwords stopwords) {
        LuceneDocumentIndexer luceneDocumentIndexer = new LuceneDocumentIndexer();
        ArrayList<Doc> documents_list = new ArrayList<>();
        Scanner list_reader = null;
        try {
            String language_Code = Utility.extractLanguage(run_type);
            list_reader = new Scanner(new File(list_name));

            while (list_reader.hasNext()) {
                String filename = list_reader.next();
                System.out.println("Extracting " + filename + "...");

                String path = "documents_" + language_Code + "/" + filename;

//                ArrayList<Doc> temp_documents = extractAndIndex(corpus, path, run_type);

                luceneDocumentIndexer.extractAndIndex(path, run_type, stopwords);

//                documents_list.addAll(temp_documents);

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

    private static void listToLuceneDocuments(String list_name, String run_type, Stopwords stopwords, Similarity similarity) {
        LuceneDocumentIndexer luceneDocumentIndexer = new LuceneDocumentIndexer();
        ArrayList<org.apache.lucene.document.Document> documents_list = new ArrayList<>();
        ArrayList<org.apache.lucene.document.Document> temp_doc_list;
        Scanner list_reader = null;
        try {
            String language_Code = Utility.extractLanguage(run_type);
            list_reader = new Scanner(new File(list_name));

            while (list_reader.hasNext()) {
                String filename = list_reader.next();
                System.out.println("Extracting " + filename + "...");

                String path = "documents_" + language_Code + "/" + filename;

                temp_doc_list = luceneDocumentIndexer.extractAndIndex(path, run_type, stopwords);

                documents_list.addAll(temp_doc_list);
            }

            IndexWriter writer = LuceneWriter.createWriter(similarity);
            writer.deleteAll();
            writer.addDocuments(documents_list);
            writer.commit();
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (list_reader != null) {
                list_reader.close();
            }
        }
    }


    private static ArrayList<org.apache.lucene.document.Document> extractAndIndex2(String path, String run_type, Stopwords stopwords) {
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
                                        temp_Doc.setTermFrequency(childValue, Utility.extractRunNo(run_type), Utility.extractLanguage(run_type), stopwords);
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

    private static ArrayList<Doc> extractAndIndex(Corpus corpus, String path, String run_type, Stopwords stopwords) {
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
                                    // Check if it is the start of a new <DOC>
                                    if (!docNo_Value.equalsIgnoreCase(previousDocID)) {
                                        // Create new document object
                                        // Update "previousDocID" with current DOC ID
                                        if (temp_Document != null) {
                                            // Add document to doc collection
                                            temp_docs.add(temp_Document);
                                            // Add document to corpus
                                            corpus.addDocuments(temp_Document);
                                            // Add document to the inverted index
                                            corpus.populateInvertedIndex(temp_Document);

                                            System.out.println("Added document " + temp_Document.getDocID());
                                        }

                                        temp_Document = new Doc(docNo_Value);
                                        previousDocID = docNo_Value;
                                    }
                                } else {
                                    // Add line item into the previous doc i.e. "Dressing to Excess"
                                    String childValue = castedChild.getTextContent();
                                    if (temp_Document != null) {
                                        temp_Document.setTermFrequency(childValue, Utility.extractRunNo(run_type), Utility.extractLanguage(run_type), stopwords);
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
}
