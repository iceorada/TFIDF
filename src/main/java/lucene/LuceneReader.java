package lucene;

import java.io.IOException;
import java.nio.file.Paths;

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

public class LuceneReader {
    //directory contains the lucene indexes
    private static final String INDEX_DIR = "index";

    public static void main(String[] args) throws Exception {
        //Create lucene searcher. It search over a single IndexReader.
        IndexSearcher searcher = createSearcher(new ClassicSimilarity());

        //Search indexed contents using search term
        TopDocs foundDocs = searchInContent("Euro inflation", searcher);

        //Total found documents
        System.out.println("Total Results :: " + foundDocs.totalHits);

        //Let's print out the path of files which have searched term
        int counter = 1;
        for (ScoreDoc scoreDoc : foundDocs.scoreDocs) {
            Document luceneDoc = searcher.doc(scoreDoc.doc);
            System.out.println("TopicID" + " 0 " + luceneDoc.get("docID") + " " + counter + " " + scoreDoc.score + " baseline");
            counter++;
        }
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
