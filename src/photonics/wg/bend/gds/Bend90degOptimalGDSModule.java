package photonics.wg.bend.gds;

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
import flanagan.integration.IntegralFunction;
import mathLib.fitting.interpol.LinearInterpolation1D;
import mathLib.func.ArrayFunc;
import mathLib.func.intf.RealFunction;
import mathLib.integral.Integral1D;
import mathLib.plot.MatlabChart;
import mathLib.util.ArrayUtils;
import mathLib.util.MathUtils;

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

		double R0 = R ;

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
		fig3.run(systemExit);
//		fig3.markerON();

		//************* calculating the curvature
		double[] C = ArrayFunc.apply(t -> A*Math.pow(Math.cos(t), 1.0/b) , theta) ;
//		double[] R = ArrayFunc.apply(t -> 1/t, C) ;

		MatlabChart fig4 = new MatlabChart() ;

		fig4.plot(xtot, ArrayUtils.concat(C, C));
		fig4.renderPlot();
		fig4.xlabel("x (um)");
		fig4.ylabel("Curvature (1/um)");
//		fig4.markerON();
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
        	Rect wgOut = new Rect(R-width/2.0, R-6e-3, R+width/2.0, R+0.01, 1) ;

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
		Bend90degOptimalGDSModule bend = new Bend90degOptimalGDSModule(100, 2.49, 4) ;
		bend.setWidth(0.4);
		bend.createGDS(null, true);
	}

}
