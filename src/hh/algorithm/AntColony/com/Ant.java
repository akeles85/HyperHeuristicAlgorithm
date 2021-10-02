/*
 * Ant.java
 *
 * Created on December 16, 2007, 5:13 PM
 * Created by @author john_locke
 */

package hh.algorithm.AntColony.com;

import java.util.ArrayList;

/**
 *
 * @author john_locke
 */
public class Ant {
    
    private ArrayList<Node>     path;        
    
    private Node                currentNode;
    
    /** Creates a new instance of Ant */
    public Ant() {
        path = new ArrayList();        
    }
    
    public void addPath(Node inNode)
    {
        this.path.add( inNode );
    }
    
    public void setInitialPosition( Node inNode )
    {
        setCurrentNode(inNode);
        this.path = new ArrayList();
        this.path.add( inNode );                
    }
    
    public void move(Node inNode)
    {
        this.path.add(inNode);
        this.setCurrentNode(inNode);           
    }    
    
    public ArrayList<Node> getPath()
    {
        return path;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }
}
