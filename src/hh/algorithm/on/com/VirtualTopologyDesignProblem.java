/*
* Filename: VirtualTopology.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.com.DijsktraShortestPath;
import hh.algorithm.com.FitnessHandler;
import hh.algorithm.com.Matrix;
import hh.algorithm.com.PrufferNumber;
import hh.algorithm.com.RepairHandler;
import hh.algorithm.com.Representation;
import hh.algorithm.com.Solution;
import hh.algorithm.com.SystemFault;
import hh.algorithm.hyperHeuristic.com.HyperHeuristicAlgorithmParams;
import hh.algorithm.representation.LigthPathArrayRepresentation;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class VirtualTopologyDesignProblem implements FitnessHandler, RepairHandler
{    
    /*CONSTANT PARAMETERS THAT ARE USED BY HEURISTICS*/
    /*THESE PARAMETERS SHOULD NOT BE CHANGED*/
    private Matrix      trafficMatrix;
    
    private Matrix      distanceMatrix;
    
    private int numOfTrans[];
    
    private int numOfRecv[];
    
    /*HEURISTICS THAT SOLVE THE SUB-PROBLEMS*/
    private RoutingAssignHeuristic routingHeuristic;
    
    private WavelengthAssignHeuristic wavelengthAssignHeuristic;
    
    private TraficFlowHeuristic trafficFlowHeuristic;
    
    /*MEMBERS THAT CREATED AFTER SOLVING APPROPRIATE SUB PROBLEMS*/
    
    private Representation  sdPairs;
    
    private LightPath   ligthpaths[];
    
    private TrafficFlow trafficFlows[];
   
    
    private VirtualTopologyDesignProblem()
    {
        
    }
    
    public VirtualTopologyDesignProblem(int numberOfNode, String trafficFileName, String distanceFileName) throws FileNotFoundException, IOException
    {
        this();
        numOfRecv = new int[numberOfNode];
        numOfTrans = new int[numberOfNode];        
        
        setAllRecv(VTDesignParams.numOfRecv);
        setAllTrans(VTDesignParams.numOfTrans);
        
        Matrix trafficMatrix = new Matrix();
        trafficMatrix.setData( Matrix.readFromFile(new File(trafficFileName)));
        trafficMatrix.scale(10);    
        
        setTrafficMatrix(trafficMatrix);
        
        distanceMatrix = new Matrix();
        distanceMatrix.setData( Matrix.readFromFile(new File(distanceFileName)));
        distanceMatrix.makeSymetrix();
                
        /*Create Routing Heuristic*/
        routingHeuristic = new RoutingAssignHeuristic(VTDesignParams.numOfWavelengths);
        routingHeuristic.setLightPathRouter( new ShortestPathRouter() );                
        routingHeuristic.setConstantDistanceMatrix((Matrix)distanceMatrix.clone());                       
        
        /*Create Wavelength Assignment Heuristic*/
        wavelengthAssignHeuristic = new WavelengthAssignHeuristic(VTDesignParams.numOfWavelengths);                
        wavelengthAssignHeuristic.setWavelengthTopology((Matrix)distanceMatrix.clone());
        wavelengthAssignHeuristic.setWavelengthAssigner( new FirstFitWavelengthAssigner() );
        
        /*Create Traffic Flow Heuristic*/
        trafficFlowHeuristic = new TraficFlowHeuristic(numberOfNode);                                     
    }    
    
    public void setLigthpaths( LightPath ligthpaths[] )
    {
        this.ligthpaths = ligthpaths;
    }
    
    public void setAllTrans(final int numOfTransForEach)
    {
        Arrays.fill(this.numOfTrans, numOfTransForEach);
    }
    
    public void setAllRecv(final int numOfRecvForEach)
    {
        Arrays.fill(this.numOfRecv, numOfRecvForEach);
    }    
    
    public TrafficFlow[] assignFlows() throws SystemFault
    {
        TrafficFlow assignedFlows[];
        this.trafficFlowHeuristic.setTrafficMatrix(this.trafficMatrix);
        this.trafficFlowHeuristic.setLightPaths( this.ligthpaths );
        assignedFlows = this.trafficFlowHeuristic.assign();
        if ( assignedFlows == null )
        {
            return null;
            /*throw new SystemFault(SystemFault.SEVERE_ERROR);*/
        }
        return assignedFlows;
    }
    

    public Matrix getTrafficMatrix() {
        return trafficMatrix;
    }

    public void setTrafficMatrix(Matrix trafficMatrix) {
        this.trafficMatrix = trafficMatrix;
    }
    
    public boolean solve(Representation representation) throws SystemFault
    {
        
        this.setSdPairs(representation);
        
        routingHeuristic.prepareToRoute();
        if( routingHeuristic.route(this.getSdPairs()) == false)
        {
            VTDesignResult.infeasibleRouting++;
            return false;
        }

        if( routingHeuristic.checkConstraints() == false )
        {           
            VTDesignResult.infeasibleRoutingConstraint++;
            return false;
        }               

        LigthPathArrayRepresentation    arrayRep = new LigthPathArrayRepresentation();
        arrayRep.setLigthpaths( routingHeuristic.getRoutedLigthPaths() );
        if ( wavelengthAssignHeuristic.assign( arrayRep, true ) == false )
        {
            VTDesignResult.infeasibleWavelengthAssign++;
            return false;
        }

        if( VTDesignParams.DEBUG_ON )
        {
            System.out.println(wavelengthAssignHeuristic);
        }

        LightPath lightpaths[] = new LightPath[ routingHeuristic.getNumOfRoutedPaths() ];
        for(int i = 0; i < lightpaths.length; i++)
        {
            lightpaths[i] = new LightPath(VTDesignParams.wavelengthCapacity);
            /*They are all indexed with the same index*/
            lightpaths[i].setPhysicalLinks( routingHeuristic.getRoutedLigthPaths(i).getPhysicalLinks() );
            lightpaths[i].setWavelengthId( wavelengthAssignHeuristic.getWavelength(i));
        }                

        this.setLigthpaths(lightpaths);              

        
        this.trafficFlows = this.assignFlows();
        if (  this.trafficFlows == null )
        {
            VTDesignResult.infeasibleFlowAssign++;
            return false;
        }

        if( VTDesignParams.DEBUG_ON )
        {
            for (int i = 0; i < this.trafficFlows.length; i++) 
            {
                System.out.println(this.trafficFlows[i] + "\n");
            }
        }
        return true;
    }       

    public double calculateFitness(Solution solution) throws SystemFault 
    {
        /*Rows are source nodes, columns are Destionation nodes*/                
        if( this.solve( solution.getRepresentation() ) == false)
        {
            if( HyperHeuristicAlgorithmParams.isMinimization )
                return Double.MAX_VALUE;
            else
                return Double.MIN_VALUE;
        }
        
        double maxFlowForALink = TrafficFlow.getMaxLinkFlow( this.trafficFlows );
        
        double capacity = VTDesignParams.wavelengthCapacity;
        double scaleFactor =  capacity / maxFlowForALink;
        
        return scaleFactor;
    }
    
    
    public int [][][] generateSolution( final int numOfSolution ) throws SystemFault, FileNotFoundException, IOException, Exception
    {
        int currNumOfSolution = 0;
        int solutions[][][];
        
        solutions = new int[numOfSolution][][];
        
        while( currNumOfSolution != numOfSolution )
        {
            if(this.generateSolution() == false)
                continue;
            
            solutions[currNumOfSolution] = this.getSdPairs().getSDArray().clone();
            currNumOfSolution++;
        }
        
        return solutions;
    }
    
    public boolean generateSolution( ) throws SystemFault, FileNotFoundException, IOException, Exception
    {
        double matrix[][];
        int prufferMatrix[][];                       

        /*Set the source and destionation nodes*/
        PrufferNumber prufferNumber = new PrufferNumber( VTDesignParams.numOfNode );

        prufferMatrix = prufferNumber.generateConnectionMatrix();

        matrix = new double[prufferMatrix.length][prufferMatrix[0].length];

        for (int i = 0; i < prufferMatrix.length; i++) {
            for (int j = 0; j < prufferMatrix[i].length; j++) {
                matrix[i][j] = prufferMatrix[i][j];

            }

        }
                
        Matrix connMatrix = new Matrix();
        connMatrix.setData(matrix);

        if ( connMatrix.satisfyRowConstraint(VTDesignParams.numOfTrans) == false )
        {
            System.out.println("Row Constraint cannot be supplied");
            return false;
        }

        if( connMatrix.satisfyColumnConstraint(VTDesignParams.numOfRecv) == false)
        {
            System.out.println("Column Constraint cannot be supplied");           
            return false;
        }                        
        
        boolean result = this.solve(connMatrix);
        
        return result;
    }

    public Representation getSdPairs() {
        return sdPairs;
    }

    public void setSdPairs(Representation sdPairs) {
        this.sdPairs = sdPairs;
    }

    /*Modify the chromosome*/
    public boolean repair(Solution chromosome) throws SystemFault 
    { 
       /* double sdPairs[][];

        sdPairs = new double[VTDesignParams.numOfNode][VTDesignParams.numOfNode];
        
        for (int i = 0; i < sdPairs.length; i++) {
            Arrays.copyOf(sdPairs[i], 0);
        }
        for(int j = 0; j < VTDesignParams.numOfNode; j++ )
        {
            for( int k = 0; k < VTDesignParams.numOfTrans; k++ )
            {
                int transmit = j;
                int receive = (Integer)chromosome.getGene(j*VTDesignParams.numOfTrans+k).getValue();
                
                sdPairs[transmit][receive]++;
                
                if( sdPairs[transmit][receive] > 1)
                    throw new SystemFault(SystemFault.SEVERE_ERROR);              
            }
        }
        
        Matrix matrix = new Matrix();
        matrix.setData(sdPairs);
        if( matrix.satisfyColumnConstraint(VTDesignParams.numOfRecv) == false)
        {
            System.out.println("Column Constraint cannot be supplied");           
            return false;
        }
              
        int currIndex = 0;
        for(int j = 0; j < VTDesignParams.numOfNode; j++ )
        {
            for( int k = 0; k < VTDesignParams.numOfNode; k++ )
            {                            
                if( matrix.get(j, k) > 1 )
                    throw new SystemFault(SystemFault.SEVERE_ERROR);
                
                /*first four of 1, then four of 2, then 3*/
    /*            if( matrix.get(j, k) == 1 )
                {
                    chromosome.getGene(currIndex).setValue(new Integer(k));   
                    currIndex++;
                }                               
                
            }
        }
        
        if( currIndex != chromosome.getNumOfGenes() )
            throw new SystemFault(SystemFault.SEVERE_ERROR);
        */
        return true;
    }

}
