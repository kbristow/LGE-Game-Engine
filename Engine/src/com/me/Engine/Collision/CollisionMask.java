package com.me.Engine.Collision;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.me.Engine.Data.MaskType;
import com.me.Engine.Structures.Level;

public abstract class CollisionMask {

	public Rectangle axisAlignedBoxBounds;
	public Circle circleBounds;
	public Polygon orientedBoxBounds;
	public ConcavePolygon generalPolygon;
	
	public MaskType maskType;
	public float offsetX, offsetY;
	public float x,y;
	//public Vector2 offset;
	//public Vector2 location;
	public float rotation;
	public float xScale;
	public float yScale;
	public Rectangle boundingRectangle;
	
	public float spriteWidth;	
	
	public Color lineColor = Color.MAGENTA;
	
	public abstract void update();
	
	public void update (float x, float y){
		this.x = x;
		this.y = y;
		update();
	}
	
	public abstract boolean collidesWith(CollisionMask other);
	
	
	public abstract boolean collidesWithCircle(Circle circleOther );
	
	public abstract boolean collidesWithAABB (Rectangle boxOther);
	
	
	public abstract boolean collidesWithOBB (Polygon boxOther);
	
	public abstract boolean collidesWithGeneralPolygon (ConcavePolygon poly);
	
	public abstract boolean checkPointCollision(float pX, float pY);

	public abstract Circle getBoundingCircle ();
	
	public abstract void onDraw();
	
	public void drawCollisionMaskLineOutline(float vertices[], boolean connectEnds){
		Level.lineRenderer.setColor(lineColor);
		for (int i = 0; i < vertices.length; i += 2){
			Level.lineRenderer.line(vertices[i], vertices[i+1], vertices[(i+2)%vertices.length], vertices[(i+3)%vertices.length]);
		}
	}
	
	public void drawCollisionMaskCircle(float xCent, float yCent, float radius){
		Level.circleRenderer.circle(xCent, yCent, radius);
	}
	
}
