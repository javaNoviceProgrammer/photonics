package photonics.wg.bend.gds;

import flanagan.integration.IntegralFunction;
import mathLib.func.ArrayFunc;
import mathLib.func.intf.RealFunction;
import mathLib.integral.Integral1D;
import mathLib.plot.MatlabChart;
import mathLib.util.ArrayUtils;
import mathLib.util.MathUtils;

public class Bend90degOptimalGDS2 {
	public static void main(String[] args) {
		double b = 2.49 ;
		double R0 = 5 ;

		//********************* solving for x0
		IntegralFunction funcX = new IntegralFunction() {
			@Override
			public double function(double theta) {
				return Math.pow(Math.cos(theta), 1.0-1/b) ;
			}
		};

		Integral1D integralX = new Integral1D(funcX, Math.PI/4.0, Math.PI/2.0) ;
		double factorX = integralX.getIntegral() ;
		System.out.println(factorX);

//		IntegralFunction funcY = new IntegralFunction() {
//			@Override
//			public double function(double theta) {
//				return Math.pow(Math.cos(theta), -1.0/b)*Math.sin(theta) ;
//			}
//		};
//
//		Integral1D integralY = new Integral1D(funcY, Math.PI/4.0, Math.PI/2.0) ;
//		double factorY = integralY.getIntegral() ;
//		System.out.println(factorY);

		double factorY = b/(b-1.0) * Math.pow(Math.sqrt(2.0), (1.0-b)/b) ;

		System.out.println(factorY);

		double x0 = R0*factorY/(factorX+factorY) ;
		System.out.println("x0 = " + x0);
		double A = factorY/x0 ;
		System.out.println("A = " + A);

		//************ now calculating the bend

		double[] theta = MathUtils.linspace(Math.PI/4.0, Math.PI/2.0, 500) ;
		double[] xx = ArrayFunc.apply(new RealFunction() {

			@Override
			public double evaluate(double arg) {
				Integral1D in = new Integral1D(s -> 1.0/A * funcX.function(s), Math.PI/4.0, arg) ;
				return x0 + in.getIntegral() ;
			}
		}, theta) ;

		double[] yy = ArrayFunc.apply(s -> R0-x0 + 1.0/A * (factorY- b/(b-1.0) * Math.pow(Math.cos(s), (b-1)/b)) , theta) ;


		MatlabChart fig1 = new MatlabChart() ;
		fig1.plot(xx, yy, "r");
		fig1.renderPlot();
		fig1.run(true);

		//************* finding the other half of y(x)
		double[] ytilde = ArrayFunc.apply(t -> R0 - t, xx) ;
		double[] xtilde = ArrayFunc.apply(t -> R0 - t, yy) ;

		double[] xtot = ArrayUtils.concat(xtilde, xx) ;
		double[] ytot = ArrayUtils.concat(ytilde, yy) ;

		MatlabChart fig3 = new MatlabChart() ;
		fig3.plot(xtot, ytot, "m");
		fig3.renderPlot();
		fig3.xlabel("X (um)");
		fig3.ylabel("Y (um)");
		fig3.run(true);
		fig3.markerON();

		//************* calculating the curvature
		double[] C = ArrayFunc.apply(t -> A*Math.pow(Math.cos(t), 1.0/b) , theta) ;
//		double[] R = ArrayFunc.apply(t -> 1/t, C) ;

		MatlabChart fig4 = new MatlabChart() ;

		fig4.plot(xtot, ArrayUtils.concat(C, C));
		fig4.renderPlot();
		fig4.xlabel("x (um)");
		fig4.ylabel("Curvature (1/um)");
		fig4.markerON();
		fig4.run(true);

		//************** create GDS file
//        try {
//            FileOutputStream fileOUT;
//            File f = new File("bend_optimal_"+R0+".gds");
//            fileOUT = new FileOutputStream(f);
//            DataOutputStream dO = new DataOutputStream(fileOUT);
//            GDSWriter g = new GDSWriter(dO);
//            Lib lib = new Lib();
//
//            double width = 0.4 ;
//
//            xtot[0] = 0.0 ;
//            xtot[xtot.length-1] = R0 ;
//            ytot[0] = 0.0 ;
//            ytot[ytot.length-1] = R0 ;
//
//            LinearInterpolation1D interpolate = new LinearInterpolation1D(xtot, ytot) ;
//            double[] xPoints = MathUtils.linspace(xtot[0], R0, 1000) ;
//            double[] yPoints = ArrayFunc.apply(t -> interpolate.interpolate(t), xPoints) ;
//            yPoints[0] = 0.0 ;
//            yPoints[yPoints.length-1] = R0 ;
//
//            Path2D.Double path = new Path2D.Double() ; // this is the path of the center
//        	path.moveTo(0, 0);
//        	for(int i=1; i<xPoints.length; i++)
//        		path.lineTo(xPoints[i], yPoints[i]);
//        	BasicStroke stroke = new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL) ;
//        	GArea area = new GArea(stroke.createStrokedShape(path), 1) ;
//
//        	Struct topCell = new Struct("top") ;
//        	Rect wgIn = new Rect(-10, -width/2.0, 2e-3, width/2.0, 1) ;
//        	Rect wgOut = new Rect(R0-width/2.0, R0-2e-3, R0+width/2.0, R0+10, 1) ;
//
//        	topCell.add(area);
//        	topCell.add(wgIn);
//        	topCell.add(wgOut);
//
//            Ref ref = new Ref(topCell, 0, 0, 0, 0) ;
//            lib.add(ref);
//
//            lib.GDSOut(g);
//            System.out.println(" Saved to " + f.getAbsolutePath());
//        } catch (IOException eOutput) {
//            eOutput.printStackTrace();
//        }
//        System.out.println("done");
	}
}
