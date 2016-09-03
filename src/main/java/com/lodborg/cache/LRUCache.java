package com.lodborg.cache;

import java.util.HashMap;

public class LRUCache<K, V> {
	private class Node{
		K key;
		V value;
		Node prev, next;

		private Node(K key, V value){
			this.key = key;
			this.value = value;
		}
	}

	private Node head, last;
	private HashMap<K, Node> map;
	private int maxSize;
	private int size;

	public LRUCache(int size){
		maxSize = size;
		map = new HashMap<>();
	}

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

	private void push(Node node){
		if (head == null){
			head = last = node;
			return;
		}
		head.prev = node;
		node.next = head;
		head = node;
	}

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

	public V get(K key){
		Node node = map.get(key);
		if (node == null)
			return null;
		remove(node);
		push(node);
		return node.value;
	}

	public void put(K key, V value){
		Node node = map.get(key);
		if (node == null){
			node = new Node(key, value);
			push(node);
			map.put(key, node);
			if (size == maxSize){
				map.remove(poll().key);
			} else
				size++;
		} else {
			node.value = value;
			remove(node);
			push(node);
		}
	}

	public void evict(K key){
		Node node = map.get(key);
		if (node != null) {
			remove(node);
			map.remove(key);
			size--;
		}
	}

	public int size(){
		return size;
	}

	public void evictAll(){
		head = last = null;
		map.clear();
		size = 0;
	}

	@Override
	public String toString() {
		if (head == null)
			return "()";
		StringBuilder builder = new StringBuilder();
		builder.append('(');
		builder.append(head.key);

		Node node = head.next;
		while (node != null){
			builder.append(',');
			builder.append(node.key);
			node = node.next;
		}
		builder.append(')');
		return builder.toString();
	}
}