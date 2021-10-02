/*
* Filename: LigthPath.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.com.Representation;
import java.util.Arrays;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class LightPath extends Representation implements Cloneable{

    private int                 physicalLinks[];
    
    private int                 wavelengthId;
    
    private static double       capacity = VTDesignParams.wavelengthCapacity;
    
    private boolean             routed;
    
    private boolean             wavelengthAssigned;
    
    private boolean             berConstraintSatisfied;
    
    private double              BER;
        

    
    public LightPath() 
    {        
        routed = false;
        wavelengthAssigned =false;
        berConstraintSatisfied = false;
    }
    
    public LightPath( final double capacity)
    {
        this();
        this.capacity = capacity;        
    }
    
    
    public double getCost()
    {
        return physicalLinks.length;
    }
    
    public void setPhysicalLinks(int physicalLinks[])
    {
        this.physicalLinks = Arrays.copyOfRange(physicalLinks, 0, physicalLinks.length);
    }
    
    public boolean isSDEqual(LightPath inLightPath)
    {
        if( this.getSourceNode() != inLightPath.getSourceNode() )
            return false;
        if( this.getDestinationNode() != inLightPath.getDestinationNode() )
            return false;
        return true;
    }
    
    public int[]getPhysicalLinks()
    {
        return this.physicalLinks;
    }
    
    public int getDestinationNode()
    {
        return this.physicalLinks[physicalLinks.length - 1];
    }
    
    
    public int getSourceNode()
    {
        return this.physicalLinks[0];
    }

    public int getWavelengthId() {
        return wavelengthId;
    }

    public void setWavelengthId(int wavelengthId) {
        this.wavelengthId = wavelengthId;
    }

    public static double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }
    
   public Representation clone()
    {
        LightPath newTf = new LightPath();
       
        newTf.capacity = this.getCapacity();
        
        newTf.wavelengthId = this.wavelengthId;
        
        for(int i = 0; i < this.physicalLinks.length; i++ )
        {
            newTf.physicalLinks = Arrays.copyOfRange( this.physicalLinks, 0, this.physicalLinks.length );
        }
        
        newTf.setRouted(this.isRouted());                
        
        newTf.setWavelengthAssigned(this.isWavelengthAssigned());
        
        newTf.setBerConstraintSatisfied(this.isBerConstraintSatisfied());
        
        return (Representation)newTf;
    } 
   
   public void prinLinks()
   {
       for (int i = physicalLinks.length - 1; i >= 0 ; i--) 
       {
           System.out.print( (physicalLinks[i] + 1) + ", ");
       }
       System.out.println("");
   }
   
   public String getPrinLinks()
   {
       String data = "";
       for (int i = 0; i < physicalLinks.length ; i++) 
       {
           data += (physicalLinks[i] + 1) + ", ";
       }
    
       return data;
   }

    public boolean isRouted() {
        return routed;
    }

    public void setRouted(boolean routed) {
        this.routed = routed;
    }

    public boolean isWavelengthAssigned() {
        return wavelengthAssigned;
    }

    public void setWavelengthAssigned(boolean wavelengthAssigned) {
        this.wavelengthAssigned = wavelengthAssigned;
    }
    

    @Override
    public int[][] getSDArray() {
       int sdArray[][];
        
        sdArray = new int[1][2];
        
        sdArray[0][0] = this.getSourceNode();
        sdArray[0][1] = this.getDestinationNode();
        
        return sdArray;
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isBerConstraintSatisfied() {
        return berConstraintSatisfied;
    }

    public void setBerConstraintSatisfied(boolean berConstraintSatisfied) {
        this.berConstraintSatisfied = berConstraintSatisfied;
    }

    public double getBER() {
        return BER;
    }

    public void setBER(double BER) {
        this.BER = BER;
    }

}

