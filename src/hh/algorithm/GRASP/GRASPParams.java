/*
* Filename: GRASPParams.java
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
public class GRASPParams {

    //public static final double          initialExperience = (1.0 / VTDesignParams.BER_THRESHOLD );
    public static final double          initialExperience = 100;
    
    public static final double          randomizeProbabilty = 0.4;
    
    public static final double          heuristicRatio = 0.3;
    
    public static final double          experimentRatio = 1 - heuristicRatio;
    
    public static final int             NUMBER_OF_ITERATION = 60;
    
    public static final double          evaprationConstant = 0.1;
    
    public static final boolean         IS_MINIMIZING_FITNESS = VTDesignParams.IS_MINIMIZING_FITNESS;
        
}
