package problem10_multi_level_cache;

import java.util.*;

public class MultiLevelCache {

    // Video data class for L1 cache
    private static class VideoData {
        String videoId;
        String content;

        VideoData(String videoId, String content) {
            this.videoId = videoId;
            this.content = content;
        }
    }

    // L1 Cache: In-memory (LinkedHashMap for LRU)
    private LinkedHashMap<String, VideoData> l1Cache;
    private int l1Capacity = 10000;

    // L2 Cache: SSD-backed (simulated with HashMap)
    private HashMap<String, String> l2Cache; // videoId -> filePath
    private int l2Capacity = 100000;

    // L3: Database (simulated with HashMap)
    private HashMap<String, String> database; // videoId -> content

    // Access count for promotion logic
    private HashMap<String, Integer> accessCount;

    public MultiLevelCache() {
        l1Cache = new LinkedHashMap<>(l1Capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                return size() > l1Capacity;
            }
        };
        l2Cache = new HashMap<>();
        database = new HashMap<>();
        accessCount = new HashMap<>();
    }

    /**
     * Add video to database
     */
    public void addVideoToDatabase(String videoId, String content) {
        database.put(videoId, content);
    }

    /**
     * Get video content using multi-level caching
     */
    public String getVideo(String videoId) {
        long start = System.currentTimeMillis();

        // Check L1
        if (l1Cache.containsKey(videoId)) {
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("L1 Cache HIT (" + elapsed + "ms)");
            accessCount.put(videoId, accessCount.getOrDefault(videoId, 0) + 1);
            return l1Cache.get(videoId).content;
        }

        // Check L2
        if (l2Cache.containsKey(videoId)) {
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("L2 Cache HIT (" + elapsed + "ms)");
            promoteToL1(videoId, l2Cache.get(videoId));
            return l2Cache.get(videoId);
        }

        // L3 Database
        if (database.containsKey(videoId)) {
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("L3 Database HIT (" + elapsed + "ms)");
            String content = database.get(videoId);
            addToL2(videoId, content);
            return content;
        }

        return null; // video not found
    }

    /**
     * Promote video from L2 to L1 if accessed frequently
     */
    private void promoteToL1(String videoId, String content) {
        int count = accessCount.getOrDefault(videoId, 0) + 1;
        accessCount.put(videoId, count);

        // Simple promotion threshold
        if (count >= 2) {
            l1Cache.put(videoId, new VideoData(videoId, content));
        }
    }

    /**
     * Add video to L2 cache
     */
    private void addToL2(String videoId, String content) {
        if (l2Cache.size() >= l2Capacity) {
            // Simple eviction: remove random entry
            Iterator<String> it = l2Cache.keySet().iterator();
            if (it.hasNext()) l2Cache.remove(it.next());
        }
        l2Cache.put(videoId, content);
        accessCount.put(videoId, 1);
    }

    /**
     * Print cache statistics
     */
    public void getStatistics() {
        int l1Hits = l1Cache.size();
        int l2Hits = l2Cache.size();
        int l3Hits = database.size() - l2Cache.size(); // simplified

        System.out.println("L1: Hit Rate approx: " + (l1Hits * 100.0 / database.size()) + "%");
        System.out.println("L2: Hit Rate approx: " + (l2Hits * 100.0 / database.size()) + "%");
        System.out.println("L3: Hit Rate approx: " + (l3Hits * 100.0 / database.size()) + "%");
    }

    /**
     * Demo / main method
     */
    public static void main(String[] args) {
        MultiLevelCache cache = new MultiLevelCache();

        // Add videos
        cache.addVideoToDatabase("video_123", "Content of video 123");
        cache.addVideoToDatabase("video_999", "Content of video 999");

        // Access videos
        cache.getVideo("video_123"); // L3 -> L2 -> possibly L1
        cache.getVideo("video_123"); // L1 hit after promotion
        cache.getVideo("video_999"); // L3 -> L2

        // Get statistics
        cache.getStatistics();
    }
}
