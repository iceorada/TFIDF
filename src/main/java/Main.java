import ir.*;

public class Main {

    public static void main(String[] args) {

        // Getting parameters from args
        String topic_set = args[0];
        String document_set = args[1];
        String run_type = args[2];
        String output_file = args[3];

        Corpus corpus = new Corpus();

        // Extract and process topics
        Topics topic_collection = new Topics(corpus, topic_set, run_type);

        // Extract and process documents
        Docs doc_collection = new Docs(corpus, document_set, run_type);

        // Traverse collection here to check
        System.out.println("!----- Check -----!");
        System.out.println("There are " + topic_collection.getDocuments().size() + " topics");
        System.out.println("There are " + doc_collection.getDocuments().size() + " documents");
        System.out.println("Corpus documents: " + corpus.getInvertedIndex().size() + " words");
        System.out.println("Test");

    }
}
