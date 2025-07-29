package com.finqube.iso20022.core.translation;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Statistics for translation cache operations.
 *
 * <p>This class tracks cache performance metrics including hit rates,
 * miss rates, cache size, and eviction statistics.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class CacheStatistics {

    private final String cacheId;
    private final Instant startTime;
    private final AtomicLong cacheHits;
    private final AtomicLong cacheMisses;
    private final AtomicLong cacheEvictions;
    private final AtomicLong cacheSize;
    private final AtomicLong maxCacheSize;

    /**
     * Constructs a new CacheStatistics.
     *
     * @param cacheId the cache identifier
     * @param startTime when statistics collection started
     * @param maxCacheSize the maximum cache size
     */
    public CacheStatistics(String cacheId, Instant startTime, long maxCacheSize) {
        this.cacheId = Objects.requireNonNull(cacheId, "Cache ID cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
        this.cacheHits = new AtomicLong(0);
        this.cacheMisses = new AtomicLong(0);
        this.cacheEvictions = new AtomicLong(0);
        this.cacheSize = new AtomicLong(0);
        this.maxCacheSize = new AtomicLong(maxCacheSize);
    }

    /**
     * Records a cache hit.
     */
    public void recordHit() {
        cacheHits.incrementAndGet();
    }

    /**
     * Records a cache miss.
     */
    public void recordMiss() {
        cacheMisses.incrementAndGet();
    }

    /**
     * Records a cache eviction.
     */
    public void recordEviction() {
        cacheEvictions.incrementAndGet();
    }

    /**
     * Updates the current cache size.
     *
     * @param size the current cache size
     */
    public void updateCacheSize(long size) {
        cacheSize.set(size);
    }

    /**
     * Gets the cache identifier.
     *
     * @return the cache identifier
     */
    public String getCacheId() {
        return cacheId;
    }

    /**
     * Gets when statistics collection started.
     *
     * @return the start time
     */
    public Instant getStartTime() {
        return startTime;
    }

    /**
     * Gets the number of cache hits.
     *
     * @return the cache hits count
     */
    public long getCacheHits() {
        return cacheHits.get();
    }

    /**
     * Gets the number of cache misses.
     *
     * @return the cache misses count
     */
    public long getCacheMisses() {
        return cacheMisses.get();
    }

    /**
     * Gets the number of cache evictions.
     *
     * @return the cache evictions count
     */
    public long getCacheEvictions() {
        return cacheEvictions.get();
    }

    /**
     * Gets the current cache size.
     *
     * @return the current cache size
     */
    public long getCacheSize() {
        return cacheSize.get();
    }

    /**
     * Gets the maximum cache size.
     *
     * @return the maximum cache size
     */
    public long getMaxCacheSize() {
        return maxCacheSize.get();
    }

    /**
     * Gets the total number of cache requests.
     *
     * @return the total cache requests count
     */
    public long getTotalRequests() {
        return cacheHits.get() + cacheMisses.get();
    }

    /**
     * Gets the cache hit rate as a percentage.
     *
     * @return the hit rate percentage
     */
    public double getHitRate() {
        long total = getTotalRequests();
        if (total == 0) {
            return 0.0;
        }
        return (double) cacheHits.get() / total * 100.0;
    }

    /**
     * Gets the cache miss rate as a percentage.
     *
     * @return the miss rate percentage
     */
    public double getMissRate() {
        long total = getTotalRequests();
        if (total == 0) {
            return 0.0;
        }
        return (double) cacheMisses.get() / total * 100.0;
    }

    /**
     * Gets the cache utilization percentage.
     *
     * @return the cache utilization percentage
     */
    public double getCacheUtilization() {
        long maxSize = maxCacheSize.get();
        if (maxSize == 0) {
            return 0.0;
        }
        return (double) cacheSize.get() / maxSize * 100.0;
    }

    /**
     * Gets the uptime in milliseconds.
     *
     * @return the uptime in milliseconds
     */
    public long getUptimeMillis() {
        return Instant.now().toEpochMilli() - startTime.toEpochMilli();
    }

    /**
     * Gets the requests per second rate.
     *
     * @return the requests per second
     */
    public double getRequestsPerSecond() {
        long uptimeSeconds = getUptimeMillis() / 1000;
        if (uptimeSeconds == 0) {
            return 0.0;
        }
        return (double) getTotalRequests() / uptimeSeconds;
    }

    @Override
    public String toString() {
        return "CacheStatistics{" +
                "cacheId='" + cacheId + '\'' +
                ", startTime=" + startTime +
                ", cacheHits=" + cacheHits.get() +
                ", cacheMisses=" + cacheMisses.get() +
                ", cacheEvictions=" + cacheEvictions.get() +
                ", cacheSize=" + cacheSize.get() +
                ", maxCacheSize=" + maxCacheSize.get() +
                ", hitRate=" + String.format("%.2f%%", getHitRate()) +
                ", cacheUtilization=" + String.format("%.2f%%", getCacheUtilization()) +
                ", requestsPerSecond=" + String.format("%.2f", getRequestsPerSecond()) +
                '}';
    }
}
