package lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

public class LuceneWriter {
    private static final String INDEX_DIR = "index";

    public static void main(String[] args) throws Exception {
        IndexWriter writer = createWriter();
        List<Document> documents = new ArrayList<>();

        Document document1 = createDocument("00000001", "Lokesh there is a lot of words here that I would have to take out from the XML....");
        documents.add(document1);

        Document document2 = createDocument("00000002", "dinner\n" + "estimate\n" + "craft\n" + "rough\n" + "file\n" + "square\n");
        documents.add(document2);

        //Let's clean everything first
        writer.deleteAll();

        writer.addDocuments(documents);
        writer.commit();
        writer.close();
    }

    public static Document createDocument(String docID, String content) {
        Document document = new Document();
        document.add(new StringField("docID", docID.toString(), Field.Store.YES));
        document.add(new TextField("content", content.toString(), Field.Store.YES));
        return document;
    }

    public static IndexWriter createWriter() throws IOException {
        FSDirectory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(dir, config);
        return writer;
    }
}