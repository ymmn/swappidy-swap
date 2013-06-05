package com.swappidy.swap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class InputMaster {
	
	public static void processKeyboardInput(GameBoard gameboard, Cursor cursor){
		if(Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) 
			cursor.moveBy(-1*SwappidySwap.BLOCK_SIZE, 0);
		if(Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)) 
			cursor.moveBy(1*SwappidySwap.BLOCK_SIZE, 0);
		if(Gdx.input.isKeyPressed(Keys.DPAD_UP)) 
			cursor.moveBy(0, 1*SwappidySwap.BLOCK_SIZE);
		if(Gdx.input.isKeyPressed(Keys.DPAD_DOWN)) 
			cursor.moveBy(0, -1*SwappidySwap.BLOCK_SIZE);
		if(Gdx.input.isKeyPressed(Keys.SPACE))
			gameboard.raiseStack();
		if(Gdx.input.isKeyPressed(Keys.Z))
			gameboard.attemptSwap();
	}
	
}