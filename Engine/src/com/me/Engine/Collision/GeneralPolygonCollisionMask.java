package com.me.Engine.Collision;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.me.Engine.Data.MaskType;

public class GeneralPolygonCollisionMask extends CollisionMask {
	
	public float baseRotation;
	public float radius;
	
	public float spriteWidth;
	
	public float rotationPointX, rotationPointY;
	public Rectangle boundingRectangle;

	public GeneralPolygonCollisionMask(float [] vertices, float offsetX, float offsetY, float rotation){
		initialiseOBB(vertices, offsetX, offsetY, rotation, offsetX, offsetY);
	}
	
	public GeneralPolygonCollisionMask(float [] vertices,float offsetX, float offsetY, float rotation, float rotationPointX, float rotationPointY){
		initialiseOBB(vertices, offsetX, offsetY, rotation, rotationPointX, rotationPointY);
	}
	
	public GeneralPolygonCollisionMask(ConcavePolygon poly, float offsetX, float offsetY, float rotation){
		initialiseOBB(poly.originalPoly.getVertices(), offsetX, offsetY, rotation, offsetX, offsetY);
	}
	
	public GeneralPolygonCollisionMask(ConcavePolygon poly,float offsetX, float offsetY, float rotation, float rotationPointX, float rotationPointY){
		initialiseOBB(poly.originalPoly.getVertices(), offsetX, offsetY, rotation, rotationPointX, rotationPointY);
	}
	
	private void initialiseOBB(float [] vertices,float offsetX, float offsetY, float rotation, float rotationPointX, float rotationPointY)
    {
        maskType = MaskType.PolygonBounds;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        baseRotation = rotation;
        
        xScale = 1.0f;
        yScale = 1.0f;
        this.rotationPointX = rotationPointX;
        this.rotationPointY = rotationPointX;
        
        generalPolygon = new ConcavePolygon(vertices);
        generalPolygon.setOrigin(rotationPointX, rotationPointY);
        //generalPolygon.rotate(baseRotation);
    }
	
	
	public void update(){
		generalPolygon.setPosition(x+offsetX, y+offsetY);
		generalPolygon.setScale(xScale, yScale);
		generalPolygon.setRotation(rotation + baseRotation);
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

		collision = IntersectorExtended.overlapCircleConcavePolygon(circleOther, generalPolygon);

		return collision;
	}
	
	public boolean collidesWithAABB (Rectangle boxOther){
		boolean collision = false;

		collision = IntersectorExtended.overlapRectangleConcavePolygon(boxOther, generalPolygon);

		return collision;
	}
	
	
	public boolean collidesWithOBB (Polygon boxOther){
		boolean collision = false;
		
		collision = IntersectorExtended.overlapPolygonConcavePolygon(boxOther, generalPolygon);

		return collision;
	}
	
	public boolean collidesWithGeneralPolygon (ConcavePolygon poly){
		boolean collision = false;

		collision = IntersectorExtended.overlapConcavePolygons(poly, generalPolygon);

		return collision;
	}
	
	public boolean checkPointCollision(float pX, float pY){
		boolean collision = false;

		if (generalPolygon.contains(pX, pY)){
			collision = true;
		}

		return collision;
	}
	
	public Circle getBoundingCircle (){
		Circle boundingCircle;

		boundingCircle = new Circle();

		boundingCircle.x = rotationPointX;
		boundingCircle.y = rotationPointY;
		//TODO maybe need to scale generalPoly before calling this
		boundingCircle.radius = generalPolygon.getFurthestPoint(rotationPointX, rotationPointY);
		
		float scaling = Math.abs(xScale);
		if (Math.abs(yScale) > scaling){
			scaling = Math.abs(yScale);
		}
		boundingCircle.radius *= scaling;
		
		return boundingCircle;
	}

	@Override
	public void onDraw() {
		drawCollisionMaskLineOutline(generalPolygon.originalPoly.getTransformedVertices(), true);		
	}

	/**
	 * @return the baseRotation
	 */
	public float getBaseRotation() {
		return baseRotation;
	}

	/**
	 * @param baseRotation the baseRotation to set
	 */
	public void setBaseRotation(float baseRotation) {
		this.baseRotation = baseRotation;
	}

	/**
	 * @return the radius
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * @param radius the radius to set
	 */
	public void setRadius(float radius) {
		this.radius = radius;
	}

	/**
	 * @return the spriteWidth
	 */
	public float getSpriteWidth() {
		return spriteWidth;
	}

	/**
	 * @param spriteWidth the spriteWidth to set
	 */
	public void setSpriteWidth(float spriteWidth) {
		this.spriteWidth = spriteWidth;
	}

	/**
	 * @return the rotationPointX
	 */
	public float getRotationPointX() {
		return rotationPointX;
	}

	/**
	 * @param rotationPointX the rotationPointX to set
	 */
	public void setRotationPointX(float rotationPointX) {
		this.rotationPointX = rotationPointX;
		generalPolygon.setOrigin(rotationPointX, rotationPointY);
	}

	/**
	 * @return the rotationPointY
	 */
	public float getRotationPointY() {
		return rotationPointY;
	}

	/**
	 * @param rotationPointY the rotationPointY to set
	 */
	public void setRotationPointY(float rotationPointY) {
		this.rotationPointY = rotationPointY;
		generalPolygon.setOrigin(rotationPointX, rotationPointY);
	}
	
	
	
}
