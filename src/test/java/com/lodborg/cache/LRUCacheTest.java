package com.lodborg.cache;

import org.junit.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

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
		assertNull(cache.get(1));
		assertNull(cache.get(2));
		assertNull(cache.get(3));
		assertNull(cache.get(4));
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

	@Test
	public void test_String(){
		LRUCache<String, Integer> cache = new LRUCache<>(3);
		cache.put("New York", 1);
		cache.put("London", 2);
		cache.put("Berlin", 3);
		assertTrue(1 == cache.get("New York"));
		cache.put("Tokyo", 4);
		assertNull(cache.get("London"));
		assertTrue(1 == cache.get("New York"));
		assertEquals("(New York,Tokyo,Berlin)", cache.toString());
		cache.evict(new String("Tokyo"));
		assertEquals(2, cache.size());
		assertNull(cache.get("Tokyo"));
		assertTrue(3 == cache.get(new String("Berlin")));
	}

	@Test
	public void test_iterator(){
		LRUCache<String, Integer> cache = new LRUCache<>(3);
		cache.put("New York", 1);
		cache.put("London", 2);
		cache.put("Berlin", 3);
		assertTrue(1 == cache.get("New York"));
		cache.put("Tokyo", 4);
		Iterator<LRUCache.Node<String, Integer>> it = cache.iterator();
		assertEquals("Tokyo", it.next().getKey());
		assertEquals("New York", it.next().getKey());
		assertEquals("Berlin", it.next().getKey());
		try {
			it.next();
			fail("Iterator should have stopped here.");
		} catch (Exception e){
			assertTrue(e instanceof NoSuchElementException);
		}
	}

	@Test
	public void test_iteratorFailsOnPut(){
		LRUCache<String, Integer> cache = new LRUCache<>(3);
		cache.put("New York", 1);
		cache.put("London", 2);
		cache.put("Berlin", 3);
		Iterator<LRUCache.Node<String, Integer>> it = cache.iterator();
		it.next();
		cache.put("Berlin", 4);
		try {
			it.next();
			fail();
		} catch (Exception e){
			assertTrue(e instanceof ConcurrentModificationException);
		}
	}

	@Test
	public void test_iteratorFailsOnGet(){
		LRUCache<String, Integer> cache = new LRUCache<>(3);
		cache.put("New York", 1);
		cache.put("London", 2);
		cache.put("Berlin", 3);
		Iterator<LRUCache.Node<String, Integer>> it = cache.iterator();
		it.next();
		assertTrue(2 == cache.get("London"));
		try {
			it.next();
			fail();
		} catch (Exception e){
			assertTrue(e instanceof ConcurrentModificationException);
		}
	}

	@Test
	public void test_iteratorFailsOnEvict(){
		LRUCache<String, Integer> cache = new LRUCache<>(3);
		cache.put("New York", 1);
		cache.put("London", 2);
		cache.put("Berlin", 3);
		Iterator<LRUCache.Node<String, Integer>> it = cache.iterator();
		it.next();
		cache.evict("Berlin");
		try {
			it.next();
			fail();
		} catch (Exception e){
			assertTrue(e instanceof ConcurrentModificationException);
		}
	}

	@Test
	public void test_iteratorFailsOnEvictAll(){
		LRUCache<String, Integer> cache = new LRUCache<>(3);
		cache.put("New York", 1);
		cache.put("London", 2);
		cache.put("Berlin", 3);
		Iterator<LRUCache.Node<String, Integer>> it = cache.iterator();
		it.next();
		cache.evictAll();
		try {
			it.next();
			fail();
		} catch (Exception e){
			assertTrue(e instanceof ConcurrentModificationException);
		}
	}

	@Test
	public void test_iteratorDoesntFailOnRemove(){
		LRUCache<String, Integer> cache = new LRUCache<>(3);
		cache.put("New York", 1);
		cache.put("London", 2);
		cache.put("Berlin", 3);
		Iterator<LRUCache.Node<String, Integer>> it = cache.iterator();
		it.next();
		it.remove();
		assertTrue(it.next().getKey().equals("London"));
		assertTrue(it.next().getKey().equals("New York"));
		assertFalse(it.hasNext());
		assertNull(cache.get("Berlin"));
	}

	@Test
	public void test_iteratorRemoveBeforeHead(){
		LRUCache<String, Integer> cache = new LRUCache<>(3);
		cache.put("New York", 1);
		cache.put("London", 2);
		cache.put("Berlin", 3);
		Iterator<LRUCache.Node<String, Integer>> it = cache.iterator();
		it.remove();
		assertTrue(it.next().getKey().equals("Berlin"));
		assertTrue(it.next().getKey().equals("London"));
		assertTrue(it.next().getKey().equals("New York"));
		assertFalse(it.hasNext());
		assertNotNull(cache.get("Berlin"));
		assertNotNull(cache.get("New York"));
		assertNotNull(cache.get("London"));
	}

	@Test
	public void test_iteratorRemoveHead(){
		LRUCache<String, Integer> cache = new LRUCache<>(3);
		cache.put("New York", 1);
		cache.put("London", 2);
		cache.put("Berlin", 3);
		Iterator<LRUCache.Node<String, Integer>> it = cache.iterator();
		assertTrue(it.next().getKey().equals("Berlin"));
		it.remove();
		assertTrue(it.next().getKey().equals("London"));
		assertTrue(it.next().getKey().equals("New York"));
		assertFalse(it.hasNext());
	}

	@Test
	public void test_iteratorRemoveBeforeLast(){
		LRUCache<String, Integer> cache = new LRUCache<>(3);
		cache.put("New York", 1);
		cache.put("London", 2);
		cache.put("Berlin", 3);
		Iterator<LRUCache.Node<String, Integer>> it = cache.iterator();
		assertTrue(it.next().getKey().equals("Berlin"));
		assertTrue(it.next().getKey().equals("London"));
		it.remove();
		assertTrue(it.next().getKey().equals("New York"));
		assertFalse(it.hasNext());
		assertNull(cache.get("London"));
		assertNotNull(cache.get("New York"));
		assertNotNull(cache.get("Berlin"));
	}

	@Test
	public void test_iteratorRemoveLast(){
		LRUCache<String, Integer> cache = new LRUCache<>(3);
		cache.put("New York", 1);
		cache.put("London", 2);
		cache.put("Berlin", 3);
		Iterator<LRUCache.Node<String, Integer>> it = cache.iterator();
		assertTrue(it.next().getKey().equals("Berlin"));
		assertTrue(it.next().getKey().equals("London"));
		assertTrue(it.next().getKey().equals("New York"));
		it.remove();
		assertFalse(it.hasNext());
		assertNull(cache.get("New York"));
		assertNotNull(cache.get("London"));
		assertNotNull(cache.get("Berlin"));
	}

	@Test
	public void test_iteratorRemoveTwiceOnSameElement(){
		LRUCache<String, Integer> cache = new LRUCache<>(3);
		cache.put("New York", 1);
		cache.put("London", 2);
		cache.put("Berlin", 3);
		Iterator<LRUCache.Node<String, Integer>> it = cache.iterator();
		assertTrue(it.next().getKey().equals("Berlin"));
		assertTrue(it.next().getKey().equals("London"));
		assertTrue(it.next().getKey().equals("New York"));
		it.remove();
		it.remove();
		assertFalse(it.hasNext());
		assertNull(cache.get("New York"));
		assertNotNull(cache.get("London"));
		assertNotNull(cache.get("Berlin"));
		assertEquals(2, cache.size());
	}

	@Test
	public void test_iteratorRemoveTwice(){
		LRUCache<String, Integer> cache = new LRUCache<>(3);
		cache.put("New York", 1);
		cache.put("London", 2);
		cache.put("Berlin", 3);
		Iterator<LRUCache.Node<String, Integer>> it = cache.iterator();
		assertTrue(it.next().getKey().equals("Berlin"));
		assertTrue(it.next().getKey().equals("London"));
		it.remove();
		assertTrue(it.next().getKey().equals("New York"));
		it.remove();
		assertFalse(it.hasNext());
		assertNull(cache.get("New York"));
		assertNull(cache.get("London"));
		assertNotNull(cache.get("Berlin"));
		assertEquals(1, cache.size());
	}
}
