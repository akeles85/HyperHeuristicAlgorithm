/*
* Filename: Problem.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public abstract class ProblemSolver 
{       
    public abstract boolean solve(Solution inSolution) throws Exception;
    
    public boolean isIterative(){ return false;}
    
    public abstract Solution getSolution();
    
    public abstract boolean isBetter( Solution firstSolution, Solution secondSolution );

        
}
