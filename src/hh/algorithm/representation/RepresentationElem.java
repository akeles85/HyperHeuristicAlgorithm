/*
* Filename: RepresentationElem.java
* Author:   Ali KELES
*
*/


package hh.algorithm.representation;

import hh.algorithm.com.SystemFault;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public abstract class RepresentationElem implements Cloneable{
   
    public static final int     BINARY = 0;
    
    public static final int     REALV = 1;
    
    public static final int     INTV = 2;   
    
    public RepresentationElem()
    {        
    }
    
    public abstract Object getValue();
    
    public abstract void setValue(Object value)throws SystemFault;
    
    @Override
    public abstract RepresentationElem clone();
    
    public abstract void changeValue() throws SystemFault;
    public abstract void changeValue(RepresentationElem restrictedNumbers[]) throws SystemFault;
    
    public abstract void initialize();
    
    public abstract boolean isZero();
   
    
}
