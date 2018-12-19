package photonics.wg;

import mathLib.numbers.Complex;

public class WgTermination {

	public Complex port1 ;
	Complex port1_accumulated ;

	public Complex S11 ; // this device only has one port and can only reflect

	public WgTermination(){

		initializePorts() ;
		calculateScattParams() ;
	}

	public void initializePorts(){
		port1 = Complex.ZERO ;
		port1_accumulated = Complex.ZERO ;
	}

	private void calculateScattParams(){
		this.S11 = Complex.ZERO ;
	}

	public void connectPorts(Complex port1In){
		port1 = getPort1(port1In) ;
		port1_accumulated = port1_accumulated.plus(port1) ;
	}

	public Complex getPort1(){
		return port1_accumulated ;
	}

	private Complex getPort1(Complex port1In){
		Complex T1 = port1In.times(S11) ;
		return T1 ;
	}

	public Complex getS11(){
		return S11 ;
	}

}
