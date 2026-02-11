package problem7_autocomplete_system;

import java.util.*;

public class AutocompleteSystem {

    // Trie Node class
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd;
        String query;
        int frequency;
    }

    private TrieNode root = new TrieNode();
    private HashMap<String, Integer> globalFrequency = new HashMap<>();

    /**
     * Add a query to the system
     */
    public void addQuery(String query) {
        globalFrequency.put(query, globalFrequency.getOrDefault(query, 0) + 1);
        int freq = globalFrequency.get(query);

        TrieNode node = root;
        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        node.isEnd = true;
        node.query = query;
        node.frequency = freq;
    }

    /**
     * Search top K suggestions for a given prefix
     */
    public List<String> search(String prefix, int k) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) return new ArrayList<>();
            node = node.children.get(c);
        }

        PriorityQueue<TrieNode> pq = new PriorityQueue<>(
                (a, b) -> a.frequency != b.frequency ? a.frequency - b.frequency : b.query.compareTo(a.query)
        );

        collectTopK(node, pq, k);

        List<String> results = new ArrayList<>();
        while (!pq.isEmpty()) results.add(0, pq.poll().query); // reverse order
        return results;
    }

    /**
     * Recursively collect suggestions
     */
    private void collectTopK(TrieNode node, PriorityQueue<TrieNode> pq, int k) {
        if (node.isEnd) {
            pq.offer(node);
            if (pq.size() > k) pq.poll();
        }
        for (TrieNode child : node.children.values()) {
            collectTopK(child, pq, k);
        }
    }

    /**
     * Update frequency of a query
     */
    public void updateFrequency(String query) {
        addQuery(query); // simply add again to increment frequency
    }

    /**
     * Demo / main method
     */
    public static void main(String[] args) {
        AutocompleteSystem auto = new AutocompleteSystem();

        // Add some queries
        auto.addQuery("java tutorial");
        auto.addQuery("javascript");
        auto.addQuery("java download");
        auto.addQuery("java tutorial"); // frequency increases

        // Search suggestions for "jav"
        System.out.println("Suggestions for 'jav': " + auto.search("jav", 10));

        // Update frequency
        auto.updateFrequency("java download");
        auto.updateFrequency("java download");

        System.out.println("Suggestions for 'jav' after updates: " + auto.search("jav", 10));
    }
}
