package ir;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Stopwords {

    public static void main(String args[]) {
        Stopwords stopword_List = new Stopwords("stopwords/stopwords-en.txt");
        System.out.println(stopword_List.isExist("Asking"));
        System.out.println(stopword_List);
    }

    private static ArrayList<String> stopwords;

    public Stopwords(String path) {
        this.stopwords = extract_Path(path);
    }

    private ArrayList<String> extract_Path(String path) {
        ArrayList<String> word_List = new ArrayList<>();
        Scanner line_reader = null;
        try {
            line_reader = new Scanner(new File(path));

            while (line_reader.hasNext()) {
                String stopword = line_reader.next();
                word_List.add(stopword);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return word_List;
    }

    public static Boolean isExist(String term) {
        for (String word : stopwords) {
            if (word.equalsIgnoreCase(term)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Stopwords{" +
                "stopwords=" + stopwords +
                '}';
    }
}
