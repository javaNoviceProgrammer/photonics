package photonics.wg.bend;

import static mathLib.func.symbolic.FMath.PI;
import static mathLib.func.symbolic.FMath.cos;
import static mathLib.func.symbolic.FMath.x;

import java.util.HashMap;

import mathLib.func.ArrayFunc;
import mathLib.func.symbolic.intf.MathFunc;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;

public class Sbend extends AbstractCurveModel {
	
	double H, V ;
	MathFunc xt, yt ;
	
	public Sbend(
			double H,
			double V
			) {
		this.H = H ;
		this.V = V ;
		this.xt = x*H ;
		this.yt = V/2.0 * (1-cos(PI * x)) ;
	}

	@Override
	public double getX(double t) {
		return xt.apply(t) ;
	}

	@Override
	public double getXPrime(double t) {
		return xt.diff("x").apply(t) ;
	}

	@Override
	public double getXDoublePrime(double t) {
		return xt.diff("x").diff("x").apply(t) ;
	}

	@Override
	public double getY(double t) {
		return yt.apply(t) ;
	}

	@Override
	public double getYPrime(double t) {
		return yt.diff("x").apply(t) ;
	}

	@Override
	public double getYDoublePrime(double t) {
		return yt.diff("x").diff("x").apply(t) ;
	}

	@Override
	public String getName() {
		return "Sbend" ;
	}

	@Override
	public HashMap<String, String> getAllParameters() {
		HashMap<String, String> map = new HashMap<>() ;
		return map ;
	}
	
	public static void main(String[] args) {
		Sbend bend = new Sbend(20, 20) ;
		double[] t = MathUtils.linspace(0, 1, 1000) ;
		double[] x = ArrayFunc.apply(s -> bend.getX(s), t) ;
		double[] y = ArrayFunc.apply(s -> bend.getY(s), t) ;
		
		MatlabChart fig = new MatlabChart() ;
		fig.plot(x, y);
		fig.renderPlot();
		fig.xlabel("X (um)");
		fig.ylabel("Y (um)");
		fig.run(true);
		
		double[] curvature = ArrayFunc.apply(s -> 1.0/bend.getRadiusOfCurvature(s), t) ;
		
		MatlabChart fig1 = new MatlabChart() ;
		fig1.plot(x, curvature, "r");
		fig1.renderPlot();
		fig1.xlabel("X (um)");
		fig1.ylabel("Curvature (1/um)");
		fig1.run(true);
	}

}
