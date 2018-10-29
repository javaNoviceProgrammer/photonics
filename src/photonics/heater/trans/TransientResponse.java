package photonics.heater.trans;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.integration.IntegralFunction;
import flanagan.integration.Integration;
import flanagan.interpolation.LinearInterpolation;
import mathLib.util.MathUtils;
import photonics.heater.impulse.AbstractImpulseResponse;
import photonics.heater.struct.SelfHeating;
import photonics.heater.voltage.AbstractVoltage;

public class TransientResponse {

	AbstractImpulseResponse impulse ;
//	ImpulseResponseDataBase impulse ;
	AbstractVoltage voltage ;
	SelfHeating selfH ;
	
	public TransientResponse(
			@ParamName(name="Self Heating Model") SelfHeating selfH,
			@ParamName(name="Impulse Response") AbstractImpulseResponse impulse,
			@ParamName(name="Heater Voltage") AbstractVoltage voltage
			){
		this.selfH = selfH ;
		this.impulse = impulse ;
		this.voltage = voltage ;
	}
	
	// we set the integration adaptive 
	// by setting an upper bound on the 
	
	
//	public double getTimeResponse(double t_usec){
//		IntegralFunction func = new IntegralFunction() {
//			@Override
//			public double function(double tau_usec) {
//				double voltH = voltage.getVoltage(t_usec-tau_usec) ;
//				double arg = selfH.getDeltaT(voltH) * impulse.getTimeResponse(tau_usec);
//				return arg ;
//			}
//		};
//		AdaptiveIntegral integral = new AdaptiveIntegral(func, 0, t_usec*1e-6) ;
//		return integral.getIntegral() ;
//	}
	
	public double getTimeResponse(double t_usec){
		double error = 0.01 ;
		double result_final = 0 ;
		double result_temp = 10*error ;
		double[] T ;
		int M = 10 ;
		while(Math.abs(result_final-result_temp)>error){
			result_final = result_temp ;
			result_temp = 0 ;
			T = MathUtils.linspace(0, t_usec, M) ;
			for(int i=0; i<M-1; i++){
				result_temp += getInvervalIntegral(t_usec, T[i], T[i+1]) ;
			}
			M += 10 ;
		}
		
		return result_final ;
	}
	
	private double getInvervalIntegral(final double t_usec, double t_start_usec, double t_end_usec){
		
		IntegralFunction func = new IntegralFunction() {
			@Override
			public double function(double tau_usec) {
				double voltH = voltage.getVoltage(t_usec-tau_usec) ;
				double arg = selfH.getDeltaT(voltH) * impulse.getTimeResponse(tau_usec);
				return arg ;
			}
		};
		
		Integration transResponse = new Integration() ;
		transResponse.setIntegrationFunction(func);
		transResponse.setLimits(t_start_usec, t_end_usec);
		
		int points = 10 ;
		double result = transResponse.gaussQuad(points) * 1e-6 ;
		return result ;
	}
	
	public double[] getTimeResponse(double[] t_usec){
		int M = t_usec.length ;
		double[] response = new double[M] ;
		for(int i=0; i<M; i++){
			response[i] = getTimeResponse(t_usec[i]) ;
		}
		return response ;
	}
	
	//************* implementing methods to interpolate impulse response and do the calculation
	
//	public double getTimeResponse(double t_usec, double[] time_usec, double[] impulseResponseValues){
//		LinearInterpolation impulseInterpolate = new LinearInterpolation(time_usec, impulseResponseValues) ;
//		IntegralFunction func = new IntegralFunction() {
//			@Override
//			public double function(double tau_usec) {
//				double voltH = voltage.getVoltage(t_usec-tau_usec) ;
//				double arg = selfH.getDeltaT(voltH) * impulseInterpolate.interpolate(tau_usec);
//				return arg*1e-6 ;
//			}
//		};
//		AdaptiveIntegral integral = new AdaptiveIntegral(func, 0, t_usec) ;
//		return integral.getIntegral() ;
//	}
	
	
	
	public double getTimeResponse(double t_usec, double[] time_usec, double[] impulseResponseValues){
		// first interpolate the impulse response
		LinearInterpolation impulseInterpolate = new LinearInterpolation(time_usec, impulseResponseValues) ;
			double error = 0.01 ;
			double result_final = 0 ;
			double result_temp = 10*error ;
			double[] T ;
			int M = 10 ;
			while(Math.abs(result_final-result_temp)>error){
				result_final = result_temp ;
				result_temp = 0 ;
				T = MathUtils.linspace(0, t_usec, M) ;
				for(int i=0; i<M-1; i++){
					result_temp += getInvervalIntegral(t_usec, T[i], T[i+1], impulseInterpolate) ;
				}
				M += 10 ;
			}
			
			return result_final ;
	}
	
	private double getInvervalIntegral(final double t_usec, double t_start_usec, double t_end_usec, LinearInterpolation impulseInterpolate){
		
		IntegralFunction func = new IntegralFunction() {
			@Override
			public double function(double tau_usec) {
				double voltH = voltage.getVoltage(t_usec-tau_usec) ;
				double arg = selfH.getDeltaT(voltH) * impulseInterpolate.interpolate(tau_usec);
				return arg ;
			}
		};
		
		Integration transResponse = new Integration() ;
		transResponse.setIntegrationFunction(func);
		transResponse.setLimits(t_start_usec, t_end_usec);
		
		int points = 10 ;
		double result = transResponse.gaussQuad(points) * 1e-6 ;
		return result ;
	}
	
	public double[] getTimeResponse(double[] t_usec, double[] time_usec, double[] impulseResponseValues){
		int M = t_usec.length ;
		double[] result = new double[M] ;
		for(int i=0; i<M; i++){
			result[i] = getTimeResponse(t_usec[i], time_usec, impulseResponseValues) ;
		}
		return result ;
	}
	
	
	
	
	
}
