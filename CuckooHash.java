/******************************************************************
 *
 *   Zaki Khan / 272 001
 *
 *   Cuckoo Hashing implementation with automatic rehashing
 *
 ********************************************************************/

import java.util.*;

public class CuckooHash<K,V> {
    private static final int MAX_ITERATIONS = 10;
    private Entry<K,V>[] table1, table2;
    private int size;
    private List<Entry<K,V>> insertionOrder;

    private static class Entry<K,V> {
        final K key;
        final V value;
        Entry(K k, V v) { key = k; value = v; }
    }

    public CuckooHash(int capacity) {
        table1 = new Entry[capacity];
        table2 = new Entry[capacity];
        insertionOrder = new ArrayList<>();
    }

    public void put(K key, V value) {
        if (key == null) return;
        
        Entry<K,V> newEntry = new Entry<>(key, value);
        
        // Check if already exists
        for (Entry<K,V> e : insertionOrder)
            if (e.key.equals(key) && e.value.equals(value))
                return;

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            // Try table1 first
            int h1 = hash1(key);
            if (table1[h1] == null) {
                table1[h1] = newEntry;
                insertionOrder.add(newEntry);
                size++;
                return;
            }
            
            // Evict and try table2
            Entry<K,V> evicted = table1[h1];
            table1[h1] = newEntry;
            
            int h2 = hash2(evicted.key);
            if (table2[h2] == null) {
                table2[h2] = evicted;
                insertionOrder.add(newEntry);
                size++;
                return;
            }
            
            // Both tables full, continue cuckoo process
            newEntry = table2[h2];
            table2[h2] = evicted;
        }
        
        // Rehash if we get here
        rehash();
        put(key, value);
    }

    private void rehash() {
        List<Entry<K,V>> entries = new ArrayList<>(insertionOrder);
        int newSize = table1.length * 2 + 1;
        
        table1 = new Entry[newSize];
        table2 = new Entry[newSize];
        insertionOrder.clear();
        size = 0;
        
        for (Entry<K,V> e : entries)
            put(e.key, e.value);
    }

    public V get(K key) {
        if (key == null) return null;
        
        int h1 = hash1(key);
        if (table1[h1] != null && table1[h1].key.equals(key))
            return table1[h1].value;
            
        int h2 = hash2(key);
        if (table2[h2] != null && table2[h2].key.equals(key))
            return table2[h2].value;
            
        return null;
    }

    private int hash1(K key) {
        return Math.abs(key.hashCode()) % table1.length;
    }

    private int hash2(K key) {
        return (Math.abs(key.hashCode()) * 31) % table2.length;
    }

    public List<V> values() {
        List<V> values = new ArrayList<>();
        for (Entry<K,V> e : insertionOrder)
            values.add(e.value);
        return values;
    }

    // Other required methods (size, clear, etc.) would go here
}
