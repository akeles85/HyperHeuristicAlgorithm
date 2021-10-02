package hh.hyperheuristicalgorithm.test;
/** This class includes several static methods which may be useful in
 *  processing CNF formulas.  A CNF is represented as an array of
 *  arrays of <tt>Literal</tt>s, where each of the component arrays
 *  represents a disjunction of literals.
 */


public class CnfUtil {

    /** Returns one plus the largest index of any symbol referenced by the
     *  given CNF.
     */
    public static int getNumSymbols(Literal[][] cnf) {
	int max = 0;

	for (int i = 0; i < cnf.length; i++)
	    for (int j = 0; j < cnf[i].length; j++)
		if (cnf[i][j].symbol > max)
		    max = cnf[i][j].symbol;

	return max + 1;
    }

    /** Converts a given CNF to a multi-line string representation
     *  suitable for printing.
     */
    public static String cnfToString(Literal[][] cnf) {
	String s = "";

	for (int i = 0; i < cnf.length; i++) {
	    for (int j = 0; j < cnf[i].length; j++)
		s += " " + (cnf[i][j].sign ? "+" : "-") + cnf[i][j].symbol;
	    s += "\n";
	}

	return s;
    }

    /** Converts a given model (represented by a boolean array) to a
     *  string representation suitable for printing.
     */
    public static String modelToString(boolean[] model) {
	String s = "";

	for (int i = 0; i < model.length; i++)
	    s += " " + (model[i] ? "+" : "-");

	return s;
    }

    /** Returns <tt>true</tt> if the given model satisfies the given
     *  clause.
     */
    public static boolean isClauseSat(Literal[] clause, boolean[] model) {
	for (int j = 0; j < clause.length; j++)
	    if (model[clause[j].symbol] == clause[j].sign)
		return true;
	return false;
    }

    /** Returns the number of clauses of the given CNF not satisfied
     *  by the given model.
     */
    public static int numClauseUnsat(Literal[][] cnf, boolean[] model) {
	int num_not_satd = 0;

	for (int i = 0; i < cnf.length; i++)
	    if (!isClauseSat(cnf[i], model))
		num_not_satd++;

	return num_not_satd;
    }
}
