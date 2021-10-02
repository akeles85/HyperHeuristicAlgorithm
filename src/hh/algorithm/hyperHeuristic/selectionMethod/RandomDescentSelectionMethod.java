/*
* Filename: RandomDescentSelectionMethod.java
* Author:   Ali KELES
*
*/


package hh.algorithm.hyperHeuristic.selectionMethod;

import hh.algorithm.com.RandomGenerator;
import hh.algorithm.com.Solution;
import hh.algorithm.hyperHeuristic.heuristic.LowLevelHeuristic;
import java.util.ArrayList;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class RandomDescentSelectionMethod extends HeuristicSelectionMethod{

    int previousHeuristicIndex;
    boolean isPreviousPerformanceSet = false;
    
    @Override
    public LowLevelHeuristic selectHeuristic(Solution currSolution, ArrayList<LowLevelHeuristic> heuristics, boolean isBetter ) 
    {
        int currHeuristicIndex;
        if( isPreviousPerformanceSet == false )
        {
            isPreviousPerformanceSet = true;
            currHeuristicIndex = RandomGenerator.genInt(heuristics.size());            
        }
        else
        {
            if( isBetter )
            {
                currHeuristicIndex = previousHeuristicIndex;
            }   
            else
            {
                currHeuristicIndex = RandomGenerator.genInt(heuristics.size());
            }
        }
        
        previousHeuristicIndex = currHeuristicIndex;
        return heuristics.get(currHeuristicIndex);        
    }

}
