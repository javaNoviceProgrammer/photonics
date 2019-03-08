package photonics.wg.coupling;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.interpolation.CubicSpline;
import mathLib.matrix.ComplexMatrix;
import mathLib.numbers.Complex;
import photonics.util.Wavelength;

public class RaceTrackCoupler {

	double ZminMicron, ZmaxMicron ;
	double gapNm, lengthMicron , radiusMicron, widthMicron = 0.45 ;
	double gapMaxNm = 1000 ;
	public Wavelength inputLambda ;

	public int numIntervals = 100 ;

	double neffEven = Double.NaN, neffOdd = Double.NaN ;

	CubicSpline neffEvenGapInterpolator, neffOddGapInterpolator  ;

	ComplexMatrix scattMatrix = new ComplexMatrix(2,2) ;
	Complex zero = new Complex(0,0) , one = new Complex(1,0) ;

	public RaceTrackCoupler(
			Wavelength inputLambda,
			@ParamName(name="Radius (micron)") double radiusMicron,
			@ParamName(name="gap size (nm)") double gapNm,
			@ParamName(name="coupling length (um)") double lengthMicron
			){
		this.inputLambda = inputLambda ;
		this.gapNm = gapNm ;
		this.lengthMicron = lengthMicron ;
		this.gapMaxNm = 500 ;
		this.radiusMicron = radiusMicron ;
		this.ZmaxMicron = Math.sqrt( (gapMaxNm-gapNm)/1000 * (2*radiusMicron-(gapMaxNm-gapNm)/1000)) + lengthMicron/2d ;
		this.ZminMicron = -this.ZmaxMicron ;
		this.scattMatrix = getScattMatrix() ;
	}

	public RaceTrackCoupler(
			Wavelength inputLambda,
			@ParamName(name="Waveguide width (nm)") double widthNm,
			@ParamName(name="Radius (micron)") double radiusMicron,
			@ParamName(name="gap size (nm)") double gapNm,
			@ParamName(name="max coupling gap (nm)") double gapMaxNm,
			@ParamName(name="coupling length (um)") double lengthMicron,
			CubicSpline neffEvenGapInterpolator,
			CubicSpline neffOddGapInterpolator
			){
		this.inputLambda = inputLambda ;
		this.gapNm = gapNm ;
		this.gapMaxNm = gapMaxNm ;
		this.lengthMicron = lengthMicron ;
		this.widthMicron = widthNm*1e-3 ;
		this.radiusMicron = radiusMicron ;
		this.ZmaxMicron = Math.sqrt( (gapMaxNm-gapNm)/1000 * (2*(radiusMicron+widthMicron/2)-(gapMaxNm-gapNm)/1000)) + lengthMicron/2d ;
		this.ZminMicron = -this.ZmaxMicron ;
		this.neffEvenGapInterpolator = neffEvenGapInterpolator ;
		this.neffOddGapInterpolator = neffOddGapInterpolator ;
		this.scattMatrix = getScattMatrix() ;
	}

	public RaceTrackCoupler(
			Wavelength inputLambda,
			@ParamName(name="Waveguide width (nm)") double widthNm,
			@ParamName(name="Radius (micron)") double radiusMicron,
			@ParamName(name="gap size (nm)") double gapNm,
			@ParamName(name="coupling length (um)") double lengthMicron,
			CubicSpline neffEvenGapInterpolator,
			CubicSpline neffOddGapInterpolator
			){
		this.inputLambda = inputLambda ;
		this.gapNm = gapNm ;
		this.lengthMicron = lengthMicron ;
		this.gapMaxNm = 500 ;
		this.widthMicron = widthNm*1e-3 ;
		this.radiusMicron = radiusMicron ; // need to modify this later
		this.ZmaxMicron = Math.sqrt( (gapMaxNm-gapNm)/1000 * (2*(radiusMicron+widthMicron/2)-(gapMaxNm-gapNm)/1000)) + lengthMicron/2d ;
		this.ZminMicron = -this.ZmaxMicron ;
		this.neffEvenGapInterpolator = neffEvenGapInterpolator ;
		this.neffOddGapInterpolator = neffOddGapInterpolator ;
		this.scattMatrix = getScattMatrix() ;
	}

	public double getRadiusMicron(){
		return radiusMicron ;
	}

	public double getLengthMicron(){
		return lengthMicron ;
	}

	public double getWavelengthNm(){
		return inputLambda.getWavelengthNm() ;
	}

	public double getGapSizeNm(){
		return gapNm ;
	}

	public double getZmin(){
		return ZminMicron ;
	}

	public double getZmax(){
		return ZmaxMicron ;
	}

	public int getNumIntervals(){
		return numIntervals ;
	}

	public void setNumIntervals(int N){
		numIntervals = N ;
		this.scattMatrix = getScattMatrix() ;
	}

	private Complex S21(double z, double Dz){
		DistributedCouplerStripWg DC ;
		if(neffEvenGapInterpolator != null && neffOddGapInterpolator != null){
			neffEven = neffEvenGapInterpolator.interpolate(getCouplingGapNm(z)) ;
			neffOdd = neffOddGapInterpolator.interpolate(getCouplingGapNm(z)) ;
			DC = new DistributedCouplerStripWg(inputLambda, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		}
		else{
			DC = new DistributedCouplerStripWg(inputLambda, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		}
		return DC.S21 ;
	}

	private Complex S31(double z, double Dz){
		DistributedCouplerStripWg DC ;
		if(neffEvenGapInterpolator != null && neffOddGapInterpolator != null){
			neffEven = neffEvenGapInterpolator.interpolate(getCouplingGapNm(z)) ;
			neffOdd = neffOddGapInterpolator.interpolate(getCouplingGapNm(z)) ;
			DC = new DistributedCouplerStripWg(inputLambda, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		}
		else{
			DC = new DistributedCouplerStripWg(inputLambda, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		}
		return DC.S31 ;
	}

	private Complex S24(double z, double Dz){
		DistributedCouplerStripWg DC ;
		if(neffEvenGapInterpolator != null && neffOddGapInterpolator != null){
			neffEven = neffEvenGapInterpolator.interpolate(getCouplingGapNm(z)) ;
			neffOdd = neffOddGapInterpolator.interpolate(getCouplingGapNm(z)) ;
			DC = new DistributedCouplerStripWg(inputLambda, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		}
		else{
			DC = new DistributedCouplerStripWg(inputLambda, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		}
		return DC.S24 ;
	}

	private Complex S34(double z, double Dz){
		DistributedCouplerStripWg DC ;
		if(neffEvenGapInterpolator != null && neffOddGapInterpolator != null){
			neffEven = neffEvenGapInterpolator.interpolate(getCouplingGapNm(z)) ;
			neffOdd = neffOddGapInterpolator.interpolate(getCouplingGapNm(z)) ;
			DC = new DistributedCouplerStripWg(inputLambda, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		}
		else{
			DC = new DistributedCouplerStripWg(inputLambda, Dz, getCouplingGapNm(z), neffEven, neffOdd) ;
		}
		return DC.S34 ;
	}

	// this method defines the non-uniform gap equation
	public double getCouplingGapNm(double z){
		if(Math.abs(z) <= lengthMicron/2.0){
			return gapNm ;
		}
		else{
			double gMicron = gapNm/1000 + radiusMicron+widthMicron/2 - Math.sqrt((radiusMicron+widthMicron/2)*(radiusMicron+widthMicron/2) - (Math.abs(z)-lengthMicron/2) * (Math.abs(z)-lengthMicron/2)) ;
			double gNm = gMicron * 1000 ;
			return gNm ;
		}
	}
	// Now finally calculate the scattering parameters
	private ComplexMatrix getScattMatrix(){
		int N = numIntervals ;
		double Dz = (ZmaxMicron-ZminMicron)/N ;
		double[] Z = new double[N] ;
		double[] Zmid = new double[N] ;
		double[] g = new double[N] ;
		for(int i=0; i<N; i++){
			Z[i] = ZminMicron + i * Dz ;
			Zmid[i] = Z[i] + Dz/2 ;
			g[i] = getCouplingGapNm(Z[i]) ;
		}
		Complex[][] d = {{new Complex(1,0), new Complex(0,0)}, {new Complex(0,0), new Complex(1,0)}} ;
		ComplexMatrix M = new ComplexMatrix(d) ;
		for(int i=0; i<N; i++){
			Complex[][] di = {{S21(Zmid[i], Dz), S24(Zmid[i], Dz)}, {S31(Zmid[i], Dz), S34(Zmid[i], Dz)}} ;
			ComplexMatrix Mi = new ComplexMatrix(di) ;
			M = M.times(Mi) ;
		}
		return M ;
	}
	// Return scattering parameters
	public Complex getS11(){
		return new Complex(0,0) ;
	}

	public Complex getS21(){
		return scattMatrix.getElement(0, 0) ;
	}

	public Complex getS31(){
		return scattMatrix.getElement(1, 0) ;
	}

	public Complex getS41(){
		return new Complex(0,0) ;
	}

	public Complex getS12(){
		return getS21() ;
	}

	public Complex getS22(){
		return new Complex(0,0) ;
	}

	public Complex getS32(){
		return new Complex(0,0) ;
	}

	public Complex getS42(){
		return scattMatrix.getElement(0, 1) ;
	}

	public Complex getS13(){
		return getS31() ;
	}

	public Complex getS23(){
		return getS32() ;
	}

	public Complex getS33(){
		return new Complex(0,0) ;
	}

	public Complex getS43(){
		return scattMatrix.getElement(1, 1) ;
	}

	public Complex getS14(){
		return getS41() ;
	}

	public Complex getS24(){
		return getS42() ;
	}

	public Complex getS34(){
		return getS43() ;
	}

	public Complex getS44(){
		return new Complex(0,0) ;
	}

	// Get the ports
	public Complex getPort1(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(getS11()) ;
		Complex T2 = port2In.times(getS12()) ;
		Complex T3 = port3In.times(getS13()) ;
		Complex T4 = port4In.times(getS14()) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	public Complex getPort2(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(getS21()) ;
		Complex T2 = port2In.times(getS22()) ;
		Complex T3 = port3In.times(getS23()) ;
		Complex T4 = port4In.times(getS24()) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	public Complex getPort3(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(getS31()) ;
		Complex T2 = port2In.times(getS32()) ;
		Complex T3 = port3In.times(getS33()) ;
		Complex T4 = port4In.times(getS34()) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	public Complex getPort4(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(getS41()) ;
		Complex T2 = port2In.times(getS42()) ;
		Complex T3 = port3In.times(getS43()) ;
		Complex T4 = port4In.times(getS44()) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

}
