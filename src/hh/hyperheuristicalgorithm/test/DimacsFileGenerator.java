
package hh.hyperheuristicalgorithm.test;

import java.util.*;
import java.io.*;

/** A <tt>Generator</tt> that simply reads a file representing a CNF
 *  in the style used in the <a href="ftp://dimacs.rutgers.edu/pub/challenge/satisfiability/benchmarks/cnf/">DIMACS implementation challenge</a>.
 *  (Note that these files must be uncompressed before using here.)
 *  Each time <tt>getNext</tt> is called, this same CNF is returned.
 *  This class also includes a <tt>main</tt> method for testing.
 */
public class DimacsFileGenerator implements Generator {

  Literal[][] cnf;

  /** Constructor for this generator.
   * @param file_name name of file containing DIMACS CNF description
   */
  public DimacsFileGenerator(String file_name)
      throws FileNotFoundException, IOException
  {
    BufferedReader in;
    String line;
    Stack<Literal> s = new Stack<>( );
    Literal literal = new Literal( );

    in = new BufferedReader(new FileReader("./inputs/aim-100-1_6-no-1.cnf"));

    int i = 0;

    while( true )
    {
      try
      {
        line = in.readLine();
      } catch (IOException e)
      {
        System.err.println("Error reading file "+file_name);
        in.close();
        throw e;
      }

      if (line == null)
        break;

      line = line.trim( );

      String[] words = line.split("\\s");
      
      if (words[0].equals("c"))
        continue;

      if (words[0].equals("p"))
      {
        if (!words[1].equals("cnf"))
          System.err.println("expect cnf after p in file " + file_name);
            int num_clauses = Integer.parseInt(words[3]);

        cnf = new Literal[num_clauses][];
      }
      else if (!line.equals(""))
      {
        for (int j = 0; j < words.length; j++)
        {
          int var = Integer.parseInt(words[j]);
          if( var == 0 )
          {
            cnf[i] = new Literal[ s.size( ) ];
            int k = 0;
            while( ! s.empty( ) )
            {
              cnf[i][k] = ( Literal ) s.pop( );
              k++;
            }
            i++;
            s = new Stack<Literal>( );
            break;
          }
          literal = new Literal( );
          literal.sign = (var > 0);
          literal.symbol = (var < 0 ? -var : var) - 1;
          s.push( literal );
        }
      }
    }
    in.close();
  }

  /** Returns the CNF read in from the named DIMACS file. */
  public Literal[][] getNext()
  {
    return cnf;
  }

  /** This is a simple <tt>main</tt> which runs <tt>MySatSolver</tt>
   * on the CNF stored in the file named in the first argument
   * within the time limit specified by the second argument.
   */
  public static void main(String[] argv)
    throws FileNotFoundException, IOException
  {
    String file_name = "E:\\aim-100-1_6-no-1.cnf";
    int time_limit = 0;
    try
    {
        //file_name = argv[0];
        time_limit = -1;
        if( time_limit == -1 )
          time_limit = 30;
    } catch (Exception e)
    {
        System.err.println("Arguments: <file_name> <time_limit>");
        return;
    }

    Generator gen = new DimacsFileGenerator(file_name);
    SatSolver sat = new MySatSolver();
    Literal[][] cnf = gen.getNext();
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

