package com.swappidy.swap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Cursor {

	  Vector2 position;
	  Vector2 DIMS;
	  Color myColor = Color.GRAY;
	  
	  public Cursor(Vector2 pos){
		    position = pos;
		    DIMS = new Vector2(SwappidySwap.BLOCK_SIZE*2, SwappidySwap.BLOCK_SIZE);
	  }
	  
	  void draw(ShapeRenderer render){
		  render.setColor(myColor);
		  render.rect(position.x, position.y, DIMS.x, DIMS.y);
	  }
	  
	  void moveBy(int x, int y){
		  position.x += x;
		  position.y += y;
	  }

}
