/*
* Filename: LightPathRouter.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.com.Copyable;
import hh.algorithm.com.Representation;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public abstract class LightPathRouterHeuristic implements Copyable, Comparable{

    private int type;
    
    public static final int     SHORTEST_PATH = 0;
    public static final int     KSHORTEST_PATH = 1;
    public static final int     LEAST_CONGESTED_SHORTEST_PATH = 2;
    public static final int     LOWEST_BER_PATH = 3;
    public static final int     MINIMIZING_HIGHEST_BER_PATH = 4;
    /**
     * @return Returns false, if it cannot find the route for any of the s-d pairs
     * @return The given parameter will be filled with the paths that are routed
     */
    public abstract int[][] execute(RoutingAssignHeuristic routingHeuristic, int [][]sdPairs);

    
    @Override
    public Object clone()  {
        return null;
    }

    @Override
    public String toString() {
        return String.valueOf( getType() );
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
        
    
}

