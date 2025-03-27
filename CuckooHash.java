/******************************************************************
 *
 * Zaki Khan / 272 001
 *
 * Note, additional comments provided throughout this source code
 * are for educational purposes
 *
 ********************************************************************/
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.lang.Math;

/**
 * Cuckoo Hashing Exercise
 *
 * Cuckoo hashing is a scheme for resolving hash collisions of keys in 
 * a hashmap that maintains a worst-case constant lookup time, O(1). 
 * The name derives from the behavior of some species of cuckoo, where 
 * the cuckoo chick pushes the other eggs or young out of the nest when 
 * it hatches in a variation of the behavior referred to as brood 
 * parasitism; analogously, inserting a new key into a cuckoo hashing 
 * table may push an older key to a different location in the table.
 *
 * Constructor:
 * CuckooHash( size ) - Where size is the initial bucket size 
 * of the hashmap
 *
 * Public Methods:
 * int size() - The number of elements, <key,value> pairs,
 * in the hashmap
 * void clear() - Empty the hashmap.
 * List<V> values() - Return a List of all values of type 'V' in 
 * the hashmap.
 * Set<K> keys() - Return a Set of all keys of type 'K" in
 * the hashmap.
 * void put(K,V) - Insert the <key,value> pair of types K and V.
 * V get(K) - Return the value of type V for the key
 * provided of type K.
 * boolean remove(K, V) - Remove <key, value> pair, return true 
 * if found and removed, else false.
 * String printTable() - Return a String representing a
 * concatenation of all <key,value> pairs.
 */
@SuppressWarnings("unchecked")
public class CuckooHash<K, V> {

    private int CAPACITY; // Hashmap capacity
    private Bucket<K, V>[] table; // Hashmap table
    private int a = 37, b = 17; // Constants used in h2(key)

    /**
     * Class Bucket
     *
     * Inner bucket class which represents a <key,value> pair 
     * within the hash map.
     *
     * @param <K> - type of key
     * @param <V> - type of value
     */
    private class Bucket<K, V> {
        private K bucKey = null;
        private V value = null;

        public Bucket(K k, V v) {
            bucKey = k; 
            value = v;
        }

        public K getBucKey() {
            return bucKey;
        }

        public V getValue() {
            return value;
        }
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
    }

    public List<V> values() {
        List<V> allValues = new ArrayList<>(); 
        for (int i = 0; i < CAPACITY; ++i) {
            if (table[i] != null) {
                allValues.add(table[i].getValue());
            }
        }
        return allValues;
    }

    public Set<K> keys() {
        Set<K> allKeys = new HashSet<>();
        for (int i = 0; i < CAPACITY; ++i) {
            if (table[i] != null) {
                allKeys.add(table[i].getBucKey());
            }
        }
        return allKeys;
    }

    public void put(K key, V value) {
        Bucket<K, V> newBucket = new Bucket<>(key, value);
        int pos1 = hash1(key);
        int pos2 = hash2(key);

        for (int i = 0; i < CAPACITY; i++) {
            if (table[pos1] == null) {
                table[pos1] = newBucket;
                return;
            } else if (table[pos1].getBucKey().equals(key) && table[pos1].getValue().equals(value)) {
                return;
            } else {
                Bucket<K, V> temp = table[pos1];
                table[pos1] = newBucket;
                newBucket = temp;
                pos1 = hash2(newBucket.getBucKey());
            }
        }
        rehash();
        put(key, value);
    }

    private void rehash() {
        CAPACITY *= 2;
        Bucket<K, V>[] oldTable = table;
        table = new Bucket[CAPACITY];

        for (Bucket<K, V> bucket : oldTable) {
            if (bucket != null) {
                put(bucket.getBucKey(), bucket.getValue());
            }
        }
    }

    public V get(K key) {
        int pos1 = hash1(key);
        int pos2 = hash2(key);

        if (table[pos1] != null && table[pos1].getBucKey().equals(key)) {
            return table[pos1].getValue();
        }
        if (table[pos2] != null && table[pos2].getBucKey().equals(key)) {
            return table[pos2].getValue();
        }
        return null;
    }

    public boolean remove(K key, V value) {
        int pos1 = hash1(key);
        int pos2 = hash2(key);

        if (table[pos1] != null && table[pos1].getBucKey().equals(key) && table[pos1].getValue().equals(value)) {
            table[pos1] = null;
            return true;
        }
        if (table[pos2] != null && table[pos2].getBucKey().equals(key) && table[pos2].getValue().equals(value)) {
            table[pos2] = null;
            return true;
        }
        return false;
    }

    public String printTable() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CAPACITY; ++i) {
            if (table[i] != null) {
                sb.append("[").append(table[i].getBucKey()).append(", ").append(table[i].getValue()).append("] ");
            } else {
                sb.append("[null] ");
            }
        }
        return sb.toString();
    }
}
