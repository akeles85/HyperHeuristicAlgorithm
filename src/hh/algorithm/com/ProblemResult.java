/*
* Filename: ProblemResult.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class ProblemResult {
    
    private static int numOfFitnessCalculation = 0;
    
    public static void incNumOfFitnessCalculation()
    {
        numOfFitnessCalculation++;
    }

    public static int getNumOfFitnessCalculation() {
        return numOfFitnessCalculation;
    }

}
