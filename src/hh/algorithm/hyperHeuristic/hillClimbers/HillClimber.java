/*
* Filename: HillClimber.java
* Author:   Ali KELES
*
*/


package hh.algorithm.hyperHeuristic.hillClimbers;

import hh.algorithm.com.Matrix;
import hh.algorithm.com.Representation;
import hh.algorithm.com.Solution;
import hh.algorithm.com.SystemFault;
import hh.algorithm.hyperHeuristic.com.HyperHeuristicAlgorithm;
import hh.algorithm.hyperHeuristic.heuristic.LowLevelHeuristic;
import hh.algorithm.representation.ArrayRepresentation;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class HillClimber extends LowLevelHeuristic{

    public void apply(Solution solution)
    {
        Representation representation = solution.getRepresentation();
        
        if( representation instanceof ArrayRepresentation )
            this.applyToArray(solution);
        else if( representation instanceof Matrix)
            this.applyToMatrix(solution);        
    }
            
    
    private void applyToArray( Solution solution )
    {
        ArrayRepresentation representation = (ArrayRepresentation) solution.getRepresentation();
        
        double oldFitness = solution.getFitness();
        double newFitness;
        for( int i = 0; i < representation.length(); i++ )
        {
            try {
                representation.changeElemValue(i);
            } catch (SystemFault ex) {
                ex.printStackTrace();
            }
            
            newFitness = solution.calculateFitness();
            /*Break at the first improvement*/
            if( HyperHeuristicAlgorithm.isBetter( newFitness, oldFitness ) )
            {
                break;
            }
            else
            {
                try {
                    /*return to the old value*/
                    representation.changeElemValue(i);
                } catch (SystemFault ex) {
                    ex.printStackTrace();
                }
            }
        }        
    }
    
    
    /**
     * Changes the values by taking the valid value constraint
     * Do not check any other constraints. (Do not check receive, transmit constraints)
     * @param solution
     */
    private void applyToMatrix( Solution solution )
    {    
        Matrix representation = (Matrix) solution.getRepresentation();        
                
        double oldFitness = solution.getFitness();
        double newFitness;
        for( int columnIndex = 0; columnIndex < representation.getNumOfColumn(); columnIndex++ )
        {
            for( int rowIndex = 0; rowIndex < representation.getNumOfRow(); rowIndex++ )
            {                
                representation.changeElemValue(columnIndex, rowIndex);
            
                newFitness = solution.calculateFitness();
                /*Break at the first improvement*/
                if( HyperHeuristicAlgorithm.isBetter( newFitness, oldFitness ) )
                {
                    break;
                }
                else
                {
                    /*return to the old value*/
                    representation.changeElemValue(columnIndex, rowIndex);

                }
            }
        }        
        
    }
}
