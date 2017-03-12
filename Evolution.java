import java.util.Arrays;

public class Evolution {
	
	VirtualGame[] generation = new VirtualGame[200];
	
	public Evolution(){		
		
		//Create random heuristics
		for(int i = 0; i < generation.length; i++){
			generation[i] = new VirtualGame(new HeuristicMap());
		}
		
		//Sort initial set
		Arrays.sort(generation);
		int steps = 250;
		while(steps > 0){
			for(int i = 0; i < generation.length; i++){
				//Simulate 5 times to reduce the effects of randomness
				generation[i].simulate(5);				
			}
			Arrays.sort(generation);
			System.out.println("Best: " + generation[0] + "\nWorse: " + generation[generation.length - 1]);
			nextGeneration();
			steps --;
		}
		
	}
	
	/**
	 * 
	 */
	public void nextGeneration(){
		VirtualGame[] fittest = new VirtualGame[generation.length / 10];
		for(int i = 0; i < fittest.length; i++){
			fittest[i] = new VirtualGame(generation[i].map);
		}
		
		//Breed every pair of fittest with each other
		VirtualGame[] fitBreed = new VirtualGame[fittest.length / 2];
		for(int i = 0; i < fittest.length; i += 2){
			fitBreed[i / 2] = new VirtualGame(fittest[i].map.breedAverage(fittest[i + 1].map));
		}
		
		int ind = 0;
		for(int i = 0; i < fittest.length; i++){
			generation[ind] = fittest[i];
			ind++;
		}
		
		for(int i = 0; i < fitBreed.length; i++){
			generation[ind] = fitBreed[i];
			ind++;
		}
		
		while(ind < generation.length){
			int r1 = (int)(Math.random() * generation.length);
			int r2 = (int)(Math.random() * generation.length);
			HeuristicMap map = generation[r1].map.breedAverage(generation[r2].map);
			generation[ind] = new VirtualGame(map);
			ind++;
		}
		
	}
	
}
