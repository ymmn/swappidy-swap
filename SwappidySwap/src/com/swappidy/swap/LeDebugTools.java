package com.swappidy.swap;

import java.awt.Point;

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

	public static String[] fiveCombo = new String[]{
		"grg",
		"rrr",
		"grg"
	};

	public static String[] raiseStackTest1 = new String[]{
		"",
		"r"
	};
	
	public static String[] occupationTest = new String[]{
		"r ",
		" g"
	};
	
	public static Block[][] createBoardAtState(String[] board, GameBoard gboard){
		SwappidySwap.NUM_COL = board[board.length-1].length();
		SwappidySwap.NUM_ROW = board.length;
		Block[][] retval = new Block[SwappidySwap.NUM_COL][SwappidySwap.NUM_ROW]; 
		for(int y = 0; y < board.length; y++){
			for(int x = 0; x < board[y].length(); x++){
				char c = board[y].charAt(x);
				Block myblock = new Block(new Point(x, (board.length-1-y)),
						0, gboard);
				int col = -1;
				if(c=='r')
					col = 2;
				else if(c=='b')
					col = 1;
				else if(c=='g')
					col = 0;
				else if(c==' ')
					myblock = null;
				
				if(myblock!=null)
					myblock.setType(col);
				
				retval[x][board.length-1-y] = myblock;
			}
		}
		return retval;
	}
	
	public static void testShrinking(Block[] blocks){
		for(int i = 0; i < blocks.length; i++)
			blocks[i].setState(Block.State.DISAPPEARING);
	}

}
