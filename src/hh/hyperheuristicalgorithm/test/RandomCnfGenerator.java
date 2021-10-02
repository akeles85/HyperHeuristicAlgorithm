package hh.hyperheuristicalgorithm.test;
import java.util.*;

/** This <tt>Generator</tt> generates random CNF formulas of a fixed
 * size over a fixed number of symbols and with a fixed number of
 * literals per clause.  This class also includes a <tt>main</tt>
 * method for testing.
 */
public class RandomCnfGenerator implements Generator {

    private int num_clauses;  // number of clauses
    private int num_symbols;  // number of symbols
    private int num_literals; // number of literals per clause

    private Random random;  // random number generator

    /** Constructor for this object.
     *  @param num_clauses  number of clauses in constructed CNF
     *  @param num_symbols  number of symbols (variables) in CNF
     *  @param num_literals number of literals per clause
     */
    public RandomCnfGenerator(int num_clauses, int num_symbols, int num_literals) {
	random = new Random();
	this.num_clauses = num_clauses;
	this.num_symbols = num_symbols;
	this.num_literals = num_literals;
    }

    /** Generates another CNF of the specified form. */
    public Literal[][] getNext() {
	Literal[][] cnf = new Literal[num_clauses][num_literals];

	for (int i = 0; i < num_clauses; i++) {
	    for (int j = 0; j < num_literals; j++) {
		cnf[i][j] = new Literal();
		cnf[i][j].sign = random.nextBoolean();
		cnf[i][j].symbol = random.nextInt(num_symbols);
	    }
	}
	return cnf;
    }

    /** This is a simple <tt>main</tt>.  It generates a random CNF
     * formula with number of clauses, number of symbols and number of
     * literals per clause specified by the first three arguments.
     * Once generated, <tt>MySatSolver</tt> is called to try to solve
     * the CNF within the time limit specified by the fourth argument.
     * This process is repeated the number of times specified by the
     * fifth argument.
     */
    public static void main(String[] argv) {
	int num_clauses = 0;
	int num_symbols = 0;
	int num_literals = 0;
	int time_limit = 0;
	int num_reps = 0;
	try {
	    num_clauses = Integer.parseInt(argv[0]);
	    num_symbols = Integer.parseInt(argv[1]);
	    num_literals = Integer.parseInt(argv[2]);
	    time_limit = Integer.parseInt(argv[3]);
	    num_reps = Integer.parseInt(argv[4]);
	}
	catch (Exception e) {
	    System.err.println("Arguments: <num_clauses> <num_symbols> <num_literals_per_clause> <time_limit> <num_reps>");
	    return;
	}

	Generator gen = new RandomCnfGenerator(num_clauses,
					       num_symbols,
					       num_literals);
	SatSolver sat = new MySatSolver();
	for (int r = 0; r < num_reps; r++) {
	    Literal[][] cnf = gen.getNext();
	    System.out.println("--------------------------");
	    System.out.println("cnf:");
	    System.out.println(CnfUtil.cnfToString(cnf));
	    Timer timer = new Timer(time_limit * 1000);
	    boolean[] model = sat.solve(cnf, timer);
	    System.out.println("elapsed time= " + timer.getTimeElapsed());
	    int num_unsat = CnfUtil.numClauseUnsat(cnf, model);
	    System.out.println("num_unsat= " + num_unsat);
	    System.out.println("model: " +
			       CnfUtil.modelToString(model));
	}
    }
}
