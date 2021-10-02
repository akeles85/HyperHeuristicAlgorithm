/*
* Filename: AlgorithmResultSaver.java
* Author:   Ali KELES
*
*/


package hh.algorithm.AntColony.com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import hh.algorithm.com.Solution;
import hh.algorithm.com.StatisticCalculator;
import hh.algorithm.on.iterativeHH.AntColonyBasedHHRWAParams;
import hh.algorithm.representation.ObjectArrayRepresentation;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class ACOResultSaver 
{
    private LinkedList<Solution>            iterSolutions;
    private Solution                        bestSolution;
    private LinkedList<Solution>            bestSolutionUpToNow;
    private double                          runningTime;
    public static String                   resultString;
    public static boolean                   resultFileOpenedFirstTime = true;
    
    public ACOResultSaver()
    {
        this.iterSolutions = new LinkedList<Solution>();
        bestSolutionUpToNow = new LinkedList<Solution>();
    }
    
    public void addIterSolution( Solution inSolution)
    {
        this.iterSolutions.addLast( inSolution.clone() );
    }
    
    public void setBestSolution( Solution inSolution )
    {
        this.bestSolution = inSolution.clone();
        bestSolutionUpToNow.add(inSolution.clone());
    }
    
    
    public void saveToFile(String problemName) throws IOException
    {        
        if( true )
            return;
        File theDir = new File( "c:\\"+ AntColonyBasedHHRWAParams.topologyName );
        if( !theDir.exists() )
            theDir.mkdir();
        
        problemName += "_" + AntColonyBasedHHRWAParams.currentInspectedParamName ;
        
        FileWriter writer = new FileWriter(theDir.getAbsolutePath() + "\\" + problemName + ".txt");
    
        writer.write("Total number of Iter: " + iterSolutions.size()+ "\n");
        
        writer.write("Best Iter Fitness: " + this.bestSolution.getFitness() + "\n");
        
        writer.write("Best Iter Feasible: " + this.bestSolution.isFeasible() + "\n");
        
        writer.write("Best Iteration Found at: " + this.bestSolution.getIterationNumber()+ "\n");
        
        writer.write("Running Time (milisecond): " + this.runningTime + "\n");
        
        Iterator<Solution> iter = this.iterSolutions.iterator();
                
        writer.write("\n");
        int i = 0;
        while( iter.hasNext() )
        {
            Solution solution = iter.next();      
            ObjectArrayRepresentation arrayRep = (ObjectArrayRepresentation)solution.getRepresentation();                                                        
            writer.write(solution.getFitness() + ":"+ solution.isFeasible()+":");                        
            for(int j = 0; j < arrayRep.size(); j++ )
                writer.write( arrayRep.get(j) + " "); 
            writer.write("\n");
        }
        
        writer.write("\n");        
               
        
        /*For Matlab Plot*/
        
        iter = this.iterSolutions.iterator();                
        i = 0;
        while( iter.hasNext() )
        {
            Solution solution = iter.next();                        
            writer.write( i + "," ); 
            i++;
        } 
         writer.write("\n");
        iter = this.iterSolutions.iterator();                        
        while( iter.hasNext() )
        {
            Solution solution = iter.next();                        
            writer.write(solution.getFitness() + "," );                        
        } 
        
        writer.write("\n");
        iter = this.bestSolutionUpToNow.iterator();                        
        while( iter.hasNext() )
        {
            Solution solution = iter.next();                        
            writer.write(solution.getFitness() + "," );                        
        }    
        
        writer.close();
    }
    
    public void setRunningTime( long time )
    {
        runningTime = time;        
    }
    
    
public static void readFromDirectory(String inDir, String pureParam, String paramName) throws FileNotFoundException, IOException
    {
        File dir = new File( inDir );
     
        System.out.println("");
        System.out.println("ParamName: " + paramName );
        File[] files = dir.listFiles();
        
        ArrayList<File> validFiles = new ArrayList();
        
        for(int i = 0; i < files.length; i++ )
        {
            if( files[i].getName().contains(paramName) )
                validFiles.add(files[i]);
        }
        
        double bestFitness = Double.MAX_VALUE;
        double averageFitness = 0;
        double worstFitness = Double.MIN_VALUE;
        double totalBestResultsUpToNow[] = null;
        double runResults[] = new double[files.length];

        if( validFiles.size() == 0)
            return;
        /*For each run*/
        for(int i = 0; i < validFiles.size(); i++ )
        {
            FileReader f = new FileReader( validFiles.get(i) );            
        
            BufferedReader reader = new BufferedReader(f);             
            
            String line;
            line = reader.readLine();
            int totalNumOfIter = Integer.valueOf( line.split(":")[1].trim() );

            line = reader.readLine();
            double currBestFitness = Double.valueOf( line.split(":")[1].trim() );
            
            line = reader.readLine();
            boolean isFeasible = Boolean.valueOf( line.split(":")[1].trim()  );
            
            line = reader.readLine();
            int bestIterFoundAt = Integer.valueOf( line.split(":")[1].trim() );
            
            line = reader.readLine();
            double runningTime = Double.valueOf( line.split(":")[1].trim() );            
            
            reader.readLine();
            
            for(int resultIndex = 0; resultIndex < totalNumOfIter; resultIndex++ )
                reader.readLine();
            
            reader.readLine();
            reader.readLine();            
            reader.readLine();
            
            line = reader.readLine();
            String[] bestResultsUpToNowString = line.split(",");
            double bestResultsUpToNow[] = new double[bestResultsUpToNowString.length];
            for(int j = 0; j < bestResultsUpToNowString.length; j++ )
                bestResultsUpToNow[j] = Double.valueOf(bestResultsUpToNowString[j]);
            
            if( bestFitness > currBestFitness )
                bestFitness = currBestFitness;
                                   
            if( worstFitness < currBestFitness )           
                worstFitness = currBestFitness;
            
            averageFitness += currBestFitness;
            
            if( totalBestResultsUpToNow == null )
            {
                totalBestResultsUpToNow = new double[ totalNumOfIter ];
                Arrays.fill( totalBestResultsUpToNow, 0 );
            }
            
            for(int j = 0; j < totalBestResultsUpToNow.length; j++ )
            {
                totalBestResultsUpToNow[j] += bestResultsUpToNow[j];
            }

            runResults[i] = currBestFitness;
        }
        
        averageFitness = averageFitness / validFiles.size();
        for(int j = 0; j < totalBestResultsUpToNow.length; j++ )
        {
            totalBestResultsUpToNow[j] = totalBestResultsUpToNow[j] / validFiles.size();
            System.out.print( totalBestResultsUpToNow[j] + ",");
        }                
        System.out.println();               
        System.out.println("Best Fitness: " + bestFitness );
        System.out.println("Average Fitness: " + averageFitness );
        System.out.println("Worst Fitness: " + worstFitness );
        double standardError = StatisticCalculator.standardError(runResults);
        System.out.println("SE: " + standardError);

        resultString = convertDoubleToTablePrint(bestFitness) + "\t"
                + convertDoubleToTablePrint(averageFitness) +  "\t"
                + convertDoubleToTablePrint(worstFitness) + "\t"
                + convertDoubleToTablePrint(standardError) + "\n"
                ;
        FileWriter writer = new FileWriter( inDir + "\\" + "final_results" + pureParam + ".txt",!resultFileOpenedFirstTime );
        resultFileOpenedFirstTime = false;
        writer.write( resultString );

        writer.close();
        
    }

    public static String convertDoubleToTablePrint(double inDouble)
    {
        String doubleString = String.valueOf(inDouble);

        String start = doubleString.substring(0, 4);
        int startOfPower = doubleString.lastIndexOf("E") + 1;
        String end = doubleString.substring( startOfPower + 1, doubleString.length());

        return (start + "x10 "+ "\t" + end);
    }
    
    public static void main(String args[]) throws FileNotFoundException, IOException
    {
        String directory = "d:\\rwa_1000\\random\\result_nsfnet3";

//        ACOResultSaver.readFromDirectory( directory,"q0", "q0_0.0");
//        ACOResultSaver.readFromDirectory( directory,"q0", "q0_0.1");
//        ACOResultSaver.readFromDirectory( directory,"q0", "q0_0.2");
//        ACOResultSaver.readFromDirectory( directory,"q0", "q0_0.3");
//        ACOResultSaver.readFromDirectory( directory,"q0", "q0_0.4");
//        ACOResultSaver.readFromDirectory( directory,"q0", "q0_0.5");
//        ACOResultSaver.readFromDirectory( directory,"q0", "q0_0.6");
//        ACOResultSaver.readFromDirectory( directory,"q0", "q0_0.7");
//        ACOResultSaver.readFromDirectory( directory,"q0", "q0_0.8");
//        ACOResultSaver.readFromDirectory( directory,"q0", "q0_0.9");
//        ACOResultSaver.readFromDirectory( directory,"q0", "q0_1.0");

        ACOResultSaver.readFromDirectory( directory,"alfa", "alfa_0.1");
        ACOResultSaver.readFromDirectory( directory,"alfa", "alfa_0.2");
        ACOResultSaver.readFromDirectory( directory,"alfa", "alfa_0.5");
        ACOResultSaver.readFromDirectory( directory,"alfa", "alfa_1.0");
        ACOResultSaver.readFromDirectory( directory,"alfa", "alfa_2.0");
        ACOResultSaver.readFromDirectory( directory,"alfa", "alfa_4.0");
        ACOResultSaver.readFromDirectory( directory,"alfa", "alfa_8.0");
        ACOResultSaver.readFromDirectory( directory,"alfa", "alfa_16.0");
        ACOResultSaver.readFromDirectory( directory,"alfa", "alfa_32.0");
        ACOResultSaver.readFromDirectory( directory,"alfa", "alfa_64.0");
        
        
        
    }    
}
