package com.me.Engine.Collision;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.me.Engine.Data.MaskType;

public class OrientedBoxCollisionMask extends CollisionMask {
	
	private Vector2 halfExtent = Vector2.Zero;

	public float baseRotation;
	//public Vector2 rotationPoint;
	public float rotationPointX, rotationPointY;
	public Rectangle boundingRectangle;
	
	private float height,width;
	
	public OrientedBoxCollisionMask(float width, float height, float offsetX, float offsetY, float rotation){
		initialiseOBB(width, height, offsetX, offsetY, rotation, offsetX, offsetY);
	}
	
	public OrientedBoxCollisionMask(float width, float height, float offsetX, float offsetY, float rotation, float rotationPointX, float rotationPointY){
		initialiseOBB(width, height, offsetX, offsetY, rotation, rotationPointX, rotationPointY);
	}
	
	private void initialiseOBB(float width, float height, float offsetX, float offsetY, float rotation, float rotationPointX, float rotationPointY)
    {
        maskType = MaskType.OrientedBoundingBox;
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        baseRotation = rotation;
        
        orientedBoxBounds = createOrientedBoundingBox();
        orientedBoxBounds.setOrigin(rotationPointX, rotationPointY);
        orientedBoxBounds.setPosition(offsetX, offsetY);
        orientedBoxBounds.setRotation(rotation);
        
        xScale = 1.0f;
        yScale = 1.0f;
        this.rotationPointX = rotationPointX;
        this.rotationPointY = rotationPointX;
    }
	
	
	private Polygon createOrientedBoundingBox(){
		//orientedBoxBounds = new Pol
		float [] vertices = new float [8];
		
		vertices[0] = -halfExtent.x;
		vertices[1] = -halfExtent.y;
		
		vertices[2] = -halfExtent.x;
		vertices[3] = halfExtent.y;
		
		vertices[4] = halfExtent.x;
		vertices[5] = halfExtent.y;
		
		vertices[6] = halfExtent.x;
		vertices[7] = -halfExtent.y;
		
		Polygon newBox = new Polygon(vertices);
		return newBox;
	}
	
	
	public void update(){
		//Vector2 pos = location.add(offset);

		//Vector2 rotP = offset.sub(rotationPoint).mul(xScale, yScale);
		Vector2 rotP = new Vector2( offsetX - rotationPointX, offsetY - rotationPointY).scl(xScale, yScale);
		if (offsetX != rotationPointX && offsetY != rotationPointY){
			rotP = rotP.rotate(rotation+baseRotation);
		}
		//Vector2 loc = location.add(rotationPoint.mul(xScale, yScale)).add(rotP);
		float locX = x + rotationPointX * xScale + rotP.x;
		float locY = y + rotationPointY * yScale + rotP.y;
		orientedBoxBounds.setPosition(locX, locY);
		orientedBoxBounds.setScale(xScale, yScale);
		orientedBoxBounds.rotate(rotation + baseRotation);
		
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
		
		collision = IntersectorExtended.overlapPolygonCircle(orientedBoxBounds, circleOther);

		return collision;
	}
	
	public boolean collidesWithAABB (Rectangle boxOther){
		boolean collision = false;
		
		collision = IntersectorExtended.overlapConvexPolygonRectangle(orientedBoxBounds, boxOther);
		
		return collision;
	}
	
	
	public boolean collidesWithOBB (Polygon boxOther){
		boolean collision = false;
		
		collision = IntersectorExtended.overlapConvexPolygons(orientedBoxBounds, boxOther);
		
		return collision;
	}
	
	public boolean collidesWithGeneralPolygon (ConcavePolygon poly){
		boolean collision = false;
		
		collision = IntersectorExtended.overlapPolygonConcavePolygon(orientedBoxBounds, poly);
		
		return collision;
	}
	
	public boolean checkPointCollision(float pX, float pY){
		boolean collision = false;

		if (orientedBoxBounds.contains(pX, pY)){
			collision = true;
		}
		
		return collision;
	}

	
	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
		halfExtent.y = height/2;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
		halfExtent.x = width/2;
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
		drawCollisionMaskLineOutline(orientedBoxBounds.getTransformedVertices(), true);
	}
	
}
