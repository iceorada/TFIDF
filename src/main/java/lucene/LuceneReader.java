package lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import ir.Topic;
import ir.Topics;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import utility.Utility;

public class LuceneReader {
    //directory contains the lucene indexes
    private static final String INDEX_DIR = "index";

    public static void main(String[] args) throws Exception {
        String topic_set = args[0];
        String document_set = args[1];
        String run_type = args[2];
        String output_file = args[3];
        Topics topic_collection = new Topics(topic_set, run_type);


        Similarity similarity = new ClassicSimilarity();

        LuceneReader luceneReader = new LuceneReader();
        Map<Float, ArrayList<String>> sortedCollection = new TreeMap<>(Collections.reverseOrder());

        //Create lucene searcher. It search over a single IndexReader using ClassicSimilarity
        IndexSearcher searcher = luceneReader.createSearcher(similarity);


        TopDocs foundDocs = null;
        //Search indexed contents using search term
        for (Topic currentTopic : topic_collection.getDocuments()) {
            String topicID = currentTopic.getTopicID();
            String title = currentTopic.getTitle();
            String filteredTitle = title.replaceAll("[^A-Za-z0-9]", " ");
            foundDocs = luceneReader.searchInContent(filteredTitle, searcher);

            //Save score to map
            for (ScoreDoc scoreDoc : foundDocs.scoreDocs) {
                Document luceneDoc = searcher.doc(scoreDoc.doc);
                //Save here
                ArrayList cosineSimilarityPair = new ArrayList<>(Arrays.asList(topicID, luceneDoc.get("docID")));
                sortedCollection.put(scoreDoc.score, cosineSimilarityPair);
            }

        }

        //Total found documents
        System.out.println("Total Results : " + foundDocs.totalHits);

        Utility.writeToFile(sortedCollection, output_file);
    }

    public static TopDocs searchInContent(String textToFind, IndexSearcher searcher) throws Exception {
        //Create search query
        QueryParser qp = new QueryParser("content", new StandardAnalyzer());
        Query query = qp.parse(textToFind);

        //search the index
        TopDocs hits = searcher.search(query, 1000);
        return hits;
    }

    public static IndexSearcher createSearcher() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));

        //It is an interface for accessing a point-in-time view of a lucene index
        IndexReader reader = DirectoryReader.open(dir);
        //Index searcher
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }

    public static IndexSearcher createSearcher(Similarity similarity) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));

        //It is an interface for accessing a point-in-time view of a lucene index
        IndexReader reader = DirectoryReader.open(dir);
        //Index searcher
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(similarity);
        return searcher;
    }
}
