import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class Main {

    public static void main(String[] args) {
        //English
//        String topic_set = "topics-train_en.xml";
//        String document_set = "documents_en.lst";
//        String run_type = "run-0_en";

        // Czech
        String topic_set = "topics-train_cs.xml";
        String document_set = "documents_cs - Test.lst";
        String run_type = "run-0_cs";

        // Extract and process topics
        Topics topic_collection = new Topics(topic_set, run_type);

        // Extract and process documents
        Docs doc_collection = new Docs(document_set, run_type);

        // Traverse collection here to check
        System.out.println("!----- Check -----!");
        System.out.println("There are " + topic_collection.getDocuments().size() + " topics");
        System.out.println("There are " + doc_collection.getDocuments().size() + " documents");

    }
}
