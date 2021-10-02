/*
* Filename: AlgorithmResultSaver.java
* Author:   Ali KELES
*
*/


package hh.algorithm.GRASP;

import hh.algorithm.com.Solution;
import hh.algorithm.com.StatisticCalculator;
import hh.algorithm.on.com.VTDesignParams;
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
import hh.algorithm.representation.ObjectArrayRepresentation;



/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class AICSResultSaver 
{
    private LinkedList<Solution>            iterSolutions;
    private Solution                        bestSolution;
    private LinkedList<Solution>            bestSolutionUpToNow;
    private double                          runningTime;
    private static boolean                  resultFileOpenedFirstTime = true;

    public static int                       numOfWriteRun = 0;
    
    public AICSResultSaver()
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
        File theDir = new File( "d:\\"+ VTDesignParams.topologyName );
        if( !theDir.exists() )
            theDir.mkdir();
        
        problemName += "_" + VTDesignParams.currentInspectedParamName ;
        
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
            ObjectArrayRepresentation arrayRep = (ObjectArrayRepresentation)solution.getSubSolution().getRepresentation();                                                        
            writer.write(solution.getSubSolution().getFitness() + ":"+ solution.getSubSolution().isFeasible()+":");                        
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

        double bestFitness = Double.MIN_VALUE;
        double averageFitness = 0;
        double worstFitness = Double.MAX_VALUE;
        double totalBestResultsUpToNow[] = null;
        int numOfEntryIntotalBestResultsUpToNow[] = null;
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

            if( bestFitness < currBestFitness )
                bestFitness = currBestFitness;

            if( worstFitness > currBestFitness )
                worstFitness = currBestFitness;

            averageFitness += currBestFitness;

            if( totalBestResultsUpToNow == null )
            {
                totalBestResultsUpToNow = new double[ totalNumOfIter ];
                Arrays.fill( totalBestResultsUpToNow, 0 );
                numOfEntryIntotalBestResultsUpToNow = new int[ totalNumOfIter ];
                Arrays.fill( numOfEntryIntotalBestResultsUpToNow, 0 );
            }

            if( totalBestResultsUpToNow.length < totalNumOfIter )
            {
                double temp[] = new double[totalNumOfIter];
                int tempInt[] = new int[totalNumOfIter];
                Arrays.fill( temp, 0 );
                for( int k = 0; k < totalBestResultsUpToNow.length; k++ )
                {
                    temp[k] = totalBestResultsUpToNow[k];
                    tempInt[k] = numOfEntryIntotalBestResultsUpToNow[k];
                }

                for(int k = totalBestResultsUpToNow.length; k < temp.length; k++ )
                {
                    temp[k] = totalBestResultsUpToNow[totalBestResultsUpToNow.length-1];
                }
                totalBestResultsUpToNow = temp;
                numOfEntryIntotalBestResultsUpToNow = tempInt;

            }

            for(int j = 0; j < bestResultsUpToNow.length; j++ )
            {

                totalBestResultsUpToNow[j] += bestResultsUpToNow[j];
                numOfEntryIntotalBestResultsUpToNow[j]++;
            }
            if( bestResultsUpToNow.length < totalBestResultsUpToNow.length )
            {
                for( int k = bestResultsUpToNow.length; k < totalBestResultsUpToNow.length; k++ )
                {
                    totalBestResultsUpToNow[k] += bestResultsUpToNow[bestResultsUpToNow.length-1];
                }
            }

            runResults[i] = currBestFitness;
        }

        averageFitness = averageFitness / validFiles.size();
        for(int j = 0; j < totalBestResultsUpToNow.length; j++ )
        {
            totalBestResultsUpToNow[j] = totalBestResultsUpToNow[j] / validFiles.size();
            System.out.print( totalBestResultsUpToNow[j] + ",");
        }
        System.out.println("");
        for(int j = 0; j < totalBestResultsUpToNow.length; j++ )
        {
            System.out.print( j + ",");
        }
        System.out.println();
        System.out.println("Best Fitness: " + bestFitness );
        System.out.println("Average Fitness: " + averageFitness );
        System.out.println("Worst Fitness: " + worstFitness );
        double standardError = StatisticCalculator.standardError(runResults);
        System.out.println("SE: " + standardError);

        String resultString = convertDoubleToTablePrint(bestFitness) + "\t"
                + convertDoubleToTablePrint(averageFitness) +  "\t"
                + convertDoubleToTablePrint(worstFitness) + "\t"
                + convertDoubleToTablePrint(standardError) + "\n"
                ;
        FileWriter writer = new FileWriter( inDir + "\\" + "final_results" + pureParam + ".txt",!resultFileOpenedFirstTime );
        resultFileOpenedFirstTime = false;
        writer.write( resultString );

        writer.close();

    }


public static void readFromDirectory(String inDir, String pureParam, String paramName, int numOfPerm) throws FileNotFoundException, IOException
    {
        File dir = new File( inDir );

        numOfPerm = 6;
        System.out.println("");
        System.out.println("ParamName: " + paramName );
        File[] files = dir.listFiles();

        ArrayList<File> validFiles = new ArrayList();

        for(int i = 0; i < files.length; i++ )
        {
            if( files[i].getName().contains(paramName) )
                validFiles.add(files[i]);
        }

        double bestFitness = Double.MIN_VALUE;
        double averageFitness = 0;
        double worstFitness = Double.MAX_VALUE;
        double totalBestResultsUpToNow[] = null;
        int numOfEntryIntotalBestResultsUpToNow[] = null;
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

            if( bestFitness < currBestFitness )
                bestFitness = currBestFitness;

            if( worstFitness > currBestFitness )
                worstFitness = currBestFitness;

            averageFitness += currBestFitness;

            if( totalBestResultsUpToNow == null )
            {
                totalBestResultsUpToNow = new double[ totalNumOfIter ];
                Arrays.fill( totalBestResultsUpToNow, 0 );
                numOfEntryIntotalBestResultsUpToNow = new int[ totalNumOfIter ];
                Arrays.fill( numOfEntryIntotalBestResultsUpToNow, 0 );
            }

            if( totalBestResultsUpToNow.length < totalNumOfIter )
            {
                double temp[] = new double[totalNumOfIter];
                int tempInt[] = new int[totalNumOfIter];
                Arrays.fill( temp, 0 );
                for( int k = 0; k < totalBestResultsUpToNow.length; k++ )
                {
                    temp[k] = totalBestResultsUpToNow[k];
                    tempInt[k] = numOfEntryIntotalBestResultsUpToNow[k];
                }

                for(int k = totalBestResultsUpToNow.length; k < temp.length; k++ )
                {
                    temp[k] = totalBestResultsUpToNow[totalBestResultsUpToNow.length-1];
                }
                totalBestResultsUpToNow = temp;
                numOfEntryIntotalBestResultsUpToNow = tempInt;

            }

            for(int j = 0; j < bestResultsUpToNow.length; j++ )
            {

                totalBestResultsUpToNow[j] += bestResultsUpToNow[j];
                numOfEntryIntotalBestResultsUpToNow[j]++;
            }
            if( bestResultsUpToNow.length < totalBestResultsUpToNow.length )
            {
                for( int k = bestResultsUpToNow.length; k < totalBestResultsUpToNow.length; k++ )
                {
                    totalBestResultsUpToNow[k] += bestResultsUpToNow[bestResultsUpToNow.length-1];
                }
            }

            runResults[i] = currBestFitness;
        }

        averageFitness = averageFitness / validFiles.size();
        for(int j = 0; j < totalBestResultsUpToNow.length; j++ )
        {
            totalBestResultsUpToNow[j] = totalBestResultsUpToNow[j] / validFiles.size();
            System.out.print( totalBestResultsUpToNow[j] + ",");
        }
        System.out.println("");
        for(int j = 0; j < totalBestResultsUpToNow.length; j++ )
        {
            System.out.print( j + ",");
        }
        System.out.println();
        System.out.println("Best Fitness: " + bestFitness );
        System.out.println("Average Fitness: " + averageFitness );
        System.out.println("Worst Fitness: " + worstFitness );
        double standardError = StatisticCalculator.standardError(runResults);
        System.out.println("SE: " + standardError);

        numOfWriteRun++;
        FileWriter writer = new FileWriter( inDir + "\\" + "final_results_best" + pureParam + ".txt",!resultFileOpenedFirstTime );
        writer.write( convertDoubleToTablePrint(bestFitness) + "\t" );
        if( numOfWriteRun == numOfPerm )
        {
            writer.write("\n");
        }
        writer.close();

        writer = new FileWriter( inDir + "\\" + "final_results_worst" + pureParam + ".txt",!resultFileOpenedFirstTime );
        writer.write( convertDoubleToTablePrint(worstFitness) + "\t" );
        if( numOfWriteRun == numOfPerm )
        {
            writer.write("\n");
        }
        writer.close();

        writer = new FileWriter( inDir + "\\" + "final_results_average" + pureParam + ".txt",!resultFileOpenedFirstTime );
        writer.write( convertDoubleToTablePrint(averageFitness) +  "\t" );
        if( numOfWriteRun == numOfPerm )
        {
            writer.write("\n");
        }
        writer.close();

        writer = new FileWriter( inDir + "\\" + "final_results_se" + pureParam + ".txt",!resultFileOpenedFirstTime );
        writer.write( convertDoubleToTablePrint(standardError) + "\t" );
        if( numOfWriteRun == numOfPerm )
        {
            writer.write("\n");
        }
        writer.close();

        resultFileOpenedFirstTime = false;

        if( numOfWriteRun == numOfPerm )
        {
            numOfWriteRun = 0;
        }
    }

    public static String convertDoubleToTablePrint(double inDouble)
    {
        String doubleString = String.valueOf(inDouble);
        int pointIndex = doubleString.indexOf(".");

        return doubleString.substring(0,pointIndex+3);
    }
    
    public static void main(String args[]) throws FileNotFoundException, IOException
    {
          String directory = "D:\\VTD_NSFNET_4";
//        AICSResultSaver.readFromDirectory( directory,"q0", "q0_0.0");
//        AICSResultSaver.readFromDirectory( directory,"q0", "q0_0.1");
//        AICSResultSaver.readFromDirectory( directory,"q0", "q0_0.2");
//        AICSResultSaver.readFromDirectory( directory,"q0", "q0_0.3");
//        AICSResultSaver.readFromDirectory( directory,"q0", "q0_0.4");
//        AICSResultSaver.readFromDirectory( directory,"q0", "q0_0.5");
//        AICSResultSaver.readFromDirectory( directory,"q0", "q0_0.6");
//        AICSResultSaver.readFromDirectory( directory,"q0", "q0_0.7");
//        AICSResultSaver.readFromDirectory( directory,"q0", "q0_0.8");
//        AICSResultSaver.readFromDirectory( directory,"q0", "q0_0.9");
//        AICSResultSaver.readFromDirectory( directory,"q0", "q0_1.0");

          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.1_0.1", 7);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.2_0.1", 7);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.5_0.1", 7);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_1.0_0.1", 7);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_2.0_0.1", 7);          
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_16.0_0.1", 6);

          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.1_0.2", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.2_0.2", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.5_0.2", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_1.0_0.2", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_2.0_0.2", 6);          
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_16.0_0.2", 6);

          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.1_0.5", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.2_0.5", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.5_0.5", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_1.0_0.5", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_2.0_0.5", 6);          
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_16.0_0.5", 6);


          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.1_1.0", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.2_1.0", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.5_1.0", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_1.0_1.0", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_2.0_1.0", 6);          
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_16.0_1.0", 6);

          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.1_2.0", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.2_2.0", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.5_2.0", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_1.0_2.0", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_2.0_2.0", 6);          
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_16.0_2.0", 6);         

          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.1_16.0", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.2_16.0", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_0.5_16.0", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_1.0_16.0", 6);
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_2.0_16.0", 6);          
          AICSResultSaver.readFromDirectory( directory,"alfa_beta", "alfa_beta_16.0_16.0", 6);
                                
    }    
}
