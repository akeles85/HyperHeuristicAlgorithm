/*
* Filename: ACOParams.java
* Author:   Ali KELES
*
*/


package hh.algorithm.AntColony.com;

import hh.hyperheuristicalgorithm.HyperHeuristicParams;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class ACOParams {

    public static final int             NUMBER_OF_ANTS = 5;
    
    public static final int             NUMBER_OF_NODE = HyperHeuristicParams.NUMBER_OF_HEURISTICS;                
    
    public static final double          beta = 0;               
    
    private static double               maxPhenomone;
    
    private static double               minPhenomone;        
    
    private static double               initialValueOfPhenomone = maxPhenomone;            
    
    public static final int             NUMBER_OF_ITERATION = 70 / NUMBER_OF_ANTS; 
    
    public static final int             NUMBER_OF_SEQUENTIAL_NON_IMPROVEMENT_ITERATION = 50;
    
    public static final boolean         DEBUG_ON = false;
    
    public static double                alfa = 0.5;
    
    public static double                q0 = 0.2;
    
    public static double                evaporationConstant = 0.1; 

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
        maxPhenomone = 10.0 / (double)( evaporationConstant * initialSolutionFitness);
        minPhenomone = (double)maxPhenomone / (double)((2 * numOfInstance) * 10.0);
        initialValueOfPhenomone = maxPhenomone;
    }
}
