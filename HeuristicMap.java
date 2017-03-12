
public class HeuristicMap {

	public int DEPTH;
	public float SCORE_FACTOR;
	public float MAX_TILE_FACTOR;
	public float TILE_COUNT_FACTOR;
	public float ORGANIZATION_FACTOR;
	public float ADJACENCY_FACTOR = 0;
	
	public HeuristicMap(int depth, float score, float tile, float count, float org, float adj){
		this.DEPTH = depth;
		this.SCORE_FACTOR = score;
		this.MAX_TILE_FACTOR = tile;
		this.TILE_COUNT_FACTOR = count;
		this.ORGANIZATION_FACTOR = org;
		this.ADJACENCY_FACTOR = adj;
	}
	
	public HeuristicMap(){
		this.DEPTH = Board.DEPTH;
		//this.SCORE_FACTOR = (float)(Math.random() * 11 - 1);
		this.SCORE_FACTOR = 0;
		this.MAX_TILE_FACTOR = (float)(Math.random() * 11 - 1);
		this.TILE_COUNT_FACTOR = (float)(Math.random() * 10 - 8);
		this.ORGANIZATION_FACTOR = (float)(Math.random() * 11 - 1);
		this.ADJACENCY_FACTOR = (float)(Math.random() * 10 - 8);
	}
	
	public static final int THRESH = 4;
	public HeuristicMap breedAverage(HeuristicMap other){
		int newDepth = Board.DEPTH;
		//float newScore = (this.SCORE_FACTOR + other.SCORE_FACTOR) / 2 + ((Math.random() < 0.02) ? ((float)Math.random() * THRESH - THRESH / 2) : 0);
		float newScore = 0;
		float newTile = (this.MAX_TILE_FACTOR + other.MAX_TILE_FACTOR) / 2 + ((Math.random() < 0.02) ? ((float)Math.random() * THRESH - THRESH / 2) : 0);
		float newCount = (this.TILE_COUNT_FACTOR + other.TILE_COUNT_FACTOR) / 2 + ((Math.random() < 0.02) ? ((float)Math.random() * THRESH - THRESH / 2) : 0);
		float newOrg = (this.ORGANIZATION_FACTOR + other.ORGANIZATION_FACTOR) / 2 + ((Math.random() < 0.02) ? ((float)Math.random() * THRESH - THRESH / 2) : 0);
		float newAdj = (this.ADJACENCY_FACTOR + other.ADJACENCY_FACTOR) / 2 + ((Math.random() < 0.02) ? ((float)Math.random() * THRESH - THRESH / 2) : 0);
		return new HeuristicMap(newDepth, newScore, newTile, newCount, newOrg, newAdj);
	}
	
	@Override
	public String toString(){
		return "DEPTH:" + DEPTH + " SCORE_FACTOR:" + SCORE_FACTOR + " MAX_TILE:" + MAX_TILE_FACTOR + " TILE_COUNT:" + TILE_COUNT_FACTOR + " ORGANIZATION:" + ORGANIZATION_FACTOR + " ADJACENCY: " + ADJACENCY_FACTOR;
	}
	
}

