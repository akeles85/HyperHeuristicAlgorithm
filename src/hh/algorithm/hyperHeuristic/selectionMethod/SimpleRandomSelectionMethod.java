/*
* Filename: SimpleRandomSelectionMethod.java
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
public class SimpleRandomSelectionMethod extends HeuristicSelectionMethod{

    @Override
    public LowLevelHeuristic selectHeuristic(Solution currSolution, ArrayList<LowLevelHeuristic> heuristics, boolean isBetter) 
    {
        int heuristicIndex = RandomGenerator.genInt(heuristics.size());
        return heuristics.get(heuristicIndex);
    }

}
