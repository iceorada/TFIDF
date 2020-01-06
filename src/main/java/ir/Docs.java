package ir;

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

    public Docs(String filename, String run_type, Similarity similarity) throws Exception {
        this.document_name = filename;
        Stopwords stopwords = null;
        if (Utility.extractRunNo(run_type) == 1) {
            stopwords = new Stopwords("stopwords-" + Utility.extractLanguage(run_type) + ".txt");
        }
        listToLuceneDocuments(filename, run_type, stopwords, similarity);
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
                System.out.println("Extracting from " + filename + "...");

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
}
