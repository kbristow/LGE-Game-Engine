package com.me.Engine.Collision;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.me.Engine.Data.MaskType;

public class CircleCollisionMask extends CollisionMask{
	
	public float radius;
	
	public CircleCollisionMask(float radius, float offsetX, float offsetY){
		maskType = MaskType.BoundingCircle;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.radius = radius;
		circleBounds = new Circle(offsetX, offsetY, radius);
		xScale = 1.0f;
		yScale = 1.0f;
	}
	
	
	public void update(){
		circleBounds.x = x + offsetX;
		circleBounds.y = y + offsetY;

		float scaling = Math.abs(xScale);
		if (Math.abs(yScale) > Math.abs(xScale)){
			scaling = Math.abs(yScale);
		}

		circleBounds.radius = radius * scaling;
	}
	
	public boolean collidesWith(CollisionMask other){
		boolean collision = false;
		switch(other.maskType){
		case AxisAlignedBoundingBox:
			collision = collidesWithAABB(other.axisAlignedBoxBounds);
			break;
		case BoundingCircle:
			collision = collidesWithCircle(other.circleBounds);
		case OrientedBoundingBox:
		case MinimalOrientedBoundingBox:
			collision = collidesWithOBB(other.orientedBoxBounds);
			break;
		case MinimalPolygonBounds:
		case PolygonBounds:
			collision = collidesWithGeneralPolygon(other.generalPolygon);
			break;
		default:
			break;
		}
		return collision;
	}
	
	
	public boolean collidesWithCircle(Circle circleOther ){
		boolean collision = false;

		collision = IntersectorExtended.overlapCircles(circleOther, circleBounds);

		return collision;
	}
	
	public boolean collidesWithAABB (Rectangle boxOther){
		boolean collision = false;

		collision = IntersectorExtended.overlapCircleRectangle(circleBounds, boxOther);

		return collision;
	}
	
	
	public boolean collidesWithOBB (Polygon boxOther){
		boolean collision = false;
		
		collision = IntersectorExtended.overlapPolygonCircle(boxOther, circleBounds);
		
		return collision;
	}
	
	public boolean collidesWithGeneralPolygon (ConcavePolygon poly){
		boolean collision = false;
			
		collision = IntersectorExtended.overlapCircleConcavePolygon(circleBounds, poly);
		
		return collision;
	}
	
	public boolean checkPointCollision(float pX, float pY){
		boolean collision = false;
		
		if (circleBounds.contains(pX, pY)){
			collision = true;
		}
		
		return collision;
	}
	
	public Circle getBoundingCircle (){
		Circle boundingCircle;
		boundingCircle = new Circle();
		
		float scaling = Math.abs(xScale);
		if (Math.abs(yScale) > scaling){
			scaling = Math.abs(yScale);
		}
		boundingCircle.radius *= scaling;
		
		return boundingCircle;
	}


	@Override
	public void onDraw() {
		drawCollisionMaskCircle(x + offsetX, y + offsetY, radius);
	}
	
}
