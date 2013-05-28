package com.swappidy.swap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Game;


public class SwappidySwap extends Game {

	public static final int BLOCK_SIZE = 1;
	public static final int WORLD_HEIGHT = 7;
	public static final int WORLD_WIDTH = 10;
	public static final int NUM_ROW = 7;
	public static final int NUM_COL = 10;
	
	public static final Color[] BLOCK_COLORS = new Color[]{
			new Color(0, 1, 0, 1), // green
			new Color(0, 0, 1, 1), // blue
			new Color(1, 0, 0, 1), // red
			new Color(1, 1, 0, 1), // purple
	};
	
	SpriteBatch spriteBatch;
    BitmapFont font;
    int screenWidth, screenHeight;
    private OrthographicCamera cam;
    private GameBoard gameboard;
	private ShapeRenderer renderer;
    
	@Override
	public void create() {
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        new Pixmap(32, 32, Pixmap.Format.RGB565);
        screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
        renderer = new ShapeRenderer();
        this.cam = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
		this.cam.position.set(5, 3.5f, 0);
		this.cam.update();
		gameboard = new GameBoard();
	}


	@Override
	public void render() {
		  update();
			   
		GL10 gl = Gdx.graphics.getGL10();
		gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		gl.glClearColor(1, 1, 1, 0);
		
		renderer.setProjectionMatrix(cam.combined);
		gameboard.draw(renderer);
		renderer.end();
	}

	void update(){
		gameboard.update();
	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
