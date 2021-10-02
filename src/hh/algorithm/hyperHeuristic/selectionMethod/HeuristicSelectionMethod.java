/*
* Filename: HeuristicSelectionMethod.java
* Author:   Ali KELES
*
*/


package hh.algorithm.hyperHeuristic.selectionMethod;

import hh.algorithm.com.Solution;
import hh.algorithm.hyperHeuristic.heuristic.LowLevelHeuristic;
import java.util.ArrayList;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public abstract class HeuristicSelectionMethod 
{
    public abstract LowLevelHeuristic selectHeuristic(Solution currSolution, ArrayList<LowLevelHeuristic> heuristics, boolean isBetter);
    
}
