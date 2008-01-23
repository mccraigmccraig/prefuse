package prefuse.util.collections;

import java.util.AbstractList;
import java.util.List;

/**
 * IntIterator implementation that combines the results of multiple
 * int iterators.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class CompositeList <E> extends AbstractList<E> {

    private final List<E>[] iters;
    private final int len;

    public CompositeList(List<E> ... iters) {
        this.iters = iters;
        int tmp = 0;
        for (List<E> element : iters) {
        	tmp += element.size();
        }
        this.len = tmp;
    }

	@Override
	public E get(int index) {
		if(index >= len) {
			throw new IndexOutOfBoundsException();
		}
		int i = 0;
		int pos = 0;
		while(index - pos >= iters[i].size()) {
			pos += iters[i++].size();
		}
		return iters[i].get(index - pos);
	}

	@Override
	public int size() {
		return len;
	}

	public int indexOf(Object o) {
		int base = 0;
		for (List<E> element : iters) {
			int idx = element.indexOf(o);
			if(idx >= 0) {
				return base + idx;
			}
			base += element.size();
		}
		return -1;
	}

}
