/*
* Filename: WavelengthAssigner.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.com;

import hh.algorithm.com.Matrix;
import hh.algorithm.representation.LigthPathArrayRepresentation;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public interface WavelengthAssigner {
    public Integer [] execute(double wavelengthTopology[][][], int numOfWavelength, LigthPathArrayRepresentation routedPaths, int numOfUsedWavelengths[], boolean updateTopology, int []leastUsedWavelength);
}
