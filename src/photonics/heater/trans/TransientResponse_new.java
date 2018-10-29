package photonics.heater.trans;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.integration.IntegralFunction;
import flanagan.interpolation.LinearInterpolation;
import mathLib.integral.Integral1D;
import photonics.heater.impulse.AbstractImpulseResponse;
import photonics.heater.struct.SelfHeating;
import photonics.heater.voltage.AbstractVoltage;

public class TransientResponse_new {

	AbstractImpulseResponse impulse ;
	AbstractVoltage voltage ;
	SelfHeating selfH ;

	public TransientResponse_new(
			@ParamName(name="Self Heating Model") SelfHeating selfH,
			@ParamName(name="Impulse Response") AbstractImpulseResponse impulse,
			@ParamName(name="Heater Voltage") AbstractVoltage voltage
			){
		this.selfH = selfH ;
		this.impulse = impulse ;
		this.voltage = voltage ;
	}

	public double getTimeResponse(double t_usec){
		IntegralFunction func = new IntegralFunction() {
			@Override
			public double function(double tau_usec) {
				double voltH = voltage.getVoltage(t_usec-tau_usec) ;
				double arg = selfH.getDeltaT(voltH) * impulse.getTimeResponse(tau_usec);
				return arg*1e-6 ;
			}
		};
		Integral1D integral = new Integral1D(func, 0, t_usec) ;
//		integral.setErrorBound(1e-4);
//		integral.setMaximumNumberOfIterations(10);
//		integral.setNumPoints(10);
		return integral.getIntegral() ;
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

	public double getTimeResponse(double t_usec, double[] time_usec, double[] impulseResponseValues){
		LinearInterpolation impulseInterpolate = new LinearInterpolation(time_usec, impulseResponseValues) ;
		IntegralFunction func = new IntegralFunction() {
			@Override
			public double function(double tau_usec) {
				double voltH = voltage.getVoltage(t_usec-tau_usec) ;
				double arg = selfH.getDeltaT(voltH) * impulseInterpolate.interpolate(tau_usec);
				return arg*1e-6 ;
			}
		};
		Integral1D integral = new Integral1D(func, 0, t_usec) ;
//		integral.setErrorBound(1e-4);
		integral.setMaximumNumberOfIterations(20);
//		integral.setNumPoints(5);
		return integral.getIntegral() ;
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
