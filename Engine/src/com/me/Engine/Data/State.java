package com.me.Engine.Data;

import com.badlogic.gdx.math.Vector2;


public class State {

	/**
	 * Location vector of the state
	 */
	public Vector2 location;
	/**
	 * Rotation of the state
	 */
	public float rotation;
	/**
	 * SpriteIndex for the state
	 */
	public int spriteIndex;
	/**
	 * Used to modify what we add to the depth of the sprite. Probably useless
	 * TODO investigate if this is useless
	 */
	public int depthModifier;
	/**
	 * Base depth to use for the state
	 */
	public float depth;
	/**
	 * Depth used for the sprite if xScale is positive
	 */
	public float positiveDeltaDepth;
	/**
	 * Depth used for the sprite if xScale is negative
	 */
	public float negativeDeltaDepth;
	
	/**
	 * Constructor which initialised all the state variables
	 */
	public State(){
		location = Vector2.Zero;
		rotation = 0;
		spriteIndex = 0;
		depthModifier = Integer.MIN_VALUE;
		depth = Float.NEGATIVE_INFINITY;
		positiveDeltaDepth = Float.NEGATIVE_INFINITY;
		negativeDeltaDepth = Float.NEGATIVE_INFINITY;
	}
}
