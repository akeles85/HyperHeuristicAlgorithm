/*
* Filename: Solution.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class Solution implements Cloneable, Comparable{
    
    private double                  fitness;
    
    private Representation          representation;
    
    private int                     iterationNumber;
    
    private boolean                 feasible = true;        
    
    private Solution                subSolution = null;
        
    
    public Solution()
    {                
        this.fitness = 0;
    }         
    

    @Override
    public Solution clone() 
    {
        Solution newSolution = null;

        newSolution = new Solution();

        newSolution.setFitness(this.fitness);

        newSolution.iterationNumber = this.iterationNumber;

        newSolution.setFeasible(this.feasible);

        newSolution.setRepresentation(this.representation.clone());
            
        if( this.subSolution != null )
            newSolution.subSolution = this.subSolution.clone();

        return newSolution;
        
    }
    
    public void initialize()
    {
        this.getRepresentation().initialize();
    }

    public Representation getRepresentation() {
        return representation;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    
    public double calculateFitness()
    {
        try {
            throw new Exception();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public int getIterationNumber() {
        return iterationNumber;
    }

    public void setIterationNumber(int iterationNumber) {
        this.iterationNumber = iterationNumber;
    }

    public void setRepresentation(Representation representation) {
        this.representation = representation;
    }

    public int compareTo(Object o) 
    {
        
        if( this.representation.compareTo( ((Solution)o).getRepresentation() ) == 0 )
        {
            return 0;
        }
        else
            return -1;
    }

    public boolean isFeasible() {
        return feasible;
    }

    public void setFeasible(boolean feasible) {
        this.feasible = feasible;
    }

    public Solution getSubSolution() {
        return subSolution;
    }

    public void setSubSolution(Solution subSolution) {
        this.subSolution = subSolution;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
