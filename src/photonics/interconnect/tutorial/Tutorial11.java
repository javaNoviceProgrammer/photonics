package photonics.interconnect.tutorial;

import mathLib.numbers.Complex;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import photonics.interconnect.elements.passive.AllPassRing;
import photonics.interconnect.modes.Neff450X220Strip;
import photonics.interconnect.solver.PhotonicCircuit;
import photonics.util.Wavelength;

public class Tutorial11 {
	
	public static void main(String[] args) {
		double radiusMicron = 10 ;
		double kappa = 0.2 ;
		double loss = 10 ; // dB per cm
		double[] lambdaNm = MathUtils.linspace(1540, 1560, 10000) ;
		Complex[] transfer = new Complex[lambdaNm.length] ; // thru port
		double[] transferdB = new double[lambdaNm.length] ; // thru port (dB)
		for(int i=0; i<lambdaNm.length; i++) {
			// set the wavelength
			Wavelength lambda = new Wavelength(lambdaNm[i]) ;
			// create photonics circuit
			PhotonicCircuit pc = new PhotonicCircuit() ;
			pc.setWavelength(lambda) ;
			// create photonics elements
			AllPassRing ring1 = new AllPassRing("ring1", new Neff450X220Strip(), radiusMicron, loss, kappa) ;
			// connect photonics elements
			pc.addElement(ring1);
			// find the transfer function
			transfer[i] = pc.getTransfer("ring1.port1", "ring1.port2") ;
			transferdB[i] = MathUtils.Conversions.todB(transfer[i].absSquared()) ;
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(lambdaNm, transferdB);
		fig.renderPlot();
		fig.xlabel("Wavelength (nm)");
		fig.ylabel("Thru (dB)");
		fig.run(true);
	}

}
