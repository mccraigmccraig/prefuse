package edu.berkeley.guir.prefuse.pipeline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.berkeley.guir.prefuse.Pipeline;

/**
 * Pipeline manager that oversees animated transitions. This manager 
 * distinguishes between ProcessingComponents, which determine a graph's
 * next configuration, and AnimationComponents, which animate changes between
 * these configurations.
 * 
 * Apr 28, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class StagedAnimationManager extends DefaultPipelineManager {
	
	public static final int TYPE_PROCESSING = 0;
	public static final int TYPE_ANIMATION  = 1;
	
	public static final String ATTR_FRAME_RATE = "frameRate";
	public static final String ATTR_ANIM_TIME = "animationTime";
	public static final String ATTR_ANIM_FRAC = "animationFrac";
	public static final String ATTR_ANIM_CONTINUE = "animationContinue";

	private AnimationRunner animationRunner;
	private List            m_stages = new ArrayList();

	private class StageEntry {
		StageEntry(int animTime) {
			processingList = new LinkedList();
			animationList = new LinkedList();
			animationTime = animTime;
		}
		List processingList;
		List animationList;
		int  animationTime;
	} //

	public StagedAnimationManager() {
		m_stages = new LinkedList();
		addStage(-1);
	} //

	public void setAnimationTime(int stage, int time) {
		((StageEntry)m_stages.get(stage)).animationTime = time;
	} //

	public void setAnimationTime(int time) {
		this.setAnimationTime(0, time);
	} //

	public void addStage(int animationTime) {
		StageEntry entry = new StageEntry(animationTime);
		m_stages.add(entry);
	} //

	public void addComponent(PipelineComponent c, int type, int stage) {
		if ( type < TYPE_PROCESSING || type > TYPE_ANIMATION )
			throw new IllegalArgumentException("Unrecognized component type");
		if ( stage < 0 || stage >= m_stages.size() )
			throw new IllegalArgumentException("Unrecognized stage number");
		StageEntry entry = (StageEntry)m_stages.get(stage);
		if ( type == TYPE_PROCESSING )
			entry.processingList.add(c);
		else if ( type == TYPE_ANIMATION )
			entry.animationList.add(c);
		m_pipeline.addComponent(c);
	} //
	
	public void addComponent(PipelineComponent c, int type) {
		this.addComponent(c, type, 0);
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.PipelineManager#runPipeline(java.util.List, edu.berkeley.guir.prefuse.graph.Graph, edu.berkeley.guir.prefuse.ItemRegistry, edu.berkeley.guir.prefuse.Display)
	 */
	public void runPipeline() {
		boolean initRunner = false;
		if (animationRunner == null) {
			animationRunner = new AnimationRunner();
			initRunner = true;
		}
		synchronized (animationRunner) {
			animationRunner.setStart(true);
			animationRunner.notify();
		}
		if (initRunner) {
			new Thread(animationRunner).start();
		}
	} //

	/**
	 * Helper method that runs each pipeline component in order.
	 * @param components
	 */
	protected void processComponents(Iterator components)
	{	
		synchronized ( this.getPipeline().getItemRegistry() ) {			
			while ( components.hasNext() ) {
				try {
					((PipelineComponent)components.next()).runComponent();
				} catch ( Exception e ) {
					e.printStackTrace();
				}
			}
		}
	} //

	class AnimationRunner implements Runnable {
		double animFrac;
		int animTime, frameCount;
		long startTime, calcIn, calcOut;
		boolean m_start = false;

		// TODO: add stop()!
		public void run() {
			while (true) {
				Pipeline pipeline = getPipeline();
STAGE_LOOP:
				for ( int i = 0; i < m_stages.size(); i++ ) {
					synchronized ( this ) {
						if (m_start) { m_start = false; i = -1; continue; }
					}
					
					long loopStart = System.currentTimeMillis();
					StageEntry entry = (StageEntry)m_stages.get(i);
					int animTime = entry.animationTime;
					
					animFrac = 0.0;
					
					// we put this in a synchronized block to ensure all items have
					// been set to the proper positions before the AWT queue can
					// possibly interrupt and draw them.					
					synchronized ( pipeline.getItemRegistry() ) {
						calcIn = System.currentTimeMillis(); /// XXX DEBUG
						pipeline.setDoubleAttribute(ATTR_ANIM_FRAC, animFrac);
						processComponents(entry.processingList.iterator());
						processComponents(entry.animationList.iterator());
						calcOut = System.currentTimeMillis(); /// XXX DEBUG
					}
					
					frameCount = 0; // XXX DEBUG
					startTime = System.currentTimeMillis();
					
					while (animFrac <= 1) {
						synchronized ( this ) {
							if (m_start) { m_start = false; i = -1; continue STAGE_LOOP; }
						}
						
						// delay fraction update until now so that value is correct
						// for any other interested components
						synchronized ( pipeline.getItemRegistry() ) {
							pipeline.setDoubleAttribute(ATTR_ANIM_FRAC, animFrac);
							processComponents(entry.animationList.iterator());
						}
						
						pipeline.getDisplay().repaint(0L);
						frameCount++; // XXX DEBUG

						long time = System.currentTimeMillis() - startTime;
						if (animFrac < 1)
							animFrac = Math.min(((double)time)/((double)animTime),1);
						else
							animFrac = 2; //higher than one :)
	
						double animFrameRate = pipeline.getIntegerAttribute(ATTR_FRAME_RATE);
						long animInterval = 1000L
							/ (animFrameRate == Double.NaN ? 30L : (long) animFrameRate);
						long timeout = animInterval - (System.currentTimeMillis() - loopStart);
	
						try {
							synchronized (this) { wait(Math.max(timeout, 1)); }
						} catch (InterruptedException e) {}
					}
					
					// XXX DEBUG
					double fRate = (1000.0*(double)frameCount)/(System.currentTimeMillis() - startTime);
					System.out.println("stage: "+i+", calc time: "+(calcOut-calcIn)
										+", frameRate: "+fRate+"fps");	
				}
				
				// chill until notified
				try {
					synchronized (this) { wait(); } 
				} catch (InterruptedException e) {}		
			}
		} //

		public synchronized boolean getStart() {
			return m_start;
		} //
		
		public synchronized void setStart(boolean s) {
			m_start = s;
		} //
	} // end of inner class AnimationRunner

} // end of class StagedAnimationManager
