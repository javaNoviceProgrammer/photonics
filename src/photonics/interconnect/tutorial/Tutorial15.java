package photonics.interconnect.tutorial;

import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import photonics.interconnect.elements.passive.DirectionalCoupler;
import photonics.interconnect.elements.passive.StraightWg;
import photonics.interconnect.modes.Neff450X220CoupledStrip;
import photonics.interconnect.modes.Neff450X220Strip;
import photonics.interconnect.solver.PhotonicCircuit;
import photonics.interconnect.util.Utils;
import photonics.util.Wavelength;

public class Tutorial15 {
	
	public static void main(String[] args) {
//		double L = 36.8 ; // um
		double Ldc1 = 34 ; // um
		double Ldc2 = 34 ; // um
		double L3 = 20 ; // um
		double Lwg = 30 ; // um
		double gap = 200e-3 ; // um
		double loss = 0 ; // dB per cm
		double[] lambdaNm = MathUtils.linspace(1545, 1555, 10000) ;
		double[] transfer = new double[lambdaNm.length] ; 
		for(int i=0; i<lambdaNm.length; i++) {
			Wavelength lambda = new Wavelength(lambdaNm[i]) ;
			PhotonicCircuit pc = new PhotonicCircuit() ;
			pc.setWavelength(lambda) ;
			DirectionalCoupler dc1 = new DirectionalCoupler("dc1", new Neff450X220CoupledStrip(), Ldc1, gap, loss) ;
			DirectionalCoupler dc2 = new DirectionalCoupler("dc2", new Neff450X220CoupledStrip(), Ldc2, gap, loss) ;
			StraightWg wg1 = new StraightWg("wg1", new Neff450X220Strip(), loss, Lwg) ;
			StraightWg wg2 = new StraightWg("wg2", new Neff450X220Strip(), loss, Lwg) ;
			StraightWg wg3 = new StraightWg("wg3", new Neff450X220Strip(), loss, L3) ;
			pc.addElements(dc1, dc2);
			pc.addElements(wg1, wg2, wg3);
			pc.connectPorts("dc1.port2", "wg1.port2");
			pc.connectPorts("wg1.port1", "dc1.port3");
			pc.connectPorts("dc2.port2", "wg2.port2");
			pc.connectPorts("wg2.port1", "dc2.port3");
			pc.connectPorts("dc1.port4", "wg3.port1");
			pc.connectPorts("dc2.port1", "wg3.port2");
			transfer[i] = Utils.todB(pc.getTransfer("dc1.port1", "dc2.port4").absSquared()) ;
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(lambdaNm, transfer);
		fig.renderPlot();
		fig.xlabel("Wavelength (nm)");
		fig.ylabel("Transmission (dB)");
		fig.run(true);
	}
}
