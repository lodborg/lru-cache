package com.lodborg.cache;

import org.junit.Test;
import static org.mockito.Mockito.*;

public class EvictionListenerTest {
	@Test
	public void test_executeOnOverflow(){
		EvictionListener mock = mock(EvictionListener.class);
		LRUCache<Integer, Integer> cache = new LRUCache<>(2);
		cache.setListener(mock);
		cache.put(1, 123);
		cache.put(2, 222);
		cache.put(3, 333);
		verify(mock).onEvict(1, 123);
	}

	@Test
	public void test_dontExecuteOnManualEvict(){
		EvictionListener mock = mock(EvictionListener.class);
		LRUCache<Integer, Integer> cache = new LRUCache<>(2);
		cache.setListener(mock);
		cache.put(1, 123);
		cache.put(2, 222);
		cache.evict(1);
		verify(mock, never()).onEvict(any(Integer.class), any(Integer.class));
	}

	@Test
	public void test_dontExecuteAfterReset(){
		EvictionListener mock = mock(EvictionListener.class);
		LRUCache<Integer, Integer> cache = new LRUCache<>(2);
		cache.setListener(mock);
		cache.put(1, 123);
		cache.put(2, 222);
		cache.removeListener();
		cache.put(3, 333);
		verify(mock, never()).onEvict(any(Integer.class), any(Integer.class));
	}

	@Test
	public void test_dontExecuteOnEvictAll(){
		EvictionListener mock = mock(EvictionListener.class);
		LRUCache<Integer, Integer> cache = new LRUCache<>(2);
		cache.setListener(mock);
		cache.put(1, 123);
		cache.put(2, 222);
		cache.evictAll();
		verify(mock, never()).onEvict(any(Integer.class), any(Integer.class));
	}
}
