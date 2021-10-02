/*
* Filename: AntColonyBasedHHRWA.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.iterativeHH;

import hh.algorithm.AntColony.com.ACO;
import hh.algorithm.AntColony.com.ACOParams;
import hh.algorithm.AntColony.com.ACOResultSaver;
import hh.algorithm.com.FitnessComparator;
import hh.algorithm.com.FitnessHandler;
import hh.algorithm.com.Matrix;
import hh.algorithm.com.ProblemResult;
import hh.algorithm.com.RandomGenerator;
import hh.algorithm.com.Solution;
import hh.algorithm.com.StatisticCalculator;
import hh.algorithm.com.SystemFault;
import hh.algorithm.com.TimeUtility;
import hh.algorithm.on.com.KShortestPathRouter;
import hh.algorithm.on.com.LeastCongestedPathRouter;
import hh.algorithm.on.com.LightPath;
import hh.algorithm.on.com.LightPathRouterHeuristic;
import hh.algorithm.on.com.LowestBERPathRouter;
import hh.algorithm.on.com.MinimizingHighestBERPathRouter;
import hh.algorithm.on.com.RWAProblemSolver;
import hh.algorithm.on.com.ShortestPathRouter;
import hh.algorithm.on.com.VTDesignParams;
import hh.algorithm.on.qTool.QTool;
import hh.algorithm.representation.LigthPathArrayRepresentation;
import hh.algorithm.representation.ObjectArrayRepresentation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class AntColonyBasedHHRWA extends HHRWA implements FitnessHandler, FitnessComparator 
{
    private ACO                         acoSearch;        
    
    private RWAProblemSolver            RWASolver;    
    
    private int[][]                     sdPairs;
    
    private Solution                    RWASolution;        
    
   private ProblemResult               problemResult;       
   
   private String                       currentProblemName = "";
        
    
    public AntColonyBasedHHRWA() throws FileNotFoundException, IOException
    {
        this.acoSearch = new ACO();                        
        
        Matrix distanceMatrix = new Matrix();
        if( VTDesignParams.IS_SD_PAIRS_READED_FROM_FILE )
        {
            distanceMatrix.setData( Matrix.readPhysicalTopologyFromFile(new File( VTDesignParams.distanceFileName )));
        }
        else
        {
            distanceMatrix.setData( Matrix.readFromFile(new File( VTDesignParams.distanceFileName )));
        }
       
        distanceMatrix.makeSymetrix();
        
        /*For BER Model, change all the link distances*/
        /*double maxDistanceValue = distanceMatrix.getMaxValueEntry();
        distanceMatrix.scale( 1 / (maxDistanceValue/QTool.MAX_LINK_DISTANCE_IN_KM) );*/
        
       double [][]physicalMatrixInKm = distanceMatrix.getData();
        for(int i = 0; i < physicalMatrixInKm.length; i++)
        {
            for(int j = 0; j < physicalMatrixInKm[i].length; j++ )
            {
                if( physicalMatrixInKm[i][j] != 0 )
                    physicalMatrixInKm[i][j] = QTool.MAX_LINK_DISTANCE_IN_KM;
            }
        }
        
        
        RWASolver = new RWAProblemSolver(distanceMatrix);
        
        this.acoSearch.setFitnessHandler(this);
        this.acoSearch.setFitnessComparator(this);
        
        problemResult = new ProblemResult();
                         
    }        
    
    public void setSourceDestionationPairs( int [][]inSDPairs )
    {
        this.sdPairs = inSDPairs;
    }
    
    public int getNumOfSDPairs()
    {
        return this.sdPairs.length;
    }

    /*[h1, h2, h3]*/
    public double calculateFitness(Solution solution) throws SystemFault 
    {
        ObjectArrayRepresentation heuristicRep = (ObjectArrayRepresentation)solution.getRepresentation();        
        
        this.RWASolver.setSelectedLigthPathRouters( heuristicRep.getArray() );
        
        /*Set physical links and other infos*/
        
        /**/
        
        this.RWASolver.solve( RWASolution.clone() );
        
        Solution result = this.RWASolver.getSolution();
        /*            
         *  solution [H1, H2, H3, ...]
         *  RWASolution [LP1, LP2, LP3, ... ] 
         */
        solution.setSubSolution(result);
        solution.setFitness( result.getFitness() );
        solution.setFeasible( result.isFeasible() );                
        
        problemResult.incNumOfFitnessCalculation();               
                
        return solution.getFitness();
    }

    /*
     * MINIMIZING
     */
    public boolean isBetter(Solution solution1, Solution solution2) {
        
        if( solution1 == null )
            return false;
        if( solution2 == null )
            return true;
        if( solution1.getFitness() < solution2.getFitness() )
            return true;
        else
            return false;
    }
    
    public void execute()
    {
        Solution initialSolution = new Solution();
        
        int numberOfHeuristics = 4;     
        
        ObjectArrayRepresentation heuristics = new ObjectArrayRepresentation();
        LightPathRouterHeuristic[]  heuristicArray = new LightPathRouterHeuristic[ this.getNumOfSDPairs() ];
        
        Arrays.fill(heuristicArray, new ShortestPathRouter());
        
        LightPathRouterHeuristic[]  domainHeuristicArray = new LightPathRouterHeuristic[ numberOfHeuristics ];
        domainHeuristicArray[0] = new ShortestPathRouter();        
        domainHeuristicArray[1] = new KShortestPathRouter(); 
        domainHeuristicArray[2] = new LeastCongestedPathRouter();
        domainHeuristicArray[3] = new LowestBERPathRouter();
        /*domainHeuristicArray[4] = new LowestBERPathRouter();*/
        
        heuristics.setArray( heuristicArray );
        heuristics.setDomain( domainHeuristicArray );
        initialSolution.setRepresentation(heuristics);
        
        
        /*Set SD Pairs*/
        RWASolution = new Solution();
        LigthPathArrayRepresentation lpRep = new LigthPathArrayRepresentation();
        LightPath   lpArray[] = new LightPath[heuristicArray.length];        
        for( int i = 0; i < heuristicArray.length; i++ )
        {
            int phsyicalLinkslpArray[] = Arrays.copyOf( this.sdPairs[i], this.sdPairs[i].length);
            lpArray[i] = new LightPath();
            lpArray[i].setPhysicalLinks(phsyicalLinkslpArray);
        }
        lpRep.setLigthpaths( lpArray );
        
        RWASolution.setRepresentation(lpRep);
        try {
            
            this.acoSearch.setNumOfMoves( this.getNumOfSDPairs() );
            this.acoSearch.solve(initialSolution);
            try {
                this.acoSearch.getResultSaver().saveToFile(this.currentProblemName);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        } catch (SystemFault ex) {
            ex.printStackTrace();
        }
    }
    
    
    public Solution getBestSolution()
    {
        return this.acoSearch.getSolution();
    }   

    
    /**
     * 
     * @param args
     *  param0: Topology Name: NSFNET or ATT
     *  param1: connection order: 3,4,5          
     *  param2: isParameter utilization on
     *      param3: q0
     *      param4: alfa
     *      param5: evopration constant
     * @throws java.lang.Exception
     */
    public static void main(String args[]) throws Exception
    {
        boolean isParameterUtilizingOn;
        
        VTDesignParams.IS_SD_PAIRS_READED_FROM_FILE = true;
        
        int paramBaseIndex = 0;
        /*Read Parameters*/
        if( args.length < 3 )
        {
            System.out.println("Error with parameter name");
            return;
        }
        try{
            if( args[paramBaseIndex+0].toLowerCase().compareTo("nsfnet") == 0 )
            {
                VTDesignParams.distanceFileName = ".\\inputs\\topology\\nsfnet\\PhysicalTopologies\\pt.txt";    
                VTDesignParams.traficFileName = ".\\inputs\\topology\\nsfnet\\VirtualTopologies\\nsfnet";      
                AntColonyBasedHHRWAParams.topologyName = "result_nsfnet";
            }
            else if( args[0].toLowerCase().compareTo("att" ) == 0 )
            {
                VTDesignParams.distanceFileName = ".\\inputs\\topology\\att\\PhysicalTopologies\\pt.txt";    
                VTDesignParams.traficFileName = ".\\inputs\\topology\\att\\VirtualTopologies\\att";                
                AntColonyBasedHHRWAParams.topologyName = "result_att";
            }
            else
            {
                throw new Exception("Param 0 is invalid");
            }
            
            int connectionOrder = Integer.valueOf( args[paramBaseIndex+1]);
            if( connectionOrder == 3  || connectionOrder == 4 || connectionOrder == 5 )
            {
                VTDesignParams.traficFileName += "_vt"+ String.valueOf(connectionOrder) +"_";
                AntColonyBasedHHRWAParams.topologyName += String.valueOf(connectionOrder);
            }
            else
            {
                throw new Exception("Param 1 is invalid");
            }
            
            if( Integer.valueOf(args[paramBaseIndex+2]) == 1 )
            {
                isParameterUtilizingOn = true;
                
                ACOParams.q0 = Double.valueOf(args[paramBaseIndex+3]);
                ACOParams.alfa = Double.valueOf(args[paramBaseIndex+4]);
                ACOParams.evaporationConstant = Double.valueOf(args[paramBaseIndex+5]);                                
                
                if( ACOParams.q0 > 1 || ACOParams.evaporationConstant > 1)
                    throw new Exception("Error Param 3");
                
                if( args[paramBaseIndex+6].compareTo("q0") == 0 )
                {
                    AntColonyBasedHHRWAParams.currentInspectedParamName += "q0_" + ACOParams.q0;
                }
                else if( args[paramBaseIndex+6].compareTo("alfa") == 0 )
                {
                    AntColonyBasedHHRWAParams.currentInspectedParamName += "alfa_" + ACOParams.alfa;
                }
                else if( args[paramBaseIndex+6].compareTo("evo") == 0 )
                {
                    AntColonyBasedHHRWAParams.currentInspectedParamName += "evo_" + ACOParams.evaporationConstant;
                }
                else
                {
                    throw new Exception("Error Param 6");
                }
                
            }
            else if ( Integer.valueOf(args[paramBaseIndex+2]) == 0 )
            {
                isParameterUtilizingOn = false;
            }
            else
            {
                throw new Exception("Param 2 is invalid");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return;
        }
        
        double  averageFitness = 0;
        int     numOfRun = 20;
        int     numOfInternalRun = 10;
        double [][]currentStatistics = null;
        double [][]weightedCurrentStatistics = null;
        double  []standardError;
        double  []standardDeviation;
        double  []internalBestResults;
        double  bestResults[];
        
        standardError = new double[numOfInternalRun];
        standardDeviation = new double[numOfInternalRun];        
        internalBestResults = new double[numOfInternalRun];        
        bestResults = new double[numOfRun * numOfInternalRun];
        String fileName = VTDesignParams.traficFileName;
        long runTimeForTopology;     
        int topologyIndex;
        long    allTimeStart, allTimeEnd;
                
        
        /*Parameter setting*/
        double q_0[] = {0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
        double alfa[] = {0.1, 0.2, 0.5, 1, 2, 4, 8, 16, 32, 64};
        double evaprationConstant[] = {0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1};                
                
        allTimeStart = TimeUtility.getUserTimeAsMiliSeconds();
        /*End of parameter setting*/
        for( int runIndex = 0; runIndex < numOfRun; runIndex++ )
        {
            System.out.println("Next Topology");
            averageFitness = 0;
        try {         
            runTimeForTopology = 0;
            long startTime, endTime;
            ObjectArrayRepresentation arrayRepOfResults[] = new ObjectArrayRepresentation[numOfInternalRun];
            startTime = TimeUtility.getUserTimeAsMiliSeconds();

            topologyIndex = runIndex;            
                        
            /*Different Paramter Settings End*/
            for( int internalRun = 0; internalRun < numOfInternalRun; internalRun++)
            {                
                int[][] sdPairs = null;
            
                AntColonyBasedHHRWA hhRWA = new AntColonyBasedHHRWA();

                /*Read the trafficMatrix*/
                Matrix trafficMatrix = new Matrix();
                //trafficMatrix.setData( Matrix.readFromFile(new File( TabuBasedHHRWAParams.traficFileName )));
                //trafficMatrix.scale(10*100);
                trafficMatrix.setData( Matrix.readVirtualTopologyFromFile(new File( fileName + String.valueOf(topologyIndex+1) +".txt" )));            

                /*Calculate total bandwitdh request*/
                double totalBWRequest = trafficMatrix.getTotalValueOfAllEntry();            

                /*Calculate needed lightpath number*/
               int numOfLightpath = (int)(totalBWRequest / LightPath.getCapacity());                       
               sdPairs = new int[numOfLightpath][2];
                /*Sort the trafic request*/
               for( int i = 0; i < numOfLightpath; i++ )
               {
                    int source_index ;
                    int  destination_index;
                    double []returnValue = trafficMatrix.getMaxValueEntryWithIndexes();
                    source_index = (int)returnValue[0];
                    destination_index = (int)returnValue[1];
                    /*Clear the current max value*/                
                    trafficMatrix.set( source_index, destination_index, Double.MIN_VALUE );

                     /*Set the sd pairs according to trafic request and T-R constraints*/                            
                    sdPairs[i][0] = source_index;
                    sdPairs[i][1] = destination_index;

               }

                hhRWA.setSourceDestionationPairs( sdPairs );
                String currentProblemName = "ACOHHRWA" + "_" + runIndex + "_" + internalRun ; 
                hhRWA.currentProblemName = currentProblemName;
                hhRWA.execute( );
                Solution bestSolution = hhRWA.getBestSolution();

                /*Print heuristics*/
                ObjectArrayRepresentation arrayRep = (ObjectArrayRepresentation)bestSolution.getRepresentation();                        
                arrayRepOfResults[ internalRun ] = arrayRep;
                if( currentStatistics == null )
                    currentStatistics = new double[5][arrayRep.size()] ;
                for( int i = 0; i < arrayRep.size(); i++ )
                {
                    System.out.print( arrayRep.get(i) + " ");
                    currentStatistics[ arrayRep.get(i).getType() ][i]++;
                }

                System.out.print( " " +runIndex + ".  fitness: " + bestSolution.getFitness() + " isFeasible " + bestSolution.isFeasible());
                System.out.println("");      
                averageFitness += bestSolution.getFitness();                        
                internalBestResults[ internalRun ] = bestSolution.getFitness();
            }
            endTime = TimeUtility.getUserTimeAsMiliSeconds();
            double currDeviation = StatisticCalculator.standardDeviation(internalBestResults);
            double currError = StatisticCalculator.standardError(internalBestResults);
            StatisticCalculator.calculateConfidenceInterval(TabuBasedHHRWAParams.CI, internalBestResults);
            runTimeForTopology = endTime - startTime;
            
            for( int intBestResultIndex = 0 ; intBestResultIndex < numOfInternalRun; intBestResultIndex++ )
            {
                bestResults[ runIndex * numOfInternalRun + intBestResultIndex ] = internalBestResults[intBestResultIndex];
            }
        
        weightedCurrentStatistics = new double[ currentStatistics.length ][ currentStatistics[0].length ];

        for( int i = 0; i < currentStatistics.length; i++ )
        {
            weightedCurrentStatistics[i] = Arrays.copyOf(currentStatistics[i], currentStatistics[i].length);
        }
        
        for( int i = 0; i < currentStatistics.length; i++ )
        {
            for(int j = 0; j < currentStatistics[i].length; j++ )
            {
                currentStatistics[i][j] = currentStatistics[i][j] / numOfInternalRun;
            }            
        }
        
        double weightedFitness[][] = new double[5] [arrayRepOfResults[0].size() ];
        for( int hIndex = 0; hIndex < 5; hIndex++ )
        {           
            for( int i = 0; i < arrayRepOfResults[0].size(); i++ )
            {                           
                double occurence = 0;
                for( int j = 0; j < numOfInternalRun; j++ )
                {
                    if( arrayRepOfResults[j].get(i).getType() == hIndex)
                    {
                        weightedFitness[hIndex][i] += internalBestResults[j];
                        occurence++;
                    }
                }
                if( occurence != 0)
                    weightedFitness[hIndex][i] = weightedFitness[hIndex][i] / occurence;
                else
                    weightedFitness[hIndex][i] = 0;
            }
        }
        
        System.out.println("Weighted Fitness" );
        for( int i = 0; i < weightedFitness.length; i++ )
        {
            for(int j = 0; j < weightedFitness[i].length; j++ )
            {
                System.out.print( String.valueOf( weightedFitness[i][j] ) + ", ");
                
            }            
            System.out.println("");
        }   
               
       int maxHeuristic;
       for( int i = 0; i < weightedFitness[0].length; i++ )
        {           
           double []tempArray;
       
           tempArray = new double[ weightedFitness.length ];
           for(int j = 0; j < weightedFitness.length; j++)
               tempArray[j] = weightedFitness[j][i];
           int maxValueHeuristicIndex = Matrix.getMaxValueIndex(tempArray);
            System.out.print(maxValueHeuristicIndex + ", ");
       }           
        
     
        System.out.println("" );  
        System.out.println("Ratio" );               
        for( int i = 0; i < currentStatistics.length; i++ )
        {
            for(int j = 0; j < currentStatistics[i].length; j++ )
            {
                System.out.print( String.valueOf( currentStatistics[i][j] ) + ", ");
                
            }            
            System.out.println("");
        }   
        
      
       for( int i = 0; i < currentStatistics[0].length; i++ )
        {           
           double []tempArray;
       
           tempArray = new double[ currentStatistics.length ];
           for(int j = 0; j < currentStatistics.length; j++)
               tempArray[j] = currentStatistics[j][i];
           int maxValueHeuristicIndex = Matrix.getMaxValueIndex(tempArray);
            System.out.print(maxValueHeuristicIndex + ", ");
       }         
        System.out.println("");            
            
            System.out.println(" Standard Deviation: " + currDeviation);
            System.out.println(" Standard Error: " + currError );
            System.out.println(" Best: " + Matrix.getMin(internalBestResults) );
            System.out.println(" Worst: " + Matrix.getMax(internalBestResults));
            System.out.println(" Run Time (miliseconds): " + (runTimeForTopology) );
            System.out.println(" Average Fitness = " + (averageFitness / numOfInternalRun)  );
            System.out.println(" CI = " + TabuBasedHHRWAParams.CI + " ( " + StatisticCalculator.lowerLimitCI + ", "+ StatisticCalculator.upperLimitCI + " )");            
            System.out.println(" Fitnes Calculation for this topology: " + ProblemResult.getNumOfFitnessCalculation() );            
            
            /*Clear variables*/
            for( int i = 0;i < currentStatistics.length; i++)
            {
                for( int j = 0; j < currentStatistics[i].length; j++)
                    currentStatistics[i][j] = 0;
            }
            
            averageFitness = 0;                        
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }            
        }
        allTimeEnd = TimeUtility.getUserTimeAsMiliSeconds();   
        System.out.println(" All Run Time (miliseconds): " + (allTimeEnd-allTimeStart) );
        System.out.println("Total Fitnes Calculation: " + ProblemResult.getNumOfFitnessCalculation() );
        StatisticCalculator.calculateConfidenceInterval(TabuBasedHHRWAParams.CI, bestResults);
        System.out.println("Final CI = " + TabuBasedHHRWAParams.CI + " ( " + StatisticCalculator.lowerLimitCI + ", "+ StatisticCalculator.upperLimitCI + " )");
        writeResults(bestResults);
        
    }
    
    public static void writeResults(double bestResults[]) throws IOException
    {
        BufferedWriter out = new BufferedWriter(new FileWriter("best_results"));
        
        for( int currPermutation = 0; currPermutation < bestResults.length; currPermutation++ )
        {            
            out.write(String.valueOf( bestResults[currPermutation] ) );                        
            out.write("\n");
            
        }
        
        out.close();             
    }
    

    public ProblemResult getProblemResult() {
        return problemResult;
    }
    
    public void execute2(int []permutation)
    {
        Solution initialSolution = new Solution();
        
        int numberOfHeuristics = 1;     
        
        ObjectArrayRepresentation heuristics = new ObjectArrayRepresentation();
        LightPathRouterHeuristic[]  heuristicArray = new LightPathRouterHeuristic[ this.getNumOfSDPairs() ];
        
        Arrays.fill(heuristicArray, new LowestBERPathRouter());
        
        LightPathRouterHeuristic[]  domainHeuristicArray = new LightPathRouterHeuristic[ numberOfHeuristics ];
        domainHeuristicArray[0] = new LowestBERPathRouter();        
        /*domainHeuristicArray[1] = new ShortestPathRouter(); 
        domainHeuristicArray[2] = new ShortestPathRouter();
        domainHeuristicArray[3] = new ShortestPathRouter();
        domainHeuristicArray[4] = new ShortestPathRouter();*/
        
        heuristics.setArray( heuristicArray );
        heuristics.setDomain( domainHeuristicArray );
        initialSolution.setRepresentation(heuristics);
                
        /*Set SD Pairs*/
        RWASolution = new Solution();
        LigthPathArrayRepresentation lpRep = new LigthPathArrayRepresentation();
        LightPath   lpArray[] = new LightPath[heuristicArray.length];        
        for( int i = 0; i < heuristicArray.length; i++ )
        {
            int phsyicalLinkslpArray[] = Arrays.copyOf( this.sdPairs[i], this.sdPairs[i].length);
            
            lpArray[ permutation[i] ] = new LightPath();
            lpArray[ permutation[i] ].setPhysicalLinks(phsyicalLinkslpArray);
        }
        lpRep.setLigthpaths( lpArray );
        
        RWASolution.setRepresentation(lpRep);
        try {

            this.acoSearch.setNumOfMoves( this.getNumOfSDPairs() );
            this.acoSearch.solve(initialSolution);
        } catch (SystemFault ex) {
            ex.printStackTrace();
        }
    }    
    
    
    public static int[][]readRandomPermutations() throws Exception
    {
        FileReader f = new FileReader("c:\\permutations.txt");
        int permutations[][] = null;
        int rowIndex = 0;
        
        BufferedReader reader = new BufferedReader(f);      
        int numberOfNode = 0;
        
        permutations = new int[3000][];
        
        int lineIndex = 0;
        while(true)
        {
            String line = reader.readLine();               
            if( line == null)
                break;
            
            String[] columns = line.split(":");
            permutations[lineIndex] = new int[ columns.length ];
            for( int i = 0; i < columns.length; i++ )
                permutations[ lineIndex ][ i ] = Integer.valueOf( columns[i] );
            lineIndex++;
        }
        
        f.close();        
        
        return permutations;
    }
    
    
    public static void createRandomPermutations( int numOfPermuation, int length ) throws Exception
    {
        int currPermutationArray[];
        
        BufferedWriter out = new BufferedWriter(new FileWriter("c:\\permutations.txt"));
        
        currPermutationArray = new int[length];              
        for( int currPermutation = 0; currPermutation < numOfPermuation; currPermutation++ )
        {
            Arrays.fill( currPermutationArray, -1 );
            for( int i = 0; i < length; i++ )
            {
                currPermutationArray[i] = RandomGenerator.genInt( length, currPermutationArray );                
                out.write( String.valueOf( currPermutationArray[i] ) );
                out.write(":");
            }
            out.write("\n");
            
        }
        
        out.close();        
    }            
    
    @Override
    public Solution getRWAOfBestSolution() {
        return this.getBestSolution().getSubSolution();
    }

}

