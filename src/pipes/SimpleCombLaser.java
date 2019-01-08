package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class SimpleCombLaser extends AbstractCombLaser {
	
	public double powerPerLinemW, powerPerLinedBm, wallPlugEff, spacingGHz ;
	
	public SimpleCombLaser(
			@ParamName(name="Power per line (dBm)") double powerPerLinedBm,
			@ParamName(name="Line spacing (GHz)") double spacingGHz,
			@ParamName(name="Wall Plug Efficiency (%)") double wpe
			) {
		this.powerPerLinedBm = powerPerLinedBm ;
		this.powerPerLinemW = Math.pow(10.0, powerPerLinedBm/10.0) ;
		this.spacingGHz = spacingGHz ;
		this.wallPlugEff = wpe/1e2 ;
	}

	@Override
	public Map<? extends String, ? extends String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		map.put("Comb Power per line (dBm)", powerPerLinedBm+"") ;
		map.put("Comb power per line (mW)", powerPerLinemW+"") ;
		map.put("wall plug efficiency (%)", wallPlugEff*1e2+"") ;
		map.put("Comb spacing (Ghz)", spacingGHz+"") ;
		return map ;
	}

	@Override
	public double getPowerPerLinedBm() {
		return powerPerLinedBm;
	}

	@Override
	public double getPowerPerLinemW() {
		return powerPerLinemW;
	}

	@Override
	public double getWPE() {
		return wallPlugEff;
	}

	@Override
	public double getSpacingGHz() {
		return spacingGHz;
	}

	@Override
	public double getSpacingNm() {
		double c = 3e8 ;
		double lambda = 1550e-9 ;
		double spacingNm = spacingGHz * lambda*lambda/c * 1e9 ;
		return spacingNm;
	}

}
