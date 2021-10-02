/*
* Filename: GRASP.java
* Author:   Ali KELES
*
 * This class is designed only for VTD problem, it uses problem specific functions and data structures 
*
*/


package hh.algorithm.GRASP;

import hh.algorithm.com.FitnessComparator;
import hh.algorithm.com.FitnessHandler;
import hh.algorithm.com.Matrix;
import hh.algorithm.com.ProblemSolver;
import hh.algorithm.com.RandomGenerator;
import hh.algorithm.com.Solution;
import hh.algorithm.com.SystemFault;
import hh.algorithm.on.com.LightPath;
import hh.algorithm.on.com.VTDesignParams;
import hh.algorithm.on.iterativeHH.AntColonyBasedHHRWA;
import hh.algorithm.on.iterativeHH.HHRWA;
import hh.algorithm.representation.ArrayRepresentation;
import hh.algorithm.representation.LigthPathArrayRepresentation;
import hh.algorithm.representation.RepresentationElem;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

                    
/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class GRASP extends ProblemSolver{

    
    private RestictedCandidateList  rcl;
    private int                     totalExecutedNumberOfIteration;
    private int                     totalNumberOfIteration;
    
    private int                     numberOfTransmitter;
    private int                     numberOfReceiver;        
    
    private Solution                bestSolution;
    
    private FitnessHandler          fitnessHandler;        
    
    private FitnessComparator       fitnessComparator;        
    
    private int                     numberOfNode;
    
    public GRASP()
    {
        totalExecutedNumberOfIteration = 0;
        totalNumberOfIteration = 0;
    }
    
    
    
    public void setSolutionComponents( int sdPairs[][], int heuristics[] )
    {
        rcl = new RestictedCandidateList();
        
        rcl.fillEntries(sdPairs, heuristics);                
        
        //System.out.println(rcl);
    }
    
    public void setTrafficMatrix(Matrix trafficMatrix)
    {
        int totalSDPair = trafficMatrix.getNumOfColumn() * ( trafficMatrix.getNumOfColumn() - 1 );
        int sdPairs[][] = new int[ totalSDPair ][2];
        int heuristics[] = new int[ totalSDPair ];
        
        numberOfNode = trafficMatrix.getNumOfColumn();
        int currSDPair = 0;
        for( int i = 0; i < trafficMatrix.getNumOfRow(); i++ )
        {
            for( int j = 0; j < trafficMatrix.getNumOfColumn(); j++ )
            {
                if( i == j )
                    continue;
                sdPairs[currSDPair][0] = i;  /*Transmitter*/
                sdPairs[currSDPair][1] = j;  /*Receiver*/
                heuristics[currSDPair] = (int)trafficMatrix.get(i, j);
                currSDPair++;
            }
        }
        
        setSolutionComponents( sdPairs, heuristics);
        
    }
   
    
    public boolean terminationSatisfied()
    {
        if( totalExecutedNumberOfIteration >= totalNumberOfIteration )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    @Override    
    public boolean solve(Solution inSolution) throws SystemFault 
    {                        
        while( !terminationSatisfied() )
        {
            Solution solution = new Solution();                        
            solution = construct( solution );
            localSearch();
            totalExecutedNumberOfIteration++;                        
            updateExperience( solution );
            
            if( this.fitnessComparator.isBetter(solution, this.bestSolution) )
            {
                this.bestSolution = solution.clone();
            }
            System.out.println("GRASP-- Best Solution:" + this.bestSolution.getFitness() );
        }
        
        return true;
    }
    
    /*As the experience is wanted to be minimized, the experience will be 1/fitness*/
    public void updateExperience( Solution inSolution )
    {
        double experience;
        
        if( GRASPParams.IS_MINIMIZING_FITNESS )
        {
            experience = 1 / inSolution.getFitness();
        }
        else
        {
            experience = 10.0 * inSolution.getFitness();
        }
        
        LigthPathArrayRepresentation lpRep = (LigthPathArrayRepresentation)inSolution.getRepresentation();
        
        /*Lower all the old experiences*/
        for( int i = 0; i < this.rcl.size(); i++ )
        {
            RCLEntry currEntry = this.rcl.get(i);
            double oldExperience = currEntry.getExperience();
            double newExperience = ( 1.0 - GRASPParams.evaprationConstant ) * oldExperience;
            currEntry.setExperience( newExperience );
        }
        
        for( int i = 0; i < lpRep.size(); i++ )
        {
            int transmitter = lpRep.get(i).getSourceNode();
            int receiver = lpRep.get(i).getDestinationNode();
            
            for( int j = 0; j < this.rcl.size(); j++ )
            {
                RCLEntry currEntry = this.rcl.get(j);
                
                if( currEntry.getTransmitter() == transmitter && currEntry.getReceiver() == receiver )
                {
                    double oldExperience = currEntry.getExperience();
                    currEntry.setExperience( oldExperience + experience );
                    break;
                }
            }
        }
               
    }
    
    public Solution construct( Solution solution )
    {
        RCLEntry    selectedEntry;        
        Matrix                              matrixRep = new Matrix( this.numberOfNode, this.numberOfNode );
        ArrayList<LightPath>                sortedLightPaths = new ArrayList();
        
        this.calculateFitnessOfSolutionComponents();
        rcl.sort();         
        rcl.makeAllValid();
        
        while(true)
        {                    
            if( RandomGenerator.genDouble() < GRASPParams.randomizeProbabilty )
            {
                selectedEntry = rcl.getRandomFromAvailable();
                if( selectedEntry == null )
                    break;                   
            }
            else
            {
                selectedEntry = rcl.getBestAvailable();
                
                /*If no available one left*/
                if( selectedEntry == null )
                    break;
            }
            
            int currSDPair[] = selectedEntry.getSdPair();
            double value = matrixRep.get( currSDPair[0], currSDPair[1] );
            value++;
            matrixRep.set(currSDPair[0], currSDPair[1], value );
            
            selectedEntry.setValid(false);  
            
            /*Set s-d of the ligthpath and add it to the sorted lightpath list*/
            LightPath currentLightPath = new LightPath();
            int phsyicalLinkslpArray[] = Arrays.copyOf( currSDPair, currSDPair.length);            
            currentLightPath.setPhysicalLinks( phsyicalLinkslpArray );
            sortedLightPaths.add( currentLightPath );             
            
                        
            boolean transmitterLeft = isTransmitterLeft( matrixRep, currSDPair[0] );
            boolean receiverLeft = isReceiverLeft( matrixRep, currSDPair[1] );
            
            /*If no receiver or transmitter left, set the infeasible ones*/
            if( !transmitterLeft || !receiverLeft )
            {
                for( int i = 0; i < rcl.size(); i++ )
                {
                    RCLEntry feasibleCheckEntry = rcl.get(i);
                    int feasibleCheckSDPair[] = feasibleCheckEntry.getSdPair();
                    if( !transmitterLeft && feasibleCheckSDPair[0] == currSDPair[0])
                    {
                        feasibleCheckEntry.setValid(false);
                    }
                    if( !receiverLeft && feasibleCheckSDPair[1] == currSDPair[1] )
                    {
                        feasibleCheckEntry.setValid(false);
                    }                    
                }
            }
            
        }        
        
        LigthPathArrayRepresentation lpArrayRep = new LigthPathArrayRepresentation();
        
        LightPath lpArray[] = new LightPath[ sortedLightPaths.size() ];
        for( int lpIndex = 0; lpIndex < lpArray.length; lpIndex++ )
        {
            lpArray[lpIndex] = sortedLightPaths.get(lpIndex);            
        }
        
        lpArrayRep.setLigthpaths( lpArray );                
        
        /*Set the representation of lightpaths*/
        solution.setRepresentation(lpArrayRep);
        try {
            this.fitnessHandler.calculateFitness(solution);
        } catch (SystemFault ex) {
            ex.printStackTrace();
        }
        
        return solution;
    }        
    
    public boolean isTransmitterLeft( Matrix result, int transmitter )
    {
        int currNumOfUsedTrans = (int) result.countValuesInARow( transmitter );
        
        if( currNumOfUsedTrans == numberOfTransmitter )
        {
            return false;
        }
        else if( currNumOfUsedTrans > numberOfTransmitter )
        {
            try {
                throw new Exception();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            finally
            {
                return false;
            }
        }
        else
            return true;
        
    }
    
    public boolean isReceiverLeft( Matrix result, int receiver )
    {
        int currNumOfUsedRecv = (int) result.countValuesInAColumn( receiver );
        
        if( currNumOfUsedRecv == numberOfReceiver )
        {
            return false;
        }
        else if( currNumOfUsedRecv > numberOfReceiver )
        {
            try {
                throw new Exception();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            finally
            {
                return false;
            }
        }
        else
            return true;        
    }    
    
    
    public void localSearch()
    {
        
    }

    @Override
    public Solution getSolution() {
        return this.bestSolution;
    }

    @Override
    public boolean isBetter(Solution firstSolution, Solution secondSolution) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void calculateFitnessOfSolutionComponents()
    {            
        for(int i = 0; i < this.rcl.size(); i++ )
        {
            rcl.get(i).calculateFitness();            
        }                                        
    }        
      

    public void setTotalNumberOfIteration(int totalNumberOfIteration) {
        this.totalNumberOfIteration = totalNumberOfIteration;
    }

    public void setNumberOfTransmitter(int numberOfTransmitter) {
        this.numberOfTransmitter = numberOfTransmitter;
    }

    public void setNumberOfReceiver(int numberOfReceiver) {
        this.numberOfReceiver = numberOfReceiver;
    }

    public void setFitnessHandler(FitnessHandler fitnessHandler) {
        this.fitnessHandler = fitnessHandler;
    }

    public void setFitnessComparator(FitnessComparator fitnessComparator) {
        this.fitnessComparator = fitnessComparator;
    }
       

}
