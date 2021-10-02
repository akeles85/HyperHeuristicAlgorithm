/*
* Filename: RandomWalkHeuristic.java
* Author:   Ali KELES
*
*/


package hh.algorithm.hyperHeuristic.heuristic;

import hh.algorithm.com.Matrix;
import hh.algorithm.com.RandomGenerator;
import hh.algorithm.com.Representation;
import hh.algorithm.com.Solution;
import hh.algorithm.com.SystemFault;
import hh.algorithm.representation.ArrayRepresentation;
import hh.algorithm.representation.ArrayRepresentation;
import hh.algorithm.representation.ArrayRepresentation;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class RandomWalkHeuristic extends LowLevelHeuristic
{
    @Override
    public void apply( Solution solution ) 
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
        
        int index = RandomGenerator.genInt( representation.length() );
        
        try{
            representation.changeElemValue(index);
        }
        catch(SystemFault sf)
        {
            sf.printStackTrace();
        }        
    }
    
    private void applyToMatrix( Solution solution )
    {
        Matrix representation = (Matrix) solution.getRepresentation();
        
        int columnIndex = RandomGenerator.genInt( representation.getNumOfColumn() );
        int rowIndex = RandomGenerator.genInt( representation.getNumOfRow() );
        
        double oldValue = representation.get(columnIndex, rowIndex);       

        if( oldValue == 0 )
        {
            double newValue = RandomGenerator.genInt( (int)representation.getMaxValidValue() );
            representation.set(columnIndex, rowIndex, newValue);
        }
        else
        {
            representation.set(columnIndex, rowIndex, 0);
        }
     
    }    

}
