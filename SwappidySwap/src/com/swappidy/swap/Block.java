package com.swappidy.swap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Block {

	public enum State{
		NORMAL, DISAPPEARING, FALLING, SWAPPING
	}

	private static final int SHRINK_SPEED = 1;

	private State state = State.NORMAL;
	private Vector2 position;
	private Vector2 DIMS;
	private Color myColor;
	private int shrinkBy;
	private Vector2 myGridPos;
	private int swapDirection;
	private GameBoard gameboard;

	Block(Vector2 pos, Color col, GameBoard gboard){
		position = pos;
		myColor = col;
		gameboard = gboard;
		myGridPos = new Vector2(pos.x/SwappidySwap.BLOCK_SIZE, pos.y/SwappidySwap.BLOCK_SIZE);
		DIMS = new Vector2(SwappidySwap.BLOCK_SIZE, SwappidySwap.BLOCK_SIZE);
	}

	private void shrink(){ // returns true if small enough
		if(shrinkBy<DIMS.x){
			shrinkBy += SHRINK_SPEED;
			return;
		}
		gameboard.removeBlock(myGridPos);
	}
	
	public void update(){
		if(state==State.DISAPPEARING)
			shrink();
		if(state==State.FALLING)
			fallDown();
		if(state==State.SWAPPING)
			swap();
	}
	
	private void fallDown(){
		// if finished falling a complete grid coordinate, I no longer occupy the spot
		// I fell from
		int targetYGridPos = (int) (myGridPos.y - 1);
		int targetYPos = targetYGridPos*SwappidySwap.BLOCK_SIZE;
		if( position.y - targetYPos <= GameBoard.FALL_SPEED ){
			position.y = targetYPos;
			gameboard.handleCompletedFalling((int)myGridPos.x, (int)myGridPos.y);
			myGridPos.y--;

			//remove this later
			state = Block.State.NORMAL;
		}
		else{ 
			position.y += -1*GameBoard.FALL_SPEED;
		}
	}
	
	private void swap(){
		// if finished falling a complete grid coordinate, I no longer occupy the spot
		// I fell from
		int targetXGridPos = (int) (myGridPos.x + swapDirection);
		int targetXPos = targetXGridPos*SwappidySwap.BLOCK_SIZE;
		if( Math.abs(position.x - targetXPos) <= GameBoard.SWAP_SPEED ){
			position.x = targetXPos;
			if(swapDirection==1) // we only need one of the pair to notify the gameboard
				gameboard.handleCompletedSwapping((int)myGridPos.x, (int)myGridPos.y);
			myGridPos.x += swapDirection;

			//remove this later
			state = Block.State.NORMAL;
		}
		else{ 
			position.x += swapDirection*GameBoard.SWAP_SPEED;
		}
	}

	void draw(ShapeRenderer render){
		render.setColor(myColor);
		render.rect(position.x+shrinkBy/2, position.y+shrinkBy/2, DIMS.x-shrinkBy, DIMS.y-shrinkBy);
	}

	public void setState(State s){
		state = s;
	}

	public void drawBorder(ShapeRenderer render) {
		render.rect(position.x+shrinkBy/2, position.y+shrinkBy/2, DIMS.x-shrinkBy, DIMS.y-shrinkBy);
	}

	public State getState(){
		return state;
	}
	
	public Vector2 getPosition(){
		return position;
	}

	public void setPosition(Vector2 pos) {
		position = pos;
	}

	public Color getColor() {
		return myColor;
	}

	public void setSwapDirection(int i) {
		swapDirection = i;
	}
	
}
