package hh.hyperheuristicalgorithm.test;
import java.util.*;

/** This is a dummy version of <tt>MySatSolver</tt> that simply
 * generates random models and returns the best one when time runs
 * out.  You can use this class as a template for your own.
 */
public class MySatSolver implements SatSolver {

    private Random random;   // a random number generator

    /** A constructor for this class.  You must include a constructor
     * such as this one taking no arguments.  (You may have other
     * constructors that you use for your experiments, but this is the
     * constructor that will be used as part of the class
     * implementation challenge.)
     */
    public MySatSolver() {
	random = new Random();
    }

    /** This dummy routine attempts to solve the satisfaction problem
     * by trying random models until time runs out and returning the
     * best one.
     */
    public boolean[] solve(Literal[][] cnf, Timer timer) {

	boolean[] model = new boolean[CnfUtil.getNumSymbols(cnf)];
	boolean[] best_model = new boolean[CnfUtil.getNumSymbols(cnf)];
	int best_num_not_satd = cnf.length + 1;
	int num_not_satd;

	while(timer.getTimeRemaining() >= 0) {

	    for (int i = 0; i < model.length; i++)
		model[i] = random.nextBoolean();

	    num_not_satd = CnfUtil.numClauseUnsat(cnf, model);

	    if (num_not_satd < best_num_not_satd) {
		if (num_not_satd == 0)
		    return model;
		best_num_not_satd = num_not_satd;
		for (int i = 0; i < model.length; i++)
		    best_model[i] = model[i];
	    }
	}

	return best_model;
    }
}
		

