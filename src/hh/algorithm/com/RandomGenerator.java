/*
* Filename: RandomGenerator.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class RandomGenerator {
    
    private static Random generator = new Random();
    
    private int prime[];
    
    /** Creates a new instance of RandomGenerator */
    public RandomGenerator()
    {
        prime = new int[1];
        prime[0] = 2;
        
    }
    
    /**
     *   0 <= x < 1
     *  x * ( max - min ) + min
     *   -> min <= x < max
     */
    public static double genRealNum(final double min, final double max)
    {
        double result = ( getGenerator().nextDouble() * ( max - min ) ) + min ;
        
        return result;
    }
    
    public static double genRealNum(final double min, final double max, double restrictedNumbers[]) throws SystemFault
    {
        double result;
        boolean sameWithRestricted;
        int maxNumOfTry = 100;
        int numOfCurTry = 0;
        
        do{
            sameWithRestricted = false;
            numOfCurTry++;
            result = ( getGenerator().nextDouble() * ( max - min ) ) + min ;
            for( int i = 0; i < restrictedNumbers.length; i++ )
            {
                if ( result == restrictedNumbers[i] )
                {
                    sameWithRestricted = true;
                    break;
                }
            }
            
            if( numOfCurTry == maxNumOfTry )
                throw new SystemFault( SystemFault.SEVERE_ERROR );
            
        }while( sameWithRestricted );
        
        return result;
    }    
    
    /**
     *Implementaion is taken from Differential Evolution
     */
    public double halten( final int totalPopulation, final int numOfParam, final int indexOfParam)
    {
        int []primes = this.findPrimeNumbers( totalPopulation );
        
        int p1,p2,i;
        double sum,x;
        
        i = totalPopulation;
        
        p1 = primes[ indexOfParam ];
        
        p2 = p1;
        
        sum = 0;
        
        do{
            x = i % p1;
            
            sum += x / p2;
            
            i =  (int) i / p1;
            
            p2 = p2 * p1;           
            
        }while( i > 0);
        
        return sum;
        
    }
    
    public int[] findPrimeNumbers( final int size )
    {
        int result[];
        
        //If the prime numbers have already been calculated, do not calculate again
        if( size == this.prime.length )
            
            result = this.prime;
        
        else
        {
            PrimeNumber factory = PrimeNumber.getInstance();
            
            result = factory.findPrimeNumbers(size);
            
            this.prime = result;
            
        }
        
        return result;
    }
    
    /**
     *@return 0 <= n <= x - 1
     *
     */
    public static int genInt(final int x)
    {
        int result = getGenerator().nextInt(x);
        return result;
    }
    
    public static int genInt(final int max, int restrictedNumbers[]) throws SystemFault
    {
        int result;
        boolean sameWithRestricted;
        int maxNumOfTry = 1000;
        int numOfCurTry = 0;
        
        do{
            sameWithRestricted = false;
            numOfCurTry++;
            result = RandomGenerator.genInt(max);
            for( int i = 0; i < restrictedNumbers.length; i++ )
            {
                if ( result == restrictedNumbers[i] )
                {
                    sameWithRestricted = true;
                    break;
                }
            }
            
            if( numOfCurTry == maxNumOfTry )
                throw new SystemFault( SystemFault.SEVERE_ERROR );
            
        }while( sameWithRestricted );
        
        return result;
    }     
    
    
    public static int genInt(final int max, ArrayList<Integer> restrictedNumbers) throws SystemFault
    {
        int result;
        boolean sameWithRestricted;
        int maxNumOfTry = 1000;
        int numOfCurTry = 0;
        
        do{
            sameWithRestricted = false;
            numOfCurTry++;
            result = RandomGenerator.genInt(max);
            for( int i = 0; i < restrictedNumbers.size(); i++ )
            {
                if ( result == restrictedNumbers.get(i) )
                {
                    sameWithRestricted = true;
                    break;
                }
            }
            
            if( numOfCurTry == maxNumOfTry )
                throw new SystemFault( SystemFault.SEVERE_ERROR );
            
        }while( sameWithRestricted );
        
        return result;
    }        
    
    public static double genDouble()
    {
        double result = getGenerator().nextDouble();
        return result;
    }

    public static Random getGenerator() {
        return generator;
    }

}
