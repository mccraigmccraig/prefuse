package edu.berkeley.guir.prefuse.pipeline;

/**
 * 
 * Jul 23, 2003 - jheer - Created class
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class SlowInSlowOutAnimator
	extends AbstractPipelineComponent
	implements AnimationComponent {

	/**
	 * @see edu.berkeley.guir.prefuse.pipeline.PipelineComponent#process()
	 */
	public void process() {
		double animFrac = this.getDoubleAttribute(AnimationManager.ATTR_ANIM_FRAC);
		animFrac = ( animFrac == 0.0 || animFrac >= 1.0 ? animFrac : sigmoid(animFrac));
		setDoubleAttribute(AnimationManager.ATTR_ANIM_FRAC, animFrac);
	} //
	
	/**
	 * Computes a normalized sigmoid
	 * @param x input value in the interval [0,1]
	 */
	private double sigmoid(double x) {
		x = 12.0*x - 6.0;
		return (1.0 / (1.0 + Math.exp(-1.0 * x)));
	} //

} // end of class SlowInSlowOutAnimator
