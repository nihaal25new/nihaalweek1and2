package problem2_inventory_manager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class InventoryManager {

    // Map to track product stock: productId -> stockCount
    private HashMap<String, Integer> stock = new HashMap<>();

    // Map to track waiting list: productId -> queue of userIds
    private HashMap<String, Queue<Integer>> waitingList = new HashMap<>();

    /**
     * Initialize a product with a stock count
     */
    public void addProduct(String productId, int count) {
        stock.put(productId, count);
        waitingList.put(productId, new LinkedList<>());
    }

    /**
     * Check stock availability for a product
     */
    public int checkStock(String productId) {
        return stock.getOrDefault(productId, 0);
    }

    /**
     * Attempt to purchase an item
     */
    public String purchaseItem(String productId, int userId) {
        int available = stock.getOrDefault(productId, 0);

        if (available > 0) {
            // Decrement stock safely
            stock.put(productId, available - 1);
            return "Success, " + (available - 1) + " units remaining";
        } else {
            // Add user to waiting list
            Queue<Integer> queue = waitingList.get(productId);
            queue.add(userId);
            return "Added to waiting list, position #" + queue.size();
        }
    }

    /**
     * Get waiting list for a product
     */
    public Queue<Integer> getWaitingList(String productId) {
        return waitingList.getOrDefault(productId, new LinkedList<>());
    }

    /**
     * Demo / main method for testing
     */
    public static void main(String[] args) {
        InventoryManager manager = new InventoryManager();

        // Add a product with 5 units
        manager.addProduct("IPHONE15_256GB", 5);

        // Check stock
        System.out.println("Stock: " + manager.checkStock("IPHONE15_256GB")); // 5

        // Simulate purchases
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 101)); // 4 remaining
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 102)); // 3 remaining
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 103)); // 2 remaining
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 104)); // 1 remaining
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 105)); // 0 remaining
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 106)); // Added to waiting list, position #1
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 107)); // Added to waiting list, position #2

        // Print waiting list
        System.out.println("Waiting List: " + manager.getWaitingList("IPHONE15_256GB"));
    }
}
