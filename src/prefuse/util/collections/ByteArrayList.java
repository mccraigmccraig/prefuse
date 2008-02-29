package prefuse.util.collections;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * A minor extension to ByteArrayOutputStream which prevents unnecessary copying
 * of data when creating an InputStream.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class ByteArrayList extends ByteArrayOutputStream {

    /**
     * Creates a new ByteArrayList. The buffer capacity is
     * initially 32 bytes, though its size increases if necessary.
     */
	public ByteArrayList() {
	}

    /**
     * Creates a new ByteArrayList, with a buffer capacity of
     * the specified size, in bytes.
     *
     * @param   size   the initial size.
     * @exception  IllegalArgumentException if size is negative.
     */
	public ByteArrayList(int size) {
		super(size);
	}

	/**
	 * @return an InputStream which contains the underlying buffer's data.
	 */
    public InputStream getAsInputStream() {
        return new ByteArrayInputStream(buf, 0, count);
    }

}
