package photonics.heater.pc;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sqrt;
import static mathLib.util.MathUtils.linspace;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootFunction;
import mathLib.plot.MatlabChart;
import mathLib.root.RealRootFinder;
import photonics.heater.struct.SelfHeating;

public class HeaterPhotoConductance {
	
	public SelfHeating selfHeating ;
	public PhotoConductance photoConductance ;
	public RingModel ring ;
	public Laser laser ;
	public double V_volt ;
	
	public HeaterPhotoConductance(
			SelfHeating selfHeating,
			PhotoConductance photoConductance,
			RingModel ring,
			Laser laser,
			@ParamName(name="Voltage (V)") double V_volt
			){
		this.selfHeating = selfHeating ;
		this.photoConductance = photoConductance ;
		this.ring = ring ;
		this.laser = laser ;
		this.V_volt = V_volt ;
	}
	
	private double getI_mA(double V_volt, double P_mW){
		double factor = 1 + photoConductance.kp1_per_mW * P_mW + photoConductance.kp2_per_mW_squared* P_mW*P_mW ;
		double num = V_volt/(selfHeating.Rlinear*1e-3) * 2 * factor ;
		double denum = 1 + sqrt(1 + selfHeating.Kv * V_volt*V_volt * factor) ;
		return (num/denum) ;
	}
	
	private double getInsideTrans(double lambda_nm, double V_volt, double I_mA){
		double num = ring.kIn*ring.kIn ;
		double phase = 2*PI*(lambda_nm-ring.resLambda_nm-ring.TOeff_nm_per_mW * V_volt * I_mA)/ring.FSR_nm ;
		double xi = ring.tIn * ring.tOut * sqrt(ring.L) ;
		double denom = 1+xi*xi-2*xi*cos(phase) ;
		return (num/denom) ;
	}
	
	private double getInsidePower_mW(double lambda_nm, double V_volt, double I_mA){
		return laser.Pin_mW * getInsideTrans(lambda_nm, V_volt, I_mA) ;
	}
	
	private double getDropTrans(double lambda_nm, double V_volt, double I_mA){
		double num = ring.kIn*ring.kIn*ring.kOut*ring.kOut*sqrt(ring.L) ;
		double phase = 2*PI*(lambda_nm - ring.resLambda_nm - ring.TOeff_nm_per_mW * V_volt * I_mA)/ring.FSR_nm ;
		double xi = ring.tIn * ring.tOut * sqrt(ring.L) ;
		double denom = 1+xi*xi-2*xi*cos(phase) ;
		return (num/denom) ;
	}
	
	public double getDropTrans(){
		return getDropTrans(laser.lambdaLaser_nm, V_volt, getI_mA()) ;
	}
	
	public double getI_mA(){
		// need to solve the equation
		RealRootFunction func1 = new RealRootFunction() {
			
			@Override
			public double function(double I_mA) {
				double LHS = I_mA ;
				double RHS = getI_mA(V_volt, getInsidePower_mW(laser.lambdaLaser_nm, V_volt, I_mA)) ;
				return (LHS-RHS);
			}
		};
		
		RealRoot root = new RealRoot() ;
		root.setEstimate(0);
		return root.bisect(func1, -1, 100) ;
	}
	
	public double getInsidePower_mW(){
		return getInsidePower_mW(laser.lambdaLaser_nm, V_volt, getI_mA()) ;
	}
	
	public double[] getI_mA_allValues(){
		// need to solve the equation
		RealRootFunction func1 = new RealRootFunction() {
			
			@Override
			public double function(double I_mA) {
				double LHS = I_mA ;
				double RHS = getI_mA(V_volt, getInsidePower_mW(laser.lambdaLaser_nm, V_volt, I_mA)) ;
				return (LHS-RHS);
			}
		};
		
		RealRootFinder root = new RealRootFinder(func1, -1, 100) ;
		root.findAllRoots();
		double[] allRoots = root.getAllRoots() ;
		if(allRoots.length == 1){
			return new double[] {allRoots[0], 0, 0} ;
		}
		else if(allRoots.length == 2){
			return new double[] {allRoots[0], allRoots[1], 0} ;
		}
		else{
			return new double[] {allRoots[0], allRoots[1], allRoots[2]} ;
		}
	}
	
	
	// for test
//	public static void main(String[] args){
//		SelfHeating selfHeating = new SelfHeating(4.5e-3, 0.6, 161) ;
//		PhotoConductance photoConductance = new PhotoConductance(5e-3, 0) ;
//		RingModel ringModel = new RingModel(0.42, 0.42, 1, 13.13, 1550, 0.26) ;
//		Laser laser = new Laser(1, 1545) ;
//		double[] voltage_V = linspace(0, 10, 10000);
//		double[] I_mA = new double[voltage_V.length] ;
//		double[] dlambda_nm = new double[voltage_V.length] ;
//		for(int i=0; i<voltage_V.length; i++){
//			HeaterPhotoConductance heater = new HeaterPhotoConductance(selfHeating, photoConductance, ringModel, laser, voltage_V[i]) ;
//			I_mA[i] = heater.getI_mA() ;
//			dlambda_nm[i] = heater.ring.TOeff_nm_per_mW * I_mA[i] * voltage_V[i] ;
//		}
//		MatlabChart fig = new MatlabChart() ;
//		fig.plot(voltage_V, selfHeating.getCurrent_mA(voltage_V), "b");
//		fig.plot(voltage_V, I_mA, "r");
//		fig.RenderPlot();
//		fig.xlabel("Heater Voltage (V)");
//		fig.ylabel("Heater Current (mA)");
//		fig.run(true);
//		
//		MatlabChart fig1 = new MatlabChart() ;
//		fig1.plot(voltage_V, Arrays.minus(I_mA, selfHeating.getCurrent_mA(voltage_V)), "b");
//		fig1.RenderPlot();
//		fig1.run(true);
//		
//	}
	
	public static void main(String[] args){
		SelfHeating selfHeating = new SelfHeating(4.5e-3, 0.6, 161) ;
		PhotoConductance photoConductance = new PhotoConductance(5e-3, 0) ;
		RingModel ringModel = new RingModel(0.42, 0.42, 1, 13.13, 1550, 0.26) ;
		Laser laser = new Laser(10, 1545) ;
		double[] voltage_V = linspace(0, 10, 2000);
		double[] I_mA = new double[voltage_V.length] ;
		double[] I_mA_2 = new double[voltage_V.length] ;
		double[] I_mA_3 = new double[voltage_V.length] ;
		double[] dlambda_nm = new double[voltage_V.length] ;
		for(int i=0; i<voltage_V.length; i++){
			I_mA[i] = 0 ;
			I_mA_2[i] = 0 ;
			I_mA_3[i] = 0 ;
			HeaterPhotoConductance heater = new HeaterPhotoConductance(selfHeating, photoConductance, ringModel, laser, voltage_V[i]) ;
			double[] allRoots = heater.getI_mA_allValues() ;
			if(allRoots.length == 1){
				I_mA[i] = heater.getI_mA_allValues()[0] ;
				System.out.println(I_mA[i]);
			}
			else if(allRoots.length == 2){
				I_mA[i] = heater.getI_mA_allValues()[0] ;
				I_mA_2[i] = heater.getI_mA_allValues()[1] ;
				System.out.println(I_mA[i] + "  , " + I_mA_2[i]);
			}
			else{
				I_mA[i] = heater.getI_mA_allValues()[0] ;
				I_mA_2[i] = heater.getI_mA_allValues()[1] ;
				I_mA_3[i] = heater.getI_mA_allValues()[2] ;
				System.out.println(I_mA[i] + "  , " + I_mA_2[i] + "  ,  " + I_mA_3[i]);
			}
			dlambda_nm[i] = heater.ring.TOeff_nm_per_mW * I_mA[i] * voltage_V[i] ;
		}
		MatlabChart fig = new MatlabChart() ;
		fig.plot(voltage_V, selfHeating.getCurrent_mA(voltage_V), "b");
		fig.plot(voltage_V, I_mA, "r");
		fig.plot(voltage_V, I_mA_2, "g");
		fig.plot(voltage_V, I_mA_3, "k");
		fig.renderPlot();
		fig.xlabel("Heater Voltage (V)");
		fig.ylabel("Heater Current (mA)");
		fig.run(true);
		
		
	}
	

}
