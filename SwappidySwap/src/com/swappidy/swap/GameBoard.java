package com.swappidy.swap;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.awt.Point;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * The thing that holds blocks. 
 * In the case of 1 player, there's only one board for the game.
 * In the case of 2 players, there are two.
 * @author abdul
 *
 */
public class GameBoard {

	public static int FALL_SPEED = 1;
	public static int SWAP_SPEED = 1;
	public static int SHRINK_SPEED = 3;


	public enum State{
		INITIAL, STATIC, MOVING
	}

	/**
	 * Initialize the board for testing here
	 * Otherwise leave uninitialized for randomized board
	 */
	BlockGrid blockGrid; // = LeDebugTools.createBoardAtState(LeDebugTools.occupationTest, this);
	boolean isOccupied[][];
	Random rng = new Random();
	Cursor cursor;
	private State boardState = State.INITIAL;


	/**
	 * random initiation of blocks. Fills entire screen.
	 */
	private void initBlocks(){
		blockGrid = new BlockGrid(SwappidySwap.NUM_COL, SwappidySwap.NUM_ROW);
		isOccupied = new boolean[SwappidySwap.NUM_COL][SwappidySwap.NUM_ROW];
		blockGrid.randomInitialize(rng, this);
	}

	public GameBoard(){
		if(blockGrid==null) initBlocks();
		cursor = new Cursor(new Point(0,0));
	}

	private void checkForFalling(Integer x, Integer y){
		checkForFalling(x, y, 0);
	}
	
	private void checkForFalling(Integer x, Integer y, int chainLength){
		if(blockGrid.getBlock(x,y)==null || blockGrid.getBlock(x,y).getState()==Block.State.FALLING){  //find the ones that should be falling and set them as falling
			int curY = y + 1;
			Block curBlock = null;
			if(curY < SwappidySwap.NUM_ROW){
				curBlock = blockGrid.getBlock(x, curY);
			}
			while(curBlock!= null && curBlock.isStable()){
				curBlock.setState(Block.State.FALLING);
				if(chainLength > 0){
					curBlock.setSubState(Block.SubState.CHAINER);
					curBlock.setChainLength(chainLength);
				}
				isOccupied[x][curY-1] = true;
				boardState = State.MOVING;
				curY++;
				if(curY >= SwappidySwap.NUM_ROW)
					break;
				curBlock = blockGrid.getBlock(x, curY);
			}
		}
	}
	
	/**
	 * updates states of blocks (duh)
	 * but does no action of its own
	 */
	public void updateBlockState(){
		// ORDER MATTERS!
		
		boardState = State.STATIC;
		
		// find blank spaces which we are free to move into, and set all the blocks above those blanks to falling  
		for(int y=0;y<SwappidySwap.NUM_ROW;y++){
			for(int x=0;x<SwappidySwap.NUM_COL;x++){
				checkForFalling(x,y);
			}
		}
		
		for(int y=0;y<SwappidySwap.NUM_ROW;y++){
			for(int x=0;x<SwappidySwap.NUM_COL;x++){
				if(blockGrid.getBlock(x, y)==null) continue;

				// only check blocks in a normal or initial state (i.e. not falling or swapping)
				if(blockGrid.getBlock(x, y).isStable()){
					checkForCombosAtBlock(x,y);
				}  
			}
		}

	}

	/**
	 * does all the actions that the blocks should take according to their state
	 * e.g. shrink, swap, etc.
	 */
	public void actionUpdate(){
		/* let disappearing blocks disappear */
		for(int y=0;y<SwappidySwap.NUM_ROW;y++){
			for(int x=0;x<SwappidySwap.NUM_COL;x++){
				if(blockGrid.getBlock(x, y)!=null) blockGrid.getBlock(x, y).update();
			}
		}
	}

	public void update(){
		if(InputMaster.processKeyboardInput(this, cursor)){
			if(boardState == State.INITIAL) updateBlockState();
			actionUpdate();
		}
	}



	private void updateBoardState() {
		State newstate = State.STATIC;
		for(int y=0;y<SwappidySwap.NUM_ROW;y++){
			for(int x=0;x<SwappidySwap.NUM_COL;x++){
				if(blockGrid.getBlock(x, y)!=null){
					if(!blockGrid.getBlock(x, y).isStable()){
						newstate = State.MOVING;
						break;
					}
				}
			}
		}
		boardState = newstate;
	}

	public void draw(ShapeRenderer render){

		render.begin(ShapeType.Filled);
		render.setColor(getColor());
		render.rect(SwappidySwap.BOARD_POS.x, SwappidySwap.BOARD_POS.y, SwappidySwap.BOARD_DIM.x, SwappidySwap.BOARD_DIM.y);

		/* draw blocks */
		for(int y=0;y<SwappidySwap.NUM_ROW;y++){
			for(int x=0;x<SwappidySwap.NUM_COL;x++){
				if(blockGrid.getBlock(x, y)!=null) blockGrid.getBlock(x, y).draw(render, x, y); 
			}
		}
		render.end();

		/* draw cursor */
		render.begin(ShapeType.Line);
		cursor.draw(render);
		render.end();
	}

	private Color getColor() {
		if(SwappidySwap.DEBUG_COLORS){
			switch(boardState){
			case INITIAL:
				return Color.WHITE;
			case STATIC:
				return Color.YELLOW;
			case MOVING:
				return Color.BLACK;
			}
		}
		return Color.LIGHT_GRAY;
	}


	
	private boolean checkForCombosAtBlock(int x, int y){
		if(!blockGrid.getBlock(x, y).isStable()) return false;
		Point[] gridPositionsOfBlocksInCombo = detectBlocksInCombo(x,y);
		for(int j = 0; j < gridPositionsOfBlocksInCombo.length; j++){
			Point gridPos = gridPositionsOfBlocksInCombo[j];
			Block b = blockGrid.getBlock(gridPos.x, gridPos.y);
			b.setState(Block.State.DISAPPEARING);
			b.setComboLength(gridPositionsOfBlocksInCombo.length);
			boardState = State.MOVING;
		}
		return gridPositionsOfBlocksInCombo.length > 0;
	}


	/**
	 * checks if the block given as the parameter is in a combo.
	 * if it is not, return an empty array.
	 * if it is, return an array containing the indices of the blocks involved in the combo
	 * will throw a nullpointerexception if called for a null block
	 */
	private Point[] detectBlocksInCombo(int myX, int myY){

		Map<String, Integer> sameColorAsMe = checkColorsAroundMe(myX, myY);

		// don't count myself
		int h = sameColorAsMe.get("east")+sameColorAsMe.get("west");
		int v = sameColorAsMe.get("south")+sameColorAsMe.get("north");
		int numBlocksInHorizCombo =  h >= 2 ? h : 0;
		int numBlocksInVertCombo  =  v >= 2 ? v : 0;
		
		// array of the indices of the blocks that are part of the combo
		int comboLength = Math.max(0, numBlocksInVertCombo+numBlocksInHorizCombo);
		if(comboLength>0) comboLength++; //remember to include myself
		Point[] gridPositionsOfBlocksInCombo = new Point[comboLength];

		if(comboLength>0) gridPositionsOfBlocksInCombo[0] = new Point(myX, myY);  // include myself

		int cnt = 1; // for purposes of filling out inACombo

		if(numBlocksInHorizCombo > 0){
			for(int i = 0; i < sameColorAsMe.get("east"); i++){
				gridPositionsOfBlocksInCombo[cnt++] = new Point(myX + i + 1, myY);
			}
			for(int i = 0; i < sameColorAsMe.get("west"); i++){
				gridPositionsOfBlocksInCombo[cnt++] = new Point(myX - i - 1, myY);
			}   
		}

		if(numBlocksInVertCombo > 0){
			for(int i = 0; i < sameColorAsMe.get("north"); i++){
				gridPositionsOfBlocksInCombo[cnt++] = new Point(myX, myY + i + 1); 
			}
			for(int i = 0; i < sameColorAsMe.get("south"); i++){
				gridPositionsOfBlocksInCombo[cnt++] = new Point(myX, myY - i - 1); 
			}   
		}

		return gridPositionsOfBlocksInCombo;  
	}


	/**
	 * Checks all four directions for blocks of matching color
	 * returns an 
	 * @param checkMe: index of the block to check around
	 * @return
	 */
	Map<String,Integer> checkColorsAroundMe(int myX, int myY){

		HashMap<String,Integer> sameColorAsMe = new HashMap<String,Integer>();
		sameColorAsMe.put("north", 0);
		sameColorAsMe.put("south", 0);
		sameColorAsMe.put("west", 0);
		sameColorAsMe.put("east", 0);

		int myType = blockGrid.getBlock(myX, myY).getType();
		int curY = myY;
		int curX = myX;


		/* check all 4 directions
		 * 3 conditions to check:
		 * 1) the block isn't a NULL_BLOCK (empty space or a wall)
		 * 2) its color matches my color
		 * 3) it's in a normal state (e.g. not being swapped or falling) 
		 */
		// check up
		curY = myY + 1;
		while(curY < SwappidySwap.NUM_ROW && blockGrid.getBlock(curX, curY)!=null
				&& myType==blockGrid.getBlock(curX, curY).getType() 
				&& (blockGrid.getBlock(curX, curY).isStable() || blockGrid.getBlock(curX, curY).getState()==Block.State.DISAPPEARING) ){
			sameColorAsMe.put("north", sameColorAsMe.get("north") + 1);
			curY++;
		}
		curY = myY;

		// check to the right
		curX = myX + 1;
		while(curX < SwappidySwap.NUM_COL && blockGrid.getBlock(curX, curY)!=null
				&& myType==blockGrid.getBlock(curX, curY).getType()
				&& (blockGrid.getBlock(curX, curY).isStable() || blockGrid.getBlock(curX, curY).getState()==Block.State.DISAPPEARING) ){
			sameColorAsMe.put("east", sameColorAsMe.get("east") + 1);
			curX++;
		}
		curX = myX;

		// check below
		curY = myY - 1;
		while(curY >= 0  && blockGrid.getBlock(curX, curY)!=null
				&& myType==blockGrid.getBlock(curX, curY).getType() 
				&& (blockGrid.getBlock(curX, curY).isStable() || blockGrid.getBlock(curX, curY).getState()==Block.State.DISAPPEARING) ){
			sameColorAsMe.put("south", sameColorAsMe.get("south") + 1);
			curY--;
		}
		curY = myY;

		// check to the left
		curX = myX - 1;
		while(curX >= 0 && blockGrid.getBlock(curX, curY)!=null
				&& myType==blockGrid.getBlock(curX, curY).getType()
				&& (blockGrid.getBlock(curX, curY).isStable() || blockGrid.getBlock(curX, curY).getState()==Block.State.DISAPPEARING) ){
			sameColorAsMe.put("west", sameColorAsMe.get("west") + 1);
			curX--;
		}

		return sameColorAsMe;
	}


	/*
	 * Adds in a new row of blocks at the bottom only if the top row is empty
	 */
	public void raiseStack(){
		if(boardState!=State.STATIC) return;
		
		for(int y=SwappidySwap.NUM_ROW-1;y>=0;y--){
			for(int x=0;x<SwappidySwap.NUM_COL;x++){
				if(blockGrid.getBlock(x, y)==null) continue;
				if(y==SwappidySwap.NUM_ROW-1){
					gameOver();
					return;
				}
				blockGrid.moveBlockUp(x, y);	
			}
		}
		
		for(int x=0; x < SwappidySwap.NUM_COL; x++){
			blockGrid.setBlock(x, 0, new Block(
					new Point(x, 0),
					rng.nextInt(SwappidySwap.BLOCK_COLORS.length),
					this));
		}
	}


	private void gameOver() {
		System.out.println("Game overrr!!!!");
	}

	/**
	 * for testing
	 * @param b
	 */
	@SuppressWarnings("unused")
	private void setBlocks(Block[][] b){
		blockGrid.setBlocks(b);
		isOccupied = new boolean[b.length][b[0].length];
	}

	@SuppressWarnings("unused")
	private Block[][] getBlocks(){
		return blockGrid.getBlocksArray();
	}
	
	@SuppressWarnings("unused")
	private void moveCursorTo(Integer x, Integer y){
		cursor.moveTo(x, y);
	}

	public void handleCompletedShrinking(Point gridpos) {
		int chainLength = blockGrid.getBlock(gridpos.x, gridpos.y).getChainLength();
		blockGrid.setBlock(gridpos.x, gridpos.y, null);
		checkForFalling(gridpos.x, gridpos.y, chainLength + 1);
		updateBoardState();
	}

	public void handleCompletedFalling(int oldx, int oldy) {
		Block fallenBlock = blockGrid.getBlock(oldx, oldy);
		blockGrid.moveBlockDown(oldx, oldy); //occupy the spot below me
		isOccupied[oldx][oldy-1] = false;
		blockGrid.setBlock(oldx, oldy, null);
		if(oldy-2 >= 0 && (blockGrid.getBlock(oldx,oldy-2)==null || blockGrid.getBlock(oldx, oldy-2).getState()==Block.State.FALLING)){
			fallenBlock.setState(Block.State.FALLING);
			isOccupied[oldx][oldy-2] = true;
		}else{
			fallenBlock.setState(Block.State.NORMAL);
			if(!checkForCombosAtBlock(oldx, oldy-1)){
				fallenBlock.setSubState(Block.SubState.NORMAL);
				fallenBlock.setChainLength(0);
			}
			updateBoardState();
		}
	}
	
	public void handleCompletedSwapping(int leftx, int y) {
		Block temp = blockGrid.getBlock(leftx, y);
		blockGrid.setBlock(leftx, y, blockGrid.getBlock(leftx+1, y));
		blockGrid.setBlock(leftx+1, y, temp);
		if(temp!=null){
			temp.setState(Block.State.NORMAL);
			temp.setGridPosition(leftx+1, y);
			isOccupied[leftx+1][y] = false;
			if(y>0) checkForFalling(leftx+1, y-1);
			checkForCombosAtBlock(leftx+1, y);
		} else {
			checkForFalling(leftx+1, y);
		}
		Block b = blockGrid.getBlock(leftx, y);
		if(b!=null){
			b.setState(Block.State.NORMAL);
			b.setGridPosition(leftx, y);
			isOccupied[leftx][y] = false;
			if(y>0) checkForFalling(leftx, y-1);
			checkForCombosAtBlock(leftx, y);
		} else {
			checkForFalling(leftx, y);
		}
	}

	public void attemptSwap() {
		Point gridpos = cursor.getGridPosition();
//		if(boardState == State.MOVING) return;
		Block leftBlock = blockGrid.getBlock(gridpos.x, gridpos.y);
		Block rightBlock = blockGrid.getBlock(gridpos.x+1, gridpos.y);
		if((leftBlock!=null 
				&& !leftBlock.isStable())
			|| (rightBlock!=null 
					&& !rightBlock.isStable())
			|| isOccupied[gridpos.x][gridpos.y] || isOccupied[gridpos.x+1][gridpos.y])
			return; // can't swap!
		boolean swapRepExists = false;
		if(leftBlock!=null){
			leftBlock.setState(Block.State.SWAPPING);
			leftBlock.setSwapDirection(1);
			leftBlock.setSwapRepresentative();
			isOccupied[gridpos.x+1][gridpos.y] = true;
			swapRepExists = true;
		}
		if(rightBlock!=null){
			rightBlock.setState(Block.State.SWAPPING);
			rightBlock.setSwapDirection(-1);
			isOccupied[gridpos.x][gridpos.y] = true;
			if(!swapRepExists) rightBlock.setSwapRepresentative();
		}
	}


}
