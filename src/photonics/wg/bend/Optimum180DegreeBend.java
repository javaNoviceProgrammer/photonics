package photonics.wg.bend;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import JGDS2.GArea;
import JGDS2.GDSWriter;
import JGDS2.Lib;
import JGDS2.Ref;
import JGDS2.Struct;
import flanagan.integration.DerivFunction;
import flanagan.integration.IntegralFunction;
import flanagan.integration.RungeKutta;
import flanagan.interpolation.LinearInterpolation;
import mathLib.fitting.interpol.LinearInterpolation1D;
import mathLib.func.ArrayFunc;
import mathLib.integral.Integral1D;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;

public class Optimum180DegreeBend {

	public static void main(String[] args){
		double R = 5 ; // in micron
		OptimalLossFunc lossFunc = new OptimalLossFunc() ;
		double b = 3 ;
		double p = (3*b-1)/(2*b) ;

		double A = lossFunc.getValue(b)/R ;

		DerivFunction dFunction = new DerivFunction() {
			public double deriv(double x, double f) {
				return A*Math.pow(1+f*f, p);
			}
		};
		RungeKutta integration = new RungeKutta() ;
		integration.setInitialValueOfX(0);
		integration.setInitialValueOfY(0);

		integration.setStepSize(1e-5*R);

		double[] x = MathUtils.Arrays.concat(MathUtils.linspace(0, 0.98*R, 200), MathUtils.linspace(0.98*R, R, 300))  ;
		double[] f = new double[x.length] ;
		for(int i=0; i<x.length; i++){
			integration.setFinalValueOfX(x[i]);
			f[i] = integration.fourthOrder(dFunction) ;
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(x, f);
		fig.renderPlot();
		fig.xlabel("x (um)");
		fig.ylabel("f(x)");
		fig.run(true);

		// now calculate y
		LinearInterpolation interpolation = new LinearInterpolation(x, f) ;
		IntegralFunction func = new IntegralFunction() {
			public double function(double x) {
				return interpolation.interpolate(x);
			}
		};

		double[] y = new double[x.length] ;

		for(int i=0; i<x.length ; i++){
			Integral1D yIntegral = new Integral1D(func, 0, x[i]) ;
			y[i] = yIntegral.getIntegral() ;
		}

		double[] xx = MathUtils.Arrays.concat(MathUtils.Arrays.times(x, -1), x) ;
		double[] yy = MathUtils.Arrays.concat(y, y) ;

		MatlabChart fig1= new MatlabChart() ;
		fig1.plot(xx, yy, "r");
		fig1.renderPlot();
		fig1.xlabel("x (um)");
		fig1.ylabel("y(x)");
		fig1.run(true);

		// find radius of curvature
		double[] radius = new double[x.length] ;
		for(int i=0; i<x.length; i++){
			radius[i] = 1/A * Math.pow(1+f[i]*f[i], 0.5/b) ;
		}

		MatlabChart fig2= new MatlabChart() ;
		fig2.plot(x, radius, "g");
		fig2.renderPlot();
		fig2.xlabel("x (um)");
		fig2.ylabel("radius of curvature (um)");
		fig2.run(true);
		
		LinearInterpolation1D interp = new LinearInterpolation1D(xx, yy) ;
		double[] xvals = MathUtils.linspace(-R, R, 500) ;
		double[] yvals = ArrayFunc.apply(t -> interp.interpolate(t), xvals) ;
		
		// create GDS file
        try {
            FileOutputStream fileOUT;
            File file = new File("bend180_optimal.gds");
            fileOUT = new FileOutputStream(file);
            DataOutputStream dO = new DataOutputStream(fileOUT);
            GDSWriter g = new GDSWriter(dO);
            Lib lib = new Lib();
            
            Struct topCell = new Struct("bend180_optimal") ;

            Path2D.Double path = new Path2D.Double() ;
            path.moveTo(xvals[0], yvals[0]);
            for(int i=1; i<xvals.length; i++) {
            	path.lineTo(xvals[i], yvals[i]);
            }

            double width = 0.5 ;
            Stroke bs = new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            GArea v = new GArea(bs.createStrokedShape(path), 1) ;
           
            topCell.add(v);
            
//            Path2D.Double path1 = new Path2D.Double() ;
//            path1.moveTo(-x[0], y[0]);
//            for(int i=1; i<x.length; i++) {
//            	path1.lineTo(-x[i], y[i]);
//            }
//            
//            Stroke bs1 = new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
//            GArea v1 = new GArea(bs1.createStrokedShape(path1), 1) ;
//            
//            topCell.add(v1);
            

            lib.add(new Ref(topCell, 0, 0));
            
            lib.GDSOut(g);
            
            fileOUT.close();
            dO.close();

            System.out.println(" Saved to " + file.getAbsolutePath());
        } catch (IOException eOutput) {
            eOutput.printStackTrace();
        }
        System.out.println("done");


	}

}
