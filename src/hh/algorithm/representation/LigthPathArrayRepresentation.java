/*
* Filename: LigthPathArray.java
* Author:   Ali KELES
*
*/


package hh.algorithm.representation;

import hh.algorithm.com.Representation;
import hh.algorithm.on.com.LightPath;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class LigthPathArrayRepresentation extends Representation{

    private LightPath[] ligthpaths;        
    
    @Override
    public int[][] getSDArray() 
    {
        int sdArray[][];
        
        sdArray = new int[ligthpaths.length][2];
        for (int i = 0; i < sdArray.length; i++) {
            sdArray[i][0] = ligthpaths[i].getSourceNode();
            sdArray[i][1] = ligthpaths[i].getDestinationNode();
        }
        return sdArray;
    }
    
    public LightPath get(int i)
    {
        return ligthpaths[i];
    }
    
    public LightPath[] getArray()
    {
        return ligthpaths;
    }
    
    public void set(int i, LightPath inLightpath)
    {
        ligthpaths[i] = inLightpath;
    }    
    
    public int size()
    {
        return ligthpaths.length;
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Representation clone() {
        LigthPathArrayRepresentation newRep = new LigthPathArrayRepresentation();
        
        newRep.ligthpaths = new LightPath[this.ligthpaths.length];
        
        for (int i = 0; i < ligthpaths.length; i++) {
            newRep.ligthpaths[i] = (LightPath)this.ligthpaths[i].clone();
        }
        
        return newRep;
    }

    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setLigthpaths(LightPath[] ligthpaths) {
        this.ligthpaths = ligthpaths;
    }
    
    public void setLigthpaths(LightPath ligthpath) {
        this.ligthpaths = new LightPath[1];
        this.ligthpaths[0] = ligthpath;
    }    

}
