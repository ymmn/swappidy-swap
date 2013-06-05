package com.swappidy.swap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Block {

	public enum State{
		NORMAL, DISAPPEARING, FALLING
	}

	private static final int SHRINK_SPEED = 1;

	private State state = State.NORMAL;
	private Vector2 position;
	private Vector2 DIMS;
	private Color myColor;
	private int shrinkBy;
	private Vector2 myGridPos;
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
			move(0, -1*GameBoard.FALL_SPEED);
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

	public void move(int i, float j) {
		position = new Vector2(position.x + i, position.y + j);
	}

	public Color getColor() {
		return myColor;
	}
	
}
