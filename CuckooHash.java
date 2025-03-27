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
    private int insertionCounter = 0;

    private class Bucket<K, V> {
        private K bucKey = null;
        private V value = null;
        private int insertionOrder;
        
        public Bucket(K k, V v, int order) {
            bucKey = k; 
            value = v;
            insertionOrder = order;
        }

        private K getBucKey() { return bucKey; }
        private V getValue() { return value; }
        private int getInsertionOrder() { return insertionOrder; }
    }

    private int hash1(K key) { return Math.abs(key.hashCode()) % CAPACITY; }
    private int hash2(K key) { return (a * b + Math.abs(key.hashCode())) % CAPACITY; }

    public CuckooHash(int size) {
        CAPACITY = size;
        table = new Bucket[CAPACITY];
    }

    public int size() {
        int count = 0;
        for (int i = 0; i < CAPACITY; ++i) {
            if (table[i] != null)
                count++;     
        }
        return count;
    }

    public void clear() {
        table = new Bucket[CAPACITY]; 
        insertionCounter = 0;
    }

    public int mapSize() { return CAPACITY; }

    public List<V> values() {
        List<Bucket<K, V>> buckets = new ArrayList<>();
        for (int i = 0; i < CAPACITY; ++i) {
            if (table[i] != null) {
                buckets.add(table[i]);
            }
        }
        buckets.sort(Comparator.comparingInt(Bucket::getInsertionOrder));
        List<V> values = new ArrayList<>();
        for (Bucket<K, V> bucket : buckets) {
            values.add(bucket.getValue());
        }
        return values;
    }

    public Set<K> keys() {
        Set<K> allKeys = new HashSet<K>();
        for (int i = 0; i < CAPACITY; ++i) {
            if (table[i] != null) {
                allKeys.add(table[i].getBucKey());
            }
        }
        return allKeys;
    }

    public void put(K key, V value) {
        // Check if this exact key-value pair already exists
        if (get(key) != null && get(key).equals(value)) {
            return;
        }

        Bucket<K, V> newBucket = new Bucket<>(key, value, insertionCounter++);
        int pos1 = hash1(key);
        int pos2 = hash2(key);

        // Try to place in first position
        if (table[pos1] == null) {
            table[pos1] = newBucket;
            return;
        }

        // Try to place in second position
        if (table[pos2] == null) {
            table[pos2] = newBucket;
            return;
        }

        // Need to evict someone - start with first position
        Bucket<K, V> current = newBucket;
        int currentPos = pos1;
        int iterations = 0;

        while (iterations <= CAPACITY) {
            if (table[currentPos] == null) {
                table[currentPos] = current;
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
        int pos1 = hash1(key);
        int pos2 = hash2(key);
        if (table[pos1] != null && table[pos1].getBucKey().equals(key))
            return table[pos1].getValue();
        else if (table[pos2] != null && table[pos2].getBucKey().equals(key))
            return table[pos2].getValue();
        return null;
    }

    public boolean remove(K key, V value) {
        int pos1 = hash1(key);
        int pos2 = hash2(key);
        if (table[pos1] != null && table[pos1].getValue().equals(value)) {
            table[pos1] = null;
            return true;
        }
        else if (table[pos2] != null && table[pos2].getValue().equals(value)) {
            table[pos2] = null;
            return true;
        }
        return false;
    }

    public String printTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (int i = 0; i < CAPACITY; ++i) {
            if (table[i] != null) {
                sb.append("<");
                sb.append(table[i].getBucKey());
                sb.append(", ");
                sb.append(table[i].getValue());
                sb.append("> ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private void rehash() {
        Bucket<K, V>[] oldTable = table;
        int oldCapacity = CAPACITY;
        CAPACITY = (CAPACITY * 2) + 1;
        table = new Bucket[CAPACITY];
        insertionCounter = 0;

        for (int i = 0; i < oldCapacity; i++) {
            if (oldTable[i] != null) {
                put(oldTable[i].getBucKey(), oldTable[i].getValue());
            }
        }
    }
}
