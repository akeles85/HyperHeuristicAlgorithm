/*
* Filename: VTDesignParams.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class VTDesignParams {
    
      public static final int numOfRun = 1;
      public static final int numOfNode = 14;
      
      public static int numOfRecv = 4, numOfTrans = 4;
      
      public static final int maxOXCsDegree = numOfNode - 1;
      public static final int numOfWavelengths = 16;
      
      public static final boolean DEBUG_ON = false;
      
      public static final int MBPS = 1000000;
      
      public static int  kShortestPath       = 5;      
      
      public static double wavelengthCapacity = (double)45 * (double)VTDesignParams.MBPS* (double)60 * (double)15 / (double)8;
      
      public static final int MAX_NUM_OF_SPARE_WAVELENGTH = 0;            
      
      /*This constant will also be used for:
       * initial experience for GRASP algoritm 
       */
      public static final double  BER_THRESHOLD = Math.pow( 10, -10);
      
      public static final boolean IS_MINIMIZING_FITNESS = false;
      
      public static boolean     IS_SD_PAIRS_READED_FROM_FILE = false;      
      //public static final String      distanceFileName = "c:\\topolojiler\\nsfnet\\PhysicalTopologies\\pt.txt";    
      //public static final String      traficFileName = "c:\\topolojiler\\nsfnet\\VirtualTopologies\\nsfnet_vt4_";      

      public static String      distanceFileName = "c:\\NSFNET_T1.txt";
      public static String      traficFileName = "c:\\NSFNET_Traffic.txt";      
      public static String      currentInspectedParamName = "";
      public static String      topologyName = "";

}
