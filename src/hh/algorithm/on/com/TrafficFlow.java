/*
* Filename: TrafficFlow.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.com.Matrix;
import java.util.Arrays;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class TrafficFlow implements Cloneable{
    
    private int         virtualLinks[];
    
    private double     capacity;

    
    public TrafficFlow() {
    }
          
    
    public void setPhysicalLinks(int virtualLinks[])
    {
        this.virtualLinks = Arrays.copyOfRange(virtualLinks, 0, virtualLinks.length);
    }
    
    public int getNumOfHops()
    {
        return this.virtualLinks.length;
    }
    
    public int getSourceNode()
    {
        return this.virtualLinks[0];
    }
    
    
    public int getDestinationNode()
    {
        return this.virtualLinks[virtualLinks.length - 1];
    }


    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }
    
   public Object clone()
    {
        TrafficFlow newTf = new TrafficFlow();
       
        newTf.capacity = this.capacity;
        
        for(int i = 0; i < this.virtualLinks.length; i++ )
        {
            newTf.virtualLinks = Arrays.copyOfRange( this.virtualLinks, 0, this.virtualLinks.length );
        }
        
        return (Object)newTf;
    }
   
    public String toString()
    {
        StringBuffer result = new StringBuffer("");
        

        result.append("C: " + this.capacity);
        
        result.append("L: ");        
        for(int i = 0; i < this.virtualLinks.length; i++)
        {
            result.append( (this.virtualLinks[i]+1) + ", ");
        }
            
  
        return result.toString();        
    }
    
    public static double getMaxLinkFlow( TrafficFlow tfs[])
    {
        double flowMatrix[][] = new double[VTDesignParams.numOfNode][VTDesignParams.numOfNode];
        
        for (int i = 0; i < flowMatrix.length; i++) {
            Arrays.copyOf(flowMatrix[i], 0);
        }
        
        for (int i = 0; i < tfs.length; i++) 
        {
            for (int j = 0; j < tfs[i].virtualLinks.length - 1; j++) 
            {
                int transmit = tfs[i].virtualLinks[j];
                int receive = tfs[i].virtualLinks[j+1];
                flowMatrix[transmit][receive] += tfs[i].getCapacity();                
            }
        }
        
        Matrix flow = new Matrix();
        flow.setData( flowMatrix );
        return flow.getMaxValueEntry();
    }
    
   public void prinLinks()
   {
       for (int i = 0; i < virtualLinks.length ; i++) 
       {
           System.out.print( (virtualLinks[i] + 1) + ", " );
       }
       System.out.println("");
   }    
   
   public String getPrinLinks()
   {
       String data = "";
       for (int i = 0; i < virtualLinks.length ; i++) 
       {
           data += String.valueOf( virtualLinks[i] + 1 ) + ", " ;
       }
       return data;
   }
    

}

