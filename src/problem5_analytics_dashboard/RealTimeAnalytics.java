package problem5_real_time_analytics;

import java.util.*;

public class RealTimeAnalytics {

    // Track total page views per URL
    private HashMap<String, Integer> pageViews = new HashMap<>();

    // Track unique users per page
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();

    // Track traffic sources
    private HashMap<String, Integer> trafficSources = new HashMap<>();

    /**
     * Process a single page view event
     */
    public void processEvent(String url, String userId, String source) {
        // Update total views
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        // Update unique visitors
        uniqueVisitors.putIfAbsent(url, new HashSet<>());
        uniqueVisitors.get(url).add(userId);

        // Update traffic sources
        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
    }

    /**
     * Get top N pages by total views
     */
    public List<String> getTopPages(int n) {
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> a.getValue() - b.getValue());

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {
            pq.offer(entry);
            if (pq.size() > n) pq.poll();
        }

        List<String> topPages = new ArrayList<>();
        while (!pq.isEmpty()) {
            topPages.add(0, pq.poll().getKey()); // add in reverse order
        }
        return topPages;
    }

    /**
     * Get dashboard summary
     */
    public void printDashboard(int topN) {
        System.out.println("=== Top Pages ===");
        List<String> topPages = getTopPages(topN);
        for (String url : topPages) {
            int total = pageViews.get(url);
            int unique = uniqueVisitors.get(url).size();
            System.out.println(url + " - " + total + " views (" + unique + " unique)");
        }

        System.out.println("\n=== Traffic Sources ===");
        int totalTraffic = trafficSources.values().stream().mapToInt(i -> i).sum();
        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            double percent = (entry.getValue() * 100.0) / totalTraffic;
            System.out.println(entry.getKey() + ": " + String.format("%.2f", percent) + "%");
        }
    }

    /**
     * Demo / main method
     */
    public static void main(String[] args) {
        RealTimeAnalytics analytics = new RealTimeAnalytics();

        // Simulate page view events
        analytics.processEvent("/article/breaking-news", "user_1", "Google");
        analytics.processEvent("/article/breaking-news", "user_2", "Facebook");
        analytics.processEvent("/sports/championship", "user_3", "Direct");
        analytics.processEvent("/article/breaking-news", "user_1", "Google"); // same user

        // Print dashboard
        analytics.printDashboard(10);
    }
}
