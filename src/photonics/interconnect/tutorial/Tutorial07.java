package photonics.interconnect.tutorial;

import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import photonics.interconnect.elements.passive.StraightWg;
import photonics.interconnect.elements.passive.Yjunction;
import photonics.interconnect.solver.PhotonicCircuit;
import photonics.interconnect.util.Neff450X220Strip;
import photonics.interconnect.util.Utils;
import photonics.util.Wavelength;

public class Tutorial07 {

	/*
	 * Simulating a Mach-Zehnder
	 */

	public static void main(String[] args) {
		double L1 = 100 ;
		double[] L2 = MathUtils.linspace(100, 110, 10000) ;
		double LambdaNm = 1550 ;
		double delta = 0 ;
		double[] transfer = new double[L2.length] ;
		double[] deltaL = new double[L2.length] ;
		for(int i=0; i<L2.length; i++) {
			Wavelength lambda = new Wavelength(LambdaNm) ;
			PhotonicCircuit pc = new PhotonicCircuit() ;
			pc.setWavelength(lambda) ;
			Yjunction y1 = new Yjunction("y1", delta) ;
			Yjunction y2 = new Yjunction("y2", delta) ;
			StraightWg wg1 = new StraightWg("wg1", new Neff450X220Strip(), 3, L1) ;
			StraightWg wg2 = new StraightWg("wg2", new Neff450X220Strip(), 3, L2[i]) ;
			pc.addElement(y1);
			pc.addElement(y2);
			pc.addElement(wg1);
			pc.addElement(wg2);
			pc.connectPorts("y1.port2", "wg1.port1");
			pc.connectPorts("y1.port3", "wg2.port1");
			pc.connectPorts("y2.port2", "wg1.port2");
			pc.connectPorts("y2.port3", "wg2.port2");
			transfer[i] = Utils.todB(pc.getTransfer("y1.port1", "y2.port1").absSquared()) ;
			deltaL[i] = L2[i]-L1 ;
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(deltaL, transfer);
		fig.renderPlot();
		fig.xlabel("Delta L (um)");
		fig.ylabel("Transfer (dB)");
		fig.run(true);
	}
}
