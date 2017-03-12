
public class VirtualGame implements Comparable {

	
	public HeuristicMap map;
	public int average;
	public int top;
	public int bottom;
	
	public VirtualGame(HeuristicMap map){
		this.map = map;
	}
	
	public void simulate(int amount){
		int sum = 0;
		int max = -1; //-1 when unset
		int min = -1;
		for(int o = 0; o < amount; o++){
			int value = simulate();
			sum += value;
			if(max == -1 || max < value){
				max = value;
			}
			
			if(min == -1 || min > value){
				min = value;
			}
		}
		average = sum / amount;
		this.top = max;
		this.bottom = min;
	}
	
	private int simulate(){
		Board board = new Board(map);
		//Spawn both random tiles
		board.spawnRandomTile();
		board.spawnRandomTile();
		boolean running = true;
		int steps = 0;
		long start = System.currentTimeMillis();
		while(running){
			Board next = board.lookAhead(Board.DEPTH);
			if(next != null){
				while(next.parent != board){
					next = next.parent;
				}
				board.makeMove(next.lastMove);
				board.spawnRandomTile();
			}else{
				//printState(board.toIntArray());
				running = false;
			}
			steps++;
		}
		long delta = System.currentTimeMillis() - start;
		if(Controller.DEBUG){
			System.out.println("Time to finish: " + delta + "ms - Steps: " + steps + " - Max Piece: " + board.getMaxPiece() + " - Score: " + board.score);
		}
		return board.score;
	}
	
	public void printState(int[][] state){	
		for(int i = 0; i < state.length; i++){
			for(int o = 0; o < state[0].length; o++){
				System.out.print("[" + state[o][i] + "]");
			}
			System.out.println();
		}
		System.out.println();
	}

	@Override
	public int compareTo(Object o) {
		VirtualGame other = (VirtualGame)o;
		return (other.average < this.average) ? -1 : ((other.average == this.average) ? 0 : 1);
	}
	
	@Override
	public String toString(){
		return "VirtualGame[avg: " + average + ", max: " + top + ", min: " + bottom + ", " + map + "]"; 
	}
	
}
