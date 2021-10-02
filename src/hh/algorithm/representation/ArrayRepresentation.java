/*
* Filename: ArrayRepresentation.java
* Author:   Ali KELES
*
*/


package hh.algorithm.representation;

import hh.algorithm.com.*;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class ArrayRepresentation extends Representation implements Cloneable{
    
    private RepresentationElem        elements[];  //Holds the parameters        
        
    
    private ArrayRepresentation() 
    {        
        
    }
    
    public ArrayRepresentation( int numOfElems, int elemType ) throws SystemFault
    {
        this.elements = new RepresentationElem[numOfElems];
        
        for(int i = 0; i < numOfElems; i++ )
        {
            if( elemType == RepresentationElem.BINARY )
            {
                elements[i] = new BinaryRepresentationElem();
            }
            else if( elemType == RepresentationElem.REALV )
            {
                elements[i] = new RealVRepresentationElem();
            }
            else if( elemType == RepresentationElem.INTV )
            {
                elements[i] = new IntVRepresentationElem();
            }            
            else
            {
                SystemFault faultHandler = new SystemFault( SystemFault.SEVERE_ERROR );
                faultHandler.setInspectIntParam(0, elemType);
                throw faultHandler;
                
            }
        }
        
    }
    
    /**
     * Initialize the genes in a chromosome
     * This method simply gives random numbers as the value of genes
     */
    public void initialize()
    {    
        for( int i = 0; i < this.elements.length; i++ )
        {
            this.elements[i].initialize();
        }
    }
    
    public void setValue(Object elemValues[], int elemType) throws SystemFault
    {
        if(elemValues.length != this.elements.length )
        {
            throw new SystemFault(SystemFault.SEVERE_ERROR);
        }
        for(int i = 0; i < this.elements.length; i++)
        {
            if( elemType == RepresentationElem.INTV )
            {
                this.elements[i].setValue( new Integer( (Integer)elemValues[i] ) );
            }
            else if( elemType == RepresentationElem.BINARY )
            {
                this.elements[i].setValue( new Boolean( (Boolean)elemValues[i] ) );
            }
            else
                throw new SystemFault(SystemFault.SEVERE_ERROR);
        }
    }
    
        
    /**
     * Get the gene from Chromosome with the given index
     */
    
    public RepresentationElem getElem(final int i)
    {
        return this.elements[i];
    }
    
    
    public ArrayRepresentation clone()
    {
        ArrayRepresentation newRepresentation = new ArrayRepresentation();
        
        newRepresentation.elements = this.elements.clone();
        
        return newRepresentation;
    }

    public String toString() 
    {
        StringBuffer result = new StringBuffer("Array Representation: " + "\n");
        
        int size = this.elements.length;
       
        result.append("[ ");
        for(int i = 0; i < size - 1 ; i++)
        {
            RepresentationElem elem = this.elements[i];
            
            result.append( elem.toString() + ", ");
            
        }
        
        result.append(this.elements[size - 1].toString());
        
        result.append(" ]");
        
        return result.toString();
    }


    
    public int length()
    {
        return this.elements.length;
    }
     

    
    public void changeElemValue( final int elemIndex) throws SystemFault
    {

        this.elements[elemIndex].changeValue( );       
        /*
        restrictedValues = new Gene[GAParams.sizeOfGeneRelation + 1];
        startPosition = geneIndex / GAParams.sizeOfGeneRelation;
        startPosition = startPosition * GAParams.sizeOfGeneRelation;
        endPosition = startPosition + GAParams.sizeOfGeneRelation;
        
        for( int i = startPosition; i < endPosition;  i++ )
        {
            restrictedValues[i-startPosition] = (Gene)this.getGene(i).clone();
        }
        
        
        if( GAParams.spaceType == Gene.INTV_GENE )
        {
            restrictedValues[restrictedValues.length - 1 ] = new IntVRepresentationElem();
            restrictedValues[restrictedValues.length - 1 ].setValue( geneIndex / GAParams.sizeOfGeneRelation );
        }
        
        this.genes.get(geneIndex).changeValue( restrictedValues );*/
    }
       

    
    public boolean isFitter( /*Chromosome inChromosome*/)
    {
        /*
        if( GAParams.MINIMIZING_FITNESS_TYPE == true )
        {
            if( this.fitness <= inChromosome.fitness )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            if( this.fitness >= inChromosome.fitness )
            {
                return true;
            }
            else
            {
                return false;
            }*/
        return true;
        }
    
        public boolean []toBooleanArray()
        {
            boolean []boolArray = new boolean[ this.elements.length ];
            
            for (int i = 0; i < boolArray.length; i++) {
                boolArray[i] = (Boolean)this.elements[i].getValue();
            }
            
            return boolArray;
            
        }

    @Override
    public int[][] getSDArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    }
