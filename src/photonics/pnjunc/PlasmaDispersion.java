package photonics.pnjunc;

public class PlasmaDispersion {
	
	public static final double An_DnSi_1550 = -8.85e-22 ;
	public static final double En_DnSi_1550 = 1.0 ;
	public static final double Ap_DnSi_1550 = -8.5e-18 ;
	public static final double Ep_DnSi_1550 = 0.8 ;
	
	public static final double An_DaSi_1550 = 8.5e-18 ;
	public static final double En_DaSi_1550 = 1.0 ;
	public static final double Ap_DaSi_1550 = 6e-18 ;
	public static final double Ep_DaSi_1550 = 1.0 ;
	
	public static double Dnsi_1550nm(double DN, double DP) {
		double termN = -8.85e-22 * DN ;
		double termP = -8.5e-18 * Math.pow(DP, 0.8) ;
		return termN + termP ;
	}
	
	public static double DalphaPerCmSi_1550nm(double DN, double DP) {
		double termN = 8.5e-18 * DN ;
		double termP = 6e-18 * DP ;
		return termN + termP ;
	}
	
	public static double DalphaDbPerCmSi_1550nm(double DN, double DP) {
		double termN = 8.5e-18 * DN ;
		double termP = 6e-18 * DP ;
		return (termN + termP) * 4.343 ;
	}

}
