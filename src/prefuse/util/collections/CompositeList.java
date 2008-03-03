package prefuse.util.collections;

import java.util.AbstractList;
import java.util.List;

/**
 * List implementation that combines multiple Lists (which are assumed to be immutable).
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class CompositeList <E> extends AbstractList<E> {

    private final List<E>[] lists;
    private final int size;

    public CompositeList(List<E> ... lists) {
        this.lists = lists;
        int tmp = 0;
        for (List<E> element : lists) {
        	tmp += element.size();
        }
        this.size = tmp;
    }

	@Override
	public E get(int index) {
		if(index >= size) {
			throw new IndexOutOfBoundsException();
		}
		int i = 0;
		int pos = 0;
		while(index - pos >= lists[i].size()) {
			pos += lists[i++].size();
		}
		return lists[i].get(index - pos);
	}

	@Override
	public int size() {
		return size;
	}

	public int indexOf(Object o) {
		int base = 0;
		for (List<E> element : lists) {
			int idx = element.indexOf(o);
			if(idx >= 0) {
				return base + idx;
			}
			base += element.size();
		}
		return -1;
	}

}
