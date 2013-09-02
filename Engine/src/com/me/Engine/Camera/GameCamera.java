package com.me.Engine.Camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;

public class GameCamera extends OrthographicCamera {
	
	public float extentMarginH = 0;
	public float extentMarginV = 0;
	
	public Rectangle bounds;
	
	/*public GameCamera(float screenWidth, float screenHeight) {
		super(screenWidth, screenHeight);
	}*/

	public boolean viewContains(float pX, float pY){
		Rectangle viewBounds = new Rectangle(this.position.x - this.viewportWidth/2 - extentMarginH,this.position.y - this.viewportHeight/2 - extentMarginH,
		this.viewportWidth + 2*extentMarginH, this.viewportHeight + 2*extentMarginH);
		boolean visible = false;
		if (viewBounds.contains(pX, pY)){
			visible = true;
		}
		return visible;
	}
	
	public void onUpdate(){
	    //GL20 gl = Gdx.graphics.getGL20();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glViewport((int)(bounds.x), (int)(bounds.y), (int)bounds.width, (int)bounds.height);
		
		this.update();
		//this.apply(gl);
		
		//bounds = new Rectangle(this.position.x- this.viewportWidth/2,this.position.y- this.viewportHeight/2,
		//		this.viewportWidth, this.viewportHeight);
		bounds = new Rectangle(0,0,this.viewportWidth, this.viewportHeight);
	}

}
