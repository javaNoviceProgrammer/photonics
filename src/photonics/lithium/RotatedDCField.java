package photonics.lithium;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static photonics.lithium.EpsilonLN.*;

import mathLib.func.ArrayFunc;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;

public class RotatedDCField {

	double twg = 700 ; // nm
	double tslab = 300 ;
	double w = 1000 ;
	double g = 1250 ;

	private double getEps33Rotated(double phiRad) {
		double eps33Prime = cos(phiRad)*cos(phiRad) * eps33DC + sin(phiRad)*sin(phiRad)*eps22DC ;
		return eps33Prime ;
	}

	private double calculateFactor(double phiRad) {
		double eps33prime = getEps33Rotated(phiRad) ;
		double epsSio2 = 3.9 ;
		double a = eps33prime * twg/w ;
		double b = epsSio2*(twg-tslab)/g ;
		double c = eps33prime * tslab/g ;
		return 1+2*a/(b+c) ;
	}

	public double getRotationFactor(double phiRad) {
		return calculateFactor(0.0)/calculateFactor(phiRad) ;
	}

	public static void main(String[] args) {
		RotatedDCField rotation = new RotatedDCField() ;
		double[] phiRad = MathUtils.linspace(0.0, 2*PI, 1000) ;
		double[] phiDeg = ArrayFunc.apply(t -> t*180.0/PI, phiRad) ;
		double[] factor = ArrayFunc.apply(t -> rotation.getRotationFactor(t), phiRad) ;

		MatlabChart fig = new MatlabChart() ;
		fig.plot(phiDeg, factor);
		fig.renderPlot();
		fig.xlabel("Phi (degree)");
		fig.ylabel("Edc/Edcmax");
		fig.run(true);

	}


}
