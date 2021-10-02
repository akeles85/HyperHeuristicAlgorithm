/*
* Filename: TimeUtility.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class TimeUtility {
    
    private static long nanoToMiliSeconds = (long) Math.pow(10, 6);
    
    /** Get user time in  */
    public static long getUserTimeAsMiliSeconds( ) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
        long nano = bean.isCurrentThreadCpuTimeSupported( ) ? bean.getCurrentThreadUserTime( ) : 0L;
        return (nano / nanoToMiliSeconds);
    }       

}
