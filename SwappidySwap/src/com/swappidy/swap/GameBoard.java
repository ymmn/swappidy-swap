package com.swappidy.swap;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

/**
 * The thing that holds blocks. 
 * In the case of 1 player, there's only one board for the game.
 * In the case of 2 players, there are two.
 * @author abdul
 *
 */
public class GameBoard {

	public static float FALL_SPEED = 1;
	/**
	 * represents empty space (a real null in the blocks array), or a wall
	 */
	private static final int NULL_BLOCK = 10323;
	Block blocks [] = new Block[SwappidySwap.NUM_ROW * SwappidySwap.NUM_COL];
	Random rng = new Random();
	Cursor cursor;

	/**
	 * random initiation of blocks. Fills entire screen.
	 */
	private void initBlocks(){
		int i = 0;
		for(int y=0;y<SwappidySwap.NUM_ROW;y++){
			for(int x=0;x<SwappidySwap.NUM_COL;x++){
				blocks[i] = new Block(
						new Vector2(SwappidySwap.BLOCK_SIZE*x, SwappidySwap.BLOCK_SIZE*y),
						SwappidySwap.BLOCK_COLORS[ rng.nextInt(SwappidySwap.BLOCK_COLORS.length) ]);
				i++;
			}
		}
	}

	public GameBoard(){
		initBlocks();
		blocks = LeDebugTools.createBoardAtState(LeDebugTools.simpleVertCombo);
		// testShrinking(blocks);
		cursor = new Cursor(new Vector2(1,1));
	}



	public void processKeyboardInput(){
		if(Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) 
			cursor.moveBy(-1*SwappidySwap.BLOCK_SIZE, 0);
		if(Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)) 
			cursor.moveBy(1*SwappidySwap.BLOCK_SIZE, 0);
		if(Gdx.input.isKeyPressed(Keys.DPAD_UP)) 
			cursor.moveBy(0, 1*SwappidySwap.BLOCK_SIZE);
		if(Gdx.input.isKeyPressed(Keys.DPAD_DOWN)) 
			cursor.moveBy(0, -1*SwappidySwap.BLOCK_SIZE);
	}

	/**
	 * updates states of blocks (duh)
	 * but does no action of its own
	 */
	public void updateBlockState(){
		// find blank spaces which we are free to move into, and set all the blocks above those blanks to falling  
		for(int i = 0; i < blocks.length; i++){ 
			int curPos = i;
			if(blocks[i]==null)  //find the ones that should be falling and set them as falling
				while( (curPos=getIndexOfBlockAboveMe(curPos))!=NULL_BLOCK && blocks[curPos].getState()==Block.State.NORMAL){
					blocks[curPos].setState(Block.State.FALLING);
				}
		}

		detectCombos();

	}

	/**
	 * does all the actions that the blocks should take according to their state
	 * e.g. shrink, swap, etc.
	 */
	public void actionUpdate(){
		fallDown();

		/* let disappearing blocks disappear */
		for(int i = 0; i < blocks.length; i++){
			if(blocks[i]==null) continue;

			if(blocks[i].getState()==Block.State.DISAPPEARING && blocks[i].shrink())
				blocks[i] = null;
		}
	}

	public void update(){
		updateBlockState();
		actionUpdate();
		//System.out.println("hi");
	}

	int getIndexOfBlockAboveMe(int refBlock){
		if( (refBlock+SwappidySwap.NUM_COL<0 || refBlock+SwappidySwap.NUM_COL>=blocks.length) || blocks[refBlock+SwappidySwap.NUM_COL]==null) return NULL_BLOCK;
		return refBlock+SwappidySwap.NUM_COL; 
	}


	private Vector2 getPositionFromBlockIndex(int blockIndex){
		int x = blockIndex % SwappidySwap.NUM_COL;
		int y = blockIndex / SwappidySwap.NUM_COL;
		return new Vector2(SwappidySwap.BLOCK_SIZE * x, SwappidySwap.BLOCK_SIZE*y);
	}

	/**
	 * make blocks fall if they should
	 */
	void fallDown(){

		int curPos;
		for(int i = 0; i < blocks.length; i++){
			curPos = i;

			if(blocks[i]==null) continue;

			//if midway falling, keep falling
			if(blocks[i].getState()==Block.State.FALLING){

				// if finished falling a complete grid coordinate, I no longer occupy the spot
				// I fell from
				int newIndex = curPos-SwappidySwap.NUM_COL;
				if( blocks[curPos].getPosition().y-getPositionFromBlockIndex(newIndex).y <= FALL_SPEED ){
					blocks[curPos].setPosition(getPositionFromBlockIndex(newIndex));
					blocks[newIndex] = blocks[curPos]; //occupy the spot below me
					blocks[curPos] = null;

					//remove this later
					blocks[newIndex].setState(Block.State.NORMAL);
				}
				else{ 
					blocks[curPos].move(0, -1*FALL_SPEED);
				}
			}

		} 

	}

	public void draw(ShapeRenderer render){

		/* draw blocks */
		render.begin(ShapeType.Filled);
		for(int i=0;i<blocks.length;i++){
			if(blocks[i]!=null) blocks[i].draw(render); 
		}
		render.end();

		/* draw borders on the blocks so they don't mush */
		render.begin(ShapeType.Line);
		render.setColor(Color.BLACK);
		for(int i=0;i<blocks.length;i++){
			if(blocks[i]!=null) blocks[i].drawBorder(render); 
		}

		cursor.draw(render);
	}

	void detectCombos(){

		int[] indicesOfBlocksInCombo;
		for(int i=0; i < blocks.length; i++){

			if(blocks[i]==null) continue;
			
			// only check blocks in a normal state (i.e. not falling or swapping)                        
			if(blocks[i].getState()==Block.State.NORMAL){

				indicesOfBlocksInCombo = detectBlocksInCombo(i);

				// set the blocks to disappear 
				for(int j = 0; j < indicesOfBlocksInCombo.length; j++){
					blocks[indicesOfBlocksInCombo[j]].setState(Block.State.DISAPPEARING);
				}

			}  
		}

	}


	/**
	 * checks if the block given as the parameter is in a combo.
	 * if it is not, return an empty array.
	 * if it is, return an array containing the indices of the blocks involved in the combo
	 * will throw a nullpointerexception if called for a null block
	 */
	int[] detectBlocksInCombo(int checkMe){

		int curBlock;

		Map<String, Integer> sameColorAsMe = checkColorsAroundMe(checkMe);

		// don't count myself
		int h = sameColorAsMe.get("east")+sameColorAsMe.get("west");
		int v = sameColorAsMe.get("south")+sameColorAsMe.get("north");
		int numBlocksInHorizCombo =  h >= 2 ? h : 0;
		int numBlocksInVertCombo  =  v >= 2 ? v : 0;

		// array of the indices of the blocks that are part of the combo
		int comboLength = Math.max(0, numBlocksInVertCombo+numBlocksInHorizCombo);
		if(comboLength>0) comboLength++; //remember to include myself
		int[] indicesOfBlocksInCombo = new int[comboLength];

		if(comboLength>0) indicesOfBlocksInCombo[0] = checkMe;  // include myself

		int cnt = 1; // for purposes of filling out inACombo

		curBlock = checkMe;
		if(numBlocksInHorizCombo > 0){
			for(int i = 0; i < sameColorAsMe.get("east"); i++){
				indicesOfBlocksInCombo[cnt++]=curBlock=getIndexOfBlockRightOfMe(curBlock);
			}
			curBlock=checkMe;
			for(int i = 0; i < sameColorAsMe.get("west"); i++){
				indicesOfBlocksInCombo[cnt++]=curBlock=getIndexOfBlockLeftOfMe(curBlock);
			}   
		}

		curBlock = checkMe;
		if(numBlocksInVertCombo > 0){
			for(int i = 0; i < sameColorAsMe.get("north"); i++){
				indicesOfBlocksInCombo[cnt++]=curBlock=getIndexOfBlockAboveMe(curBlock);
			}
			curBlock=checkMe;
			for(int i = 0; i < sameColorAsMe.get("south"); i++){
				indicesOfBlocksInCombo[cnt++]=curBlock=getIndexOfBlockBelowMe(curBlock);
			}   
		}

		return indicesOfBlocksInCombo;  
	}


	/**
	 * Checks all four directions for blocks of matching color
	 * returns an 
	 * @param checkMe: index of the block to check around
	 * @return
	 */
	Map<String,Integer> checkColorsAroundMe(int checkMe){

		HashMap<String,Integer> sameColorAsMe = new HashMap<String,Integer>();
		sameColorAsMe.put("north", 0);
		sameColorAsMe.put("south", 0);
		sameColorAsMe.put("west", 0);
		sameColorAsMe.put("east", 0);
		
		Color myColor = blocks[checkMe].getColor();
		int curBlock; 
		

		/* check all 4 directions
		 * 3 conditions to check:
		 * 1) the block isn't a NULL_BLOCK (empty space or a wall)
		 * 2) its color matches my color
		 * 3) it's in a normal state (e.g. not being swapped or falling) 
		 */
		// check up
		curBlock = checkMe;
		while(getIndexOfBlockAboveMe(curBlock)!=NULL_BLOCK && myColor==blocks[curBlock=getIndexOfBlockAboveMe(curBlock)].getColor() 
				&& (blocks[curBlock].getState()==Block.State.NORMAL) )
			sameColorAsMe.put("north", sameColorAsMe.get("north") + 1);

		// check to the right
		curBlock = checkMe;
		while(getIndexOfBlockRightOfMe(curBlock)!=NULL_BLOCK && myColor==blocks[curBlock=getIndexOfBlockRightOfMe(curBlock)].getColor()
				&& (blocks[curBlock].getState()==Block.State.NORMAL) )
			sameColorAsMe.put("east", sameColorAsMe.get("east") + 1);

		// check below
		curBlock = checkMe;
		while(getIndexOfBlockBelowMe(curBlock)!=NULL_BLOCK && myColor==blocks[curBlock=getIndexOfBlockBelowMe(curBlock)].getColor() 
				&& (blocks[curBlock].getState()==Block.State.NORMAL) )
			sameColorAsMe.put("south", sameColorAsMe.get("south") + 1);

		// check to the left
		curBlock = checkMe;
		while(getIndexOfBlockLeftOfMe(curBlock)!=NULL_BLOCK && myColor==blocks[curBlock=getIndexOfBlockLeftOfMe(curBlock)].getColor() 
				&& (blocks[curBlock].getState()==Block.State.NORMAL) )
			sameColorAsMe.put("west", sameColorAsMe.get("west") + 1);

		return sameColorAsMe;

	}

	int getIndexOfBlockBelowMe(int refBlock){
		if( (refBlock-SwappidySwap.NUM_COL>=blocks.length ||
				refBlock-SwappidySwap.NUM_COL<0) || // bounds check
				blocks[refBlock-SwappidySwap.NUM_COL]==null) return NULL_BLOCK;
		return refBlock-SwappidySwap.NUM_COL; 
	}

	int getIndexOfBlockRightOfMe(int refBlock){
		if(refBlock+1>=blocks.length || // bounds check
				refBlock%SwappidySwap.NUM_COL==(SwappidySwap.NUM_COL-1) ||  // next to wall
				blocks[refBlock+1]==null) return NULL_BLOCK;
		return refBlock+1; 
	}

	int getIndexOfBlockLeftOfMe(int refBlock){
		if(refBlock-1<0 || // bounds check
				refBlock%SwappidySwap.NUM_COL==0 || // next to wall
				blocks[refBlock-1]==null) return NULL_BLOCK;
		return refBlock-1; 
	}  

	/**
	 * for testing
	 * @param b
	 */
	@SuppressWarnings("unused")
	private void setBlocks(Block[] b){
		blocks = b;
	}

	@SuppressWarnings("unused")
	private Block[] getBlocks(){
		return blocks;
	}


}
