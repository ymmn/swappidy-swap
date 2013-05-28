package com.swappidy.swap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Block {

	public enum State{
		NORMAL, DISAPPEARING
	}

	private static final int SHRINK_SPEED = 1;

	private State state = State.NORMAL;
	private Vector2 position;
	private Vector2 DIMS;
	private Color myColor;
	private int shrinkBy;

	Block(Vector2 pos, Color col){
		position = pos;
		myColor = col;
		DIMS = new Vector2(SwappidySwap.BLOCK_SIZE, SwappidySwap.BLOCK_SIZE);
	}

	public boolean shrink(){ // returns true if small enough
		if(shrinkBy<DIMS.x){
			shrinkBy += SHRINK_SPEED;
			return false;
		}
		return true;
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
	
}
