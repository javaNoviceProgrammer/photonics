package photonics.heater.trans;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.interpolation.LinearInterpolation;
import mathLib.fourier.core.FFT;
import mathLib.numbers.Complex;
import mathLib.util.MathUtils;
import photonics.heater.impulse.ImpulseResponse1D_Modified_FFT;
import photonics.heater.struct.SelfHeating;
import photonics.heater.voltage.AbstractVoltage;

public class TransientResponse_FFT {

	ImpulseResponse1D_Modified_FFT impulse ;
	AbstractVoltage voltage ;
	SelfHeating selfH ;

	LinearInterpolation transientInterpolator ;
	public double[] transResponse  ;
	public double[] time_usec  ;
	int fftOrder = 17 ;

	public TransientResponse_FFT(
			@ParamName(name="Self Heating Model") SelfHeating selfH,
			@ParamName(name="Impulse Response") ImpulseResponse1D_Modified_FFT impulse,
			@ParamName(name="Heater Voltage") AbstractVoltage voltage
			){
		this.selfH = selfH ;
		this.impulse = impulse ;
		this.voltage = voltage ;
	}
	
	public void setFftOrder(int fftOrder){
		this.fftOrder = fftOrder ;
	}
	
	public void buildModel(int fftOrder){
		this.fftOrder = fftOrder ;
		buildModel();
	}
	
	public void buildModel(){
		double ts = 5e-9 ; // sampling time = 5nsec
		double fs = 1/ts ; // frequency cutoff
		int N = (int) Math.pow(2, fftOrder) ; // number of samples --> power of 2
		time_usec = new double[N] ;
		double[] freqImpulseReal = new double[N] ;
		double[] freqImpulseImag = new double[N] ;
		double[] freqTempReal = new double[N] ;
		double[] freqTempImag = new double[N] ;
		impulse.buildModel(fftOrder);

		for(int i=0; i<N; i++){
//			time_usec[i] = (i)*ts*1e6 ;
//			freqImpulseReal[i] = impulse.getTimeResponse(time_usec[i]) ;
			time_usec[i] = impulse.time_usec[i] ;
			freqImpulseReal[i] = impulse.Iwg[i] ;
			freqImpulseImag[i] = 0 ;
			freqTempReal[i] = selfH.getDeltaT(voltage.getVoltage(time_usec[i])) ;
			freqTempImag[i] = 0 ;
		}

		// zero padding to ensure that circular convolution does not mess up the final linear convolution
		freqTempReal = MathUtils.Arrays.concat(freqTempReal, MathUtils.Arrays.setZero(N)) ;
		freqTempImag = MathUtils.Arrays.concat(freqTempImag, MathUtils.Arrays.setZero(N)) ;

		freqImpulseReal = MathUtils.Arrays.concat(freqImpulseReal, MathUtils.Arrays.setZero(N)) ;
		freqImpulseImag = MathUtils.Arrays.concat(freqImpulseImag, MathUtils.Arrays.setZero(N)) ;

		FFT fft = new FFT(2*N) ;
		fft.fft(freqTempReal, freqTempImag); // take the fft of temperature of heater
		fft.fft(freqImpulseReal, freqImpulseImag); // take the fft of impulse response

		double[] freqTransReal = new double[2*N] ;
		double[] freqTransImag = new double[2*N] ;

		for(int i=0; i<2*N; i++){
			Complex arg0 = new Complex(freqTempReal[i], freqTempImag[i]) ;
			Complex arg1 = new Complex(freqImpulseReal[i], freqImpulseImag[i]) ;
			Complex arg2 = arg0.times(arg1) ;
			freqTransReal[i] = arg2.re() ;
			freqTransImag[i] = arg2.im() ;
		}

		fft.fft(freqTransImag, freqTransReal);
		transResponse = new double[N] ;
		for(int i=0; i<N; i++){
			transResponse[i] = freqTransReal[i] / (2*N*fs) ;
		}
		transientInterpolator = new LinearInterpolation(time_usec, transResponse) ;
	}

	public double getTimeResponse(double t_usec){
		if(t_usec <= 0 ){
			return 0 ;
		}
		else{
			return transientInterpolator.interpolate(t_usec) ;
		}
		
	}

	public double[] getTimeResponse(double[] t_usec){
		int M = t_usec.length ;
		double[] response = new double[M] ;
		for(int i=0; i<M; i++){
			response[i] = getTimeResponse(t_usec[i]) ;
		}
		return response ;
	}

//	//****** for test*******
//
//	public static void main(String[] args){
//		double f0 = 750 ;
//		double nu = 222 ;
//		ImpulseResponse1D_Modified_FFT impulse = new ImpulseResponse1D_Modified_FFT(f0, nu) ;
//		impulse.buildModel();
//
//		SelfHeating selfH = new SelfHeating(4.5e-3, 0.2, 850) ;
////		AbstractVoltage voltage = new StepVoltage(5, 0, 2) ;
//		AbstractVoltage voltage = new PulseTrainVoltage(0.1, 1/5e4*1e6, 8000, 0, 2) ;
//		TransientResponse_FFT trans = new TransientResponse_FFT(selfH, impulse, voltage) ;
//
//		int fftOrder = 16 ;
//		double ts = 5e-9 ; // sampling time = 5nsec
//		double fs = 1/ts ; // frequency cutoff
//		int N = (int) Math.pow(2, fftOrder) ; // number of samples --> power of 2
//		double deltaF = fs/N ; // frequency resolution
//		double[] freq_kHz = new double[N] ;
//		double[] time_usec = new double[N] ;
//		double[] freqImpulseReal = new double[N] ;
//		double[] freqImpulseImag = new double[N] ;
//		double[] freqTempReal = new double[N] ;
//		double[] freqTempImag = new double[N] ;
//
//		for(int i=0; i<N; i++){
//			double freqHz = i*deltaF ;
//			freq_kHz[i] = freqHz*1e-3 ;
//			time_usec[i] = (i)*ts*1e6 ;
//			freqImpulseReal[i] = impulse.getTimeResponse(time_usec[i]) ; // this is fft
//			freqImpulseImag[i] = 0 ; // this is fft
//			freqTempReal[i] = selfH.getDeltaT(voltage.getVoltage(time_usec[i])) ;
//			freqTempImag[i] = 0 ;
//		}
//
//		// zero padding to ensure that circular convolution does not mess up the final linear convolution
//		freqTempReal = MathUtils.Arrays.concat(freqTempReal, MathUtils.Arrays.setZero(N)) ;
//		freqTempImag = MathUtils.Arrays.concat(freqTempImag, MathUtils.Arrays.setZero(N)) ;
//
//		freqImpulseReal = MathUtils.Arrays.concat(freqImpulseReal, MathUtils.Arrays.setZero(N)) ;
//		freqImpulseImag = MathUtils.Arrays.concat(freqImpulseImag, MathUtils.Arrays.setZero(N)) ;
//
//		FFT fft = new FFT(2*N) ;
//		fft.fft(freqTempReal, freqTempImag); // take the fft of temperature of heater
//		fft.fft(freqImpulseReal, freqImpulseImag); // take the fft of impulse response
//
//		double[] freqTransReal = new double[2*N] ;
//		double[] freqTransImag = new double[2*N] ;
//
//		for(int i=0; i<2*N; i++){
//			Complex arg0 = new Complex(freqTempReal[i], freqTempImag[i]) ;
//			Complex arg1 = new Complex(freqImpulseReal[i], freqImpulseImag[i]) ;
//			Complex arg2 = arg0.times(arg1) ;
//			freqTransReal[i] = arg2.re() ;
//			freqTransImag[i] = arg2.im() ;
//		}
//
//		fft.fft(freqTransImag, freqTransReal);
//
//		double[] conv = new double[N] ;
//		double[] idealTemp = new double[N] ;
//		for(int i=0; i<N; i++){
////			conv[i] = 1.0/(N*fs) *(2*freqTempReal[i]-x0) ;
//			conv[i] = freqTransReal[i] / (2*N*fs) ;
//			idealTemp[i] = selfH.getDeltaT(voltage.getVoltage(time_usec[i]))*(nu-1)/nu ;
//		}
//
//
//		MatlabChart fig2 = new MatlabChart() ;
//		fig2.plot(time_usec, conv, "b");
//		fig2.plot(time_usec, idealTemp, "r");
//		fig2.RenderPlot();
//		fig2.run(true);
//
//
//	}
//
//	//**********************





}
