import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.*;

public class Doc implements Comparable<Doc> {

    private String docID;
    private HashMap<String, Integer> termFreq;


    public Doc(String docID) {
        this.docID = docID;
        termFreq = new HashMap<String, Integer>();
    }

    public void setTermFreq(String text, int run_Type, String language_code) {
        Scanner textReader = new Scanner(text);

        while (textReader.hasNext()) {
            String word = textReader.next();
            String filteredWord = word.replaceAll("[^A-Za-z0-9]", "");
//            String filteredWord = word;

            if (!(filteredWord.equalsIgnoreCase(""))) {
                if (run_Type == 0) {
                    if (termFreq.containsKey(filteredWord)) {
                        int oldCount = termFreq.get(filteredWord);
                        termFreq.put(filteredWord, ++oldCount);
                    } else {
                        termFreq.put(filteredWord, 1);
                    }
                } else if (run_Type == 1) {
                    // Change words to lower case
                    filteredWord = filteredWord.toLowerCase();

                    if (termFreq.containsKey(filteredWord)) {
                        int oldCount = termFreq.get(filteredWord);
                        termFreq.put(filteredWord, ++oldCount);
                    } else {
                        termFreq.put(filteredWord, 1);
                    }
                }
            }
        }
        textReader.close();
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

    public void setDocID(String docID) {
        this.docID = docID;
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
