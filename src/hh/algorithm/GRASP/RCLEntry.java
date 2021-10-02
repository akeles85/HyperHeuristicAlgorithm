/*
* Filename: RCLEntry.java
* Author:   Ali KELES
*
*/


package hh.algorithm.GRASP;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class RCLEntry implements Comparable
{
    private int sdPair[];
    
    private double heuristic;
    
    private double experience;
    
    private boolean valid;
    
    private double fitness;
    
    public RCLEntry()
    {
        valid = true;
    }

    public int[] getSdPair() {
        return sdPair;
    }
    
    public int getTransmitter()
    {
        return sdPair[0];
    }
    
    public int getReceiver()
    {
        return sdPair[1];
    }    

    public void setSdPair(int[] sdPair) {
        this.sdPair = new int[sdPair.length];
        this.sdPair[0] = sdPair[0];
        this.sdPair[1] = sdPair[1];
    }

    public double getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(double heuristic) {
        this.heuristic = heuristic;
    }

    public double getExperience() {
        return experience;
    }

    public void setExperience(double experience) {
        this.experience = experience;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int compareTo(Object o) {
        
        RCLEntry inEntry = (RCLEntry)o;
        
        if( this.fitness < inEntry.fitness )
            return 1;
        else if( this.fitness > inEntry.fitness )
            return -1;
        else
            return 0;
    }
    
    public double calculateFitness()
    {
        double result;
        
        result = Math.pow( this.heuristic, GRASPParams.heuristicRatio) + Math.pow( this.experience, GRASPParams.experimentRatio);
        
        this.fitness = result;
        return result;
    }
    
        
    
}
