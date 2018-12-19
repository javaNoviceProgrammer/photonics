package photonics.wg;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import photonics.wg.loss.AbstractBendLossModel;

public class WgProperties {

	double wgPropLossdBperCm ;
	double confinementFactor ;
	double widthNm = 450 ;
	double heightNm = 220 ;
	int numConvSteps ;
	AbstractBendLossModel bendLossModel ;
	String wgMode = "TE00" ;

	public WgProperties(
			@ParamName(name="Number of Steps for Convergence", default_="500") int numConvSteps,
			@ParamName(name="Straight Waveguide Propagation Loss (dB/cm)", default_="2") double wgPropLossdBperCm ,
			@ParamName(name="Confinement Factor of the Optical Mode", default_="1.1") double confinementFactor ,
			@ParamName(name="Choose Bend Loss Model for Curved Waveguides") AbstractBendLossModel bendLossModel
			){
		this.wgPropLossdBperCm = wgPropLossdBperCm ;
		this.confinementFactor = confinementFactor ;
		this.bendLossModel = bendLossModel ;
		this.numConvSteps = numConvSteps ;
	}

	public WgProperties(
			@ParamName(name="Number of Steps for Convergence", default_="500") int numConvSteps,
			@ParamName(name="Width of Waveguide (nm)") double widthNm,
			@ParamName(name="Height of Waveguide (nm)") double heightNm,
			@ParamName(name="Straight Waveguide Propagation Loss (dB/cm)", default_="2") double wgPropLossdBperCm ,
			@ParamName(name="Confinement Factor of the Optical Mode", default_="1.1") double confinementFactor ,
			@ParamName(name="Choose Bend Loss Model for Curved Waveguides") AbstractBendLossModel bendLossModel
			){
		this.wgPropLossdBperCm = wgPropLossdBperCm ;
		this.widthNm = widthNm ;
		this.heightNm = heightNm ;
		this.confinementFactor = confinementFactor ;
		this.bendLossModel = bendLossModel ;
		this.numConvSteps = numConvSteps ;
	}

	public Map<String, String> getAllParameters(){
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put("wg width (nm)", widthNm+"") ;
		map.put("wg height (nm)", heightNm+"") ;
		map.put("wg Mode", wgMode) ;
		map.put("wg prop loss (dB/cm)", wgPropLossdBperCm+"") ;
		map.put("convergence steps", numConvSteps+"") ;
		return map ;
	}

	// add calculations for waveguide sensitivity parameters

	public int getConvergenceSteps(){
		return numConvSteps ;
	}

	public double getWgPropLossdBperCm(){
		return wgPropLossdBperCm ;
	}

	public double getWgPropLossPerMeter(){
		return (wgPropLossdBperCm * 23) ;
	}

	public double getWgPropLossPerCm(){
		return (wgPropLossdBperCm * 23/100) ;
	}

	public double getConfinementFactor(){
		return confinementFactor ;
	}

	public AbstractBendLossModel getBendLossModel(){
		return bendLossModel ;
	}

	public double getWidthCm(){
		return widthNm*1e-9*1e2 ;
	}

	public double getWidthNm(){
		return widthNm ;
	}

	public double getHeightNm(){
		return heightNm ;
	}

	public double getWidthMeter(){
		return widthNm*1e-9 ;
	}

	public double getHeightCm(){
		return heightNm*1e-9*1e2 ;
	}

	public double getHeightMeter(){
		return heightNm*1e-9 ;
	}

	public double getCrossSectionAreaCmSquare(){
		return (getWidthCm()*getHeightCm()) ;
	}

	public double getCrossSectionAreaMeterSquare(){
		return (getWidthMeter()*getHeightMeter()) ;
	}


}
