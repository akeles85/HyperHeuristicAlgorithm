/*
* Filename: Representation.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public abstract class Representation implements Cloneable, Comparable{
    
    abstract public int [][]getSDArray();
    
    abstract public void initialize();
    
    abstract public Representation clone();
        
}
