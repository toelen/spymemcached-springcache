package net.spy.memcached.spring.cache;

import java.util.concurrent.ExecutionException;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;

import org.springframework.cache.Cache;
import org.springframework.cache.support.ValueWrapperImpl;

public class MemcachedCache implements Cache {
	private final String name;
	private final MemcachedClient client;
	private int expiry = 3600;

	public MemcachedCache(String name, MemcachedClient client, int expiry) {
		this.name = name;
		this.client = client;
		this.expiry = expiry;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
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

	@Override
	public ValueWrapper get(Object key) {
		Object value = client.get(keyToString(key));
		return new ValueWrapperImpl(value);
	}

	@Override
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

	@Override
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

	@Override
	public void clear() {
		// Not implemented
	}

}
