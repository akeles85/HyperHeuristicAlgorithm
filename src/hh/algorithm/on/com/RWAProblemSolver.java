/*
* Filename: RWAProblemSolver.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.com.Matrix;
import hh.algorithm.com.ProblemSolver;
import hh.algorithm.com.Representation;
import hh.algorithm.com.Solution;
import hh.algorithm.com.SystemFault;
import hh.algorithm.on.qTool.QTool;
import hh.algorithm.representation.LigthPathArrayRepresentation;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class RWAProblemSolver extends ProblemSolver
{

    private RoutingAssignHeuristic          routingAssignmentHeuristic;
    
    private WavelengthAssignHeuristic       wavelengthAssignmentHeuristic;
    
    private QTool                           qTool;
    
    private double                          BERThreshold = VTDesignParams.BER_THRESHOLD;
    
    private Solution                        finalSolution;
    
    private LightPathRouterHeuristic[]               selectedLigthPathRouters;
    
    
    private RWAProblemSolver() 
    {
        finalSolution = new Solution();
        
        this.routingAssignmentHeuristic = new RoutingAssignHeuristic(VTDesignParams.numOfWavelengths);        
        
        this.wavelengthAssignmentHeuristic = new WavelengthAssignHeuristic(VTDesignParams.numOfWavelengths);
        
        this.wavelengthAssignmentHeuristic.setWavelengthAssigner( new LeastUsedWavelengthAssigner() );
        
        qTool = new QTool();
        this.routingAssignmentHeuristic.setQtool(qTool);
        this.selectedLigthPathRouters = null;
    }
    
    public RWAProblemSolver( Matrix  distanceMatrix ) throws FileNotFoundException, IOException
    {
        this();
        
        this.routingAssignmentHeuristic.setConstantDistanceMatrix((Matrix)distanceMatrix.clone());
        
        this.wavelengthAssignmentHeuristic.setWavelengthTopology((Matrix)distanceMatrix.clone());
    }

    /*Solution.Representation = [ LP1, LP2, LP3, LP4, ... ]*/
    @Override
    public boolean solve(Solution inSolution) throws SystemFault 
    {
        boolean result;
        int blocked = 0;
        int total = 0;
        double currBER;
        double blockedByBer = 0;
        
        /*DIKKAT, bu asama 22.12.2009 tarihinde eklendi.*/
        qTool = new QTool();
        this.routingAssignmentHeuristic.setQtool(qTool);
        
        if( this.selectedLigthPathRouters == null )
            throw new SystemFault(SystemFault.SEVERE_ERROR);
        
        finalSolution = inSolution;
        LigthPathArrayRepresentation  finalSolutionRep = (LigthPathArrayRepresentation)finalSolution.getRepresentation();
        
        this.routingAssignmentHeuristic.prepareToRoute();
        this.wavelengthAssignmentHeuristic.prepareToAssign();            
        this.routingAssignmentHeuristic.setWavelengthAssignHeuristic(wavelengthAssignmentHeuristic);
        
        double numOfUnroutedLPs = 0, numOfUnWavelengthAssignedLPs = 0, numOfUnSatisfiedBerLPs = 0;
        for( int lpIndex = 0; lpIndex < finalSolutionRep.size(); lpIndex++ )
        {            
            LightPath   currLightPath;
            
            currLightPath = finalSolutionRep.get(lpIndex);
            /*Set the next lightpath*/
            this.routingAssignmentHeuristic.setLightPathRouter( selectedLigthPathRouters[ lpIndex ] );
            /*Route the next lightpath*/ 
            /*If the routing policy needs wavelength, it will also do the wavelength assignment in this step*/
            result = this.routingAssignmentHeuristic.route( currLightPath );
            
            /*If the s-d is not routed, set the solution as infeasible and return false*/
            if( result == false )
            {
                currLightPath.setRouted(false);
                numOfUnroutedLPs++;
                continue;
                /*DIKKAT
                finalSolution.setFeasible( false );
                return false;
                */
            }
            
            currLightPath = routingAssignmentHeuristic.getRoutedLigthPaths(0);
            if( currLightPath == null )
            {
                throw new SystemFault(SystemFault.SEVERE_ERROR);
            }
                                               
            currLightPath.setRouted(true); 

            /*Now, set the wavelength of the current lightpath*/                        
            LigthPathArrayRepresentation    repForWA = new LigthPathArrayRepresentation();
            repForWA.setLigthpaths( currLightPath );
            result = this.wavelengthAssignmentHeuristic.assign( repForWA, true );

            if( result == false)
            {
                throw new SystemFault(SystemFault.SEVERE_ERROR);
                /*DIKKAT
                finalSolution.setFeasible( false );            
                return false;                           
                 */ 
            }

            if( wavelengthAssignmentHeuristic.getWavelength(0) == null ) 
            {
                numOfUnWavelengthAssignedLPs++;
                currLightPath.setWavelengthAssigned(false);
                continue;                
                /*DIKKAT*
                 /*   finalSolutionRep.get(lpIndex).setWavelengthAssigned(false);
                    continue;*/
            }
            currLightPath.setWavelengthAssigned(true);            
            currLightPath.setWavelengthId( wavelengthAssignmentHeuristic.getWavelength(0));            
                
            currBER = qTool.calculateBER( currLightPath, this.routingAssignmentHeuristic.getConstantDistanceMatrix().getData(), true );
            
            finalSolutionRep.set(lpIndex,(LightPath) currLightPath.clone());
            
            //System.out.println( currLightPath.getPrinLinks() );
            
           /* System.out.println("i: " + lpIndex);
            System.out.println( finalSolutionRep.get(lpIndex).getPrinLinks() );
            System.out.println("Wavelength " + finalSolutionRep.get(lpIndex).getWavelengthId());*/
        }
        //System.out.println("END");
        /*Delete the lightpath routers, to provide the null check next time*/
        this.selectedLigthPathRouters = null;                            
                                   

        double fitness = 0;
        double totalBER1 = 0, totalBER2 = 0;
        /*
         DEGISTI: 07.02.2010: Eskiden tüm lightpath'ler kurulduktan ve dalga boyları atandıktan sonra
         BER hesaplanıyordu. Artık lightpath'ler tek tek kuruluyor ve her kurulduğunda BER'i hesaplanıyor
         * Bu işlemler LBER, MinHBER heuristic'leri için gerekliydi.
         */
        /*qTool = new QTool();
        this.routingAssignmentHeuristic.setQtool(qTool);        
        for(int i = 0; i < finalSolutionRep.size(); i++)
        {            
            if( !finalSolutionRep.get(i).isFeasible() )
            {         
                continue;           
            }
            
            currBER = qTool.calculateBER( finalSolutionRep.get(i), this.routingAssignmentHeuristic.getConstantDistanceMatrix().getData(), true );
            totalBER1 += currBER;
           // System.out.println( i + "Curr Ber: " + currBER);
         /*   for( int link = 0; link < finalSolutionRep.get(i).getPhysicalLinks().length; link++ )
                System.out.print( finalSolutionRep.get(i).getPhysicalLinks()[link] + " ");            
            System.out.println("Curr Ber: " + currBER);            
            System.out.println( selectedLigthPathRouters[i] );
        }       */
       // System.out.println("END");
        
        for(int i = 0; i < finalSolutionRep.size(); i++)
        {            
            if( ! ( finalSolutionRep.get(i).isRouted() && finalSolutionRep.get(i).isWavelengthAssigned() ) )
            {
                continue;           /*Continue with the next lightpath*/
            }
            
            currBER = qTool.calculateBER( finalSolutionRep.get(i), this.routingAssignmentHeuristic.getConstantDistanceMatrix().getData(), true );
            totalBER2 += currBER;           
            //System.out.println("CurrBer " + currBER );
           if( currBER > this.BERThreshold )
            {                                
                numOfUnSatisfiedBerLPs++;
                finalSolutionRep.get(i).setBerConstraintSatisfied(false);                
                fitness += currBER; 
                continue;                
            }
            finalSolutionRep.get(i).setBerConstraintSatisfied(true);
            //System.out.println( i + "Curr Ber 2: " + currBER);            
            fitness += currBER;            
        }        
        //System.out.println("Total Ber" + fitness );
          double penalty = (numOfUnroutedLPs + numOfUnWavelengthAssignedLPs) * this.BERThreshold + numOfUnSatisfiedBerLPs * this.BERThreshold * 10.0;
          
          fitness += penalty;          
       // System.out.println("1: " + totalBER1+ " 2: " + totalBER2);
       // fitness = (double)blocked/(double)finalSolutionRep.size();
 //       System.out.println("Blocked " + blocked+ " By Ber " + blockedByBer);
        //System.out.println("Fitness: " + fitness);        
                  
         // System.out.println("numOfUnSatisfiedBerLPs: " + numOfUnSatisfiedBerLPs);
         if( penalty > 0)
         {
            finalSolution.setFeasible( false );            
         }
         else
         {
            finalSolution.setFeasible( true );                          
         }
             
        
        
        /*Calculate fitness*/
        /*FITNESS HESABINI DEGISTIRDIM*/
//        for( int i = 0; i < finalSolutionRep.size(); i++)
//        {
//            fitness += finalSolutionRep.get(i).getCost();
//        }
                
        finalSolution.setFitness(fitness);
        return true;
    }

    @Override
    public Solution getSolution() {
        return finalSolution;
    }

    @Override
    public boolean isBetter(Solution firstSolution, Solution secondSolution) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSelectedLigthPathRouters(LightPathRouterHeuristic[] selectedLigthPathRouters) {
        this.selectedLigthPathRouters = selectedLigthPathRouters;
    }
    

}
