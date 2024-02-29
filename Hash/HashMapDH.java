package Hash;

import java.lang.reflect.Array;
import java.util.HashSet;

import HashBlueprint.AbstractHashMap;
import HashBlueprint.HashEntry;

/*
 * The file should contain the implementation of a hashmap with:
 * - Open addressing for collision handling
 * - Double hashing for probing. The double hash function should be of the form: q - (k mod q)
 * - Multiply-Add-Divide (MAD) for compression: (a*k+b) mod p
 * - Resizing (to double its size) and rehashing when the load factor gets above a threshold
 * 
 * Some helper functions are provided to you. We suggest that you go over them.
 * 
 * You are not allowed to use any existing java data structures other than for the keyset method
 */

public class HashMapDH<Key, Value> extends AbstractHashMap<Key, Value> {

  // The underlying array to hold hash entries (see the HashEntry class)
  private HashEntry<Key, Value>[] buckets;

  //Do not forget to call this when you need to increase the size!
  @SuppressWarnings("unchecked")
  protected void resizeBuckets(int newSize) {
    // Update the capacity
    N = nextPrime(newSize);
    buckets = (HashEntry<Key, Value>[]) Array.newInstance(HashEntry.class, N);
  }

  // The threshold of the load factor for resizing
  protected float criticalLoadFactor;

  // The prime number for the secondary hash
  int dhP;

  /*
   * ADD MORE FIELDS IF NEEDED
   * 
   */

  private HashSet<Key> keySet;

  /*
   * ADD A NESTED CLASS IF NEEDED
   * 
   */

  public static class Flag<Key, Value> extends HashEntry<Key, Value> {

    static String notice = "Item Removed";
    static int count = 0;
    int id;

    public Flag(Key k, Value v) {
      super(k, v);
      this.id = count;
      count++;
    }
  }

  // Default constructor
  public HashMapDH() {
    this(101);
  }

  public HashMapDH(int initSize) {
    this(initSize, 0.6f);
  }

  public HashMapDH(int initSize, float criticalAlpha) {
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

  /**
   * Calculates the hash value by compressing the given hashcode. Note that you
   * need to use the Multiple-Add-Divide method. The class variables "a" is the
   * scale, "b" is the shift, "mainP" is the prime which are calculated for you.
   * Do not include the size of the array here
   * 
   * Make sure to include the absolute value since there maybe integer overflow!
   */
  protected int primaryHash(int hashCode) {
    // TODO: Implement MAD compression given the hash code, should be 1 line
    return Math.abs(a * hashCode + b) % P;
  }

  /**
   * The secondary hash function. Remember you need to use "dhP" here!
   * 
   */
  protected int secondaryHash(int hashCode) {
    // TODO: Implement the secondary hash function taught in the class
    // return 1 + Math.abs(a * hashCode + b) % dhP;
    return Math.abs(dhP - (hashCode % dhP));
  }

  @Override
  public int hashValue(Key key, int iter) {
    int k = Math.abs(key.hashCode());
    return Math.abs(primaryHash(k) + iter * secondaryHash(k)) % N;
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
      // TODO check if something goes wrong
      HashEntry<Key, Value>[] copyBuckets = buckets.clone();
      N *= 2;
      n = 0;
      resizeBuckets(N);
      updateHashParams();
      for (HashEntry<Key, Value> entry : copyBuckets) {
        if (entry != null) {
          put(entry.getKey(), entry.getValue());
        }
      }
    }
  }

  @Override
  public Value get(Key k) {
    // TODO Auto-generated method stub
    if (k == null) {
      return null;
    }
    int iter = 0;
    int hash = hashValue(k, iter);
    HashEntry<Key, Value> startEntry = null;
    while (true) {
      // if we stumble upon an empty slot
      // the following iterations will not find the entry
      if (buckets[hash] == null) {
        return null;
      }
      // if we iterated through all the entries it is not in the table
      if (buckets[hash].equals(startEntry)) {
        return null;
      }
      if (!(buckets[hash] instanceof Flag) && buckets[hash].getKey().equals(k)) {
        return buckets[hash].getValue();
      }
      if (startEntry == null) {
        startEntry = buckets[hash];
      }
      hash = hashValue(k, ++iter);
    }
  }

  @Override
  public Value put(Key k, Value v) {
    // TODO check if something goes wrong
    if (k == null) {
      return null;
    }
    checkAndResize();
    int iter = 0;
    int hash = hashValue(k, iter);
    // there are three options:
    // 1. the cell is empty (null)
    // -> then assign it there
    // 2. it was removed (cast to Flag)
    // -> then assign it there
    // 3. it has the same key k
    // -> setValue as v, return oldVal
    while (buckets[hash] != null) {
      if (!(buckets[hash] instanceof Flag) && !(buckets[hash].getKey().equals(k))) {
        hash = hashValue(k, ++iter);
      } else {
        break;
      }
    }
    if ((buckets[hash] != null)) {
      if (!(buckets[hash] instanceof Flag) && (buckets[hash].getKey().equals(k))) {
        Value oldVal = buckets[hash].getValue();
        buckets[hash].setValue(v);
        return oldVal;
      }
    }
    buckets[hash] = new HashEntry<>(k, v);
    keySet.add(k);
    n += 1;
    return null;
  }

  @Override
  public Value remove(Key k) {
    // TODO not sure if right
    if (k == null) {
      return null;
    }
    int iter = 0;
    int hash = hashValue(k, iter);
    HashEntry<Key, Value> startEntry = null;
    while (true) {
      // if we stumble upon an empty slot
      // the following iterations will not find the entry
      if (buckets[hash] == null) {
        return null;
      }
      // if we iterated through all the entries it is not in the table
      if (buckets[hash].equals(startEntry)) {
        return null;
      }
      if (!(buckets[hash] instanceof Flag) && buckets[hash].getKey().equals(k)) {
        Value value = buckets[hash].getValue();
        buckets[hash] = new Flag<>(buckets[hash].getKey(), buckets[hash].getValue());
        keySet.remove(k);
        n -= 1;
        return value;
      }
      if (startEntry == null) {
        startEntry = buckets[hash];
      }
      hash = hashValue(k, ++iter);
    }
    /*
    int iter = 0;
    boolean didRemove = false;
    int index = hashValue(k, iter);
    while (keySet.contains(k) && !didRemove) {
      if (buckets[index] == null || !buckets[index].getKey().equals(k)) {
        iter += 1;
        index = hashValue(k, iter);
      } else {
        keySet.remove(k);
        didRemove = true;
      }
    }
    if (!didRemove) {
      return null;
    } else {
      buckets[index] = (Flag) buckets[index];
      n -= 1;
      return buckets[index].getValue();
    }
     */
  }

  // This is the only function you are allowed to use an existing Java data
  // structure!
  @Override
  public Iterable<Key> keySet() {
    return keySet;
  }

  @Override
  protected void updateHashParams() {
    super.updateHashParams();
    dhP = nextPrime(N / 2);
  }

}
