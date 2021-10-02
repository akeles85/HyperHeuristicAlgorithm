/*
* Filename: HyperHeuristicFrameworkA.java
* Author:   Ali KELES
*
*/


package hh.algorithm.hyperHeuristic.framework;

import hh.algorithm.com.Solution;
import hh.algorithm.hyperHeuristic.com.HyperHeuristicAlgorithmParams;
import hh.algorithm.hyperHeuristic.heuristic.LowLevelHeuristic;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class HyperHeuristicFrameworkA extends HyperHeuristicAbstractFramework
{
    
    @Override
    public void executive() {
        
        Solution currSolution = this.getAlgorithm().getInitialSolution();
        Solution prevSolution = currSolution;
        int index = 0;
        
        while( index < HyperHeuristicAlgorithmParams.numOfIterationNumber )
        {
            prevSolution = currSolution.clone();            
            LowLevelHeuristic heuristic = this.getAlgorithm().selectHeuristic( currSolution );
        
            this.getAlgorithm().applyHeuristic( heuristic, currSolution );
            
            currSolution.setFitness( currSolution.calculateFitness() );
            
            if( !this.getAlgorithm().isAcceptSolution( currSolution ) )
            {
               currSolution = prevSolution; 
            }
            else
            {
                currSolution.setIterationNumber(index);   
            }   
            
            this.getAlgorithm().setNIterResult( currSolution );
            
            index++;
            
        }
        
        
    }

    
}
