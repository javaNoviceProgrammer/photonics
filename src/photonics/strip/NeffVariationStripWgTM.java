package photonics.strip;

import ch.epfl.general_libraries.clazzes.ParamName;
import photonics.util.Wavelength;

public class NeffVariationStripWgTM {
	
	// Note that this class is written for strip waveguide with TE mode. We can write a separate class for the TM modes in case we need that.
	
	Wavelength inputLambda ;
	double DnSi, DnSiO2, DwNm, DhNm, neff_original, neff_perturbed, Dneff, widthNm, heightNm ;
	double ng_original, ng_perturbed, Dng ;
	
	public NeffVariationStripWgTM(
			@ParamName(name="Mode m index") int mIndex,
			@ParamName(name="Mode n index") int nIndex,
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Width (nm)") double widthNm,
			@ParamName(name="Waveguide Height (nm)") double heightNm,
			@ParamName(name="Chagne of WG width (nm)") double DwNm,
			@ParamName(name="Chagne of WG height (nm)") double DhNm,
			@ParamName(name="Change of Silicon Index") double DnSi,
			@ParamName(name="Chagne of Silica Index") double DnSiO2
			){
		this.inputLambda = inputLambda ;
		this.widthNm = widthNm ;
		this.heightNm = heightNm ;
		this.DnSi = DnSi ;
		this.DnSiO2 = DnSiO2 ;
		this.DwNm = DwNm ;
		this.DhNm = DhNm ;
		StripWg stripWg_original = new StripWg(inputLambda, widthNm, heightNm, mIndex, nIndex) ;
		StripWg stripWg_perturbed = new StripWg(inputLambda, widthNm + DwNm, heightNm + DhNm, DnSi, DnSiO2) ;
		ModeStripWgTM stripTM_original = new ModeStripWgTM(stripWg_original, mIndex, nIndex) ; // for TEmn mode
		ModeStripWgTM stripTM_perturbed = new ModeStripWgTM(stripWg_perturbed, mIndex, nIndex) ; // for TEmn mode
		neff_original = stripTM_original.getEffectiveIndex() ;
		neff_perturbed = stripTM_perturbed.getEffectiveIndex() ;
		Dneff = neff_perturbed - neff_original ;
		ng_original = stripTM_original.getGroupIndex() ;
		ng_perturbed = stripTM_perturbed.getGroupIndex() ;
		Dng = ng_perturbed - ng_original ;
	}
	
	// this constructor with default TE00 mode
	public NeffVariationStripWgTM(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Width (nm)") double widthNm,
			@ParamName(name="Waveguide Height (nm)") double heightNm,
			@ParamName(name="Chagne of WG width (nm)") double DwNm,
			@ParamName(name="Chagne of WG height (nm)") double DhNm,
			@ParamName(name="Change of Silicon Index") double DnSi,
			@ParamName(name="Chagne of Silica Index") double DnSiO2
			){
		this.inputLambda = inputLambda ;
		this.widthNm = widthNm ;
		this.heightNm = heightNm ;
		this.DnSi = DnSi ;
		this.DnSiO2 = DnSiO2 ;
		this.DwNm = DwNm ;
		this.DhNm = DhNm ;
		StripWg stripWg_original = new StripWg(inputLambda, widthNm, heightNm, 0, 0) ;
		StripWg stripWg_perturbed = new StripWg(inputLambda, widthNm + DwNm, heightNm + DhNm, DnSi, DnSiO2) ;
		ModeStripWgTM stripTM_original = new ModeStripWgTM(stripWg_original, 0, 0) ; // for TE00 mode
		ModeStripWgTM stripTM_perturbed = new ModeStripWgTM(stripWg_perturbed, 0, 0) ; // for TE00 mode
		neff_original = stripTM_original.getEffectiveIndex() ;
		neff_perturbed = stripTM_perturbed.getEffectiveIndex() ;
		Dneff = neff_perturbed - neff_original ;
		ng_original = stripTM_original.getGroupIndex() ;
		ng_perturbed = stripTM_perturbed.getGroupIndex() ;
		Dng = ng_perturbed - ng_original ;
	}
	
	public double getLambdaNm(){
		return inputLambda.getWavelengthNm() ;
	}
	
	public double getDnSi(){
		return DnSi ;
	}
	
	public double getDnSiO2(){
		return DnSiO2 ;
	}
	
	public double getWidthNm(){
		return widthNm ;
	}
	
	public double getDwNm(){
		return DwNm ;
	}
	
	public double getHeightNm(){
		return heightNm ;
	}
	
	public double getDhNm(){
		return DhNm ;
	}
	
	public double getDNeffDnSiCoeff(){
		return Dneff/DnSi ;
	}
	
	public double getDNeffDnSiO2Coeff(){
		return Dneff/DnSiO2 ;
	}
	
	public double getDNeffDwNmCoeff(){
		return Dneff/DwNm ;
	}
	
	public double getDNeffDhNmCoeff(){
		return Dneff/DhNm ;
	}
	
	public double getDNeff(){
		return Dneff ;
	}
	
	public double getNeffOriginal(){
		return neff_original ;
	}
	
	public double getNeffPerturbed(){
		return neff_perturbed ;
	}
	
	public double getNgOriginal(){
		return ng_original ;
	}
	
	public double getNgPerturbed(){
		return ng_perturbed ;
	}
	
	public double getDNg(){
		return Dng ;
	}
	
}
