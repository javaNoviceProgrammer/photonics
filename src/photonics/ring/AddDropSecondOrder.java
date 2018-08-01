package photonics.ring;

import java.util.ArrayList;
import ch.epfl.general_libraries.clazzes.ParamName;
import complexSFG.math.Complex;
import static complexSFG.math.Complex.*;
import complexSFG.solver.SFG;

public class AddDropSecondOrder {

	public double kin, kout, tin, tout, kmiddle, tmiddle ;
	public double L, phi_rad ;
	ArrayList<String> nodes ;

	public SFG sfg ;

	public AddDropSecondOrder(
			@ParamName(name="round-trip phase (rad)") double phi_rad,
			@ParamName(name="ring loss") double L,
			@ParamName(name="input kappa") double kin,
			@ParamName(name="output kappa") double kout,
			@ParamName(name="middle kappa") double kmiddle
			){
		this.phi_rad = phi_rad ;
		this.L = L ;
		this.kin = kin ;
		this.kout = kout ;
		this.tin = Math.sqrt(1-kin*kin) ;
		this.tout = Math.sqrt(1-kout*kout) ;
		this.kmiddle = kmiddle ;
		this.tmiddle = Math.sqrt(1-kmiddle*kmiddle) ;
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
		
		nodes.add("DC3.N1") ;
		nodes.add("DC3.N2") ;
		nodes.add("DC3.N3") ;
		nodes.add("DC3.N4") ;

		sfg = new SFG(nodes.size(), nodes) ;
		
		Complex gain = minusJ.times(phi_rad/2).exp().times(Math.pow(L, 0.25)) ;

		sfg.addArrow("DC1.N1", "DC1.N2", new Complex(tin, 0));
		sfg.addArrow("DC1.N1", "DC1.N3", new Complex(0, -kin));
		sfg.addArrow("DC1.N4", "DC1.N3", new Complex(tin, 0));
		sfg.addArrow("DC1.N4", "DC1.N2", new Complex(0, -kin));

		sfg.addArrow("DC2.N2", "DC2.N1", new Complex(tmiddle, 0));
		sfg.addArrow("DC2.N2", "DC2.N4", new Complex(0, -kmiddle));
		sfg.addArrow("DC2.N3", "DC2.N4", new Complex(tmiddle, 0));
		sfg.addArrow("DC2.N3", "DC2.N1", new Complex(0, -kmiddle));
		
		sfg.addArrow("DC1.N3", "DC2.N2", gain);
		sfg.addArrow("DC2.N1", "DC1.N4", gain);
		
		sfg.addArrow("DC3.N1", "DC3.N2", new Complex(tout, 0));
		sfg.addArrow("DC3.N1", "DC3.N3", new Complex(0, -kout));
		sfg.addArrow("DC3.N4", "DC3.N3", new Complex(tout, 0));
		sfg.addArrow("DC3.N4", "DC3.N2", new Complex(0, -kout));
		
		sfg.addArrow("DC2.N4", "DC3.N1", gain);
		sfg.addArrow("DC3.N2", "DC2.N3", gain);
	}
	
	public Complex getS31(){
		sfg.buildForwardPaths("DC1.N1", "DC3.N3");
		return sfg.computeForwardGain().divides(sfg.computeDelta()) ;
	}
	
	public Complex getS21(){
		sfg.buildForwardPaths("DC1.N1", "DC1.N2");
		return sfg.computeForwardGain().divides(sfg.computeDelta()) ;
	}
	
	public void buildDrop(){
		sfg.buildForwardPaths("DC1.N1", "DC3.N3");
	}
	
	public void buildThru(){
		sfg.buildForwardPaths("DC1.N1", "DC1.N2");
	}
	
	// for test
	public static void main(String[] args){
		AddDropSecondOrder adr = new AddDropSecondOrder(0, 1, 0.1, 0.1, 0.1) ;
		System.out.println(adr.sfg.printAllLoops_compactForm());
	}
	
}
