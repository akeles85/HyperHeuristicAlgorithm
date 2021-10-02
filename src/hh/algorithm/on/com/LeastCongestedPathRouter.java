/*
* Filename: LeastCongestedPathRouter.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.com.DijsktraShortestPath;
import hh.algorithm.com.KShortestPathMatrix;
import hh.algorithm.com.SystemFault;
import java.util.Arrays;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class LeastCongestedPathRouter extends LightPathRouterHeuristic
{

    public LeastCongestedPathRouter() {
        this.setType( LightPathRouterHeuristic.LEAST_CONGESTED_SHORTEST_PATH );
    }
    

    public int[][] execute(RoutingAssignHeuristic routingHeuristic, int sdPairs[][]  ) 
    {                   
        int []path = null;
        int []currPath = null;
        try{
            KShortestPathMatrix kShortestPath = new KShortestPathMatrix( routingHeuristic.getCostTopology(), sdPairs);                                    
            
            int leastCongestedPathCost = Integer.MAX_VALUE, leastCongestedPathIndex = -1;
            
            if( sdPairs.length > 1)
                throw new SystemFault(SystemFault.SEVERE_ERROR);
            
            /*Prepare KShortestPath Matrix*/
            kShortestPath.preparePaths();           
            
            for( int i = 0; i < kShortestPath.getNumOfPairs(); i++ )
            {
                try {
                    for( int j = 0; j < kShortestPath.getNumOfPath(); j++ )
                    {
                        kShortestPath.changeShortestPath(i, j);
                        currPath = kShortestPath.getPath(i);
                        if( currPath == null)
                            continue;
                        int currCongestedPathCost = routingHeuristic.getCongestedPathCost(currPath);
                                                
                        /*Update the least congtested path*/
                        if( currCongestedPathCost < leastCongestedPathCost)
                        {
                            int debug =1;
                            if( j != 0)
                            {
                                debug = 0;
                            }
                            leastCongestedPathCost = currCongestedPathCost;
                            leastCongestedPathIndex = i;
                            path = Arrays.copyOf(currPath, currPath.length);
                        }
                    }
                                        
                    
                    if( VTDesignParams.DEBUG_ON )
                    {
                        for (int j = 0; j < path.length; j++) 
                        {
                            System.out.print(" " + (path[j]+1) );
                        }
                        System.out.println("\n");
                    }                    

                } catch (SystemFault ex) {
                    ex.printStackTrace();
                }
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
        return new LeastCongestedPathRouter();
    }

    public int compareTo(Object o) {
        if( o instanceof LeastCongestedPathRouter )
            return 0;
        else
            return -1;
    }
}
