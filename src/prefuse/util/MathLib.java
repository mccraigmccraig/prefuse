package prefuse.util;

import prefuse.Constants;

/**
 * Library of mathematical constants and methods not included in the
 * {@link java.lang.Math} class.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class MathLib {

    /** The value 2 * PI */
    public static final double TWO_PI = 2*Math.PI;
    /** The natural logarithm of 10 */
    public static final double LOG10 = Math.log(10);
    /** The natural logarithm of 2 */
    public static final double LOG2 = Math.log(2);
    
    private MathLib() {
        // prevent instantiation
    }
    
    /**
     * The base 2 logarithm of the input value
     * @param x the input value
     * @return the base 2 logarithm
     */
    public static double log2(double x) {
        return Math.log(x)/LOG2;
    }

    /**
     * The base 10 logarithm of the input value
     * @param x the input value
     * @return the base 10 logarithm
     */
    public static double log10(double x) {
        return Math.log(x)/LOG10;
    }
    
    /**
     * The "safe" base 10 logarithm of the input value, handling
     * negative values by simply making them positive and then
     * negating the return value.
     * @param x the input value
     * @return the "negative-safe" base 10 logarithm
     */
    public static double safeLog10(double x) {
        boolean neg = (x < 0.0);
        if ( neg ) { x = -x; }
        //if ( x < 10.0 ) { x += (10.0-x) / 10; }
        x = Math.log(x) / LOG10;
        
        return neg ? -x : x;
    }
    
    /**
     * The "safe" square root of the input value, handling
     * negative values by simply making them positive and then
     * negating the return value.
     * @param x the input value
     * @return the "negative-safe" square root
     */
    public static double safeSqrt(double x) {
        return ( x<0 ? -Math.sqrt(-x) : Math.sqrt(x) );
    }
    
    /**
     * Interpolates a value between a given minimum and maximum value using
     * a specified scale.
     * @param scale The scale on which to perform the interpolation, one of
     * {@link prefuse.Constants#LINEAR_SCALE},
     * {@link prefuse.Constants#LOG_SCALE}, or
     * {@link prefuse.Constants#SQRT_SCALE}.
     * @param val the interpolation value, a fraction between 0 and 1.0.
     * @param min the minimum value of the interpolation range
     * @param max the maximum value of the interpolation range
     * @return the resulting interpolated value
     */
    public static double interp(int scale, double val, double min, double max) {
        switch ( scale ) {
        case Constants.LINEAR_SCALE:
            return linearInterp(val, min, max);
        case Constants.LOG_SCALE:
            return logInterp(val, min, max);
        case Constants.SQRT_SCALE:
            return sqrtInterp(val, min, max);
        }
        throw new IllegalArgumentException("Unrecognized scale value: "+scale);
    }
    
    /**
     * Interpolates a value between a given minimum and maximum value using
     * a linear scale.
     * @param val the interpolation value, a fraction between 0 and 1.0.
     * @param min the minimum value of the interpolation range
     * @param max the maximum value of the interpolation range
     * @return the resulting interpolated value
     */
    public static double linearInterp(double val, double min, double max) {
        return (val-min)/(max-min);
    }
    
    /**
     * Interpolates a value between a given minimum and maximum value using
     * a base-10 logarithmic scale.
     * @param val the interpolation value, a fraction between 0 and 1.0.
     * @param min the minimum value of the interpolation range
     * @param max the maximum value of the interpolation range
     * @return the resulting interpolated value
     */
    public static double logInterp(double val, double min, double max) {
        double logMin = safeLog10(min);
        return (safeLog10(val)-logMin) / (safeLog10(max)-logMin); 
    }
    
    /**
     * Interpolates a value between a given minimum and maximum value using
     * a square root scale.
     * @param val the interpolation value, a fraction between 0 and 1.0.
     * @param min the minimum value of the interpolation range
     * @param max the maximum value of the interpolation range
     * @return the resulting interpolated value
     */
    public static double sqrtInterp(double val, double min, double max) {
        double sqrtMin = safeSqrt(min);
        return (safeSqrt(val)-sqrtMin) / (safeSqrt(max)-sqrtMin);
    }
    
} // end of class MathLib
