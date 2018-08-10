package photonics.transfer;

import mathLib.matrix.ComplexMatrix;
import mathLib.numbers.Complex;
import photonics.util.Wavelength;

public class TransferMatrixTM {

	// Transfer function of amplitudes from "First" [material 1] to "Second" [material 2] of the interface
	
	// Step 1: get the input parameters: wavelength, incidence angle, index of left side, index of right side, position of interface
	// Step 2: Create the 2x2 complex preliminary matrix
	// Step 3: Have a method to return the inverse of a 2x2 matrix
	
	double mu0 = 4*Math.PI*1e-7 ;
	double eps0 = 1/(36*Math.PI) * 1e-9 ;
	double omega, lambdaNm, lambda, X, epsFirst, epsSecond, nFirst, nSecond, muFirst, muSecond, kFirst, kSecond   ;
	Complex thetaFirst ; 
	Complex thetaSecond, kNormalFirst, kNormalSecond, kTangentFirst, kTangentSecond ;
	Wavelength inputLamda ;
	
	Complex zero = new Complex(0,0), one = new Complex(1,0), plusJ = new Complex(0,1), minusJ = new Complex(0,-1) ;
	
	// This constructor based on incident angle --> must also include the total internal reflection...
	// The other option is to have the effective index (tangential component of the K vector at both sides of the interface) as the input
	public TransferMatrixTM(
			Wavelength inputLambda,
			double nFirst,
			double nSecond,
			double thetaFirstDegree,
			double Xnm
			){
		this.inputLamda = inputLambda ;
		X = Xnm * 1e-9 ;
		lambdaNm = inputLambda.getWavelengthNm() ;
		lambda = lambdaNm * 1e-9 ;
		omega = 2*Math.PI*inputLambda.getFreqHz() ;
		this.nFirst = nFirst; 
		this.nSecond = nSecond ;
		epsFirst = eps0 * nFirst * nFirst ;
		epsSecond = eps0 * nSecond * nSecond ;
		kFirst = 2*Math.PI/lambda * nFirst ;
		kSecond = 2*Math.PI/lambda * nSecond ;
		muFirst = mu0 ; muSecond = mu0 ;
		double thetaFirstRadian = thetaFirstDegree * Math.PI/180 ;
		// Let's work with complex angles instead of real angles to cover even the "Total Internal Reflection"
		thetaFirst = new Complex(thetaFirstDegree * Math.PI/180, 0) ;
		if(nFirst*Math.sin(thetaFirstRadian)/nSecond <= 1){
			double A = nFirst*Math.sin(thetaFirstRadian)/nSecond ;
			thetaSecond = new Complex(Math.asin(A), 0) ; // because of the Snell's law
		}
		else{
			double A = nFirst*Math.sin(thetaFirstRadian)/nSecond ;
			double a = Math.PI/2 ;
			double b = acosh(A) ;
			thetaSecond = new Complex(a, b) ; // in the case of "total internal reflection" we get complex reflection angle
		}
		kTangentFirst = thetaFirst.sin().times(kFirst) ;
		kTangentSecond = thetaSecond.sin().times(kSecond) ;
		kNormalFirst = thetaFirst.cos().times(kFirst) ;
		kNormalSecond = thetaSecond.cos().times(kSecond) ;
	}
	//****************************************************************************************************
		public TransferMatrixTM(
				Wavelength inputLambda,
				double nFirst,
				double nSecond,
				double neff,
				double Xnm,
				int dummy
				){
			this.inputLamda = inputLambda ;
			X = Xnm * 1e-9 ;
			lambdaNm = inputLambda.getWavelengthNm() ;
			lambda = lambdaNm * 1e-9 ;
			omega = 2*Math.PI*inputLambda.getFreqHz() ;
			this.nFirst = nFirst; 
			this.nSecond = nSecond ;
			epsFirst = eps0 * nFirst * nFirst ;
			epsSecond = eps0 * nSecond * nSecond ;
			kFirst = 2*Math.PI/lambda * nFirst ;
			kSecond = 2*Math.PI/lambda * nSecond ;
			muFirst = mu0 ; muSecond = mu0 ;
			//**************
			if(neff <= nFirst){
				thetaFirst = new Complex(Math.asin(neff/nFirst), 0) ; // because of the Snell's law
			}
			else{
				double A = neff/nFirst ;
				double a = Math.PI/2 ;
				double b = acosh(A) ;
				thetaFirst = new Complex(a, b) ; // in the case of "total internal reflection" we get complex reflection angle
			}
			//**************
			if(neff <= nSecond){
				thetaSecond = new Complex(Math.asin(neff/nSecond), 0) ; // because of the Snell's law
			}
			else{
				double A = neff/nSecond ;
				double a = Math.PI/2 ;
				double b = acosh(A) ;
				thetaSecond = new Complex(a, b) ; // in the case of "total internal reflection" we get complex reflection angle
			}
			//**************
			kTangentFirst = thetaFirst.sin().times(kFirst) ;
			kTangentSecond = thetaSecond.sin().times(kSecond) ;
			kNormalFirst = thetaFirst.cos().times(kFirst) ;
			kNormalSecond = thetaSecond.cos().times(kSecond) ;
		}
		//****************************************************************************************************
	
	private ComplexMatrix getMaterialMatrix(double eps, double mu, Complex kNormal){
		Complex[][] elements = new Complex[2][2] ;
		Complex plusE = plusJ.times(kNormal.times(X)).exp() ;
		Complex minusE = minusJ.times(kNormal.times(X)).exp() ;
		elements[0][0] = one.times(minusE) ;
		elements[0][1] = one.times(plusE) ;
		elements[1][0] = one.divides(plusJ.times(omega*eps)).times(minusE).times(minusJ).times(kNormal) ;
		elements[1][1] = one.divides(plusJ.times(omega*eps)).times(plusE).times(plusJ).times(kNormal) ;
		ComplexMatrix M = new ComplexMatrix(elements) ;
		return M ;
	}

	private ComplexMatrix getInverseMatrix(ComplexMatrix M){
		Complex[][] data = new Complex[2][2] ;
		Complex term1 = M.getElement(0, 0).times(M.getElement(1, 1)) ;
		Complex term2 = M.getElement(0, 1).times(M.getElement(1, 0)) ;
		Complex detM = term1.minus(term2) ;
		data[0][0] = one.divides(detM).times(M.getElement(1, 1)) ;
		data[0][1] = one.divides(detM).times(M.getElement(0, 1)).times(-1) ;
		data[1][0] = one.divides(detM).times(M.getElement(1, 0)).times(-1) ;
		data[1][1] = one.divides(detM).times(M.getElement(0, 0)) ;
		ComplexMatrix invM = new ComplexMatrix(data) ;
		return invM ;
	}
	
	private Complex getDeterminant(ComplexMatrix M){
		Complex term1 = M.getElement(0, 0).times(M.getElement(1, 1)) ;
		Complex term2 = M.getElement(0, 1).times(M.getElement(1, 0)) ;
		Complex detM = term1.minus(term2) ;
		return detM ;
	}
			
	public ComplexMatrix getTransferMatrix(){
		ComplexMatrix M1 = getMaterialMatrix(epsFirst, muFirst, kNormalFirst) ;
		ComplexMatrix M2 = getMaterialMatrix(epsSecond, muSecond, kNormalSecond) ;
		ComplexMatrix invM2 = getInverseMatrix(M2) ;
		ComplexMatrix Q1to2 = invM2.times(M1) ;
		return Q1to2 ;	
	}
			
	
	public Complex getComplexAngleOfRefractionDegree(){
		return thetaSecond.times(180/Math.PI) ;
	}	
	
	// reflection of electric field
	public Complex getComplexAngleOfReflectionDegree(){
		return thetaFirst.times(180/Math.PI);
	}
	
	public Complex getFieldReflection(){
		ComplexMatrix Q1to2 = getTransferMatrix() ;
		Complex R = Q1to2.getElement(1, 0).divides(Q1to2.getElement(1, 1)).times(-1) ;
		return R ;
	}
	
	public double getPowerReflection(){
		return getFieldReflection().absSquared() ;
	}
		
	public Complex getFieldTransmission(){
		ComplexMatrix Q1to2 = getTransferMatrix() ;
		Complex T = getDeterminant(Q1to2).divides(Q1to2.getElement(1, 1)) ;
		return T;
	}
	
	// For transmitted power you have to look at the poynting vector
	public double getPowerTransmission(){
		Complex T = getFieldTransmission() ; // this is magnetic field --> multiply by normal impedance
		Complex ZnormalFirst = kNormalFirst.divides(omega*epsFirst) ;
		Complex ZnormalSecond = kNormalSecond.divides(omega*epsSecond) ;
		double Trans_Coeff = T.absSquared() * (one.times(ZnormalSecond).conjugate()).re() / (one.times(ZnormalFirst).conjugate()).re() ;
		return Trans_Coeff ;
	}
	
	// Returning Material properties
	public Complex getKnormalFirst(){
		return kNormalFirst ;
	}
	
	public Complex getKtangentFirst(){
		return kTangentFirst ;
	}
	
	public Complex getKnormalSecond(){
		return kNormalSecond ;
	}
	
	public Complex getKtangentSecond(){
		return kTangentSecond ;
	}
	
	// adding the hyperbolic inverse functions
	static double asinh(double x) 
	{ 
	return Math.log(x + Math.sqrt(x*x + 1.0)); 
	} 

	static double acosh(double x) 
	{ 
	return Math.log(x + Math.sqrt(x*x - 1.0)); 
	} 

	static double atanh(double x) 
	{ 
	return 0.5*Math.log( (x + 1.0) / (-x + 1.0) ); 
	} 
	
}
