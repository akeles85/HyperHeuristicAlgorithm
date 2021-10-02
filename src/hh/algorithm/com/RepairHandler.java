/*
* Filename: RepairHandler.java
* Author:   Ali KELES
*
*/


package hh.algorithm.com;

import hh.algorithm.com.Solution;
import hh.algorithm.com.SystemFault;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public interface RepairHandler {
    public boolean repair(Solution solution)throws SystemFault;
}
