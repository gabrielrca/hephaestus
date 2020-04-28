import java.util.Random;


public class CollectorNode {
	private int typeOfCollectorNode; 	
	private int situation;
	
	private Random generateSample = new Random();
	
	
	public CollectorNode(int typeOfCollectorNode, int situation) { 
	      this.typeOfCollectorNode = typeOfCollectorNode;
	      this.situation = situation;
	   }
	
	Double receiveDataFromNode(){
		Double sampleToReturn = 0.0;
		
		
		if(typeOfCollectorNode == 1){	//tipo 1	
			
			switch (situation){
			case 1:
				sampleToReturn =  (generateSample.nextGaussian()* 4 ) + 50;  
				break;
			case 2:
				sampleToReturn =   (generateSample.nextGaussian()* 2 ) + 85;  
				break;
			case 3:
				sampleToReturn =  (generateSample.nextGaussian()* 4 ) + 50;  
				break;
			case 4:
				sampleToReturn =  (generateSample.nextGaussian()* 2 ) + 85; 
				break;
			}
				
		
		}else if(typeOfCollectorNode ==2){	//tipo 2
			switch (situation){
			case 1:
				sampleToReturn =  (generateSample.nextGaussian()* 18) + 92;   
				break;
			case 2:
				sampleToReturn =   (generateSample.nextGaussian()* 18) + 92;   
				break;
			case 3:
				sampleToReturn =  (generateSample.nextGaussian()* 2) + 155; 
				break;
			case 4:
				sampleToReturn =  (generateSample.nextGaussian()* 2) + 155;  
				break;
			}

		}

		return sampleToReturn;
	}


}


