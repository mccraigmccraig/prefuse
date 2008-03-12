package prefuse.util.force;

import java.util.List;

/**
 *
 * @author Anton Marsden
 */
public interface ForceSimulator {

	/**
	 * Get the speed limit, or maximum velocity value allowed by this
	 * simulator.
	 * @return the "speed limit" maximum velocity value
	 */
	float getSpeedLimit();

	/**
	 * Clear this simulator, removing all ForceItem and Spring instances
	 * for the simulator.
	 */
	void clear();

	/**
	 * Add a new Force function to the simulator.
	 * @param f the Force function to add
	 */
	void addForce(Force f);

	/**
	 * Get an array of all the Force functions used in this simulator.
	 * @return an array of Force functions
	 */
	Force[] getForces();

	/**
	 * Add a ForceItem to the simulation.
	 * @param item the ForceItem to add
	 */
	void addItem(ForceItem item);

	/**
	 * Remove a ForceItem to the simulation.
	 * @param item the ForceItem to remove
	 */
	boolean removeItem(ForceItem item);

	/**
	 * Get an iterator over all registered ForceItems.
	 * @return an iterator over the ForceItems.
	 */
	List<ForceItem> getItems();

	/**
	 * Add a Spring to the simulation.
	 * @param item1 the first endpoint of the spring
	 * @param item2 the second endpoint of the spring
	 * @return the Spring added to the simulation
	 */
	Spring addSpring(ForceItem item1, ForceItem item2);

	/**
	 * Add a Spring to the simulation.
	 * @param item1 the first endpoint of the spring
	 * @param item2 the second endpoint of the spring
	 * @param length the spring length
	 * @return the Spring added to the simulation
	 */
	Spring addSpring(ForceItem item1, ForceItem item2, float length);

	/**
	 * Add a Spring to the simulation.
	 * @param item1 the first endpoint of the spring
	 * @param item2 the second endpoint of the spring
	 * @param coeff the spring coefficient
	 * @param length the spring length
	 * @return the Spring added to the simulation
	 */
	Spring addSpring(ForceItem item1, ForceItem item2, float coeff, float length);

	/**
	 * Get an iterator over all registered Springs.
	 * @return an iterator over the Springs.
	 */
	List<Spring> getSprings();

	/**
	 * Run the simulator for one timestep.
	 * @param timestep the span of the timestep for which to run the simulator
	 */
	void runSimulator(long timestep);

	/**
	 * Accumulate all forces acting on the items in this simulation
	 */
	void accumulate();

}