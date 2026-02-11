package problem1_username_checker;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class UsernameChecker {
    // Map of existing usernames -> userId
    private HashMap<String, Integer> users = new HashMap<>();

    // Map of username -> number of times attempted
    private HashMap<String, Integer> attemptCount = new HashMap<>();

    /**
     * Check if a username is available.
     * Tracks attempt frequency.
     */
    public boolean checkAvailability(String username) {
        attemptCount.put(username, attemptCount.getOrDefault(username, 0) + 1);
        return !users.containsKey(username);
    }

    /**
     * Add a new user with a given username and userId.
     */
    public void addUser(String username, int userId) {
        users.put(username, userId);
    }

    /**
     * Suggest alternative usernames if requested username is taken.
     */
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int suffix = 1;

        // Generate suggestions by adding numbers
        while (suggestions.size() < 3) { // generate 3 suggestions
            String newName = username + suffix;
            if (!users.containsKey(newName)) {
                suggestions.add(newName);
            }
            suffix++;
        }

        // Add a suggestion with dots instead of underscores
        String dotName = username.replace("_", ".");
        if (!users.containsKey(dotName)) {
            suggestions.add(dotName);
        }

        return suggestions;
    }

    /**
     * Return the username that has been attempted the most times.
     */
    public String getMostAttempted() {
        String mostAttempted = null;
        int maxCount = 0;

        for (String name : attemptCount.keySet()) {
            int count = attemptCount.get(name);
            if (count > maxCount) {
                maxCount = count;
                mostAttempted = name;
            }
        }
        return mostAttempted;
    }

    /**
     * Demo / main method for testing
     */
    public static void main(String[] args) {
        UsernameChecker checker = new UsernameChecker();

        // Add some existing users
        checker.addUser("john_doe", 1);
        checker.addUser("alice_smith", 2);

        // Check availability
        System.out.println("john_doe available? " + checker.checkAvailability("john_doe")); // false
        System.out.println("jane_smith available? " + checker.checkAvailability("jane_smith")); // true

        // Suggest alternatives
        System.out.println("Alternatives for john_doe: " + checker.suggestAlternatives("john_doe"));

        // Check attempts
        checker.checkAvailability("admin");
        checker.checkAvailability("admin");
        checker.checkAvailability("john_doe");
        System.out.println("Most attempted username: " + checker.getMostAttempted()); // admin
    }
}
