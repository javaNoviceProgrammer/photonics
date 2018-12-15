package photonics.wg.coupling;

import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.numbers.Complex;
import photonics.util.Wavelength;

//This class is for 450nmX220nm Strip waveguides

public class DistributedCouplerStripWg {

	/**
	 * This is for a strip waveguide with 450nm X 220 nm cross-section
	 */

	public double lengthMicron ;
	public double gapNm ;
	public double lambdaNm ;
	public Wavelength inputLambda ;
	double neffEven = Double.NaN , neffOdd = Double.NaN ;

	public Complex port1, port2, port3, port4 ;
	Complex port1_accumulated, port2_accumulated, port3_accumulated, port4_accumulated ;

	public Complex S11, S21, S31, S41 ;
	public Complex S12, S22, S32, S42 ;
	public Complex S13, S23, S33, S43 ;
	public Complex S14, S24, S34, S44 ;

	public DistributedCouplerStripWg(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Length (um)") double lengthMicron,
			@ParamName(name="Gap (nm)") double gapNm
			){
		this.inputLambda = inputLambda ;
		this.lambdaNm = inputLambda.getWavelengthNm() ;
		this.lengthMicron = lengthMicron ;
		this.gapNm = gapNm ;

		initializePorts();
		calculateScattParams();
	}


	public void initializePorts(){
		port1 = port2 = port3 = port4 = Complex.ZERO ;
		port1_accumulated = port2_accumulated = port3_accumulated = port4_accumulated = Complex.ZERO ;
	}

	public void calculateScattParams(){
		this.S11 = Complex.ZERO ;
		this.S21 = getS21() ;
		this.S31 = getS31() ;
		this.S41 = Complex.ZERO ;

		this.S12 = getS21() ;
		this.S22 = Complex.ZERO ;
		this.S32 = Complex.ZERO ;
		this.S42 = getS31() ;

		this.S13 = getS31() ;
		this.S23 = Complex.ZERO ;
		this.S33 = Complex.ZERO ;
		this.S43 = getS21() ;

		this.S14 = Complex.ZERO ;
		this.S24 = getS31() ;
		this.S34 = getS21() ;
		this.S44 = Complex.ZERO ;
	}

	public void connectPorts(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		port1 = getPort1(port1In, port2In, port3In, port4In) ;
		port2 = getPort2(port1In, port2In, port3In, port4In) ;
		port3 = getPort3(port1In, port2In, port3In, port4In) ;
		port4 = getPort4(port1In, port2In, port3In, port4In) ;
		port1_accumulated = port1_accumulated.plus(port1) ;
		port2_accumulated = port2_accumulated.plus(port2) ;
		port3_accumulated = port3_accumulated.plus(port3) ;
		port4_accumulated = port4_accumulated.plus(port4) ;
	}

	public Complex getPort1(){
		return port1_accumulated ;
	}

	public Complex getPort2(){
		return port2_accumulated ;
	}

	public Complex getPort3(){
		return port3_accumulated ;
	}

	public Complex getPort4(){
		return port4_accumulated ;
	}

	public void setNeffEven(double neffEven){
		this.neffEven = neffEven ;
	}

	public void setNeffOdd(double neffOdd){
		this.neffOdd = neffOdd ;
	}

	// First I need to read gap and wavelength
	// Then I need to determine the wavelength interval and weights
	// Then I need to calculate the even & odd effective indexes based on gap interpolations and weights on the wavelengths
	// Finally I need to interpolate the wavelength dependence for a dense simulation

	public double getEffectiveIndexEven(){
		if(Double.isNaN(neffEven)){
			double[] coeffs = getCoeffs(true) ;
			double nEffEven = 0 ;
			for(int i=0; i<9; i++){
				nEffEven += coeffs[i] * Math.pow(gapNm, 8-i) ;
			}
			return nEffEven ;
		}
		else{
			return neffEven ;
		}

	}

	public double getEffectiveIndexOdd(){
		if(Double.isNaN(neffOdd)){
			double[] coeffs = getCoeffs(false) ;
			double nEffOdd = 0 ;
			for(int i=0; i<9; i++){
				nEffOdd += coeffs[i] * Math.pow(gapNm, 8-i) ;
			}
			return nEffOdd ;
		}
		else{
			return neffOdd ;
		}

	}

	public double getBetaPlus(){
		double lambdaMeter = inputLambda.getWavelengthMeter() ;
		double neffEven = getEffectiveIndexEven() ;
		double neffOdd = getEffectiveIndexOdd() ;
		double betaPlus = (2*Math.PI/lambdaMeter) * (neffEven + neffOdd)/2 ;
		return betaPlus ;
	}

	public double getBetaMinus(){
		double lambdaMeter = inputLambda.getWavelengthMeter() ;
		double neffEven = getEffectiveIndexEven() ;
		double neffOdd = getEffectiveIndexOdd() ;
		double betaPlus = (2*Math.PI/lambdaMeter) * (neffEven - neffOdd)/2 ;
		return betaPlus ;
	}

	private Complex getS21(){
		double alpha = 0 ; // this is for electric field
		Complex phi_plus = new Complex(getBetaPlus() * lengthMicron*1e-6, -alpha*lengthMicron*1e-6) ;
		Complex T1 = phi_plus.times(new Complex(0,-1)).exp() ;
		double phi_minus = getBetaMinus() * lengthMicron*1e-6 ;
		Complex C = new Complex(Math.cos(phi_minus),0) ;
		Complex S21 = T1.times(C) ;
		return S21 ;

	}

	private Complex getS31(){
		double alpha = 0 ; // this is for electric field
		Complex phi_plus = new Complex(getBetaPlus() * lengthMicron*1e-6, -alpha*lengthMicron*1e-6) ;
		Complex T1 = phi_plus.times(new Complex(0,-1)).exp() ;
		double phi_minus = getBetaMinus() * lengthMicron*1e-6 ;
		Complex S = new Complex(0,-Math.sin(phi_minus)) ;
		Complex S31 = T1.times(S) ;
		return S31 ;
	}

	public Complex getPort1(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S11) ;
		Complex T2 = port2In.times(S12) ;
		Complex T3 = port3In.times(S13) ;
		Complex T4 = port4In.times(S14) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	public Complex getPort2(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S21) ;
		Complex T2 = port2In.times(S22) ;
		Complex T3 = port3In.times(S23) ;
		Complex T4 = port4In.times(S24) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	public Complex getPort3(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S31) ;
		Complex T2 = port2In.times(S32) ;
		Complex T3 = port3In.times(S33) ;
		Complex T4 = port4In.times(S34) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	public Complex getPort4(Complex port1In, Complex port2In, Complex port3In, Complex port4In){
		Complex T1 = port1In.times(S41) ;
		Complex T2 = port2In.times(S42) ;
		Complex T3 = port3In.times(S43) ;
		Complex T4 = port4In.times(S44) ;
		return T1.plus(T2).plus(T3).plus(T4) ;
	}

	//*************************************

	public double[] getCoeffs(boolean isEven){
		double lambda_min_interval = 0 ;
//		double lambda_max_interval = 0 ;
		double lambdaMin = 1500 ;
		double lambdaMax = 1600 ;
		double DlambdaNm = 2.5 ; // resolution is 2.5 nm
		int N = 41 ;
		int intervalIndex = 0 ;
		double[] lambdaArray = new double[N] ;
		for(int i=0; i<N; i++){
			lambdaArray[i] = lambdaMin + i * DlambdaNm ;
		}
		if(lambdaNm >= lambdaMax){
			lambda_min_interval = lambdaArray[N-2] ;
//			lambda_max_interval = lambdaArray[N-1] ;
			intervalIndex = N-1 ;
		}
		else if(lambdaNm <= lambdaMin){
			lambda_min_interval = lambdaArray[0] ;
//			lambda_max_interval = lambdaArray[1] ;
			intervalIndex = 1 ;
		}
		else{
			for(int i=0; i<N-1; i++){
				if(lambdaNm >= lambdaArray[i] && lambdaNm < lambdaArray[i+1] ){
					lambda_min_interval = lambdaArray[i] ;
//					lambda_max_interval = lambdaArray[i+1] ;
					intervalIndex = i+1 ;
				}
			}
		}
		double weight = 1 - ((lambdaNm-lambda_min_interval)/(DlambdaNm)) ;

		double[] coeffs = new double[9] ;
		if(isEven){
			for(int i=0; i<9; i++){
			coeffs[i] = weight*getCoeffsFromDataBase(intervalIndex, true)[i] + (1-weight)*getCoeffsFromDataBase(intervalIndex+1, true)[i] ;
			}
		}
		else{
			for(int i=0; i<9; i++){
			coeffs[i] = weight*getCoeffsFromDataBase(intervalIndex, false)[i] + (1-weight)*getCoeffsFromDataBase(intervalIndex+1, false)[i] ;
			}
		}

		return coeffs ;
	}

	// DataBase from the fitted curves (index vs. gap size) simulated in COMSOL --> 8-degree polynomial (A8*x^8 + ... + A1*x + A0)
	public double[] getCoeffsFromDataBase(int curveIndex, boolean isEven){
		if(curveIndex==1){
			if(isEven){return new double[] {2.965629597e-21, -7.280712806e-18, 7.631076863e-15, -4.464377308e-12, 0.000000001601136847, -0.0000003645635475, 0.00005289093942, -0.004761503845, 2.652300897};}
			else{return new double[] {2.355058865e-23, -7.975201845e-20, 1.151151775e-16, -9.274540782e-14, 4.528097966e-11, -0.00000001323629609, 0.00000194066208, 0.000003223364635, 2.389239764};}
		}
		else if(curveIndex==2){
			if(isEven){return new double[] {2.971726406e-21, -7.296138127e-18, 7.647897787e-15, -4.474759412e-12, 0.00000000160514942, -0.0000003655819073, 0.0000530652022, -0.004781821842, 2.650635919}; }
			else{return new double[] {2.318465411e-23, -7.88611991e-20, 1.142300382e-16, -9.230830004e-14, 4.519122088e-11, -0.00000001324573908, 0.000001947819531, 0.000002970513884, 2.385893109}; }
		}
		else if(curveIndex==3){
			if(isEven){return new double[] {2.977717222e-21, -7.311307363e-18, 7.664455174e-15, -4.484990892e-12, 0.00000000160910981, -0.0000003665891062, 0.00005323806406, -0.004802062804, 2.64897274}; }
			else{return new double[] {2.284256314e-23, -7.802079585e-20, 1.133886433e-16, -9.189118929e-14, 4.510664901e-11, -0.00000001325599727, 0.000001955075773, 0.000002710551107, 2.382544016}; }
		}
		else if(curveIndex==4){
			if(isEven){return new double[] {2.983607344e-21, -7.326227689e-18, 7.680751143e-15, -4.495070399e-12, 0.000000001613016868, -0.000000367584802, 0.00005340947431, -0.004822222957, 2.647311269}; }
			else{return new double[] {2.251344906e-23, -7.720916642e-20, 1.12573259e-16, -9.148634533e-14, 4.502532113e-11, -0.00000001326678186, 0.000001962405103, 0.000002444849107, 2.379192445}; }
		}
		else if(curveIndex==5){
			if(isEven){return new double[] {2.989406177e-21, -7.340926938e-18, 7.696818826e-15, -4.505018697e-12, 0.000000001616878051, -0.000000368570549, 0.00005357961322, -0.004842312572, 2.645651733}; }
			else{return new double[] {2.216396762e-23, -7.635348186e-20, 1.117179798e-16, -9.106175961e-14, 4.493820332e-11, -0.00000001327659587, 0.000001969668397, 0.000002179737703, 2.375838293}; }
		}
		else if(curveIndex==6){
			if(isEven){return new double[] {2.995115445e-21, -7.355403955e-18, 7.712651866e-15, -4.514829579e-12, 0.000000001620690526, -0.0000003695456501, 0.00005374838644, -0.004862325008, 2.643993962}; }
			else{return new double[] {2.183478084e-23, -7.554075623e-20, 1.108997182e-16, -9.065372245e-14, 4.485515408e-11, -0.00000001328698738, 0.00000197699869, 0.000001909836498, 2.372481622}; }
		}
		else if(curveIndex==7){
			if(isEven){return new double[] {3.000701483e-21, -7.369583688e-18, 7.728179712e-15, -4.524466671e-12, 0.000000001624443098, -0.0000003705080137, 0.0000539155638, -0.00488224669, 2.642337652}; }
			else{return new double[] {2.150425268e-23, -7.47240782e-20, 1.100764501e-16, -9.024217012e-14, 4.477068859e-11, -0.00000001329710153, 0.000001984325517, 0.000001637625627, 2.36912241}; }
		}
		else if(curveIndex==8){
			if(isEven){return new double[] {3.006205142e-21, -7.383560645e-18, 7.743494984e-15, -4.533979878e-12, 0.000000001628151792, -0.0000003714607364, 0.0000540814902, -0.00490209724, 2.640683241}; }
			else{return new double[] {2.116599725e-23, -7.388944534e-20, 1.092352598e-16, -8.982055297e-14, 4.468280148e-11, -0.00000001330655624, 0.000001991604709, 0.000001365843735, 2.365760592}; }
		}
		else if(curveIndex==9){
			if(isEven){return new double[] {3.011620188e-21, -7.39731827e-18, 7.758578557e-15, -4.543356881e-12, 0.00000000163181186, -0.0000003724027186, 0.00005424601788, -0.004921866214, 2.639030457}; }
			else{return new double[] {2.082375553e-23, -7.304633225e-20, 1.083860835e-16, -8.939449178e-14, 4.459336182e-11, -0.00000001331572377, 0.000001998879294, 0.000001091907188, 2.362396219}; }
		}
		else if(curveIndex==10){
			if(isEven){return new double[] {3.016950286e-21, -7.410863538e-18, 7.773436139e-15, -4.552600416e-12, 0.000000001635424177, -0.0000003733341463, 0.00005440917031, -0.004941554894, 2.63737933}; }
			else{return new double[] {2.049847671e-23, -7.224181746e-20, 1.075733079e-16, -8.898676179e-14, 4.450926465e-11, -0.00000001332585125, 0.000002006279221, 0.0000008087601182, 2.35902944}; }
		}
		else if(curveIndex==11){
			if(isEven){return new double[] {3.022174441e-21, -7.424152668e-18, 7.788029728e-15, -4.561692584e-12, 0.000000001638983722, -0.0000003742541533, 0.00005457085564, -0.004961157665, 2.635729732}; }
			else{return new double[] {2.017391213e-23, -7.143566001e-20, 1.06755023e-16, -8.857355045e-14, 4.442250886e-11, -0.00000001333533064, 0.000002013619137, 0.0000005272818676, 2.355660015}; }
		}
		else if(curveIndex==12){
			if(isEven){return new double[] {3.027307214e-21, -7.437215876e-18, 7.802385531e-15, -4.570645769e-12, 0.000000001642493985, -0.0000003751633371, 0.00005473113324, -0.00498067736, 2.63408172}; }
			else{return new double[] {1.985365011e-23, -7.063869711e-20, 1.059446278e-16, -8.816371437e-14, 4.433649569e-11, -0.00000001334492918, 0.000002020996274, 0.0000002413549311, 2.352288078}; }
		}
		else if(curveIndex==13){
			if(isEven){return new double[] {3.032343773e-21, -7.450044372e-18, 7.816497135e-15, -4.579457564e-12, 0.000000001645954454, -0.0000003760616259, 0.00005488999329, -0.005000112733, 2.632435248}; }
			else{return new double[] {1.952763135e-23, -6.983109978e-20, 1.051259852e-16, -8.775020145e-14, 4.424942232e-11, -0.00000001335437354, 0.000002028387672, -0.00000004821366858, 2.348913627}; }
		}
		else if(curveIndex==14){
			if(isEven){return new double[] {3.037268886e-21, -7.462602911e-18, 7.83033051e-15, -4.58811011e-12, 0.00000000164935957, -0.0000003769479705, 0.00005504731865, -0.005019456701, 2.630790161}; }
			else{return new double[] {1.920591192e-23, -6.903023931e-20, 1.043102104e-16, -8.733588563e-14, 4.416127185e-11, -0.00000001336349328, 0.000002035756714, -0.0000003382863145, 2.345536564}; }
		}
		else if(curveIndex==15){
			if(isEven){return new double[] {3.0421375e-21, -7.475012606e-18, 7.843996637e-15, -4.596658266e-12, 0.000000001652725214, -0.0000003778251014, 0.00005520337719, -0.00503872245, 2.629146712}; }
			else{return new double[] {1.889235825e-23, -6.824718172e-20, 1.03510291e-16, -8.692889318e-14, 4.407499571e-11, -0.00000001337292414, 0.000002043183057, -0.0000006342325038, 2.342157037}; }
		}
		else if(curveIndex==16){
			if(isEven){return new double[] {3.046884905e-21, -7.487131763e-18, 7.857366471e-15, -4.605038597e-12, 0.000000001656033102, -0.0000003786898747, 0.00005535785537, -0.005057893341, 2.627504556}; }
			else{return new double[] {1.858308966e-23, -6.747458595e-20, 1.027204701e-16, -8.65267313e-14, 4.398990952e-11, -0.00000001338253561, 0.000002050649503, -0.0000009347280601, 2.338775002}; }
		}
		else if(curveIndex==17){
			if(isEven){return new double[] {3.051557434e-21, -7.499061042e-18, 7.870531074e-15, -4.613295607e-12, 0.000000001659295977, -0.0000003795444541, 0.00005551096133, -0.005076979262, 2.625863878}; }
			else{return new double[] {1.82642589e-23, -6.667917706e-20, 1.019074934e-16, -8.611155575e-14, 4.39004688e-11, -0.00000001339132237, 0.000002058053795, -0.000001234501614, 2.335390353}; }
		}
		else if(curveIndex==18){
			if(isEven){return new double[] {3.056112501e-21, -7.510709677e-18, 7.883409895e-15, -4.621390639e-12, 0.000000001662502927, -0.000000380386993, 0.00005566251202, -0.005095970302, 2.624224464}; }
			else{return new double[] {1.795509594e-23, -6.590575951e-20, 1.011150389e-16, -8.57064124e-14, 4.381378511e-11, -0.00000001340058026, 0.000002065530363, -0.000001540779219, 2.332003245}; }
		}
		else if(curveIndex==19){
			if(isEven){return new double[] {3.060589215e-21, -7.522161146e-18, 7.896077055e-15, -4.629359204e-12, 0.000000001665663914, -0.0000003812191475, 0.0000558126638, -0.00511487378, 2.622586455}; }
			else{return new double[] {1.765480775e-23, -6.515018195e-20, 1.003364261e-16, -8.530617626e-14, 4.372771563e-11, -0.00000001340980385, 0.000002073013805, -0.000001849378104, 2.328613576}; }
		}
		else if(curveIndex==20){
			if(isEven){return new double[] {3.064973083e-21, -7.533385716e-18, 7.908507572e-15, -4.637190199e-12, 0.000000001668776107, -0.0000003820404925, 0.00005596137749, -0.005133687272, 2.620949789}; }
			else{return new double[] {1.735034834e-23, -6.438646846e-20, 9.955146955e-17, -8.490332389e-14, 4.364102271e-11, -0.00000001341898491, 0.000002080527664, -0.000002162949728, 2.325221432}; }
		}
		else if(curveIndex==21){
			if(isEven){return new double[] {3.069282503e-21, -7.54441912e-18, 7.920729299e-15, -4.64489471e-12, 0.00000000167184186, -0.0000003828512721, 0.00005610865956, -0.005152409907, 2.619314431}; }
			else{return new double[] {1.702305565e-23, -6.357231224e-20, 9.871915783e-17, -8.447579039e-14, 4.354657939e-11, -0.00000001342673175, 0.00000208791518, -0.000002472468145, 2.321826617}; }
		}
		else if(curveIndex==22){
			if(isEven){return new double[] {3.073485227e-21, -7.555196903e-18, 7.932689409e-15, -4.652449736e-12, 0.000000001674855384, -0.000000383650618, 0.00005625443092, -0.005171037252, 2.617680278}; }
			else{return new double[] {1.672715765e-23, -6.282586825e-20, 9.79473324e-17, -8.407710126e-14, 4.346006116e-11, -0.00000001343578837, 0.000002095452777, -0.000002792331573, 2.318429422}; }
		}
		else if(curveIndex==23){
			if(isEven){return new double[] {3.077620144e-21, -7.565801234e-18, 7.944460082e-15, -4.659889509e-12, 0.000000001677826206, -0.000000384440111, 0.00005639884208, -0.005189576491, 2.61604747}; }
			else{return new double[] {1.643242488e-23, -6.208148479e-20, 9.717626755e-17, -8.367769164e-14, 4.337281583e-11, -0.00000001344463402, 0.000002102985218, -0.000003114238521, 2.315029672}; }
		}
		else if(curveIndex==24){
			if(isEven){return new double[] {3.081633308e-21, -7.576112971e-18, 7.955931538e-15, -4.667159257e-12, 0.000000001680738255, -0.0000003852169317, 0.00005654160524, -0.005208011841, 2.614415665}; }
			else{return new double[] {1.615344216e-23, -6.137101658e-20, 9.643513842e-17, -8.32921328e-14, 4.328916812e-11, -0.0000000134540495, 0.000002110596938, -0.000003443298393, 2.311627504}; }
		}
		else if(curveIndex==25){
			if(isEven){return new double[] {3.085586598e-21, -7.586268355e-18, 7.967228705e-15, -4.674320817e-12, 0.000000001683609429, -0.0000003859841427, 0.00005668301733, -0.005226357724, 2.61278514}; }
			else{return new double[] {1.586017462e-23, -6.062922896e-20, 9.566504523e-17, -8.289179316e-14, 4.32010001e-11, -0.00000001346267283, 0.000002118156759, -0.000003772891671, 2.308222782}; }
		}
		else if(curveIndex==26){
			if(isEven){return new double[] {3.089433079e-21, -7.596167588e-18, 7.97826393e-15, -4.681333094e-12, 0.000000001686428632, -0.0000003867400057, 0.00005682292613, -0.005244607159, 2.611155771}; }
			else{return new double[] {1.556126855e-23, -5.987564919e-20, 9.488418338e-17, -8.248563331e-14, 4.311078943e-11, -0.00000001347087922, 0.000002125692219, -0.000004103664159, 2.304815503}; }
		}
		else if(curveIndex==27){
			if(isEven){return new double[] {3.093202795e-21, -7.605873056e-18, 7.989090196e-15, -4.6882197e-12, 0.000000001689201728, -0.0000003874853049, 0.00005696137794, -0.005262760107, 2.609527508}; }
			else{return new double[] {1.528010565e-23, -5.915947025e-20, 9.413568115e-17, -8.20942236e-14, 4.302439009e-11, -0.00000001347969249, 0.000002133311476, -0.000004441936035, 2.301405818}; }
		}
		else if(curveIndex==28){
			if(isEven){return new double[] {3.096882781e-21, -7.615357653e-18, 7.999684247e-15, -4.694970117e-12, 0.000000001691926039, -0.0000003882196573, 0.00005709834279, -0.005280815107, 2.607900322}; }
			else{return new double[] { 1.49711761e-23, -5.838450378e-20, 9.333496118e-17, -8.167734908e-14, 4.293054489e-11, -0.00000001348722236, 0.00000214083012, -0.000004778351841, 2.297993553}; }
		}
		else if(curveIndex==29){
			if(isEven){return new double[] {3.10047724e-21, -7.624631241e-18, 8.010055812e-15, -4.701589545e-12, 0.000000001694603185, -0.0000003889433592, 0.00005723385003, -0.005298773312, 2.606274232}; }
			else{return new double[] {1.463769146e-23, -5.754903943e-20, 9.247228401e-17, -8.122621085e-14, 4.282561787e-11, -0.00000001349267488, 0.000002148154907, -0.000005107382503, 2.294578579}; }
		}
		else if(curveIndex==30){
			if(isEven){return new double[] {3.104009291e-21, -7.633743244e-18, 8.02024869e-15, -4.708098789e-12, 0.000000001697238874, -0.0000003896573122, 0.00005736797525, -0.005316637216, 2.604649256}; }
			else{return new double[] {1.441515793e-23, -5.696482414e-20, 9.184566961e-17, -8.089402804e-14, 4.275545456e-11, -0.0000000135041313, 0.00000215609654, -0.000005470980689, 2.29116175}; }
		}
		else if(curveIndex==31){
			if(isEven){return new double[] {3.107410865e-21, -7.642546352e-18, 8.030130442e-15, -4.714433817e-12, 0.000000001699815097, -0.0000003903585373, 0.00005750043842, -0.005334393011, 2.603025131}; }
			else{return new double[] {1.411163683e-23, -5.619981758e-20, 9.105089824e-17, -8.047727523e-14, 4.266034949e-11, -0.00000001351125819, 0.000002163636233, -0.000005817469807, 2.287742052}; }
		}
		else if(curveIndex==32){
			if(isEven){return new double[] {3.110774627e-21, -7.651243022e-18, 8.039885024e-15, -4.720684642e-12, 0.000000001702357518, -0.0000003910513417, 0.00005763164724, -0.005352059994, 2.601402211}; }
			else{return new double[] {1.383179568e-23, -5.548253388e-20, 9.029528871e-17, -8.007720121e-14, 4.256913489e-11, -0.00000001351890509, 0.000002171239361, -0.00000616991942, 2.284319939}; }
		}
		else if(curveIndex==33){
			if(isEven){return new double[] {3.114050568e-21, -7.659725132e-18, 8.049415206e-15, -4.726804131e-12, 0.000000001704852783, -0.0000003917334791, 0.00005776138317, -0.005369626639, 2.599780252}; }
			else{return new double[] {1.357845851e-23, -5.482827514e-20, 8.960084028e-17, -7.970836727e-14, 4.248699438e-11, -0.00000001352811101, 0.00000217902125, -0.000006534834158, 2.280895556}; }
		}
		else if(curveIndex==34){
			if(isEven){return new double[] {3.117198994e-21, -7.667899219e-18, 8.058629351e-15, -4.732743797e-12, 0.000000001707285897, -0.000000392402206, 0.00005788935946, -0.00538707733, 2.598158935}; }
			else{return new double[] {1.329144159e-23, -5.409752616e-20, 8.883397178e-17, -7.930230713e-14, 4.239355286e-11, -0.00000001353530298, 0.000002186625204, -0.000006893950524, 2.277468548}; }
		}
		else if(curveIndex==35){
			if(isEven){return new double[] {3.120323055e-21, -7.675999596e-18, 8.067749513e-15, -4.738617398e-12, 0.000000001709690967, -0.0000003930635691, 0.00005801618366, -0.005404442732, 2.59653885}; }
			else{return new double[] {1.302198502e-23, -5.340598483e-20, 8.810308058e-17, -7.891368e-14, 4.230491526e-11, -0.00000001354329768, 0.000002194334186, -0.000007262065337, 2.274039226}; }
		}
		else if(curveIndex==36){
			if(isEven){return new double[] {3.123309745e-21, -7.683777921e-18, 8.076548638e-15, -4.744313038e-12, 0.0000000017120358, -0.0000003937120954, 0.00005814132511, -0.005421696145, 2.594919479}; }
			else{return new double[] {1.273987552e-23, -5.268327369e-20, 8.733980902e-17, -7.850651739e-14, 4.220993073e-11, -0.00000001355002265, 0.000002201924776, -0.000007626631031, 2.270607323}; }
		}
		else if(curveIndex==37){
			if(isEven){return new double[] {3.126277658e-21, -7.691488232e-18, 8.08525212e-15, -4.74993794e-12, 0.000000001714349983, -0.0000003943525673, 0.00005826521752, -0.005438857001, 2.59330115}; }
			else{return new double[] {1.248984717e-23, -5.203708268e-20, 8.665224368e-17, -7.813949667e-14, 4.212729142e-11, -0.00000001355902083, 0.000002209782521, -0.000008009341185, 2.267173318}; }
		}
		else if(curveIndex==38){
			if(isEven){return new double[] {3.129137658e-21, -7.698942697e-18, 8.093696731e-15, -4.755416509e-12, 0.000000001716613377, -0.0000003949818757, 0.0000583875936, -0.005455913523, 2.591683666}; }
			else{return new double[] {1.222667133e-23, -5.135892824e-20, 8.593171272e-17, -7.775368441e-14, 4.203824804e-11, -0.00000001356674931, 0.000002217524584, -0.000008388999663, 2.263736768}; }
		}
		else if(curveIndex==39){
			if(isEven){return new double[] {3.13192093e-21, -7.706200788e-18, 8.101927365e-15, -4.7607655e-12, 0.000000001718829009, -0.0000003956001714, 0.00005850842339, -0.005472861133, 2.590066876}; }
			else{return new double[] {1.196230654e-23, -5.067537806e-20, 8.520308387e-17, -7.736180569e-14, 4.194666035e-11, -0.00000001357391158, 0.000002225225676, -0.000008769851358, 2.260297781}; }
		}
		else if(curveIndex==40){
			if(isEven){return new double[] {3.134615477e-21, -7.713244414e-18, 8.109936061e-15, -4.765985724e-12, 0.000000001720998617, -0.0000003962080293, 0.00005862779114, -0.005489705214, 2.588450905}; }
			else{return new double[] {1.168692168e-23, -4.996916185e-20, 8.445485884e-17, -7.69604924e-14, 4.185227423e-11, -0.00000001358059269, 0.000002232906452, -0.000009153510187, 2.256856405}; }
		}
		else{
			if(isEven){return new double[] {3.137250555e-21, -7.72013719e-18, 8.117780118e-15, -4.771104947e-12, 0.000000001723130067, -0.0000003968067684, 0.00005874582131, -0.005506451278, 2.586835842}; }
			else{return new double[] {1.144450151e-23, -4.933604792e-20, 8.377333729e-17, -7.659139765e-14, 4.176674582e-11, -0.00000001358870902, 0.000002240746355, -0.000009548753583, 2.253412822}; }
		}

//		return coeffs ;
	}











}
