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
		//blocks = LeDebugTools.createBoardAtState(LeDebugTools.threeX3);
		//testShrinking(blocks);
		cursor = new Cursor(new Vector2(1,1));
	}

	public void update(){
		if(Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) 
			cursor.moveBy(-1*SwappidySwap.BLOCK_SIZE, 0);
		if(Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)) 
			cursor.moveBy(1*SwappidySwap.BLOCK_SIZE, 0);
		if(Gdx.input.isKeyPressed(Keys.DPAD_UP)) 
			cursor.moveBy(0, 1*SwappidySwap.BLOCK_SIZE);
		if(Gdx.input.isKeyPressed(Keys.DPAD_DOWN)) 
			cursor.moveBy(0, -1*SwappidySwap.BLOCK_SIZE);
		
		for(int i = 0; i < blocks.length; i++){
			if(blocks[i]==null) continue;
			
			if(blocks[i].getState()==Block.State.DISAPPEARING && blocks[i].shrink())
				blocks[i] = null;
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


}
