package photonics.ring;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootFunction;

public class AddDropRingGeneral {

	double phiRad, lossDB, loss, inputKappa, outputKappa ; // this is the round trip phase inside the ring

	public AddDropRingGeneral(
			@ParamName(name="Input Kappa") double inputKappa,
			@ParamName(name="Output Kappa") double outputKappa,
			@ParamName(name="Round Trip Loss (dB)") double lossDB,
			@ParamName(name="Round Trip phase (rad)") double phiRad
			){
		this.inputKappa = inputKappa ;
		this.outputKappa = outputKappa ;
		this.lossDB = lossDB ;
		loss = Math.pow(10, -lossDB/10) ;
		this.phiRad = phiRad ;
	}

	public double getThruTransmission(){
		double t_out = Math.sqrt(1 - outputKappa*outputKappa) ;
		double t_in = Math.sqrt(1 - inputKappa * inputKappa) ;
		double L = loss ;
		double num = t_in*t_in + t_out*t_out*L - 2*t_in*t_out*Math.sqrt(L)*Math.cos(phiRad) ;
		double denum = 1 + t_in*t_in*t_out*t_out*L - 2*t_in*t_out*Math.sqrt(L)*Math.cos(phiRad) ;
		double trans = num/denum ;
		return trans ;
	}

	public double getThruTransmissionDB(){
		double trdB = 10*Math.log10(getThruTransmission()) ;
		return trdB ;
	}

	public double getDropTransmission(){
		double t_out = Math.sqrt(1 - outputKappa*outputKappa) ;
		double t_in = Math.sqrt(1 - inputKappa * inputKappa) ;
		double k_out = outputKappa ;
		double k_in = inputKappa ;
		double L = loss ;
		double num = (k_in*k_in)*(k_out*k_out)*Math.sqrt(L) ;
		double denum = 1 + t_in*t_in*t_out*t_out*L - 2*t_in*t_out*Math.sqrt(L)*Math.cos(phiRad) ;
		double trans = num/denum ;
		return trans ;
	}

	private double getDropTransmission(double phiRad){
		double t_out = Math.sqrt(1 - outputKappa*outputKappa) ;
		double t_in = Math.sqrt(1 - inputKappa * inputKappa) ;
		double k_out = outputKappa ;
		double k_in = inputKappa ;
		double L = loss ;
		double num = (k_in*k_in)*(k_out*k_out)*Math.sqrt(L) ;
		double denum = 1 + t_in*t_in*t_out*t_out*L - 2*t_in*t_out*Math.sqrt(L)*Math.cos(phiRad) ;
		double trans = num/denum ;
		return trans ;
	}

	public double getDropTransmissionDB(){
		double drop = getDropTransmission() ;
		return 10*Math.log10(drop) ;
	}

	public double getFinesse(){
		double t_in = Math.sqrt(1 - inputKappa * inputKappa) ;
		double t_out = Math.sqrt(1 - outputKappa*outputKappa) ;
		double L = loss ;
		double A = 1-t_in * t_out * Math.sqrt(L) ;
		double B = 2*t_in*t_out*Math.sqrt(L) ;
		double arg = 1-(A*A/B) ;
		double Dphi3dB = 2*Math.acos(arg) ;
		double fsr = 2*Math.PI ;
		return (fsr/Dphi3dB) ;
	}

	public double getFinesseNumeric(){
		RealRootFunction func = new RealRootFunction() {
			@Override
			public double function(double phi) {
				double y = getDropTransmission(phi) - getDropTransmission(0) * 1/2 ;
				return y;
			}
		};
		RealRoot rootFinder = new RealRoot() ;
		double phi3dB = rootFinder.bisect(func, 0, Math.PI) ;
		return (Math.PI/phi3dB) ;
	}


}
