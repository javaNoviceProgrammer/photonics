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

public class Tutorial04 {

	/*
	 * Simulating an all-pass ring resonator with back scattering
	 */

	public static void main(String[] args) {
		double radiusMicron = 10 ;
		double kappa = 0.2 ;
		double[] lambdaNm = MathUtils.linspace(1543, 1544.5, 1000) ;
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
			Reflector lr1 = new Reflector("lr1", 0.03) ;
			// connect photonics elements
			pc.addElement(cwg1);
			pc.addElement(dc1);
			pc.addElement(lr1);
			pc.connectPorts("dc1.port3", "lr1.port1");
			pc.connectPorts("lr1.port2", "cwg1.port2");
			pc.connectPorts("dc1.port4", "cwg1.port1");
			// find the transfer function
			transfer[i] = pc.getTransfer("dc1.port1", "dc1.port2") ;
			transferdB[i] = MathUtils.Conversions.todB(ComplexMath.absSquared(transfer[i])) ;

			if(i==0) {
				System.out.println(pc.getCircuit().printForwardPaths_noGains());
				System.out.println(pc.getCircuit().printCofactors());
				System.out.println(pc.getCircuit().printAllLoops_compactForm());
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
