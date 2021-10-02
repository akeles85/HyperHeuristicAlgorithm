/*
 * ACO.java
 *
 * Created on December 16, 2007, 4:59 PM
 * Created by @author john_locke
 */

package hh.algorithm.AntColony.com;

import hh.algorithm.com.FitnessComparator;
import hh.algorithm.com.FitnessHandler;
import hh.algorithm.com.ProblemSolver;
import hh.algorithm.com.RandomGenerator;
import hh.algorithm.com.Solution;
import hh.algorithm.com.SystemFault;
import hh.algorithm.com.TimeUtility;
import hh.algorithm.representation.ObjectArrayRepresentation;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author john_locke
 */
public class ACO extends ProblemSolver{
    
    private Network                     network;                   
    
    ArrayList<Ant>                      ants;
    
    private int                         numOfMoves = 0;
    
    /*Problem Specific Functions: fitnessHandler, fitnessComparator*/
    private FitnessHandler              fitnessHandler;    
    
    private FitnessComparator           fitnessComparator;                                                
    
    Solution                            bestSolution;           
    
    private double                      startNodeHeuristic[];
    
    private ACOResultSaver        resultSaver;
    
    /** Creates a new instance of ACO */
    public ACO() 
    {
        this.network = new Network( ACOParams.NUMBER_OF_NODE );       
        
        this.ants = new ArrayList<Ant>();
        
        for( int i = 0 ; i < ACOParams.NUMBER_OF_ANTS; i++ )
        {
            this.ants.add(new Ant());
        }
        
        this.bestSolution = null;
        
        startNodeHeuristic = new double[ACOParams.NUMBER_OF_NODE];
        
        resultSaver = new ACOResultSaver();
    }       
    
    public void positionAnts()
    {   
        int initialPosition;
        int bestStartNode;
       
        bestStartNode = getBestStartNodeHeuristic();
        for(Ant ant : this.ants)
        {
            if( RandomGenerator.genDouble() <= ACOParams.q0 )
            {                
                ant.setInitialPosition( this.network.getNode(bestStartNode));
            }
            else
            {                            
                initialPosition = RandomGenerator.genInt( this.network.getNumOfNode() );
                ant.setInitialPosition( this.network.getNode(initialPosition) );
            }
        }
        
    }
    
    /*
     * Returns null if the ant will not move
     */
    public Node decideNextNode(int antNumber)
    {
        Ant currentAnt = this.ants.get(antNumber);                
        Node    selectedNeighbour = null;
        ArrayList<Node> neighbours = this.network.getNeighbours( currentAnt.getCurrentNode() );
                
        if( RandomGenerator.genDouble() <= ACOParams.q0 )
        {
            double  maxTaoValue = Double.MIN_VALUE;
            Node    maxTaoNeighbour = null;
            
            Iterator<Node>  iter = neighbours.iterator();
            while( iter.hasNext() )
            {
                Node node = iter.next();
                double currentTao = getTaoValue( currentAnt.getCurrentNode(), node );
                if( currentTao > maxTaoValue )
                {
                    maxTaoValue = currentTao;
                    maxTaoNeighbour = node;
                }
                else if( currentTao == maxTaoValue && RandomGenerator.genDouble() < 0.5 )
                {
                    maxTaoValue = currentTao;
                    maxTaoNeighbour = node;                    
                }
            }
            selectedNeighbour = maxTaoNeighbour;
        }
        else
        {
            double neighbourhoodSumation = 0;
            Iterator<Node>  iter = neighbours.iterator();
            while( iter.hasNext() )
            {
                Node node = iter.next();
                neighbourhoodSumation += getTaoValue( currentAnt.getCurrentNode(), node );
            }

            iter = neighbours.iterator();
            while( iter.hasNext() )
            {
                Node node = iter.next();
                double currentProbability = RandomGenerator.genDouble();

                double currentPValue = getTaoValue( currentAnt.getCurrentNode(), node );

                currentPValue = currentPValue / neighbourhoodSumation;
                if( currentPValue >= currentProbability )
                {
                    selectedNeighbour = node;
                    break;
                }                                    
            }
        }
                        
        return selectedNeighbour;
        
    }
    
    public double getTaoValue(Node source, Node dest)
    {
        double result  = 0;
        double phenomone = this.network.getPhenomone( source, dest );
        double heuristic = this.network.getHeuristic( source, dest );
        result = ( Math.pow( phenomone, ACOParams.alfa) * Math.pow( heuristic, ACOParams.beta) );        
        return result;
    }
    
    
    public void moveAnts(int antIndex, Node nextNode)
    {
        Ant currentAnt = this.ants.get(antIndex);
        
        currentAnt.move(nextNode);               
    }
    
    public void localUpdate()
    {
        
    }
    
    public void globalUpdate( ArrayList<Node> path , double fitness )
    {
        double increment;
        
                
        for( int i = 0; i < this.network.getNumOfArc(); i++ )
        {
            Arc currentArc = this.network.getArc(i);
            double phenomone = currentArc.getPhenomone();            
            phenomone = (1 - ACOParams.evaporationConstant) * phenomone;
            
            phenomone = checkPhenomone(phenomone);
            currentArc.setPhenomone(phenomone);            
        }

        for( int i = 0; i < path.size() - 1; i++ )
        {
            Arc currentArc = this.network.getArc(path.get(i), path.get(i+1));
            double phenomone = currentArc.getPhenomone();
            increment = 1 / fitness;
            phenomone += increment;            
            phenomone = checkPhenomone(phenomone);
            currentArc.setPhenomone(phenomone);
        }
    }   
    
    public void updateStartNodeHeuristic( ArrayList<Node> path , double fitness )
    {
        for(int i = 0; i < this.startNodeHeuristic.length; i++)
        {
            this.startNodeHeuristic[i] = this.startNodeHeuristic[i] * ( 1 - ACOParams.evaporationConstant);
        }
        
        Node startNode = path.get(0);
        
        this.startNodeHeuristic[ startNode.getIndex() ] += (double)(1.0 / fitness);
    }
    
    public double checkPhenomone(double inPhenomone)
    {
        if( inPhenomone < ACOParams.getMinPhenomone() )
        {
            if( ACOParams.DEBUG_ON )
            {
                System.out.println("Min Value is set " + inPhenomone + " -> " + ACOParams.getMinPhenomone());
            }
            return ACOParams.getMinPhenomone();
        }
        if( inPhenomone > ACOParams.getMaxPhenomone() )
        {                    
            if( ACOParams.DEBUG_ON )
            {
               System.out.println("Max Value is set " + inPhenomone + " -> " + ACOParams.getMaxPhenomone());
            }
            return ACOParams.getMaxPhenomone();   
        }
        return inPhenomone;
    }
    
    public void initPhenomones()
    {
        for( int i = 0; i < this.network.getNumOfArc(); i++ )
        {
            this.network.getArc(i).setPhenomone( ACOParams.getInitialValueOfPhenomone() );
        }
    }
    
    public void initStartNodeHeuristic()
    {
        for( int i = 0; i < this.startNodeHeuristic.length; i++ )
        {
            this.startNodeHeuristic[i] = ACOParams.getInitialValueOfPhenomone();
        }
    }
    
    public int getBestStartNodeHeuristic()
    {
        double maxTao = Double.MIN_VALUE;
        int maxIndex = 0;
        
        for( int i = 0; i < this.startNodeHeuristic.length; i++ )
        {
            if( this.startNodeHeuristic[i] > maxTao )
            {
                maxTao = this.startNodeHeuristic[i];
                maxIndex = i;
            }
        }
        
        return maxIndex;
    }
    
    
    public Solution getResult()
    {
        return this.bestSolution;
    }


    public FitnessHandler getFitnessHandler() {
        return fitnessHandler;
    }

    public void setFitnessHandler(FitnessHandler fitnessHandler) {
        this.fitnessHandler = fitnessHandler;
    }

    public FitnessComparator getFitnessComparator() {
        return fitnessComparator;
    }

    public void setFitnessComparator(FitnessComparator fitnessComparator) {
        this.fitnessComparator = fitnessComparator;
    }

    @Override
    public boolean solve(Solution inSolution) throws SystemFault 
    {
        Solution globalBestSolution = null;
        ArrayList<Node> globalBestPath = null;   
        boolean terminate = false;
        int numOfRun = 0;     
        int lastGlobalBestFoundIteration = 0;
        
    
        /*Create an initial solution [0,0,0,0,0,0,0] to init 
         * maxPhenomone, minPhenomone, initialPhenomone values.
         trail_max = 1. / ( (rho) * nn_tour() );
         trail_min = trail_max /(2. *n); 
         * initial = trail_max;
         */
        ObjectArrayRepresentation representation = (ObjectArrayRepresentation) inSolution.getRepresentation();
        for( int i = 0; i < representation.size(); i++)
        {
            representation.setElemValue( i, 0);                        
        }
        
        double initialFitness = this.fitnessHandler.calculateFitness(inSolution);
        
        ACOParams.initPhenomoneValues(initialFitness, representation.size());
        this.initPhenomones();               
        this.initStartNodeHeuristic();
        
        long startTime = TimeUtility.getUserTimeAsMiliSeconds();
        long endTime = startTime;
        while( !terminate )
        {
            this.positionAnts();
            
            for(int i = 1; i < this.numOfMoves; i++)
            {
                for( int antIndex = 0 ; antIndex < this.ants.size(); antIndex++ )
                {
                    Node nextNode = this.decideNextNode(antIndex);
     
                    if( ACOParams.DEBUG_ON )
                    {
                        if( nextNode != null )
                            System.out.println("Ant " + antIndex + " moved from " + this.ants.get(antIndex).getCurrentNode().getIndex() + " to " + nextNode.getIndex() );
                        else
                            System.out.println("Ant " + antIndex + " stayes at node " + this.ants.get(antIndex).getCurrentNode().getIndex() + "");
                    }
                    
                    if( nextNode != null )
                    {
                        this.moveAnts(antIndex, nextNode);
                    }       
                    else
                    {
                        /*
                         * ALL THE PATHS MUST HAVE THE SAME SIZE: H1 H1 H3 H4
                         * SO IF THE ANT DOES NOT MOVE ACCORDING TO PHENOMONE VALUES,
                         * SET THE NEXT NODE AS THE CURENT NODE
                         */
                        this.moveAnts(antIndex, this.ants.get(antIndex).getCurrentNode());
                    }
                        
                }
            
            }
            
            /*Print paths of ant*/
            if( ACOParams.DEBUG_ON )
            {
                for( int antK = 0; antK < this.ants.size(); antK++ )
                {
                    Ant currentAnt = this.ants.get(antK);
                    System.out.println("Ant "+ antK);
                    for(int pathI = 0; pathI < currentAnt.getPath().size(); pathI++)
                    {
                        System.out.print( currentAnt.getPath().get(pathI) + ", ");
                    }                    
                    System.out.println("");
                }
            }
            
            /*Each ant complete their moves*/            
            ArrayList<Node> bestPath = null;            
            Solution bestSolution = null;
            for( int i = 0; i < this.ants.size(); i++)
            {
                ArrayList<Node> pathOfCurrentAnt = this.ants.get(i).getPath();
                
                /*Convert the path to the solution*/
                Solution currentSolution = convertPathToSolution(inSolution, pathOfCurrentAnt);                

                this.fitnessHandler.calculateFitness(currentSolution);
                
                getResultSaver().addIterSolution( currentSolution );
                
                if( this.fitnessComparator.isBetter(currentSolution, bestSolution) )
                {                                                     
                    bestSolution = currentSolution.clone();
                    bestPath = new ArrayList<>();
                    for( int j = 0; j < pathOfCurrentAnt.size(); j++ )
                    {
                        bestPath.add( pathOfCurrentAnt.get(j) );
                    }                    
                }
                
                if( this.fitnessComparator.isBetter(currentSolution, globalBestSolution) )
                {                    
                    globalBestPath = new ArrayList<>();
                    for( int j = 0; j < pathOfCurrentAnt.size(); j++ )
                    {
                        globalBestPath.add( pathOfCurrentAnt.get(j) );
                    }
                    globalBestSolution = currentSolution.clone();                                        
                    
                    lastGlobalBestFoundIteration = numOfRun;
                    
                    ACOParams.initPhenomoneValues(globalBestSolution.getFitness(), representation.size());
                    
                    globalBestSolution.setIterationNumber( numOfRun );                                        
                    
                    if( ACOParams.DEBUG_ON )
                    {                        
                        //System.out.println("Global Best Found " + numOfRun);
                        /*for(int pathI = 0; pathI < globalBestPath.size(); pathI++)
                        {
                            System.out.print( globalBestPath.get(pathI) + ", ");
                        }                    
                        System.out.println("");                  
                        
                        double fitness = globalBestSolution.getFitness();
                        System.out.println("FITNESS1: " + fitness );                        
                        //System.out.println(this.network);*/
                    }                    
                }
                
                getResultSaver().setBestSolution( globalBestSolution );
            }
        
            /*If no improvement occurs for NUMBER_OF_SEQUENTIAL_NON_IMPROVEMENT_ITERATION*/
            if( (numOfRun-lastGlobalBestFoundIteration) == ACOParams.NUMBER_OF_SEQUENTIAL_NON_IMPROVEMENT_ITERATION )
            {
                lastGlobalBestFoundIteration = numOfRun;
                this.initPhenomones();
                if( ACOParams.DEBUG_ON )
                        System.out.println("Phenomone values are reinitialized at " + numOfRun);
            }
            if(bestPath == null)
                continue;
            
            this.globalUpdate( globalBestPath, globalBestSolution.getFitness());
            this.updateStartNodeHeuristic( globalBestPath, globalBestSolution.getFitness() );
            
            if( numOfRun == ACOParams.NUMBER_OF_ITERATION )
            {
                terminate = true;
                endTime = TimeUtility.getUserTimeAsMiliSeconds();
            }
            numOfRun++;
        }        
        if( ACOParams.DEBUG_ON )
            System.out.println(this.network);
        
        this.bestSolution = globalBestSolution.clone();
        
        this.resultSaver.setRunningTime( endTime - startTime );
        
        return true;        
    }
    
    public Solution convertPathToSolution(Solution constantSolution, ArrayList<Node> path)
    {
        ObjectArrayRepresentation arrayRep = (ObjectArrayRepresentation)constantSolution.getRepresentation();                            
        for( int i = 0; i < path.size(); i++ )
        {
            arrayRep.setElemValue(i, path.get(i).getIndex());   
        }        
        
        Solution newSolution = new Solution();
        
        newSolution.setRepresentation(arrayRep);
        
        return newSolution;
    }

    @Override
    public Solution getSolution() {
        return this.bestSolution;
    }

    @Override
    public boolean isBetter(Solution firstSolution, Solution secondSolution) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getNumOfMoves() {
        return numOfMoves;
    }

    public void setNumOfMoves(int numOfMoves) {
        this.numOfMoves = numOfMoves;
    }

    public ACOResultSaver getResultSaver() {
        return resultSaver;
    }
      
}
