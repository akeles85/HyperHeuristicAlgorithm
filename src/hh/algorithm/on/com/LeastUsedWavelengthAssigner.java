/*
* Filename: LeastUsedWavelengthAssigner.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.com.RandomGenerator;
import hh.algorithm.representation.LigthPathArrayRepresentation;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class LeastUsedWavelengthAssigner implements WavelengthAssigner
{    
    public Integer[] execute(double[][][] wavelengthTopology, int numOfWavelength, LigthPathArrayRepresentation routedPaths, int[] numOfUsedWavelengths, boolean updateTopology, int []leastUsedWavelength) 
    {       
               
        Integer []result = new Integer[routedPaths.size()];
        /*Execute for each path*/
        for( int i = 0; i < routedPaths.size(); i++ )
        {
            int []foundAvailableWavelengths = new int[numOfWavelength];
            int numberOfAvailableWavelengths = 0;
            int selectedWavelength;
            /*If this path is not routed, do not consider wavelength assignment for it*/
            if( routedPaths.get(i).isRouted() == false )
                continue;
            int[] physicalLinks = routedPaths.get(i).getPhysicalLinks();
            int [][]links = new int[physicalLinks.length - 1][2];
            for(int j = 0; j < routedPaths.get(i).getPhysicalLinks().length - 1; j++)
            {
                links[j][0] = physicalLinks[j];    
                links[j][1] = physicalLinks[j+1];  
            }
            
            for(int wavelengthIndex = 0; wavelengthIndex < numOfWavelength; wavelengthIndex++)
            {
                boolean wavelengthAvailableForAllLink = true;
                /*Search the availability of this wavelength for each link in i. lightpath*/
                for(int linkIndex = 0; linkIndex < links.length; linkIndex++)
                {
                    int transmit, receive;
                    transmit = links[linkIndex][0];
                    receive = links[linkIndex][1];
                    /*i. wavelength is not available at link (x,y)*/
                    if( wavelengthTopology[wavelengthIndex][transmit][receive] == 0)
                    {
                        wavelengthAvailableForAllLink = false;
                        break;
                    }

                }
                /*An available wavelength is found for each link*/
                if( wavelengthAvailableForAllLink == true )
                {
                    /*Set wavelength*/
                    foundAvailableWavelengths[numberOfAvailableWavelengths++] = wavelengthIndex;                                        
                }

            }
            
            /*No available wavalength*/
            if( numberOfAvailableWavelengths == 0 )
            {
                /*WARNING*/
                 result[i] = null;
                 continue;
                /*return null;*/
            }
            
            
            /*Find the least used one, from the available ones*/
            selectedWavelength = -1;
            int leastUsedOneValue = Integer.MAX_VALUE;
            for( int j = 0; j < numberOfAvailableWavelengths; j++ )
            {
                int currWavelength = foundAvailableWavelengths[ j ];
                if( leastUsedOneValue > leastUsedWavelength[ currWavelength ] )
                {
                    leastUsedOneValue = leastUsedWavelength[ currWavelength ];
                    selectedWavelength = currWavelength;
                }
            }
            leastUsedWavelength[selectedWavelength]++;
            
            for(int linkIndex = 0; linkIndex < links.length; linkIndex++)
            {
                int transmit, receive;
                transmit = links[linkIndex][0];
                receive = links[linkIndex][1];
                
                /*i. wavelength is not available at link (x,y)*/
                if( wavelengthTopology[selectedWavelength][transmit][receive] == 0 )
                {
                    try {
                        throw new Exception();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if( updateTopology )
                    wavelengthTopology[selectedWavelength][transmit][receive]= 0;                        
                /*DIKKAT*/
                /*wavelengthTopology[wavelengthIndex][receive][transmit]= 0; */
            }
            
            result[i] = selectedWavelength;                                                                        

        }
        return result;                  
    }

}
