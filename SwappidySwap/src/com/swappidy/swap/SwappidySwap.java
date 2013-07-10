package com.swappidy.swap;

import java.awt.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Game;


public class SwappidySwap extends Game {


	public static int NUM_ROW = 7;
	public static int NUM_COL = 10;
	public static final Point WORLD_DIM = new Point(NUM_COL*100, NUM_ROW*100);
	public static final Point BOARD_DIM = new Point(NUM_COL*100, NUM_ROW*90);
	public static Point BLOCK_SIZE = new Point(BOARD_DIM.x/NUM_COL, BOARD_DIM.y/NUM_ROW);
	public static Point BOARD_POS = new Point(0, 0);
	public static boolean TICK_BY_TICK = false;
	public static final boolean DEBUG_COLORS = true;
	public static boolean TESTING = false;

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
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		renderer = new ShapeRenderer();
		this.cam = new OrthographicCamera(WORLD_DIM.x, WORLD_DIM.y);
		this.cam.position.set(WORLD_DIM.x/2, WORLD_DIM.y/2, 0);
		 font = new BitmapFont();

		this.cam.update();
		gameboard = new GameBoard();
	}


	@Override
	public void render() {
		update();
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
//		spriteBatch.begin();
//		font.draw(spriteBatch, "my-string", 200, 200);
//		spriteBatch.end();
		
		renderer.setProjectionMatrix(cam.combined);
		gameboard.draw(renderer);
	}

	void update(){
		gameboard.update();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

}
