package hh.hyperheuristicalgorithm.test;
/** The interface for a CNF <tt>Generator</tt>.  Each time the
 *  <tt>getNext</tt> method is called, a new CNF is generated and
 *  returned.
 */
public interface Generator {

    /** This method returns a new CNF formula each time it is called. */
    public Literal[][] getNext();
}
