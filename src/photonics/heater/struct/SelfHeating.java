package photonics.heater.struct;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class SelfHeating {

	public double alphaH, Kv, Rlinear ;
	
	public SelfHeating(
			@ParamName(name="alphaH (1/K)", default_="4.5e-3") double alphaH,
			@ParamName(name="Kv (1/V^2)", default_="0.6") double Kv,
			@ParamName(name="R linear (Ohm)", default_="161") double Rlinear
			){
		this.alphaH = alphaH ;
		this.Kv = Kv ;
		this.Rlinear = Rlinear ;
	}
	
	public Map<String, String> getAllParameters(){
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put("alphaH", alphaH+"") ;
		map.put("Kv", Kv+"") ;
		map.put("Rlinear", Rlinear+"") ;
		return map ;
	}
	
	public double getAlphaH(){
		return alphaH ;
	}
	
	public double getKv(){
		return Kv ;
	}
	
	public double getRlinear(){
		return Rlinear ;
	}
	
	public double getDeltaT(double voltage){
		double DeltaT = 1/(2*alphaH) * (-1 + Math.sqrt(1 + Kv * voltage * voltage)) ;
		return DeltaT ;
	}
	
	public double[] getDeltaT(double[] voltage){
		double[] DeltaT = new double[voltage.length] ;
		for(int i=0; i<voltage.length; i++){
			DeltaT[i] = getDeltaT(voltage[i]) ;
		}
		return DeltaT ;
	}
	
	public double getCurrent_mA(double voltage){
		double selfHeatingFactor = 2/(1+Math.sqrt(1+Kv*voltage*voltage)) ;
		double I = voltage/Rlinear * selfHeatingFactor ;
		return (I*1e3) ;
	}

	public double[] getCurrent_mA(double[] voltage){
		int M = voltage.length ;
		double[] I_mA = new double[M] ;
		for(int i=0; i<M; i++){
			I_mA[i] = getCurrent_mA(voltage[i]) ;
		}
		return I_mA ;
	}
	
}
