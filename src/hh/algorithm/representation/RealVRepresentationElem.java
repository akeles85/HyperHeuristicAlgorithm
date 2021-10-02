/*
* Filename: RealVGene.java
* Author:   Ali KELES
*
*/


package hh.algorithm.representation;

import hh.algorithm.com.RandomGenerator;
import hh.algorithm.com.SystemFault;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class RealVRepresentationElem extends RepresentationElem{

    private double maxBound;
    
    private double minBound;
    
    private Double value = new Double(0);
    
    
    public RealVRepresentationElem()
    {
        super();
        this.maxBound = 10;
        this.minBound = 0;
    }
    
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) throws SystemFault 
    {
        this.value = (Double)value;
        if( this.isAtBounds() == false)
            throw new SystemFault(SystemFault.SEVERE_ERROR);
    }
    
    public double getMaxBound() {
        return maxBound;
    }

    public double getMinBound() {
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

    public void changeValue( RepresentationElem restrictedGenes[] ) throws SystemFault
    {
        Double newValue = new Double(0);
        double restrictedValues[] = new double[restrictedGenes.length];
        
        for (int i = 0; i < restrictedValues.length; i++) {
            restrictedValues[i] = (Double)restrictedGenes[i].getValue();
        }
                        
        do
        {
            newValue = RandomGenerator.genRealNum(this.minBound, this.maxBound, restrictedValues);            
        }while( this.value.equals(newValue.intValue()) );
        
        this.value = newValue;
    }
    
    public void changeValue( ) throws SystemFault
    {
        double newValue;
        do
        {
            newValue = RandomGenerator.genRealNum(this.minBound, this.maxBound);            
        }while( this.value == newValue  );
        
        this.value = newValue;
    }    
    
    public void initialize()
    {
        this.value = RandomGenerator.genRealNum(this.minBound, this.maxBound);
    }    
    
    public RepresentationElem clone() 
    {
        RealVRepresentationElem realVGene = new RealVRepresentationElem();
        try {
            realVGene.setValue(this.getValue());
        } catch (SystemFault ex) {
            ex.printStackTrace();
        }
        return realVGene;
    }
    
    @Override
    public boolean isZero() {
        if( this.value == 0)
            return true;
        else
            return false;
    }        
}
