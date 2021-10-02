/*
* Filename: HHRWA.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.iterativeHH;

import hh.algorithm.com.Solution;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public abstract class HHRWA {

    public abstract void execute();
    
    public abstract void setSourceDestionationPairs( int [][]inSDPairs );
    
    public abstract Solution getBestSolution();
    
    public abstract Solution getRWAOfBestSolution();
}
