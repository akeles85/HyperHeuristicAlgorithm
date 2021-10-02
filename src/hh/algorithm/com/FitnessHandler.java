/*
* Filename: FitnessHandler.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

import hh.algorithm.com.SystemFault;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public interface FitnessHandler {

    public double calculateFitness(Solution solution)throws SystemFault;
}
