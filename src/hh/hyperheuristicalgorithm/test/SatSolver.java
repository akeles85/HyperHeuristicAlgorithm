package hh.hyperheuristicalgorithm.test;

/** This is the interface defining a <tt>SatSolver</tt> object.  Every
 * <tt>SatSolver</tt> must include a method <tt>solve</tt> for solving CNF
 * satisfiability problems.
 */
public interface SatSolver {
    /** This is the method for solving satifiability problems.  This
     * method should return something by when time runs out on the
     * given <tt>timer</tt> object (or very soon thereafter).
     */
    public boolean[] solve(Literal[][] cnf, Timer timer);
}
