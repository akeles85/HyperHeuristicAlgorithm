/*
* Filename: QTool.java
* Author:   Ali KELES
*
*/


package hh.algorithm.on.qTool;

import hh.algorithm.com.Tupple;
import hh.algorithm.on.com.LightPath;
import hh.algorithm.on.com.VTDesignParams;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Ali KELES
 * @version 1.0
 */
public class QTool 
{
    private int         numOfWavelength = VTDesignParams.numOfWavelengths;
    
    private double      wavelengthsInNM[] = {1546.99, 1547.80, 1548.60, 1549.40, 1550.20, 1551.00, 1551.80, 1552.60,1546.99, 1547.80, 1548.60, 1549.40, 1550.20, 1551.00, 1551.80, 1552.60,1546.99, 1547.80, 1548.60, 1549.40, 1550.20, 1551.00, 1551.80, 1552.60,1546.99, 1547.80, 1548.60, 1549.40, 1550.20, 1551.00, 1551.80, 1552.60};
    
    private double      lightSpeed = 300000000;
    
    private double     bitRatePerChannelAsGBPS = 1;         
    
    private double     electronicBandwidthASGBPS = 0.7;
    
    private double      LmxInDb = 4;
    
    private double      LdmInDb = 4;
    
    private double      LsInDb = 1;
    
    private double      LwInDb = 1;    
    
    private double[]      LswInDb;
    


    private double[]      switchInputNumber = {8,4,4,4,4,8,8,4,4,4,4,4,8,4,4};          /*16 for node number 14, Must be order of 2*/
    
//    private double[]      switchInputNumber = {4,4,4,4,4,8,8,4,8,4,8,4,4,4,4,8,8,4,4,4,4,4,4,4,4};          /*16 for node number 14, Must be order of 2*/    
    
    private double      LtapInDb = 1;
    
    private double      LfInDbKm = 0.2;
    
    private double      GinInDb = 22;
    
    /*Different for each node according its connection order*/
    private double[]      GoutInDb =  {18, 16, 16, 16, 16, 18, 18, 16, 16, 16, 16, 16, 18, 16, 16};
    
//    private double[]      GoutInDb =  {16, 16, 16, 16, 16, 18, 18, 16, 18, 16, 18, 16, 16, 16, 16, 18, 18, 16, 16, 16, 16, 16, 16, 16};    
    
    private double      AseFactor = 1.5;        
    
    private double      PlaserInMiliWatt = 0.1;
    
    private double      PsatInDbm = 15;
    
    private double      XswInDb = 30;
    
    private double      dGInDb = 0;
    
    private double      h;       
    
    private double      Nth;     
    
    private double      [][][]PxtMatrix;        /*from tranmitter node to receiver node at wavelegth k*/
    private double      [][][]PaseMatrix;    
    private double      [][][]PsigMatrix;    
    private boolean     [][][]PxtMatrixPathExist; /*Is there a path from node i to node j at wavelength k*/
    
    private double      [][][]oldPxtMatrix;        /*from tranmitter node to receiver node at wavelegth k*/
    private double      [][][]oldPaseMatrix;    
    private double      [][][]oldPsigMatrix;    
    private boolean     [][][]oldPxtMatrixPathExist; /*Is there a path from node i to node j at wavelength k*/    
    
    private ArrayList<Tupple>   oldPxtMatrixList;        /*from tranmitter node to receiver node at wavelegth k*/
    private ArrayList<Tupple>      oldPaseMatrixList;    
    private ArrayList<Tupple>      oldPsigMatrixList;    
    private ArrayList<Tupple>     oldPxtMatrixPathExistList; /*Is there a path from node i to node j at wavelength k*/       
    private boolean [][][]oldPxtMatrixListInList;
    private boolean [][][]oldPaseMatrixListInList;
    private boolean [][][]oldPsigMatrixListInList;
    
    private boolean     snapshotActive = false;
    
    private double      Rlambda;    /*http://www.coseti.org/9008-039.htm*/
    
    public static final int MAX_LINK_DISTANCE_IN_KM = 100;
        
    
    public QTool() 
    {               
        LswInDb = new double[ this.switchInputNumber.length ];
        
        for(int i = 0; i < LswInDb.length; i++ )
        {                    
            LswInDb[i] = 2 * ( Math.log(this.switchInputNumber[i]) / Math.log(2) ) * this.LsInDb;
        
            LswInDb[i] += (4 * this.LwInDb ); 
        }
        
        PxtMatrixPathExist = new boolean[VTDesignParams.numOfNode][ VTDesignParams.numOfNode ][VTDesignParams.numOfWavelengths];                
        
        for( int i = 0; i < VTDesignParams.numOfNode; i++)
            for( int j = 0; j < VTDesignParams.numOfNode; j++ )
            Arrays.fill(PxtMatrixPathExist[i][j], false);
        
        PxtMatrix = new double[VTDesignParams.numOfNode][VTDesignParams.numOfNode][VTDesignParams.numOfWavelengths];              
        PaseMatrix = new double[VTDesignParams.numOfNode][VTDesignParams.numOfNode][VTDesignParams.numOfWavelengths];              
        PsigMatrix = new double[VTDesignParams.numOfNode][VTDesignParams.numOfNode][VTDesignParams.numOfWavelengths];                              
        
        for( int i = 0; i < VTDesignParams.numOfNode; i++ )
        {             
            for(int j = 0; j < VTDesignParams.numOfNode; j++ )                
            {
                Arrays.fill(PxtMatrix[i][j], 0);
                Arrays.fill(PaseMatrix[i][j], 0);
                Arrays.fill(PsigMatrix[i][j], 0);                
            }
        }
        
        h = 6.62606896 * Math.pow(10, -34); /*  J*s  = W*s*s */
        
        Rlambda = (0.5 * 1.6 * Math.pow(10, -19)) / (h*getVi(0));
        
        Nth = Math.pow( 5.3 * Math.pow(10, -12), 2);
        
        oldPxtMatrixListInList = new boolean[VTDesignParams.numOfNode][VTDesignParams.numOfNode][VTDesignParams.numOfWavelengths];              
        oldPsigMatrixListInList = new boolean[VTDesignParams.numOfNode][VTDesignParams.numOfNode][VTDesignParams.numOfWavelengths];             
        oldPaseMatrixListInList = new boolean[VTDesignParams.numOfNode][VTDesignParams.numOfNode][VTDesignParams.numOfWavelengths];                      
               
        for(int i = 0; i < VTDesignParams.numOfNode; i++)
        {
            for(int j = 0; j < VTDesignParams.numOfNode; j++)
            {
                for( int k = 0; k < VTDesignParams.numOfWavelengths; k++)
                {
                    oldPxtMatrixListInList[i][j][k] = false;
                    oldPsigMatrixListInList[i][j][k] = false;
                    oldPaseMatrixListInList[i][j][k] = false;
                }
            }
        }
    }
        
    
    public double dbToConst( double dbValue )
    {
        double returnValue;
        
        returnValue = Math.pow(10, (dbValue / 10) );
        
        return returnValue;
    }
    
    public double[] calcPsig(LightPath lightPath, double [][]physicalMatrixInKm )
    {
        int nodes[] = lightPath.getPhysicalLinks();
        double []powerValues = new double[nodes.length];
        int wavelengthId = lightPath.getWavelengthId();
        
        powerValues[0] = this.PlaserInMiliWatt;
        
        for( int i = 1; i < nodes.length; i++ )
        {
            double distance = physicalMatrixInKm[ nodes[i-1] ][ nodes[i] ];
            powerValues[i] = powerValues[i-1] * dbToConst( gainsAlongPathInDb(nodes[i-1], nodes[i], distance) );
            
            double oldValue = PsigMatrix[ nodes[i-1] ][ nodes[i] ][ wavelengthId ];
            PsigMatrix[ nodes[i-1] ][ nodes[i] ][ wavelengthId ] = powerValues[i];
            if( snapshotActive && oldPsigMatrixListInList[ nodes[i-1] ][ nodes[i] ][wavelengthId] == false )
            {
                this.oldPsigMatrixList.add( new Tupple(nodes[i-1], nodes[i], wavelengthId, oldValue) );
                oldPsigMatrixListInList[ nodes[i-1] ][ nodes[i] ][wavelengthId] = true;
            }            
            
        }
        
        return powerValues;
    }
    
    public double[] calcPase( LightPath lightPath, double [][]physicalMatrixInKm  )
    {
        int nodes[] = lightPath.getPhysicalLinks();
        double []powerValues = new double[nodes.length];
        
        int wavelengthId = lightPath.getWavelengthId();
        
        powerValues[0] = 0;
        
        for( int i = 1; i < nodes.length; i++ )
        {
            double distance = physicalMatrixInKm[ nodes[i-1] ][ nodes[i] ];
            powerValues[i] = powerValues[i-1] * dbToConst( gainsAlongPathInDb(nodes[i-1], nodes[i],distance) );
            
            double term1 =  2 * this.AseFactor * ( dbToConst(getGinInDb() -1) ) * (this.h*1000) * getVi(wavelengthId) * this.bitRatePerChannelAsGBPS * Math.pow(10, 9) * dbToConst(GoutInDb[ nodes[i]] ) ;                    
            
            term1 = term1 / ( dbToConst(LdmInDb) * dbToConst(LswInDb[ nodes[i] ]) * dbToConst(LmxInDb) * dbToConst(LtapInDb) );
                        
            double term2 = 2 * this.AseFactor * ( dbToConst(GoutInDb[ nodes[i] ] - 1)  ) * (this.h*1000) * getVi(wavelengthId) * this.bitRatePerChannelAsGBPS * Math.pow(10, 9) ;                    
            
            term2 = term2 / ( dbToConst(LtapInDb));
            
            powerValues[i] = powerValues[i] + term1;
            powerValues[i] = powerValues[i] + term2;
            
            double oldvalue = PaseMatrix[ nodes[i-1] ][ nodes[i] ][ wavelengthId ];
            PaseMatrix[ nodes[i-1] ][ nodes[i] ][ wavelengthId ] = powerValues[i];            
            if( snapshotActive && oldPaseMatrixListInList[ nodes[i-1] ][ nodes[i] ][wavelengthId] == false)
            {
                this.oldPaseMatrixList.add( new Tupple(nodes[i-1], nodes[i], wavelengthId, oldvalue) );
                oldPaseMatrixListInList[ nodes[i-1] ][ nodes[i] ][wavelengthId] = true;
            }
        }
        
        return powerValues;
    }
    
    /*
     * nodePowerValues: i.node j. wavelength
     */
    public double[] calcPxt( LightPath lightPath, double [][]physicalMatrixInKm  )
    {
        int nodes[] = lightPath.getPhysicalLinks();
        double []powerValues = new double[nodes.length];
        
        int wavelengthId = lightPath.getWavelengthId();
        
        powerValues[0] = 0;        
        
        for( int i = 1; i < nodes.length; i++ )
        {
            boolean oldValue = PxtMatrixPathExist[ nodes[i-1] ][ nodes[i] ][ wavelengthId ];
            PxtMatrixPathExist[ nodes[i-1] ][ nodes[i] ][ wavelengthId ] = true;
            if( snapshotActive )
            {
                if (oldValue)
                    this.oldPxtMatrixPathExistList.add( new Tupple(nodes[i-1], nodes[i], wavelengthId, 1) );
                else
                    this.oldPxtMatrixPathExistList.add( new Tupple(nodes[i-1], nodes[i], wavelengthId, 0) );                
            }               
            double distance = physicalMatrixInKm[ nodes[i-1] ][ nodes[i] ];
            powerValues[i] = powerValues[i-1] * dbToConst( gainsAlongPathInDb(nodes[i-1], nodes[i],distance) );            
            
            double totalCrossTalkPower = 0;
            int []onlineTransmitters = getTransmittersForPxt( nodes[i], wavelengthId );
            for(int j = 0; j <  onlineTransmitters.length ;j++)
            {                
                int currOnlineTransmiter = onlineTransmitters[j];
                int debug = 0;
                if( (currOnlineTransmiter == 9) && (nodes[i] ==13) )
                    debug = 1;                
                if( currOnlineTransmiter == nodes[i-1] )    /*if it is the considered signal*/
                    continue;
                double term1 = getTotalPower( currOnlineTransmiter, nodes[i], wavelengthId ) * dbToConst( GoutInDb[nodes[i]] );
                term1 = term1 / ( dbToConst(XswInDb) * dbToConst(LswInDb[ nodes[i] ]) * dbToConst(LmxInDb) * dbToConst(LtapInDb)) ;                
                totalCrossTalkPower += term1;
            }
                    
            powerValues[i] = powerValues[i] + totalCrossTalkPower;
                      
            double doubleOldValue = PxtMatrix[ nodes[i-1] ][ nodes[i] ][ wavelengthId ];
            PxtMatrix[ nodes[i-1] ][ nodes[i] ][ wavelengthId ] = powerValues[i];                                            
            if( snapshotActive && oldPxtMatrixListInList[ nodes[i-1] ][ nodes[i] ][wavelengthId] == false )
            {
                this.oldPxtMatrixList.add( new Tupple(nodes[i-1], nodes[i], wavelengthId, doubleOldValue) );
                oldPxtMatrixListInList[ nodes[i-1] ][ nodes[i] ][wavelengthId] = true;
            }            
            
            /*Update Pxt of copropagating lightpaths*/
            for(int j = 0; j < onlineTransmitters.length ;j++)
            {
                int currTransmiter = onlineTransmitters[j];
                if( currTransmiter == nodes[i-1])       /*It is already calculated*/
                    continue;
                totalCrossTalkPower = 0;
                for(int coProSignal = 0; coProSignal < onlineTransmitters.length; coProSignal++ )
                {
                    if( j == coProSignal )        /*k.signal cannot affect the k.signal (same signal)*/
                        continue;
                    double term1 = getTotalPower( onlineTransmitters[coProSignal], nodes[i], wavelengthId ) * dbToConst( GoutInDb[nodes[i]] );
                    term1 = term1 / ( dbToConst(XswInDb) * dbToConst(LswInDb[ nodes[i] ]) * dbToConst(LmxInDb) * dbToConst(LtapInDb)) ;                
                    totalCrossTalkPower += term1;                                        
                }
                doubleOldValue = PxtMatrix[ currTransmiter ][ nodes[i] ][ wavelengthId ];
                PxtMatrix[ currTransmiter ][ nodes[i] ][ wavelengthId ] = totalCrossTalkPower;
                if( snapshotActive && oldPsigMatrixListInList[ currTransmiter ][ nodes[i] ][wavelengthId] == false)
                {
                    this.oldPxtMatrixList.add( new Tupple(currTransmiter, nodes[i], wavelengthId, doubleOldValue) );
                    oldPxtMatrixListInList[ currTransmiter ][ nodes[i] ][wavelengthId] = true;
                }                   
                /*THIS CROSSTALK POWER MUST BE UPDATED ALONG THE PATH*/
            }
            
        }        
        
        return powerValues;
    }
    
    public double getTotalPower(int tranNodeIndex, int recvNodeIndex, int wavelengthId)
    {
        double totalPower = 0;
        
        totalPower = PsigMatrix[tranNodeIndex][recvNodeIndex][wavelengthId] + PaseMatrix[tranNodeIndex][recvNodeIndex][wavelengthId] + PxtMatrix[tranNodeIndex][recvNodeIndex][wavelengthId];
        
        return totalPower;
    }
    
    public double getVi(int i )
    {
        return (lightSpeed / wavelengthsInNM[i] ) * Math.pow(10, 9);
    }
    
    public int []getTransmittersForPxt(int receivernode, int wavelengthId)
    {
        int connectedNumber = 0;    
        for(int i = 0; i < VTDesignParams.numOfNode; i++ )
        {
            if( PxtMatrixPathExist[i][receivernode][wavelengthId] == true )
            {
                connectedNumber++;
            }
        }
        
        int result[] = new int[ connectedNumber ];
        int currIndex = 0;
        for(int i = 0; i < VTDesignParams.numOfNode; i++ )
        {
            if( PxtMatrixPathExist[i][receivernode][wavelengthId] == true )
            {
                result[currIndex++] = i;
            }
        }        
        
        return result;
    }
    
    public double gainsAlongPathInDb( int transNode, int recvNode, double distance )
    {
            double gains = this.getGinInDb() + this.GoutInDb[recvNode];
            double fiberLoss = (this.LfInDbKm*distance);
            double loses = fiberLoss + this.LdmInDb + this.LswInDb[recvNode] + this.LmxInDb + 2 * this.LtapInDb;
            double totalDb = gains - loses;        
            
            return totalDb;
    }
    
    public double varianceSignalCrossBeat(LightPath lightpath, boolean zero)
    {
        double result = 0;
        double bi = 2;
        
        if( zero )
            bi = 0;        
        
        int receiver,lastTransmitter;
        int []nodes = lightpath.getPhysicalLinks();
        int wavelength = lightpath.getWavelengthId();
        receiver = nodes[nodes.length - 1];
        lastTransmitter = nodes[nodes.length - 2];
        
        result = (double)(2 * (0.5) * bi );
        result = result * Math.pow(Rlambda, 2);
        result = result * this.PsigMatrix[lastTransmitter][receiver][wavelength]*this.PxtMatrix[lastTransmitter][receiver][wavelength];      
        
        return result;
    }
    
    public double varianceSignalAseBeat(LightPath lightpath, boolean zero)
    {
        double result = 0;
        double bi = 2;
        
        if( zero )
            bi = 0;     
        
        int receiver,lastTransmitter;
        int []nodes = lightpath.getPhysicalLinks();
        int wavelength = lightpath.getWavelengthId();
        receiver = nodes[nodes.length - 1];
        lastTransmitter = nodes[nodes.length - 2];        
        
        result = 4 * Math.pow(Rlambda, 2) * bi * (this.electronicBandwidthASGBPS / this.bitRatePerChannelAsGBPS) * this.PsigMatrix[lastTransmitter][receiver][wavelength]*this.PaseMatrix[lastTransmitter][receiver][wavelength];
        
        return result;        
    }
    
    public double varianceShotNoise(LightPath lightpath, boolean zero)
    {
        double result = 0;
        double bi = 2;
        
        if( zero )
            bi = 0;        
        
        int receiver,lastTransmitter;
        int []nodes = lightpath.getPhysicalLinks();
        int wavelength = lightpath.getWavelengthId();
        receiver = nodes[nodes.length - 1];
        lastTransmitter = nodes[nodes.length - 2]; 
        
        result = 2 * this.Rlambda * (bi * this.PsigMatrix[lastTransmitter][receiver][wavelength]
                + this.PxtMatrix[lastTransmitter][receiver][wavelength]
                + this.PaseMatrix[lastTransmitter][receiver][wavelength]
                ) * this.electronicBandwidthASGBPS;
        
        return result;        
    }
    
    public double varianceThermalNoise(LightPath lightpath, boolean zero)
    {
        double result = 0;
        
        result = this.Nth * this.electronicBandwidthASGBPS * Math.pow(10, 9);
        
        return result;        
    }

    private double noiseVarince(LightPath lightpath, boolean zero)
    {
        double result = this.varianceSignalCrossBeat(lightpath,zero) + this.varianceSignalAseBeat(lightpath,zero) /*+ this.varianceShotNoise(lightpath,zero) */+ this.varianceThermalNoise(lightpath,zero);
        return result;
    }
    
    public double calculateBER(LightPath lightpath, double [][]physicalMatrixInKm, boolean update)
    {
        double Ith, Is1;
        double  squareOfZeroNoiseVariance,squareOfOneNoiseVariance;
        
        /*DEBUG*/
/*        for(int i = 0; i < physicalMatrixInKm.length; i++)
            Arrays.fill( physicalMatrixInKm[i], 100 );*/
        /*DEBUG*/
        
        this.calcPase(lightpath, physicalMatrixInKm );
        this.calcPsig(lightpath, physicalMatrixInKm );
        /*Crosstalk power must be estimated at last*/
        this.calcPxt(lightpath, physicalMatrixInKm );
        
        squareOfZeroNoiseVariance = Math.sqrt( this.noiseVarince( lightpath, true ) );
        squareOfOneNoiseVariance = Math.sqrt( this.noiseVarince( lightpath, false ) );                
        
        int receiver,lastTransmitter;
        int []nodes = lightpath.getPhysicalLinks();
        int wavelength = lightpath.getWavelengthId();
        receiver = nodes[nodes.length - 1];
        lastTransmitter = nodes[nodes.length - 2];         
        
        double bi = 2;
        Is1 = this.Rlambda * this.PsigMatrix[lastTransmitter][receiver][wavelength] * bi;
        
        //System.out.println("Psig: " + this.PsigMatrix[lastTransmitter][receiver][wavelength] );
        //System.out.println("Pxt: " + this.PxtMatrix[lastTransmitter][receiver][wavelength]);
        //System.out.println("Pase: " + this.PaseMatrix[lastTransmitter][receiver][wavelength]);
        Ith = (Is1/2);      
        
        double ber, term1, term2;                
        
        term1 = erfc( (Is1 - Ith) / ( Math.sqrt(2)* squareOfOneNoiseVariance) );
        term2 = erfc( Ith / ( Math.sqrt(2)* squareOfZeroNoiseVariance) );
        
        ber = 0.25 * ( term1 + term2);
        
        lightpath.setBER(ber);
        return ber;
      
    }
    
    public static double erfc(double qFactor)
    {
        double result;
        
        result = Math.exp( -1 * (qFactor * qFactor) ) / (qFactor * Math.sqrt(Math.PI));
        
        return result;
    }
    
    public double dbmToMiliWatt(double dbmValue)
    {
        double result;
        result = Math.pow(10, dbmValue / 10);
        return result;
    }

    public double getGinInDb() {
        return GinInDb;
    }

    public void setGinInDb(double GinInDb) {
        this.GinInDb = GinInDb;
    }
    
    public void takeSnapShot()
    {
//        this.oldPaseMatrix = cloneArray(PaseMatrix);
//        this.oldPsigMatrix = cloneArray(PsigMatrix);
//        this.oldPxtMatrix = cloneArray(PxtMatrix);
//        this.oldPxtMatrixPathExist = cloneArray(PxtMatrixPathExist);
        this.oldPxtMatrixList = new ArrayList();
        this.oldPaseMatrixList = new ArrayList();
        this.oldPsigMatrixList = new ArrayList();
        this.oldPxtMatrixPathExistList = new ArrayList();
        snapshotActive = true;
        
    }    
    
    public static double[][][]cloneArray( double inArray[][][])
    {
        double newArray[][][];
        newArray = new double[ inArray.length ][ inArray[0].length ][ inArray[0][0].length];
        
        for(int i = 0; i < inArray.length; i++ )
        {
            for( int j = 0; j < inArray[i].length; j++ )
            {
                newArray[i][j] = Arrays.copyOf(inArray[i][j], inArray[i][j].length );
            }
        }
        return newArray;
    }
    
    public static boolean[][][]cloneArray( boolean inArray[][][])
    {
        boolean newArray[][][];
        newArray = new boolean[ inArray.length ][ inArray[0].length ][ inArray[0][0].length];
        
        for(int i = 0; i < inArray.length; i++ )
        {
            for( int j = 0; j < inArray[i].length; j++ )
            {
                newArray[i][j] = Arrays.copyOf(inArray[i][j], inArray[i][j].length );
            }
        }
        
        return newArray;
    }    
    
    
    
    public void loadSnapShot()
    {
//        this.PaseMatrix = oldPaseMatrix;
//        this.PsigMatrix = oldPsigMatrix;
//        this.PxtMatrix = oldPxtMatrix;
//        this.PxtMatrixPathExist = oldPxtMatrixPathExist;                
        
        for( int i = 0; i < this.oldPaseMatrixList.size(); i++ )
        {
            Tupple currTupple = this.oldPaseMatrixList.get(i);
            int x = currTupple.first;
            int y = currTupple.second;
            int z = currTupple.third;
            PaseMatrix[x][y][z]= currTupple.value;
            oldPaseMatrixListInList[x][y][z] = false;
        }
        
        for( int i = 0; i < this.oldPsigMatrixList.size(); i++ )
        {
            Tupple currTupple = this.oldPsigMatrixList.get(i);
            int x = currTupple.first;
            int y = currTupple.second;
            int z = currTupple.third;
            PsigMatrix[x][y][z]= currTupple.value;
            oldPsigMatrixListInList[x][y][z] = false;            
        }
        
        for( int i = 0; i < this.oldPxtMatrixList.size(); i++ )
        {
            Tupple currTupple = this.oldPxtMatrixList.get(i);
            int x = currTupple.first;
            int y = currTupple.second;
            int z = currTupple.third;
            PxtMatrix[x][y][z]= currTupple.value;
            oldPxtMatrixListInList[x][y][z] = false;            
        }
        
        for( int i = 0; i < this.oldPxtMatrixPathExistList.size(); i++ )
        {
            Tupple currTupple = this.oldPxtMatrixPathExistList.get(i);
            int x = currTupple.first;
            int y = currTupple.second;
            int z = currTupple.third;
            PxtMatrixPathExist[x][y][z] = (currTupple.value != 0);
        }        
        
        snapshotActive = false;
    }
    
    public static void main(String args[])
    {
        for(int i = -1; i < 10; i++)
            System.out.println( QTool.erfc(i));
    }    
 
}
