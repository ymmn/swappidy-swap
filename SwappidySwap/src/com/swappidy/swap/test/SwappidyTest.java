package com.swappidy.swap.test;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import com.swappidy.swap.Block;
import com.swappidy.swap.GameBoard;
import com.swappidy.swap.LeDebugTools;
import com.swappidy.swap.SwappidySwap;

public class SwappidyTest {

	GameBoard gboard;
	
	@Before
	public void setUp() throws Exception {
		gboard = new GameBoard();
		GameBoard.FALL_SPEED = 1;
		GameBoard.SWAP_SPEED = 1;
		SwappidySwap.BLOCK_SIZE = 100;
	}

	@Test
	public void testFallingBlockState() {
		callPrivateMethod("setBlocks", new Object[]{ LeDebugTools.createBoardAtState(LeDebugTools.fallingTest,gboard) });
		gboard.updateBlockState();
		Block[][] blocks = (Block[][])callPrivateMethod("getBlocks", new Object[0]);
		for(int x = 0; x < 3; x++){
			if(blocks[x][0].getState()!=Block.State.NORMAL)
				fail("Blocks are falling when they shouldn't");
			if(blocks[x][2].getState()!=Block.State.FALLING)
				fail("Blocks aren't falling when they should");
		}
	}
	
	@Test
	public void testSwappingBlockState() {
		callPrivateMethod("setBlocks", new Object[]{ LeDebugTools.createBoardAtState(LeDebugTools.twoX2,gboard) });
		callPrivateMethod("moveCursorTo", new Object[]{ 0,0 });
		gboard.attemptSwap();
		gboard.updateBlockState();
		Block[][] blocks = (Block[][])callPrivateMethod("getBlocks", new Object[0]);
		for(int x = 0; x < 2; x++){
			if(blocks[x][0].getState()!=Block.State.SWAPPING)
				fail("Blocks aren't swapping when they should");
			if(blocks[x][1].getState()!=Block.State.NORMAL)
				fail("Aaaaand something funky is happening up there");
		}
	}
	
	@Test
	public void testSwappingBlocks() {
		callPrivateMethod("setBlocks", new Object[]{ LeDebugTools.createBoardAtState(LeDebugTools.twoX2,gboard) });
		callPrivateMethod("moveCursorTo", new Object[]{ 0,0 });
		Block[][] blocks = (Block[][])callPrivateMethod("getBlocks", new Object[0]);
		Block oldLeft = blocks[0][0];
		Block oldRight = blocks[1][0];
		gboard.attemptSwap();
		gboard.updateBlockState();
		for(int i = 0; i < 101; i++){
			gboard.actionUpdate();
		}
		blocks = (Block[][])callPrivateMethod("getBlocks", new Object[0]);
		if(blocks[0][0]==oldLeft)
			fail("Didn't swap!");
		if(blocks[1][0]==oldRight)
			fail("Didn't swap!");
		for(int x = 0; x < 2; x++){
			if(blocks[x][0].getState()==Block.State.SWAPPING)
				fail("Why still swapping!");
			if(blocks[x][0].getState()!=Block.State.NORMAL)
				fail("Weird state!");
		}
	}
	
	@Test
	public void testFallingBlocks() {
		callPrivateMethod("setBlocks", new Object[]{ LeDebugTools.createBoardAtState(LeDebugTools.fallingTest,gboard) });
		gboard.updateBlockState();
		for(int i = 0; i < 100; i++){
			gboard.actionUpdate();
		}
		Block[][] blocks = (Block[][])callPrivateMethod("getBlocks", new Object[0]);
		for(int x = 0; x < 3; x++){
			if(blocks[x][0].getState()!=Block.State.NORMAL)
				fail("Wait wut?");
			if(blocks[x][1]==null)
				fail("Blocks haven't fallen to their slot");
			if(blocks[x][1].getState()==Block.State.FALLING)
				fail("Blocks are still falling when they should stop");
			if(blocks[x][2]!=null)
				fail("Blocks are still occupying their previous slot");
		}
	}
	
	@Test
	public void testRaiseStack() {
		callPrivateMethod("setBlocks", new Object[]{ LeDebugTools.createBoardAtState(LeDebugTools.raiseStackTest1,gboard) });
		Block[][] blocks = (Block[][])callPrivateMethod("getBlocks", new Object[0]);
		Block old = blocks[0][0];
		gboard.raiseStack();
		blocks = (Block[][])callPrivateMethod("getBlocks", new Object[0]);
		if(blocks[0][1]==null)
			fail("We don't have a block up here");
		if(blocks[0][1]!=old)
			fail("Block didnt' move up!");
		if(blocks[0][0]==null)
			fail("no new block!");
		if(old.getPosition().y != SwappidySwap.BLOCK_SIZE)
			fail("block didn't physically move");
	}
	
	private Object callPrivateMethod(String name, Object[] params){
		@SuppressWarnings("rawtypes")
		Class[] parameterTypes = new Class[params.length];
		for(int i = 0; i < params.length; i++)
			parameterTypes[i] = params[i].getClass();
	  
	    Method m;
		try {
			m = gboard.getClass().getDeclaredMethod(name, parameterTypes);
			m.setAccessible(true);
		    return m.invoke(gboard, params); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
		throw new RuntimeException("failed to call private method");
	}
	
	

}
