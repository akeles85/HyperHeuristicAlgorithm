/*
* Filename: BinaryGene.java
* Author:   Ali KELES
*
*/


package hh.algorithm.representation;

import hh.algorithm.com.RandomGenerator;
import hh.algorithm.com.SystemFault;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class BinaryRepresentationElem extends RepresentationElem{

    private Boolean value = new Boolean(false);
    
    public Boolean getValue() {
        return value;
    }
    
    public void setValue(Object value) 
    {
        this.value = (Boolean)value;
    }
    
    public void changeValue(RepresentationElem restrictedNumbers[]) throws SystemFault
    {
        this.changeValue();
    }
    
    public void changeValue( ) throws SystemFault
    {
        this.value = !this.value.booleanValue();
    }    
    
    public void initialize()
    {
        if ( RandomGenerator.genDouble() < 0.5)
               this.value = false;
        else
            this.value = true;
    }
    
    public RepresentationElem clone()
    {
        BinaryRepresentationElem binaryGene = new BinaryRepresentationElem();
        binaryGene.setValue(this.getValue());
        return binaryGene;
    }            
    
    public String toString() 
    {
        StringBuffer result = new StringBuffer(this.value + " ");
        
        return result.toString();
    }


    @Override
    public boolean isZero() {
        if( this.value == true)
            return false;
        else
            return true;
    }

}
