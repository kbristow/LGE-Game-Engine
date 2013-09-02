package com.me.Engine.Collision;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.me.Engine.Data.MaskType;

public class AxisAlignedBoxCollisionMask extends CollisionMask {
	
	private float height,width;
	
	public AxisAlignedBoxCollisionMask(float width, float height, float offsetX, float offsetY){
		maskType = MaskType.AxisAlignedBoundingBox;
		this.width = width;
		this.height = height;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		axisAlignedBoxBounds = new Rectangle(offsetX, offsetY, offsetX + width, offsetY + height);
		xScale = 1.0f;
		yScale = 1.0f;
	}
	
	public void update(){
		float posX = x + offsetX;
		float posY = y + offsetY;
		float xMin = Math.abs(xScale)/2 - 0.5f;
		if (xScale < 0){
			posX = x + spriteWidth*xScale - offsetX*xScale - width*xMin;
			//pos = location.add(new Vector2(spriteWidth - offset.x - width, offset.y));
		}
		
		//float xMax = Math.abs(xScale)/2 + 0.5f;

		axisAlignedBoxBounds.x = posX - width*xMin;
		axisAlignedBoxBounds.y = posY - height * (yScale/2 - 0.5f);

		axisAlignedBoxBounds.width =  width * xScale;
		axisAlignedBoxBounds.height = height * yScale;
		
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
		boolean collision;
		
		collision = IntersectorExtended.overlapCircleRectangle(circleOther, axisAlignedBoxBounds);
		
		return collision;
	}
	
	public boolean collidesWithAABB (Rectangle boxOther){
		boolean collision = false;

		collision = IntersectorExtended.overlapRectangles(axisAlignedBoxBounds, boxOther);
		
		return collision;
	}
	
	
	public boolean collidesWithOBB (Polygon boxOther){
		boolean collision = false;
		
		collision = IntersectorExtended.overlapConvexPolygonRectangle(boxOther, axisAlignedBoxBounds);
		
		return collision;
	}
	
	public boolean collidesWithGeneralPolygon (ConcavePolygon poly){
		boolean collision = false;
		
		collision = IntersectorExtended.overlapRectangleConcavePolygon(axisAlignedBoxBounds, poly);
		
		return collision;
	}
	
	public boolean checkPointCollision(float pX, float pY){
		boolean collision = false;
		
		if (axisAlignedBoxBounds.contains(pX, pY)){
			collision = true;
		}
		
		return collision;
	}

	
	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}
	
	public Circle getBoundingCircle (){
		Circle boundingCircle;

		float radius = (float)Math.sqrt(width * width + height*height)/2;
		boundingCircle = new Circle(width/2, height/2, radius);

		float scaling = Math.abs(xScale);
		if (Math.abs(yScale) > scaling){
			scaling = Math.abs(yScale);
		}
		boundingCircle.radius *= scaling;
		
		return boundingCircle;
	}

	@Override
	public void onDraw() {
		float [] vertices = new float [8];

		vertices[0] = axisAlignedBoxBounds.x;
		vertices[1] = axisAlignedBoxBounds.y;

		vertices[2] = axisAlignedBoxBounds.x;
		vertices[3] = axisAlignedBoxBounds.y + axisAlignedBoxBounds.height;

		vertices[4] = axisAlignedBoxBounds.x + axisAlignedBoxBounds.width;
		vertices[5] = axisAlignedBoxBounds.y + axisAlignedBoxBounds.height;

		vertices[6] = axisAlignedBoxBounds.x + axisAlignedBoxBounds.width;
		vertices[7] = axisAlignedBoxBounds.y;

		drawCollisionMaskLineOutline(vertices, true);
		
	}
}
