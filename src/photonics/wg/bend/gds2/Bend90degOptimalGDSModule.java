package photonics.wg.bend.gds2;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

import java.awt.BasicStroke;
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
import flanagan.integration.IntegralFunction;
import mathLib.func.ArrayFunc;
import mathLib.integral.Integral1D;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;

public class Bend90degOptimalGDSModule {

	double a, b, R ;
	double width = 0.4 ; // default
	int numPoints = 500 ;
	double A = 0 ;

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

	public void setNumPoints(int n) {
		this.numPoints = n ;
	}

	public double getAuxiliaryC(double theta) {
		if(PI/4.0 <= theta && theta <= PI/2.0)
			return pow(cos(theta), 1.0/b) ;
		else
			return getAuxiliaryC(PI/2.0-theta) ;
	}

	public double getCurvature(double theta) {
		if(A == 0.0) {
			IntegralFunction func = var -> cos(var)/getAuxiliaryC(var) ;
			Integral1D integral = new Integral1D(func, 0, PI/2.0) ;
			A = 1.0/R * integral.getIntegral() ;
		}

		if(PI/4.0 <= theta && theta <= PI/2.0)
			return A*getAuxiliaryC(theta) ;
		else
			return getCurvature(PI/2.0-theta) ;
	}

	public double getX(double theta) {
		IntegralFunction func = var -> cos(var)/getCurvature(var) ;
		Integral1D integral = new Integral1D(func, 0, theta) ;
		return integral.getIntegral() ;
	}

	public double getY(double theta) {
		IntegralFunction func = var -> sin(var)/getCurvature(var) ;
		Integral1D integral = new Integral1D(func, 0, theta) ;
		return integral.getIntegral() ;
	}

	public double getS(double theta) {
		IntegralFunction func = var -> 1.0/getCurvature(var) ;
		Integral1D integral = new Integral1D(func, 0, theta) ;
		return integral.getIntegral() ;
	}

	public void createGDS(String filePath, boolean systemExit){

		double[] theta = MathUtils.linspace(0.0, PI/2.0, numPoints) ;

		IntegralFunction funcX = var -> cos(var)/getCurvature(var) ;
		IntegralFunction funcY = var -> sin(var)/getCurvature(var) ;
		IntegralFunction funcS = var -> 1.0/getCurvature(var) ;

		double[] x = ArrayFunc.apply(t -> new Integral1D(funcX, 0.0, t).getIntegral(), theta) ;
		double[] y = ArrayFunc.apply(t -> new Integral1D(funcY, 0.0, t).getIntegral(), theta) ;
		double[] length = ArrayFunc.apply(t -> new Integral1D(funcS, 0.0, t).getIntegral(), theta) ;
		double[] curvature = ArrayFunc.apply(s -> getCurvature(s), theta) ;

		MatlabChart fig = new MatlabChart() ;
		fig.plot(x, y);
		fig.renderPlot();
		fig.xlabel("X (um)");
		fig.ylabel("Y (um)");
		fig.run(systemExit);

		MatlabChart fig1 = new MatlabChart() ;
		fig1.plot(x, curvature, "r");
		fig1.renderPlot();
		fig1.xlabel("x (um)");
		fig1.ylabel("Curvature (1/um)");
		fig1.run(systemExit);

		MatlabChart fig2 = new MatlabChart() ;
		fig2.plot(length, curvature, "g");
		fig2.renderPlot();
		fig2.xlabel("S (um)");
		fig2.ylabel("Curvature (1/um)");
		fig2.run(systemExit);

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

            double[] xPoints = x ;
            double[] yPoints = y ;

            Path2D.Double path = new Path2D.Double() ; // this is the path of the center
        	path.moveTo(0, 0);
        	for(int i=1; i<xPoints.length; i++)
        		path.lineTo(xPoints[i], yPoints[i]);
        	BasicStroke stroke = new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL) ;
        	GArea area = new GArea(stroke.createStrokedShape(path), 1) ;

        	Struct topCell = new Struct("bend90_optimal_"+R) ;
        	topCell.add(area);
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
		Bend90degOptimalGDSModule bend = new Bend90degOptimalGDSModule(100, 2.49, 5) ;
		bend.setWidth(0.4);
		bend.setNumPoints(100);
		bend.createGDS(null, true);
	}

}
