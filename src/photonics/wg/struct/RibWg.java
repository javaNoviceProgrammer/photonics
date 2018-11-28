package photonics.wg.struct;

import ch.epfl.general_libraries.clazzes.ParamName;
import photonics.material.AbstractDielectric;
import photonics.material.Silica;
import photonics.material.Silicon;
import photonics.slab.SlabWg;
import photonics.util.Wavelength;

public class RibWg {

	Wavelength inputLambda ;
	double widthNm, heightNm, heightSlabNm ;
	double n_core, n_subs, n_clad ;
	AbstractDielectric Si, SiO2 ;
	SlabWg slabX, slabY, sideSlabY ;
	
	// inputs are width, height, and wavelength 
	public RibWg(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide width (nm)") double widthNm,
			@ParamName(name="Waveguide height (nm)") double heightNm,
			@ParamName(name="Slab height (nm)") double heightSlabNm
			){
		this.inputLambda = inputLambda ;
		this.widthNm = widthNm ;
		this.heightNm = heightNm ;
		this.heightSlabNm = heightSlabNm ;
		// index of silicon and silica
		Si = new Silicon() ;
		SiO2 = new Silica() ;
		n_core = Si.getIndex(inputLambda) ;
		n_clad = SiO2.getIndex(inputLambda) ;
		n_subs = SiO2.getIndex(inputLambda) ;
		// need to create to slab waveguides for the x and y directions 
		// For equivalent slab in X direction we need to calculate the effective index of the small side slabs
		double sideSlabFactor = 1 ; // adding an additional factor to the side slab height
		sideSlabY = new SlabWg(inputLambda, heightSlabNm*sideSlabFactor, n_subs, n_core, n_clad) ;
		slabX = new SlabWg(inputLambda, widthNm, n_clad, n_core, n_clad) ;
		slabY = new SlabWg(inputLambda, heightNm, n_subs, n_core, n_clad) ; // need to include the slab height as well
	}
	//*******************************************************************************
	// This constructor includes the effects of change in Si and SiO2 index
	public RibWg(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide width (nm)") double widthNm,
			@ParamName(name="Waveguide height (nm)") double heightNm,
			@ParamName(name="Slab height (nm)") double heightSlabNm,
			@ParamName(name="Si index change") double DnSi,
			@ParamName(name="SiO2 index change") double DnSiO2
			){
		this.inputLambda = inputLambda ;
		this.widthNm = widthNm ;
		this.heightNm = heightNm ;
		this.heightSlabNm = heightSlabNm ;
		// index of silicon and silica
		Si = new Silicon() ;
		SiO2 = new Silica() ;
		n_core = Si.getIndex(inputLambda) + DnSi ;
		n_clad = SiO2.getIndex(inputLambda) + DnSiO2 ;
		n_subs = SiO2.getIndex(inputLambda) + DnSiO2 ;
		// need to create to slab waveguides for the x and y directions 
		// For equivalent slab in X direction we need to calculate the effective index of the small side slabs
		double sideSlabFactor = 1 ; // adding an additional factor to the side slab height
		sideSlabY = new SlabWg(inputLambda, heightSlabNm*sideSlabFactor, n_subs, n_core, n_clad) ;
		slabX = new SlabWg(inputLambda, widthNm, n_clad, n_core, n_clad) ;
		slabY = new SlabWg(inputLambda, heightNm, n_subs, n_core, n_clad) ; // need to include the slab height as well
	}
	//*******************************************************************************
	// This constructor includes free choice of material for substrate, core, and cladding
	public RibWg(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide width (nm)") double widthNm,
			@ParamName(name="Waveguide height (nm)") double heightNm,
			@ParamName(name="Slab height (nm)") double heightSlabNm,
			@ParamName(name="Cladding Material") AbstractDielectric claddMaterial,
			@ParamName(name="Core Material") AbstractDielectric coreMaterial,
			@ParamName(name="Substrate Material") AbstractDielectric substrateMaterial
			){
		this.inputLambda = inputLambda ;
		this.widthNm = widthNm ;
		this.heightNm = heightNm ;
		this.heightSlabNm = heightSlabNm ;
		// index of silicon and silica
		n_core = coreMaterial.getIndex(inputLambda) ;
		n_clad = claddMaterial.getIndex(inputLambda) ;
		n_subs = substrateMaterial.getIndex(inputLambda) ;
		// need to create two slab waveguides for the x and y directions 
		// For equivalent slab in X direction we need to calculate the effective index of the small side slabs
		double sideSlabFactor = 1 ; // adding an additional factor to the side slab height
		sideSlabY = new SlabWg(inputLambda, heightSlabNm*sideSlabFactor, n_subs, n_core, n_clad) ;
		slabX = new SlabWg(inputLambda, widthNm, n_clad, n_core, n_clad) ;
		slabY = new SlabWg(inputLambda, heightNm, n_subs, n_core, n_clad) ; // need to include the slab height as well
	}
	//*******************************************************************************
	
	public double getWavelengthNm(){
		return inputLambda.getWavelengthNm() ;
	}
	
	public double getWidthNm(){
		return widthNm ;
	}
	
	public double getHeightNm(){
		return heightNm ;
	}
	
	public double getSlabHeightNm(){
		return heightSlabNm ;
	}
	
	public double getCoreIndex(){
		return n_core ;
	}
	
	public double getCladIndex(){
		return n_clad ;
	}
	
	public double getSubstrateIndex(){
		return n_subs ;
	}
	
	public SlabWg getSlabX(){
		return slabX ;
	}
	
	public SlabWg getSlabY(){
		return slabY ;
	}
	
	public SlabWg getSideSlabY(){
		return sideSlabY ;
	}
	

	// creating the index profile
	
	public double getIndexProfile(double xValNm, double yValNm){
		if(xValNm>=0 && xValNm<=widthNm && yValNm>=0 && yValNm<=heightNm+heightSlabNm){return n_core; }
		else if(yValNm>=0 && yValNm<=heightSlabNm && xValNm<0){return n_core;}
		else if(yValNm>=0 && yValNm<=heightSlabNm && xValNm>widthNm){return n_core;}
		else if(yValNm>heightSlabNm && xValNm<0){return n_clad;}
		else if(yValNm>heightSlabNm && xValNm>widthNm){return n_clad;}
		else if(yValNm>heightNm+heightSlabNm && xValNm>=0 && xValNm<=widthNm){return n_clad;}
		else{ return n_subs; }
	}
	
}
