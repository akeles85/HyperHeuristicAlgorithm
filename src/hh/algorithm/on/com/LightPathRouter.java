/*
* Filename: LightPathRouter.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.com.Representation;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public interface LightPathRouter {

    /**
     * @return Returns false, if it cannot find the route for any of the s-d pairs
     * @return The given parameter will be filled with the paths that are routed
     */
    public int[][] execute(RoutingAssignHeuristic routingHeuristic, int [][]sdPairs);
}
