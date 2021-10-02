/*
* Filename: TabuBasedHHRWA.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.iterativeHH;

import hh.algorithm.com.FitnessComparator;
import hh.algorithm.com.FitnessHandler;
import hh.algorithm.com.Matrix;
import hh.algorithm.com.ProblemSolver;
import hh.algorithm.com.RandomGenerator;
import hh.algorithm.com.Representation;
import hh.algorithm.com.SATSolution;
import hh.algorithm.com.Solution;
import hh.algorithm.com.StatisticCalculator;
import hh.algorithm.com.SystemFault;
import hh.algorithm.com.TabuSearch;
import hh.algorithm.on.com.KShortestPathRouter;
import hh.algorithm.on.com.LeastCongestedPathRouter;
import hh.algorithm.on.com.LightPath;
import hh.algorithm.on.com.LightPathRouterHeuristic;
import hh.algorithm.on.com.LowestBERPathRouter;
import hh.algorithm.on.com.MinimizingHighestBERPathRouter;
import hh.algorithm.on.com.RWAProblemSolver;
import hh.algorithm.on.com.RoutingAssignHeuristic;
import hh.algorithm.on.com.ShortestPathRouter;
import hh.algorithm.on.com.VTDesignParams;
import hh.algorithm.on.com.WavelengthAssignHeuristic;
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
public class TabuBasedHHRWA extends HHRWA implements FitnessHandler, FitnessComparator
{
    private TabuSearch                  tabuSearch;
    
    private RWAProblemSolver            RWASolver;    
    
    private int[][]                     sdPairs;
    
    private Solution                    RWASolution;
        
    
    public TabuBasedHHRWA() throws FileNotFoundException, IOException
    {
        this.tabuSearch = new TabuSearch();
        this.tabuSearch.setCapacityOfTabuList( TabuBasedHHRWAParams.TABU_LIST_CAPACITY);
        
        Matrix distanceMatrix = new Matrix();
        //distanceMatrix.setData( Matrix.readFromFile(new File( TabuBasedHHRWAParams.distanceFileName )));
        distanceMatrix.setData( Matrix.readPhysicalTopologyFromFile(new File( VTDesignParams.distanceFileName )));
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
        
        this.tabuSearch.setFitnessHandler(this);
        this.tabuSearch.setFitnessComparator(this);
                         
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
        
        /*            
         *  solution [H1, H2, H3, ...]
         *  RWASolution [LP1, LP2, LP3, ... ] 
         */
        Solution result = RWASolver.getSolution();
        solution.setSubSolution(result);
        solution.setFitness( result.getFitness() );
        solution.setFeasible( result.isFeasible() );
        
        
        return solution.getFitness();
    }

    /*
     * MINIMIZING
     */
    public boolean isBetter(Solution solution1, Solution solution2) {
        
        if( solution1.getFitness() < solution2.getFitness() )
            return true;
        else
            return false;
    }
    
    public void execute()
    {
        Solution initialSolution = new Solution();
        
        int numberOfHeuristics = 5;     
        
        ObjectArrayRepresentation heuristics = new ObjectArrayRepresentation();
        LightPathRouterHeuristic[]  heuristicArray = new LightPathRouterHeuristic[ this.getNumOfSDPairs() ];
        
        Arrays.fill(heuristicArray, new KShortestPathRouter());
        
        LightPathRouterHeuristic[]  domainHeuristicArray = new LightPathRouterHeuristic[ numberOfHeuristics ];
        domainHeuristicArray[0] = new KShortestPathRouter();        
        /*domainHeuristicArray[1] = new KShortestPathRouter(); 
        domainHeuristicArray[2] = new LeastCongestedPathRouter();
        domainHeuristicArray[3] = new LowestBERPathRouter();
        domainHeuristicArray[4] = new MinimizingHighestBERPathRouter();*/
        
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

            this.tabuSearch.solve(initialSolution);
        } catch (SystemFault ex) {
            ex.printStackTrace();
        }
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

            this.tabuSearch.solve(initialSolution);
        } catch (SystemFault ex) {
            ex.printStackTrace();
        }
    }    
    
    
    public Solution getBestSolution()
    {
        return this.tabuSearch.getBestSolution();
    }

    public static void main(String args[]) throws Exception
    {
        double  averageFitness = 0;
        int     numOfRun = 20;
        int     numOfInternalRun = 3000;
        double [][]currentStatistics = null;
        double [][]weightedCurrentStatistics = null;
        double  []standardError;
        double  []standardDeviation;
        double  []internalBestResults;
        
        standardError = new double[numOfInternalRun];
        standardDeviation = new double[numOfInternalRun];        
        internalBestResults = new double[numOfInternalRun];        
              
        String fileName = VTDesignParams.traficFileName;
        
        /*Must be called only once for all heuristic*/
        //createRandomPermutations(3000, 56);
        int permutations[][] = readRandomPermutations();
        long runTimeForTopology;
        for( int runIndex = 0; runIndex < numOfRun; runIndex++ )
        {
            System.out.println("Next Topology " + runIndex);
        try {         
            runTimeForTopology = 0;
            long startTime, endTime;
            ObjectArrayRepresentation arrayRepOfResults[] = new ObjectArrayRepresentation[numOfInternalRun];
            startTime = System.currentTimeMillis();
            for( int internalRun = 0; internalRun < numOfInternalRun; internalRun++)
            {                
                int[][] sdPairs = null;
            
                TabuBasedHHRWA hhRWA = new TabuBasedHHRWA();

                /*Read the trafficMatrix*/
                Matrix trafficMatrix = new Matrix();
                //trafficMatrix.setData( Matrix.readFromFile(new File( TabuBasedHHRWAParams.traficFileName )));
                //trafficMatrix.scale(10*100);
                trafficMatrix.setData( Matrix.readVirtualTopologyFromFile(new File( fileName + String.valueOf(runIndex+1) +".txt" )));            

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
                hhRWA.execute2( permutations[ internalRun ] );
                Solution bestSolution = hhRWA.getBestSolution();

                /*Print heuristics*/
                ObjectArrayRepresentation arrayRep = (ObjectArrayRepresentation)bestSolution.getRepresentation();                        
                arrayRepOfResults[ internalRun ] = arrayRep;
                if( currentStatistics == null )
                    currentStatistics = new double[5][arrayRep.size()] ;
                for( int i = 0; i < arrayRep.size(); i++ )
                {
                 //   System.out.print( arrayRep.get(i) + " ");
                    currentStatistics[ arrayRep.get(i).getType() ][i]++;
                }

               // System.out.print( " " +runIndex + ".  fitness: " + bestSolution.getFitness() );
               // System.out.println("");      
                averageFitness += bestSolution.getFitness();                        
                internalBestResults[ internalRun ] = bestSolution.getFitness();
            }
            endTime = System.currentTimeMillis();
                        
            double currDeviation = StatisticCalculator.standardDeviation(internalBestResults);
            double currError = StatisticCalculator.standardError(internalBestResults);
            
            runTimeForTopology = endTime - startTime;
            System.out.println("Run Time: " + (runTimeForTopology / 3000) );
        System.out.println("Total Fitness = " + (averageFitness / numOfInternalRun)  );
        
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
           int maxValueHeuristicIndex = Matrix.getMinValueIndex(tempArray, 0);
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
                
        
    }
    
    public static int[][]readRandomPermutations() throws Exception
    {
        FileReader f = new FileReader("e:\\permutations.txt");
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
        
        BufferedWriter out = new BufferedWriter(new FileWriter("e:\\permutations.txt"));
        
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

