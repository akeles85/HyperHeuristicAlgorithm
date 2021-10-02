/*
* Filename: AICS.java
* Author:   Ali KELES
*
*/


package hh.algorithm.GRASP;

import hh.algorithm.AntColony.com.ACOConstraintHandler;
import hh.algorithm.AntColony.com.Ant;
import hh.algorithm.AntColony.com.Arc;
import hh.algorithm.AntColony.com.Network;
import hh.algorithm.AntColony.com.Node;
import hh.algorithm.com.FitnessComparator;
import hh.algorithm.com.FitnessHandler;
import hh.algorithm.com.Matrix;
import hh.algorithm.com.ProblemSolver;
import hh.algorithm.com.RandomGenerator;
import hh.algorithm.com.Solution;
import hh.algorithm.com.SystemFault;
import hh.algorithm.com.TimeUtility;
import hh.algorithm.on.com.LightPath;
import hh.algorithm.on.com.VTDesignParams;
import hh.algorithm.representation.LigthPathArrayRepresentation;
import hh.algorithm.representation.ObjectArrayRepresentation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class AICS extends ProblemSolver {
    
    private Network                     network;                   
    
    ArrayList<Ant>                      ants;
    
    private int                         numOfMoves = 0;
    
    /*Problem Specific Functions: fitnessHandler, fitnessComparator*/
    private FitnessHandler              fitnessHandler;    
    
    private FitnessComparator           fitnessComparator;                                                        
    
    Solution                            bestSolution;           
    
    private double                      startNodeHeuristic[];
    
    private Matrix                      matrixRep;    
    
    private Matrix                      trafficMatrix;
    
    private AICSResultSaver             resultSaver;
    
    private static final int WA = 0;
    private static final int CA1 = 1;
    private static final int CA2 = 2;
    private static final int UT = 3;
    private static final int CO = 4;
    private static final int TX = 5;
    private static final int NE = 6;
    private static final int IL = 7;
    private static final int PA = 8;
    private static final int GA = 9;
    private static final int MI = 10;
    private static final int NY = 11;
    private static final int NJ = 12;
    private static final int MD = 13;   
    
    public static final String []nodeTexts = {"WA", "CA1",
    "CA2", 
    "UT", 
    "CO", 
    "TX", 
    "NE", 
    "IL", 
    "PA", 
    "GA",
    "MI", 
    "NY", 
    "NJ", 
    "MD"} ;
    
    /** Creates a new instance of ACO */
    public AICS() 
    {
        this.network = new Network( AICSParams.NUMBER_OF_NODE );       
        
        this.ants = new ArrayList();
        
        for( int i = 0 ; i < AICSParams.NUMBER_OF_ANTS; i++ )
        {
            this.ants.add(new Ant());
        }
        
        this.bestSolution = null;
        
        startNodeHeuristic = new double[AICSParams.NUMBER_OF_NODE];
        
        resultSaver = new AICSResultSaver();
    }       
    
    public void setHeuristics(Matrix inTrafficMatrix)
    {                
        trafficMatrix = inTrafficMatrix.clone();        
    }
    
    public void scaleHeuristicAccordingToFitness( double initialFitnessValue )
    {
        double max = trafficMatrix.getMaxValueEntry();
        double multiplier = initialFitnessValue / max;
        trafficMatrix.scale(multiplier);
        
        /*Use the beta*/
        double data[][] = trafficMatrix.getData();
        for( int i = 0; i < data.length; i++ )
        {
            for( int j = 0; j < data.length; j++ )
            {
                data[i][j] = Math.pow(data[i][j], AICSParams.beta);
            }
        }
    }
    
    public void positionAnts()
    {   
        int initialPosition;
        
        int bestStartNodeHeuristic = getBestStartNodeHeuristic();
        Node selectedNode = null;
        
        for(Ant ant : this.ants)
        {
            if( RandomGenerator.genDouble() <= AICSParams.q0 )
            {                
                selectedNode = this.network.getNode(bestStartNodeHeuristic);
                ant.setInitialPosition( selectedNode );
            }
            else
            {          
                boolean isUnSet = true;
                do{
                    initialPosition = RandomGenerator.genInt( this.network.getNumOfNode() );
                    /*Do not choose x,x as a sd pair*/
                    if(this.network.getNode(initialPosition).receiver == this.network.getNode(initialPosition).transmitter)
                        continue;
                    
                    selectedNode = this.network.getNode(initialPosition);
                    ant.setInitialPosition( selectedNode );
                    isUnSet = false;
                }while(isUnSet);
            }
        }
                
        double value = matrixRep.get( selectedNode.transmitter, selectedNode.receiver );
        value++;
        matrixRep.set( selectedNode.transmitter, selectedNode.receiver, value );          
        
    }
    
    /*
     * Returns null if the ant will not move
     */
    public Node decideNextNode(int antNumber)
    {
        Ant currentAnt = this.ants.get(antNumber);                
        Node    selectedNeighbour = null;
        ArrayList<Node> neighbours = this.network.getNeighbours( currentAnt.getCurrentNode() );
                        
        if( RandomGenerator.genDouble() <= AICSParams.q0 )
        {            
            double  maxTaoValue = Double.MIN_VALUE;
            Node    maxTaoNeighbour = null;
            
            Iterator<Node> iter = neighbours.iterator();
            while( iter.hasNext() )
            {
                Node currNeighbour = iter.next();
                if( currNeighbour.isValid() == false )
                    continue;                          
                
                double currentTao = getTaoValue( currentAnt.getCurrentNode(), currNeighbour );
                if( currentTao > maxTaoValue )
                {
                    maxTaoValue = currentTao;
                    maxTaoNeighbour = currNeighbour;
                }
                else if( currentTao == maxTaoValue && RandomGenerator.genDouble() < 0.5 )
                {
                    maxTaoValue = currentTao;
                    maxTaoNeighbour = currNeighbour;                    
                }
            }
            selectedNeighbour = maxTaoNeighbour;
        }
        else
        {
            double neighbourhoodSumation = 0;
            Iterator<Node> iter = neighbours.iterator();
            while( iter.hasNext() )
            {
                Node currNeighbour = iter.next();                  

                if( currNeighbour.isValid() == false )
                    continue;
                neighbourhoodSumation += getTaoValue( currentAnt.getCurrentNode(), currNeighbour );
            }

            boolean selectionDecided = false;            
            Node firstRandomValidNode = null;
            iter = neighbours.iterator();
            while( iter.hasNext() )
            {            
                Node currNeighbour = iter.next();            
                
                if( currNeighbour.isValid() == false )
                    continue;
                
                if( firstRandomValidNode == null )
                    firstRandomValidNode = currNeighbour;
                double currentProbability = RandomGenerator.genDouble();

                double currentPValue = getTaoValue( currentAnt.getCurrentNode(), currNeighbour );

                currentPValue = currentPValue / neighbourhoodSumation;
                if( currentPValue >= currentProbability )
                {
                    selectedNeighbour = currNeighbour;
                    selectionDecided = true;
                    break;
                }                                    
            }
            if( selectionDecided == false )
                selectedNeighbour = firstRandomValidNode;
        }
                        
        return selectedNeighbour;
        
    }
    
    public double getTaoValue(Node source, Node dest)
    {
        double result  = 0;
        double phenomone = this.network.getPhenomone( source, dest );
        double heuristic = trafficMatrix.get(dest.transmitter, dest.receiver );
        result = ( Math.pow( phenomone, AICSParams.alfa) * heuristic );        
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
        double oldPhenome;
        double newPhenome = 0;
        double increment;
        
        
        for(int i = 0; i < this.network.getNumOfArc(); i++ )
        {
            Arc currentArc = this.network.getArc(i);
            double phenomone = currentArc.getPhenomone();            
            phenomone = (1 - AICSParams.evaporationConstant) * phenomone;
            
            phenomone = checkPhenomone(phenomone);
            currentArc.setPhenomone(phenomone);            
        }

        for( int i = 0; i < path.size() - 1; i++ )
        {
            Arc currentArc = this.network.getArc(path.get(i), path.get(i+1));
            double phenomone = currentArc.getPhenomone();
            increment = fitness;
            phenomone += increment;            
            phenomone = checkPhenomone(phenomone);
            currentArc.setPhenomone(phenomone);
        }
    }   
    
    public void updateStartNodeHeuristic( ArrayList<Node> path , double fitness )
    {
        for(int i = 0; i < this.startNodeHeuristic.length; i++)
        {
            this.startNodeHeuristic[i] = this.startNodeHeuristic[i] * ( 1.0 - AICSParams.evaporationConstant);
        }
        
        Node startNode = path.get(0);
        
        this.startNodeHeuristic[ startNode.getIndex() ] += (double)(fitness);
    }
    
    public double checkPhenomone(double inPhenomone)
    {
        if( inPhenomone < AICSParams.getMinPhenomone() )
        {
            if( AICSParams.DEBUG_ON )
            {
                System.out.println("Min Value is set " + inPhenomone + " -> " + AICSParams.getMinPhenomone());
            }
            return AICSParams.getMinPhenomone();
        }
        if( inPhenomone > AICSParams.getMaxPhenomone() )
        {                    
            if( AICSParams.DEBUG_ON )
            {
               System.out.println("Max Value is set " + inPhenomone + " -> " + AICSParams.getMaxPhenomone());
            }
            return AICSParams.getMaxPhenomone();   
        }
        return inPhenomone;
    }
    
    public void initPhenomones()
    {
        for(int i = 0; i < this.network.getNumOfArc(); i++ )
        {
            Arc currentArc = this.network.getArc(i);
            currentArc.setPhenomone( AICSParams.getInitialValueOfPhenomone() );
        }
    }
    
    public void initStartNodeHeuristic()
    {
        for( int i = 0; i < this.startNodeHeuristic.length; i++ )
        {
            this.startNodeHeuristic[i] = AICSParams.getInitialValueOfPhenomone();
            
            int transmitter = i / VTDesignParams.numOfNode;
            int receiver = i - transmitter * VTDesignParams.numOfNode;
            
            if( transmitter == receiver )
                this.startNodeHeuristic[i] = Double.MIN_VALUE;
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
        long endTime, startTime;
        startTime = TimeUtility.getUserTimeAsMiliSeconds();
        endTime = startTime;
        double initialFitness = 100;
        int representationSize = VTDesignParams.numOfNode * VTDesignParams.numOfTrans;
        
        AICSParams.initPhenomoneValues(initialFitness, representationSize);
        scaleHeuristicAccordingToFitness(initialFitness);
        
        this.initPhenomones();               
        this.initStartNodeHeuristic();
        
        /*Reeanble network*/        
        for( int i = 0; i < this.network.getNumOfNode(); i++ )
        {
            this.network.getNode(i).transmitter = i / VTDesignParams.numOfNode;
            this.network.getNode(i).receiver = i - (this.network.getNode(i).transmitter * VTDesignParams.numOfNode );            
        }        
                        
        
        while( !terminate )
        {
            matrixRep = new Matrix( VTDesignParams.numOfNode, VTDesignParams.numOfNode );                            
            
            /*matrixRep will be modified in this function for the first selected node*/
            
            this.positionAnts();                        

            /*Reeanble network*/            
            Iterator<Node> iter =this.network.getNodes().iterator();
            while( iter.hasNext() )
            {                
                Node currentNode = iter.next();
                currentNode.setValid(true);
                if( currentNode.receiver == currentNode.transmitter )
                {
                    currentNode.setValid( false );    
                }
            }

            /*Start node has already stared */
            for( int antIndex = 0 ; antIndex < this.ants.size(); antIndex++ )
            {
                int i;
                for( i = 1; i < this.numOfMoves; i++)
                {                                   
                    Node nextNode = this.decideNextNode(antIndex);                                                                                
                    if( AICSParams.DEBUG_ON )
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
                        break;
                    }   
                    
                    double value = matrixRep.get( nextNode.transmitter, nextNode.receiver );
                    value++;
                    matrixRep.set( nextNode.transmitter, nextNode.receiver, value );                    
     
                    nextNode.setValid( false );                                       
                    
                    
                    boolean transmitterLeft = isTransmitterLeft( matrixRep, nextNode.transmitter );
                    boolean receiverLeft = isReceiverLeft( matrixRep, nextNode.receiver );
            
                    /*If no receiver or transmitter left, set the infeasible ones*/
                    if( !transmitterLeft || !receiverLeft )
                    {
                        Iterator<Node> feasibilityIter = network.getNodes().iterator();
                        while( feasibilityIter.hasNext() )
                        {                            
                            Node feasibleCheckEntry = feasibilityIter.next();
                            if( !transmitterLeft && feasibleCheckEntry.transmitter == nextNode.transmitter)
                            {
                                feasibleCheckEntry.setValid(false);
                            }
                            if( !receiverLeft && feasibleCheckEntry.receiver == nextNode.receiver )
                            {
                                feasibleCheckEntry.setValid(false);
                            }                    
                        }
                    }                    
                        
                }
                if( i > VTDesignParams.numOfNode * VTDesignParams.numOfTrans )
                {
                    int transmitters [] = new int[VTDesignParams.numOfNode];
                    int receivers[] = new int[VTDesignParams.numOfNode];
                    Arrays.fill(transmitters, 0);
                    Arrays.fill(receivers, 0);
                    for( int antK = 0; antK < this.ants.size(); antK++ )
                    {
                        Ant currentAnt = this.ants.get(antK);
                        System.out.println("Ant "+ antK);
                        for(int pathI = 0; pathI < currentAnt.getPath().size(); pathI++)
                        {
                            System.out.print( currentAnt.getPath().get(pathI).transmitter + ", " + currentAnt.getPath().get(pathI).receiver + "\n");
                            transmitters[ currentAnt.getPath().get(pathI).transmitter ]++;
                            receivers[ currentAnt.getPath().get(pathI).receiver ]++;
                        }                    
                        System.out.println("");
                    }

                    for (int j = 0; j < transmitters.length; j++) {
                        System.out.println( j + ". transmitter" + transmitters[j]);
                    }
                    
                    for (int j = 0; j < receivers.length; j++) {
                        System.out.println( j + ". receiver" + receivers[j]);
                    }
                    SystemFault sf = new SystemFault( SystemFault.SEVERE_ERROR );
                    sf.setInspectIntParam(0, i);
                    throw sf;
                }
            
            }
            
            try{
                for( int i = 0; i < this.network.getNumOfNode(); i++ )
                {
                    if( this.network.getNode(i).isValid() )
                        throw new Exception();    
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();                
            }
            
            /*Print paths of ant*/
            if( AICSParams.DEBUG_ON )
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
            double currentFitness;
            for( int i = 0; i < this.ants.size(); i++)
            {
                ArrayList<Node> pathOfCurrentAnt = this.ants.get(i).getPath();
                
                /*Convert the path to the solution*/
                Solution currentSolution = convertPathToSolution(null, pathOfCurrentAnt);                

                this.fitnessHandler.calculateFitness(currentSolution);                                
                
                currentSolution.setIterationNumber( numOfRun );
                getResultSaver().addIterSolution( currentSolution );
                if( this.fitnessComparator.isBetter(currentSolution, bestSolution) )
                {         
                    bestSolution = currentSolution.clone();
                    bestPath = new ArrayList();
                    for( int j = 0; j < pathOfCurrentAnt.size(); j++ )
                    {
                        bestPath.add( pathOfCurrentAnt.get(j) );
                    }
                }
                                
                if( this.fitnessComparator.isBetter(currentSolution, globalBestSolution) )
                {                    
                    globalBestPath = new ArrayList();
                    for( int j = 0; j < pathOfCurrentAnt.size(); j++ )
                    {
                        globalBestPath.add( pathOfCurrentAnt.get(j) );
                    }
                    globalBestSolution = currentSolution.clone();                                        
                    
                    lastGlobalBestFoundIteration = numOfRun;
                    
                    AICSParams.initPhenomoneValues(globalBestSolution.getFitness(), pathOfCurrentAnt.size() );
                    
                  
                       // System.out.println("Global Best Found " + numOfRun + "fitness: " + globalBestSolution.getFitness());
                        /*for(int pathI = 0; pathI < globalBestPath.size(); pathI++)
                        {
                            System.out.print( globalBestPath.get(pathI) + ", ");
                        }                    
                        System.out.println("");                  
                        
                        double fitness = globalBestSolution.getFitness();
                        System.out.println("FITNESS1: " + fitness );                        
                        //System.out.println(this.network);*/
                            
                }
                
                getResultSaver().setBestSolution( globalBestSolution );
            }
        
            /*If no improvement occurs for NUMBER_OF_SEQUENTIAL_NON_IMPROVEMENT_ITERATION*/
            if( (numOfRun-lastGlobalBestFoundIteration) == AICSParams.NUMBER_OF_SEQUENTIAL_NON_IMPROVEMENT_ITERATION )
            {
                lastGlobalBestFoundIteration = numOfRun;
                this.initPhenomones();                
                System.out.println("Phenomone values are reinitialized at " + numOfRun);
            }
            if(bestPath == null)
                throw new SystemFault(SystemFault.SEVERE_ERROR);
            
            this.globalUpdate( globalBestPath, globalBestSolution.getFitness());
            this.updateStartNodeHeuristic( globalBestPath, globalBestSolution.getFitness() );
            
            endTime = TimeUtility.getUserTimeAsMiliSeconds();
            if( (endTime - startTime) >= AICSParams.duration )
            {
                terminate = true;                
            }
            numOfRun++;
        }        
        if( AICSParams.DEBUG_ON )
            System.out.println(this.network);
        
        this.bestSolution = globalBestSolution.clone();
        
        this.resultSaver.setRunningTime( endTime - startTime );
        
        return true;        
    }
       
    
    public Solution convertPathToSolution(Solution constantSolution, ArrayList<Node> path)
    {       
        ArrayList<LightPath>                sortedLightPaths = new ArrayList();
        
        int[] testArray = {CA1, UT, NJ, MD,TX, NE, GA, MD, WA, UT, NE, GA, NE, IL, PA, MI, WA, GA, MI, MD, CA1, PA, MI, MD, CA2, CO, TX, IL, CA1, NE, NY, NJ, WA, UT, CO, NJ, CA2, IL, NY, NJ, CO, TX, PA, NY, WA, TX, IL, PA, CA2, GA, MI, NY ,CA1, CA2, UT, CO};
        
        for( int i = 0; i < VTDesignParams.numOfNode; i++ )
        {
            for( int j = 0; j < VTDesignParams.numOfRecv; j++ )
            {
                 
                /*Set s-d of the ligthpath and add it to the sorted lightpath list*/
                LightPath currentLightPath = new LightPath();
                int phsyicalLinkslpArray[] = new int[2];
                phsyicalLinkslpArray[0] = i;
                phsyicalLinkslpArray[1] = testArray[ (i*VTDesignParams.numOfRecv) + j];
                currentLightPath.setPhysicalLinks( phsyicalLinkslpArray );
                sortedLightPaths.add( currentLightPath );                       
            }
        }  
        
//        for( int i = 0; i < path.size(); i++ )
//        {
//            Node nextNode = path.get(i);            
//            /*Set s-d of the ligthpath and add it to the sorted lightpath list*/
//            LightPath currentLightPath = new LightPath();
//            int phsyicalLinkslpArray[] = new int[2];
//            phsyicalLinkslpArray[0] = nextNode.transmitter;
//            phsyicalLinkslpArray[1] = nextNode.receiver;
//            currentLightPath.setPhysicalLinks( phsyicalLinkslpArray );
//            sortedLightPaths.add( currentLightPath );                       
//        }      
        
        Solution currentSolution = new Solution();
        LigthPathArrayRepresentation lpArrayRep = new LigthPathArrayRepresentation();

        LightPath lpArray[] = new LightPath[ sortedLightPaths.size() ];
        for( int lpIndex = 0; lpIndex < lpArray.length; lpIndex++ )
        {
            lpArray[lpIndex] = sortedLightPaths.get(lpIndex);            
        }

        lpArrayRep.setLigthpaths( lpArray );                

        /*Set the representation of lightpaths*/
        currentSolution.setRepresentation(lpArrayRep);                 
        
        return currentSolution;
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

    public boolean isTransmitterLeft( Matrix result, int transmitter )
    {
        int currNumOfUsedTrans = (int) result.countValuesInARow( transmitter );
        
        if( currNumOfUsedTrans == VTDesignParams.numOfTrans )
        {
            return false;
        }
        else if( currNumOfUsedTrans > VTDesignParams.numOfTrans )
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
        
        if( currNumOfUsedRecv == VTDesignParams.numOfRecv )
        {
            return false;
        }
        else if( currNumOfUsedRecv > VTDesignParams.numOfRecv )
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

    public AICSResultSaver getResultSaver() {
        return resultSaver;
    }
      
}
