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
import photonics.wg.bend.nature.SpecialFunc;

public class Bend90degOptimalGDSModule {

	double a, b, R ;
	double width = 0.4 ; // default

	public Bend90degOptimalGDSModule(
			double a,
			double b,
			double R
			) {
		this.a = a ;
		this.b = b ;
		this.R = R ;
	}
	
	public void setWidth(double width) {
		this.width = width ;
	}

	public void createGDS(String filePath, boolean systemExit){

		double xi = (3.0*b-1.0)/(2.0*b) ;
		double R0 = R ;
		double a1 = sqrt(Math.PI)/2.0 * gamma(xi-0.5)/gamma(xi) ;
		SpecialFunc specialFunc = new SpecialFunc() ;
		double a2 = specialFunc.getValueAtMinusOne(b) ;

		//********************* solving for x0

		RealRootFunction funcX0 = new RealRootFunction() {

			@Override
			public double function(double x) {
				double A = (a1-a2)/(R0 - x) ;

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
				rk.setInitialValueOfX(x);
				rk.setFinalValueOfX(R0);
				rk.setInitialValueOfY(new double[] {R0-x, 1});
				double[] yz = rk.fourthOrder(func) ;
				return yz[0]-R0 ;
			}
		};

		RealRoot root = new RealRoot() ;
		double x0 = root.bisect(funcX0, 0, R0) ;
		System.out.println("x0 = " + x0);


		//************ now calculating the bend

		double A = (a1-a2)/(R0 - x0) ;
		System.out.println("A = " + A);
		double[] xx = MathUtils.linspace(x0, R0*0.999, 1000) ;
		xx = ArrayUtils.concat(xx, MathUtils.linspace(0.999*R0, R0, 1000)) ;
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
			rk.setInitialValueOfX(x0);
			rk.setFinalValueOfX(xx[i]);
			rk.setInitialValueOfY(new double[] {R0-x0, 1});
			double[] yz = rk.fourthOrder(func) ;
			yy[i] = yz[0] ;
			yyprime[i] = yz[1] ;
		}

		//************* finding the total loss
		double lossdB = a*Math.pow(A, b) * 2*(R0-x0)*1e-4 ;
		System.out.println("Loss (dB) = " + lossdB);

		//************* finding the other half of y(x)
		double[] ytilde = ArrayFunc.apply(t -> R0 - t, xx) ;
		double[] xtilde = ArrayFunc.apply(t -> R0 - t, yy) ;

		double[] xtot = ArrayUtils.concat(xtilde, xx) ;
		double[] ytot = ArrayUtils.concat(ytilde, yy) ;

		MatlabChart fig3 = new MatlabChart() ;
		fig3.plot(xtot, ytot, "b");
		fig3.renderPlot();
		fig3.xlabel("X (um)");
		fig3.ylabel("Y (um)");
		fig3.run(systemExit);

		//************* calculating the curvature
		double[] C = ArrayFunc.apply(t -> A/Math.pow(1+t*t, 1/(2*b)), yyprime) ;

		MatlabChart fig4 = new MatlabChart() ;
		fig4.plot(xtot, ArrayUtils.concat(C, C), "r");
		fig4.renderPlot();
		fig4.xlabel("x (um)");
		fig4.ylabel("Curvature (1/um)");
		fig4.run(systemExit);

		//************** create GDS file
        try {
            FileOutputStream fileOUT;
            File f ;
            if(filePath == null){
            	f = new File("bend90_optimal_"+R+".gds");
            }
            else{
            	f = new File(filePath + File.separator + "bend90_optimal_"+R+".gds");
            }
            fileOUT = new FileOutputStream(f);
            DataOutputStream dO = new DataOutputStream(fileOUT);
            GDSWriter g = new GDSWriter(dO);
            Lib lib = new Lib();

//            double width = 0.4 ;

            xtot[0] = 0.0 ;
            xtot[xtot.length-1] = R0 ;
            ytot[0] = 0.0 ;
            ytot[ytot.length-1] = R0 ;

            LinearInterpolation1D interpolate = new LinearInterpolation1D(xtot, ytot) ;
            double[] xPoints = MathUtils.linspace(xtot[0], R0, 1000) ;
            double[] yPoints = ArrayFunc.apply(t -> interpolate.interpolate(t), xPoints) ;
            yPoints[0] = 0.0 ;
            yPoints[yPoints.length-1] = R0 ;

            Path2D.Double path = new Path2D.Double() ; // this is the path of the center
        	path.moveTo(0, 0);
        	for(int i=1; i<xPoints.length; i++)
        		path.lineTo(xPoints[i], yPoints[i]);
        	BasicStroke stroke = new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL) ;
        	GArea area = new GArea(stroke.createStrokedShape(path), 1) ;

        	Struct topCell = new Struct("bend90_optimal_"+R) ;
        	Rect wgIn = new Rect(-0.01, -width/2.0, 2e-3, width/2.0, 1) ;
        	Rect wgOut = new Rect(R-width/2.0, R-3e-3, R+width/2.0, R+0.01, 1) ;

        	area.or(wgIn).or(wgOut) ;

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
		Bend90degOptimalGDSModule bend = new Bend90degOptimalGDSModule(100, 2.49, 10) ;
		bend.setWidth(0.45);
		bend.createGDS(null, true);
	}

}
