/*
* Filename: Node.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class Node {
    
    private int id;
    Node predecessor[];
    private Node parent;

    private Node()
    {
        
    }
    
    public Node(int id, int numOfPredecessor)
    {
        this.id = id;
        this.parent = null;
        this.predecessor = new Node[numOfPredecessor];
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public void insertPredecessor(Node node) throws SystemFault
    {
        int i = 0;
        
        while(true)
        {
            if(this.predecessor[i] != null)
            {
                i++;
                continue;
            }
            this.predecessor[i] = node;         /*Add new node to the first empty entry in array*/
            break;
        }
        
        if( i == this.predecessor.length)
        {
            throw new SystemFault(SystemFault.SEVERE_ERROR);
        }
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
    

}
