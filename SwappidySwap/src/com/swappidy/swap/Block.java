package com.swappidy.swap;

import java.awt.Point;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.swappidy.swap.Block.SubState;

public class Block {

	public enum State{
		NORMAL, DISAPPEARING, FALLING, SWAPPING
	}

	public enum SubState {
		CHAINER, NORMAL
	}

	private State state = State.NORMAL;
	private SubState substate;
	private Vector2 position;
	private Vector2 DIMS;
	private int shrinkBy;
	private Point myGridPos;
	private int swapDirection;
	private int chainLength = 0;
	private Color myColor;
	private GameBoard gameboard;
	private boolean swapRep;
	private int gap = 10;
	private int type;
	private int comboLength;

	Block(Point pos, int type, GameBoard gboard){
		position = new Vector2(pos.x*SwappidySwap.BLOCK_SIZE.x, pos.y*SwappidySwap.BLOCK_SIZE.y);
		this.type = type;
		myColor = SwappidySwap.BLOCK_COLORS[type];
		gameboard = gboard;
		myGridPos = pos;
		DIMS = new Vector2(SwappidySwap.BLOCK_SIZE.x, SwappidySwap.BLOCK_SIZE.y);
	}

	private void shrink(){ // returns true if small enough
		if(shrinkBy<DIMS.x){
			shrinkBy += GameBoard.SHRINK_SPEED;
			return;
		}
		gameboard.handleCompletedShrinking(myGridPos);
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
		int targetYGridPos = myGridPos.y - 1;
		int targetYPos = targetYGridPos*SwappidySwap.BLOCK_SIZE.y;
		if( position.y - targetYPos <= GameBoard.FALL_SPEED ){
			position.y = targetYPos;
			gameboard.handleCompletedFalling(myGridPos.x, myGridPos.y);
		}
		else{ 
			position.y += -1*GameBoard.FALL_SPEED;
		}
	}

	private void swap(){
		// if finished falling a complete grid coordinate, I no longer occupy the spot
		// I fell from
		int targetXGridPos = myGridPos.x + swapDirection;
		int targetXPos = targetXGridPos*SwappidySwap.BLOCK_SIZE.x;
		if( Math.abs(position.x - targetXPos) <= GameBoard.SWAP_SPEED ){
			if(swapRep){ // we only need one of the pair to notify the gameboard
				if(swapDirection==1) // if we're the left block, send our pos
					gameboard.handleCompletedSwapping(myGridPos.x, myGridPos.y);
				else // we're the right block, so send left block's pos
					gameboard.handleCompletedSwapping(myGridPos.x-1, myGridPos.y);

			}
			swapRep = false;
		}
		else{
			position.x += swapDirection*GameBoard.SWAP_SPEED;
		}
	}

	void draw(ShapeRenderer render, int xIndex, int yIndex){
		render.setColor(getColor());
		render.rect(SwappidySwap.BOARD_POS.x + position.x+gap+shrinkBy/2, SwappidySwap.BOARD_POS.y + position.y+gap+shrinkBy/2, DIMS.x-gap-shrinkBy, DIMS.y-gap-shrinkBy);
		String txt = getDrawingText(xIndex, yIndex);
		if(txt.length() > 0){
			SwappidySwap.font.draw(SwappidySwap.spriteBatch, txt, position.x+(DIMS.x/2), position.y+(DIMS.y/2));
		}

	}

	private String getDrawingText(int x, int y) {
		String s = "";
		if(state==State.DISAPPEARING){
			if(comboLength > 4)
				s = "CMBO" + comboLength;
			if(substate==SubState.CHAINER)
				s = "CHAIN" + chainLength;
		}
		if(SwappidySwap.DEBUG_COLORS)
			s = "(" + myGridPos.x + ", " + myGridPos.y + ")";
			//s = "DBG" + chainLength;
		return s;
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

	public int getType(){
		return type;
	}

	private Color getColor() {
		if(SwappidySwap.DEBUG_COLORS){
			switch(state){
			case NORMAL:
				return Color.BLUE;
			case DISAPPEARING:
				return Color.WHITE;
			case FALLING:
				return Color.RED;
			case SWAPPING:
				return Color.CYAN;
			}
		}
		return myColor;
	}

	public void setSwapDirection(int i) {
		swapDirection = i;
	}

	public void moveGridPos(int i, int j) {
		position = position.add(SwappidySwap.BLOCK_SIZE.x*i, SwappidySwap.BLOCK_SIZE.y*j);
		myGridPos.move(myGridPos.x + i, myGridPos.y + j);
	}

	public void setSwapRepresentative() {
		swapRep = true;
	}

	public boolean isStable() {
		return state==State.NORMAL;
	}

	public void setGridPosition(int x, int y) {
		myGridPos = new Point(x, y);
		position = new Vector2(x*SwappidySwap.BLOCK_SIZE.x, y*SwappidySwap.BLOCK_SIZE.y);
	}

	public void setType(int col) {
		type = col;
	}

	public void setComboLength(int length) {
		comboLength = length;
	}

	public void setSubState(SubState ss) {
		substate = ss;
	}

	public void setChainLength(int l){
		chainLength = l;
	}

	public int getChainLength() {
		return chainLength;
	}

}
