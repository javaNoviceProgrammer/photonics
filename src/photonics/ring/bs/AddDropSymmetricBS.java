package photonics.ring.bs;

import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.numbers.Complex;
import photonics.wg.LumpedReflector;

public class AddDropSymmetricBS {

	public LumpedReflector LR ;
	public double resLambdaNm, FSR_nm, phiRad, k1, t1, k2, t2, r, t, L ;
	Complex O ;

	public Complex port1, port2, port3, port4 ;
	Complex port1_accumulated, port2_accumulated, port3_accumulated, port4_accumulated ;

	Complex zero = Complex.ZERO ;
	Complex one = Complex.ONE ;

	public Complex S11, S12, S13, S14 ;
	public Complex S21, S22, S23, S24 ;
	public Complex S31, S32, S33, S34 ;
	public Complex S41, S42, S43, S44 ;

	int steps = 1000 ;

	public AddDropSymmetricBS(
			@ParamName(name="input kappa") double inputKappa,
			@ParamName(name="output kappa") double outputKappa,
			@ParamName(name="Round-trip power attenuation of the ring") double L,
			@ParamName(name="Round-trip phase (rad)") double phi_rad,
			@ParamName(name="Lumped Reflector Model") LumpedReflector LR
			){
		this.LR = LR ;
		this.L = L ;
		k1 = inputKappa ;
		t1 = Math.sqrt(1-k1*k1) ;
		k2 = outputKappa ;
		t2 = Math.sqrt(1-k2*k2) ;
		r = LR.S11.abs() ;
		t = LR.S21.abs() ;
		this.phiRad = phi_rad  ;
		O = Complex.minusJ.times(phiRad).exp().times(t1*t2*Math.sqrt(L)) ;
		
		initializePorts() ;
		calculateScattParams() ;
	}

	public void setNumIterations(int steps){
		this.steps = steps ;
	}

	private void calculateScattParams(){
		S11 = calculateS11(); 
		S21 = calculateS21(); 
		S31 = calculateS31(); 
		S41 = calculateS41();
		S12 = S21 ; 
		S22 = S11 ; 
		S32 = S41 ; 
		S42 = S31 ;
		S13 = S31 ; 
		S23 = S41 ; 
		S33 = S11 ; 
		S43 = S21 ;
		S14 = S41 ; 
		S24 = S31 ; 
		S34 = S21 ; 
		S44 = S11 ;
	}
	
	private Complex calculateS11(){
		Complex denum = O.times(O).plus(one).minus(O.times(2*t)) ;
		Complex num = Complex.plusJ.times(k1*k1*r) ;
		return num.divides(denum) ;
	}
	
	private Complex calculateS21(){
		Complex denum = O.times(O).plus(one).minus(O.times(2*t)) ;
		Complex num = O.times(O).plus(t1*t1).minus(O.times(t*(1+t1*t1))).divides(t1) ;
		return num.divides(denum) ;
	}
	
	private Complex calculateS31(){
		Complex denum = O.times(O).plus(one).minus(O.times(2*t)) ;
		Complex a = Complex.plusJ.times(k1*k2*r).times(Math.pow(L, 0.25)) ;
		Complex num = Complex.minusJ.times(phiRad/2).exp().times(a) ;
		return num.divides(denum) ;
	}
	
	private Complex calculateS41(){
		Complex denum = O.times(O).plus(one).minus(O.times(2*t)) ;
		Complex a = Complex.minusJ.times(phiRad/2).exp().times(k1*k2*Math.pow(L, 0.25)) ;
		Complex num = O.minus(t).times(a) ;
		return num.divides(denum) ;
	}

	public void initializePorts(){
		port1 = port2 = port3 = port4 = Complex.ZERO ;
		port1_accumulated = port2_accumulated = port3_accumulated = port4_accumulated = Complex.ZERO ;
	}

	public void connectPorts(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		port1 = getPort1(port1In, port2In, port3In, port4In) ;
		port2 = getPort2(port1In, port2In, port3In, port4In) ;
		port3 = getPort3(port1In, port2In, port3In, port4In) ;
		port4 = getPort4(port1In, port2In, port3In, port4In) ;
		port1_accumulated = port1_accumulated.plus(port1) ;
		port2_accumulated = port2_accumulated.plus(port2) ;
		port3_accumulated = port3_accumulated.plus(port3) ;
		port4_accumulated = port4_accumulated.plus(port4) ;
	}
	
	public Complex getPort1(){
		return port1_accumulated ;
	}
	
	public Complex getPort2(){
		return port2_accumulated ;
	}
	
	public Complex getPort3(){
		return port3_accumulated ;
	}
	
	public Complex getPort4(){
		return port4_accumulated ;
	}

	private Complex getPort1(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S11) ;
		Complex T2 = port2In.times(S12) ;
		Complex T3 = port3In.times(S13) ;
		Complex T4 = port4In.times(S14) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	private Complex getPort2(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S21) ;
		Complex T2 = port2In.times(S22) ;
		Complex T3 = port3In.times(S23) ;
		Complex T4 = port4In.times(S24) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	private Complex getPort3(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S31) ;
		Complex T2 = port2In.times(S32) ;
		Complex T3 = port3In.times(S33) ;
		Complex T4 = port4In.times(S34) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	private Complex getPort4(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S41) ;
		Complex T2 = port2In.times(S42) ;
		Complex T3 = port3In.times(S43) ;
		Complex T4 = port4In.times(S44) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}


}
