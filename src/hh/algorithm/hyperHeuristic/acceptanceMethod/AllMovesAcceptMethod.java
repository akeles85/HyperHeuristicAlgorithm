/*
* Filename: AllMovesAcceptMethod.java
* Author:   Ali KELES
*
*/


package hh.algorithm.hyperHeuristic.acceptanceMethod;

import hh.algorithm.com.Solution;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class AllMovesAcceptMethod extends MoveAcceptanceMethod
{

    @Override
    public boolean decideToAccept(Solution solution) 
    {
        return true;
    }

}
