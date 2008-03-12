package prefuse.data.util;

import java.util.AbstractSequentialList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import prefuse.data.Tuple;
import prefuse.data.expression.Predicate;

/**
 * Filters a List based on a Predicate.
 *
 * @author Anton Marsden
 *
 * @param <T>
 */
public class FilteredList <T extends Tuple<?>> extends AbstractSequentialList<T> {

	private final Predicate predicate;
	private final List<T> l;

	public FilteredList(List<T> l, Predicate predicate) {
		this.predicate = predicate;
		this.l = l;
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return new FilteredListIterator(index);
	}

	public boolean isEmpty() {
		return !iterator().hasNext();
	}

	@Override
	public int size() {
		int s = 0;
		for(T t : l) {
			if(predicate.getBoolean(t)) {
				s++;
			}
		}
		return s;
	}

	/**
	 * TODO: improve the performance of this class (hasNext() => next() and
	 * hasPrevious() => previous() should be faster)
	 *
	 * @author Anton Marsden
	 */
	private class FilteredListIterator implements ListIterator<T> {

		private int filteredNextIdx;
		private int internalNextIdx;

		public FilteredListIterator(int index) {
			filteredNextIdx = 0;
			internalNextIdx = 0;
			// skip along until we reach the right index
			for(int i = 0; i < index; i++) {
				next();
			}
		}

		public void add(T e) {
			throw new UnsupportedOperationException();
		}

		public boolean hasNext() {
			int lSize = l.size();
			int internalNextCopy = internalNextIdx;
			T t;
			do {
				if(internalNextCopy >= lSize) {
					return false;
				}
				t = l.get(internalNextCopy++);
			} while(!predicate.getBoolean(t));
			return true;
		}

		public boolean hasPrevious() {
			return filteredNextIdx > 0;
		}

		public T next() {
			int lSize = l.size();
			T t;
			do {
				if(internalNextIdx >= lSize) {
					throw new NoSuchElementException();
				}
				t = l.get(internalNextIdx++);
			} while(!predicate.getBoolean(t));
			filteredNextIdx++;
			return t;
		}

		public int nextIndex() {
			return filteredNextIdx;
		}

		public T previous() {
			T t;
			do {
				if(--internalNextIdx < 0) {
					throw new NoSuchElementException();
				}
				t = l.get(internalNextIdx);
			} while (!predicate.getBoolean(t));
			filteredNextIdx--;
			return t;
		}

		public int previousIndex() {
			return filteredNextIdx - 1;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public void set(T e) {
			throw new UnsupportedOperationException();
		}

	}

}
