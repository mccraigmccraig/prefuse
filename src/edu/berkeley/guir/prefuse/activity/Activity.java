package edu.berkeley.guir.prefuse.activity;

import java.util.ArrayList;
import java.util.List;

import edu.berkeley.guir.prefuse.event.ActivityEventMulticaster;
import edu.berkeley.guir.prefuse.event.ActivityListener;

/**
 * Represents an activity that can be scheduled and run. This could include
 * graph processing actions and any number of animations.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 * @see edu.berkeley.guir.prefuse.activity.ActivityManager, edu.berkeley.guir.prefuse.activity.ActionPipeline
 */
public abstract class Activity {

    public static final long DEFAULT_STEP_TIME = 20L; // frame rate of 50fps
    
    private static final int SCHEDULED = 0;
    private static final int STARTED   = 1;
    private static final int STEPPED   = 2;
    private static final int FINISHED  = 3;
    private static final int CANCELLED = 4;
    
    private boolean m_enabled = true;
    
    private long m_startTime = -1L;
    private long m_duration  = -1L;
    private long m_stepTime  = -1L;
    private long m_nextTime  = -1L;
    private boolean m_isRunning = false;
    private boolean m_isScheduled = false;
    
    private ActivityListener m_listener;
    private List m_tmp = new ArrayList();
    
    /**
     * Creates a new Activity.
     * @param duration the length of this activity.
     *  A value of -1 indicates an infinite running time.
     * @see edu.berkeley.guir.prefuse.action.Activity#Activity(long,long,long)     
     */
    public Activity(long duration) {
        this(duration, DEFAULT_STEP_TIME);
    } //
    
    /**
     * Creates a new Activity.
     * @param duration the length of this activity.
     *  A value of -1 indicates an infinite running time.
     * @param stepTime the delay time between steps of this activity
     * @see edu.berkeley.guir.prefuse.action.Activity#Activity(long,long,long)
     */
    public Activity(long duration, long stepTime) {
        this(duration, stepTime, System.currentTimeMillis());
    } //
    
    /**
     * Creates a new Activity.
     * @param duration the length of this activity. 
     *  A value of -1 indicates an infinite running time.
     * @param stepTime the delay time between steps of this activity
     * @param startTime the time at which this activity should begin
     */
    public Activity(long duration, long stepTime, long startTime) {
        m_startTime = startTime;
        m_nextTime  = startTime;
        m_duration  = duration;
        m_stepTime  = stepTime;
    } //
    
    /**
     * Run this activity one step. Subclasses should override this method to
     * specify the actions this activity should perform.
     * @param elapsedTime the time elapsed since the start of the activity.
     */
    protected abstract void run(long elapsedTime);
    
    /**
     * Run this activity for a single step. This method is called by the
     * ActivityManager -- outside code should have no need to call or
     * override this method. To implement custom activities, override the
     * run() method instead.
     * @param currentTime the time at which this step is being run.
     * @return the time (in milliseconds) when this activity should be
     *  run again. A return value of -1 indicates this activity is finished.
     */
    public long runActivity(long currentTime) {
        if (currentTime < m_startTime) {
            return m_startTime - currentTime;  
        }
        
        long elapsedTime = currentTime - m_startTime;
        
        if (m_duration == 0 || currentTime > getStopTime()) {
            if ( !isRunning() ) {
               setRunning(true);
               if ( m_listener != null )
                   m_listener.activityStarted(this);
           }
           if ( m_enabled ) {
               run(elapsedTime);
               if ( m_listener != null )
                   m_listener.activityStepped(this);
           }
           setRunning(false);
           if ( m_listener != null )
               m_listener.activityFinished(this);
           ActivityManager.removeActivity(this);
           return -1;
        }
        
        if ( currentTime >= m_nextTime ) {
            if ( !isRunning() ) {
                setRunning(true);
                if ( m_listener != null )
                    m_listener.activityStarted(this);
            }
            if ( m_enabled ) {
                run(elapsedTime);
                m_nextTime = currentTime + m_stepTime;
                if ( m_listener != null )
                    m_listener.activityStepped(this);
            }
            m_nextTime = currentTime + m_stepTime;
        }
        
        return m_stepTime;
    } //
    
    /**
     * Cancels this activity, if scheduled or running. This will stop a
     * running activity, and will remove the activity from
     * the ActivityManager's schedule.
     */
    public void cancel() {
        if ( isScheduled() ) {
            // attempt to remove this activity, if the remove fails,
            // this activity is not currently scheduled with the manager
            if ( m_listener != null )
                m_listener.activityCancelled(this);
            ActivityManager.removeActivity(this);
        }
        setRunning(false);
    } //
    
    /**
     * Indicates if this activity is currently scheduled 
     *  with an ActivityManager
     * @return true if scheduled, false otherwise
     */
    public synchronized boolean isScheduled() {
        return m_isScheduled;
    } //
    
    /**
     * Sets whether or not this Activity has been scheduled. This method should
     * only be called by the ActivityManager.
     * @param s the scheduling state of this Activity
     */
    void setScheduled(boolean s) {
        synchronized ( this ) {
            boolean fire = (s && !m_isScheduled);
            m_isScheduled = s;
        }
        if ( m_listener != null )
            m_listener.activityScheduled(this);
    } //
    
    /**
     * Sets a flag indicating whether or not this activity is currently running
     * @param s the new running state of this activity
     */
    protected synchronized void setRunning(boolean s) {
        m_isRunning = s;
    } //
    
    /**
     * Indicates if this activity is currently running.
     * @return true if running, false otherwise
     */
    public synchronized boolean isRunning() {
        return m_isRunning;
    } //
    
    // ========================================================================
    // == LISTENER METHODS ====================================================
    
    /**
     * Add an ActivityListener to monitor this activity's events.
     * @param l the ActivityListener to add
     */
    public void addActivityListener(ActivityListener l) {
        m_listener = ActivityEventMulticaster.add(m_listener, l);
    } //
    
    /**
     * Remove a registered ActivityListener
     * @param l the ActivityListener to remove
     */
    public void removeActivityListener(ActivityListener l) {
        m_listener = ActivityEventMulticaster.remove(m_listener, l);
    } //
    
    // ========================================================================
    // == ACCESSOR / MUTATOR METHODS ==========================================
    
    /**
     * Get the time at which this activity should complete.
     * @return the stopping time for this activity, or Long.MAX_VALUE
     *  if this activity should run indefinitely.
     */
    public long getStopTime() {
        if (m_duration == -1) {
            return Long.MAX_VALUE;
        }
        return m_startTime + m_duration;
    } //
    
    /**
     * Get the time at which this activity should be run next.
     * @return the time this activity should run next
     */
    public long getNextTime() {
        return m_nextTime;
    } //
    
    /**
     * Returns the duration of this activity
     * @return the duration of this activity, in milliseconds
     */
    public long getDuration() {
        return m_duration;
    } //

    /**
     * Set the duration of this activity
     * @param duration The new duration, in milliseconds, for this activity.
     *  A value of -1 indicates that this activity should run indefinitely.
     */
    public void setDuration(long duration) {
        this.m_duration = duration;
    } //

    /**
     * Returns this activity's start time
     * @return the starting time for this activity
     */
    public long getStartTime() {
        return m_startTime;
    } //

    /**
     * Sets this activity's start time
     * @param time the new starting time for this activity
     */
    public void setStartTime(long time) {
        m_startTime = time;
    } //

    /**
     * Returns the delay between runs for this activity
     * @return the step time between runs of this activity
     */
    public long getStepTime() {
        return m_stepTime;
    } //

    /**
     * Sets the delay between runs for this activity
     * @param time the new step time between runs of this activity
     */
    public void setStepTime(long time) {
        m_stepTime = time;
    } //
    
    /**
     * Indicates whether or not this activity is currently enabled.
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return m_enabled;
    } //
    
    /**
     * Sets whether this component is enabled.
     * @param s true to enable component, false to disable it
     */
    public void setEnabled(boolean s) {
        m_enabled = s;
    } //
    
} // end of class Activity
