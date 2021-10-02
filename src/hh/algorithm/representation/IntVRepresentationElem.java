/*
* Filename: IntVGene.java
* Author:   Ali KELES
*
*/


package hh.algorithm.representation;

import java.util.Arrays;

import hh.algorithm.com.RandomGenerator;
import hh.algorithm.com.SystemFault;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class IntVRepresentationElem extends RepresentationElem{

    private int maxBound;
    
    private int minBound;
    
    private static final int degreeOfRelation = 1;
    
    private Integer value = 0;
    
    
    public IntVRepresentationElem()
    {
        super();
        this.maxBound = (int) 10;
        this.minBound = (int) 0;
    }
    
    public IntVRepresentationElem(int value)
    {
        this();
        try {
            this.setValue(value);
        } catch (SystemFault ex) {
            ex.printStackTrace();
        }
    }    
    
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) throws SystemFault 
    {
        this.value = (Integer)value;
        if( this.isAtBounds() == false)
            throw new SystemFault(SystemFault.SEVERE_ERROR);
    }
    
    public int getMaxBound() {
        return maxBound;
    }

    public int getMinBound() {
        return minBound;
    }    
    
    /**
     * Checks if a gene is at the bounds
     *
     */
    public boolean isAtBounds()
    {    
        if(this.value >= this.getMinBound() && this.value <= this.getMaxBound())
            return true;
        
        else
            return false;
    }

    
    public void changeValue( ) throws SystemFault
    {
        int newValue;
        do
        {
            newValue = RandomGenerator.genInt(this.maxBound);            
        }while( this.value == newValue  );
        
        this.value = newValue;
    }
    
    public void changeValue( RepresentationElem restrictedGenes[] ) throws SystemFault
    {
        int newValue = 0;
        int restrictedValues[] = new int[restrictedGenes.length];
        int numOfRestricted[] = new int[degreeOfRelation];
        
        Arrays.copyOf(numOfRestricted, 0);
        
        for (int i = 0; i < restrictedValues.length; i++) {
            restrictedValues[i] = (Integer)restrictedGenes[i].getValue();
            numOfRestricted[ (int)restrictedValues[i] ]++;
            
            if( numOfRestricted[ (int)restrictedValues[i] ] > 1)
            {
                throw new SystemFault( SystemFault.SEVERE_ERROR );
            }
        }
                        
        do
        {
            newValue = RandomGenerator.genInt(this.maxBound, restrictedValues);            
        }while( this.value.equals(newValue) );
        
        
        this.value = newValue;
    }
    
    public void initialize()
    {
        this.value = (int)RandomGenerator.genRealNum(this.minBound, this.maxBound);
    }    
    
    public RepresentationElem clone() 
    {
        IntVRepresentationElem gene = new IntVRepresentationElem();
        try {
            gene.setValue(this.getValue());
        } catch (SystemFault ex) {
            ex.printStackTrace();
        }
        return gene;
    }
    
    @Override
    public String toString() 
    {
        StringBuffer result = new StringBuffer( "" + this.value.intValue() );      
        
        return result.toString();
    }
    
    @Override
    public boolean isZero() {
        if( this.value == 0)
            return true;
        else
            return false;
    }    
}
