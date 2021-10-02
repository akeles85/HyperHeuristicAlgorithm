/*
* Filename: KShortestPathRouter.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.com.DijsktraShortestPath;
import hh.algorithm.com.Representation;
import hh.algorithm.com.SystemFault;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class ShortestPathRouter extends LightPathRouterHeuristic{

    public ShortestPathRouter() {
        this.setType( LightPathRouterHeuristic.SHORTEST_PATH );
    }

    
    public int[][] execute(RoutingAssignHeuristic routingHeuristic, int sdPairs[][]  ) 
    {       
            DijsktraShortestPath shortestPath = new DijsktraShortestPath();
            int routedPaths[][];

            routedPaths = new int[sdPairs.length][];
            for( int i = 0; i < sdPairs.length; i++ )
            {
                try {
                    int[] path = shortestPath.getPath(sdPairs[i][0], sdPairs[i][1], routingHeuristic.getCostTopology());                            

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
                    routedPaths[i] = path;
                    if( routingHeuristic.usePath(path ) == false )     /*Physical Topology is changed, because of the used links*/
                    {
                        /*WARNING*/
                        /*throw new SystemFault(SystemFault.SEVERE_ERROR);*/
                        return null;
                    }

                } catch (SystemFault ex) {
                    ex.printStackTrace();
                }
            }
            return routedPaths;
        }        
    
        @Override
    public Object clone()  {
        return new ShortestPathRouter();
    }    
    public int compareTo(Object o) {
        if( o instanceof ShortestPathRouter )
            return 0;
        else
            return -1;
    }         
    

}
