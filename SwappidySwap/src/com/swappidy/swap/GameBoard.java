package com.swappidy.swap;

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
	private static final int NULL_BLOCK = 10323;
	Block blocks [] = new Block[SwappidySwap.NUM_ROW * SwappidySwap.NUM_COL];
	Random rng = new Random();
	Cursor cursor;

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
		blocks = LeDebugTools.createBoardAtState(LeDebugTools.fallingTest);
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
	
	public void updateBlockState(){
		// find blank spaces which we are free to move into, and set all the blocks above those blanks to falling  
		for(int i = 0; i < blocks.length; i++){ 
			int curPos = i;
			if(blocks[i]==null)  //find the ones that should be falling and set them as falling
				while( (curPos=getBlockAboveMe(curPos))!=NULL_BLOCK && blocks[curPos].getState()==Block.State.NORMAL){
					blocks[curPos].setState(Block.State.FALLING);
					System.out.println("woot");
				}
		}
		
		for(int i = 0; i < blocks.length; i++){
			if(blocks[i]==null) continue;

			if(blocks[i].getState()==Block.State.DISAPPEARING && blocks[i].shrink())
				blocks[i] = null;
		}
	}
	
	public void actionUpdate(){
		fallDown();
	}
	
	public void update(){
		updateBlockState();
		actionUpdate();
		//System.out.println("hi");
	}

	int getBlockAboveMe(int refBlock){
		if( (refBlock+SwappidySwap.NUM_COL<0 || refBlock+SwappidySwap.NUM_COL>=blocks.length) || blocks[refBlock+SwappidySwap.NUM_COL]==null) return NULL_BLOCK;
		return refBlock+SwappidySwap.NUM_COL; 
	}

	private Vector2 getCoordinatePosition(int blockIndex){
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
				if( blocks[curPos].getPosition().y-getCoordinatePosition(newIndex).y <= FALL_SPEED ){
					blocks[curPos].setPosition(getCoordinatePosition(newIndex));
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
