/*
* Filename: PrufferNumber.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

import java.util.Arrays;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class PrufferNumber 
{
    /**
     * Same entry can exist more than once
     */
    private int innerNumbers[];
    
    /*First true value will return the smallest outer number*/
    boolean isOuterNumbers[];
    
    private int sizeOfTree;    
    
    private final static int    EMPTY = -1;
    
    private PrufferNumber()
    {
        
    }
    
    public PrufferNumber(int sizeOfTree)
    {
        this.sizeOfTree = sizeOfTree;
        
        this.innerNumbers = new int[this.sizeOfTree - 2];
        
        isOuterNumbers = new boolean[this.sizeOfTree];
        
        Arrays.fill(isOuterNumbers, true);  /*At first, each number is an outer number*/
        
        /*DIKKAT: YENI EKLEDIM*/
        Arrays.fill(innerNumbers, EMPTY);  
    }
    
    public int [][] generateConnectionMatrix() throws SystemFault
    {
        int connectionMatrix[][];
        int mostLeftInnerNumber, smallestOuterNumber,mostLeftInnerNumberIndex;
        Node innerNode, outerNode;
        /*Take memory for nodes*/
        Node nodes[];
        nodes = new Node[this.sizeOfTree];
        
        for(int i = 0; i < this.sizeOfTree; i++)
        {
            /*a node with index i, and degree of ( sizeOfTree - 1 ) will be created*/
            nodes[i] = new Node(i, this.sizeOfTree - 1 );
        }
        
        /*Generate PrufferNumbers randomly*/
        this.generateRandomNumbers();
        
        /*Search for each inner Number*/
        for( int i = 0; i < this.innerNumbers.length; i++ )
        {
            if( this.innerNumbers[i] == PrufferNumber.EMPTY )
                continue;

            mostLeftInnerNumberIndex = this.mostLeftInnerNumberIndex();
            /*Check if any number is left*/
            if( mostLeftInnerNumberIndex == this.innerNumbers.length )
                break;

            mostLeftInnerNumber = this.innerNumbers[mostLeftInnerNumberIndex];
            smallestOuterNumber = this.smallestOuterNumber();

            innerNode = nodes[mostLeftInnerNumber];
            outerNode = nodes[smallestOuterNumber];

            /*Set Relations*/
            outerNode.setParent(innerNode);
            innerNode.insertPredecessor(outerNode);

            /*Arrange outerNumbers*/
            this.arrangeInnerNumbers(mostLeftInnerNumber, smallestOuterNumber);

        }
        
        int lastOuterNumber1, lastOuterNumber2;
        
        lastOuterNumber1 = this.smallestOuterNumber();
        this.isOuterNumbers[lastOuterNumber1] = false;
        lastOuterNumber2 = this.smallestOuterNumber();
        
        innerNode = nodes[lastOuterNumber1];
        outerNode = nodes[lastOuterNumber2]; 
        /*Set Relations*/
        outerNode.setParent(innerNode);
        innerNode.insertPredecessor(outerNode);        
        
        this.checkRootNode(nodes);
        /*Take memory*/
        connectionMatrix = new int[this.sizeOfTree][this.sizeOfTree];       
        for( int i = 0 ; i < this.sizeOfTree; i++ )
        {
            connectionMatrix[i] = new int[this.sizeOfTree];
            Arrays.fill(connectionMatrix[i], 0);
        }
        
        int num_of_connection = 0;
        for(int i = 0; i < this.sizeOfTree; i++ )
        {
            if(nodes[i].getParent() != null)
            {
                int rowIndex = nodes[i].getParent().getId();
                int columnIndex = nodes[i].getId();
                connectionMatrix[rowIndex][columnIndex] = 1;
                connectionMatrix[columnIndex][rowIndex] = 1;
                num_of_connection++;
            }
        }
        
        if( num_of_connection != this.sizeOfTree - 1)
        {
            SystemFault sf = new SystemFault(SystemFault.SEVERE_ERROR);
            sf.setInspectIntParam(0, num_of_connection);
            sf.setInspectIntParam(1, this.sizeOfTree);
            throw sf;
        }
        return connectionMatrix;
    }
    
    
    private Node checkRootNode(Node nodes[]) throws SystemFault
    {
        int numOfRoot = 0;
        Node rootNode = null;
        
        for(int i = 0; i < nodes.length; i++)
        {
            if( nodes[i].getParent() == null )
            {
                numOfRoot++;
                rootNode = nodes[i];
            }
        }
        
        if( numOfRoot != 1 )
        {
            throw new SystemFault(SystemFault.SEVERE_ERROR);
        }
        return rootNode;
    }
    
    private void arrangeInnerNumbers(int innerNumber, int outerNumber)
    {
        /*Remove the used outer number*/
        this.isOuterNumbers[outerNumber] = false;
        
        /*Remove the most left inner number*/
        this.innerNumbers[this.mostLeftInnerNumberIndex()] = PrufferNumber.EMPTY;
        
        /*Search if most left innerNumber is still in Pruffer array, if not add it to outerNumber*/
        int i;
        for(i = 0 ; i < this.innerNumbers.length; i++ )
        {
            if(this.innerNumbers[i] == innerNumber)
            {
                break;
            }
        }
        
        if( i == this.innerNumbers.length )
        {
            this.isOuterNumbers[innerNumber] = true;
        }
    }
    
    private int mostLeftInnerNumberIndex()
    {
        int index = this.innerNumbers.length;
        /*Search for each inner Number*/
        for( int i = 0; i < this.innerNumbers.length; i++ )
        {
            if( this.innerNumbers[i] != PrufferNumber.EMPTY )
            {
                index = i;
                break;
            }
        }    
        return index;
    }
   
    
    private int smallestOuterNumber()
    {
        int number = PrufferNumber.EMPTY;
        
        for( int i = 0; i < this.isOuterNumbers.length; i++ )
        {
            if( this.isOuterNumbers[i] == true )
            {
                number = i;
                break;
            }
        }
        return number;
    }
    
    private void generateRandomNumbers()
    {   
        
        for( int i = 0; i < innerNumbers.length; i++ )
        {
            this.innerNumbers[i] = RandomGenerator.genInt(this.sizeOfTree);
        }
        
        /*Set isOuterNumber array*/
        for( int j = 0; j < this.innerNumbers.length; j++ )
        {
            this.isOuterNumbers[ this.innerNumbers[j] ] = false;
        }
        
    }
   
    public static void main(String args[])
        {
            try {
                int connMatrix[][];
                PrufferNumber prufferNumber = new PrufferNumber(14);
                
                connMatrix = prufferNumber.generateConnectionMatrix();
                
                for(int i = 0; i < connMatrix.length ; i++)
                {
                    for(int j = 0; j < connMatrix[i].length; j++ )
                    {
                        System.out.print( connMatrix[i][j]);
                    }
                    System.out.println(" ");
                }
            
            } catch (SystemFault ex) {
                ex.handler();
            }catch(Exception ex)
            {     
                ex.printStackTrace();
            }
        }    
    
}
