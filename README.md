# Least Recently Used Cache (LRU Cache)
[![Build Status](https://travis-ci.org/lodborg/lru-cache.svg?branch=master)](https://travis-ci.org/lodborg/lru-cache)

An implementation for an efficient, light-weight and generic in-memory LRU Cache, that can be used to store key-value pairs for fast lookup. Some use cases:
* To store the output of a computationally heavy operation, so that next time you need the result, you don't have to execute it again.
* To avoid calling a web-service, if you have already fetched the response and expect that it wouldn't change.
* To minimize disk reads and store heavily used information in memory.

## Usage
```java
LRUCache<String, Integer> cache = new LRUCache<>(3);   // Instantiates a new cache with capacity for 3 objects

cache.put("New York", 7816);      // Let's put some data in the cache.
cache.put("Berlin", 2761);
cache.put("London", 9252);

cache.get("New York");            // Returns the value 7816, associated with New York
cache.size();                     // Returns the amount of stored objects, currently 3.

cache.put("Berlin", 2777);        // Updates the stored value for Berlin to 2777.
cache.size();                     // Still 3.

cache.put("Sydney", 5072);        // Since we only have capacity for 3 objects in the cache, adding a fourth
                                  // object will evict the least recently used object - in this case London.
cache.size();                     // Returns 3.

cache.get("London");              // Returns null, since the value is not stored anymore.

cache.evict("Sydney");            // Removes an object from the cache, freeing up space.
cache.size();                     // Returns 2.

cache.put("Tokyo", 5288);         // Adds another object to the cache.
```

## Data Structure
The LRU cache consists of a doubly linked list and a hash map. Each node in the linked list holds a page in the cache (essentially, a key-value pair). Whenever a key is accessed, either by an update or a get operation, the corresponding node is bumped to the head of the list. This ensures that the nodes are ordered by their access time with the most recently nodes at the head of the list and the least recently used at the tail.

Since list modifications, such as moving, deleting or inserting a new node, take constant time once the desired node is found, the efficiency of the data structure is bounded by the time necessary to find the particular node holding the information for a given key. Searching in a list requires a linear traversal and would be too slow. This is where the hash map comes in handy. The hash map is indexed by the keys of the key-value pairs, but instead of mapping the key to the particular value, it maps the key to the corresponding node in the linked list. This way we can find the linked list node for the price of one lookup in the hash table.

## A Note on Time Complexities
The hash map performance depends heavily on the hash function you employ to hash to hash your keys. Hash collisions are inevitable and expected in the hash map design. They don't cause performance degradation, if your hash function distributes objects uniformly among the buckets of the map. This is why it is extremely important to overwrite the `hashCode` and `equals` methods in your custom objects, if you are using them as keys in the cache.

Luckily, the implementation of HashMap in Java 8 switches automatically from list-based buckets to binary-tree-based ones, when it detects a heavy load on a bucket. This means that even if your hash function favors only few buckets in the map, lookups will still be very fast - more precisely, in O(logn). This closes a vulnerability in Java 7, which could be used as a [vector for hash map based attacks](http://www.ocert.org/advisories/ocert-2011-003.html). If you are using HashMap heavily in your application, it is worth considering switching to Java 8, if you haven't already.