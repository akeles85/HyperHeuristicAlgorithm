/*
* Filename: Arc.java
* Author:   Ali KELES
*
*/


package hh.algorithm.AntColony.com;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class Arc {

    private Node        start;
    private Node        end;
    private double      phenomone;
    private double      heuristic;
    
    private Arc()
    {
        
    }
    
    public Arc(Node start, Node end)
    {
        this.start = start;
        this.end = end;
    }

    public double getPhenomone() {
        return phenomone;
    }

    public void setPhenomone(double phenomone) {
        this.phenomone = phenomone;
    }

    public Node getStart() {
        return start;
    }

    public Node getEnd() {
        return end;
    }

    public double getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(double heuristic) {
        this.heuristic = heuristic;
    }
    
}
