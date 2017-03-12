
public class Tile {

	public boolean merged = false;
	public int value;
	
	public Tile(int value){
		this.value = value;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj != null){
			if(obj instanceof Tile){
				return merged || ((Tile)obj).value == value;
			}else if(obj instanceof Integer){
				return ((Integer)obj) == value;
			}
		}
		return false;
	}
	
}
