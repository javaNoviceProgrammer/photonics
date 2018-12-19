package photonics.wg;

import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.numbers.Complex;

public class LumpedReflector {

	public double refAmp, refPhaseRad ;

	public Complex port1, port2 ;
	Complex port1_accumulated, port2_accumulated ;

	public Complex S11, S12 ;
	public Complex S21, S22 ;

	public LumpedReflector(
			@ParamName(name="Field Reflection Strength") double refAmp,
			@ParamName(name="Field Reflection Phase (rad)") double refPhaseRad
			){
		this.refAmp = refAmp ;
		this.refPhaseRad = refPhaseRad ;

		initializePorts() ;
		calculateScattParams() ;
	}

	private void calculateScattParams(){
		S11 = new Complex(refAmp*Math.cos(refPhaseRad), refAmp*Math.sin(refPhaseRad)) ;
		S22 = new Complex(refAmp*Math.cos(refPhaseRad), refAmp*Math.sin(refPhaseRad)) ;
		double tAmp = Math.sqrt(1-refAmp*refAmp) ;
		double tPhaseRad = refPhaseRad + Math.PI/2 ;
		S12 = new Complex(tAmp*Math.cos(tPhaseRad), tAmp*Math.sin(tPhaseRad)) ;
		S21 = new Complex(tAmp*Math.cos(tPhaseRad), tAmp*Math.sin(tPhaseRad)) ;
	}

	public void initializePorts(){
		port1 = port2 = Complex.ZERO ;
		port1_accumulated = port2_accumulated = Complex.ZERO ;
	}

	public void connectPorts(Complex port1In, Complex port2In){
		port1 = getPort1(port1In, port2In) ;
		port2 = getPort2(port1In, port2In) ;
		port1_accumulated = port1_accumulated.plus(port1) ;
		port2_accumulated = port2_accumulated.plus(port2) ;
	}

	public Complex getPort1(){
		return port1_accumulated ;
	}

	public Complex getPort2(){
		return port2_accumulated ;
	}

	public Complex getS11(){
		return S11 ;
	}

	public Complex getS21(){
		return S21 ;
	}

	public Complex getS12(){
		return S12 ;
	}

	public Complex getS22(){
		return S22 ;
	}

	private Complex getPort1(Complex port1In, Complex port2In){
		Complex T1 = port1In.times(S11) ;
		Complex T2 = port2In.times(S12) ;
		return T1.plus(T2) ;
	}

	private Complex getPort2(Complex port1In, Complex port2In){
		Complex T1 = port1In.times(S21) ;
		Complex T2 = port2In.times(S22) ;
		return T1.plus(T2) ;
	}


}
