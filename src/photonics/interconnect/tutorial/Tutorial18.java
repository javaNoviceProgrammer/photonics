package photonics.interconnect.tutorial;

import photonics.interconnect.elements.general.GeneralElement;
import photonics.interconnect.solver.PhotonicCircuit;
import photonics.util.Wavelength;

public class Tutorial18 {

	/*
	 * Simulating an all-pass ring resonator with back scattering
	 */

	public static void main(String[] args) {
		Wavelength lambda = new Wavelength(1550) ;
		PhotonicCircuit pc = new PhotonicCircuit() ;
		pc.setWavelength(lambda);
		GeneralElement dc1 = new GeneralElement("dc1", 4, false) ;
		GeneralElement cwg1 = new GeneralElement("cwg1", 2, false) ;
		pc.addElements(dc1, cwg1);
		pc.connectPorts("dc1.port3", "cwg1.port2");
		pc.connectPorts("dc1.port4", "cwg1.port1");
		pc.getTransfer("dc1.port1", "dc1.port2") ;
		pc.printDetails();
	}
}
