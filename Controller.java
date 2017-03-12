import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Controller {

	public static final int COLOR_BACK = -4016988;
	public static final int COLOR_2 = -1384751;
	public static final int COLOR_4 = -1451333;
	public static final int COLOR_8 = -1072543;
	public static final int COLOR_16 = -818868;
	public static final int COLOR_32 = -760503;
	public static final int COLOR_64 = -768730;
	public static final int COLOR_128 = -1456040;
	public static final int COLOR_256 = -1457081;
	public static final int COLOR_512 = -1458123;
	public static final int COLOR_1024 = -1459165;
	public static final int COLOR_2048 = -1459955;
	
	public static boolean DEBUG = false;
	public static boolean SIMULATE = false;
	
	public static int MULTIPLE = 4;
	public static Color TARGET_COLOR = new Color(173, 157, 143);
	
	public static HashMap<Integer, Integer> colors = new HashMap<Integer, Integer>();
	
	public static void main(String[] s){
		
		colors.put(COLOR_BACK, 0);
		colors.put(COLOR_2, 2);
		colors.put(COLOR_4, 4);
		colors.put(COLOR_8, 8);
		colors.put(COLOR_16, 16);
		colors.put(COLOR_32, 32);
		colors.put(COLOR_64, 64);
		colors.put(COLOR_128, 128);
		colors.put(COLOR_256, 256);
		colors.put(COLOR_512, 512);
		colors.put(COLOR_1024, 1024);
		colors.put(COLOR_2048, 2048);
		
		/*for(Map.Entry<Integer, Integer> set : colors.entrySet()){
			System.out.println(set.getValue() + " - " + new Color(set.getKey()));
		}*/
		
		new Controller();
	}
	
	private Robot robot;
	
	public Controller(){
		
		if(Controller.SIMULATE){
			new Evolution();
		}else{
			try {
				robot = new Robot();
				Thread.sleep(2000);
				Rectangle region = detectRegion();
				startGame(region);
			} catch (AWTException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public void startGame(Rectangle region) throws AWTException, InterruptedException, IOException{
		
		System.out.println("Using bounds: " + region);
		
		boolean running = true;
		int[][] state = new int[4][4];
		int score = 0;
				
		//HeuristicMap map = new HeuristicMap(2, 4.913263F, 3.7663338F, -2.4868162F, 4.2855263F, -1.6792945F);
		//HeuristicMap map = new HeuristicMap(2, 4.8977137F, 3.8344226F, -2.6027603F, 5.0628004F, -2.4381642F);
		//HeuristicMap map = new HeuristicMap(2, 4.89323F, 3.763247F, -2.5643945F, 4.454717F, -3.095696F);
		HeuristicMap map = new HeuristicMap(2, 4.7380714F, 3.9223156F, -2.7394152F, 4.5411673F, -2.4708776F);
		//HeuristicMap map = new HeuristicMap(2, 5.0704913F, 3.5275478F, -2.889957F, 4.44707F, -2.1762323F);
		
		//HeuristicMap map = new HeuristicMap(3, 0, 6.92079F, -2.7949147F, 4.4665427F, -4.1156783F);

		while(running){
			BufferedImage game = robot.createScreenCapture(region);
			updateState(game, state);
			Board b = new Board(state, map);
			b.score = score;
			Board next = b.lookAhead(Board.DEPTH);
			
			int depth = 0;
			if(next != null){
				System.out.println("END SCORE: " + next.score +  " MOVE: " + next.lastMove);
				while(next.parent != b){
					depth++;
					next = next.parent;
				}
				System.out.println("DEPTH: " + depth + "  SCORE: " + next.score +  " MOVE: " + next.lastMove);
				makeMove(next.lastMove);
			}else{
				running = false;
				System.out.println("Losing state detected");
			}
			robot.delay(360);
		}
		
	}
	
	public void makeMove(int move){
		robot.keyPress(move);
		robot.keyRelease(move);
	}
	
	public MovementAnalysis getPossibleMoves(int[][] state){
		
		MovementAnalysis mov = new MovementAnalysis();
		
		for(int i = 0; i < 4; i++){
			for(int o = 0; o < 4; o++){
				int value = state[i][o];
				if(value != 0){
					int left = i - 1;
					int right = i + 1;
					int up = o - 1;
					int down = o + 1;
					
					if(left >= 0 && (state[left][o] == value || state[left][o] == 0)){
						mov.addPossibleMove(state[o][left], KeyEvent.VK_LEFT);
					}
					if(right < 4 && (state[right][o] == value || state[right][o] == 0)){
						mov.addPossibleMove(state[right][o], KeyEvent.VK_RIGHT);
					}
					if(up >= 0 && (state[i][up] == value || state[i][up] == 0)){
						mov.addPossibleMove(state[i][up], KeyEvent.VK_UP);
					}
					if(down < 4 && (state[i][down] == value || state[i][down] == 0)){
						mov.addPossibleMove(state[i][down], KeyEvent.VK_DOWN);
					}
				}
				
			}
		}
		
		return mov;
	}
	
	public boolean updateState(BufferedImage game, int[][] state) throws IOException{
		
		for(int x = 0; x < state.length; x++){
			for(int y = 0; y < state[0].length; y++){
				int posx = x * game.getWidth() / 4 + 30;
				int posy = y * game.getHeight() / 4 + 30;
				int color = game.getRGB(posx, posy);
				if(!colors.containsKey(color)){
					//Attempt to match color
					if(!Controller.checkAndAddToValidColor(color)){
						System.out.println(" " + x + " " + y + " - " + color);
						
						game.getGraphics().setColor(Color.red);
						game.getGraphics().fillOval(posx, posy, 2, 2);
						File outputfile = new File("image.png");
						ImageIO.write(game, "png", outputfile);
						System.exit(-1);
						
						return false;
					}
				}
				int value = colors.get(color);
				state[x][y] = value;
			}
		}
		return true;
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
	
	public Rectangle detectRegion() throws IOException{
		BufferedImage screen = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
	
		int x2 = getRightX(screen);
		int y1 = getTopY(screen);
		int x1 = getLeftX(screen);
		int y2 = getBottomY(screen);
		
		screen.getGraphics().setColor(Color.RED);
		screen.getGraphics().fillRect(x2, y1, x2 - x1, y2 - y1);
								
		return new Rectangle(x1, y1, x2 - x1, y2 - y1);
	}
	
	private int getTopY(BufferedImage screen){
		for(int y = 0; y < screen.getHeight(); y++){
			for(int x = 0; x < screen.getWidth(); x++){
				Color c = new Color(screen.getRGB(x, y));
				if(Controller.pixelSimilar(TARGET_COLOR, c, 2)){
					return y;
				}
			}
		}
		return -1;
	}
	
	private int getBottomY(BufferedImage screen){
		for(int y = screen.getHeight() - 1; y >= 0; y--){
			for(int x = 0; x < screen.getWidth(); x++){
				Color c = new Color(screen.getRGB(x, y));
				if(Controller.pixelSimilar(TARGET_COLOR, c, 2)){
					return y;
				}
			}
		}
		return -1;
	}
	
	private int getLeftX(BufferedImage screen){
		for(int x = 0; x < screen.getWidth(); x++){
			for(int y = 0; y < screen.getHeight(); y++){
				Color c = new Color(screen.getRGB(x, y));
				if(Controller.pixelSimilar(TARGET_COLOR, c, 2)){
					return x;
				}
			}
		}
		return -1;
	}
	
	private int getRightX(BufferedImage screen){
		for(int x = screen.getWidth() - 1; x >= 0; x--){
			for(int y = 0; y < screen.getHeight(); y++){
				Color c = new Color(screen.getRGB(x, y));
				if(Controller.pixelSimilar(TARGET_COLOR, c, 2)){
					return x;
				}
			}
		}
		return -1;
	}
	
	public int[][] performVirtualBoardMove(int[][] board, int move){
		
		int[][] next = copyBoard(board);
		
		if(move == KeyEvent.VK_LEFT){
			
		}else if(move == KeyEvent.VK_RIGHT){
			
		}else if(move == KeyEvent.VK_UP){
			
		}else if(move == KeyEvent.VK_DOWN){
			
		}
		
		return next;
	}
	
	public int[][] copyBoard(int[][] board){
		int[][] tmp = new int[board.length][board[0].length];
		for(int i = 0; i < board.length; i++){
			for(int o = 0; o < board[0].length; o++){
				tmp[i][o] = board[i][o];
			}
		}
		return tmp;
	}
	
	public static boolean pixelSimilar(Color c1, Color c2, int sensitivity){
		return (Math.abs(c1.getRed() - c2.getRed()) < sensitivity && Math.abs(c1.getGreen() - c2.getGreen()) < sensitivity && Math.abs(c1.getBlue() - c2.getBlue()) < sensitivity);
	}
	
	public static boolean checkAndAddToValidColor(int color){

		for(Map.Entry<Integer, Integer> set : colors.entrySet()){
			if(Controller.pixelSimilar(new Color(set.getKey()), new Color(color), 2)){
				colors.put(color, set.getValue());
				return true;
			}
		}
		
		System.out.print("Color " + new Color(color) + " not found to be similar");
		return false;
	}
	
}
