package photonics.heater.impulse;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import flanagan.interpolation.CubicSpline;
import mathLib.fourier.core.FFT;
import mathLib.numbers.Complex;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import photonics.heater.struct.HeaterWgCrossSection;

public class ImpulseResponse1D_Modified_FFT extends AbstractImpulseResponse {

	double xi_sio2 = 8.7e-7 ;
	double f0_hz, f3dB_kHz, nu, fmax_hz ;
	HeaterWgCrossSection crossSection ;

	CubicSpline IwgInterpolator ;
	public double[] Iwg  ;
	public double[] time_usec  ;
	int fftOrder = 17 ;

	public ImpulseResponse1D_Modified_FFT(
			@ParamName(name="Cross Section") HeaterWgCrossSection crossSection
			){
		this.crossSection = crossSection ;
		double d_um = crossSection.getDistanceMicron() ;
		nu = crossSection.getDistanceFromSubstrateMicron()/crossSection.getDistanceMicron() ;
		f0_hz = xi_sio2 /(Math.PI * d_um*1e-6*d_um*1e-6) ;
		double f3dB_hz = f0_hz * (Math.log(2*nu/(nu-1)) * Math.log(2*nu/(nu-1))) ;
		f3dB_kHz = f3dB_hz/1e3 ;
	}

	public ImpulseResponse1D_Modified_FFT(
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

	public void setFftOrder(int fftOrder){
		this.fftOrder = fftOrder ;
	}

	public double getf3dBKHz(){
		return f3dB_kHz ;
	}

	public double getf0KHz(){
		return f0_hz*1e-3 ;
	}

	public void buildModel(){
		calculateInterpolator();
	}

	public void buildModel(int fftOrder){
		this.fftOrder = fftOrder ;
		calculateInterpolator();
	}

	private void calculateInterpolator(){
		double ts = 5e-9 ; // sampling time = 5nsec
		double fs = 1/ts ; // frequency cutoff
		int N = (int) Math.pow(2, fftOrder) ; // number of samples --> power of 2
		double deltaF = fs/N ; // frequency resolution
		time_usec = new double[N] ;
		double[] freqResReal = new double[N] ;
		double[] freqResImag = new double[N] ;
		for(int i=0; i<N; i++){
			double freqHz = i*deltaF ;
			time_usec[i] = i*ts*1e6 ;
			freqResReal[i] = getComplexFreqResponse(freqHz).re()  ;
			freqResImag[i] = getComplexFreqResponse(freqHz).im()  ;
		}
		FFT fft = new FFT(N) ;
		fft.fft(freqResImag, freqResReal);

		double[] IwgFFT = new double[N] ;
		for(int i=0; i<N; i++){
			IwgFFT[i] = 1/(N*ts) * (2*freqResReal[i]-(nu-1)/nu) ;
			if(i>=2){IwgFFT[i] = IwgFFT[i]-IwgFFT[1]; }
		}
		Iwg = IwgFFT ;
//		IwgInterpolator = new LinearInterpolation(time_usec, IwgFFT) ;
		IwgInterpolator = new CubicSpline(time_usec, IwgFFT) ;

	}

	public double getTimeResponse(double t_usec){
		if(t_usec <= 0){
			return 0 ;
		}
		else{
			return IwgInterpolator.interpolate(t_usec) ;
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
		double Iwg_max = MathUtils.Arrays.FindMaximum.getValue(Iwg) ;
		return IwgInterpolator.interpolate(t_usec)/Iwg_max ;
	}

	public double[] getNormalizedImpulseResponse(double[] t_usec) {
		double Iwg_max = MathUtils.Arrays.FindMaximum.getValue(Iwg) ;
		double[] Iwg_normalized = new double[t_usec.length] ;
		for(int i=0; i<t_usec.length; i++){
			Iwg_normalized[i] = IwgInterpolator.interpolate(t_usec[i])/Iwg_max ;
		}
		return Iwg_normalized ;
	}

	//*************for test
	public static void main(String[] args){
		double f0 = 30 ;
		double nu = 1.325 ;
		ImpulseResponse1D_Modified_FFT impulse = new ImpulseResponse1D_Modified_FFT(f0, nu) ;
		impulse.buildModel();
		double[] time_usec = impulse.time_usec ;
		double[] Iwg = impulse.Iwg ;
		MatlabChart fig = new MatlabChart() ;
		fig.plot(time_usec, Iwg);
		fig.renderPlot();
		fig.run(true);

	}
	//*********************


}
