/*
* Filename: RestictedCandidateList.java
* Author:   Ali KELES
*
*/


package hh.algorithm.GRASP;

import hh.algorithm.com.RandomGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class RestictedCandidateList {

    private ArrayList<RCLEntry>     entries;
    
    public RestictedCandidateList()
    {
        entries = new ArrayList<RCLEntry>();
    }
        
    public void fillEntries( int sdPairs[][], int heuristics[] )
    {
        for(int i = 0; i < sdPairs.length; i++ )
        {                    
            RCLEntry rclEntry = new RCLEntry();        
            rclEntry.setSdPair( sdPairs[i] );
            rclEntry.setHeuristic( heuristics[i] );
            rclEntry.setExperience( GRASPParams.initialExperience );
            entries.add(rclEntry);
        }
    }
    
    public void sort()
    {            
            Collections.sort( entries );             
    }
    
    public RCLEntry get(int i)
    {
        return entries.get(i);
    }
    
    public int size()
    {
        return entries.size();
    }
    
    public RCLEntry getBestAvailable()
    {
        for( int i = 0; i < this.entries.size(); i++ )
        {
            if( entries.get(i).isValid() )
                return entries.get(i);
        }
        return null;
    }
    
    public RCLEntry getRandomFromAvailable()
    {
        ArrayList<Integer>  availableIndexes = new ArrayList();
        
        for( int i = 0; i < this.entries.size(); i++ )
        {
            if( entries.get(i).isValid() )
            {
                availableIndexes.add( new Integer(i) );
            }
        }      
        
        if( availableIndexes.size() == 0 )
            return null;
        
        int randomValue = RandomGenerator.genInt( availableIndexes.size() );
        
        randomValue = availableIndexes.get( randomValue );
        
        return entries.get( randomValue );
    }
    
    public void makeAllValid()
    {            
        for(int i = 0; i < this.entries.size(); i++ )
        {
            entries.get(i).setValid(true);            
        }                                        
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append( "RCL START" );
        for( int i = 0; i < entries.size(); i++)
            buffer.append( entries.get(i).getSdPair()[0] + ", " + entries.get(i).getSdPair()[1] + "\n");
        buffer.append( "RCL END" );
        
        return buffer.toString();
    }
        
}
