package photonics.lithium;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static photonics.lithium.Crystal.Xcut;
import static photonics.lithium.EOCoeff.r13;
import static photonics.lithium.EOCoeff.r22;
import static photonics.lithium.EOCoeff.r23;
import static photonics.lithium.EOCoeff.r33;
import static photonics.lithium.EpsilonLN.eps11;
import static photonics.lithium.EpsilonLN.eps22;
import static photonics.lithium.EpsilonLN.eps33;

import mathLib.func.ArrayFunc;
import mathLib.func.intf.RealFunction2D;
import mathLib.integral.Integral1D;
import mathLib.integral.intf.IntegralFunction1D;
import mathLib.plot.ColorMapPlot;
import mathLib.plot.MatlabChart;
import mathLib.plot.util.MeshGrid;
import mathLib.util.MathUtils;

public class RingFillingFactor {

	private double getDneff(Crystal cut, double phi) {
		RotatedDCField rotation = new RotatedDCField() ;

		double factor = 0.0 ;
		switch (cut) {
			case Xcut:
				factor = cos(phi)*cos(phi)*cos(phi) + (eps22*eps22)/(eps33*eps33)*(r22/r33 * sin(phi) + r23/r33*cos(phi))*sin(phi)*sin(phi) ;
//				factor = cos(phi)*cos(phi)*cos(phi) + (eps22*eps22)/(eps33*eps33)*(r22/r33 * sin(phi) + r23/r33*cos(phi))*sin(phi)*sin(phi) +
//							+2*sin(phi)*sin(phi)*cos(phi)*eps22/eps33*r42/r33	;
//				factor = cos(phi)*cos(phi)*cos(phi) ;
				break;
			case Ycut:
				factor = cos(phi)*cos(phi)*cos(phi) + (eps11*eps11)/(eps33*eps33)*(r13/r33*cos(phi))*sin(phi)*sin(phi) ;
				break;
			case Zcut:
				factor = 1.0 ;
				break;
			default:
				break;
		}
		return factor * rotation.getRotationFactor(phi) ;
//		return factor ;
	}

	private double getFF(Crystal cut, double phi0, double phi1) {
		IntegralFunction1D dneff = phi -> getDneff(cut, phi) ;
		Integral1D integral = new Integral1D(dneff, phi0, phi1) ;
		return 1.0/(2*PI)*integral.getIntegral() ;
	}

	public void plotFF(Crystal cut) {
		double[] phi = MathUtils.linspace(0, PI, 500) ;
		double[] ff = ArrayFunc.apply(t -> getFF(cut, -t, t), phi) ;
		MatlabChart fig = new MatlabChart() ;
		fig.plot(ArrayFunc.apply(x -> 180.0/PI*x, phi), ff, "b", 1f, cut.name());
		fig.renderPlot();
//		fig.legendON();
		fig.run(true);
	}

	public void plotContours(Crystal cut) {
		double[] phi0 = MathUtils.linspace(-PI, 0, 500) ;
		double[] phi1 = MathUtils.linspace(0, PI, 500) ;
		MeshGrid meshGrid = new MeshGrid(phi0, phi1) ;
		RealFunction2D func = (u, v) -> getFF(cut, u, v) ;
		ColorMapPlot fig = new ColorMapPlot(meshGrid, ArrayFunc.apply(func, meshGrid)) ;
		fig.run(true);
	}

	public static void main(String[] args) {
		RingFillingFactor rff = new RingFillingFactor() ;
//		rff.plotContours(Xcut);
		rff.plotFF(Xcut);
//		rff.plotFF(Ycut);

//		double[] x = MathUtils.linspace(0, 2*PI, 100) ;
//		double[] y = ArrayFunc.apply(t -> rff.getFF(Ycut, -t, 0), x) ;
//		MatlabChart fig = new MatlabChart() ;
//		fig.plot(ArrayFunc.apply(t -> 180.0/PI*t, x), y);
//		fig.renderPlot();
//		fig.run(true);

//		double[] x = MathUtils.linspace(0, PI, 100) ;
//		double[] y = ArrayFunc.apply(t -> rff.getFF(Xcut, -PI/2.0-t, -PI/2.0+t), x) ;
//		MatlabChart fig = new MatlabChart() ;
//		fig.plot(ArrayFunc.apply(t -> 180.0/PI*t, x), y);
//		fig.renderPlot();
//		fig.run(true);

//		System.out.println(rff.getFF(Xcut, -73.0*PI/180.0, 107*PI/180.0));

//		double[] x = MathUtils.linspace(0, 2*PI, 1000) ;
//		double[] y = ArrayFunc.apply(t -> rff.getDneff(Xcut, t), x) ;
////		double[] y = ArrayFunc.apply(t -> rff.getFF(Xcut, -PI/2.0+t, PI/2.0+t)*2, x) ;
////		double[] y = ArrayFunc.apply(t -> rff.getFF(Xcut, -PI/2.0-t, PI/2.0-t) - rff.getFF(Xcut, PI/2.0-t, 3*PI/2.0-t), x) ;
//		MatlabChart fig = new MatlabChart() ;
//		fig.plot(ArrayFunc.apply(t -> 180.0/PI*t, x), y);
//		fig.renderPlot();
//		fig.run(true);
//		fig.xlabel("Rotation Angle (deg)");
//		fig.ylabel("Filling Factor");

	}

}
