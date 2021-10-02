/*
* Filename: SystemFault.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class SystemFault extends Exception{
    
    public static final int     SEVERE_ERROR = 0;
    
    private int faultType;
    
    public static final int     NUM_OF_PARAMS = 4;
    
    private double inspectDoubleParams[] = new double[NUM_OF_PARAMS];
    
    private boolean doubleParamsValid[] = new boolean[NUM_OF_PARAMS];
    
    private int inspectIntParams[] = new int[NUM_OF_PARAMS];
    
    private boolean intParamsValid[] = new boolean[NUM_OF_PARAMS];
    
    private String inspectStringParams[] = new String[NUM_OF_PARAMS];
    
    private boolean StringParamsValid[] = new boolean[NUM_OF_PARAMS];
    
    private SystemFault()
    {
        
    }
    
    public SystemFault( int faultType )
    {
        this.faultType = faultType;
        
        for(int i = 0; i < SystemFault.NUM_OF_PARAMS; i++ )
        {
            this.intParamsValid[i] = false;
            this.doubleParamsValid[i] = false;
            this.StringParamsValid[i] = false;
        }
    }
    
    public void setInspectIntParam( final int index, final int intParam)
    {
        this.inspectIntParams[index] = intParam;
        this.intParamsValid[index] = true;
    }
    
    public void setInspectDoubleParam( final int index, final int doubleParam)
    {
        this.inspectDoubleParams[index] = doubleParam;
        this.doubleParamsValid[index] = true;
    }
    
    public void setInspectStringParam( final int index, final String stringParam)
    {
        this.inspectStringParams[index] = stringParam;
        this.StringParamsValid[index] = true;
    }    
    
    public String toString()
    {
        
        String faultDumpString = "Fault Dump: \n";
        
       faultDumpString += "Fault Type: " + this.faultType;
        
        for(int i = 0; i < SystemFault.NUM_OF_PARAMS; i++ )
        {
            if(this.intParamsValid[i])
            {
                faultDumpString += "\nInt Param " + i + "= " + String.valueOf(this.inspectIntParams[i]) ;
            }
        }

        for(int i = 0; i < SystemFault.NUM_OF_PARAMS; i++ )
        {
            if(this.doubleParamsValid[i])
                faultDumpString += "\nDouble Param " + i + "= " + String.valueOf(this.inspectDoubleParams[i]) ;
        }
        
        for(int i = 0; i < SystemFault.NUM_OF_PARAMS; i++ )
        {
            if(this.StringParamsValid[i])
                faultDumpString += "\nString Param " + i + "= " + String.valueOf(this.inspectStringParams[i]) ;
        }
        
        
        return faultDumpString;
    }
    
    public void handler()
    {
        System.out.print(this);
        
        this.printStackTrace();
    }

}
