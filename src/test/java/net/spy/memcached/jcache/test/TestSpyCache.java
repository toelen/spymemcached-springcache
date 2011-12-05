package net.spy.memcached.jcache.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.spring.cache.MemcachedCacheManager;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;

import com.thimbleware.jmemcached.CacheImpl;
import com.thimbleware.jmemcached.Key;
import com.thimbleware.jmemcached.LocalCacheElement;
import com.thimbleware.jmemcached.MemCacheDaemon;
import com.thimbleware.jmemcached.storage.CacheStorage;
import com.thimbleware.jmemcached.storage.hash.ConcurrentLinkedHashMap;

public class TestSpyCache {
	private static CacheManager cacheManager;
	private static Cache cache;

	@BeforeClass
	public static void setup() throws IOException {
		int port = 11212;
		// create daemon and start it
		final MemCacheDaemon<LocalCacheElement> daemon = new MemCacheDaemon<LocalCacheElement>();

		CacheStorage<Key, LocalCacheElement> storage = ConcurrentLinkedHashMap
				.create(ConcurrentLinkedHashMap.EvictionPolicy.FIFO, 10000,
						10000);
		daemon.setCache(new CacheImpl(storage));
		// daemon.setBinary(true);
		daemon.setAddr(new InetSocketAddress("localhost", port));
		// daemon.setIdleTime(10000);
		// daemon.setVerbose(true);
		daemon.start();

		String servers = "localhost:" + port;
		System.setProperty("spymemcachedservers", servers);

		List<InetSocketAddress> addr = new ArrayList<InetSocketAddress>();
		addr.add(new InetSocketAddress("localhost", port));
		MemcachedClient client = new MemcachedClient(addr);
		// AddrUtil.getAddresses(servers));
		cacheManager = new MemcachedCacheManager(client, 3600);
		assertNotNull(cacheManager);
		cache = cacheManager.getCache("unittest");
		assertNotNull(cache);
	}

	@Test
	public void testNonExisting() {
		ValueWrapper value = cache.get(UUID.randomUUID());
		assertNotNull(value);
		assertNull(value.get());
	}

	@Test
	public void testPutAndGet() {
		UUID key = UUID.randomUUID();
		cache.put(key, key);
		ValueWrapper value = cache.get(key);
		assertNotNull(value);
		assertNotNull(value.get());
		assertEquals(key, value.get());
	}

	@Test
	public void testMultiple() {
		for (int i = 0; i < 100; i++) {
			UUID key = UUID.randomUUID();
			cache.put(key, key);
			ValueWrapper value = cache.get(key);
			assertNotNull(value);
			assertNotNull(value.get());
			assertEquals(key, value.get());
		}
	}

	@Test
	public void testDelete() {
		UUID key = UUID.randomUUID();
		cache.put(key, key);
		ValueWrapper value = cache.get(key);
		assertNotNull(value);
		assertNotNull(value.get());
		assertEquals(key, value.get());

		cache.evict(key);

		value = cache.get(key);
		assertNotNull(value);
		assertNull(value.get());
	}
}
