/*
* Filename: DijsktraShortestPath.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class DijsktraShortestPath 
{

    public DijsktraShortestPath() {
    }

    
    /**
     * @return Returns path array if it finds a path, returns null if it cannot finds a path
     *    
     */
    public int[] getPath(int source, int destination, Matrix matrix) throws SystemFault
    {
        double constGraph[][],graph[][];
        boolean visited[];
        int prevNode[];
        double nodeCost[];
       
        constGraph = matrix.getData();
        
        if( constGraph.length != constGraph[0].length )
            throw new SystemFault(SystemFault.SEVERE_ERROR);
        
        graph = new double[constGraph.length][constGraph[0].length];
        visited = new boolean[constGraph.length];
        prevNode = new int[constGraph.length];
        nodeCost = new double[constGraph.length];
        
        for(int i = 0; i < constGraph.length; i++ )
        {
            graph[i] = Arrays.copyOf(constGraph[i], constGraph[i].length);
        }
        
        for(int i = 0; i < graph.length; i++)
        {
            for(int j = 0; j < graph[0].length; j++ )
            {
                if( graph[i][j] == 0 )
                {
                    if(i != j )
                        graph[i][j] = -1;
                }
            }
        }
        
        int currentNode = source;
        int nextVertex = 0;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < graph.length; i++)
        {
            visited[i] = false;
            prevNode[i] = -1;
            nodeCost[i] = Double.MAX_VALUE;
        }                
        nodeCost[currentNode] = 0;
        
        boolean nextVertexSet = true;
        while (true)
        {           

            nextVertexSet = false;
            
            minDistance = Double.MAX_VALUE;
            visited[currentNode] =  true;
            for (int i = 0; i < graph.length; i++)
            {
                /*If a path exist*/
                if (graph[currentNode][i] > 0 && visited[i] == false && i != currentNode)
                {
                    double newCostValue = nodeCost[currentNode] + graph[currentNode][i];
                    /*update all neighbours*/
                    if( newCostValue < nodeCost[i] )
                    {
                        nodeCost[i] = newCostValue;
                        prevNode[i] = currentNode;
                    }
                }
            }
            
            
            /*Decide Next Node*/
            double minNodeValue = Double.MAX_VALUE;
            boolean nextNodeDecided = false;
            for(int i = 0; i < graph.length; i++)
            {
                if( visited[i] == false )
                {
                    if( nodeCost[i] < minNodeValue )
                    {
                        minNodeValue = nodeCost[i];
                        currentNode = i;
                        nextNodeDecided = true;
                    }
                }
            }
            
            if( nextNodeDecided == false )
            {
                /*
                SystemFault sf = new SystemFault(SystemFault.SEVERE_ERROR);
                sf.setInspectIntParam(0, source);
                sf.setInspectIntParam(1, destination);
                throw  sf;
                 * */
                return null;    /*No path is available*/
            }
            if( currentNode == destination )
                break;
                        
        }
        
        int currPrevNode = destination;
        int lengthOfPath = 0;
        try{
            while(currPrevNode != source)
            {
                currPrevNode = prevNode[currPrevNode];
                lengthOfPath++;
            }
        }
        catch(Exception e)
        {
            SystemFault sf = new SystemFault(SystemFault.SEVERE_ERROR);
            sf.setInspectIntParam(0, currPrevNode);
            sf.handler();
        }
        
        int shortestPath[] = new int[ lengthOfPath + 1];
        
        currPrevNode = destination;
        int i = 0;
        while(currPrevNode != source)
        {
            shortestPath[i++] = currPrevNode;
            currPrevNode = prevNode[currPrevNode];
        }
        shortestPath[i] = source;
        
        if(i != lengthOfPath )
            throw new SystemFault(SystemFault.SEVERE_ERROR);
                
        /*Reverse the array*/
        int[] reversedShortestPath = Matrix.reverseArray( shortestPath );
        return reversedShortestPath;
    }    
}
