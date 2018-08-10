package photonics.slab;

import mathLib.numbers.Complex;

public class ProfileSlabWgTE {

	SlabWg slab ;
	ModeSlabWgTE slabTE ;
	double widthNm, neff, n_up, n_down, n_core, k0 ;
	public double XpositionNm ;
	int modeNumber ;
	Complex one = new Complex(1,0), zero = new Complex(0,0), minusJ = new Complex(0,-1), plusJ = new Complex(0,1) ;
	
	// First I need to define the constructor for the geometry
	
	// This constructor is used when the mode number is specified
	public ProfileSlabWgTE(
			SlabWg slab, 
			double XpositionNm,
			int modeNumber
			){
		this.slab = slab ;
		this.widthNm = slab.getWidthNm() ;
		this.slabTE = new ModeSlabWgTE(slab) ;
		this.neff = slabTE.findSpecificModeIndex(modeNumber) ;
		this.n_core = slab.getCoreIndex() ;
		this.n_down = slab.getSubstrateIndex() ;
		this.n_up = slab.getCladIndex() ;
		this.k0 = 2*Math.PI/(slab.getWavelengthNm()*1e-9) ;
		this.XpositionNm = XpositionNm ;
	}
	
	// This constructor is useful when neff is known
	public ProfileSlabWgTE(
			SlabWg slab, 
			double XpositionNm,
			double neff
			){
		this.slab = slab ;
		this.widthNm = slab.getWidthNm() ;
		this.slabTE = new ModeSlabWgTE(slab) ;
		this.neff = neff ;
		this.n_core = slab.getCoreIndex() ;
		this.n_down = slab.getSubstrateIndex() ;
		this.n_up = slab.getCladIndex() ;
		this.k0 = 2*Math.PI/(slab.getWavelengthNm()*1e-9) ;
		this.XpositionNm = XpositionNm ;
	}
	
	// now I need to calculate normal index of each area (n_x)
	private Complex kx_up(){
		double A = Math.sqrt(neff*neff - n_up * n_up) ;
		return new Complex(0, -1*A*k0) ;
	}
	
	private Complex kx_core(){
		double A = Math.sqrt(n_core * n_core - neff * neff) ;
		return new Complex(A*k0, 0) ;
	}
	
	private Complex kx_down(){
		double A = Math.sqrt(neff*neff - n_down * n_down) ;
		return new Complex(0, -1*A*k0) ;
	}
	
	// Now finding the only component of electric field (Ey)
	
	private Complex[] find_Ey_fields_down(double xValNm){
		// forward wave
		Complex amp_forward = new Complex(0,0) ;
		Complex Ey_field_forward = (minusJ.times(kx_down()).times(xValNm*1e-9).exp().times(amp_forward)) ; // forward wave: -j*k_x
		// backward wave
		Complex amp_backward = new Complex(1,0) ; 
		Complex Ey_field_backward = (plusJ.times(kx_down()).times(xValNm*1e-9).exp().times(amp_backward)) ; // backward wave: +j*k_x
		return new Complex[] {Ey_field_forward, Ey_field_backward} ;
	}
	
	private Complex find_Ey_field_down(double xValNm){
		Complex[] Ey_down = find_Ey_fields_down(xValNm) ;
		return (Ey_down[0].plus(Ey_down[1])); // summing up forward and backward waves
	}
	
	private Complex[] find_Ey_fields_core(double xValNm){
		Complex A_plus_B = find_Ey_field_down(0) ;
		Complex A_minus_B = find_Ey_field_down(0).times(plusJ.times(kx_down())).divides(minusJ.times(kx_core())) ;
		// forward wave
		Complex amp_forward = (A_plus_B).plus(A_minus_B).divides(2) ;
		Complex Ey_forward = minusJ.times(kx_core()).times(xValNm*1e-9).exp().times(amp_forward) ;
		// backward wave
		Complex amp_backward = (A_plus_B).minus(A_minus_B).divides(2) ;
		Complex Ey_backward = plusJ.times(kx_core()).times(xValNm*1e-9).exp().times(amp_backward) ;
		return new Complex[] {Ey_forward, Ey_backward} ;
	}
	
	private Complex find_Ey_field_core(double xValNm){
		Complex[] Ey_core = find_Ey_fields_core(xValNm) ;
		return (Ey_core[0].plus(Ey_core[1])); // suming up forward and backward waves
	}
	
	private Complex[] find_Ey_fields_up(double xValNm){
		// forward wave
		Complex amp_forward = find_Ey_field_core(widthNm).divides(minusJ.times(kx_up()).times(widthNm*1e-9).exp()) ;
		Complex Ey_field_forward = (minusJ.times(kx_down()).times(xValNm*1e-9).exp().times(amp_forward)) ; // forward wave: -j*k_x
		// backward wave
		Complex amp_backward = new Complex(0,0) ; 
		Complex Ey_field_backward = (plusJ.times(kx_down()).times(xValNm*1e-9).exp().times(amp_backward)) ; // backward wave: +j*k_x
		return new Complex[] {Ey_field_forward, Ey_field_backward} ;
	}
	
	private Complex find_Ey_field_up(double xValNm){
		Complex[] Ey_up = find_Ey_fields_up(xValNm) ;
		return (Ey_up[0].plus(Ey_up[1])); // suming up forward and backward waves
	}
	
	public Complex get_Ey_field(){
		double xValNm = XpositionNm ;
		if(xValNm<0){
			return find_Ey_field_down(xValNm) ;
		}
		else if(xValNm>=0 && xValNm < widthNm){
			return find_Ey_field_core(xValNm) ;
		}
		else{
			return find_Ey_field_up(xValNm) ;
		}
	}
	
	public Complex get_Ex_field(){
		return new Complex(0,0) ;
	}
	
	public Complex get_Ez_field(){
		return new Complex(0,0) ;
	}
	// Now finding the components of magnetic field
	public Complex get_Hy_field(){
		return new Complex(0,0) ;
	}
	
	private Complex find_Hx_field_up(double xValNm){
		Complex coeff = new Complex(-neff/(120*Math.PI) , 0) ;
		Complex Hx_up = find_Ey_field_up(xValNm).times(coeff) ;
		return Hx_up ;
	}
	
	private Complex find_Hx_field_down(double xValNm){
		Complex coeff = new Complex(-neff/(120*Math.PI) , 0) ;
		Complex Hx_down = find_Ey_field_down(xValNm).times(coeff) ;
		return Hx_down ;
	}
	
	private Complex find_Hx_field_core(double xValNm){
		Complex coeff = new Complex(-neff/(120*Math.PI) , 0) ;
		Complex Hx_core = find_Ey_field_core(xValNm).times(coeff) ;
		return Hx_core ;
	}
	
	public Complex get_Hx_field(){
		double xValNm = XpositionNm ;
		if(xValNm<0){
			return find_Hx_field_down(xValNm) ;
		}
		else if(xValNm>=0 && xValNm < widthNm){
			return find_Hx_field_core(xValNm) ;
		}
		else{
			return find_Hx_field_up(xValNm) ;
		}
	}
	
	private Complex find_Hz_field_down(double xValNm){
		Complex coeff = new Complex(0, 1/(k0*120*Math.PI)) ;
		Complex Hz_forward = find_Ey_fields_down(xValNm)[0].times(minusJ.times(kx_down())).times(coeff) ;
		Complex Hz_backward = find_Ey_fields_down(xValNm)[1].times(plusJ.times(kx_down())).times(coeff) ;
		return (Hz_forward.plus(Hz_backward)) ;
	}
	
	private Complex find_Hz_field_up(double xValNm){
		Complex coeff = new Complex(0, 1/(k0*120*Math.PI)) ;
		Complex Hz_forward = find_Ey_fields_up(xValNm)[0].times(minusJ.times(kx_up())).times(coeff) ;
		Complex Hz_backward = find_Ey_fields_up(xValNm)[1].times(plusJ.times(kx_up())).times(coeff) ;
		return (Hz_forward.plus(Hz_backward)) ;
	}
	
	private Complex find_Hz_field_core(double xValNm){
		Complex coeff = new Complex(0, 1/(k0*120*Math.PI)) ;
		Complex Hz_forward = find_Ey_fields_core(xValNm)[0].times(minusJ.times(kx_core())).times(coeff) ;
		Complex Hz_backward = find_Ey_fields_core(xValNm)[1].times(plusJ.times(kx_core())).times(coeff) ;
		return (Hz_forward.plus(Hz_backward)) ;
	}
	
	public Complex get_Hz_field(){
		double xValNm = XpositionNm ;
		if(xValNm<0){
			return find_Hz_field_down(xValNm) ;
		}
		else if(xValNm>=0 && xValNm < widthNm){
			return find_Hz_field_core(xValNm) ;
		}
		else{
			return find_Hz_field_up(xValNm) ;
		}
	}
	
}
