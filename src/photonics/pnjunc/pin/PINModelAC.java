package photonics.pnjunc.pin;

import static java.lang.Math.PI;
import static mathLib.numbers.Complex.ONE;
import static mathLib.numbers.Complex.plusJ;
import static mathLib.util.MathUtils.linspace;

import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.numbers.Complex;
import mathLib.plot.MatlabChart;

public class PINModelAC {
	
	Complex Z0 = new Complex(50, 0) ; // reference impedance
	double RD_ohm, CD_pF, Rs2_ohm, Cox_pF, Rs1_ohm, Cp_fF ;
	
	public PINModelAC(
			@ParamName(name="RD (ohm)") double RD_ohm,
			@ParamName(name="CD (pF)") double CD_pF,
			@ParamName(name="Rs2 (ohm)") double Rs2_ohm,
			@ParamName(name="Cox (pF)") double Cox_pF,
			@ParamName(name="Rs1 (ohm)") double Rs1_ohm,
			@ParamName(name="Cp (fF)") double Cp_fF
			){
		this.RD_ohm = RD_ohm ;
		this.CD_pF = CD_pF ;
		this.Rs2_ohm = Rs2_ohm ;
		this.Cox_pF = Cox_pF ;
		this.Rs1_ohm = Rs1_ohm ;
		this.Cp_fF = Cp_fF ;
	}
	
	public void setRefZ0(Complex refZ0){
		this.Z0 = refZ0 ;
	}
	
	public Complex getZL(double freqGhz){
		double f_hZ = freqGhz*1e9 ;
		Complex s = plusJ.times(2*PI*f_hZ) ;
		Complex YD = s.times(CD_pF*1e-12).plus(1/RD_ohm) ;
		Complex ZDs2 = ONE.divides(YD).plus(Rs2_ohm) ;
		Complex YDOX = s.times(Cox_pF*1e-12).plus(ONE.divides(ZDs2)) ;
		Complex ZDOXs1 = ONE.divides(YDOX).plus(Rs1_ohm) ;
		Complex YDOXs1 = ONE.divides(ZDOXs1) ;
		Complex Ytotal = s.times(Cp_fF*1e-15).plus(YDOXs1) ;
		return Ytotal.reciprocal() ;
	}
	
	public Complex getYL(double freqGhz){
		double f_hZ = freqGhz*1e9 ;
		Complex s = plusJ.times(2*PI*f_hZ) ;
		Complex YD = s.times(CD_pF*1e-12).plus(1/RD_ohm) ;
		Complex ZDs2 = ONE.divides(YD).plus(Rs2_ohm) ;
		Complex YDOX = s.times(Cox_pF*1e-12).plus(ONE.divides(ZDs2)) ;
		Complex YDOXs1 = ONE.divides(YDOX).plus(Rs1_ohm) ;
		Complex Ytotal = s.times(Cp_fF*1e-15).plus(YDOXs1) ;
		return Ytotal ;
	}
	
	public Complex getS11(double freqGhz){
		Complex num = getZL(freqGhz).minus(Z0) ;
		Complex denom = getZL(freqGhz).plus(Z0) ;
		return num.divides(denom) ;
	}
	
	// for test
	public static void main(String[] args){
		PINModelAC pinModelAC = new PINModelAC(25.1, 6.42, 64.9, 0.18, 115.8, 5) ;
		double[] freqGhz = linspace(0, 20, 1000) ;
		double[] ZL_amp = new double[freqGhz.length] ;
		for(int i=0; i<freqGhz.length; i++){
			ZL_amp[i] = pinModelAC.getZL(freqGhz[i]).abs() ;
		}
		MatlabChart fig = new MatlabChart() ;
		fig.plot(freqGhz, ZL_amp);
		fig.renderPlot();
		fig.run(true);
	}
	

}
