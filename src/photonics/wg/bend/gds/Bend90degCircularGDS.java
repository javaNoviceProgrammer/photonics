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
import mathLib.util.MathUtils;

public class Bend90degCircularGDS {

	public static void main(String[] args){
		double R = 10 ; // in micron
		// sweep B to find minimum loss
		
		double[] t = MathUtils.linspace(-Math.PI/2.0, 0.0, 500) ;
		double[] x = ArrayFunc.apply(s -> R*Math.cos(s) , t) ;
		double[] y = ArrayFunc.apply(s -> R + R*Math.sin(s), t) ;
		double[] curvature = ArrayFunc.apply(s -> 1.0/R, t) ;
		double[] length = ArrayFunc.apply(s -> R*(s+Math.PI/2.0), t) ;

		MatlabChart fig = new MatlabChart() ;
		fig.plot(x, y);
		fig.renderPlot();
		fig.xlabel("X (um)");
		fig.ylabel("Y (um)");
		fig.run(true);

		MatlabChart fig1 = new MatlabChart() ;
		fig1.plot(x, curvature, "r");
		fig1.renderPlot();
		fig1.xlabel("x (um)");
		fig1.ylabel("Curvature (1/um)");
		fig1.run(true);
		
		MatlabChart fig2 = new MatlabChart() ;
		fig2.plot(length, curvature, "g");
		fig2.renderPlot();
		fig2.xlabel("S (um)");
		fig2.ylabel("Curvature (1/um)");
		fig2.run(true);
		
		//************** create GDS file
        try {
            FileOutputStream fileOUT;
            File f = new File("bend_circular_"+R+".gds");
            fileOUT = new FileOutputStream(f);
            DataOutputStream dO = new DataOutputStream(fileOUT);
            GDSWriter g = new GDSWriter(dO);
            Lib lib = new Lib();

            double width = 0.4 ;

            double[] xPoints = x ;
            double[] yPoints = y ;

            Path2D.Double path = new Path2D.Double() ; // this is the path of the center
        	path.moveTo(0, 0);
        	for(int i=1; i<xPoints.length; i++)
        		path.lineTo(xPoints[i], yPoints[i]);
        	BasicStroke stroke = new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL) ;
        	GArea area = new GArea(stroke.createStrokedShape(path), 1) ;

        	Struct topCell = new Struct("top") ;
//            Struct curvedWg = new Struct("bend", area) ;
            Rect wgIn = new Rect(-10, -width/2.0, 0, width/2.0, 1) ;
            Rect wgOut = new Rect(R-width/2.0, R, R+width/2.0, R+10, 1) ;
            
            topCell.add(area);
            topCell.add(wgIn);
            topCell.add(wgOut);

            Ref ref = new Ref(topCell, 0, 0, 0, 0) ;
            lib.add(ref);

            lib.GDSOut(g);
            System.out.println(" Saved to " + f.getAbsolutePath());
        } catch (IOException eOutput) {
            eOutput.printStackTrace();
        }
        System.out.println("done");
	}

}
