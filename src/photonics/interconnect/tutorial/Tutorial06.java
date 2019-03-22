package photonics.interconnect.tutorial;

import static java.lang.Math.PI;
import static mathLib.numbers.Complex.j;
import static mathLib.numbers.ComplexMath.exp;

import mathLib.numbers.Complex;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import photonics.interconnect.elements.passive.Yjunction;
import photonics.interconnect.solver.PhotonicCircuit;
import photonics.util.Wavelength;

public class Tutorial06 {

	/*
	 * Simulating a Y-junction
	 */

	public static void main(String[] args) {
		double delta = 0 ;
		double[] phi = MathUtils.linspace(0, PI, 1000) ;
		double[] outPowerRatio = new double[phi.length] ; // thru port
		for(int i=0; i<phi.length; i++) {
			Wavelength lambda = new Wavelength(1550) ;
			PhotonicCircuit pc = new PhotonicCircuit() ;
			pc.setWavelength(lambda) ;
			Yjunction yjunc = new Yjunction("y1", delta) ;
			pc.addElement(yjunc);
			Complex t1 = pc.getTransfer("y1.port2", "y1.port1") ;
			Complex t2 = pc.getTransfer("y1.port3", "y1.port1") ;
			outPowerRatio[i] = (t1+t2*exp(-j*phi[i])).absSquared()/2.0 ;
			
			if(i==0)
				pc.printDetails();
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(phi, outPowerRatio, "b", 2f, "delta=0");
		fig.renderPlot();
		fig.xlabel("Phi (rad)");
		fig.ylabel("Power Ratio");
		fig.legendON();
		fig.run(true);

	}
}
