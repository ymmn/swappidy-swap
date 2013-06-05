package com.swappidy.swap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class LeDebugTools {
	
	public static String[] twoX2 = new String[]{
			"rb",
			"rb"
	};
	
	public static String[] threeX3 = new String[]{
		"rbr",
		"rbr",
		"ggg"
	};
	
	public static String[] fallingTest = new String[]{
		"rbr",
		"",
		"grg"
	};
	
	public static String[] simpleHorizCombo = new String[]{
		"rrr"
	};
	
	public static String[] simpleVertCombo = new String[]{
		"r",
		"r",
		"r"
	};
	
	public static Block[][] createBoardAtState(String[] board, GameBoard gboard){
		SwappidySwap.NUM_COL = board[0].length();
		SwappidySwap.NUM_ROW = board.length;
		Block[][] retval = new Block[board[0].length()][board.length]; 
		for(int y = 0; y < board.length; y++){
			for(int x = 0; x < board[board.length-1-y].length(); x++){
				char c = board[y].charAt(x);
				Color col = null;
				if(c=='r')
					col = Color.RED;
				else if(c=='b')
					col = Color.BLUE;
				else if(c=='g')
					col = Color.GREEN;
				
				retval[x][board.length-1-y] = new Block(
						new Vector2(SwappidySwap.BLOCK_SIZE*x, SwappidySwap.BLOCK_SIZE*(board.length-1-y)),
						col, gboard);
			}
		}
		return retval;
	}
	
	public static void testShrinking(Block[] blocks){
		for(int i = 0; i < blocks.length; i++)
			blocks[i].setState(Block.State.DISAPPEARING);
	}

}
