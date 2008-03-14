package prefuse.util.collections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Maps keys to multiple values.
 * 
 * @param <K>
 * @param <V>
 * 
 * @author Anton Marsden
 */
public class MultiMap<K, V> {

	private final Map<K, Set<V>> m;

	public MultiMap() {
		this.m = new HashMap<K, Set<V>>();
	}

	public boolean containsValue(Object key, Object value) {
		Set<V> values = m.get(key);
		return values != null && values.contains(value);
	}

	public Set<V> get(Object key) {
		return m.get(key);
	}

	public V put(K key, V value) {
		Set<V> values = m.get(key);
		if(values == null) {
			values = new HashSet<V>();
			m.put(key,values);
		}
		return values.add(value) ? value : null;
	}

	public Set<V> remove(Object key) {
		return m.remove(key);
	}

	@SuppressWarnings("unchecked")
	public V remove(Object key, Object item) {
		Set<V> values = m.get(key);
		if(values != null) {
			if(values.remove(item)) {
				if(values.isEmpty()) {
					m.remove(key);
					return (V) item;
				}
			}
		}
		return null;
	}

	/**
	 * @return the number of keys in the multi-map
	 */
	public int size() {
		return m.size();
	}

	public Set<K> keySet() {
		return m.keySet();
	}

	public Set<Map.Entry<K, Set<V>>> entrySet() {
		return m.entrySet();
	}
	
	public boolean isEmpty() {
		return m.isEmpty();
	}
	
	public void clear() {
		m.clear();
	}

}
