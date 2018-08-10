package photonics.slab;

import ch.epfl.general_libraries.clazzes.ParamName;
import photonics.material.AbstractDielectric;
import photonics.util.Wavelength;

public class SlabWg {

	// I need to create the slab profile and calculate all the parameters of the dielectric slab waveguide
	
	double lambdaNm, widthNm, n_down, n_core, n_up, n_high, n_low ;
	//***************************************************************
	// First constructor for specific wavelength
	public SlabWg(
			@ParamName(name="wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Slab width (nm)") double widthNm,
			@ParamName(name="Substrate index") double n_down,
			@ParamName(name="Core index") double n_core,
			@ParamName(name="Cladding index") double n_up
			){
		this.lambdaNm = inputLambda.getWavelengthNm() ;
		this.widthNm = widthNm ;
		this.n_down = n_down ;
		this.n_core = n_core ;
		this.n_up = n_up ;
		this.n_high = n_core ;
		this.n_low = Math.max(n_up, n_down) ;
		// The correct index profile is like this: "n_up (cladding) < n_down (substrate) < n_core (waveguide)"
	}
	//***************************************************************
	// This constructor based on normalized frequency
	public SlabWg(
			@ParamName(name="Normalized Frequency") double V,
			@ParamName(name="Slab width (nm)") double widthNm,
			@ParamName(name="Substrate index") double n_down,
			@ParamName(name="Core index") double n_core,
			@ParamName(name="Cladding index") double n_up
			){
		this.lambdaNm = 2*Math.PI/V * widthNm * Math.sqrt(n_core*n_core - n_down*n_down) ;
		this.widthNm = widthNm ;
		this.n_down = n_down ;
		this.n_core = n_core ;
		this.n_up = n_up ;
		this.n_high = n_core ;
		this.n_low = Math.max(n_up, n_down) ;
		// The correct index profile is like this: "n_up (cladding) < n_down (substrate) < n_core (waveguide)"
	}
	//***************************************************************
	// Third constructor for specific wavelength and material choice
	public SlabWg(
			@ParamName(name="wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Slab width (nm)") double widthNm,
			@ParamName(name="Substrate Material") AbstractDielectric substrateMaterial,
			@ParamName(name="Core Material") AbstractDielectric coreMaterial,
			@ParamName(name="Cladding Material") AbstractDielectric cladMaterial
			){
		this.lambdaNm = inputLambda.getWavelengthNm() ;
		this.widthNm = widthNm ;
		this.n_down = substrateMaterial.getIndex(inputLambda) ;
		this.n_core = coreMaterial.getIndex(inputLambda) ;
		this.n_up = cladMaterial.getIndex(inputLambda) ;
		this.n_high = n_core ;
		this.n_low = Math.max(n_up, n_down) ;
		// The correct index profile is like this: "n_up (cladding) < n_down (substrate) < n_core (waveguide)"
	}
	//***************************************************************
	// This constructor based on normalized frequency
	public SlabWg(
			@ParamName(name="Normalized Frequency") double V,
			@ParamName(name="Wavelength (nm)") double lambdaNm,
			@ParamName(name="Substrate Material") AbstractDielectric substrateMaterial,
			@ParamName(name="Core Material") AbstractDielectric coreMaterial,
			@ParamName(name="Cladding Material") AbstractDielectric cladMaterial
			){
		Wavelength inputLambda = new Wavelength(lambdaNm) ;
		this.n_down = substrateMaterial.getIndex(inputLambda) ;
		this.n_core = coreMaterial.getIndex(inputLambda) ;
		this.n_up = cladMaterial.getIndex(inputLambda) ;
		this.n_high = n_core ;
		this.n_low = Math.max(n_up, n_down) ;
		this.widthNm = V * lambdaNm/(2*Math.PI* Math.sqrt(n_core*n_core - n_down*n_down)) ;
		this.lambdaNm = lambdaNm ;
		// The correct index profile is like this: "n_up (cladding) < n_down (substrate) < n_core (waveguide)"
	}
	//***************************************************************
	
	public double getNormalizedFreq(){
		double NA = Math.sqrt(n_high*n_high - n_low*n_low) ;
		return ((2*Math.PI/lambdaNm)*widthNm*NA) ;
	}
	
	public double getNormalizedIndex(double neff){
		double b = (neff*neff - n_low*n_low)/(n_high*n_high - n_low*n_low) ; 
		return b ;
	}
	// For TE mode
	public double getTEsymmetryFactor(){
		double ns = Math.max(n_up, n_down) ;
		double nc = Math.min(n_up, n_down) ;
		double A = ns*ns - nc*nc ;
		double B = n_high * n_high - ns*ns ;
		return (A/B) ;
	}
	// For TM mode
	public double getTMsymmetryFactor(){
		double ns = Math.max(n_up, n_down) ;
		double nc = Math.min(n_up, n_down) ;
		double A = ns*ns - nc*nc ;
		double B = n_high * n_high - ns*ns ;
		return (A/B * Math.pow(n_core, 4)/Math.pow(n_up, 4)) ;
	}
	
	public double getWidthNm(){
		return widthNm ;
	}
	
	public double getWavelengthNm(){
		return lambdaNm ;
	}
	
	public double getCoreIndex(){
		return n_core ;
	}
	
	public double getSubstrateIndex(){
		return Math.max(n_up, n_down) ;
	}
	
	public double getCladIndex(){
		return Math.min(n_up, n_down) ;
	}
	
	public double getIndexProfile(double xValNm){
		if(xValNm<=0){return n_down;}
		else if(xValNm>0 && xValNm<widthNm){return n_core;}
		else{return n_up; }
	}
	
}
