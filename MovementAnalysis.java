import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MovementAnalysis {

	int maxValue = -1;
	ArrayList<Pair> movements = new ArrayList<Pair>();
	
	public void addPossibleMove(int value, int mov){
		if(value > maxValue){
			maxValue = value;
		}
		movements.add(new Pair(value, mov));
	}
	
	public class Pair{
		int value;
		int mov;
		public Pair(int value, int mov){
			this.value = value;
			this.mov = mov;
		}
	}
	
	public Integer determineOptimalMove(){
		HashMap<Integer, Integer> freq = new HashMap<Integer, Integer>();
		HashSet<Integer> maxMovs = new HashSet<Integer>();
		//Get frequencies of each move candidate, as well as possible max-value moves
		for(Pair p : movements){
			//Empty tiles should not add to the frequencies
			if(p.value != 0){
				if(!freq.containsKey(p.mov)){
					freq.put(p.mov, 1);
				}else{
					freq.put(p.mov, freq.get(p.mov) + 1);
				}
				if(p.value == this.maxValue){
					maxMovs.add(p.mov);
				}
			}
		}		
		
		
		//Determine which move has the highest frequency
		Integer maxFreq = 0;
		Integer maxFreqMov = null;
		for(int mov : maxMovs){
			if(maxFreq < freq.get(mov)){
				maxFreq = freq.get(mov);
				maxFreqMov = mov;
			}
		}
		if(maxFreqMov == null){
			maxFreqMov = movements.get((int)(Math.random() * movements.size())).mov;
		}
		return maxFreqMov;
	}
	
}
