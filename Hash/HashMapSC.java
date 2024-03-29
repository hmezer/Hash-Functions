package Hash;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.LinkedList;

import HashBlueprint.AbstractHashMap;
import HashBlueprint.HashEntry;

/*
 * The file should contain the implementation of a hashmap with:
 * - Separate Chaining for collision handling
 * - Multiply-Add-Divide (MAD) for compression: (a*k+b) mod p
 * - Java's own linked lists for the secondary containers
 * - Resizing (to double its size) and rehashing when the load factor gets above a threshold
 *   Note that for this type of hashmap, load factor can be higher than 1
 * 
 * Some helper functions are provided to you. We suggest that you go over them.
 * 
 * You are not allowed to use any existing java data structures other than for the buckets (which have been 
 * created for you) and the keyset method
 */

public class HashMapSC<Key, Value> extends AbstractHashMap<Key, Value> {

  // The underlying array to hold hash entry Lists
  private LinkedList<HashEntry<Key, Value>>[] buckets;

  // Note that the Linkedlists are still not initialized!
  @SuppressWarnings("unchecked")
  protected void resizeBuckets(int newSize) {
    // Update the capacity
    N = nextPrime(newSize);
    buckets = (LinkedList<HashEntry<Key, Value>>[]) Array.newInstance(LinkedList.class, N);
  }

  /*
   * ADD MORE FIELDS IF NEEDED
   * 
   */
  private HashSet<Key> keySet;

  // The threshold of the load factor for resizing
  protected float criticalLoadFactor;

  /*
   * ADD A NESTED CLASS IF NEEDED
   * 
   */

  public int hashValue(Key key, int iter) {
    return hashValue(key);
  }

  public int hashValue(Key key) {
    // TODO: check later if right
    // Implement the hashvalue computation with the MAD method. Will be almost
    // the same as the primaryHash method of HashMapDH
    int k = Math.abs(key.hashCode());
    return Math.abs(((a * k + b) % P)) % N;
  }

  // Default constructor
  public HashMapSC() {
    this(101);
  }

  public HashMapSC(int initSize) {
    // High criticalAlpha for representing average items in a secondary container
    this(initSize, 10f);
  }

  public HashMapSC(int initSize, float criticalAlpha) {
    N = initSize;
    criticalLoadFactor = criticalAlpha;
    resizeBuckets(N);
    
    // Set up the MAD compression and secondary hash parameters
    updateHashParams();

    /*
     * ADD MORE CODE IF NEEDED
     * 
     */
    keySet = new HashSet<>();
    n = 0;
  }

  /*
   * ADD MORE METHODS IF NEEDED
   * 
   */

  @Override
  public Value get(Key k) {
    // TODO check if wrong
    if (k == null) {
      return null;
    }
    int hash = hashValue(k);
    if (buckets[hash] != null) {
      for (HashEntry<Key, Value> entry : buckets[hash]) {
        if (entry.getKey().equals(k)) {
          return entry.getValue();
        }
      }
    }
    return null;
  }

  @Override
  public Value put(Key k, Value v) {
    // TODO check if wrong
    if (k == null) {
      return null;
    }
    checkAndResize();
    //
    int hash = hashValue(k);
    if (buckets[hash] == null) {
      buckets[hash] = new LinkedList<>();
    }
    for (HashEntry<Key, Value> entry : buckets[hash]) {
      if (entry.getKey().equals(k)) {
        Value oldVal = entry.getValue();
        entry.setValue(v);
        return oldVal;
      }
    }
    buckets[hash].add(new HashEntry<>(k, v));
    keySet.add(k);
    n += 1;
    return null;
  }

  @Override
  public Value remove(Key k) {
    // TODO check if wrong
    if (k == null) {
      return null;
    }
    int hash = hashValue(k);
    if (buckets[hash] != null) {
      for (HashEntry<Key, Value> entry : buckets[hash]) {
        if (entry.getKey().equals(k)) {
          Value val = entry.getValue();
          buckets[hash].remove(entry);
          keySet.remove(k);
          n -= 1;
          return val;
        }
      }
    }
    return null;
  }

  @Override
  public Iterable<Key> keySet() {
    return keySet;
  }

  /**
   * checkAndResize checks whether the current load factor is greater than the
   * specified critical load factor. If it is, the table size should be increased
   * to 2*N and recreate the hash table for the keys (rehashing). Do not forget to
   * re-calculate the hash parameters and do not forget to re-populate the new
   * array!
   */
  protected void checkAndResize() {
    if (loadFactor() > criticalLoadFactor) {
      // TODO check if right later if something goes wrong
      LinkedList<HashEntry<Key, Value>>[] bucketsCopy = buckets.clone();
      N *= 2;
      n = 0;
      resizeBuckets(N);
      updateHashParams();
      for (LinkedList<HashEntry<Key, Value>> chain : bucketsCopy) {
        if (chain != null) {
          for (HashEntry<Key, Value> entry : chain) {
            put(entry.getKey(), entry.getValue());
          }
        }
      }
    }
  }
}
