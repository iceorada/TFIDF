package ir;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import pipeline.Pipeline;
import stemmer.CzechStemmerAgressive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.*;

public class Doc implements Comparable<Doc> {

    private String docID;
    private HashMap<String, Integer> termFreq;


    public Doc(String docID) {
        this.docID = docID;
        termFreq = new HashMap<String, Integer>();
    }

    public void setTermFrequency(String text, int run_Type, String language_code) {
        StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
        CoreDocument coreDocument = new CoreDocument(text);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreLabel> coreLabelList = coreDocument.tokens();

        for (CoreLabel word : coreLabelList) {
            String originalWord = word.originalText();
            String lemmaWord = word.lemma().toLowerCase();
            String run1_term = "";

            if (run_Type == 0) {
                if (termFreq.containsKey(originalWord)) {
                    int oldCount = termFreq.get(originalWord);
                    termFreq.put(originalWord, ++oldCount);
                } else {
                    termFreq.put(originalWord, 1);
                }
            } else if (run_Type == 1) {

                if (language_code.equalsIgnoreCase("en")) {
                    run1_term = lemmaWord;
                } else if (language_code.equalsIgnoreCase("cs")) {
                    run1_term = toStemCzech(originalWord);
                }

                if (termFreq.containsKey(run1_term)) {
                    int oldCount = termFreq.get(run1_term);
                    termFreq.put(run1_term, ++oldCount);
                } else {
                    termFreq.put(run1_term, 1);
                }
            }
        }
    }

    public void convertToFile() {
        String textToWrite = "";
        OutputStream writer = null;

        try {
            writer = new FileOutputStream(new File("data/" + docID + ".txt"));
            for (String text : getTermList()) {
                for (int i = 0; i < getTermFrequency(text); i++) {
                    textToWrite += text + " ";
                }
                textToWrite += "\r\n";
            }
            writer.write(textToWrite.getBytes(), 0, textToWrite.length());
            System.out.println("Written to " + "data/" + docID + ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private String toStemCzech(String word) {
        return new CzechStemmerAgressive().stem(word);
    }

    public double getTermFrequency(String word) {
        if (termFreq.containsKey(word)) {
            return termFreq.get(word);
        } else {
            return 0;
        }
    }

    public String getDocID() {
        return docID;
    }

    public Set<String> getTermList() {
        return termFreq.keySet();
    }

    public int getTotalFreq() {
        int count = 0;
        for (Entry<String, Integer> frequency : termFreq.entrySet()) {
            count += frequency.getValue();
        }
        return count;
    }

    public String toString() {
        String message = "Doc ID: " + docID + "\n";
        for (Entry<String, Integer> frequency : termFreq.entrySet()) {
            message += frequency.getKey() + " = " + frequency.getValue() + "\n";
        }
        return message;
    }

    @Override
    public int compareTo(Doc o) {
        return 0;
    }

}
