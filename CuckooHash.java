/******************************************************************
 *
 *   Zaki Khan / 272 001
 *
 *   Note, additional comments provided throughout this source code
 *   is for educational purposes
 *
 ********************************************************************/

import java.util.*;
import java.lang.Math;

@SuppressWarnings("unchecked")
public class CuckooHash<K, V> {
    private int CAPACITY;
    private Bucket<K, V>[] table;
    private int a = 37, b = 17;
    private LinkedHashMap<K, V> insertionOrderMap = new LinkedHashMap<>();

    private class Bucket<K, V> {
        private K bucKey = null;
        private V value = null;
        
        public Bucket(K k, V v) {
            bucKey = k; 
            value = v;
        }

        private K getBucKey() { return bucKey; }
        private V getValue() { return value; }
    }

    private int hash1(K key) { return Math.abs(key.hashCode()) % CAPACITY; }
    private int hash2(K key) { return (a * b + Math.abs(key.hashCode())) % CAPACITY; }

    public CuckooHash(int size) {
        CAPACITY = size;
        table = new Bucket[CAPACITY];
    }

    public int size() {
        return insertionOrderMap.size();
    }

    public void clear() {
        table = new Bucket[CAPACITY];
        insertionOrderMap.clear();
    }

    public int mapSize() { return CAPACITY; }

    public List<V> values() {
        return new ArrayList<>(insertionOrderMap.values());
    }

    public Set<K> keys() {
        return insertionOrderMap.keySet();
    }

    public void put(K key, V value) {
        // Check if this exact key-value pair already exists
        if (insertionOrderMap.containsKey(key) && insertionOrderMap.get(key).equals(value)) {
            return;
        }

        Bucket<K, V> newBucket = new Bucket<>(key, value);
        int pos1 = hash1(key);
        int pos2 = hash2(key);

        // Try to place in first position
        if (table[pos1] == null) {
            table[pos1] = newBucket;
            insertionOrderMap.put(key, value);
            return;
        }

        // Try to place in second position
        if (table[pos2] == null) {
            table[pos2] = newBucket;
            insertionOrderMap.put(key, value);
            return;
        }

        // Need to evict someone - start with first position
        Bucket<K, V> current = newBucket;
        int currentPos = pos1;
        int iterations = 0;

        while (iterations <= CAPACITY) {
            if (table[currentPos] == null) {
                table[currentPos] = current;
                insertionOrderMap.put(current.getBucKey(), current.getValue());
                return;
            }

            // Swap current with existing
            Bucket<K, V> temp = table[currentPos];
            table[currentPos] = current;
            current = temp;

            // Move to alternate position
            currentPos = (currentPos == hash1(current.getBucKey())) 
                ? hash2(current.getBucKey()) 
                : hash1(current.getBucKey());

            iterations++;
        }

        // If we get here, we have a cycle - rehash
        rehash();
        put(current.getBucKey(), current.getValue());
    }

    public V get(K key) {
        return insertionOrderMap.get(key);
    }

    public boolean remove(K key, V value) {
        if (!insertionOrderMap.containsKey(key) || !insertionOrderMap.get(key).equals(value)) {
            return false;
        }
        
        int pos1 = hash1(key);
        int pos2 = hash2(key);
        
        if (table[pos1] != null && table[pos1].getBucKey().equals(key) && table[pos1].getValue().equals(value)) {
            table[pos1] = null;
            insertionOrderMap.remove(key);
            return true;
        }
        else if (table[pos2] != null && table[pos2].getBucKey().equals(key) && table[pos2].getValue().equals(value)) {
            table[pos2] = null;
            insertionOrderMap.remove(key);
            return true;
        }
        return false;
    }

    public String printTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (Map.Entry<K, V> entry : insertionOrderMap.entrySet()) {
            sb.append("<");
            sb.append(entry.getKey());
            sb.append(", ");
            sb.append(entry.getValue());
            sb.append("> ");
        }
        sb.append("]");
        return sb.toString();
    }

    private void rehash() {
        Map<K, V> oldEntries = new LinkedHashMap<>(insertionOrderMap);
        CAPACITY = (CAPACITY * 2) + 1;
        table = new Bucket[CAPACITY];
        insertionOrderMap.clear();
        
        for (Map.Entry<K, V> entry : oldEntries.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
}
