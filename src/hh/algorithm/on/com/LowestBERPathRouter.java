/*
* Filename: LowestBERPathRouter.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.com.KShortestPathMatrix;
import hh.algorithm.com.RandomGenerator;
import hh.algorithm.on.qTool.QTool;
import hh.algorithm.representation.LigthPathArrayRepresentation;
import java.util.Arrays;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class LowestBERPathRouter extends LightPathRouterHeuristic
{

    public LowestBERPathRouter() {
        this.setType(LightPathRouterHeuristic.LOWEST_BER_PATH );
    }
    
    

    public int[][] execute(RoutingAssignHeuristic routingHeuristic, int sdPairs[][]  ) 
    {                   
        int []path = null;
        int []lowestBERPath = null; 
        boolean result;
        
        try{
            KShortestPathMatrix kShortestPath = new KShortestPathMatrix( routingHeuristic.getConstantDistanceMatrix(), sdPairs);                                    
                              
            //int randomWavelengthId = 0;
            QTool qTool = routingHeuristic.getQtool();                
                
            /*Prepare KShortestPath Matrix*/
            kShortestPath.preparePaths();           
            
            for(int i = 0; i < sdPairs.length; i++ )
            {                           
                double currBER, lowestBER = Double.MAX_VALUE;                

                for( int j = 0; j < kShortestPath.getNumOfPath(); j++ )
                {
                    LightPath tempLightPath = new LightPath();                     
                    kShortestPath.changeShortestPath(i, j);
                    path = kShortestPath.getPath(0);                    
                    if( path == null )
                        continue;
                    
                    tempLightPath.setPhysicalLinks( path ); 
                    tempLightPath.setRouted(true);
                    
                    /*Now, set the wavelength of the current lightpath*/                        
                    LigthPathArrayRepresentation    repForWA = new LigthPathArrayRepresentation();
                    repForWA.setLigthpaths( tempLightPath );
                    result = routingHeuristic.getWavelengthAssignHeuristic().assign( repForWA, false );

                    if( result == false )
                    {                        
                        continue;
                    }

                    if( routingHeuristic.getWavelengthAssignHeuristic().getWavelength(0) == null ) 
                    {                            
                        continue;
                    }
                    
                    tempLightPath.setWavelengthAssigned(true);            
                    tempLightPath.setWavelengthId( routingHeuristic.getWavelengthAssignHeuristic().getWavelength(0));                                    

                    qTool.takeSnapShot();
                    currBER = qTool.calculateBER( tempLightPath, routingHeuristic.getConstantDistanceMatrix().getData(), false );
                    //System.out.println("Curr Ber at Router: " + currBER);
                    qTool.loadSnapShot();
                    if(currBER < lowestBER)
                    {
                        if( j != 0)
                        {
                           // System.out.println("Lowest Ber Index " + j + " Old BER:" + lowestBER + "New BER: " + currBER);                        
                        }
                        lowestBER = currBER;
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
            else
            {                
                /*
                 DEGISTI: 06.02.2010,
                 * The BER value of the chosen lightpath should be stored at QTOOL Matrixes,
                 * because the BER of a lightpath affects the next chosen lightpath.
                 * After routing finished, the QTool will be created again.
                 */ 
//                /
//                /*If a path is found, calculate its BER for the following iterations. (XT noise)*/
//                LightPath tempLightPath = new LightPath();
//                
//                tempLightPath.setPhysicalLinks( path ); 
//                tempLightPath.setRouted(true);
//                
//                /*Now, set the wavelength of the current lightpath*/                        
//                LigthPathArrayRepresentation    repForWA = new LigthPathArrayRepresentation();
//                repForWA.setLigthpaths( tempLightPath );
//                
//                result = routingHeuristic.getWavelengthAssignHeuristic().assign( repForWA, false );                
//                
//                if( result == false )
//                {                        
//                    throw new Exception();
//                }
//
//                if( routingHeuristic.getWavelengthAssignHeuristic().getWavelength(0) == null ) 
//                {                            
//                    throw new Exception();
//                }
//
//                tempLightPath.setWavelengthAssigned(true);            
//                tempLightPath.setWavelengthId( routingHeuristic.getWavelengthAssignHeuristic().getWavelength(0));                                                    
//                                                                               
//                qTool.calculateBER( tempLightPath, routingHeuristic.getConstantDistanceMatrix().getData(), false );                                
                
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
        return new LowestBERPathRouter();
    }
        
    public int compareTo(Object o) {
        if( o instanceof LowestBERPathRouter )
            return 0;
        else
            return -1;
    }        
}
