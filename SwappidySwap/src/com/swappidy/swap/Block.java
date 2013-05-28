package com.swappidy.swap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Block {

	  Vector2 position;
	  Vector2 DIMS;
	  Color myColor;
	  
	  Block(Vector2 pos, Color col, int size){
		    position = pos;
		    myColor = col;
		    DIMS = new Vector2(size, size);
	  }
	  
	  void draw(ShapeRenderer render){
		  render.setColor(myColor);
		  render.rect(position.x, position.y, DIMS.x, DIMS.y);
	  }

	public void drawBorder(ShapeRenderer render) {
		render.rect(position.x, position.y, DIMS.x, DIMS.y);
	}
	  
}
