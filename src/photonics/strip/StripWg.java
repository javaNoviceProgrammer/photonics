package photonics.strip;

import ch.epfl.general_libraries.clazzes.ParamName;
import photonics.material.AbstractDielectric;
import photonics.material.Silica;
import photonics.material.Silicon;
import photonics.slab.SlabWg;
import photonics.util.Wavelength;

public class StripWg {

	Wavelength inputLambda ;
	double widthNm, heightNm ;
	double n_core, n_subs, n_clad, DnSi, DnSiO2 ;
	AbstractDielectric Si, SiO2 ;
	SlabWg slabX, slabY ;

	// inputs are width, height, and wavelength
	public StripWg(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide width (nm)") double widthNm,
			@ParamName(name="Waveguide height (nm)") double heightNm
			){
		this.inputLambda = inputLambda ;
		this.widthNm = widthNm ;
		this.heightNm = heightNm ;
		DnSi = 0 ;
		DnSiO2 = 0 ;
		// index of silicon and silica
		Si = new Silicon() ;
		SiO2 = new Silica() ;
		n_core = Si.getIndex(inputLambda) ;
		n_clad = SiO2.getIndex(inputLambda) ;
		n_subs = SiO2.getIndex(inputLambda) ;
		// need to create to slab waveguides for the x and y directions
		slabX = new SlabWg(inputLambda, widthNm, n_clad, n_core, n_clad) ;
		slabY = new SlabWg(inputLambda, heightNm, n_subs, n_core, n_clad) ;
	}
	//*******************************************************************************
	// This constructor includes the effects of change in Si and SiO2 index
	public StripWg(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide width (nm)") double widthNm,
			@ParamName(name="Waveguide height (nm)") double heightNm,
			@ParamName(name="Si index change") double DnSi,
			@ParamName(name="SiO2 index change") double DnSiO2
			){
		this.inputLambda = inputLambda ;
		this.widthNm = widthNm ;
		this.heightNm = heightNm ;
		this.DnSi = DnSi ;
		this.DnSiO2 = DnSiO2 ;
		// index of silicon and silica
		Si = new Silicon() ;
		SiO2 = new Silica() ;
		n_core = Si.getIndex(inputLambda) + DnSi ;
		n_clad = SiO2.getIndex(inputLambda) + DnSiO2 ;
		n_subs = SiO2.getIndex(inputLambda) + DnSiO2 ;
		// need to create two slab waveguides for the x and y directions
		slabX = new SlabWg(inputLambda, widthNm, n_clad, n_core, n_clad) ;
		slabY = new SlabWg(inputLambda, heightNm, n_subs, n_core, n_clad) ;
	}
	//*******************************************************************************
	// This constructor allows us to have different cladding and substrate Materials
	public StripWg(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide width (nm)") double widthNm,
			@ParamName(name="Waveguide height (nm)") double heightNm,
			@ParamName(name="Cladding Material") AbstractDielectric claddMaterial,
			@ParamName(name="Core Material") AbstractDielectric coreMaterial,
			@ParamName(name="Substrate Material") AbstractDielectric substrateMaterial
			){
		this.inputLambda = inputLambda ;
		this.widthNm = widthNm ;
		this.heightNm = heightNm ;
		DnSi = 0 ;
		DnSiO2 = 0 ;
		n_core = coreMaterial.getIndex(inputLambda) ;
		n_clad = claddMaterial.getIndex(inputLambda) ;
		n_subs = substrateMaterial.getIndex(inputLambda) ;
		// need to create to slab waveguides for the x and y directions
		slabX = new SlabWg(inputLambda, widthNm, n_clad, n_core, n_clad) ;
		slabY = new SlabWg(inputLambda, heightNm, n_subs, n_core, n_clad) ;
	}

	//*******************************************************************************
	// This constructor allows us to have different cladding and substrate Materials
	public StripWg(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide width (nm)") double widthNm,
			@ParamName(name="Waveguide height (nm)") double heightNm,
			@ParamName(name="Cladding index") double n_clad,
			@ParamName(name="Core index") double n_core,
			@ParamName(name="Substrate index") double n_subs
			){
		this.inputLambda = inputLambda ;
		this.widthNm = widthNm ;
		this.heightNm = heightNm ;
		DnSi = 0 ;
		DnSiO2 = 0 ;
		this.n_core = n_core ;
		this.n_clad = n_clad ;
		this.n_subs = n_subs ;
		// need to create to slab waveguides for the x and y directions
		slabX = new SlabWg(inputLambda, widthNm, n_clad, n_core, n_clad) ;
		slabY = new SlabWg(inputLambda, heightNm, n_subs, n_core, n_clad) ;
	}

	//*******************************************************************************
	public Wavelength getInputLambda(){
		return inputLambda ;
	}

	public double getWavelengthNm(){
		return inputLambda.getWavelengthNm() ;
	}

	public double getWidthNm(){
		return widthNm ;
	}

	public double getHeightNm(){
		return heightNm ;
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

	public double getDnSi(){
		return DnSi ;
	}

	public double getDnSiO2(){
		return DnSiO2 ;
	}

	// now creating the index profile

	public double getIndexProfile(double xValNm, double yValNm){
		if(xValNm>=0 && xValNm<=widthNm && yValNm>=0 && yValNm<=heightNm){return n_core; }
		else if(yValNm>=0 && xValNm<0) {return n_clad; }
		else if(yValNm>=0 && xValNm>widthNm) { return n_clad; }
		else{return n_subs; }
	}

}
