/*
* Filename: HyperHeuristicAbstractFramework.java
* Author:   Ali KELES
*
*/


package hh.algorithm.hyperHeuristic.framework;

import hh.algorithm.hyperHeuristic.com.HyperHeuristicAlgorithm;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public abstract class HyperHeuristicAbstractFramework {

    private HyperHeuristicAlgorithm   algorithm;
    
    abstract public void executive();

    public HyperHeuristicAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(HyperHeuristicAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
}
