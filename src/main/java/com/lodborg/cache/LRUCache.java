package com.lodborg.cache;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The LRU cache consists of a doubly linked list and a hash map. Each node in
 * the linked list holds a page in the cache (essentially, a key-value pair).
 * Whenever a key is accessed, either by an update or a get operation, the
 * corresponding node is bumped to the head of the list. This ensures that the
 * nodes are ordered by their access time with the most recently nodes at the head
 * of the list and the least recently used at the tail.
 *
 * List operations take constant time once the desired node is found. To avoid
 * linearly searching the list for a given node, the implementation uses a HashMap
 * indexed on the keys and mapping the keys to nodes in the linked list. Since
 * we need to store the concrete nodes in the list as values in the map, using
 * the LinkedList implementation from java.util is not an option. Instead,
 * a custom private list class is provided.
 *
 * The iterator of the cache returns the Nodes of the linked list in the order
 * they are stored, the most recent elements first. The iterator is fail-fast,
 * meaning that it will throw a ConcurrentModificationException, if the cache
 * gets modified by any means other than the iterator's remove() method.
 * Iterating via the iterator doesn't cause the elements to be bumped to the
 * head of the cache.
 *
 * @param <K> The generic type of the keys
 * @param <V> The generic type of the values
 */
public class LRUCache<K, V> implements Iterable<LRUCache.Node<K, V>>{

	/**
	 * A class representing a node in the doubly linked list. The node is the only
	 * place in the cache actually containing the real value mapped to a key.
	 */
	public static class Node<K, V>{
		private K key;
		private V value;
		private Node<K, V> prev, next;

		private Node(K key, V value){
			this.key = key;
			this.value = value;
		}

		public K getKey(){
			return key;
		}

		public V getValue(){
			return value;
		}
	}

	/**
	 * An implementation of a queue using a doubly linked list.
	 */
	private class LinkedList {
		private Node<K, V> head, last;

		/**
		 * Detaches the last node from the tail of the list and returns it.
		 * @return The last node from the tail of the list, after it was detached
		 *         from the list. Returns null, if the list was empty.
		 */
		private Node<K, V> poll(){
			if (head == null)
				return null;
			if (head == last){
				Node<K, V> node = last;
				head = last = null;
				return node;
			}
			Node<K, V> node = last;
			last = last.prev;
			last.next = null;
			node.prev = null;
			return node;
		}

		/**
		 * Adds a node at the head of the list.
		 * @param node The node that will be added.
		 */
		private void offer(Node<K, V> node){
			if (head == null){
				head = last = node;
				return;
			}
			head.prev = node;
			node.next = head;
			head = node;
		}

		/**
		 * Removes a node from the linked list. Assumes that the node is a part
		 * of the current list, but doesn't check explicitly for that. The user
		 * has to make sure that she is removing nodes from the correct list.
		 * @param node A reference to he node to be removed.
		 */
		private void remove(Node<K, V> node){
			if (node == last){
				poll();
			} else {
				if (head == node)
					head = node.next;
				else
					node.prev.next = node.next;
				node.next.prev = node.prev;
				node.prev = null;
				node.next = null;
			}
		}

		/**
		 * Removes all elements from the list. Currently, it only dereferences the
		 * head an tail pointers and leaves the cleanup to the garbage collector.
		 * Make sure that you are not using the nodes somewhere else in the code or
		 * they will not be garbage collected, possibly resulting in a memory leak.
		 */
		private void clear(){
			list.head = list.last = null;
		}
	}

	private LinkedList list;
	private HashMap<K, Node<K, V>> map;
	private int maxSize;
	private int size;
	private EvictionListener<K, V> listener;
	private int modCount;

	/**
	 * Instantiates a new cache instance.
	 * @param capacity The total amount of key-value pairs that can be stored in the
	 *                 cache. If the cache overflows, the least recently used pair
	 *                 will be removed.
	 */
	public LRUCache(int capacity){
		maxSize = capacity;
		list = new LinkedList();
		map = new HashMap<>();
	}

	public void setListener(EvictionListener<K, V> listener){
		this.listener = listener;
	}

	public void removeListener(){
		this.listener = null;
	}

	/**
	 * Returns the value for the given key, if it is stored in the cache, or null
	 * otherwise. It bumps the linked list node corresponding to that key to the
	 * head of the list.
	 * @param key The key for the lookup.
	 * @return The value associated with the key or null, if not in the cache
	 */
	public V get(K key){
		modCount++;
		Node<K, V> node = map.get(key);
		if (node == null)
			return null;
		list.remove(node);
		list.offer(node);
		return node.value;
	}

	/**
	 * Inserts a new key-value pair in the cache, or updates the value associated
	 * with a key already in the cache. Either way, the node corresponding to the
	 * key in the linked list is moved at the head of the list.
	 * @param key The key to be changed or inserted.
	 * @param value The value associated to the key.
	 */
	public void put(K key, V value){
		modCount++;
		Node<K, V> node = map.get(key);
		if (node == null){
			node = new Node<>(key, value);
			list.offer(node);
			map.put(key, node);
			if (size == maxSize){
				Node<K, V> removed = list.poll();
				if (listener != null)
					listener.onEvict(removed.key, removed.value);
				map.remove(removed.key);
			} else
				size++;
		} else {
			node.value = value;
			list.remove(node);
			list.offer(node);
		}
	}

	/**
	 * Removes a key and its associated value from the cache.
	 * @param key The key to be removed.
	 */
	public void evict(K key){
		modCount++;
		Node<K, V> node = map.get(key);
		if (node != null) {
			list.remove(node);
			map.remove(key);
			size--;
		}
	}

	/**
	 * @return The total amount of key-value pairs stored in the cache.
	 */
	public int size(){
		return size;
	}

	public void evictAll(){
		modCount++;
		list.clear();
		map.clear();
		size = 0;
	}

	@Override
	public String toString() {
		if (list.head == null)
			return "()";
		StringBuilder builder = new StringBuilder();
		builder.append('(');
		builder.append(list.head.key);

		Node node = list.head.next;
		while (node != null){
			builder.append(',');
			builder.append(node.key);
			node = node.next;
		}
		builder.append(')');
		return builder.toString();
	}

	/**
	 * The iterator will return the elements in the cache in the order they are stored,
	 * most recent elements first. The iterator is fail-fast and will fail, if the
	 * cache has been modified by any means other than the iterator's own remove() method.
	 */
	@Override
	public Iterator<LRUCache.Node<K, V>> iterator() {
		// The prev pointer of the head is not set, because this is only a temporary node.
		// It must be garbage collected once the iterator moves to the next element, that's
		// why there has to be no permanent references to it.
		final Node<K, V> placeholder = new Node<>(null, null);
		placeholder.next = list.head;

		return new Iterator<LRUCache.Node<K, V>>() {
			Node<K, V> node = placeholder;
			int currentModCount = modCount;

			@Override
			public boolean hasNext() {
				return node.next != null;
			}

			@Override
			public Node<K, V> next() {
				if (currentModCount != modCount)
					throw new ConcurrentModificationException();
				if (!hasNext())
					throw new NoSuchElementException();

				node = node.next;
				return node;
			}

			@Override
			public void remove() {
				if (node.key != null ) {
					Node<K, V> placeholder = new Node<>(null, null);
					placeholder.next = node.next;
					evict(node.key);
					node = placeholder;
					currentModCount++;
				}
			}
		};
	}
}