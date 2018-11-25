package photonics.pnjunc;

import ch.epfl.general_libraries.clazzes.ParamName;

// assuming a symmetric PN junction

public class PlasmaDispersionModel {
	
	private double DalphaPerCm ;
	private double DnSi ;
	private double DN ;
//	private boolean DalphaVaries ; 
//	private boolean DnSiVaries ;
//	private boolean DNVaries ;
	
	
	public PlasmaDispersionModel(
			@ParamName(name="Specify Index Change of Silicon") double DnSi,
			@ParamName(name="Is Index Change Variable?") boolean DnSiVaries,
			@ParamName(name="Specify Change of Loss (1/cm)") double DalphaPerCm,
			@ParamName(name="Is Change of Loss Variable?") boolean DalphaVaries,
			@ParamName(name="Specify Change of Carrier Density (1/cm^3)") double DN,
			@ParamName(name="Is Carrier Density Variable?") boolean DNVaries
			){
		if(DNVaries){
			this.DN = DN ;
			this.DalphaPerCm = getDalphaPerCmfromDN(DN) ;
			this.DnSi = getIndexChangefromDN(DN) ;
		}
		if(DalphaVaries){
			this.DalphaPerCm = DalphaPerCm ;
			this.DN = getDNfromDalphaPerCm(DalphaPerCm) ;
			this.DnSi = getIndexChangefromLossPerCm(DalphaPerCm) ;
		}
		if(DnSiVaries){
			this.DnSi = DnSi ;
			this.DalphaPerCm = getExcessLossFromIndexChange(DnSi, 10000) ;
			this.DN = getDNfromIndexChange(DnSi, 10000) ;
		}
	}
	
	// Getting parameters
	public double getDalphadBperCm(){
		return DalphaPerCm/0.23 ;
	}
	
	public double getDalphaPerCm(){
		return DalphaPerCm ;
	}
	
	public double getDalphaPerMeter(){
		return DalphaPerCm * 100 ;
	}
	
	public double getDN(){
		return DN ;
	}
	
	public double getDnSi(){
		return DnSi ;
	}

	// if DN is specified
	private double getDalphaPerCmfromDN(double DN){
		return  8.5e-18 * DN + 6e-18 * DN ;
	}
	
	private double getIndexChangefromDN(double DN){
		double a = 8.8e-22 * DN ;
		double b = 8.5e-18 * Math.pow(Math.abs(DN), 0.8) ;
//		return  -(8.8e-22 * DN + 8.5e-18 * Math.pow(DN, 0.8)) ;
		if(DN>=0){return -(a+b) ;}
		else{return a+b; }
	}
	
	// if Excess Loss is specified
	private double getIndexChangefromLossPerCm(double DalphaPerCm){
		double a = 1e-4*0.607 * DalphaPerCm ;
		double b = 1e-4 * 2.5138*Math.pow(Math.abs(DalphaPerCm), 0.8) ;
//		return  -1e-4*(0.607 * DalphaPerCm + 2.5138*Math.pow(DalphaPerCm, 0.8)) ;
		if(DalphaPerCm>0){return -(a+b); }
		else{return a+b; }
	}
	
	private double getDNfromDalphaPerCm(double DalphaPerCm){
		return DalphaPerCm/(14.5e-18) ;
	}
	
	// if Index change of silicon is specified
	private double getExcessLossFromIndexChange(double DnSi, int numSteps){
		double[] Dalpha = new double[numSteps] ;
		double DalphaMin = 0 ; 
		double DalphaMax = 100 ;
		double step = (DalphaMax-DalphaMin)/numSteps ;
		
		if(Math.abs(DnSi)<1e-6){
			return 0 ;
		}
		else{
			for (int i=0; i<Dalpha.length; i++){
				Dalpha[i] = DalphaMin + i*step ;
			}
			int k = 0 ;
			while(Math.abs(DnSi-getIndexChangefromLossPerCm(Dalpha[k]))>1e-6){
				k++ ;
			}
			if(k<numSteps){
				return Dalpha[k] ;
			}
			else{
				return Dalpha[numSteps-1] ;
			}
		}
	}
	
	private double getDNfromIndexChange(double DnSi, int numSteps){
		double Dalpha = getExcessLossFromIndexChange(DnSi, numSteps) ;
		return getDNfromDalphaPerCm(Dalpha) ;
	}

	
	// Need to add relation between voltage and DN ...
	
}
