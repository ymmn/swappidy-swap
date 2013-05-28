package com.swappidy.swap;

import java.util.Random;

import com.badlogic.gdx.ApplicationListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Game implements ApplicationListener {

	public static final int BLOCK_SIZE = 3;
	public static final int WORLD_HEIGHT = 10;
	public static final int WORLD_WIDTH = 7;
	
	Color[] blockColors = new Color[]{
			new Color(0, 1, 0, 1), // green
			new Color(0, 0, 1, 1), // blue
			new Color(1, 0, 0, 1), // red
			new Color(1, 1, 0, 1), // purple
	};
	
	SpriteBatch spriteBatch;
    BitmapFont font;
    CharSequence str = "Hello World!";
    int screenWidth, screenHeight;
    private OrthographicCamera cam;
    Random rng = new Random();

	ShapeRenderer debugRenderer;
    
	@Override
	public void create() {
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        new Pixmap(32, 32, Pixmap.Format.RGB565);
        screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
        debugRenderer = new ShapeRenderer();
        this.cam = new OrthographicCamera(WORLD_HEIGHT, WORLD_WIDTH);
		this.cam.position.set(5, 3.5f, 0);
		this.cam.update();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		GL10 gl = Gdx.graphics.getGL10();
		gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		gl.glClearColor(1, 1, 1, 0);

		debugRenderer.setProjectionMatrix(cam.combined);
		debugRenderer.begin(ShapeType.Filled);
		for(int i = 0; i < WORLD_WIDTH; i++){
			for(int j = 0; j < WORLD_HEIGHT; j++){
				debugRenderer.setColor(blockColors[rng.nextInt(blockColors.length)]);
				debugRenderer.rect(i, j, 1,1);
			}
		}
		debugRenderer.end();
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
