package net.spy.memcached.spring.cache;

import java.util.Collection;

import net.spy.memcached.MemcachedClient;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

public class MemcachedCacheManager extends AbstractCacheManager {
	private Collection<Cache> caches;
	private MemcachedClient client = null;
	private int expiry = -1;

	public MemcachedCacheManager() {
	}

	public MemcachedCacheManager(MemcachedClient client, int expiry) {
		setClient(client);
		setExpiry(expiry);
	}

	private void checkState() {
		if (client == null) {
			throw new IllegalStateException(
					"MemcachedClient not configured yet");
		} else if (client.isAlive() == false) {
			throw new IllegalStateException("MemcachedClient is not alive");
		}
	}

	public Cache getCache(String name) {
		checkState();

		Cache cache = super.getCache(name);
		if (cache == null) {
			cache = new MemcachedCache(name, client, expiry);
			addCache(cache);
		}
		return cache;
	}

	private void updateCaches() {
		if (caches != null) {
			for (Cache cache : caches) {
				if (cache instanceof MemcachedCache) {
					MemcachedCache memcachedCache = (MemcachedCache) cache;
					memcachedCache.setClient(client);
					memcachedCache.setExpiry(expiry);
				}
			}
		}
	}

	public Collection<Cache> getCaches() {
		return caches;
	}

	public MemcachedClient getClient() {
		return client;
	}

	public int getExpiry() {
		return expiry;
	}

	@Override
	protected Collection<Cache> loadCaches() {
		return this.caches;
	}

	/**
	 * Specify the collection of Cache instances to use for this CacheManager.
	 */
	public void setCaches(Collection<Cache> caches) {
		this.caches = caches;
	}

	public void setClient(MemcachedClient client) {
		this.client = client;

		updateCaches();
	}

	public void setExpiry(int expiry) {
		this.expiry = expiry;

		updateCaches();
	}

	public void shutdown() {
		// TODO

	}

}
