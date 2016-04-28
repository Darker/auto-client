/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.league_of_legends.maps;

import cz.autoclient.league_of_legends.DataLoader;
import cz.autoclient.league_of_legends.GameObject;
import cz.autoclient.league_of_legends.LoLVersion;
import java.beans.Expression;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Jakub
 * @param <T> GameObject is any object loaded from 
 */
public abstract class GameObjectMap<T extends GameObject>    
                          extends DataLoader
                          implements Map<String, T>, Iterable<T> {
  private HashMap<String, T> data;
  public final Class<T> type;
  
  public String version = null;
  
  private volatile HashMap<Integer, List> cachedValueLists;
  private final Object cachedValueLists_mutex = new Object();
  
  

  public GameObjectMap(Class<T> type, LoLVersion v, boolean download_if_missing) {
    super(v, v.getRoot(), download_if_missing);
    this.type = type;
  }
  
  
  
  public Map<String, T> getData() {
    if(data==null) {
      loadData();
    }
    return data;
  }
  public synchronized void loadData() {
    if(data!=null)
      return;
    data = new HashMap();
    JSONObject jsonData = getJSONData();
    try {
      jsonData = jsonData.getJSONObject("data");
    }
    catch(JSONException e) {
      return; 
    }
    
    if(jsonData!=null) {
      try {
        Iterator<String> keys = jsonData.keys();
        String key;
        JSONObject json_entry;
        while( keys.hasNext() && (key = keys.next())!=null ) {
          if ((json_entry = jsonData.getJSONObject(key)) != null) {
            try {
              T result = (T)new Expression(type, "new", new Object[]{this, key, json_entry}).getValue();
              data.put(key, result);
            }
            catch(Exception e) {
              //must not happen
              e.printStackTrace();
              throw new RuntimeException("Failed to instantiate game object of type "+type.getName());
              
              //throw new NoSuchMethodException("Constructor for "+handler.getName()+" failed with exception:"+e);
            }
          }
        }
      } catch (JSONException ex) {
        return;
      }
    }
  }
  @Override
  public String getVersion() {
    if(version==null) {
      try {
        version=getJSONData().getString("version");
      }
      catch(JSONException e) {
        System.out.println(e);
        return "error"; 
      }
    }
    return version;
  }
  /**
   * Getter for any values within the GameObject and it's subclasses. 
   * Used as callback.
   * 
   * Typical implementation would look like:
   *   new ValueGetter<SummonerSpell, String> {
   *     public String getValue(SummonerSpell source) {
   *       return source.toString();
   *     }
   *   }
   * @param <T> 
   * @param <V> Type of value retrieved
   */
  public static interface ValueGetter<T extends GameObject, V> {
    public V getValue(T source);
    public default boolean valEquals(V value, T source) {
      V val2 = getValue(source);
      if(value==null) {
        return val2==null; 
      }
      return value.equals(val2);
    }
  }
  
  /**
   * Will call the given value getter for all elements and return array of values.
   * @param <V>
   * @param reader
   * @return 
   */ 
  public <V> List<V> enumValues(ValueGetter<T, V> reader) {
    if(cachedValueLists != null) {
      synchronized(cachedValueLists_mutex) {
        int code = reader.getClass().hashCode();
        if(cachedValueLists.containsKey(code))
          return cachedValueLists.get(code);
      }
    }
    //System.out.println("Enumerating values using "+reader.getClass().getName());
    List<V> vals = new ArrayList();
    
    for(T o : this) {
      vals.add(reader.getValue(o));
    }
    return vals;
  }
  /**
   * Will call the given value getter for all elements and return array of values.
   * @param <V>
   * @param reader
   * @param cached true if you want to cache the resulting array
   * @return 
   */
  public <V> List<V> enumValues(ValueGetter<T, V> reader, boolean cached) {
    List<V> vals = enumValues(reader);
    if(cached) {
      synchronized(cachedValueLists_mutex) {
        if(cachedValueLists == null)
          cachedValueLists = new HashMap();
        int code = reader.getClass().hashCode();
        if(!cachedValueLists.containsKey(code))
          cachedValueLists.put(reader.getClass().hashCode(), vals);
      }
    }
    return vals;
  }
  
  /**
   * Will try this getter for every value untill one equals the second parameter
   * @param <V>
   * @param reader
   * @param desired value we are trying to find
   * @return 
   */
  public <V> T find(ValueGetter<T, V> reader, V desired) {
    for(T o : this) {
      if(reader.valEquals(desired, o))
        return o;
    }
    return null;
  }
  
  @Override
  public void unloadData() {
    for(GameObject o : this) {
      o.unloadData();
    }
    super.unloadData();
  }
  
  public JSONObject getJSONObject(String key) throws JSONException {
    return getJSONData().getJSONObject(key);
  }
  
  @Override
  public Iterator<T> iterator() {
    return ((HashMap<String, T>)getData()).values().iterator();
  }
  
  public Iterator<String> keys() {
    return ((HashMap<String, T>)getData()).keySet().iterator();
  }
  
  @Override
  public int size() {
    return getData().size();
  }

  @Override
  public boolean isEmpty() {
    return getData().isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return getData().containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return getData().containsValue(value);
  }

  @Override
  public T get(Object key) {
    return getData().get(key);
  }

  @Override
  public T put(String key, T value) {
    return getData().put(key, value);
  }

  @Override
  public T remove(Object key) {
    return getData().remove(key);
  }

  @Override
  public void putAll(Map<? extends String, ? extends T> m) {
    getData().putAll(m);
  }

  @Override
  public void clear() {
    getData().clear();
  }

  @Override
  public Set<String> keySet() {
    return getData().keySet();
  }

  @Override
  public Collection<T> values() {
    return getData().values();
  }

  @Override
  public Set<Entry<String, T>> entrySet() {
    return getData().entrySet();
  }

  @Override
  public boolean equals(Object o) {
    return getData().equals(o);
  }

  @Override
  public int hashCode() {
    return getData().hashCode();
  }

  @Override
  public T getOrDefault(Object key, T defaultValue) {
    return getData().getOrDefault(key, defaultValue);
  }

  @Override
  public void forEach(BiConsumer<? super String, ? super T> action) {
    getData().forEach(action);
  }

  @Override
  public void replaceAll(BiFunction<? super String, ? super T, ? extends T> function) {
    getData().replaceAll(function);
  }

  @Override
  public T putIfAbsent(String key, T value) {
    return getData().putIfAbsent(key, value);
  }

  @Override
  public boolean remove(Object key, Object value) {
    return getData().remove(key, value);
  }

  @Override
  public boolean replace(String key, T oldValue, T newValue) {
    return getData().replace(key, oldValue, newValue);
  }

  @Override
  public T replace(String key, T value) {
    return getData().replace(key, value);
  }

  @Override
  public T computeIfAbsent(String key, Function<? super String, ? extends T> mappingFunction) {
    return getData().computeIfAbsent(key, mappingFunction);
  }

  @Override
  public T computeIfPresent(String key, BiFunction<? super String, ? super T, ? extends T> remappingFunction) {
    return getData().computeIfPresent(key, remappingFunction);
  }

  @Override
  public T compute(String key, BiFunction<? super String, ? super T, ? extends T> remappingFunction) {
    return getData().compute(key, remappingFunction);
  }

  @Override
  public T merge(String key, T value, BiFunction<? super T, ? super T, ? extends T> remappingFunction) {
    return getData().merge(key, value, remappingFunction);
  }
}
