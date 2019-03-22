package photonics.interconnect.tutorial;

import static java.lang.Math.PI;

import mathLib.numbers.Complex;
import mathLib.numbers.ComplexMath;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import photonics.interconnect.elements.passive.CompactCoupler;
import photonics.interconnect.elements.passive.Reflector;
import photonics.interconnect.elements.passive.StraightWg;
import photonics.interconnect.modes.Neff450X220Strip;
import photonics.interconnect.solver.PhotonicCircuit;
import photonics.util.Wavelength;

public class Tutorial17 {

	/*
	 * Simulating an all-pass ring resonator with back scattering
	 */

	public static void main(String[] args) {
		double radiusMicron = 5 ;
		double kappa = 0.1 ;
		Neff450X220Strip modeTE00 = new Neff450X220Strip() ;
		double[] lambdaNm = MathUtils.linspace(1541, 1547, 10000) ;
		Complex[] transfer = new Complex[lambdaNm.length] ; // thru port
		double[] transferdB = new double[lambdaNm.length] ; // thru port (dB)
		for(int i=0; i<lambdaNm.length; i++) {
			// set the wavelength
			Wavelength lambda = new Wavelength(lambdaNm[i]) ;
			// create photonics circuit
			PhotonicCircuit pc = new PhotonicCircuit() ;
			pc.setWavelength(lambda) ;
			// create photonics elements
			StraightWg cwg1 = new StraightWg("cwg1", modeTE00, 5, PI*radiusMicron) ;
			StraightWg cwg2 = new StraightWg("cwg2", modeTE00, 5, PI*radiusMicron) ;
			CompactCoupler dc1 = new CompactCoupler("dc1", kappa) ;
			CompactCoupler dc2 = new CompactCoupler("dc2", kappa) ;
			Reflector lr = new Reflector("lr", 0.01) ;
			// connect photonics elements
			pc.addElements(cwg1, cwg2, dc1, dc2, lr);
			pc.connectPorts("dc1.port3", "lr.port1");
			pc.connectPorts("lr.port2", "cwg1.port2");
			pc.connectPorts("cwg1.port1", "dc2.port3");
			pc.connectPorts("dc2.port4", "cwg2.port2");
			pc.connectPorts("cwg2.port1", "dc1.port4");
			// find the transfer function
			transfer[i] = pc.getTransfer("dc1.port1", "dc2.port1") ;
			transferdB[i] = MathUtils.Conversions.todB(ComplexMath.absSquared(transfer[i])) ;

			if(i==0) {
				pc.printDetails();
//				pc.saveToFile();
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
