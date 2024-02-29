package Hash;

import HashBlueprint.iPrintable;
import HashBlueprint.iSet;

/*
 * A set class implemented with hashing. Note that there is no "value" here 
 * 
 * You are free to implement this however you want. Two potential ideas:
 * 
 * - Use a hashmap you have implemented with a dummy value class that does not take too much space
 * OR
 * - Re-implement the methods but tailor/optimize them for set operations
 * 
 * You are not allowed to use any existing java data structures
 * 
 */

public class HashSet<Key> implements iSet<Key>, iPrintable<Key>{

  // A default public constructor is mandatory!
  public HashSet() {
   /*
    * Add code here 
    */
    map = new HashMapDH<>();
  }
  
  /*
   * 
   * Add whatever you want!
   * 
   */

  public static class DummyValue {

    public String notice;

    public DummyValue() {
      this.notice = "dummy";
    }
  }

  private static final DummyValue value = new DummyValue();
  private HashMapDH<Key, DummyValue> map;

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean contains(Key k) {
    if (k == null) {
      return false;
    }
    DummyValue output = map.get(k);
    return output != null;
  }

  @Override
  public boolean put(Key k) {
    if (k == null) {
      return false;
    }
    DummyValue output = map.put(k, value);
    return output == null;
  }

  @Override
  public boolean remove(Key k) {
    if (k == null) {
      return false;
    }
    DummyValue output = map.remove(k);
    return output != null;
  }

  @Override
  public Iterable<Key> keySet() {
    return map.keySet();
  }

  @Override
  public Object get(Key key) {
    // Do not touch
    return null;
  }

}
