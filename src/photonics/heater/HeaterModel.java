package photonics.heater;

import ch.epfl.general_libraries.clazzes.ParamName;
import photonics.heater.impulse.ImpulseResponse1D_Modified_FFT;
import photonics.heater.struct.SelfHeating;
import photonics.heater.trans.TransientResponse_FFT;
import photonics.heater.voltage.AbstractVoltage;

public class HeaterModel {

	public AbstractVoltage voltage ;
	public SelfHeating selfHeat ;
	public ImpulseResponse1D_Modified_FFT impulse ;
	public TransientResponse_FFT transResponse ;

	public HeaterModel(
			@ParamName(name="Impulse Response") ImpulseResponse1D_Modified_FFT impulse,
			@ParamName(name="Self Heating Model") SelfHeating selfHeat,
			@ParamName(name="Heater Voltage (V)") AbstractVoltage voltage
			){
		this.selfHeat = selfHeat ;
		this.selfHeat = selfHeat ;
		this.voltage = voltage ;
		this.impulse = impulse ;
		transResponse = new TransientResponse_FFT(selfHeat, this.impulse, voltage) ;
		transResponse.buildModel();
	}


}
