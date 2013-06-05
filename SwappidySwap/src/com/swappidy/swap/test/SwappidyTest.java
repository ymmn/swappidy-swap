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
		SwappidySwap.BLOCK_SIZE = 100;
	}

	@Test
	public void testFallingBlockState() {
		callPrivateMethod("setBlocks", new Object[]{ LeDebugTools.createBoardAtState(LeDebugTools.fallingTest,gboard) });
		gboard.updateBlockState();
		Block[] blocks = (Block[])callPrivateMethod("getBlocks", new Object[0]);
		for(int i = 0; i < 3; i++)
			if(blocks[i].getState()!=Block.State.NORMAL)
				fail("Blocks are falling when they shouldn't");
		for(int i = 6; i < 9; i++)
			if(blocks[i].getState()!=Block.State.FALLING)
				fail("Blocks aren't falling when they should");
	}
	
	@Test
	public void testFallingBlocks() {
		callPrivateMethod("setBlocks", new Object[]{ LeDebugTools.createBoardAtState(LeDebugTools.fallingTest,gboard) });
		gboard.updateBlockState();
		for(int i = 0; i < 100; i++){
			gboard.actionUpdate();
		}
		Block[] blocks = (Block[])callPrivateMethod("getBlocks", new Object[0]);
		for(int i = 0; i < 3; i++)
			if(blocks[i].getState()!=Block.State.NORMAL)
				fail("Wait wut?");
		for(int i = 3; i < 6; i++){
			if(blocks[i]==null)
				fail("Blocks haven't fallen to their slot");
			if(blocks[i].getState()==Block.State.FALLING)
				fail("Blocks are still falling when they should stop");
		}
		for(int i = 6; i < 9; i++)
			if(blocks[i]!=null)
				fail("Blocks are still occupying their previous slot");
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
