package photonics.interconnect.tutorial;

import static java.lang.Math.PI;

import mathLib.numbers.Complex;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import photonics.interconnect.elements.passive.CompactCoupler;
import photonics.interconnect.elements.passive.StraightWg;
import photonics.interconnect.modes.Neff450X220Strip;
import photonics.interconnect.solver.PhotonicCircuit;
import photonics.util.Wavelength;

public class Tutorial03 {

	/*
	 * Simulating an all-pass ring resonator
	 */

	public static void main(String[] args) {
		double radiusMicron = 10 ;
		double kappa = 0.2 ;
		double[] lambdaNm = MathUtils.linspace(1540, 1560, 5000) ;
		Complex[] transfer = new Complex[lambdaNm.length] ; // thru port
		double[] transferdB = new double[lambdaNm.length] ; // thru port (dB)
		for(int i=0; i<lambdaNm.length; i++) {
			// set the wavelength
			Wavelength lambda = new Wavelength(lambdaNm[i]) ;
			// create photonics circuit
			PhotonicCircuit pc = new PhotonicCircuit() ;
			pc.setWavelength(lambda) ;
			// create photonics elements
			StraightWg cwg1 = new StraightWg("cwg1", new Neff450X220Strip(), 10, 2*PI*radiusMicron) ;
			CompactCoupler dc1 = new CompactCoupler("dc1", kappa) ;
			// connect photonics elements
			pc.addElement(cwg1);
			pc.addElement(dc1);
			pc.connectPorts("dc1.port3", "cwg1.port2");
			pc.connectPorts("dc1.port4", "cwg1.port1");
			// find the transfer function
			transfer[i] = pc.getTransfer("dc1.port1", "dc1.port2") ;
			transferdB[i] = MathUtils.Conversions.todB(transfer[i].absSquared()) ;
			
			if(i==0) {
				pc.printDetails();
			}
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(lambdaNm, transferdB);
		fig.renderPlot();
		fig.xlabel("Wavelength (nm)");
		fig.ylabel("Thru (dB)");
		fig.run(true);

	}
}
