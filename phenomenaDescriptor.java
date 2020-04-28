
public class phenomenaDescriptor {

	 String picos;
	 double media;
	
	phenomenaDescriptor(String picos, double media){
		this.picos = picos;
		this.media = media;
	}
	@Override
	  public String toString() {
		String output = picos + "," + Double.toString(media);
	    return output;
	  }
}
