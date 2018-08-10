package photonics.slab;

import ch.epfl.general_libraries.clazzes.ParamName;

public class CoupledSlabWg {

/*			n_up
	-----------------------
			n_core1
	-----------------------
			n_gap
	-----------------------
			n_core2
	-----------------------
			n_down
*/
	
	public double w1_nm, w2_nm, gap_nm;
	public double n_down, n_core1, n_core2, n_up, n_gap, n_high, n_low ;

	public CoupledSlabWg(
			@ParamName(name="waveguide 1 width (nm)") double w1_nm,
			@ParamName(name="waveguide 2 width (nm)") double w2_nm,
			@ParamName(name="gap size (nm)") double gap_nm,
			@ParamName(name="up index") double n_u,
			@ParamName(name="core 1 index") double n_c_1,
			@ParamName(name="gap index") double n_g,
			@ParamName(name="core 2 index") double n_c_2,
			@ParamName(name="down index") double n_d
			){
		this.w1_nm = w1_nm ;
		this.w2_nm = w2_nm ;
		this.gap_nm = gap_nm ;
		this.n_up = n_u ;
		this.n_core1 = n_c_1 ;
		this.n_gap = n_g ;
		this.n_core2 = n_c_2 ;
		this.n_down = n_d ;
		this.n_high = Math.max(n_c_1, n_c_2) ;
		this.n_low = Math.min(Math.min(n_u, n_d), n_g) ;
	}

	
}
