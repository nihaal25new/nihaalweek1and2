package problem4_plagiarism_detector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PlagiarismDetector {

    // Map of n-gram hash -> set of document IDs
    private HashMap<String, Set<String>> ngramIndex = new HashMap<>();
    private int nGramSize;

    public PlagiarismDetector(int nGramSize) {
        this.nGramSize = nGramSize; // e.g., 5 or 7
    }

    /**
     * Process a document and index its n-grams
     */
    public void indexDocument(String documentId, String filePath) throws IOException {
        List<String> words = readWordsFromFile(filePath);

        for (int i = 0; i <= words.size() - nGramSize; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < nGramSize; j++) {
                sb.append(words.get(i + j)).append(" ");
            }
            String ngram = sb.toString().trim();

            ngramIndex.putIfAbsent(ngram, new HashSet<>());
            ngramIndex.get(ngram).add(documentId);
        }
    }

    /**
     * Analyze a document against indexed documents for plagiarism
     */
    public void analyzeDocument(String documentId, String filePath) throws IOException {
        List<String> words = readWordsFromFile(filePath);
        HashMap<String, Integer> matchCount = new HashMap<>();

        for (int i = 0; i <= words.size() - nGramSize; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < nGramSize; j++) {
                sb.append(words.get(i + j)).append(" ");
            }
            String ngram = sb.toString().trim();

            if (ngramIndex.containsKey(ngram)) {
                for (String doc : ngramIndex.get(ngram)) {
                    if (!doc.equals(documentId)) {
                        matchCount.put(doc, matchCount.getOrDefault(doc, 0) + 1);
                    }
                }
            }
        }

        // Print similarity percentages
        System.out.println("Similarity report for " + documentId + ":");
        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {
            double similarity = (entry.getValue() * 100.0) / (words.size() - nGramSize + 1);
            System.out.println(" - " + entry.getKey() + ": " + String.format("%.2f", similarity) + "% similarity");
        }
    }

    /**
     * Utility: Read all words from a file
     */
    private List<String> readWordsFromFile(String filePath) throws IOException {
        List<String> words = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] split = line.trim().split("\\s+");
            for (String w : split) {
                if (!w.isEmpty()) {
                    words.add(w.toLowerCase());
                }
            }
        }
        reader.close();
        return words;
    }

    /**
     * Demo / main method
     */
    public static void main(String[] args) throws IOException {
        PlagiarismDetector detector = new PlagiarismDetector(5);

        // Index some documents
        detector.indexDocument("essay_089.txt", "essay_089.txt");
        detector.indexDocument("essay_092.txt", "essay_092.txt");

        // Analyze a new document
        detector.analyzeDocument("essay_123.txt", "essay_123.txt");
    }
}
