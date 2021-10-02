/*
* Filename: TabuSearch.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

import hh.algorithm.representation.ObjectArrayRepresentation;
import java.util.ArrayList;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class TabuSearch
{
    private ArrayList<Solution>     tabuList;
    
    private ArrayList<Solution>     failedList;
    
    private int                     capacityOfTabuList;
    
    private Solution                bestSolution;
    
    private Solution                currentSolution;
    
    private static final int        DEFAULT_CAPACITY_OF_TABU_LIST = 50;
    
    private FitnessHandler           fitnessHandler;    
    
    private FitnessComparator       fitnessComparator;     
    
    private int                     currIterationNumber = 0;
    
    private static final int        MAX_ITERATION_NUMBER = 1;            
    
    
    public TabuSearch()
    {
        this.tabuList = new ArrayList();
        this.failedList = new ArrayList();
        this.bestSolution = null;
        this.capacityOfTabuList = DEFAULT_CAPACITY_OF_TABU_LIST;
        this.currentSolution = null;        
    }
    
    public void setInitialSolution(Solution inSolution)
    {
        this.currentSolution = inSolution;
    }
    
    public void setCapacityOfTabuList(int inCapacity)
    {
        this.capacityOfTabuList = inCapacity;
    }
    /*Representation [H1, H2, H3, ... ]*/
    public boolean solve( Solution initialSolution) throws SystemFault
    {
        this.setInitialSolution(initialSolution);
                
        while( !this.isFinished() )
        {
            this.currIterationNumber++;
            /*Change the solution representation randomly*/
            ObjectArrayRepresentation arrayRep = (ObjectArrayRepresentation)currentSolution.getRepresentation();            
            
            /*Change one element randomly*/
            arrayRep.changeElemValue( RandomGenerator.genInt( arrayRep.size() ));                        
            
            /**/
            if( this.isInFailedList(this.currentSolution) )
                continue;       /*Do not change the current solution*/
            
            if( this.isInTabuList( this.currentSolution ) ) /*Recently visited*/
                continue;

            double fitness = this.getFitnessHandler().calculateFitness( this.currentSolution );
            
            this.currentSolution.setFitness(fitness);
                            
            if( !this.currentSolution.isFeasible() )
            {
                this.failedList.add( this.currentSolution.clone() );
            }
            else
            {
                if( this.bestSolution == null )
                {
                    this.bestSolution = this.currentSolution.clone();                    
                }
                
                if( this.getFitnessComparator().isBetter(this.currentSolution, this.bestSolution ))
                {
                    this.bestSolution = this.currentSolution.clone();
                }
                
                if( this.isTabuListFull() )                
                {
                    this.removeFirstElemFromFullTabuList();
                }
                
                this.tabuList.add(this.currentSolution.clone());
            }            
            
        }
        

        return true;
        
    }
    
    public boolean isFinished()
    {        
        if( this.currIterationNumber == MAX_ITERATION_NUMBER )
            return true;
        else
            return false;
        
    }
    
    
    public boolean isInFailedList( Solution inSolution )
    {
        return this.isInList(inSolution, failedList);
    }
    
    public boolean isInTabuList( Solution inSolution )
    {
        return this.isInList(inSolution, tabuList);
    }
    
    private boolean isInList( Solution inSolution, ArrayList<Solution> searchList )
    {
        for (int i = 0; i < searchList.size(); i++) 
        {
            if( searchList.get(i).compareTo( inSolution ) == 0 )
            {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean isTabuListFull() throws SystemFault
    {
        /*If tabu List is full*/
        if( this.tabuList.size() == this.capacityOfTabuList )
            return true; 
        
        else if( this.tabuList.size() > this.capacityOfTabuList )
        {
            throw new SystemFault(SystemFault.SEVERE_ERROR);
        }
        else
        {
            return false;
        }
    }
    
    /*THIS CODE CAN BE TUNED BY USING OTHER DATA STRUCTURE THAN ARRAYLIST, remove(0) shifts all the entries*/
    public boolean removeFirstElemFromFullTabuList()
    {
        /*If tabu List is full*/
        if( this.tabuList.size() != this.capacityOfTabuList )
            return false;
        
        this.tabuList.remove(0);
        return true;
    }

    public FitnessHandler getFitnessHandler() {
        return fitnessHandler;
    }

    public FitnessComparator getFitnessComparator() {
        return fitnessComparator;
    }

    public void setFitnessHandler(FitnessHandler fitnessHandler) {
        this.fitnessHandler = fitnessHandler;
    }

    public void setFitnessComparator(FitnessComparator fitnessComparator) {
        this.fitnessComparator = fitnessComparator;
    }

    public Solution getBestSolution()
    {
        return this.bestSolution;
    }

}
