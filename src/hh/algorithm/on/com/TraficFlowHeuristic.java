/*
* Filename: TraficFlowHeuristic.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.GRASP.AICS;
import hh.algorithm.com.DijsktraShortestPath;
import hh.algorithm.com.Matrix;
import hh.algorithm.com.SystemFault;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class TraficFlowHeuristic {

    private Matrix costOfPathMatrix;            /*The entries with zero means there is no path between the nodes*/

    private Matrix virtualToplogy;

    private Matrix constVirtualToplogy;

    private Matrix trafficMatrix;               /*Input of the algorithm*/

    private Matrix wijMatrix;               /*Input of the algorithm*/

    private int numOfNode;

    private LightPath   lightpaths[];

    public double scaleUp;

    public TraficFlowHeuristic( final int numOfNode )
    {
        costOfPathMatrix = new Matrix();

        trafficMatrix = new Matrix();

        virtualToplogy = new Matrix();

        wijMatrix = new Matrix();

        wijMatrix.setData( new double[numOfNode][numOfNode]);

        this.numOfNode = numOfNode;
    }

    public void setLightPaths( LightPath lightpaths[] )
    {
        this.lightpaths = lightpaths;
        this.prepareTopologies(lightpaths);
    }

    public void setTrafficMatrix( Matrix trafficMatrix )
    {
        this.trafficMatrix = (Matrix)trafficMatrix.clone();
    }

    private void prepareTopologies(LightPath lightPath[])
    {
        double costMatrix[][] = new double[numOfNode][numOfNode];
        double flowMatrix[][] = new double[numOfNode][numOfNode];

        for( int i = 0; i < lightPath.length; i++ )
        {
            int transmit,recieve;
            transmit = lightPath[i].getSourceNode();
            recieve = lightPath[i].getDestinationNode();
            /*IMPORTANT: Tranmit, Receive. No flow from Receive to Transmit*/
            costMatrix[ transmit ][ recieve ] = lightPath[i].getCost();
            flowMatrix[ transmit ][ recieve ] = lightPath[i].getCapacity();
        }
        this.costOfPathMatrix.setData( costMatrix );
        this.virtualToplogy.setData( flowMatrix );
        constVirtualToplogy = this.virtualToplogy.clone();

    }

    public TrafficFlow[] assign() throws SystemFault
    {
        DijsktraShortestPath shortestPath = new DijsktraShortestPath();
        int sdTrafficFlow[][];
        double neededFlow, usedFlow;
        ArrayList<TrafficFlow> routedTrafficFlows;

        sdTrafficFlow = trafficMatrix.getSDArrayDescOrder();
        routedTrafficFlows = new ArrayList<TrafficFlow>();

        /*For each SD pair in traffic matrix*/
        double totalFlow = 0;
        for( int i = 0; i < sdTrafficFlow.length; i++ )
        {
                neededFlow = trafficMatrix.get( sdTrafficFlow[i][0], sdTrafficFlow[i][1] );
                int[] path = shortestPath.getPath(sdTrafficFlow[i][0], sdTrafficFlow[i][1], this.costOfPathMatrix);

                /*Path cannot be found*/
                if( path == null)
                {
                    return null;
                }
                TrafficFlow newTf = new TrafficFlow();
                newTf.setPhysicalLinks(path);

                int links[][] = new int[path.length - 1][2];
                int transmit,receive = 0;
                for( int j = 0; j < path.length - 1; j++ )
                {
                    transmit = path[j];
                    receive = path[j+1];
                    links[j][0] = transmit;
                    links[j][1] = receive;
                  //  System.out.print( AICS.nodeTexts[ transmit ] + ", " );
                }
                //System.out.println( AICS.nodeTexts[ receive ] );

                double minFlow = this.virtualToplogy.getMinValue(links);

                /*If no flow is found*/
                if( minFlow == 0 )
                {
                    try {
                        throw new Exception("No Flow exist");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                if( minFlow > neededFlow )
                {
                    usedFlow = neededFlow;
                }
                else
                {
                    usedFlow = minFlow;
                    /*in the next iteration, continue with the same sd Pair*/
                    i--;
                }
                totalFlow += usedFlow;
                newTf.setCapacity(usedFlow);
                routedTrafficFlows.add(newTf);
                this.useFlow( path, usedFlow );
        }

        double[][] firstFlowMatrix = new double[ virtualToplogy.getNumOfRow() ][ virtualToplogy.getNumOfColumn() ];

        for(int i = 0; i < firstFlowMatrix.length; i++)
        {
            for( int j = 0; j < firstFlowMatrix.length; j++ )
            {
                if( this.virtualToplogy.get(i, j) != 0 )
                    firstFlowMatrix[i][j] = VTDesignParams.wavelengthCapacity - this.virtualToplogy.get(i, j);
            }
        }

        while( true )
        {
            /*Create Wij Matrix*/
            this.formWijMatrix( this.virtualToplogy, totalFlow );

            /*Reset Traffic Flows*/
            this.virtualToplogy = constVirtualToplogy;

            for( int i = 0; i < sdTrafficFlow.length; i++ )
            {
                neededFlow = trafficMatrix.get( sdTrafficFlow[i][0], sdTrafficFlow[i][1] );
                int[] path = shortestPath.getPath( sdTrafficFlow[i][0], sdTrafficFlow[i][1], this.wijMatrix );

                /*Path cannot be found*/
                if( path == null)
                {
                    return null;
                }
                TrafficFlow newTf = new TrafficFlow();
                newTf.setPhysicalLinks(path);

                int links[][] = new int[path.length - 1][2];
                int transmit,receive = 0;
                for( int j = 0; j < path.length - 1; j++ )
                {
                    transmit = path[j];
                    receive = path[j+1];
                    links[j][0] = transmit;
                    links[j][1] = receive;
                  //  System.out.print( AICS.nodeTexts[ transmit ] + ", " );
                }
                //System.out.println( AICS.nodeTexts[ receive ] );

                double minFlow = this.virtualToplogy.getMinValue(links);

                /*If no flow is found*/
                if( minFlow == 0 )
                {
                    try {
                        throw new Exception("No Flow exist");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                if( minFlow > neededFlow )
                {
                    usedFlow = neededFlow;
                }
                else
                {
                    usedFlow = minFlow;
                    /*in the next iteration, continue with the same sd Pair*/
                    i--;
                }
                newTf.setCapacity(usedFlow);
                routedTrafficFlows.add(newTf);
                this.useFlowForWij( path, usedFlow );
            }

            double[][] currFlowMatrix = new double[ virtualToplogy.getNumOfRow() ][ virtualToplogy.getNumOfColumn() ];

            for(int i = 0; i < currFlowMatrix.length; i++)
            {
                for( int j = 0; j < currFlowMatrix.length; j++ )
                {
                    if( this.virtualToplogy.get(i, j) != 0 )
                        currFlowMatrix[i][j] = VTDesignParams.wavelengthCapacity - this.virtualToplogy.get(i, j);
                }
            }

            /*  Calculate the traffic matrix according to a step  */
            double newFlowMatrix[][] = new double[ firstFlowMatrix.length ][ firstFlowMatrix.length ];
            double a_step = 0.05;

            double currDValue = this.calculateD( currFlowMatrix, totalFlow);

            double currLowestDvalue = Double.MAX_VALUE;
            for( double current_a_step = 0; current_a_step < 1; current_a_step = current_a_step + a_step)
            {
                for(int i = 0; i < firstFlowMatrix.length; i++ )
                {
                    for( int j = 0; j < firstFlowMatrix.length; j++ )
                    {
                        newFlowMatrix[i][j] = firstFlowMatrix[i][j] + current_a_step * ( currFlowMatrix[i][j] - firstFlowMatrix[i][j] );
                    }
                }
                double currIncrementResult = this.calculateD( newFlowMatrix, totalFlow);
                if( currLowestDvalue > currIncrementResult )
                {
                    Matrix debugMatrix = new Matrix();
                    debugMatrix.setData( newFlowMatrix );
                    currLowestDvalue = currIncrementResult;
                    scaleUp = VTDesignParams.wavelengthCapacity / debugMatrix.getMaxValueEntry();
                }

            }
            currFlowMatrix = newFlowMatrix;
            /*If no improvement exist, break*/
            if( currDValue <= currLowestDvalue )
                break;
        }


        TrafficFlow tfs[];
        tfs = new TrafficFlow[ routedTrafficFlows.size() ];

        for( int i = 0; i < routedTrafficFlows.size(); i++ )
        {
            tfs[i] = (TrafficFlow) routedTrafficFlows.get(i).clone();
        }

        return tfs;
    }

    /**
     * Important: Returns true if the link can be established
     * Updates the cost topology and physical topology
     * @param path: 1->4->5 = (1,4) (4,5)
     * @return If returns false, it means no wavelength is exist in this link
     *
     */
    public boolean useFlow( int path[], double flow ) throws SystemFault
    {
        double [][]matrix = this.virtualToplogy.getData();

        for( int i = 0; i < path.length - 1; i++ )
        {
            int transmit = path[i];
            int receive = path[i+1];

            virtualToplogy.set(transmit,receive, virtualToplogy.get(transmit, receive) - flow);
            /*Path cannot be established, It is a control check. It is not expected to be occured*/
            /*Because path sould be established, if shortest path algorithm finds one*/
            if ( virtualToplogy.get(transmit, receive) < 0  )
            {
                throw new SystemFault(SystemFault.SEVERE_ERROR);
            }

            /*If no available flow is left*/
            if( virtualToplogy.get(transmit, receive) == 0 )
            {
                /*Delete the entry in the cost Matrix that is sent to Dijsktra*/
                this.costOfPathMatrix.set(transmit, receive, 0);
            }
        }

        return true;
    }

    /**
     * Important: Returns true if the link can be established
     * Updates the cost topology and physical topology
     * @param path: 1->4->5 = (1,4) (4,5)
     * @return If returns false, it means no wavelength is exist in this link
     *
     */
    public boolean useFlowForWij( int path[], double flow ) throws SystemFault
    {
        double [][]matrix = this.virtualToplogy.getData();

        for( int i = 0; i < path.length - 1; i++ )
        {
            int transmit = path[i];
            int receive = path[i+1];

            virtualToplogy.set(transmit,receive, virtualToplogy.get(transmit, receive) - flow);
            /*Path cannot be established, It is a control check. It is not expected to be occured*/
            /*Because path sould be established, if shortest path algorithm finds one*/
            if ( virtualToplogy.get(transmit, receive) < 0  )
            {
                throw new SystemFault(SystemFault.SEVERE_ERROR);
            }

            /*If no available flow is left*/
            if( virtualToplogy.get(transmit, receive) == 0 )
            {
                /*Delete the entry in the cost Matrix that is sent to Dijsktra*/
                this.wijMatrix.set(transmit, receive, 0);
            }
        }

        return true;
    }


    private double calculateD( double[][] matrix, double totalFlow)
    {
        double d = 1;

        d = 1.0 / totalFlow;

        double sumation = 0;
        for( int i = 0; i < matrix.length; i++ )
        {
            for(int j = 0; j < matrix[i].length; j++ )
            {
                if( matrix[i][j] != 0 )
                {
                    /*the formulation is different because matrix[i][j] holds the left available flow on the link*/
                    sumation += ( matrix[i][j] ) / ( VTDesignParams.wavelengthCapacity - matrix[i][j]) ;
                }
            }
        }
        return (d*sumation);
    }

    private void formWijMatrix(Matrix inVirtualTopology, double totalFlow)
    {
        double [][]matrix = inVirtualTopology.getData();

        for(int i = 0; i < matrix.length; i++ )
        {
            for( int j = 0; j < matrix[i].length; j++ )
            {
                if( matrix[i][j] == 0)
                    this.wijMatrix.set(i, j, 0);
                else
                {
                    double currentValue = VTDesignParams.wavelengthCapacity / ( totalFlow * Math.pow( matrix[i][j],2) );
                    this.wijMatrix.set(i,j, currentValue);
                }
            }
        }
    }


}
