package net.spy.memcached.spring.cache;

import java.util.Collection;

import net.spy.memcached.MemcachedClient;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

public class MemcachedCacheManager extends AbstractCacheManager {
	private Collection<Cache> caches;
	private final MemcachedClient client;
	private final int expiry;

	public MemcachedCacheManager(MemcachedClient client, int expiry) {
		this.client = client;
		this.expiry = expiry;
	}

	/**
	 * Specify the collection of Cache instances to use for this CacheManager.
	 */
	public void setCaches(Collection<Cache> caches) {
		this.caches = caches;
	}

	@Override
	protected Collection<Cache> loadCaches() {
		return this.caches;
	}

	public Cache getCache(String name) {
		Cache cache = super.getCache(name);
		if (cache == null) {
			cache = new MemcachedCache(name, client, expiry);
			addCache(cache);
		}
		return cache;
	}

}
