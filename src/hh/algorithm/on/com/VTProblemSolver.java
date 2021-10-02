/*
* Filename: VTProblemSolver.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.GRASP.AICS;
import hh.algorithm.GRASP.AICSParams;
import hh.algorithm.GRASP.GRASP;
import hh.algorithm.GRASP.GRASPParams;
import hh.algorithm.com.FitnessComparator;
import hh.algorithm.com.FitnessHandler;
import hh.algorithm.com.Matrix;
import hh.algorithm.com.ProblemSolver;
import hh.algorithm.com.Solution;
import hh.algorithm.com.StatisticCalculator;
import hh.algorithm.com.SystemFault;
import hh.algorithm.com.TimeUtility;
import hh.algorithm.on.iterativeHH.AntColonyBasedHHRWA;
import hh.algorithm.on.iterativeHH.HHRWA;
import hh.algorithm.representation.LigthPathArrayRepresentation;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class VTProblemSolver extends ProblemSolver implements FitnessHandler, FitnessComparator{

    
    private HHRWA                   hhRWA;
    private AICS                    aics;
    private Solution                bestSolution;
    private TraficFlowHeuristic     trafficFlowHeuristic;
    private Matrix                  trafficMatrix;
    private TrafficFlow             trafficFlows[];
    private static double           infeasibleFitnessValue;
    public String                  currentProblemName;

    public VTProblemSolver() 
    {
        if( VTDesignParams.IS_MINIMIZING_FITNESS )
            infeasibleFitnessValue = Double.MAX_VALUE;
        else
            infeasibleFitnessValue = Double.MIN_VALUE;
    }
    
        
    
    @Override
    public boolean solve(Solution inSolution) throws Exception 
    {
            AntColonyBasedHHRWA hhRWA = new AntColonyBasedHHRWA();    
            
            this.hhRWA = hhRWA;
                                   
            /*Algorithm to solve VTD Problem*/
            aics = new AICS();           

            /*Read the trafficMatrix*/
            trafficMatrix = new Matrix();
            trafficMatrix.setData( Matrix.readFromFile(new File( VTDesignParams.traficFileName )));                                
            trafficMatrix.scale(10);
            /*grasp.setTrafficMatrix(trafficMatrix);                         */

            aics.setHeuristics( trafficMatrix );
            aics.setFitnessHandler(this);
            aics.setFitnessComparator( this );
                        
            trafficFlowHeuristic = new TraficFlowHeuristic( trafficMatrix.getNumOfColumn() );
            this.trafficFlows = null;
            
            aics.setNumOfMoves( VTDesignParams.numOfNode * VTDesignParams.numOfNode );
            aics.solve( null );
            
            aics.getResultSaver().saveToFile(this.currentProblemName);

            bestSolution = aics.getSolution();
            
            
            return bestSolution.isFeasible();
    }

    @Override
    public Solution getSolution() 
    {
        return this.bestSolution;
    }
    
    
   public TrafficFlow[] assignFlows(Solution inSolution) throws SystemFault
    {       
        TrafficFlow assignedFlows[];
        
        LigthPathArrayRepresentation lpRep = (LigthPathArrayRepresentation)inSolution.getRepresentation();
        
        
        /*
         * Get The Lightpaths 
         */
        LightPath allLps[] = lpRep.getArray();
        int numOfFeasibleLps = 0;
        for(int i = 0; i < allLps.length; i++ )
        {
            if( allLps[i].isWavelengthAssigned() && allLps[i].isRouted() && allLps[i].isBerConstraintSatisfied() )
                numOfFeasibleLps++;
        }
        
        LightPath feasibleLps[] = new LightPath[numOfFeasibleLps];
        
        int currFeasibleLpIndex = 0;
        for(int i = 0; i < allLps.length; i++ )
        {
            if( allLps[i].isWavelengthAssigned() && allLps[i].isRouted() && allLps[i].isBerConstraintSatisfied() )
            {
                feasibleLps[ currFeasibleLpIndex++ ] = allLps[i];
            }
        }        
        
        //System.out.println("Infeasible" + (allLps.length - currFeasibleLpIndex) );
        this.trafficFlowHeuristic.setTrafficMatrix( this.trafficMatrix );
        this.trafficFlowHeuristic.setLightPaths( feasibleLps );
        
        assignedFlows = this.trafficFlowHeuristic.assign();
        if ( assignedFlows == null )
        {
            return null;
            /*throw new SystemFault(SystemFault.SEVERE_ERROR);*/
        }
        return assignedFlows;
    }

    /*
     * MINIMIZING
     */
    public boolean isBetter(Solution solution1, Solution solution2) {
        
        if( solution1 == null )
            return false;
        if( solution2 == null )
            return true;
        if( VTDesignParams.IS_MINIMIZING_FITNESS )
        {
            if(  solution1.getFitness() < solution2.getFitness() )
                return true;
            else
                return false;            
        }
        else
        {
            if(  solution1.getFitness() > solution2.getFitness() )
                return true;
            else
                return false;
        }
    }
    
    
    /*Get the sorted sd pairs ans calculate the fitness*/
    public double calculateFitness(Solution solution) throws SystemFault 
    {                
        LigthPathArrayRepresentation lpRep = (LigthPathArrayRepresentation) solution.getRepresentation();
        
        int sdPairs[][];                
        
        sdPairs = lpRep.getSDArray();
        
        /*First do the Routing and wavelength Assignment*/
        hhRWA.setSourceDestionationPairs( sdPairs );
        hhRWA.execute();
                
        /*Try to route the traffic over feasible lightpaths*/
        
        /*if( !hhRWA.getBestSolution().isFeasible() )
        {
            solution.setFitness( infeasibleFitnessValue );
            solution.setFeasible( false );                          
            return solution.getFitness();
        }*/
         
        
         /*Then do the traffic routing over feasible lightpaths*/
        solution.setSubSolution( hhRWA.getBestSolution() );
        this.trafficFlows = this.assignFlows( hhRWA.getRWAOfBestSolution().clone() );
        if (  this.trafficFlows == null )
        {
            System.out.println("infeasible traffic flow");
            VTDesignResult.infeasibleFlowAssign++;
            solution.setFitness( infeasibleFitnessValue );
            solution.setFeasible( false );              
            return solution.getFitness();
        }        
                
        double scaleUpFactor = this.getScaleUpFactor(solution);
        
        solution.setFeasible(true);
        solution.setFitness( this.trafficFlowHeuristic.scaleUp );
        
        return solution.getFitness();
    }    
    
    
    public double getScaleUpFactor(Solution solution) throws SystemFault 
    {
         
        double maxFlowForALink = TrafficFlow.getMaxLinkFlow( this.trafficFlows );
        
        double capacity = VTDesignParams.wavelengthCapacity;
        double scaleFactor =  capacity / maxFlowForALink;
        
        return scaleFactor;
    }    
    
    
    /*
     * parameters
     *  param0 connection order: 3,4,5 
     *  param1 isParameterUtilizing 0,1
     *  param2 q0
     *  param3 alfa
     *  param4 beta
     *  param5 currentInspectionParam and value q0_0.1
     */      
    public static void main(String args[]) throws Exception
    {
        
        /*Set Params*/
        
        if( args.length < 2 )
            throw new Exception("Missing parameters");
        
        int connectionOrder = Integer.valueOf( args[0] );
        if( connectionOrder == 3 || connectionOrder == 4 || connectionOrder == 5 )
        {
            VTDesignParams.numOfRecv = connectionOrder;
            VTDesignParams.numOfTrans = connectionOrder;
            VTDesignParams.topologyName = "VTD_NSFNET_" + connectionOrder;
            System.out.println("Connection Order: " + connectionOrder);
        }
        else
        {
            throw new Exception("param0 is invalid");
        }
        
        if( Integer.valueOf( args[1]) == 1 )
        {
                AICSParams.q0 = Double.valueOf(args[2]);
                AICSParams.alfa = Double.valueOf(args[3]);
                AICSParams.beta = Double.valueOf(args[4]);  
                System.out.println("AICSParams.q0 " + AICSParams.q0);
                System.out.println("AICSParams.alfa " + AICSParams.alfa);
                System.out.println("AICSParams.beta " + AICSParams.beta );
                
                if( AICSParams.q0 > 1 )
                    throw new Exception("Error Param 2");
                
                if( args[5].compareTo("q0") == 0 )
                {
                    VTDesignParams.currentInspectedParamName += "q0_" + AICSParams.q0;
                }
                else if( args[5].compareTo("alfa") == 0 )
                {
                    VTDesignParams.currentInspectedParamName += "alfa_" + AICSParams.alfa;
                }
                else if( args[5].compareTo("beta") == 0 )
                {
                    VTDesignParams.currentInspectedParamName += "beta_" + AICSParams.beta;
                }
                else if( args[5].compareTo("alfa_beta") == 0 )
                {
                    VTDesignParams.currentInspectedParamName += "alfa_beta_" + AICSParams.alfa + "_" + AICSParams.beta;
                }                
                else
                {
                    throw new Exception("Error Param 6");
                }          
        }
        else if( Integer.valueOf( args[1]) == 0)
        {
            
        }
        else
        {
            throw new Exception("param1 is invalid");
        }
        
        
        double  averageFitness = 0;
        int     numOfRun = 10;        
        double []currentStatistics = null;
        double []weightedCurrentStatistics = null;
        double  standardError;
        double  standardDeviation;        
        double  bestResults[];      
        
        VTDesignParams.IS_SD_PAIRS_READED_FROM_FILE = false;
        

        int problemRun = 20;
                
        double[][] allFitness = new double[ problemRun][numOfRun];
        for( int problemIndex = 0; problemIndex < problemRun; problemIndex++ )
        {
            VTDesignParams.traficFileName = "C:\\VTD_Traffic\\TrafficMatrix" + "_" + problemIndex + ".txt";
            System.out.println(VTDesignParams.traficFileName );
            long runTimeForTopology;       
            for( int runIndex = 0; runIndex < numOfRun; runIndex++ )
            {
                System.out.println( runIndex + ". run is starting...");                

                averageFitness = 0;

                runTimeForTopology = 0;
                long startTime, endTime;            
                startTime = TimeUtility.getUserTimeAsMiliSeconds();
                
                VTProblemSolver vtProblemSolver = new VTProblemSolver();
                
                vtProblemSolver.currentProblemName = problemIndex + "_" + runIndex + "_";

                vtProblemSolver.solve(null);                                
                                
                allFitness[problemIndex][runIndex] = vtProblemSolver.getSolution().getFitness();
                        
                endTime = TimeUtility.getUserTimeAsMiliSeconds();

                runTimeForTopology = (long) ((endTime - startTime) / 1000);
                System.out.println("Run Time: " + (runTimeForTopology) );                
            } 
            System.out.println(" Problem Index: " + problemIndex);
            System.out.println(" Standard Deviation: " + StatisticCalculator.standardDeviation(allFitness[problemIndex]));
            System.out.println(" Standard Error: " + StatisticCalculator.standardError(allFitness[problemIndex]) );
            System.out.println(" Best: " + Matrix.getMax( allFitness[problemIndex] ) );
            System.out.println(" Worst: " + Matrix.getMin( allFitness[problemIndex] ));
            
            averageFitness = 0; 
        } 
        
    }    

}
