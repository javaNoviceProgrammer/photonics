package photonics.wg.bend.gds;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

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
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootFunction;
import mathLib.fitting.interpol.LinearInterpolation1D;
import mathLib.func.ArrayFunc;
import mathLib.integral.Integral1D;
import mathLib.plot.MatlabChart;
import mathLib.util.ArrayUtils;
import mathLib.util.MathUtils;
import photonics.wg.bend.nature.LossModel;

public class Bend90degClothoidGDSModule {

	double a, b, R ;

	public Bend90degClothoidGDSModule(
			double a,
			double b,
			double R
			) {
		this.a = a ;
		this.b = b ;
		this.R = R ;
	}

	public void createGDS(String filePath, boolean systemExit){

		LossModel lossModel = new LossModel(a, b, 0.0) ;
		double R0 = R ;
		double r = 1.0/0.7 ;
		double theta = PI/2.0 * r/(1+2*r) ;

		//******** solve for sEnd & beta
		RealRootFunction funcSend = new RealRootFunction() {
			@Override
			public double function(double s) {
				double beta = sqrt((PI/4.0)/(1+2.0*r))/s ;
				double Rmin = s * 2.0/PI * (1+2*r) ;
				Integral1D xIntegral = new Integral1D(t -> cos(beta*beta*t*t), 0, s) ;
				double xEnd = xIntegral.getIntegral() ;
				Integral1D yIntegral = new Integral1D(t -> sin(beta*beta*t*t), 0, s) ;
				double yEnd = yIntegral.getIntegral() ;
				double equation = (xEnd + yEnd) - (R0 - sqrt(2.0)*Rmin*sin(theta)) ;
				return equation;
			}
		};

		RealRoot rootSend = new RealRoot() ;
		double sEnd = rootSend.bisect(funcSend, 1e-2, 2*R0) ;
		System.out.println("sEnd = " + sEnd);

		double beta = sqrt((PI/4.0)/(1+2.0*r))/sEnd ;
		double Rmin = sEnd * 2.0/PI * (1+2*r) ;

		//******* finding the loss
		double a = lossModel.getA() ;
		double b = lossModel.getB() ;
		double lossdB = 2*a*pow(2*beta*beta, b)*pow(sEnd, b+1)*(1.0/(b+1.0) + r) * 1e-4 ;
		System.out.println("loss (dB) = " + lossdB);

		//******* finding the bend
		double[] s1 = MathUtils.linspace(0.0, sEnd, 100) ;
		double[] x1 = ArrayFunc.apply(z -> (new Integral1D(t -> cos(beta*beta*t*t), 0, z)).getIntegral(), s1) ;
		double[] y1 = ArrayFunc.apply(z -> (new Integral1D(t -> sin(beta*beta*t*t), 0, z)).getIntegral(), s1) ;

		Integral1D xIntegral = new Integral1D(t -> cos(beta*beta*t*t), 0, sEnd) ;
		double xEnd = xIntegral.getIntegral() ;
		System.out.println("xEnd = " + xEnd);
		Integral1D yIntegral = new Integral1D(t -> sin(beta*beta*t*t), 0, sEnd) ;
		double yEnd = yIntegral.getIntegral() ;
		System.out.println("yEnd = " + yEnd);

		double[] angle = MathUtils.linspace(-PI/4.0-theta, -PI/4, 20) ;
		double[] x2 = ArrayFunc.apply(t -> xEnd + Rmin*(cos(t)-cos(-PI/4.0-theta)), angle) ;
		double[] y2 = ArrayFunc.apply(t -> yEnd + Rmin*(sin(t)-sin(-PI/4.0-theta)), angle) ;

		double[] x = ArrayUtils.concat(x1, x2) ;
		double[] y = ArrayUtils.concat(y1, y2) ;

		double[] xtilde = ArrayFunc.apply(t -> R0-t, y) ;
		double[] ytilde = ArrayFunc.apply(t -> R0-t, x) ;
		double[] xx = ArrayUtils.concat(x, xtilde) ;
		double[] yy = ArrayUtils.concat(y, ytilde) ;

		MatlabChart fig = new MatlabChart() ;
		fig.plot(xx, yy);
		fig.renderPlot();
		fig.xlabel("X (um)");
		fig.ylabel("Y (um)");
		fig.run(systemExit);


		//***************** finding the curvature
//		CubicSpline interpolateY = new CubicSpline(xx, yy) ;
//		RealFunction yprime = t -> Richardson.deriv(z -> interpolateY.interpolate(z), t, 1e-5, 2) ;
//		RealFunction ydoubleprime = t -> Richardson.deriv2(z -> interpolateY.interpolate(z), t, 1e-5, 2) ;
//
//		RealFunction radius = t -> Math.pow(1+yprime.evaluate(t)*yprime.evaluate(t), 1.5)/Math.abs(ydoubleprime.evaluate(t)) ;
//
//		IntegralFunction funcLength = t -> Math.sqrt(1+yprime.evaluate(t)*yprime.evaluate(t)) ;
//		double[] xtot = MathUtils.linspace(1e-8, R0, 100) ;
//		double[] ss = ArrayFunc.apply(z -> (new Integral1D(funcLength, 1e-8, z)).getIntegral(), xtot) ;
//		double[] cc = ArrayFunc.apply(z -> 1/radius.evaluate(z), xtot) ;
//
//		MatlabChart fig2 = new MatlabChart() ;
//		fig2.plot(xtot, cc, "r");
//		fig2.renderPlot();
//		fig2.xlabel("X (um)");
//		fig2.ylabel("Curvature (1/um)");
//		fig2.run(systemExit);
//
//		MatlabChart fig3 = new MatlabChart() ;
//		fig3.plot(ss, cc, "k");
//		fig3.renderPlot();
//		fig3.xlabel("S (um)");
//		fig3.ylabel("Curvature (1/um)");
//		fig3.run(systemExit);

		//************** create GDS file
        try {
            FileOutputStream fileOUT;
            File f ;
            if(filePath == null){
            	f = new File("bend90_clothoid_"+R+".gds");
            }
            else{
            	f = new File(filePath + File.separator + "bend90_clothoid_"+R+".gds");
            }
            fileOUT = new FileOutputStream(f);
            DataOutputStream dO = new DataOutputStream(fileOUT);
            GDSWriter g = new GDSWriter(dO);
            Lib lib = new Lib();

            double width = 0.4 ;

            LinearInterpolation1D interpolate = new LinearInterpolation1D(xx, yy) ;
            double[] xPoints = MathUtils.linspace(0, R0, 1000) ;
            double[] yPoints = ArrayFunc.apply(q -> interpolate.interpolate(q), xPoints) ;

            Path2D.Double path = new Path2D.Double() ; // this is the path of the center
        	path.moveTo(0, 0);
        	for(int i=1; i<xPoints.length; i++)
        		path.lineTo(xPoints[i], yPoints[i]);
        	BasicStroke stroke = new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL) ;
        	GArea area = new GArea(stroke.createStrokedShape(path), 1) ;

        	Struct topCell = new Struct("top") ;
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
		Bend90degClothoidGDSModule bend = new Bend90degClothoidGDSModule(100, 2.49, 5) ;
		bend.createGDS(null, true);
	}
}
