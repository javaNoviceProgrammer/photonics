package photonics.pnjunc.pin;

import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.plot.MatlabChart;

import static java.lang.Math.* ;
import static mathLib.util.MathUtils.*;

public class PINShiftResDC {
	
	double a ; // no units
	double c ; // nm/(mA)^2 units
	double I0 ; // in mA
	
	public PINShiftResDC(
			@ParamName(name="a") double a,
			@ParamName(name="c (nm/(mA)^2)") double c,
			@ParamName(name="I0 (mA)") double I0
			){
		this.a = a ;
		this.c = c ;
		this.I0 = I0 ;
	}
	
	public double getDlambdaNm(double I_mA){
		double dLambda_nm = -a *(sqrt(1+I_mA/I0)-1) + c * I_mA*I_mA ;
		return dLambda_nm ;
	}
	
	// for test
	public static void main(String[] args){
		PINShiftResDC pinShiftResDC = new PINShiftResDC(0.6566, 0.0808, 0.0909) ;
		double[] ImA = linspace(0, 10, 1000) ;
		double[] resShiftNm = new double[ImA.length] ;
		for(int i=0; i<ImA.length; i++){
			resShiftNm[i] = pinShiftResDC.getDlambdaNm(ImA[i]) ;
		}
		MatlabChart fig = new MatlabChart() ;
		fig.plot(ImA, resShiftNm);
		fig.renderPlot();
		fig.run(true);
	}
	

}
