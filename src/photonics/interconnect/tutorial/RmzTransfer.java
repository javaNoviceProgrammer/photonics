package photonics.interconnect.tutorial;

import photonics.interconnect.elements.general.FourPortElement;
import photonics.interconnect.elements.general.TwoPortElement;
import photonics.interconnect.solver.PhotonicCircuit;
import photonics.util.Wavelength;

public class RmzTransfer {
	
	public static void main(String[] args) {
		Wavelength lambda = 1550.0 ;
		PhotonicCircuit pc = new PhotonicCircuit() ;
		pc.setWavelength(lambda);
		FourPortElement mmi1 = new FourPortElement("mmi1") ,
						mmi2 = new FourPortElement("mmi2") ;
		TwoPortElement ps1 = new TwoPortElement("ps1") ,
					   ps2 = new TwoPortElement("ps2") ;
		
		TwoPortElement ring1 = new TwoPortElement("ring1") ,
					   ring2 = new TwoPortElement("ring2") ;
		
		pc.addElements(mmi1, mmi2, ps1, ps2, ring1, ring2);
		
//		pc.connectPorts("mmi1.port3", "ps1.port1");
//		pc.connectPorts("mmi1.port2", "ps2.port1");
		pc.connectPorts("ps1.port2", "ring1.port1");
		pc.connectPorts("ps2.port2", "ring2.port1");
//		pc.connectPorts("ring1.port2", "mmi2.port3");
//		pc.connectPorts("ring2.port2", "mmi2.port2");
		
		pc.connectPortsUnidirectional("mmi1.port3", "ps1.port1");
		pc.connectPortsUnidirectional("mmi1.port2", "ps2.port1");
//		pc.connectPortsUnidirectional("ps1.port2", "ring1.port1");
//		pc.connectPortsUnidirectional("ps2.port2", "ring2.port1");
		pc.connectPortsUnidirectional("ring1.port2", "mmi2.port3");
		pc.connectPortsUnidirectional("ring2.port2", "mmi2.port2");
		
		pc.getTransfer("mmi1.port4", "mmi2.port4") ;
		pc.printDetails() ;
		
	}

}
