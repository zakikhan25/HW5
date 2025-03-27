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
    private int size = 0;
    private List<Map.Entry<K, V>> insertionOrder = new ArrayList<>();

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
        return size;
    }

    public void clear() {
        table = new Bucket[CAPACITY];
        insertionOrder.clear();
        size = 0;
    }

    public int mapSize() { return CAPACITY; }

    public List<V> values() {
        List<V> values = new ArrayList<>();
        for (Map.Entry<K, V> entry : insertionOrder) {
            values.add(entry.getValue());
        }
        return values;
    }

    public Set<K> keys() {
        Set<K> keys = new HashSet<>();
        for (Map.Entry<K, V> entry : insertionOrder) {
            keys.add(entry.getKey());
        }
        return keys;
    }

    public void put(K key, V value) {
        // Check if this exact key-value pair already exists
        for (Map.Entry<K, V> entry : insertionOrder) {
            if (entry.getKey().equals(key) && entry.getValue().equals(value)) {
                return;
            }
        }

        Bucket<K, V> newBucket = new Bucket<>(key, value);
        int pos1 = hash1(key);
        int pos2 = hash2(key);

        // Try to place in first position
        if (table[pos1] == null) {
            table[pos1] = newBucket;
            insertionOrder.add(new AbstractMap.SimpleEntry<>(key, value));
            size++;
            return;
        }

        // Try to place in second position
        if (table[pos2] == null) {
            table[pos2] = newBucket;
            insertionOrder.add(new AbstractMap.SimpleEntry<>(key, value));
            size++;
            return;
        }

        // Need to evict someone - start with first position
        Bucket<K, V> current = newBucket;
        int currentPos = pos1;
        int iterations = 0;

        while (iterations <= CAPACITY) {
            if (table[currentPos] == null) {
                table[currentPos] = current;
                insertionOrder.add(new AbstractMap.SimpleEntry<>(current.getBucKey(), current.getValue()));
                size++;
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
        
        if (table[pos1] != null && table[pos1].getBucKey().equals(key) && table[pos1].getValue().equals(value)) {
            table[pos1] = null;
            removeFromInsertionOrder(key, value);
            size--;
            return true;
        }
        else if (table[pos2] != null && table[pos2].getBucKey().equals(key) && table[pos2].getValue().equals(value)) {
            table[pos2] = null;
            removeFromInsertionOrder(key, value);
            size--;
            return true;
        }
        return false;
    }

    private void removeFromInsertionOrder(K key, V value) {
        for (Iterator<Map.Entry<K, V>> it = insertionOrder.iterator(); it.hasNext();) {
            Map.Entry<K, V> entry = it.next();
            if (entry.getKey().equals(key) && entry.getValue().equals(value)) {
                it.remove();
                break;
            }
        }
    }

    public String printTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (Map.Entry<K, V> entry : insertionOrder) {
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
        List<Map.Entry<K, V>> oldEntries = new ArrayList<>(insertionOrder);
        CAPACITY = (CAPACITY * 2) + 1;
        table = new Bucket[CAPACITY];
        insertionOrder.clear();
        size = 0;
        
        for (Map.Entry<K, V> entry : oldEntries) {
            put(entry.getKey(), entry.getValue());
        }
    }
}
