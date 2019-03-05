package photonics.interconnect.tutorial;

import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import photonics.interconnect.elements.passive.DirectionalCoupler;
import photonics.interconnect.modes.Neff450X220CoupledStrip;
import photonics.interconnect.solver.PhotonicCircuit;
import photonics.util.Wavelength;

public class Tutorial12 {
	
	public static void main(String[] args) {
		double lambdaNm = 1550 ; // nm
		double gap = 200e-3 ; // um
		double loss = 0 ; // dB per cm
		double[] L = MathUtils.linspace(0, 100, 1000) ;
		double[] thru = new double[L.length] ; 
		double[] cross = new double[L.length] ; 
		for(int i=0; i<L.length; i++) {
			Wavelength lambda = new Wavelength(lambdaNm) ;
			PhotonicCircuit pc = new PhotonicCircuit() ;
			pc.setWavelength(lambda) ;
			DirectionalCoupler dc1 = new DirectionalCoupler("dc1", new Neff450X220CoupledStrip(), L[i], gap, loss) ;
			pc.addElement(dc1);
			thru[i] = pc.getTransfer("dc1.port1", "dc1.port2").absSquared() ;
			cross[i] = pc.getTransfer("dc1.port1", "dc1.port3").absSquared() ;
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(L, thru, "b");
		fig.plot(L, cross, "r");
		fig.renderPlot();
		fig.xlabel("Length (um)");
		fig.ylabel("Transmission");
		fig.run(true);
	}
}
