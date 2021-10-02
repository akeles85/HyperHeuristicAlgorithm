/*
* Filename: MinimizingMaxBERPathRouter.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.com.KShortestPathMatrix;
import hh.algorithm.com.RandomGenerator;
import hh.algorithm.on.qTool.QTool;
import java.util.Arrays;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class MinimizingHighestBERPathRouter extends LightPathRouterHeuristic
{

    public MinimizingHighestBERPathRouter() {
        this.setType(LightPathRouterHeuristic.MINIMIZING_HIGHEST_BER_PATH );
    }
    
     public int[][] execute(RoutingAssignHeuristic routingHeuristic, int sdPairs[][]  ) 
    {                   
        int []path = null;
        int []lowestBERPath = null;   
        double currHighestBER;
        double minHighestBER = Double.MAX_VALUE;
        try{
            KShortestPathMatrix kShortestPath = new KShortestPathMatrix( routingHeuristic.getConstantDistanceMatrix(), sdPairs);                                    
                                    
            /*Prepare KShortestPath Matrix*/
            kShortestPath.preparePaths();           
            
            for(int i = 0; i < sdPairs.length; i++ )
            {
                QTool qTool = routingHeuristic.getQtool();                
            
                double currBER;                
                int randomWavelengthId = RandomGenerator.genInt( VTDesignParams.numOfWavelengths );
                for( int j = 0; j < kShortestPath.getNumOfPath(); j++ )
                {
                    LightPath tempLightPath = new LightPath();                     
                    kShortestPath.changeShortestPath(i, j);
                    path = kShortestPath.getPath(0);                    
                    if( path == null )
                        continue;
                    tempLightPath.setPhysicalLinks( path );                   
                    tempLightPath.setWavelengthId( randomWavelengthId );

                    qTool.takeSnapShot();
                    currBER = qTool.calculateBER( tempLightPath, routingHeuristic.getConstantDistanceMatrix().getData(), false );
                    currHighestBER = Double.MIN_VALUE;
                    /*Update BER of other routed lightpaths*/
                    for( int routedLPIndex = 0; routedLPIndex < routingHeuristic.getNumOfRoutedPaths(); routedLPIndex++ )
                    {
                        double currRoutedBER;
                        LightPath currRoutedLightPath = routingHeuristic.getRoutedLigthPaths(routedLPIndex );
                        /*If this lightpath hasn't been routed*/
                        if( currRoutedLightPath == null )
                            continue;
                        currRoutedBER = qTool.calculateBER( currRoutedLightPath, routingHeuristic.getConstantDistanceMatrix().getData(), false );
                        if( currRoutedBER > currHighestBER )
                        {
                            currHighestBER = currRoutedBER;
                        }
                        
                    }
                    qTool.loadSnapShot();
                    if(currHighestBER < minHighestBER)
                    {
                       /* if( j != 0)
                        {
                            System.out.println("Lowest Ber Index " + j + " Old BER:" + lowestBER + "New BER: " + currBER);                        
                        }*/
                        minHighestBER = currHighestBER;
                        lowestBERPath = Arrays.copyOf(path, path.length);                        

                    }
                }
            }
                    
            if( lowestBERPath != null )
            {
                path = Arrays.copyOf( lowestBERPath, lowestBERPath.length);
            }
            else
            {
                path = null;
            }

            if( VTDesignParams.DEBUG_ON )
            {
                for (int j = 0; j < path.length; j++) 
                {
                    System.out.print(" " + (path[j]+1) );
                }
                System.out.println("\n");
            }                    

            
            /*Path cannot be found*/
            if( path == null)
            {
                return null;
            }
                    
            if( routingHeuristic.usePath(path ) == false )     /*Physical Topology is changed, because of the used links*/
            {
                return null;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
            
            int routedPaths[][];
            routedPaths = new int[1][];
            
            routedPaths[0] = path;
                    
            return routedPaths;
     }


    
        @Override
    public Object clone()  {
        return new MinimizingHighestBERPathRouter();
    }
        
    public int compareTo(Object o) {
        if( o instanceof MinimizingHighestBERPathRouter )
            return 0;
        else
            return -1;
    }        
}
