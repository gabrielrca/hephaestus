import java.util.Arrays;
import java.util.Vector;


public class Hefestos {
	private static int maxSizeWindow = 200; // Quantidade de amostras
	private static int SITUATION = 4; // Situacoes 1 2 3 ou 4
	private static Vector<Double> FW = new Vector<Double>(maxSizeWindow); 

	static int nivel = 0;

	static int rodada = 0;
	static long StartTime = 0;
    
     //-------inicio delay log--------
     static long delayInicio =0;
     
     //--------fim delay log---------
	public static void main(String[] args) {

		Vector<CollectorNode> collectorNodesVector = new Vector<CollectorNode>(20);
		 
		 for(int i=0; i<5; i++){ // Cria 5 "Tipo 1"
			 collectorNodesVector.add(new CollectorNode(1, SITUATION)); 
		 }
		 for(int i=0; i<5; i++){ // Cria 5 "Tipo 2"
			 collectorNodesVector.add(new CollectorNode(2, SITUATION)); 
		 }

		 
		StartTime = System.currentTimeMillis();

		while((rodada ) < 200){ //quantidade de rodadas	
		nivel =0;
		rodada++;
		int collectorNodeIndex =0;
		FW.clear();
		delayInicio = System.currentTimeMillis();
		for(int i =0; i < maxSizeWindow; i++){
			Double receivedData = collectorNodesVector.elementAt(collectorNodeIndex).receiveDataFromNode();

			FW.addElement(receivedData);
			try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
			
			collectorNodeIndex++;
			if(collectorNodeIndex == 10){
				collectorNodeIndex = 0;
			
			}
		
		}
		
		
		Vector<phenomenaDescriptor> phenomena = fusionHeuristics(similarityAnalysis(FW), FW);

		System.out.println("T"+SITUATION+" Feat. Map: " + phenomena);


		
	}
		System.out.println("Elapsed time (miliseconds): " + (System.currentTimeMillis() - StartTime) );
		System.out.println(maxSizeWindow + " Samples");
	}

	

	static Vector<phenomenaDescriptor> fusionHeuristics(Vector<Double> siMeasure, Vector<Double> FW ){
		double media = siMeasure.elementAt(0);
		double kurtose = siMeasure.elementAt(1);
		double assimetria = siMeasure.elementAt(2);
		
		Vector<phenomenaDescriptor> phenomenonVector = new Vector<phenomenaDescriptor>(10);

		if(Math.abs(assimetria) <= 1){
			if(Math.abs(kurtose) <= 0.263){
				phenomenaDescriptor pd = new phenomenaDescriptor("Leptokurt: " , media);
				phenomenonVector.add(pd);
			}else{
				phenomenaDescriptor pd = new phenomenaDescriptor("Platykurt: " , media);
				phenomenonVector.add(pd);
			}	
		}else{
			Vector<Double> metInf = metadeInferiorDaFusionWindow(FW,  media);
			Vector<Double> metSup = metadeSuperiorDaFusionWindow(FW,  media);
			if(!metInf.isEmpty() && nivel < 8){
				nivel++;
				phenomenonVector.addAll(fusionHeuristics(similarityAnalysis(metInf), metInf));
			}
			if(!metSup.isEmpty() && nivel < 8){
				nivel++;
				phenomenonVector.addAll(fusionHeuristics(similarityAnalysis(metSup), metSup));
			}
		}
		

		
		return phenomenonVector;
	}
	
	static Vector<Double> similarityAnalysis(Vector<Double> FW){
		FW = ordenarVector(FW);
		Vector<Double> siVector = new Vector<Double>(3);
		siVector.add(mediaValoresDoVector(FW));
		siVector.add(kurtosisDoVector(FW));
		siVector.add(skewnessDoVector(FW));
		
		return siVector;
	}
	
	static Vector<Double> ordenarVector(Vector<Double> WD) {
        double[] numbers = new double[WD.size()];
        double WDtam = WD.size();

        for (int i = 0; i < WDtam; i++) {
            Double temp = (Double) WD.elementAt(i);
            numbers[i] = temp.doubleValue();
        }
        Arrays.sort(numbers);
        WD.removeAllElements();

        for (int i = 0; i < WDtam; i++) {
            WD.addElement(Double.valueOf(numbers[i]));
        }
        return WD;
    }

	static double mediaValoresDoVector(Vector<Double> WD) {
        double WDtam = WD.size();
        double sum = 0;


        for (int i = 0; i < WDtam; i++) {
            Double temp = (Double) WD.elementAt(i);
            sum = sum + temp.intValue();
        }
        return sum / WDtam;
    }
	
	static double percentilDoVector(Vector<Double> WD, double percentil) {
		
		double[] numbers = new double[WD.size()];
        double WDtam = WD.size();

        for (int i = 0; i < WDtam; i++) {
            Double temp = (Double) WD.elementAt(i);
            numbers[i] = temp.doubleValue();
        }

        if(numbers.length == 0 || numbers == null){
            System.out.println("Problema");
        }

        int index =   (int) ((numbers.length) * (percentil / 100));
        
        return numbers[index];
		

        
        
    }

	static double kurtosisDoVector(Vector<Double> WD){
        double p25 = percentilDoVector(WD, 25);
        double p75 = percentilDoVector(WD, 75);
        double p90 = percentilDoVector(WD, 90);
        double p10 = percentilDoVector(WD, 10);
        
        double cima = (p75 -p25);
        if (cima == 0){return 0;}
        double baixo = (p90 -p10);

        return ((double) 1/2) * (cima/baixo);
    }

	static double standardDeviationDoVector(Vector<Double> WD) {
        double media = mediaValoresDoVector(WD);
        double sum =0;
        double xi = 0;
        for(int i =0; i<WD.size(); i++){
            Double currentNumber = (Double) WD.elementAt(i);
            xi = currentNumber.doubleValue();
            sum = sum + ((xi - media)*(xi - media));
        }
        double variance = sum/( (double) WD.size() - (double)1.0);

        return Math.sqrt(variance);
    }

	static double skewnessDoVector(Vector<Double> WD) {
        double media = mediaValoresDoVector(WD);
        double p50 = percentilDoVector(WD, 50);
        double sd = standardDeviationDoVector(WD);

        double CTE3 = 3;
        double cima = (media - p50);
        if(cima == 0){return 0;}
        return CTE3 * (cima/sd);
    }

	static Vector<Double> metadeInferiorDaFusionWindow(Vector<Double> FW, double media){
		Vector<Double> metadeInferior = new Vector<Double>(FW.size());
		for(int i=0; i< FW.size(); i++){
			if(FW.elementAt(i)<=media){
				metadeInferior.add(FW.elementAt(i));
			}
		}
		return metadeInferior;
	}
	static Vector<Double> metadeSuperiorDaFusionWindow(Vector<Double> FW, double media){
		Vector<Double> metadeSuperior = new Vector<Double>(FW.size());
		for(int i=0; i< FW.size(); i++){
			if(FW.elementAt(i)>media){
				metadeSuperior.add(FW.elementAt(i));
			}
		}
		return metadeSuperior;
	}
		

}
