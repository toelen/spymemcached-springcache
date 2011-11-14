package net.spy.memcached.jcache.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.UUID;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.spring.cache.MemcachedCacheManager;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;

public class TestSpyCache {
	private static CacheManager cacheManager;
	private static Cache cache;

	@BeforeClass
	public static void setup() throws IOException {
		String servers = "localhost:11211";
		System.setProperty("spymemcachedservers", servers);

		MemcachedClient client = new MemcachedClient(
				AddrUtil.getAddresses(servers));
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
