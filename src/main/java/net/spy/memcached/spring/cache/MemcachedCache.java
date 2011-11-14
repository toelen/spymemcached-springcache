package net.spy.memcached.spring.cache;

import java.util.concurrent.ExecutionException;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;

import org.springframework.cache.Cache;
import org.springframework.cache.support.ValueWrapperImpl;

public class MemcachedCache implements Cache {
	private final String name;
	private MemcachedClient client;
	private int expiry = 3600;

	public MemcachedCache(String name, MemcachedClient client, int expiry) {
		this.name = name;
		this.client = client;
		this.expiry = expiry;
	}

	public String getName() {
		return name;
	}

	public Object getNativeCache() {
		return client;
	}

	private static String keyToString(Object key) {
		if (key == null) {
			return null;
		} else if (key instanceof String) {
			return (String) key;
		} else {
			return key.toString();
		}
	}

	public ValueWrapper get(Object key) {
		Object value = client.get(keyToString(key));
		return new ValueWrapperImpl(value);
	}

	public void put(Object key, Object value) {
		OperationFuture<Boolean> of = client.add(keyToString(key), expiry,
				value);
		try {
			of.get();
		} catch (InterruptedException e) {
			of.cancel(false);
		} catch (ExecutionException e) {
			of.cancel(false);
		}
	}

	public void evict(Object key) {
		OperationFuture<Boolean> of = client.delete(keyToString(key));
		try {
			of.get();
		} catch (InterruptedException e) {
			of.cancel(false);
		} catch (ExecutionException e) {
			of.cancel(false);
		}
	}

	public void clear() {
		// Not implemented
	}

	public void setClient(MemcachedClient client) {
		this.client = client;
	}

	public void setExpiry(int expiry) {
		this.expiry = expiry;
	}

}
