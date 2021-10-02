/*
* Filename: OnlyImprovingMovesAcceptMethod.java
* Author:   Ali KELES
*
*/


package hh.algorithm.hyperHeuristic.acceptanceMethod;

import hh.algorithm.com.Solution;
import hh.algorithm.hyperHeuristic.com.HyperHeuristicAlgorithm;
import hh.algorithm.hyperHeuristic.com.HyperHeuristicAlgorithmParams;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class OnlyImprovingMovesAcceptMethod extends MoveAcceptanceMethod{

    double previousFitness = 0;
    boolean previousFitnessSet = false;
    
    @Override
    public boolean decideToAccept(Solution solution) 
    {
        if( previousFitnessSet == false )
        {
            previousFitness = solution.getFitness();
            previousFitnessSet = true;
            return true;
        }
        else
        {
            if( HyperHeuristicAlgorithm.isBetter(solution.getFitness(), this.previousFitness  ) )
            {
                this.previousFitness = solution.getFitness();
                return true;         
            }
            else
            {                
                return false;
            }
        }
    }

}
