/*
* Filename: StatisticCalculator.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class StatisticCalculator 
{           
    public static double upperLimitCI;
    public static double lowerLimitCI;
    
    public static void calculateConfidenceInterval( double CI, double array[]) throws Exception
    {                
        double tValue = getTValue( CI, array.length );
        
        upperLimitCI = mean(array) + tValue * standardError(array);
        lowerLimitCI = mean(array) - tValue * standardError(array);
        
    }
    
    public static double getTValue( double CI, int instanceSize ) throws SystemFault
    {
        double tValue;
        if(CI == 0.95)
        {
            switch(instanceSize)
            {
                case 10:
                    tValue = 2.2281;
                    break;
               case 20:
                    tValue = 2.086;
                    break;                   
                case 200:
                    tValue = 1.9719;
                    break;
               case 220:
                    tValue = 1.9708;
                    break;
                case 3000:
                    tValue = 1.9608;
                    break;                    
                default: 
                    throw new SystemFault(SystemFault.SEVERE_ERROR);                    
            }
        }        
        else if(CI == 0.99)
        {
            switch(instanceSize)
            {
                case 10:                    
                case 200:                    
                default:
                    throw new SystemFault(SystemFault.SEVERE_ERROR);                    
            }
        }  
        else if(CI == 0.90)
        {
            switch(instanceSize)
            {
                case 10:                    
                case 200:                    
                default:
                    throw new SystemFault(SystemFault.SEVERE_ERROR);
            }
        } 
        else
        {
            throw new SystemFault(SystemFault.SEVERE_ERROR);
        }
        
        return tValue;
    }
    
    public static double mean(double array[])
    {
        double mean = 0;
        
        for( int i = 0; i < array.length; i++)
        {
            mean += array[ i ];
        }
        
        mean = mean / (double)array.length;
        return mean;
    }
    
             /*
     * sigma = sqrt( 1 / N * ( sum( square(x_i - x_mean) ) ) )
     */
    public static double standardDeviation(double array[])
    {
        double sigma = 0;
        double sumation = 0;
        double term = 0;
        double mean = 0;
        
        for( int i = 0; i < array.length; i++)
        {
            mean += array[ i ];
        }
        
        mean = mean / (double)array.length;
        
        for( int i = 0; i < array.length; i++)
        {
            term = Math.pow( array[ i ]  - mean, 2 ) ;
            sumation += term;
        }
        
        sigma = Math.sqrt( sumation / (double)array.length );
        
        return sigma;
    }
    
    /*
     *standard_error=standard_deviation/SQRT(N)
     */
    public static double standardError( double array[] )
    {
        double deviation = StatisticCalculator.standardDeviation( array );
        double error = 0;
        
        error = deviation / Math.sqrt( array.length );
        
        return error;
        
    } 
    

}
