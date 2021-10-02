/*
* Filename: KShortestPathRouter.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.com.KShortestPathMatrix;
import hh.algorithm.com.RandomGenerator;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class KShortestPathRouter extends LightPathRouterHeuristic
{

    public KShortestPathRouter() {
        this.setType(LightPathRouterHeuristic.KSHORTEST_PATH );
    }
    
    

    public int[][] execute(RoutingAssignHeuristic routingHeuristic, int sdPairs[][]  ) 
    {                   
        int []path = null;
        try{
            KShortestPathMatrix kShortestPath = new KShortestPathMatrix( routingHeuristic.getCostTopology(), sdPairs);                                    
                                    
            /*Prepare KShortestPath Matrix*/
            kShortestPath.preparePaths();           
            
            for(int i = 0; i < sdPairs.length; i++ )
            {
                int randomIndex = RandomGenerator.genInt( kShortestPath.getNumOfPath() );
            
                kShortestPath.changeShortestPath(i, randomIndex);
            }
                    
            path = kShortestPath.getPath(0);

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
        return new KShortestPathRouter();
    }
        
    public int compareTo(Object o) {
        if( o instanceof KShortestPathRouter )
            return 0;
        else
            return -1;
    }        
}
