package photonics.interconnect.tutorial;

import mathLib.numbers.Complex;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import photonics.interconnect.elements.passive.uni.AddDropRingUnidirectional;
import photonics.interconnect.modes.Neff450X220Strip;
import photonics.interconnect.solver.PhotonicCircuit;
import photonics.util.Wavelength;

public class Tutorial10 {
	
	public static void main(String[] args) {
		double radiusMicron = 10 ;
		double kappa = 0.2 ;
		double loss = 10 ; // dB per cm
		double[] lambdaNm = MathUtils.linspace(1540, 1560, 5000) ;
		Complex[] transfer = new Complex[lambdaNm.length] ; // thru port
		double[] transferThrudB = new double[lambdaNm.length] ; // thru port (dB)
		double[] transferDropdB = new double[lambdaNm.length] ; // drop port (dB)
		for(int i=0; i<lambdaNm.length; i++) {
			// set the wavelength
			Wavelength lambda = new Wavelength(lambdaNm[i]) ;
			// create photonics circuit
			PhotonicCircuit pc = new PhotonicCircuit() ;
			pc.setWavelength(lambda) ;
			// create photonics elements
			AddDropRingUnidirectional ring1 = new AddDropRingUnidirectional("ring1", new Neff450X220Strip(), radiusMicron, loss, kappa, kappa) ;
			// connect photonics elements
			pc.addElement(ring1);
			// find the transfer function
			transfer[i] = pc.getTransfer("ring1.port1", "ring1.port2") ;
			transferThrudB[i] = MathUtils.Conversions.todB(transfer[i].absSquared()) ;
			transferDropdB[i] = MathUtils.Conversions.todB(pc.getTransfer("ring1.port1", "ring1.port4").absSquared()) ;
			if(i==0)
				pc.printDetails();
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(lambdaNm, transferThrudB, "b", 2f, "Thru (dB)");
		fig.plot(lambdaNm, transferDropdB, "r", 2f, "Drop (dB)");
		fig.renderPlot();
		fig.xlabel("Wavelength (nm)");
		fig.ylabel("Thru (dB)");
		fig.legendON();
		fig.run(true);
	}

}
