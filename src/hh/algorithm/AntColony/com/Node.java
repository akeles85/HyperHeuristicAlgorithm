/*
 * Node.java
 *
 * Created on December 16, 2007, 1:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package hh.algorithm.AntColony.com;

/**
 *
 * @author john_locke
 */
public class Node {
    
    /** index that is used to get the node within an array */
    private int index;  
    
    public int transmitter;
    
    public int receiver;
    
    private boolean valid = true;
    
    /** Creates a new instance of Node */
    private Node() {
                     
    }   
    
    public Node(int inIndex)
    {
        setIndex(inIndex);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }        
    
    
    public String toString()
    {
        StringBuffer result = new StringBuffer("");
        
        result.append(this.index);
        return result.toString();        
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
