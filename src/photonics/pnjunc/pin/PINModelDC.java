package photonics.pnjunc.pin;

import static mathLib.util.MathUtils.linspace;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootFunction;
import mathLib.plot.MatlabChart;

public class PINModelDC {

	double R_kohm ; // in Kilo Ohms
	double Vbi_volt ; // built-in diode voltage
	double n = 0.62 ; // non-ideality factor of the diode
	double Vthermal_volt = 0.026 ; // Thermal voltage = KT/q
	double Is_mA  ; // 90 nano amperes = 9e-5 mA


	public PINModelDC(
			@ParamName(name="Vbi (V)") double Vbi_volt,
			@ParamName(name="R (KOhm)") double R_kohm,
			@ParamName(name="Is (nA)") double Is_nA,
			@ParamName(name="n factor (0<n<1)") double n
			){
		this.Vbi_volt = Vbi_volt ;
		this.R_kohm = R_kohm ;
		this.Is_mA = Is_nA*1e-6 ;
		this.n = n ;
	}

	public double getVoltage(double I_mA){
		double V_volt = Vbi_volt + R_kohm * I_mA + 1/n * Vthermal_volt * Math.log(I_mA/Is_mA + 1) ;
		return V_volt ;
	}

	public double getCurrent(double voltage_V){
		if(voltage_V < Vbi_volt){
			return 0 ;
		}
		else{
			RealRootFunction func = new RealRootFunction() {

				@Override
				public double function(double I_mA) {
					return getVoltage(I_mA)-voltage_V ;
				}
			};
			RealRoot root = new RealRoot() ;
			root.setEstimate(1);
			return root.bisect(func, 0, 20) ;
		}
	}

	// for test
	public static void main(String[] args){
		PINModelDC pinModelDC = new PINModelDC(0.7, 0.25, 90, 0.62) ;
		double[] V_volt = linspace(0, 2, 1000) ;
		double[] I_mA = new double[V_volt.length] ;
		for(int i=0; i<V_volt.length; i++){
			I_mA[i] = pinModelDC.getCurrent(V_volt[i]);
		}
		MatlabChart fig = new MatlabChart() ;
		fig.plot(V_volt, I_mA);
		fig.renderPlot();
		fig.run(true);
	}

}
