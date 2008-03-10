/**
 * 
 */
package prefuse.util;


/**
 * TODO: add some documentation
 * 
 * @author Anton Marsden
 */
public class ObjectPair<O>
{
    private final O a;
    private final O b;

    public ObjectPair( O a, O b )
    {
        this.a = a;
        this.b = b;
    }
    
    public O getA() {
    	return a;
    }

    public O getB() {
    	return b;
    }
    

    public boolean equals( Object other )
    {
        if ( this == other )
        {
            return true;
        }
        if ( !( other instanceof ObjectPair ) )
        {
            return false;
        }
        ObjectPair o = (ObjectPair) other;
        return a.equals(o.a) && b.equals(o.b) || a.equals(o.b) && b.equals(o.a);
    }

    public int hashCode()
    {
        return a.hashCode() ^ b.hashCode();
    }
}