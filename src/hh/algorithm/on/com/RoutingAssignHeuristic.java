/*
* Filename: RoutingAssignHeuristic.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.com.Matrix;
import hh.algorithm.com.Representation;
import hh.algorithm.com.SystemFault;
import hh.algorithm.on.qTool.QTool;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class RoutingAssignHeuristic 
{   
    /*RESULT OF THE HEURISTIC*/
    private LightPath[]         routedLigthPaths;
    
    /*PARAMETERS THAT ARE CHANGED IN EACH ITERATION*/
    private Matrix physicalTopology;  /*Each physical link will be represented by the number of wavelength */
    
    private Matrix costTopology;
    
    /*CONSTANT PARAMS*/
    private Matrix constantDistanceMatrix;
    
    private int numOfWavelength;
            
    private LightPathRouterHeuristic lightPathRouter;
    
    private boolean iterative = false;
    
    private QTool                       qtool;
    
    private WavelengthAssignHeuristic   wavelengthAssignHeuristic;
                

    
    public RoutingAssignHeuristic( final int numOfWavelength) 
    {
        this.numOfWavelength = numOfWavelength;
    }
    
    private void setPhysicalTopology(Matrix distanceMatrix)
    {
        physicalTopology = distanceMatrix.createConnMatrix();
        
        getPhysicalTopology().scale(this.numOfWavelength);
           
    }
    
    private void setCostTopology(Matrix distanceMatrix)
    {        
        this.costTopology = (Matrix)distanceMatrix.clone();
       // this.initCostTopology();
    }
    
    public void initCostTopology()
    {
        double maxEntry = this.getConstantDistanceMatrix().getMaxValueEntry();
        double newCostMatrix[][] = this.costTopology.getData();
        double distanceMatrix[][] = this.getConstantDistanceMatrix().getData();
        
        for (int i = 0; i < newCostMatrix.length; i++) {
            for (int j = 0; j < newCostMatrix[i].length; j++) {
                /*CHANGED*/
                /*newCostMatrix[i][j] = this.w2 * distanceMatrix[i][j] / maxEntry;   */
                newCostMatrix[i][j] = distanceMatrix[i][j];
            }
        }
        
    }

    /*This function should be called for the first time of a topology*/
    public void prepareToRoute()
    {
            /*Reset the cost topology that Disjkstra runs on, and physical topology*/
            this.setPhysicalTopology(getConstantDistanceMatrix());
            this.setCostTopology(getConstantDistanceMatrix());                    
    }
    
     /**
     * @return Returns false, if it cannot find the route for any of the s-d pairs
     */
    public boolean route(Representation sdPairs)
    {
        boolean result;
      
        if( lightPathRouter != null )
        {
            int [][]routedPaths = lightPathRouter.execute(this, sdPairs.getSDArray());
            if( routedPaths == null )
            {
                this.routedLigthPaths = null;
                return false;
            }
            this.routedLigthPaths = new LightPath[ routedPaths.length ];
            
            for (int i = 0; i < routedPaths.length; i++) 
            {
                LightPath currLightPath = new LightPath();
                currLightPath.setPhysicalLinks( routedPaths[i] );
                this.routedLigthPaths[i] = currLightPath;
            }
            
            return true;
        }
        this.routedLigthPaths = null;
        return false;
    }
        
    
    public void setLightPathRouter(LightPathRouterHeuristic router)
    {
        this.lightPathRouter = router;
    }

    public Matrix getCostTopology() {
        return costTopology;
    }
    
    /**
     * Important: Returns true if the link can be established
     * Updates the cost topology and physical topology
     * @param path: 1->4->5 = (1,4) (4,5)
     * @return If returns false, it means no wavelength is exist in this link
     * 
     */
    public boolean usePath( int path[] ) throws SystemFault
    {
        double [][]matrix = this.getPhysicalTopology().getData();
        double [][]costMatrix = this.costTopology.getData();
        double [][]distanceMatrix = this.getConstantDistanceMatrix().getData();
        
        for( int i = 0; i < path.length - 1; i++ )
        {            
            int transmit = path[i];
            int receive = path[i+1];  
            /*Path cannot be established, It is a control check. It is not expected to be occured*/
            /*Because path sould be established, if shortest path algorithm finds one*/
            if ( (--matrix[transmit][receive]) < 0  )
            {
                /*WARNING*/
                /*throw new SystemFault(SystemFault.SEVERE_ERROR);*/
                return false;
            }
            if( matrix[transmit][receive] == 0)
            {
                costMatrix[transmit][receive] = 0;                
            }
            
            else
            {
                /*CHANGED*/
                /*
                int numOfVlinkUsed = this.numOfWavelength - (int)matrix[transmit][receive];
                costMatrix[transmit][receive] =  this.w1 * (numOfVlinkUsed) + this.w2 * (distanceMatrix[transmit][receive] / this.distanceMatrix.getMaxValueEntry());
                costMatrix[receive][transmit] = costMatrix[transmit][receive];
                 */
                costMatrix[transmit][receive] = distanceMatrix[transmit][receive];                
            }
        }

        return true;
    }
    
    public int getCongestedPathCost(int []inPath)
    {
        double [][]matrix = this.getPhysicalTopology().getData();        
        int minLeftWavelength = Integer.MAX_VALUE;        
        
        for( int i = 0; i < inPath.length - 1; i++ )
        {
            int transmit = inPath[i];
            int receive = inPath[i+1]; 
            
            if( matrix[transmit][receive] < minLeftWavelength )
            {                
                minLeftWavelength = (int)matrix[transmit][receive];
            }
            
        }
        
        return ( this.numOfWavelength - minLeftWavelength );
    }
    
    
    /**
     * Topology of the cost should be updated after a path is found
     * --If no physical link is remained, the entry in the cost topology should be made 0
     * --If path continues to exist, new cost should be calculated
     * 
     */
    private void updateCostTopology()
    {
        double [][]matrix = this.getPhysicalTopology().getData();
        double [][]distanceMatrix = this.getConstantDistanceMatrix().getData();
        
        double [][]costMatrix = this.costTopology.getData();
        
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                /*If there is no available link*/
                if( matrix[i][j] == 0)
                {
                    costMatrix[i][j] = 0;
                }
                else
                {
                    int numOfVlinkUsed = this.numOfWavelength - (int)matrix[i][j];
                    /*CHANGED*/
                    /*costMatrix[i][j] =  this.w1 * (numOfVlinkUsed) + this.w2 * (distanceMatrix[i][j] / this.distanceMatrix.getMaxValueEntry());*/
                    costMatrix[i][j] =  distanceMatrix[i][j];
                }
            }
        }
    }

    public LightPath[] getRoutedLigthPaths() {
        return routedLigthPaths;
    }
    
    public LightPath getRoutedLigthPaths(final int i) {
        return this.routedLigthPaths[i];
    }    
    
    public boolean checkConstraints()
    {
        int matrix[][] = new int[physicalTopology.getNumOfRow()][physicalTopology.getNumOfColumn()];
        
        for(int i = 0; i < matrix.length; i++)
        {
            Arrays.fill(matrix[i], 0);
        }
                
        for (int i = 0; i < routedLigthPaths.length; i++) 
        {
            int []routedPaths = routedLigthPaths[i].getPhysicalLinks();
            
            for (int j = 0; j < routedPaths.length - 1; j++) 
            {
                int transmit,receive;
                transmit = routedPaths[j];    
                receive = routedPaths[j+1];
                matrix[transmit][receive]++;                
            }            
        }
        
        for(int i = 0; i < matrix.length; i++ )
        {
            for(int j = 0; j < matrix[i].length; j++ )
            {
                if( matrix[i][j] > this.numOfWavelength )
                    return false;
            }
        }
        return true;
    }
    
    public int getNumOfRoutedPaths()
    {
        return this.routedLigthPaths.length;
    }

    public Matrix getConstantDistanceMatrix() {
        return constantDistanceMatrix;
    }

    public void setConstantDistanceMatrix(Matrix constantDistanceMatrix) {
        this.constantDistanceMatrix = constantDistanceMatrix;
    }

    public boolean isIterative() {
        return iterative;
    }

    public void setIterative(boolean iterative) {
        this.iterative = iterative;
    }

    public QTool getQtool() {
        return qtool;
    }

    public void setQtool(QTool qtool) {
        this.qtool = qtool;
    }

    public Matrix getPhysicalTopology() {
        return physicalTopology;
    }

    public WavelengthAssignHeuristic getWavelengthAssignHeuristic() {
        return wavelengthAssignHeuristic;
    }

    public void setWavelengthAssignHeuristic(WavelengthAssignHeuristic wavelengthAssignHeuristic) {
        this.wavelengthAssignHeuristic = wavelengthAssignHeuristic;
    }
    
    
}


