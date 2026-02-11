package problem6_rate_limiter;

import java.util.HashMap;

public class RateLimiter {

    // TokenBucket class for each client
    private static class TokenBucket {
        int tokens;
        long lastRefillTime; // in milliseconds
        int maxTokens;
        int refillRate; // tokens per second

        TokenBucket(int maxTokens, int refillRate) {
            this.maxTokens = maxTokens;
            this.refillRate = refillRate;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
        }

        synchronized boolean allowRequest() {
            refillTokens();
            if (tokens > 0) {
                tokens--;
                return true;
            } else {
                return false;
            }
        }

        synchronized void refillTokens() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillTime; // milliseconds
            int newTokens = (int) (elapsed / 1000.0 * refillRate);
            if (newTokens > 0) {
                tokens = Math.min(maxTokens, tokens + newTokens);
                lastRefillTime = now;
            }
        }

        synchronized int getRemainingTokens() {
            refillTokens();
            return tokens;
        }
    }

    // Map: clientId -> TokenBucket
    private HashMap<String, TokenBucket> clients = new HashMap<>();
    private int maxTokensPerHour = 1000;

    /**
     * Register a new client
     */
    public void registerClient(String clientId) {
        int refillRate = maxTokensPerHour / 3600; // tokens per second
        clients.put(clientId, new TokenBucket(maxTokensPerHour, refillRate));
    }

    /**
     * Check if client can make a request
     */
    public String checkRateLimit(String clientId) {
        TokenBucket bucket = clients.get(clientId);
        if (bucket == null) {
            return "Client not registered";
        }

        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getRemainingTokens() + " requests remaining)";
        } else {
            return "Denied (0 requests remaining, retry later)";
        }
    }

    /**
     * Get client status
     */
    public String getRateLimitStatus(String clientId) {
        TokenBucket bucket = clients.get(clientId);
        if (bucket == null) {
            return "Client not registered";
        }
        return "{used: " + (maxTokensPerHour - bucket.getRemainingTokens()) +
                ", limit: " + maxTokensPerHour + "}";
    }

    /**
     * Demo / main method
     */
    public static void main(String[] args) throws InterruptedException {
        RateLimiter limiter = new RateLimiter();

        limiter.registerClient("abc123");

        // Simulate requests
        for (int i = 0; i < 5; i++) {
            System.out.println(limiter.checkRateLimit("abc123"));
            Thread.sleep(500); // simulate time between requests
        }

        System.out.println(limiter.getRateLimitStatus("abc123"));
    }
}
