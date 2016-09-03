package com.lodborg.cache;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LRUCacheTest {

	@Test
	public void test_getEmpty(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(1);
		assertNull(cache.get(1));
	}

	@Test
	public void test_nullValue(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(3);
		cache.put(1, 11);
		cache.put(2, null);
		cache.put(3, 13);
		assertNull(cache.get(2));
		assertEquals(3, cache.size());
		assertEquals("(2,3,1)", cache.toString());
	}

	@Test
	public void test_getLast(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(3);
		assertEquals(0, cache.size());
		cache.put(1, 11);
		cache.put(2, 12);
		cache.put(3, 13);
		assertTrue(cache.get(1) == 11);
		assertEquals(3, cache.size());
		assertEquals("(1,3,2)", cache.toString());
		cache.put(4, 14);
		assertEquals(3, cache.size());
		assertEquals("(4,1,3)", cache.toString());
		assertNull(cache.get(2));
	}

	@Test
	public void test_getFirst(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(3);
		cache.put(1, 11);
		cache.put(2, 12);
		cache.put(3, 13);
		assertTrue(cache.get(3) == 13);
		assertEquals("(3,2,1)", cache.toString());
		cache.put(4, 14);
		assertEquals(3, cache.size());
		assertEquals("(4,3,2)", cache.toString());
		assertNull(cache.get(1));
	}

	@Test
	public void test_getInner(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(3);
		cache.put(1, 11);
		cache.put(2, 12);
		cache.put(3, 13);
		assertTrue(cache.get(2) == 12);
		assertEquals(3, cache.size());
		assertEquals("(2,3,1)", cache.toString());
		cache.put(4, 14);
		assertEquals(3, cache.size());
		assertEquals("(4,2,3)", cache.toString());
		assertNull(cache.get(1));
	}

	@Test
	public void test_changeLast(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(2);
		cache.put(1, 11);
		cache.put(2, 12);
		cache.put(1, 111);
		assertEquals("(1,2)", cache.toString());
		assertEquals(2, cache.size());
		assertTrue(cache.get(1) == 111);
	}

	@Test
	public void test_changeFirst(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(2);
		cache.put(1, 11);
		cache.put(2, 12);
		cache.put(2, 22);
		assertEquals("(2,1)", cache.toString());
		assertEquals(2, cache.size());
		assertTrue(cache.get(2) == 22);
	}

	@Test
	public void test_changeMiddle(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(5);
		cache.put(1, 11);
		cache.put(2, 12);
		cache.put(3, 13);
		cache.put(2, 22);
		assertEquals("(2,3,1)", cache.toString());
		assertEquals(3, cache.size());
		assertTrue(cache.get(2) == 22);
	}

	@Test
	public void test_evictOnly(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(5);
		cache.put(1, 11);
		assertEquals(1, cache.size());
		cache.evict(1);
		assertEquals(0, cache.size());
		assertEquals("()", cache.toString());
		assertNull(cache.get(1));
	}

	@Test
	public void test_evictFirst(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(5);
		cache.put(1, 11);
		cache.put(2, 12);
		cache.put(3, 13);
		assertEquals(3, cache.size());
		cache.evict(3);
		assertEquals(2, cache.size());
		assertEquals("(2,1)", cache.toString());
		assertNull(cache.get(3));
	}

	@Test
	public void test_evictLast(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(5);
		cache.put(1, 11);
		cache.put(2, 12);
		cache.put(3, 13);
		cache.evict(1);
		assertEquals(2, cache.size());
		assertEquals("(3,2)", cache.toString());
		assertNull(cache.get(1));
	}

	@Test
	public void test_evictInner(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(5);
		cache.put(1, 11);
		cache.put(2, 12);
		cache.put(3, 13);
		cache.put(4, 14);
		cache.put(5, 15);
		assertEquals(5, cache.size());
		cache.evict(2);
		assertEquals(4, cache.size());
		assertEquals("(5,4,3,1)", cache.toString());
		assertNull(cache.get(2));
	}

	@Test
	public void test_sizeOne(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(1);
		cache.put(1, 11);
		assertEquals(1, cache.size());
		cache.put(2, 12);
		assertEquals(1, cache.size());
		cache.put(3, 13);
		assertEquals(1, cache.size());
		assertEquals("(3)", cache.toString());
		assertNull(cache.get(2));
		assertNull(cache.get(1));
	}

	@Test
	public void test_getNonExistent(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(8);
		cache.put(1, 11);
		cache.put(2, 12);
		cache.put(3, 13);
		assertNull(cache.get(8));
		assertEquals("(3,2,1)", cache.toString());
	}

	@Test
	public void test_evictNonExistent(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(8);
		cache.put(1, 11);
		cache.put(2, 12);
		cache.put(3, 13);
		assertEquals(3, cache.size());
		cache.evict(8);
		assertEquals(3, cache.size());
		assertEquals("(3,2,1)", cache.toString());
	}

	@Test
	public void test_evictAll(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(4);
		cache.put(1, 11);
		cache.put(2, 12);
		cache.put(3, 13);
		cache.put(4, 14);
		assertEquals(4, cache.size());
		cache.evictAll();
		assertEquals(0, cache.size());
	}

	@Test
	public void test_longer(){
		LRUCache<Integer, Integer> cache = new LRUCache<>(5);
		cache.put(1, 11);
		cache.put(2, 12);
		cache.put(3, 13);
		assertTrue(cache.get(2) == 12);
		cache.put(1, 111);
		cache.put(2, 22);
		cache.put(4, 14);
		cache.put(5, 15);
		cache.put(6, 16);
		assertNull(cache.get(3));
		assertTrue(cache.get(2) == 22);
		assertTrue(cache.get(6) == 16);
		cache.put(2, 12);
		assertTrue(cache.get(1) == 111);
		cache.put(7, 17);
		assertNull(cache.get(4));
		assertEquals("(7,1,2,6,5)", cache.toString());
	}
}
