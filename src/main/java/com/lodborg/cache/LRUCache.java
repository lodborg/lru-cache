package com.lodborg.cache;

import java.util.HashMap;

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
 * @param <K> The generic type of the keys
 * @param <V> The generic type of the values
 */
public class LRUCache<K, V> {

	/**
	 * A class representing a node in the doubly linked list. The node is the only
	 * place in the cache actually containing the real value mapped to a key.
	 */
	private class Node{
		K key;
		V value;
		Node prev, next;

		private Node(K key, V value){
			this.key = key;
			this.value = value;
		}
	}

	/**
	 * An implementation of a queue using a doubly linked list.
	 */
	private class LinkedList {
		private Node head, last;

		/**
		 * Detaches the last node from the tail of the list and returns it.
		 * @return The last node from the tail of the list, after it was detached
		 *         from the list. Returns null, if the list was empty.
		 */
		private Node poll(){
			if (head == null)
				return null;
			if (head == last){
				Node node = last;
				head = last = null;
				return node;
			}
			Node node = last;
			last = last.prev;
			last.next = null;
			node.prev = null;
			return node;
		}

		/**
		 * Adds a node at the head of the list.
		 * @param node The node that will be added.
		 */
		private void offer(Node node){
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
		private void remove(Node node){
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
	private HashMap<K, Node> map;
	private int maxSize;
	private int size;
	EvictionListener<K, V> listener;

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
		Node node = map.get(key);
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
		Node node = map.get(key);
		if (node == null){
			node = new Node(key, value);
			list.offer(node);
			map.put(key, node);
			if (size == maxSize){
				Node removed = list.poll();
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
		Node node = map.get(key);
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
}