/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hh.hyperheuristicalgorithm;

import java.io.FileNotFoundException;
import java.io.IOException;

import hh.algorithm.com.SATSolution;
import hh.algorithm.com.Solution;
import hh.algorithm.com.SystemFault;
import hh.algorithm.hyperHeuristic.acceptanceMethod.AllMovesAcceptMethod;
import hh.algorithm.hyperHeuristic.com.HyperHeuristicAlgorithm;
import hh.algorithm.hyperHeuristic.framework.HyperHeuristicAbstractFramework;
import hh.algorithm.hyperHeuristic.framework.HyperHeuristicFrameworkA;
import hh.algorithm.hyperHeuristic.heuristic.RandomWalkHeuristic;
import hh.algorithm.hyperHeuristic.hillClimbers.HillClimber;
import hh.algorithm.hyperHeuristic.selectionMethod.SimpleRandomSelectionMethod;

/**
 *
 * @author lock
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        try {

    
            HyperHeuristicAlgorithm algorithm;
            HyperHeuristicAbstractFramework framework = new HyperHeuristicFrameworkA();

            SATSolution.createCNF();
            
            algorithm = new HyperHeuristicAlgorithm();
            framework.setAlgorithm(algorithm);

            algorithm.setFramework(framework);

            algorithm.setSelectionMethod(new SimpleRandomSelectionMethod());
            
            algorithm.setMoveAcceptanceMethod(new AllMovesAcceptMethod() );
            
            algorithm.addLowLevelHeuristic( new RandomWalkHeuristic() );                       
            algorithm.addLowLevelHeuristic( new HillClimber() );
            
            algorithm.executive();
            
            Solution bestResult = algorithm.getBestSolution();
                        
            System.out.println("Best Result " + bestResult.getFitness() );
            System.out.println("Best Result Iter Num " + bestResult.getIterationNumber() );
            
        } catch (SystemFault ex) {
            ex.printStackTrace();
        }
        
        
    }

}
