/*
* Filename: HyperHeuristicAlgorithm.java
* Author:   Ali KELES
*
*/


package hh.algorithm.hyperHeuristic.com;

import hh.algorithm.com.Solution;
import hh.algorithm.com.SystemFault;
import hh.algorithm.hyperHeuristic.acceptanceMethod.MoveAcceptanceMethod;
import hh.algorithm.hyperHeuristic.framework.HyperHeuristicAbstractFramework;
import hh.algorithm.hyperHeuristic.heuristic.LowLevelHeuristic;
import hh.algorithm.hyperHeuristic.hillClimbers.HillClimber;
import hh.algorithm.hyperHeuristic.selectionMethod.HeuristicSelectionMethod;
import java.util.ArrayList;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class HyperHeuristicAlgorithm 
{
    private HeuristicSelectionMethod            selectionMethod;
    
    private MoveAcceptanceMethod                moveAcceptanceMethod;
    
    private ArrayList<LowLevelHeuristic>        lowLevelHeuristics;
    
    private ArrayList<HillClimber>              hillClimbers;
    
    private HyperHeuristicAbstractFramework     framework;
    
    private Solution                            initialSolution;
    
    private Solution                            bestSolution;    
    
    private double                              fitnessOfLastIteration;
    private boolean                             isBetterSolutionAtThisIteration = false;
    
    public HyperHeuristicAlgorithm() throws SystemFault
    {
        this.lowLevelHeuristics = new ArrayList();
        
        this.hillClimbers = new ArrayList();
        
        initialSolution = new Solution();
        
        initialSolution.initialize();
    }
    
    public LowLevelHeuristic selectHeuristic(Solution currSolution)
    {
        return this.selectionMethod.selectHeuristic( currSolution, this.lowLevelHeuristics, this.isBetterSolutionAtThisIteration );
    }
    
    public Solution applyHeuristic( LowLevelHeuristic inHeuristic, Solution currSolution)
    {
        inHeuristic.apply( currSolution );
        
        return currSolution;
    }
    
    public Solution getInitialSolution()
    {
        return initialSolution;
    }
    
    public void addLowLevelHeuristic(LowLevelHeuristic inHeuristic)
    {
        this.lowLevelHeuristics.add(inHeuristic);
    }
    
    public void addHillClimber(HillClimber inHillClimber)
    {
        this.hillClimbers.add(inHillClimber);
    }

    public void setSelectionMethod(HeuristicSelectionMethod selectionMethod) {
        this.selectionMethod = selectionMethod;
    }

    public void setMoveAcceptanceMethod(MoveAcceptanceMethod moveAcceptanceMethod) {
        this.moveAcceptanceMethod = moveAcceptanceMethod;
    }

    public void setFramework(HyperHeuristicAbstractFramework framework) {
        this.framework = framework;
    }
    
    public boolean isAcceptSolution(Solution currSolution)
    {
        return this.moveAcceptanceMethod.decideToAccept( currSolution );
    }
    
    public void executive()
    {
        this.framework.executive();
    }
    
    public void setNIterResult( Solution solution)
    {               
        if( getBestSolution() == null )
        {
            bestSolution = solution.clone();
        }
        else
        {
            if( isBetter( solution.getFitness(), getBestSolution().getFitness() )   )
            {
                bestSolution = solution.clone();                
            }
        }                
        
        if( isBetter( solution.getFitness(), fitnessOfLastIteration )  )
        {
            this.isBetterSolutionAtThisIteration = true;
        }
        else
        {
            this.isBetterSolutionAtThisIteration = false;
        }
                
        fitnessOfLastIteration = solution.getFitness();        
    }
    
    /**
     * 
     * @return true: if first param is better than second one
     */
    public static boolean isBetter( double fitness1, double fitness2 )
    {
        if( HyperHeuristicAlgorithmParams.isMinimization )
        {
            if( fitness1 < fitness2 )
                return true;
            else
                return false;
        }
        else
        {
            if( fitness1 > fitness2 )
                return true;
            else
                return false;            
        }
    }

    public Solution getBestSolution() {
        return bestSolution;
    }
    
    
}
