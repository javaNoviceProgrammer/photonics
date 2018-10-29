package photonics.heater.struct;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class HeaterWgCrossSection {

	double widthH_um, thicknessH_um, d_um, Y0_um ;
	
	public HeaterWgCrossSection(
			@ParamName(name="Width of Heater (um)") double widthH_um,
			@ParamName(name="Thickness of Heater (um)") double thickness_um,
			@ParamName(name="Distance from Wg (um)") double d_um,
			@ParamName(name="Distance from Substrate (um)") double Y0_um
			){
		this.widthH_um = widthH_um ;
		this.thicknessH_um = thickness_um ;
		this.d_um = d_um ;
		this.Y0_um = Y0_um ;
	}
	
	public double getWidthMicron(){
		return widthH_um ;
	}
	
	public double getThicknessMicron(){
		return thicknessH_um ;
	}
	
	public double getDistanceMicron(){
		return d_um ;
	}
	
	public double getDistanceFromSubstrateMicron(){
		return Y0_um ;
	}
	
	public Map<String, String> getAllParameters(){
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put("d (um)", d_um+"") ;
		map.put("wH (um)", widthH_um+"") ;
		map.put("thicknessH (um)", thicknessH_um+"") ;
		map.put("d_substrate (um)", Y0_um + "") ;
		return map ;
	}
	
}
