package hh.hyperheuristicalgorithm.test;

import java.util.*;

/** An object in this class acts as a timer, indicating both how much
 * time has elapsed from the time the object was created, as well as
 * how much time remains until the end of a specified period.
 */
public class Timer {

    private long start_time;
    private long end_time;

    /** This constructor begins a timer which will run for a period
     * specified by the <tt>max_time</tt> parameter.
     * @param max_time timer duration in milliseconds
     */
    public Timer(long max_time) {
	start_time = System.currentTimeMillis();
	end_time = start_time + max_time;
    }

    /** Returns the time (in milliseconds) that has elapsed since the
     * creation of this object.
     */
    public long getTimeElapsed() {
	return (System.currentTimeMillis() - start_time);
    }

    /** Returns the time (in milliseconds) that remains until the
     * timer object reaches the end of the period specified at its
     * creation.
     */
    public long getTimeRemaining() {
	return (end_time - System.currentTimeMillis());
    }
}
