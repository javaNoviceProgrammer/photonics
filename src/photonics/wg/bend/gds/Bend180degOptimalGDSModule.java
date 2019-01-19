package photonics.wg.bend.gds;

import static java.lang.Math.sqrt;
import static mathLib.func.GammaFunc.gamma;

import java.awt.BasicStroke;
import java.awt.geom.Path2D;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import JGDS2.GArea;
import JGDS2.GDSWriter;
import JGDS2.Lib;
import JGDS2.Rect;
import JGDS2.Ref;
import JGDS2.Struct;
import flanagan.integration.RungeKutta;
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootFunction;
import mathLib.fitting.interpol.LinearInterpolation1D;
import mathLib.func.ArrayFunc;
import mathLib.ode.intf.DerivnFunction1D;
import mathLib.plot.MatlabChart;
import mathLib.util.ArrayUtils;
import mathLib.util.MathUtils;

public class Bend180degOptimalGDSModule {

	double a, b, R ;

	public Bend180degOptimalGDSModule(
			double a,
			double b,
			double R
			) {
		this.a = a ;
		this.b = b ;
		this.R = R ;
	}

	public void createGDS(String filePath, boolean systemExit){

		double xi = (3.0*b-1.0)/(2.0*b) ;
		double R0 = R ;
		double a1 = sqrt(Math.PI)/2.0 * gamma(xi-0.5)/gamma(xi) ;
		double a2 = 0 ;

		//********************* solving for d

		RealRootFunction funcd0 = new RealRootFunction() {

			@Override
			public double function(double d) {
				double A = (a1-a2)/(R0) ;

				RungeKutta rk = new RungeKutta() ;
				DerivnFunction1D func = new DerivnFunction1D() {

					@Override
					public double[] derivn(double x, double[] yy) {
						// z = y'
//						double y = yy[0] ;
						double z = yy[1] ;
						double yprime = z ;
						double zprime = A * Math.pow(1+z*z, xi) ;
						return new double[] {yprime, zprime};
					}
				};

				rk.setStepSize(1e-4);
				rk.setInitialValueOfX(0);
				rk.setFinalValueOfX(R0);
				rk.setInitialValueOfY(new double[] {d, 0});
				double[] yz = rk.fourthOrder(func) ;
				return yz[0] ;
			}
		};

		RealRoot root = new RealRoot() ;
		double d = root.bisect(funcd0, -5*R0, 0) ;
		System.out.println("d = " + d);


		//************ now calculating the bend

		double A = (a1-a2)/(R0) ;
		System.out.println("A = " + A);
		double[] xx = MathUtils.linspace(0, R0*0.995, 100) ;
		xx = ArrayUtils.concat(xx, MathUtils.linspace(0.995*R0, R0, 200)) ;
		double[] yy = new double[xx.length] ;
		double[] yyprime = new double[xx.length] ;

		for(int i=0; i<xx.length; i++) {

			RungeKutta rk = new RungeKutta() ;
			DerivnFunction1D func = new DerivnFunction1D() {

				@Override
				public double[] derivn(double x, double[] yy) {
					// z = y'
//					double y = yy[0] ;
					double z = yy[1] ;
					double yprime = z ;
					double zprime = A * Math.pow(1+z*z, xi) ;
					return new double[] {yprime, zprime};
				}
			};

			rk.setStepSize(1e-4);
			rk.setInitialValueOfX(0);
			rk.setFinalValueOfX(xx[i]);
			rk.setInitialValueOfY(new double[] {d, 0});
			double[] yz = rk.fourthOrder(func) ;
			yy[i] = yz[0] ;
			yyprime[i] = yz[1] ;
		}

		//************* finding the other half of y(x)
		double[] ytilde = ArrayFunc.apply(t -> t, yy) ;
		double[] xtilde = ArrayFunc.apply(t -> - t, xx) ;

		double[] xtot = ArrayUtils.concat(xtilde, xx) ;
		double[] ytot = ArrayUtils.concat(ytilde, yy) ;

		MatlabChart fig3 = new MatlabChart() ;
		fig3.plot(xtot, ytot, "b");
		fig3.renderPlot();
		fig3.run(true);
		fig3.markerON();

		//************* calculating the curvature
		double[] C = ArrayFunc.apply(t -> A/Math.pow(1+t*t, 1/(2*b)), yyprime) ;
//		double[] R = ArrayFunc.apply(t -> 1/t, C) ;

		double[] Ctot = ArrayUtils.concat(C, C) ;

		MatlabChart fig4 = new MatlabChart() ;
		fig4.plot(xtot, Ctot, "r");
		fig4.renderPlot();
		fig4.run(true);

		//************** create GDS file
        try {
            FileOutputStream fileOUT;
            File f ;
            if(filePath == null){
            	f = new File("bend180_optimal_"+R+".gds");
            }
            else{
            	f = new File(filePath + File.separator + "bend180_optimal_"+R+".gds");
            }
            fileOUT = new FileOutputStream(f);
            DataOutputStream dO = new DataOutputStream(fileOUT);
            GDSWriter g = new GDSWriter(dO);
            Lib lib = new Lib();

            double width = 0.4 ;

            LinearInterpolation1D interpolate = new LinearInterpolation1D(xtot, ytot) ;
            double[] xPoints = MathUtils.linspace(-R0, R0, 1000) ;
            double[] yPoints = ArrayFunc.apply(t -> interpolate.interpolate(t), xPoints) ;

            Path2D.Double path = new Path2D.Double() ; // this is the path of the center
        	path.moveTo(-R, 0);
        	for(int i=1; i<xPoints.length; i++)
        		path.lineTo(xPoints[i], yPoints[i]);
        	BasicStroke stroke = new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL) ;
        	GArea area = new GArea(stroke.createStrokedShape(path), 1) ;

        	Struct topCell = new Struct("top") ;
//        	Rect wgIn = new Rect(-0.01, -width/2.0, 2e-3, width/2.0, 1) ;
//        	Rect wgOut = new Rect(-0.01, -width/2.0+2*R, 2e-3, 2*R+width/2.0, 1) ;

//        	area.or(wgIn).or(wgOut) ;

        	topCell.add(area);
//        	topCell.add(wgIn);
//        	topCell.add(wgOut);

            Ref ref = new Ref(topCell, 0, 0, 0, 0) ;
            lib.add(ref);

            lib.GDSOut(g);
            System.out.println(" Saved to " + f.getAbsolutePath());
        } catch (IOException eOutput) {
            eOutput.printStackTrace();
        }
        System.out.println("done");
	}

	public static void main(String[] args) {
		Bend180degOptimalGDSModule bend = new Bend180degOptimalGDSModule(100, 2.49, 5) ;
		bend.createGDS(null, true);
	}

}
