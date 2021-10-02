/*
* Filename: AICSParams.java
* Author:   Ali KELES
*
*/


package hh.algorithm.GRASP;

import hh.algorithm.on.com.VTDesignParams;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class AICSParams{

    public static final int             NUMBER_OF_ANTS = 1;
    
    public static final int             NUMBER_OF_NODE = VTDesignParams.numOfNode * (VTDesignParams.numOfNode);        
    
    public static double                alfa = 0.6;
    
    public static double                beta = 0.4;
    
    public static double                q0 = 0.3;        
    
    public static final double          evaporationConstant = 0.1;        
    
    private static double               maxPhenomone;
    
    private static double               minPhenomone;        
    
    private static double               initialValueOfPhenomone = maxPhenomone;            
    
    public static final int             NUMBER_OF_ITERATION = 300 / NUMBER_OF_ANTS; 
    
    public static final int             NUMBER_OF_SEQUENTIAL_NON_IMPROVEMENT_ITERATION = 60;
    
    public static final boolean         DEBUG_ON = false;        
    
    public static final long            duration = 1 * 30 * 1000;

    public static double getMaxPhenomone() {
        return maxPhenomone;
    }

    public static double getMinPhenomone() {
        return minPhenomone;
    }

    public static double getInitialValueOfPhenomone() {
        return initialValueOfPhenomone;
    }


    public static void initPhenomoneValues(double initialSolutionFitness, int numOfInstance) 
    {
        maxPhenomone = (10.0 * initialSolutionFitness)/ (double)( evaporationConstant);
        minPhenomone = (double)maxPhenomone / (double)((2 * numOfInstance)*10);
        initialValueOfPhenomone = maxPhenomone;
    }
        
}
