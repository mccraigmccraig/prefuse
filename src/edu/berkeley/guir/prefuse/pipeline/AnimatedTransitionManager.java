package edu.berkeley.guir.prefuse.pipeline;

import java.util.Iterator;
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
public class AnimatedTransitionManager extends DefaultPipelineManager {
	
	public static final String ATTR_FRAME_RATE = "frameRate";
	public static final String ATTR_ANIM_TIME = "animationTime";
	public static final String ATTR_ANIM_FRAC = "animationFrac";
	public static final String ATTR_ANIM_CONTINUE = "animationContinue";

	private AnimationRunner animationRunner;

	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.PipelineManager#runPipeline(java.util.List, edu.berkeley.guir.prefuse.graph.Graph, edu.berkeley.guir.prefuse.ItemRegistry, edu.berkeley.guir.prefuse.Display)
	 */
	public void runPipeline() {
		boolean initRunner = false;
		if (animationRunner == null) {
			animationRunner = new AnimationRunner();
			initRunner = true;
		}
		animationRunner.setStart(true);
		synchronized (animationRunner) {
			animationRunner.notify();
		}
		if (initRunner) {
			new Thread(animationRunner).start();
		}
	} //

	class AnimationRunner implements Runnable {
		double animFrac;
		int animTime, frameCount;
		long startTime, calcIn, calcOut;
		boolean m_start = false;

		public void run() {
			while (true) {
				long loopStart = System.currentTimeMillis();
				boolean start = getStart();
				Pipeline pipeline = getPipeline();
				if (start) {
					setStart(false);
					setAnimationState(pipeline.getComponents(), false);
						
					calcIn = System.currentTimeMillis(); /// XXX DEBUG
					processComponents(pipeline.getComponents());
					calcOut = System.currentTimeMillis(); /// XXX DEBUG
						
					setAnimationState(pipeline.getComponents(), true);

					animTime = pipeline.getIntegerAttribute(ATTR_ANIM_TIME);
					if (animTime == Integer.MIN_VALUE) {
						throw new IllegalArgumentException("Animation time is not set!");
					}
					animFrac = 0.0;
					pipeline.setDoubleAttribute(ATTR_ANIM_FRAC, animFrac);
					frameCount = 0; // XXX DEBUG
					startTime = System.currentTimeMillis();
				}

				if (animFrac <= 1) {
					processComponents(pipeline.getComponents());

					pipeline.getDisplay().repaint();
					frameCount++; // XXX DEBUG

					long time = System.currentTimeMillis() - startTime;
					if (animFrac < 1)
						animFrac =
							Math.min(((double) time) / ((double) animTime), 1);
					else
						animFrac = 2; //higher than one :)
					pipeline.setDoubleAttribute(ATTR_ANIM_FRAC, animFrac);

					double animFrameRate = pipeline.getIntegerAttribute(ATTR_FRAME_RATE);
					long animInterval = 1000L
						/ (animFrameRate == Double.NaN ? 30L : (long) animFrameRate);
					long timeout = animInterval - (System.currentTimeMillis() - loopStart);

					try {
						synchronized (this) { wait(Math.max(timeout, 1)); }
					} catch (InterruptedException e) {}
				} else {
					// XXX DEBUG
					double fRate = (1000.0*(double)frameCount)/(System.currentTimeMillis() - startTime);
					System.out.println("calc time: "+(calcOut-calcIn)+", frameRate: "+fRate+"fps");
						
					// chill until notified
					try {
						synchronized (this) { wait(); } 
					} catch (InterruptedException e) {}
				}
			}
		} //

		public synchronized boolean getStart() {
			return m_start;
		} //
		
		public synchronized void setStart(boolean s) {
			m_start = s;
		} //

		protected void setAnimationState(Iterator components, boolean animating) {
			Iterator iter = components;
			while (components.hasNext()) {
				PipelineComponent pc = (PipelineComponent) components.next();
				if (pc instanceof ProcessingComponent) {
					pc.setEnabled(!animating);
				} else if (pc instanceof AnimationComponent) {
					pc.setEnabled(animating);
				}
			}
		} //
		
	} // end of inner class AnimationRunner

} // end of class AnimatedTransitionManager
