/*
* Filename: WavelengthAssignHeuristic.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.com.Matrix;
import hh.algorithm.representation.LigthPathArrayRepresentation;
import java.util.Arrays;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class WavelengthAssignHeuristic 
{

    private WavelengthAssigner  wavelengthAssigner;
    
    private double               wavelengthTopology[][][];
    
    private int                 numOfWavelength;
    
    private Integer                 assignedWavelengths[];
    
    private static int               numOfSpare;
   
    private int                 numOfUsedWavelengths[];
    
    private double[][][]         tempWavelengthTopology;
    
    int[]                       leastUsedWavelengthArray;
    
    public WavelengthAssignHeuristic(int numOfWavelength) {
        this.numOfWavelength = numOfWavelength;
        this.wavelengthTopology = new double[numOfWavelength+VTDesignParams.MAX_NUM_OF_SPARE_WAVELENGTH][][];
        numOfUsedWavelengths = new int[numOfWavelength+VTDesignParams.MAX_NUM_OF_SPARE_WAVELENGTH];
        Arrays.fill( numOfUsedWavelengths, 0 );
        numOfSpare = 0;
        
        leastUsedWavelengthArray = new int[numOfWavelength+VTDesignParams.MAX_NUM_OF_SPARE_WAVELENGTH];
        Arrays.fill( leastUsedWavelengthArray,0);
    }
    
    public void prepareToAssign()
    {
        tempWavelengthTopology = Matrix.cloneMatrix(this.wavelengthTopology);        
        Arrays.fill( leastUsedWavelengthArray,0);
    }
    
    public boolean assign(LigthPathArrayRepresentation routedPaths, boolean updateTopology)
    {
        if( this.wavelengthAssigner  != null )
        {            
            assignedWavelengths = this.wavelengthAssigner.execute( tempWavelengthTopology, numOfWavelength, routedPaths, this.numOfUsedWavelengths, updateTopology, leastUsedWavelengthArray);
            
            if( assignedWavelengths == null)
            {
                return false;
            }
            else
                return true;
        }
        else
            return false;
    }
    
    public void setWavelengthTopology(Matrix distanceMatrix)
    {
        /*For each wavelength in a link */
        for( int i = 0; i < this.wavelengthTopology.length; i++ )
        {
            this.wavelengthTopology[i] = distanceMatrix.createConnMatrix().getData();
        }        
        Arrays.fill( numOfUsedWavelengths, 0 );
        
    }

    public void setWavelengthAssigner(WavelengthAssigner wavelengthAssigner) {
        this.wavelengthAssigner = wavelengthAssigner;
    }
    
    public String toString()
    {
        StringBuffer result = new StringBuffer("Wavelength for lightpaths: ");
        
        
        for(int i = 0; i < this.assignedWavelengths.length; i++)
        {      
            result.append(i+":"+this.assignedWavelengths[i]+ ", ");
        }
        result.append("\n");
        return result.toString();        
    }
    
    public Integer getWavelength(final int i)
    {
        return this.assignedWavelengths[i];
    }
    
    public Integer[] getAssignedWavelengths()
    {
        return this.assignedWavelengths;
    }

    public static int getNumOfSpare() {
        return numOfSpare;
    }

    public static void setNumOfSpare(int numOfSpare) {
        WavelengthAssignHeuristic.numOfSpare = numOfSpare;
    }
   
}
