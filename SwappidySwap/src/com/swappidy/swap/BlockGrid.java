package com.swappidy.swap;

import java.awt.Point;
import java.util.Random;

public class BlockGrid {

	Block blocks[][];
	
	public BlockGrid(int numCols, int numRows){
		blocks = new Block[numCols][numRows];
	}
	
	public void moveBlockUp(int x, int y){
		blocks[x][y].moveGridPos(0, 1);
		blocks[x][y+1] = blocks[x][y];
	}
	
	public void moveBlockDown(int x, int y){
		blocks[x][y].moveGridPos(0, -1);
		blocks[x][y-1] = blocks[x][y];
	}
	
	public void moveBlockRight(int x, int y){
		blocks[x][y].moveGridPos(1, 0);
		blocks[x+1][y] = blocks[x][y];
	}
	
	public void moveBlockLeft(int x, int y){
		blocks[x][y].moveGridPos(-1, 0);
		blocks[x-1][y] = blocks[x][y];
	}

	public void randomInitialize(Random rng, GameBoard gboard) {
		for(int y=0;y<blocks[0].length;y++){
			for(int x=0;x<blocks.length;x++){
				blocks[x][y] = new Block(
						new Point(x, y),
						rng.nextInt(SwappidySwap.BLOCK_COLORS.length),
						gboard);
			}
		}
	}

	public Block getBlock(int x, int y) {
		return blocks[x][y];
	}

	public void setBlock(int x, int y, Block block) {
		blocks[x][y] = block;
	}

	public void setBlocks(Block[][] b) {
		blocks = b;
	}

	public Block[][] getBlocksArray() {
		return blocks;
	}
	
}
