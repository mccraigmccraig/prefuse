package prefuse.util.force;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a simulation of physical forces acting on bodies. To create a
 * custom ForceSimulator, add the desired {@link Force} functions and choose an
 * appropriate {@link Integrator}.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class ForceSimulatorImpl implements ForceSimulator {

    private final List<ForceItem> items;
    private final List<Spring> springs;
    private Force[] iforces;
    private Force[] sforces;
    private int iflen, sflen;
    private Integrator integrator;
    private float speedLimit = 1.0f;

    /**
     * Create a new, empty ForceSimulator. A RungeKuttaIntegrator is used
     * by default.
     */
    public ForceSimulatorImpl() {
        this(new RungeKuttaIntegrator());
    }

    /**
     * Create a new, empty ForceSimulator.
     * @param integr the Integrator to use
     */
    public ForceSimulatorImpl(Integrator integr) {
        integrator = integr;
        iforces = new Force[5];
        sforces = new Force[5];
        iflen = 0;
        sflen = 0;
        items = new ArrayList<ForceItem>();
        springs = new ArrayList<Spring>();
    }

    /* (non-Javadoc)
	 * @see prefuse.util.force.ForceSimulator#getSpeedLimit()
	 */
    public float getSpeedLimit() {
        return speedLimit;
    }

    /* (non-Javadoc)
	 * @see prefuse.util.force.ForceSimulator#setSpeedLimit(float)
	 */
    public void setSpeedLimit(float limit) {
        speedLimit = limit;
    }

    /* (non-Javadoc)
	 * @see prefuse.util.force.ForceSimulator#getIntegrator()
	 */
    public Integrator getIntegrator() {
        return integrator;
    }

    /* (non-Javadoc)
	 * @see prefuse.util.force.ForceSimulator#setIntegrator(prefuse.util.force.Integrator)
	 */
    public void setIntegrator(Integrator intgr) {
        integrator = intgr;
    }

    /* (non-Javadoc)
	 * @see prefuse.util.force.ForceSimulator#clear()
	 */
    public void clear() {
        items.clear();
        Spring.SpringFactory f = Spring.getFactory();
        for ( Spring s : springs ) {
			f.reclaim(s);
		}
        springs.clear();
    }

    /* (non-Javadoc)
	 * @see prefuse.util.force.ForceSimulator#addForce(prefuse.util.force.Force)
	 */
    public void addForce(Force f) {
        if ( f.isItemForce() ) {
            if ( iforces.length == iflen ) {
                // resize necessary
                Force[] newf = new Force[iflen+10];
                System.arraycopy(iforces, 0, newf, 0, iforces.length);
                iforces = newf;
            }
            iforces[iflen++] = f;
        }
        if ( f.isSpringForce() ) {
            if ( sforces.length == sflen ) {
                // resize necessary
                Force[] newf = new Force[sflen+10];
                System.arraycopy(sforces, 0, newf, 0, sforces.length);
                sforces = newf;
            }
            sforces[sflen++] = f;
        }
    }

    /* (non-Javadoc)
	 * @see prefuse.util.force.ForceSimulator#getForces()
	 */
    public Force[] getForces() {
        Force[] rv = new Force[iflen+sflen];
        System.arraycopy(iforces, 0, rv, 0, iflen);
        System.arraycopy(sforces, 0, rv, iflen, sflen);
        return rv;
    }

    /* (non-Javadoc)
	 * @see prefuse.util.force.ForceSimulator#addItem(prefuse.util.force.ForceItem)
	 */
    public void addItem(ForceItem item) {
        items.add(item);
    }

    /* (non-Javadoc)
	 * @see prefuse.util.force.ForceSimulator#removeItem(prefuse.util.force.ForceItem)
	 */
    public boolean removeItem(ForceItem item) {
        return items.remove(item);
    }

    /* (non-Javadoc)
	 * @see prefuse.util.force.ForceSimulator#getItems()
	 */
    public List<ForceItem> getItems() {
        return items;
    }

    /* (non-Javadoc)
	 * @see prefuse.util.force.ForceSimulator#addSpring(prefuse.util.force.ForceItem, prefuse.util.force.ForceItem)
	 */
    public Spring addSpring(ForceItem item1, ForceItem item2) {
        return addSpring(item1, item2, -1.f, -1.f);
    }

    /* (non-Javadoc)
	 * @see prefuse.util.force.ForceSimulator#addSpring(prefuse.util.force.ForceItem, prefuse.util.force.ForceItem, float)
	 */
    public Spring addSpring(ForceItem item1, ForceItem item2, float length) {
        return addSpring(item1, item2, -1.f, length);
    }

    /* (non-Javadoc)
	 * @see prefuse.util.force.ForceSimulator#addSpring(prefuse.util.force.ForceItem, prefuse.util.force.ForceItem, float, float)
	 */
    public Spring addSpring(ForceItem item1, ForceItem item2, float coeff, float length) {
        if ( item1 == null || item2 == null ) {
			throw new IllegalArgumentException("ForceItems must be non-null");
		}
        Spring s = Spring.getFactory().getSpring(item1, item2, coeff, length);
        springs.add(s);
        return s;
    }

    /* (non-Javadoc)
	 * @see prefuse.util.force.ForceSimulator#getSprings()
	 */
    public List<Spring> getSprings() {
        return springs;
    }

    /* (non-Javadoc)
	 * @see prefuse.util.force.ForceSimulator#runSimulator(long)
	 */
    public void runSimulator(long timestep) {
        accumulate();
        integrator.integrate(this, timestep);
    }

    /* (non-Javadoc)
	 * @see prefuse.util.force.ForceSimulator#accumulate()
	 */
    public void accumulate() {
        for ( int i = 0; i < iflen; i++ ) {
			iforces[i].init(this);
		}
        for ( int i = 0; i < sflen; i++ ) {
			sforces[i].init(this);
		}
        for ( ForceItem item : items) {
            item.force[0] = 0.0f; item.force[1] = 0.0f;
            for ( int i = 0; i < iflen; i++ ) {
				iforces[i].getForce(item);
			}
        }
        for ( Spring s : springs ) {
            for ( int i = 0; i < sflen; i++ ) {
                sforces[i].getForce(s);
            }
        }
    }

} // end of class ForceSimulator
