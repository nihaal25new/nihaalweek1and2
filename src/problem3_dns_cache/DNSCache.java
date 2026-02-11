package problem3_dns_cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DNSCache {

    // Entry class for each DNS record
    private static class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime; // in milliseconds

        DNSEntry(String domain, String ipAddress, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private HashMap<String, DNSEntry> cache = new HashMap<>();
    private int hits = 0;
    private int misses = 0;

    /**
     * Resolve a domain name
     */
    public String resolve(String domain, long ttlSeconds) {
        cleanExpired(); // remove expired entries

        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);
            if (!entry.isExpired()) {
                hits++;
                return "Cache HIT → " + entry.ipAddress;
            }
        }

        // Cache miss
        misses++;
        String ip = queryUpstreamDNS(domain);
        cache.put(domain, new DNSEntry(domain, ip, ttlSeconds));
        return "Cache MISS → Query upstream → " + ip;
    }

    /**
     * Simulate upstream DNS query
     */
    private String queryUpstreamDNS(String domain) {
        // For demo purposes, return dummy IP based on hash
        int hash = Math.abs(domain.hashCode() % 255);
        return "192.168.1." + hash;
    }

    /**
     * Remove expired entries
     */
    private void cleanExpired() {
        Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, DNSEntry> entry = it.next();
            if (entry.getValue().isExpired()) {
                it.remove();
            }
        }
    }

    /**
     * Get cache stats
     */
    public String getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);
        return "Hit Rate: " + String.format("%.2f", hitRate) + "%, Total Lookups: " + total;
    }

    /**
     * Demo / main method
     */
    public static void main(String[] args) throws InterruptedException {
        DNSCache dns = new DNSCache();

        System.out.println(dns.resolve("google.com", 3)); // MISS
        System.out.println(dns.resolve("google.com", 3)); // HIT
        System.out.println(dns.resolve("example.com", 3)); // MISS

        Thread.sleep(4000); // wait for TTL to expire

        System.out.println(dns.resolve("google.com", 3)); // EXPIRED → MISS
        System.out.println(dns.getCacheStats());
    }
}
