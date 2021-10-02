/*
* Filename: ObjectArrayRepresentation.java
* Author:   Ali KELES
*
*/


package hh.algorithm.representation;

import hh.algorithm.com.ComparableObject;
import hh.algorithm.com.Copyable;
import hh.algorithm.com.RandomGenerator;
import hh.algorithm.com.Representation;
import hh.algorithm.on.com.LightPath;
import hh.algorithm.on.com.LightPathRouterHeuristic;
import java.util.Arrays;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class ObjectArrayRepresentation extends Representation implements Cloneable{

    private LightPathRouterHeuristic[] array;    
    private LightPathRouterHeuristic[] domain;
    
    public LightPathRouterHeuristic get(int i)
    {
        return array[i];
    }
    
    public void set(int index, LightPathRouterHeuristic inObject)
    {
        array[ index ] = inObject;        
    }
    
    public void changeElemValue(int index)
    {       
        LightPathRouterHeuristic newOne = null;
        do
        {
            int domainChoice = RandomGenerator.genInt( domain.length );       
            newOne = (LightPathRouterHeuristic)domain[domainChoice].clone();
        }while( array[index].compareTo(newOne) == 0 && domain.length > 1);
        
        this.array[ index ] = newOne;
    }
    
    public void setElemValue(int index, int newValueDomainIndex)
    {               
        Object newValue = this.domain[newValueDomainIndex];
        this.array[ index ] = (LightPathRouterHeuristic) newValue;
    }      
    
    public LightPathRouterHeuristic[] getArray()
    {
        return array;
    }
    
    public int size()
    {
        return array.length;
    }
    
    public void setArray( LightPathRouterHeuristic[] inArray)
    {
        array = inArray;
    }
    
    
    @Override
    public int[][] getSDArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Representation clone() {
        ObjectArrayRepresentation newRep = new ObjectArrayRepresentation();
        
        newRep.array = Arrays.copyOf(this.array, this.array.length);
        
        for (int i = 0; i < array.length; i++) {
            newRep.array[i] = (LightPathRouterHeuristic)this.array[i].clone();
        }
        
        newRep.domain = Arrays.copyOf(this.domain, this.domain.length);
        
        for (int i = 0; i < domain.length; i++) {
            newRep.domain[i] = (LightPathRouterHeuristic)this.domain[i].clone();
        }        
        
        return newRep;
    }
    

    public void setDomain(LightPathRouterHeuristic[] domain) {
        this.domain = domain;
    }

    public int compareTo(Object o) {
        
        boolean result = true;
        ObjectArrayRepresentation inObject = (ObjectArrayRepresentation)o;
        
        for( int i = 0; i < this.array.length; i++ )
        {
            if( inObject.array[i].compareTo( this.array[i]) != 0)
            {
                result = false;
                break;
            }
        }
        
        if( result == true )
            return 0;
        else
            return -1;
        
    }

}
