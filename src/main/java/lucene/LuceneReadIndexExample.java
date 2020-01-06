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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneReadIndexExample {
    private static final String INDEX_DIR = "index";

    public static void main(String[] args) throws Exception {
        IndexSearcher searcher = createSearcher();

        //Search by ID
        TopDocs foundDocs = searchByDocID(1, searcher);

        System.out.println("Total Results :: " + foundDocs.totalHits);

        for (ScoreDoc sd : foundDocs.scoreDocs) {
            Document d = searcher.doc(sd.doc);
            System.out.println(String.format(d.get("docID")));
        }

        //Search by firstName
        TopDocs foundDocs2 = searchByContent("Brian is", searcher);

        System.out.println("Total Results :: " + foundDocs2.totalHits);

        for (ScoreDoc sd : foundDocs2.scoreDocs) {
            Document d = searcher.doc(sd.doc);
            System.out.println(String.format(d.get("docID")));
        }
    }

    private static TopDocs searchByContent(String content, IndexSearcher searcher) throws Exception {
        QueryParser qp = new QueryParser("content", new StandardAnalyzer());
        Query contentQuery = qp.parse(content);
        TopDocs hits = searcher.search(contentQuery, 10);
        return hits;
    }

    private static TopDocs searchByDocID(Integer docID, IndexSearcher searcher) throws Exception {
        QueryParser qp = new QueryParser("docID", new StandardAnalyzer());
        Query docIDQuery = qp.parse(docID.toString());
        TopDocs hits = searcher.search(docIDQuery, 10);
        return hits;
    }

    private static IndexSearcher createSearcher() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }
}