/*
* Filename: SATSolution.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

import hh.algorithm.representation.ArrayRepresentation;
import hh.algorithm.representation.RepresentationElem;
import hh.hyperheuristicalgorithm.test.CnfUtil;
import hh.hyperheuristicalgorithm.test.DimacsFileGenerator;
import hh.hyperheuristicalgorithm.test.Generator;
import hh.hyperheuristicalgorithm.test.Literal;
import java.io.IOException;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class SATSolution extends Solution implements Cloneable
{
    private static Literal[][]      cnf;

    public SATSolution() throws SystemFault 
    {
        this.setRepresentation( new ArrayRepresentation( CnfUtil.getNumSymbols(cnf) , RepresentationElem.BINARY ) );
    }
        
    
    public static void createCNF() throws IOException
    {
        Generator gen = new DimacsFileGenerator("E:\\aim-100-1_6-no-1.cnf");            
        cnf = gen.getNext();        
    }
    
    public double calculateFitness()
    {
        double fitness = 0;
        
        /* (a V b V c) and ( a V b V c') and (a' V b' V c') */
        /*Calculate number of true clauses*/

        fitness = this.cnf.length - CnfUtil.numClauseUnsat( this.cnf, ((ArrayRepresentation)this.getRepresentation()).toBooleanArray() );
        
        return fitness;
    }    
    
    @Override
    public SATSolution clone() 
    {
        SATSolution newSolution = null;

        newSolution = (SATSolution)super.clone();
            
        return newSolution;
        
    }    
    
}
