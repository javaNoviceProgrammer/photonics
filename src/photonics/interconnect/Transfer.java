package photonics.interconnect;

public class Transfer {
	
	String startPort ;
	String endPort ;
	
	public Transfer(
			String startPort,
			String endPort
			){
		this.startPort = startPort ;
		this.endPort = endPort ;
	}
	
	public String getStartPort(){
		return startPort ;
	}
	
	public String getEndPort(){
		return endPort ;
	}

}
