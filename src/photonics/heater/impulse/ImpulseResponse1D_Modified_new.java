package photonics.heater.impulse;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import flanagan.integration.IntegralFunction;
import mathLib.integral.Integral1D;
import mathLib.numbers.Complex;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import photonics.heater.struct.HeaterWgCrossSection;

public class ImpulseResponse1D_Modified_new extends AbstractImpulseResponse {

	double xi_sio2 = 8.7e-7 ;
	double f0_hz, f3dB_kHz, nu, fmax_hz ;
	HeaterWgCrossSection crossSection ;
	int M = 10 ;

	double[] response  ;
	double[] time_usec  ;

	public ImpulseResponse1D_Modified_new(
			@ParamName(name="Cross Section") HeaterWgCrossSection crossSection
			){
		this.crossSection = crossSection ;
		double d_um = crossSection.getDistanceMicron() ;
		nu = crossSection.getDistanceFromSubstrateMicron()/crossSection.getDistanceMicron() ;
		f0_hz = xi_sio2 /(Math.PI * d_um*1e-6*d_um*1e-6) ;
		double f3dB_hz = f0_hz * (Math.log(2*nu/(nu-1)) * Math.log(2*nu/(nu-1))) ;
		f3dB_kHz = f3dB_hz/1e3 ;
		fmax_hz = M * M * f0_hz ;
	}

	public ImpulseResponse1D_Modified_new(
			@ParamName(name="f0 (kHz)") double f0_kHz,
			@ParamName(name= "nu (>1)") double nu
			){
		f0_hz = f0_kHz * 1e3 ;
		this.nu = nu ;
		double f3dB_hz = f0_hz * (Math.log(2*nu/(nu-1)) * Math.log(2*nu/(nu-1))) ;
		f3dB_kHz = f3dB_hz/1e3 ;
		double d_um = 1e6 * Math.sqrt((xi_sio2/Math.PI)/f0_hz) ;
		double Y0_um = d_um * nu ;
		this.crossSection = new HeaterWgCrossSection(5, 0.2, d_um, Y0_um) ;
		fmax_hz = M * M * f0_hz ;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put("xi_sio2", xi_sio2+"") ;
		map.put("f3dB (kHz)", f3dB_kHz+"") ;
		map.put("f0 (kHz)", getf0KHz()+"") ;
		map.put("model name", "1D Modified model") ;
		map.putAll(crossSection.getAllParameters()) ;
		return map ;
	}

	public double getf3dBKHz(){
		return f3dB_kHz ;
	}

	public double getf0KHz(){
		return f0_hz*1e-3 ;
	}

	public double getTimeResponse(double t_usec){
		if(t_usec <= 0){
			return 0 ;
		}
		else{
			double t_sec = t_usec * 1e-6 ;
			IntegralFunction func = new IntegralFunction() {
				@Override
				public double function(double f_hz) {
					double func1 = getComplexFreqResponse(f_hz).abs() ;
					double func2 = Math.cos(2*Math.PI*f_hz * t_sec + getComplexFreqResponse(f_hz).phase()) ;
					return (2*func1*func2) ;
				}
			};
			Integral1D integral = new Integral1D(func, 0, fmax_hz) ;
			integral.setErrorBound(1e-4);
			integral.setMaximumNumberOfIterations(50);
			integral.setNumPoints(10);
			double result = integral.getIntegral() ;
//			System.out.println(integral.getNumberOfIterations());
			return result ;
		}

	}

	public double[] getTimeResponse(double[] t_usec){
		int M = t_usec.length ;
		double[] result = new double[M] ;
		for(int i=0; i<M; i++){
			result[i] = getTimeResponse(t_usec[i]) ;
		}
		return result ;

	}

	public Complex getComplexFreqResponse(double freqHz){
		if(freqHz < 0){freqHz = -freqHz ;}
		if(freqHz == 0 ){return new Complex((nu-1)/nu, 0); }
		Complex f1 = new Complex (-Math.sqrt(freqHz/f0_hz), -Math.sqrt(freqHz/f0_hz)*Math.signum(freqHz)) ;
		Complex func1 = f1.exp() ;
		Complex one = new Complex(1,0) ;
		Complex f2 = f1.times(2*(nu-1)).exp() ;
		Complex func2 = one.minus(f2) ;
		Complex f3 = f1.times(2*nu).exp() ;
		Complex func3 = one.minus(f3) ;
		Complex response = func1.times(func2).divides(func3) ;
		return response ;
	}

	public double getFreqResponse(double freqHz){
		return getComplexFreqResponse(freqHz).abs() ;
	}

	public double[] getFreqResponse(double[] freqHz){
		double[] response = new double[freqHz.length] ;
		for(int i=0; i<freqHz.length; i++){
			response[i] = getFreqResponse(freqHz[i]) ;
		}
		return response ;
	}

	public double getFreqResponsedB(double freqHz){
		double val = getFreqResponse(freqHz) ;
		return 10*Math.log10(val) ;
	}

	public double[] getFreqResponsedB(double[] freqHz){
		double[] response = new double[freqHz.length] ;
		for(int i=0; i<freqHz.length; i++){
			response[i] = getFreqResponsedB(freqHz[i]) ;
		}
		return response ;
	}

	public double getFreqResponsePhaseRad(double freqHz){
		return getComplexFreqResponse(freqHz).phaseMinusPiToPi() ;
	}

	public double[] getfreqResponsePhaseRad(double[] freqHz){
		double[] response = new double[freqHz.length] ;
		for(int i=0; i<freqHz.length; i++){
			response[i] = getFreqResponsePhaseRad(freqHz[i]) ;
		}
		return response ;
	}

	public double getFreqResponsePhaseDegree(double freqHz){
		return getFreqResponsePhaseRad(freqHz)*180/Math.PI ;
	}

	public double[] getfreqResponsePhaseDegree(double[] freqHz){
		double[] response = new double[freqHz.length] ;
		for(int i=0; i<freqHz.length; i++){
			response[i] = getFreqResponsePhaseDegree(freqHz[i]) ;
		}
		return response ;
	}

	@Override
	public double getNormalizedImpulseResponse(double t_usec) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double[] getNormalizedImpulseResponse(double[] t_usec) {
		// TODO Auto-generated method stub
		return null;
	}

	//*************for test
	public static void main(String[] args){
		double f0 = 300 ;
		double nu = 2.3 ;
		ImpulseResponse1D_Modified_new impulse = new ImpulseResponse1D_Modified_new(f0, nu) ;
		double[] time = MathUtils.linspace(0,  10, 400) ;
		double[] Iwg = new double[time.length] ;
		for(int i=0; i<Iwg.length; i++){
			Iwg[i] = impulse.getTimeResponse(time[i]) ;
		}
		MatlabChart fig1 = new MatlabChart() ;
		fig1.plot(time, Iwg, "r");
		fig1.renderPlot();
		fig1.xlim(0, 10);
		fig1.run(true);
	}
	//*********************


}
