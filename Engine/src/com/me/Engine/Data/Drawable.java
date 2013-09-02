package com.me.Engine.Data;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Drawable {

	/**
	 * Float used to determine the drawing order of objects. Higher depth Drawables are drawn behind lower depth Drawables
	 */
	public float depth;
	
	/**
	 * If set to false, the object is not automatically drawn. Setting it to false means depth will no longer play a role as to where it is drawn
	 */
	public boolean autoDraw = true;
	
	/**
	 * Specifies how the Drawable needs to be drawn
	 * @param spriteBatch SpriteBatch used to draw the Drawable
	 */
	public abstract void draw(SpriteBatch spriteBatch);
	
}
