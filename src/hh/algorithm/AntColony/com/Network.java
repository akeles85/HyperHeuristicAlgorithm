/*
* Filename: Network.java
* Author:   Ali KELES
*
*/


package hh.algorithm.AntColony.com;

import java.util.ArrayList;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class Network {

        private ArrayList<Node> nodes;
        
        private Arc             arcs[][];
               
        public Network()
        {
            
        }
        
        public Network(int numberOfNode)
        {
            this.nodes = new ArrayList<>();
            
            for(int i = 0; i < numberOfNode; i++ )
            {
                this.nodes.add( new Node(i) );
            }
            
            this.arcs = new Arc[numberOfNode][numberOfNode];
            
            /*FULLY CONNECTED NETWORK: Each node connected to each other
             * Each node connected to each other
             * A node is connected to itself
             */
            for( int i = 0; i < numberOfNode; i++ )
            {
                for( int j = 0; j < numberOfNode; j++)
                {
                    this.arcs[i][j] = new Arc( this.nodes.get(i), this.nodes.get(j));
                }
            }            
        }
                
        
        public ArrayList<Node> getNeighbours(Node inNode)
        {
            return this.nodes;
        }
        
        
        public int getNumOfNode()
        {
            return this.nodes.size();
        }
        
        public Node getNode(int i )
        {
            return this.nodes.get(i);
        }
        
        public int getNumOfArc()
        {
            return this.arcs.length * this.arcs[0].length;
        }
        
        public Arc getArc(int i)
        {            
            int row = i / this.arcs[0].length;
            int column =  i - row * this.arcs[0].length;
            return this.arcs[row][column];
        }
        
        public double getPhenomone(Node start, Node end)
        {
            return this.getArc(start, end).getPhenomone();
        }
        
        public double getHeuristic(Node start, Node end)
        {
            return this.getArc(start, end).getHeuristic();
        }
        
        public Arc getArc(Node start, Node end)
        {
            return this.arcs[ start.getIndex() ][ end.getIndex() ];
        }
        
    public String toString()
    {
        StringBuffer result = new StringBuffer("");
        
        result.append("Network:");
        result.append("\n");
        for(int i = 0; i < this.arcs.length; i++)
        {            
            for( int j = 0; j < this.arcs[i].length; j++ )
            {
                Arc currentArc = this.arcs[i][j];
                result.append( "( " + currentArc.getStart().getIndex() + ", " + currentArc.getEnd().getIndex() + " ) : " + currentArc.getPhenomone());                        
                result.append("\n");
            }
        }
        return result.toString();        
    }

    
    public ArrayList<Node> getNodes() {
        return nodes;
    }    
        
}
