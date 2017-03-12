import java.awt.event.KeyEvent;

public class Board {

	private Tile[][] tiles = new Tile[4][4];
	public int score = 0;
	public int lastMove = 0;
	public boolean lastMoveSuccess = true;
	public Board parent = null;
	
	public HeuristicMap map;
	
	public Board(int[][] state, HeuristicMap map){
		for(int x = 0; x < state.length; x++){
			for(int y = 0; y < state[0].length; y++){
				if(state[x][y] != 0){
					Tile t = new Tile(state[x][y]);
					tiles[x][y] = t;
				}
			}
		}
		this.map = map;
	}
	
	public Board(HeuristicMap map){
		this.map = map;
	}
	
	public void resetMergeState(){
		for(int i = 0; i < tiles.length; i++){
			for(int o = 0; o < tiles[0].length; o++){
				if(tiles[i][o] != null){
					tiles[i][o].merged = false;
				}
			}
		}
	}
	
	/*
	 * x = -1 -> left
	 * x = 1 -> right
	 * y = -1 -> up
	 * y = 1 -> down
	 */
	public void makeMove(int key){
		if(key == KeyEvent.VK_UP){
			this.lastMoveSuccess = makeMove(0, -1);
		}else if(key == KeyEvent.VK_DOWN){
			this.lastMoveSuccess = makeMove(0, 1);
		}else if(key == KeyEvent.VK_LEFT){
			this.lastMoveSuccess = makeMove(-1, 0);
		}else if(key == KeyEvent.VK_RIGHT){
			this.lastMoveSuccess = makeMove(1, 0);
		}
		this.lastMove = key;
	}
	
	public boolean makeMove(int x, int y){
		int startX = (x == -1) ? 3 : 0;
		int startY = (y == -1) ? 3 : 0;
		int incX = (x == -1) ? -1 : 1; 
		int incY = (y == -1) ? -1 : 1; 
		
		boolean moveMade = false;
		
		for(int i = startX; i < 4 && i >= 0; i += incX){
			for(int o = startY; o < 4 && o >= 0; o += incY){
				int nextX = i + x;
				int nextY = o + y;
				
				//If the tile is against the wall, continue
				if(nextX < 0 || nextX >= 4 || nextY < 0 || nextY >= 4){
					continue;
				}
				
				Tile from = tiles[i][o];
				Tile to = tiles[nextX][nextY];
				
				//If current tile is null, continue
				if(from == null){
					continue;
				}
				
				//If the next position is equal empty
				if(to == null){
					tiles[nextX][nextY] = tiles[i][o];
					tiles[i][o] = null;
					moveMade = true;
				}else if(to.value == from.value && !(to.merged || from.merged)){ //if next tile matches, and were not merged
					to.value = to.value * 2;
					to.merged = true;
					tiles[i][o] = null;
					score += to.value;
					moveMade = true;
				}
			}
		}
		
		//Recursively run until the board is in a steady state
		if(moveMade){
			makeMove(x, y);
			return true;
		}else{
			return false;
		}
	}
	
	public Board lookAhead(int amount){
		
		this.resetMergeState();
		
		Board[] boards = new Board[4];
		
		for(int i = 0; i < boards.length; i++){
			boards[i] = this.copyBoard();
			boards[i].parent = this;
		}
		
		//Booleans of valid moves
		boards[0].makeMove(KeyEvent.VK_LEFT);
		boards[1].makeMove(KeyEvent.VK_RIGHT);
		boards[2].makeMove(KeyEvent.VK_UP);
		boards[3].makeMove(KeyEvent.VK_DOWN);
		
		//Return the maximum of current iteration if it is the last iteration
		if(amount == 0){
			Board max = boards[0];
			for(int i = 1; i < boards.length; i++){
				if(max.getHeuristicSumOfBoardHistory() < boards[i].getHeuristicSumOfBoardHistory() || (!max.lastMoveSuccess && boards[i].lastMoveSuccess)){
					max = boards[i];
				}
			}
			return max;
		}else{
			Board max = null;
			for(int i = 0; i < boards.length; i++){
				if(boards[i].lastMoveSuccess){ //Only pursue branches that have valid moves
					Board b = boards[i].lookAhead(amount - 1);
					if(max == null || (max.getHeuristicSumOfBoardHistory() < b.getHeuristicSumOfBoardHistory() && b.lastMoveSuccess) || (!max.lastMoveSuccess && b.lastMoveSuccess)){
						max = b;
					}
				}
			}
			return max;
		}
		
	}
	
	//Heuristic factors
	public static int DEPTH = 3;
	
	//Todo implement
	public float getHeuristicSumOfBoard(){
		float max = (float)Math.log(getMaxPiece()) * map.MAX_TILE_FACTOR;
		float score = this.score * map.SCORE_FACTOR; 
		float tiles = this.getTileCount() * map.TILE_COUNT_FACTOR;
		float organization = this.getOrganizationScore() * map.ORGANIZATION_FACTOR;
		float adjacency = this.getOrganizationScore() * map.ADJACENCY_FACTOR;
		if(Controller.DEBUG)
			System.out.println(max + "  " + score + "  " + tiles + "  " + organization);
		
		float sum = organization + max + score + tiles + adjacency;
		
		return sum;
	}
	
	public float getHeuristicSumOfBoardHistory(){
		Board b = this;
		float sum = this.getHeuristicSumOfBoard();
		int ind = Board.DEPTH + 1;
		while(b.parent != null){
			sum += b.parent.getHeuristicSumOfBoard() / (1.1 * ind);
			b = b.parent;
			ind --;
		}
		return sum;
	}
	
	public int getMaxPiece(){
		int max = 0;
		for(int i = 0; i < tiles.length; i++){
			for(int o = 0; o < tiles[0].length; o++){
				Tile t = tiles[i][o];
				if(t != null && t.value > max){
					max = t.value;
				}
			}
		}
		return max;
	}
	
	public int getTileCount(){
		int count = 0;
		for(int i = 0; i < tiles.length; i++){
			for(int o = 0; o < tiles[0].length; o++){
				Tile t = tiles[i][o];
				if(t != null){
					count ++;
				}
			}
		}
		return count;
	}
	
	public Board copyBoard(){
		int[][] arr = this.toIntArray();
		Board b = new Board(arr, map);
		b.score = this.score;
		return b;
	}
	
	//Lower is better
	public int getAdjacencyScore(){
		int sum = 0;
		for(int y = 0; y < tiles.length; y++){
			for(int x = 0; x < tiles.length; x++){
				if(tiles[x][y] != null){
					int left = x - 1;
					int right = x + 1;
					int up  = y - 1;
					int down = y + 1;
					
					if(left > 0 && tiles[left][y] != null && tiles[left][y].value != 0){
						sum += tiles[x][y].value / tiles[left][y].value;
					}
					if(right < 4 && tiles[right][y] != null && tiles[right][y].value != 0){
						sum += tiles[x][y].value / tiles[right][y].value;
					}
					if(down > 0 && tiles[x][down] != null && tiles[x][down].value != 0){
						sum += tiles[x][y].value / tiles[x][down].value;
					}
					if(up > 0 && tiles[x][up] != null && tiles[x][up].value != 0){
						sum += tiles[x][y].value / tiles[x][right].value;
					}
				}
			}
		}
		return sum;
	}
	
	public float getOrganizationScore(){
		float sum = 0;
		for(int y = 0; y < tiles.length; y++){
			for(int x = 0; x < tiles.length; x++){
				if(tiles[x][y] != null){
					sum += (y * x + x) * tiles[x][y].value;
				}
			}
		}
		return sum;
	}
	
	public void spawnRandomTile(){
		int count = this.getTileCount();
		int blankCount = tiles.length * tiles[0].length - count - 1;
		int choice = (int)(Math.random() * blankCount);
		int ind = 0;
		for(int x = 0; x < tiles.length; x++){
			for(int y = 0; y < tiles[0].length; y++){
				if(tiles[x][y] == null){
					if(ind == choice){

						int value = Math.random() < 0.9 ? 2 : 4;
						Tile t = new Tile(value);
						tiles[x][y] = t;
						
						return;
					}
						ind ++;
				}
			}
		}
	}
	
	public int[][] toIntArray(){
		int[][] arr = new int[tiles.length][tiles[0].length];
		
		for(int x = 0; x < arr.length; x++){
			for(int y = 0; y < arr[0].length; y++){
				if(tiles[x][y] != null){
					arr[x][y] = tiles[x][y].value;
				}else{
					arr[x][y] = 0;
				}
			}
		}
		
		return arr;
		
	}
	
	@Override
	public String toString(){
		return map + " " + ", Score: " + this.score + ", Max Piece: " + this.getMaxPiece();
	}
	
}

