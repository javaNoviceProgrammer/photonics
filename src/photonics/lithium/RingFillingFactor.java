package photonics.lithium;

import static photonics.lithium.EOCoeff.*;
import static photonics.lithium.EpsilonLN.*;

import mathLib.func.ArrayFunc;
import mathLib.func.intf.RealFunction2D;
import mathLib.integral.Integral1D;
import mathLib.integral.intf.IntegralFunction1D;
import mathLib.plot.ColorMapPlot;
import mathLib.plot.MatlabChart;
import mathLib.plot.util.MeshGrid;
import mathLib.util.MathUtils;

import static java.lang.Math.*;

public class RingFillingFactor {

	private double getDneff(Crystal cut, double phi) {
		double factor = 0.0 ;
		switch (cut) {
			case Xcut:
				factor = cos(phi)*cos(phi)*cos(phi) + (eps22*eps22)/(eps33*eps33)*(r22/r33 * sin(phi) + r23/r33*cos(phi))*sin(phi)*sin(phi) ;
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
		return factor ;
	}
	
	private double getFF(Crystal cut, double phi0, double phi1) {
		IntegralFunction1D dneff = phi -> getDneff(cut, phi) ;
		Integral1D integral = new Integral1D(dneff, phi0, phi1) ;
		return 1.0/(2*PI)*integral.getIntegral() ;
	}
	
	private void plotFF(Crystal cut) {
		double[] phi = MathUtils.linspace(0, PI, 500) ;
		double[] ff = ArrayFunc.apply(t -> getFF(cut, -t, t), phi) ;
		MatlabChart fig = new MatlabChart() ;
		fig.plot(ArrayFunc.apply(x -> 180.0/PI*x, phi), ff);
		fig.renderPlot();
		fig.run(true);
	}
	
	
	private void plotContours(Crystal cut) {
		double[] phi0 = MathUtils.linspace(-PI, 0, 500) ;
		double[] phi1 = MathUtils.linspace(0, PI, 500) ;
		MeshGrid meshGrid = new MeshGrid(phi0, phi1) ;
		RealFunction2D func = (u, v) -> getFF(cut, u, v) ;
		ColorMapPlot fig = new ColorMapPlot(meshGrid, ArrayFunc.apply(func, meshGrid)) ;
		fig.run(true);
	}
	
	public static void main(String[] args) {
		RingFillingFactor rff = new RingFillingFactor() ;
//		rff.plotContours(Crystal.Xcut);
		rff.plotFF(Crystal.Xcut);
//		rff.plotFF(Crystal.Ycut);
	}

}
