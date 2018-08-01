package photonics.ring;

import java.util.ArrayList;
import ch.epfl.general_libraries.clazzes.ParamName;
import complexSFG.math.Complex;
import static complexSFG.math.Complex.*;
import complexSFG.solver.SFG;

public class AddDropFirstOrder {

	public double kin, kout, tin, tout ;
	public double L, phi_rad ;
	ArrayList<String> nodes ;

	public SFG sfg ;

	public AddDropFirstOrder(
			@ParamName(name="round-trip phase (rad)") double phi_rad,
			@ParamName(name="ring loss") double L,
			@ParamName(name="input kappa") double kin,
			@ParamName(name="output kappa") double kout
			){
		this.phi_rad = phi_rad ;
		this.L = L ;
		this.kin = kin ;
		this.kout = kout ;
		this.tin = Math.sqrt(1-kin*kin) ;
		this.tout = Math.sqrt(1-kout*kout) ;
		nodes = new ArrayList<>() ;
		buildSFG() ;
	}

	private void buildSFG(){
		nodes.add("DC1.N1") ;
		nodes.add("DC1.N2") ;
		nodes.add("DC1.N3") ;
		nodes.add("DC1.N4") ;

		nodes.add("DC2.N1") ;
		nodes.add("DC2.N2") ;
		nodes.add("DC2.N3") ;
		nodes.add("DC2.N4") ;

		sfg = new SFG(nodes.size(), nodes) ;
		
		Complex gain = minusJ.times(phi_rad/2).exp().times(Math.pow(L, 0.25)) ;

		sfg.addArrow("DC1.N1", "DC1.N2", new Complex(tin, 0));
		sfg.addArrow("DC1.N1", "DC1.N3", new Complex(0, -kin));
		sfg.addArrow("DC1.N4", "DC1.N3", new Complex(tin, 0));
		sfg.addArrow("DC1.N4", "DC1.N2", new Complex(0, -kin));

		sfg.addArrow("DC2.N2", "DC2.N1", new Complex(tout, 0));
		sfg.addArrow("DC2.N2", "DC2.N4", new Complex(0, -kout));
		sfg.addArrow("DC2.N3", "DC2.N4", new Complex(tout, 0));
		sfg.addArrow("DC2.N3", "DC2.N1", new Complex(0, -kout));
		
		sfg.addArrow("DC1.N3", "DC2.N2", gain);
		sfg.addArrow("DC2.N1", "DC1.N4", gain);
	}
	
	public Complex getS41(){
		sfg.buildForwardPaths("DC1.N1", "DC2.N4");
		return sfg.computeForwardGain().divides(sfg.computeDelta()) ;
	}
	
	public Complex getS21(){
		sfg.buildForwardPaths("DC1.N1", "DC1.N2");
		return sfg.computeForwardGain().divides(sfg.computeDelta()) ;
	}
	
	public void buildDrop(){
		sfg.buildForwardPaths("DC1.N1", "DC2.N4");
	}
	
	public void buildThru(){
		sfg.buildForwardPaths("DC1.N1", "DC1.N2");
	}
	
	// for test
	public static void main(String[] args){
		AddDropFirstOrder adr = new AddDropFirstOrder(0, 1, 0.1, 0.1) ;
		System.out.println(adr.sfg.printAllLoops_compactForm());
	}
	
}
