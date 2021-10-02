/*
* Filename: KShortestPathMatrix.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

import hh.algorithm.on.com.VTDesignParams;
import java.util.Arrays;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class KShortestPathMatrix extends Representation implements Cloneable
{    
    private int currPathIndex[];        /*i: The index of a pair in the kShortestPath list*/
    
    private boolean currPathValid[];
    
    private int kShortestPaths[][][];   /*i. pair, j. path*/
    
    private Matrix distanceMatrix;
    
    private KShortestPathMatrix()
    {
        
    }
    
    public int getNumOfPath()
    {
        return VTDesignParams.kShortestPath;
    }
    
    /**
     * First create KShortestPathMatrix, then use preparePaths to fill the kShortestPath
     */
    public KShortestPathMatrix(Matrix inDistanceMatrix, int sdPairs[][]) throws SystemFault
    {
        int currNumOfKShortestPath = 0;
        this.distanceMatrix = (Matrix)inDistanceMatrix.clone();
        /*Initial paths include start and end nodes*/
        this.kShortestPaths = new int[ sdPairs.length ][VTDesignParams.kShortestPath][2];    
        for( int i = 0 ; i < sdPairs.length; i++ )
        {                
                for(int k = 0 ; k < VTDesignParams.kShortestPath; k++)
                {
                    this.kShortestPaths[ currNumOfKShortestPath ][k][0] = sdPairs[i][0];
                    this.kShortestPaths[ currNumOfKShortestPath ][k][1] = sdPairs[i][1];
                }  
                currNumOfKShortestPath++;            
        }
        
        if( currNumOfKShortestPath != this.kShortestPaths.length )
        {
            throw new SystemFault( SystemFault.SEVERE_ERROR );
        }
        this.currPathIndex = new int[ distanceMatrix.getNumOfColumn() * (distanceMatrix.getNumOfRow() - 1) ];        
        this.currPathValid = new boolean[ distanceMatrix.getNumOfColumn() * (distanceMatrix.getNumOfRow() - 1)  ];
        Arrays.fill(currPathValid, true);
    }
    
    public void preparePaths() throws SystemFault
    {
        int lastPath[];
        
        for(int i = 0 ; i < this.getNumOfPairs(); i++)
        {
            Matrix currMatrix = (Matrix)distanceMatrix.clone();
            lastPath = null;
            int transmiter = this.getStartNode(i);
            int receiver = this.getEndNode(i);
            for( int j = 0 ; j < VTDesignParams.kShortestPath; j++ )
            {
                DijsktraShortestPath shortestPath = new DijsktraShortestPath();
                
                this.kShortestPaths[i][j] = shortestPath.getPath( transmiter, receiver, currMatrix);
                
                if( this.kShortestPaths[i][j] == null)
                {
                    this.kShortestPaths[i][j] = lastPath;   /*If no new path exist, copy the last found one*/
                    /* WARNING if( lastPath == null )
                    {
                        SystemFault sf = new SystemFault(SystemFault.SEVERE_ERROR);
                        sf.setInspectIntParam(0, i);
                        sf.setInspectIntParam(1, j);
                        throw sf;
                    }*/
                    continue;
                }
                
           
                lastPath = this.kShortestPaths[i][j];
                /*No such path exist*/
                if( currMatrix.usePath( this.kShortestPaths[i][j] ) == false)
                {
                    SystemFault sf = new SystemFault(SystemFault.SEVERE_ERROR);
                    sf.setInspectIntParam(0, i);
                    sf.setInspectIntParam(1, j);
                    throw sf;
                }
            }
            
        }
        
        this.distanceMatrix = null; /*Remove distance Matrix, we should not use it after this step*/
    }
    
    public int[][] getSDArray() 
    {
        int result[][];
        
        result = new int[this.getNumOfPairs()][2];
        
        for(int i = 0; i < this.getNumOfPairs(); i++ )
        {
            result[i][0] = this.getStartNode(i);
            result[i][1] = this.getEndNode(i);
        }
        return result;
    }
    
    public int getNumOfPairs()
    {
        return this.kShortestPaths.length;
    }
    
    public int getStartNode(int i)
    {
        return this.kShortestPaths[i][ this.currPathIndex[i] ][0];
    }
    
    public int getEndNode(int i)
    {
        int endIndex = this.kShortestPaths[i][ this.currPathIndex[i] ].length  - 1;
        return this.kShortestPaths[i][ this.currPathIndex[i] ][ endIndex ];        
    }

    public void changeShortestPath(int sdIndex, int newPathIndex)
    {
        this.currPathIndex[sdIndex] = newPathIndex;
    }
    
    public void reArrange(int sdPairs[][]) throws SystemFault
    {
        int x,y;
        int currIndex = 0;
        
        int localKShortestPaths[][][];
        
        localKShortestPaths = new int[this.getNumOfPairs()][VTDesignParams.kShortestPath][];
        
        if( sdPairs.length != this.getNumOfPairs() || sdPairs[0].length != 2)
        {
            throw new SystemFault( SystemFault.SEVERE_ERROR );
        }
        
        for(int i = 0; i < sdPairs.length; i++ )
        {
            x = sdPairs[i][0];
            y = sdPairs[i][1];
            
            /*Find the index of x,y pair*/
            for(int j = 0 ; j < this.getNumOfPairs(); j++ )
            {
                if( this.getStartNode(j) == x && this.getEndNode(j) == y )
                {
                    for(int k = 0; k < VTDesignParams.kShortestPath; k++ )
                    {
                        localKShortestPaths[currIndex][k] = this.kShortestPaths[j][k];
                    }
                    currIndex++;
                }
                
            }
        }
        
        if( currIndex != this.getNumOfPairs() )
        {
            throw new SystemFault(SystemFault.SEVERE_ERROR);
        }
        
        /*Copy the local variable as kShortestPath*/
        this.kShortestPaths = localKShortestPaths;
    }
    
    public void setPathValidity(int inIndex, boolean inParam)
    {
        this.currPathValid[inIndex] = inParam;
    }
    
    public boolean isPathValid(int inIndex)
    {
        return this.currPathValid[ inIndex ]; 
    }
    
    public int[] getPath(int i) throws SystemFault
    {
        if( this.currPathValid[i] == false)
        {
            throw new SystemFault(SystemFault.SEVERE_ERROR);
        }
        return this.kShortestPaths[i][ this.currPathIndex[i] ];
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Representation clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}


