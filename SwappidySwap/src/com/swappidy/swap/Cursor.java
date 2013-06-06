package com.swappidy.swap;

import java.awt.Point;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Cursor {

	  private static final int CURSOR_DELAY = 100000000;
	  private Vector2 position;
	  private Vector2 DIMS;
	  private Color myColor = Color.GRAY;
	  private Point gridPos;
	  private long lastMoved;
	  
	  
	  public Cursor(Vector2 pos){
		    position = pos;
		    gridPos = new Point((int)(pos.x/SwappidySwap.BLOCK_SIZE), (int)(pos.y/SwappidySwap.BLOCK_SIZE));
		    DIMS = new Vector2(SwappidySwap.BLOCK_SIZE*2, SwappidySwap.BLOCK_SIZE);
	  }
	  
	  void draw(ShapeRenderer render){
		  render.setColor(myColor);
		  render.rect(position.x, position.y, DIMS.x, DIMS.y);
	  }
	  
	  void moveTo(int x, int y){
		  position.x = SwappidySwap.BLOCK_SIZE*x;
		  position.y = SwappidySwap.BLOCK_SIZE*y;
		  gridPos.x = x;
		  gridPos.y = y;
	  }
	  
	  void moveBy(int x, int y){
		  long l = System.nanoTime();
		  if(l - lastMoved > CURSOR_DELAY){
			  lastMoved = l;
		  } else{
			  return;
		  }
		  if(gridPos.x==SwappidySwap.NUM_COL-2 && x>0) // at wall
			  return;
		  if(gridPos.x==0 && x<0)
			  return;
		  if(gridPos.y==0 && y<0)
			  return;
		  if(gridPos.y==SwappidySwap.NUM_ROW-1 && y>0)
			  return;
		  position.x += SwappidySwap.BLOCK_SIZE*x;
		  position.y += SwappidySwap.BLOCK_SIZE*y;
		  gridPos.x += x;
		  gridPos.y += y;
	  }
	  
	  public Vector2 getPosition(){
		  return position;
	  }
	  
	  public Point getGridPosition(){
		  return gridPos;
	  }

}
