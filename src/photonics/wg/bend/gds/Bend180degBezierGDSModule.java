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
import mathLib.func.ArrayFunc;
import mathLib.plot.MatlabChart;
import mathLib.util.ArrayUtils.FindMinimum;
import mathLib.util.MathUtils;
import photonics.wg.bend.BendLossCalculate;
import photonics.wg.bend.BezierCurve180;
import photonics.wg.bend.LossModel;

public class Bend180degBezierGDSModule {

	double a, b, R ;
	double width = 0.4 ; // default

	public Bend180degBezierGDSModule(
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
//		double R = 10 ; // in micron
		// sweep B to find minimum loss
		double[] B = MathUtils.linspace(0.5*R, 3*R, 1000) ;
		double[] lossdB = new double[B.length] ;

		LossModel model = new LossModel(a, b, 0.0) ;

		for(int i=0; i<B.length; i++) {
			BezierCurve180 bezier = new BezierCurve180(R, B[i]) ;
			BendLossCalculate lossCalc = new BendLossCalculate(model, bezier) ;
			lossdB[i] = lossCalc.getLossDB(0.0, 1.0) ;
		}

//		MatlabChart fig0 = new MatlabChart() ;
//		fig0.plot(B, lossdB);
//		fig0.renderPlot();
//		fig0.show(systemExit);

		System.out.println("Min Loss (dB) = " + FindMinimum.getValue(lossdB));

		int minIndex = FindMinimum.getIndex(lossdB) ;
		System.out.println("B = " + B[minIndex]);

		double Bopt = B[minIndex] ;
		BezierCurve180 optBend = new BezierCurve180(R, Bopt) ;
		double[] t = MathUtils.linspace(0.0, 1.0, 1000) ;
		double[] x = ArrayFunc.apply(s -> optBend.getX(s) , t) ;
		double[] y = ArrayFunc.apply(s -> optBend.getY(s), t) ;
		double[] curvature = ArrayFunc.apply(s -> 1.0/optBend.getRadiusOfCurvature(s), t) ;
		double[] length = ArrayFunc.apply(s -> optBend.getLength(0, s), t) ;

		MatlabChart fig = new MatlabChart() ;
		fig.plot(x, y);
		fig.renderPlot();
		fig.xlabel("X (um)");
		fig.ylabel("Y (um)");
		fig.markerON();
		fig.setFigLineWidth(0, 0f);
		fig.run(systemExit);

		MatlabChart fig1 = new MatlabChart() ;
		fig1.plot(y, curvature, "r");
		fig1.renderPlot();
		fig1.xlabel("Y (um)");
		fig1.ylabel("Curvature (1/um)");
		fig1.run(systemExit);

		MatlabChart fig2 = new MatlabChart() ;
		fig2.plot(length, curvature, "g");
		fig2.renderPlot();
		fig2.xlabel("S (um)");
		fig2.ylabel("Curvature (1/um");
		fig2.run(systemExit);

		//************** create GDS file
        try {
            FileOutputStream fileOUT;
            File f ;
            if(filePath == null){
            	f = new File("bend180_bezier_"+R+".gds");
            }
            else{
            	f = new File(filePath + File.separator + "bend180_bezier_"+R+".gds");
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

        	Struct topCell = new Struct("top") ;
        	Rect wgIn = new Rect(-0.01, -width/2.0, 2e-3, width/2.0, 1) ;
        	Rect wgOut = new Rect(-0.01, -width/2.0+2*R, 2e-3, 2*R+width/2.0, 1) ;

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
		Bend180degBezierGDSModule bend = new Bend180degBezierGDSModule(100, 2.49, 5) ;
		bend.createGDS(null, true);
	}

}
