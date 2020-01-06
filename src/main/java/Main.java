import ir.*;
import lucene.LuceneReader;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import utility.Utility;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {

        // Getting parameters from args
        String topic_set = args[0];
        String document_set = args[1];
        String run_type = args[2];
        String output_file = args[3];


        if (Utility.extractRunNo(run_type) == 0) {
            run_0(topic_set, document_set, run_type, output_file);
        } else if (Utility.extractRunNo(run_type) == 1) {
            run_1(topic_set, document_set, run_type, output_file);
        }
    }

    private static void run_0(String topic_set, String document_set, String run_type, String output_file) throws Exception {
        Corpus corpus = new Corpus();

        // Extract and process topics
        Topics topic_collection = new Topics(corpus, topic_set, run_type);

        // Extract and process documents
        Docs doc_collection = new Docs(document_set, run_type);

//        ArrayList<Doc> processingList = new ArrayList<>();
//        processingList.addAll(topic_collection.getDocuments());
//        processingList.addAll(doc_collection.getDocuments());
//
//        // Traverse collection here to check
//        System.out.println("!----- Check -----!");
//        System.out.println("There are " + topic_collection.getDocuments().size() + " topics");
//        System.out.println("There are " + doc_collection.getDocuments().size() + " documents");
//        System.out.println("Corpus documents: " + corpus.getDocuments().size() + " documents");
//        System.out.println("Corpus words: " + corpus.getInvertedIndex().size() + " words");
//
//        VectorSpaceModel vectorSpace = new VectorSpaceModel(corpus);
//
//        // Sort relevance score in Desc order
//        Map<Double, ArrayList<String>> sortedCosineCollection = new TreeMap<>(Collections.reverseOrder());
//
//        for (Doc doc : doc_collection.getDocuments()) {
//            for (Doc topic : topic_collection.getDocuments()) {
//                ArrayList cosineSimilarityPair = new ArrayList<>(Arrays.asList(topic.getDocID(), doc.getDocID()));
//                sortedCosineCollection.put(vectorSpace.cosineSimilarity(topic, doc), cosineSimilarityPair);
//            }
//        }
//
//        writeToFile(sortedCosineCollection, output_file);
    }

    private static void run_1(String topic_set, String document_set, String run_type, String output_file) throws Exception {
        Similarity similarity = new BM25Similarity();

        Corpus corpus = new Corpus();

        // Extract and index topics
        Topics topic_collection = new Topics(corpus, topic_set, run_type);

        // Extract and index documents
        new Docs(document_set, run_type, similarity);

        LuceneReader luceneReader = new LuceneReader();
        Map<Float, ArrayList<String>> sortedCollection = new TreeMap<>(Collections.reverseOrder());

        //Create lucene searcher. It search over a single IndexReader using BM25Similarity
        IndexSearcher searcher = luceneReader.createSearcher(similarity);


        TopDocs foundDocs = null;
        //Search indexed contents using search term
        for (Doc currentTopic : topic_collection.getDocuments()) {
            String title = currentTopic.getDocID();
            foundDocs = luceneReader.searchInContent(title, searcher);

            //Save score to map
            for (ScoreDoc scoreDoc : foundDocs.scoreDocs) {
                Document luceneDoc = searcher.doc(scoreDoc.doc);
                //Save here
                ArrayList cosineSimilarityPair = new ArrayList<>(Arrays.asList(title, luceneDoc.get("docID")));
                sortedCollection.put(scoreDoc.score, cosineSimilarityPair);
            }

        }

        //Total found documents
        System.out.println("Total Results : " + foundDocs.totalHits);

        Utility.writeToFile(sortedCollection, output_file);
    }


}
