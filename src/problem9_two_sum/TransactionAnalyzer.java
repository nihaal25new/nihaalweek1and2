package problem9_two_sum;

import java.util.*;

public class TransactionAnalyzer {

    public static class Transaction {
        int id;
        int amount;
        String merchant;
        String time;

        public Transaction(int id, int amount, String merchant, String time) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.time = time;
        }
    }

    private List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    /**
     * Classic Two-Sum: Find pairs that sum to target
     */
    public List<int[]> findTwoSum(int target) {
        List<int[]> result = new ArrayList<>();
        Map<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                result.add(new int[]{map.get(complement).id, t.id});
            }
            map.put(t.amount, t);
        }

        return result;
    }

    /**
     * Detect duplicate transactions (same amount, same merchant)
     */
    public List<Map<String, Object>> detectDuplicates() {
        Map<String, List<String>> dupMap = new HashMap<>();
        for (Transaction t : transactions) {
            String key = t.amount + "-" + t.merchant;
            dupMap.putIfAbsent(key, new ArrayList<>());
            dupMap.get(key).add("id:" + t.id);
        }

        List<Map<String, Object>> duplicates = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : dupMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                Map<String, Object> map = new HashMap<>();
                String[] parts = entry.getKey().split("-");
                map.put("amount", Integer.parseInt(parts[0]));
                map.put("merchant", parts[1]);
                map.put("transactions", entry.getValue());
                duplicates.add(map);
            }
        }
        return duplicates;
    }

    /**
     * K-Sum: Find K transactions that sum to target (simple recursive solution)
     */
    public List<List<Integer>> findKSum(int k, int target) {
        List<List<Integer>> result = new ArrayList<>();
        findKSumHelper(0, k, target, new ArrayList<>(), result);
        return result;
    }

    private void findKSumHelper(int start, int k, int target, List<Integer> current, List<List<Integer>> result) {
        if (k == 0) {
            if (target == 0) result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < transactions.size(); i++) {
            current.add(transactions.get(i).id);
            findKSumHelper(i + 1, k - 1, target - transactions.get(i).amount, current, result);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Demo / main method
     */
    public static void main(String[] args) {
        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        analyzer.addTransaction(new Transaction(1, 500, "Store A", "10:00"));
        analyzer.addTransaction(new Transaction(2, 300, "Store B", "10:15"));
        analyzer.addTransaction(new Transaction(3, 200, "Store C", "10:30"));
        analyzer.addTransaction(new Transaction(4, 500, "Store A", "10:45")); // duplicate

        // Classic Two-Sum
        System.out.println("Two-Sum pairs for 500: " + analyzer.findTwoSum(500));

        // Detect duplicates
        System.out.println("Duplicates: " + analyzer.detectDuplicates());

        // K-Sum example
        System.out.println("3-Sum for 1000: " + analyzer.findKSum(3, 1000));
    }
}
