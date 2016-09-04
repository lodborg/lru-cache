package com.lodborg.cache;

/**
 * This interface can be used in conjunction with the LRU Cache and defines
 * a callback that will be executed by the cache whenever it evicts an item.
 * This will happen only if the item has been discarded due to cache
 * overflow. If the item has been evicted via an API call, the callback will
 * not be executed.
 */
public interface EvictionListener<K, V> {
	void onEvict(K key, V value);
}
