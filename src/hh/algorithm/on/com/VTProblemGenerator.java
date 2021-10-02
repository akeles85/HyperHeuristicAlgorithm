/*
* Filename: VTProblemGenerator.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.com.Matrix;
import hh.algorithm.com.RandomGenerator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class VTProblemGenerator {

    public static void main(String args[]) throws FileNotFoundException, IOException, Exception
    {
        String dirName = "c:\\VTD_Traffic\\";
        File dir = new File( dirName );
        
        if( !dir.exists() )
            dir.mkdir();
        
        for( int problemInstanceIndex = 0; problemInstanceIndex < 20; problemInstanceIndex++ )
        {
            Matrix trafficMatrix = new Matrix();
            trafficMatrix.setData( Matrix.readFromFile(new File( "c:\\NSFNET_Traffic.txt" )));                                                
            
            if(  trafficMatrix.getNumOfColumn() != trafficMatrix.getNumOfRow() )
                throw new Exception("invalid traffic");
            
            int numOfEntry = trafficMatrix.getNumOfColumn() * trafficMatrix.getNumOfRow() - trafficMatrix.getNumOfColumn();
            double valueArray[] = new double[numOfEntry]; 
            
            double trafficData[][] = trafficMatrix.getData();
            
            int currValueArray = 0;
            for( int i = 0; i < trafficData.length; i++ )
            {
                for( int j = 0; j < trafficData[i].length; j++ )
                {
                    if( i == j )
                        continue;
                    
                    valueArray[ currValueArray++ ] = trafficData[i][j];
                }
            }
            
            ArrayList<Integer> selectedNumbers = new ArrayList();
            
            double newArray[] = new double[ valueArray.length ];
            for( int i = 0; i < valueArray.length; i++ )
            {                
                int currSelected = RandomGenerator.genInt(valueArray.length, selectedNumbers);   
                selectedNumbers.add(currSelected);
                newArray[i] = valueArray[ currSelected ];
            }     
            
            double newTrafficMatrixData[][] = new double[trafficMatrix.getNumOfRow()][trafficMatrix.getNumOfColumn()];
            
            int currNewArrayIndex = 0;
            for( int i = 0; i < newTrafficMatrixData.length; i++ )
            {
                for( int j = 0; j < newTrafficMatrixData[i].length; j++ )
                {
                    if( i == j )
                        continue;
                    
                    newTrafficMatrixData[i][j] = newArray[currNewArrayIndex++];
                }
            }
            
            Matrix.writeToFile( new File(dirName+"TrafficMatrix_"+problemInstanceIndex+".txt"), newTrafficMatrixData );
            
        }
            
    }
}
