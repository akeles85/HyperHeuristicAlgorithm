/*
* Filename: LowLevelHeuristic.java
* Author:   Ali KELES
*
*/


package hh.algorithm.hyperHeuristic.heuristic;

import hh.algorithm.com.Solution;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public abstract class LowLevelHeuristic 
{

    /**
     * 
     * @param solution: will be modified
     */
    public abstract void apply(Solution solution);
}
