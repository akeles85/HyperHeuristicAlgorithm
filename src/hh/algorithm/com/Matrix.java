/*
* Filename: ConnectionMatrix.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import hh.algorithm.on.com.VTDesignParams;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class Matrix extends Representation implements Cloneable{

    private double              connMatrix[][];
    
    private double              maxValidValue = 0;
    private double              minValidValue = 0;
    private boolean             valueValidityCheck = false;

    
    public Matrix()
    {
    }
    
    public Matrix(int row, int column)
    {
        this.connMatrix = new double[row][column];
        
        for( int i = 0; i < row; i++)
        {
            Arrays.fill(this.connMatrix[i], 0);
        }
    }    

    public double[][] getData() {
        return connMatrix;
    }

    public void setData(double[][] connMatrix) {
        this.connMatrix = connMatrix;
    }
    
    /**
    * Makes all rows with the given degree
     * @return if the condition is satisfied
     */
    public boolean satisfyRowConstraint(final int degree)
    {
        boolean result = true;
        
        for( int rowIndex = 0; rowIndex < this.connMatrix.length; rowIndex++ )
        {
            int numOfOne = this.countRowFor(rowIndex, 1);
            
            if( numOfOne < degree )
            {
                result = this.fillRowRandomly(rowIndex, degree-numOfOne, 1, 100);
                if(result == false)
                    break;
            }
            /*Go on with the next row*/
            else if( numOfOne == degree)
            {
                continue;
            }
            else
            {
                result = false;
                break;
            }
        }
        return result;
    }
        
    
    /*
    * Makes all columns to be equal or less then degree by not violating row constraint
    */
    public boolean satisfyColumnConstraint( final int degree )
    {
        int excess[];
        boolean isPermenant[][];
        
        excess = new int[this.getNumOfColumn()];
        isPermenant = new boolean[this.getNumOfRow()][this.getNumOfColumn()];
        
        for(int i = 0; i < this.getNumOfRow(); i++)
            Arrays.fill(isPermenant[i], false);
        
        while(true)
        {
            int maxPosExcessIndex = 0;
            int minNegExceesIndex = 0;
            
            /*Calculate Excess Values*/
            for( int i = 0; i < this.getNumOfColumn(); i++ )
            {
                excess[i] = this.countColumnFor(i, 1) - degree;
                for(int j = 0; j < this.getNumOfRow(); j++)
                {
                    if( excess[i] == 0)
                    {
                        isPermenant[j][i] = true;       /*All entries in the column with excess value 0 is set as permenant*/
                    }
                    
                    /*Diagonals should not be changed!*/
                    if(i == j)
                    {
                        isPermenant[j][i] = true;   
                    }
                }
            }    
            
            for( int i = 0; i < excess.length; i++ )
            {
                    if( excess[i] > excess[maxPosExcessIndex])
                        maxPosExcessIndex = i;
                    
                    if( excess[i] < excess[minNegExceesIndex])
                        minNegExceesIndex = i;                                   
            }
            
            /*CHECK FOR STOP CONDITIONS*/
            
            /*Cannot satisfy the column constraint*/
            if( excess[maxPosExcessIndex] > 0 && !(excess[minNegExceesIndex] < 0))
            {
                return false;
            }
            
            /*All columns have negative or zero excess values which supplies the column constraint*/
            if( excess[maxPosExcessIndex] <= 0 && excess[minNegExceesIndex] <= 0)
            {
                /*Cannot satisfy the column constraint*/
                return true;
            }            
            
            int numOfExchange = Math.min( excess[maxPosExcessIndex], Math.abs(excess[minNegExceesIndex]) );
            this.exchangeColumnsRandomly(maxPosExcessIndex, minNegExceesIndex, numOfExchange, isPermenant);        
            
        }
       
    }
    
    /**
     * @param interger array
     * @param integer that will be count
     * @return num of integer in that array
     */
    private int countRowFor( int rowIndex, int pattern)
    {
        int result = 0;
        double array[];
        
        array = this.connMatrix[rowIndex];
        
        for( int i = 0; i < this.getNumOfColumn(); i++ )
        {
            if( array[i] == pattern )
            {
                result++;
            }
        }
        return result;
    }
    
    /**
     * The number of exchange value is Min( excess1, excess2)
     * After exchange operation, the exchanged entries which are 1s and 0s will be made permenant.
     *
     * @param columnIndexWithOnes: 
     * @param columnIndexWithZeros: 
     * @param permenantOnes[][]: Includes the permenant entries of the matrix, after exchange it should be updated
     */
    public void exchangeColumnsRandomly( final int columnIndexWithOnes, final int columnIndexWithZeros, final int numOfExchange, final boolean permenantOnes[][])
    {
        boolean isAvailable[];
        int numOfAvailable = 0;
        int currNumOfExchanged = 0;
        
        isAvailable = new boolean[this.getNumOfRow()];
        
        Arrays.fill(isAvailable, false);
        
        for( int i = 0; i < isAvailable.length; i++ )
        {
            if( this.connMatrix[i][columnIndexWithOnes] == 1 && this.connMatrix[i][columnIndexWithZeros] == 0)
            {
                if( permenantOnes[i][columnIndexWithOnes] == false && permenantOnes[i][columnIndexWithZeros] == false)
                {
                    isAvailable[i] = true;
                    numOfAvailable++;
                }
            }
        }
        
        int availableIndexes[];
        availableIndexes = new int[numOfAvailable];
        
        for( int i = 0,j = 0; i < isAvailable.length; i++ )
        {
            if(isAvailable[i] == true)
            {
                availableIndexes[j] = i;
                j++;
            }
        }
        
        while(currNumOfExchanged < numOfExchange)
        {
            int randomNumber = RandomGenerator.genInt(numOfAvailable);
            
            int randRowIndex = availableIndexes[randomNumber];
            /*Is this random number has been currently selected*/
            if( permenantOnes[ randRowIndex ][columnIndexWithOnes] == true)
                continue;
            
            permenantOnes[ randRowIndex ][ columnIndexWithOnes ] = true;
            permenantOnes[ randRowIndex ][ columnIndexWithZeros ] = true;
            
            this.connMatrix[randRowIndex][columnIndexWithOnes] = 0;
            this.connMatrix[randRowIndex][columnIndexWithZeros] = 1;
            currNumOfExchanged++;
        }
        
    }
    
    
    /**
     * @param interger array
     * @param integer that will be count
     * @return num of integer in that array
     */
    private int countColumnFor( int columnIndex, int pattern)
    {
        int result = 0;
        
        for( int i = 0; i < this.connMatrix[0].length; i++ )
        {
            if( this.connMatrix[i][columnIndex] == pattern )
            {
                result++;
            }
        }
        return result;
    }    
    
    private boolean fillRowRandomly( final int rowIndex, final int fillNumber, final int fillPattern, int timeout)
    {
        int currFilledNumber = 0;
        boolean result = true;
        
        while( currFilledNumber < fillNumber )
        {
            timeout--;
            if( timeout <= 0)
                  result = false;
            int fillIndex = RandomGenerator.genInt(this.connMatrix[rowIndex].length);
            
            /*If randomly generated number is in diagonal, continue to generate*/
            if(fillIndex == rowIndex )
                continue;
            
            if( this.connMatrix[rowIndex][fillIndex] == fillPattern)
                continue;
            
            this.connMatrix[rowIndex][fillIndex] = fillPattern;
            currFilledNumber++;
        }
        
        return result;
    }
    
    public int getNumOfRow()
    {
        return this.connMatrix.length;
    }
    
    public int getNumOfColumn()
    {
        return this.connMatrix[0].length;
    }
    
    public String toString()
    {
        StringBuffer result = new StringBuffer("");
        
        for(int i = 0; i < this.getNumOfRow(); i++)
        {
            for( int j = 0; j < this.getNumOfColumn(); j++ )
                result.append(this.connMatrix[i][j] + " ");
            
            result.append("\n");
        }
        return result.toString();        
    }
    
    public void makeSymetrix()
    {
        for(int i = 0; i < this.getNumOfRow(); i++ )
        {
            for(int j = 0; j < this.getNumOfColumn(); j++ )
            {
                this.connMatrix[j][i] = this.connMatrix[i][j];
            }
        }
    }
    
    public static void writeToFile(File file, double data[][]) throws IOException
    {
        FileWriter f = new FileWriter(file);
        
        BufferedWriter writer = new BufferedWriter(f); 
        
        for( int i = 0; i < data.length; i++ )
        {
            for( int j = 0; j < data[i].length; j++ )
            {
                writer.write( data[i][j] + "\t");       
            }
            writer.write("\n");
        }    
        
        writer.close();
        f.close();
    }
    
    public static double[][] readFromFile(File file) throws FileNotFoundException, IOException
    {   
        FileReader f = new FileReader(file);
        double connectionMatrix[][] = null;
        int rowIndex = 0;
        
        BufferedReader reader = new BufferedReader(f);      
        
        while(true)
        {
            String line = reader.readLine();   
            
            if(line == null)
                break;
            
            String column[] = line.split("\t");
            
            if( rowIndex == column.length )
                break;
            
            if( connectionMatrix == null )
                connectionMatrix = new double[column.length][column.length];
            
            for(int i = 0; i < column.length; i++)
                connectionMatrix[rowIndex][i] = Double.valueOf(column[i]);
            
            rowIndex++;
            
        }
        
        f.close();
                
        return connectionMatrix;        
    }
    
    public static double[][] readPhysicalTopologyFromFile(File file) throws FileNotFoundException, IOException
    {   
        FileReader f = new FileReader(file);
        double connectionMatrix[][] = null;
        int rowIndex = 0;
        
        BufferedReader reader = new BufferedReader(f);      
        int numberOfNode = 0;
        
        int lineIndex = 0;
        while(true)
        {
            String line = reader.readLine();   
            lineIndex++;
            if(line == null)
                break;
            
            if( lineIndex == 1 )
            {
                numberOfNode = Integer.valueOf(line);
                continue;
            }
            
            line = line.replace(" ", ":");
            String column[] = line.split(":");
            
            if( column.length != 3 )
                continue;
            
            if( connectionMatrix == null )
                connectionMatrix = new double[numberOfNode][numberOfNode];
            
           int transmiter = Integer.valueOf(column[0]) ;
           int receiver = Integer.valueOf(column[1]) ;
           double distance = Double.valueOf(column[2]) ;
           connectionMatrix[ transmiter ][ receiver ] = distance;
            
            rowIndex++;
            
        }
        
        f.close();
                
        return connectionMatrix;        
    }    
    
    
    public static double[][] readVirtualTopologyFromFile(File file) throws FileNotFoundException, IOException
    {   
        FileReader f = new FileReader(file);
        double connectionMatrix[][] = null;
        int rowIndex = 0;
        
        BufferedReader reader = new BufferedReader(f);      
        int numberOfNode = 0;
        
        int lineIndex = 0;
        while(true)
        {
            String line = reader.readLine();   
            lineIndex++;
            if(line == null)
                break;
            
            if( lineIndex == 1 )
            {
                numberOfNode = Integer.valueOf(line);
                continue;
            }
            
            line = line.replace(" ", ":");
            String column[] = line.split(":");
            
            if( column.length != 2 )
                continue;
            
            if( connectionMatrix == null )
                connectionMatrix = new double[numberOfNode][numberOfNode];
            
           int transmiter = Integer.valueOf(column[0]) ;
           int receiver = Integer.valueOf(column[1]) ;           
           connectionMatrix[ transmiter ][ receiver ] = VTDesignParams.wavelengthCapacity;
           connectionMatrix[ receiver ][ transmiter ] = VTDesignParams.wavelengthCapacity;
            
            rowIndex++;
            
        }
        
        f.close();
                
        return connectionMatrix;        
    }      
    
    /**
     * @param Multiplies each entry of matrix with the scaleFactor
     */
    public void scale(final int scaleFactor)
    {
        for( int i = 0; i < this.getNumOfRow(); i++ )
        {
            for( int j = 0; j < this.getNumOfColumn(); j++ )
            {
                this.connMatrix[i][j] *= scaleFactor;
            }
        }
    }
    
    /**
     * @param Multiplies each entry of matrix with the scaleFactor
     */
    public void scale(final double scaleFactor)
    {
        for( int i = 0; i < this.getNumOfRow(); i++ )
        {
            for( int j = 0; j < this.getNumOfColumn(); j++ )
            {
                this.connMatrix[i][j] *= scaleFactor;
            }
        }
    }    
    
    /**
     * @return Returns a new Matrix whose entries are 0 or 1
     */
    public Matrix createConnMatrix()
    {
        Matrix matrix = new Matrix();
     
        double newConnMatrix[][] = new double[this.getNumOfRow()][ this.getNumOfColumn() ];
        
        for( int i = 0; i < this.getNumOfRow(); i++ )
        {
            for( int j = 0; j < this.getNumOfColumn(); j++ )
            {
                if( this.connMatrix[i][j] > 0 )
                    newConnMatrix[i][j] = 1;
                else
                    newConnMatrix[i][j] = 0;
            }
        }
        matrix.setData(newConnMatrix);
        
        return matrix;
    }
    
    public int [][]getSDArray()
    {
        int numOfSDPairs = 0, currSDPairIndex = 0;
        int sdArray[][];
        int numOfRow = getNumOfRow();
        int numOfColumn = getNumOfColumn();
        
        for( int i = 0; i < numOfRow; i++ )
        {
            for( int j = 0; j < numOfColumn; j++ )
            {
                if ( this.connMatrix[i][j] != 0 )
                {
                    numOfSDPairs++;
                }
            }
        }
        
        sdArray = new int[numOfSDPairs][2];
        
        for( int i = 0; i < numOfRow; i++ )
        {
            for( int j = 0; j < numOfColumn; j++ )
            {
                if ( this.connMatrix[i][j] != 0 )
                {
                    sdArray[currSDPairIndex][0] = i;
                    sdArray[currSDPairIndex][1] = j;
                    currSDPairIndex++;
                }
            }
        }        
        
        return sdArray;
    }
    
    public double getMaxValueEntry()
    {
        double max = 0;
        for (int i = 0; i < this.getNumOfRow(); i++) {
            for (int j = 0; j < this.getNumOfColumn(); j++) {
                if( this.connMatrix[i][j] > max )
                    max = this.connMatrix[i][j];
            }
        }
        return max;
    }
    
    /*i,j,value*/
    public double[] getMaxValueEntryWithIndexes( )
    {
        double max = 0;
        double returnValue[] = new double[3];
        
        for (int i = 0; i < this.getNumOfRow(); i++) {
            for (int j = 0; j < this.getNumOfColumn(); j++) {
                if( this.connMatrix[i][j] > max )
                {
                    returnValue[0] = i;
                    returnValue[1] = j;
                    max = this.connMatrix[i][j];
                }
            }
        }        
        returnValue[2] = max;
        return returnValue;
    }    
    
    public double[] getRandomValueEntryWithIndexes( )
    {        
        double returnValue[] = new double[3];
        
        int row, column;
        
        do
        {
            row = RandomGenerator.genInt( this.getNumOfRow() );
            column = RandomGenerator.genInt( this.getNumOfColumn() );
        }    
        while( this.connMatrix[row][column] <= 0 );
          
        returnValue[0] = row;
        returnValue[1] = column;
        returnValue[2] = this.connMatrix[row][column];
        return returnValue;
    }        
    
    
    public double getTotalValueOfAllEntry()
    {
        double total = 0;
        for (int i = 0; i < this.getNumOfRow(); i++) {
            for (int j = 0; j < this.getNumOfColumn(); j++) {                
                    total += this.connMatrix[i][j];
            }
        }
        return total;
    }    
   
    @Override
    public Matrix clone()
    {
        Matrix newMatrix = new Matrix();
       
        newMatrix.connMatrix = new double[this.connMatrix.length][ this.connMatrix.length];
        for(int i = 0; i < this.connMatrix.length; i++ )
        {
            newMatrix.connMatrix[i] = Arrays.copyOfRange(connMatrix[i], 0, this.connMatrix.length);
        }
        
        return newMatrix;
    }
    
    public double getMaxValue(int links[][] ) throws SystemFault
    {
        double maxValue = Double.MIN_VALUE;
        
        for(int i = 0; i < links.length; i++ )
        {       
            if(links[i].length > 2)
            {
                SystemFault sf = new SystemFault(SystemFault.SEVERE_ERROR);
                sf.setInspectIntParam(0, i);
                sf.setInspectIntParam(1, links[i].length);
                throw sf;
            }
            
            int x = links[i][0];
            int y = links[i][1];
            if( this.connMatrix[x][y] > maxValue )
            {
                maxValue = this.connMatrix[x][y];
            }
        }
        
        return maxValue;
    }
    
    public double getMinValue(int links[][] ) throws SystemFault
    {
        double minValue = Double.MAX_VALUE;
        
        for(int i = 0; i < links.length; i++ )
        {       
            if(links[i].length > 2)
            {
                SystemFault sf = new SystemFault(SystemFault.SEVERE_ERROR);
                sf.setInspectIntParam(0, i);
                sf.setInspectIntParam(1, links[i].length);
                throw sf;
            }
            
            int x = links[i][0];
            int y = links[i][1];
            if( this.connMatrix[x][y] < minValue )
            {
                minValue = this.connMatrix[x][y];
            }
        }
        
        return minValue;
    }   
    
    public void decSymValueofAllEntry(int links[][])throws SystemFault
    {
        double minValue = Double.MAX_VALUE;
        
        for(int i = 0; i < links.length; i++ )
        {       
            if(links[i].length > 2)
            {
                SystemFault sf = new SystemFault(SystemFault.SEVERE_ERROR);
                sf.setInspectIntParam(0, i);
                sf.setInspectIntParam(1, links[i].length);
                throw sf;
            }
            
            int x = links[i][0];
            int y = links[i][1];
            
            this.connMatrix[x][y]--;
            this.connMatrix[y][x]--;
       
        }
    }
    
    public double get(final int y, final int x)
    {
        return this.connMatrix[y][x];
    }
    
    public void set(final int y, final int x, final double value)
    {
        this.connMatrix[y][x] = value;
    }    
    
    public int[][] getSDArrayDescOrder() throws SystemFault
    {
        int numOfSDPairs = 0, currSDPairIndex = 0;
        int sdArray[][];
        int sortedSDArray[][];
        double costOfSDArray[];
        
        for( int i = 0; i < this.getNumOfRow(); i++ )
        {
            for( int j = 0; j < this.getNumOfColumn(); j++ )
            {
                if ( this.connMatrix[i][j] != 0 )
                {
                    numOfSDPairs++;
                }
            }
        }
        
        sdArray = new int[numOfSDPairs][2];
        sortedSDArray = new int[numOfSDPairs][2];
        costOfSDArray = new double[numOfSDPairs];
        
        for( int i = 0; i < this.getNumOfRow(); i++ )
        {
            for( int j = 0; j < this.getNumOfColumn(); j++ )
            {
                if ( this.connMatrix[i][j] != 0 )
                {
                    sdArray[currSDPairIndex][0] = i;
                    sdArray[currSDPairIndex][1] = j;
                    costOfSDArray[currSDPairIndex] = this.connMatrix[i][j]; 
                    currSDPairIndex++;
                }
            }
        }        
        
        for(int k = 0; k < sortedSDArray.length; k++)
        {
            double maxValue = Double.MIN_VALUE;
            int maxValueIndex = -1;
            /*Search for current minimum values index*/
            for( int i = 0; i < sdArray.length; i++)
            {
                if( maxValue < costOfSDArray[i] && costOfSDArray[ i ] != -1)
                {
                    maxValue = costOfSDArray[i];
                    maxValueIndex = i;
                }
            }
            
            if( maxValueIndex == -1 )
                throw new SystemFault(SystemFault.SEVERE_ERROR);
            
            sortedSDArray[k][0] = sdArray[ maxValueIndex][0];
            sortedSDArray[k][1] = sdArray[ maxValueIndex][1];
            
            costOfSDArray[ maxValueIndex ] = -1;
        }
        
        
        return sortedSDArray;
    }
    
    /**
     * Makes the entries as zero, in the links of path
     * @param path: 1->4->5 = (1,4) (4,5)
     * 
     * 
     */
    public boolean usePath( int path[] ) throws SystemFault
    {        
        for( int i = 0; i < path.length - 1; i++ )
        {
            int transmit = path[i];
            int receive = path[i+1];
           
            /*Path cannot be established, It is a control check. It is not expected to be occured*/
            /*Because path sould be established, if shortest path algorithm finds one*/
            //WARNING
            /*if ( (--this.connMatrix[transmit][receive]) < 0 || (--this.connMatrix[receive][transmit] < 0) )*/
            if ( (--this.connMatrix[transmit][receive]) < 0  )            
            {
                throw new SystemFault(SystemFault.SEVERE_ERROR);
            }

            /*Path has already been deleted. Or there is not path like this in connection matrix*/
            /*if( this.connMatrix[transmit][receive] == 0 || this.connMatrix[receive][transmit] == 0)*/
            if( this.connMatrix[transmit][receive] == 0 )
            {
                return false;
            }
            this.connMatrix[transmit][receive] = 0;
            /*this.connMatrix[receive][transmit] = 0;*/
           
        }

        return true;
    }    
    
    /*
     * For paths include bidirectional links
     */
    public boolean isValidPath( int path[])
    {
        boolean pathValid = true;
        
        for( int i = 0; i < path.length - 1; i++ )
        {
            int transmit = path[i];
            int receive = path[i+1];
            
            if( this.connMatrix[transmit][receive] <= 0 || this.connMatrix[receive][transmit] <= 0 )
            {
                pathValid = false;
                break;
            }
        }
        
        return pathValid;
    }
    
    public static int [] reverseArray(int inArray[]) throws SystemFault
    {
        int newArray[];
        newArray = new int[inArray.length];
        
        int currIndex = 0;
        for( int i = inArray.length - 1; i >= 0; i-- )
        {
            newArray[currIndex] = inArray[i];
            currIndex++;
        }
        
        if( newArray.length != inArray.length )
        {
            throw new SystemFault(SystemFault.SEVERE_ERROR);
        }
        return newArray;
    }


    public double getMaxValidValue() {
        return maxValidValue;
    }

    public double getMinValidValue() {
        return minValidValue;
    }
    
    public void changeElemValue(int columnIndex, int rowIndex)
    {
        
    }
    
    public void initialize()
    {
        
    }
    
    public static double[][][] cloneMatrix(double inMatrix[][][])
    {
        double [][][]temp;
        
        temp = new double[ inMatrix.length ][ inMatrix[0].length ][ inMatrix[0][0].length ];
        
        for (int i = 0; i < temp.length; i++) 
        {
            for (int j = 0; j < temp[0].length; j++) 
            {
                for (int k = 0; k < temp[0][0].length; k++) 
                {
                    temp[i][j][k] = inMatrix[i][j][k];
                }                        
            }            
        }
        return temp;
    }
    
    public static int getMaxValueIndex(double inArray[])
    {
        double max;
        int maxIndex = 0;
        max = inArray[maxIndex];
        for( int i = 0; i < inArray.length; i++ )
        {
            if( inArray[i] >= max )
            {
                max = inArray[i];
                maxIndex = i;
            }
        }
        
        return maxIndex;
    }
    
    public static int getMinValueIndex(double inArray[], double notEqualToValue)
    {
        double max = Double.MAX_VALUE;
        int maxIndex = 0;
        max = inArray[maxIndex];
        for( int i = 0; i < inArray.length; i++ )
        {
            if( inArray[i] < max && inArray[i] != notEqualToValue )
            {
                max = inArray[i];
                maxIndex = i;
            }
        }
        
        return maxIndex;
    }    
    
    public static double getMax(double []inArray)
    {
        double result = Double.MIN_VALUE;
        
        for (int i = 0; i < inArray.length; i++) {
            if( inArray[i] > result )
                result = inArray[i];
        }
        return result;        
    }
    
    public static double getMin( double []inArray)
    {
        double result = Double.MAX_VALUE;
        
        for (int i = 0; i < inArray.length; i++) {
            if( inArray[i] < result )
                result = inArray[i];
        }
        return result;
    }    

    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public double countValuesInARow(int row)
    {
        double result = 0;
        for( int i = 0; i < this.connMatrix[row].length; i++ )
        {
            result += this.connMatrix[row][i];
        }
        return result;
    }
    
    public double countValuesInAColumn(int column)
    {
        double result = 0;
        for( int i = 0; i < this.connMatrix.length; i++ )
        {
            result += this.connMatrix[i][column];
        }
        return result;
    }    
}
