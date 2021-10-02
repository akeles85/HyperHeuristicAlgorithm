/*
* Filename: MoveAcceptanceMethod.java
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
public abstract class MoveAcceptanceMethod {

    public abstract boolean decideToAccept(Solution solution);
}
