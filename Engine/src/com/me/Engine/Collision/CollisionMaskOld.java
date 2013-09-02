
package com.me.Engine.Collision;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.me.Engine.Data.MaskType;

public class CollisionMaskOld {

	public Rectangle axisAlignedBoxBounds;
	public Circle circleBounds;
	public Polygon orientedBoxBounds;
	private Vector2 halfExtent = Vector2.Zero;
	public ConcavePolygon generalPolygon;

	public MaskType maskType;
	public Vector2 offset;
	public Vector2 location;
	public float rotation;
	public float baseRotation;
	public Vector2 rotationPoint;
	public float radius;
	public float xScale;
	public float yScale;
	public Rectangle boundingRectangle;

	public float spriteWidth;

	private float height,width;

	public CollisionMaskOld(float width, float height, Vector2 offset){
		maskType = MaskType.AxisAlignedBoundingBox;
		this.width = width;
		this.height = height;
		this.offset = offset;
		axisAlignedBoxBounds = new Rectangle(offset.x, offset.y, offset.x + width, offset.y + height);
		xScale = 1.0f;
		yScale = 1.0f;
	}

	public CollisionMaskOld(float radius, Vector2 offset){
		maskType = MaskType.BoundingCircle;
		this.offset = offset;
		this.radius = radius;
		circleBounds = new Circle(offset, radius);
		xScale = 1.0f;
		yScale = 1.0f;
	}

	public CollisionMaskOld(float width, float height, Vector2 offset, float rotation){
		initialiseOBB(width, height, offset, rotation, offset);
	}

	public CollisionMaskOld(float width, float height, Vector2 offset, float rotation, Vector2 rotationPoint){
		initialiseOBB(width, height, offset, rotation, rotationPoint);
	}

	private void initialiseOBB(float width, float height, Vector2 offset, float rotation, Vector2 rotationPoint)
	{
		maskType = MaskType.OrientedBoundingBox;
		this.width = width;
		this.height = height;
		this.offset = offset;
		baseRotation = rotation;

		orientedBoxBounds = createOrientedBoundingBox();
		orientedBoxBounds.setOrigin(rotationPoint.x, rotationPoint.y);
		orientedBoxBounds.setPosition(offset.x, offset.y);
		orientedBoxBounds.setRotation(rotation);

		xScale = 1.0f;
		yScale = 1.0f;
		this.rotationPoint = rotationPoint;
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
		Vector2 pos = location.add(offset);
		switch(maskType){
		case AxisAlignedBoundingBox:
			if (xScale < 0){
				pos = location.add(new Vector2(spriteWidth - offset.x - width, offset.y));
			}
			float xMin = Math.abs(xScale)/2 - 0.5f;
			float xMax = Math.abs(xScale)/2 + 0.5f;

			axisAlignedBoxBounds.x = pos.x - width*xMin;
			axisAlignedBoxBounds.y = pos.y - height * (yScale/2 - 0.5f);

			axisAlignedBoxBounds.width = 2 * width * xMax;
			axisAlignedBoxBounds.height = height * (yScale + 1);

			break;
		case BoundingCircle:
			circleBounds.x = pos.x;
			circleBounds.y = pos.y;

			float scaling = Math.abs(xScale);
			if (Math.abs(yScale) > Math.abs(xScale)){
				scaling = Math.abs(yScale);
			}

			circleBounds.radius = radius * scaling;
			break;
		case MinimalOrientedBoundingBox:
		case OrientedBoundingBox:
			
			Vector2 rotP = offset.sub(rotationPoint).scl(xScale, yScale);
			if (offset != rotationPoint){
				rotP = rotP.rotate(rotation+baseRotation);
			}
			Vector2 loc = location.add(rotationPoint.scl(xScale, yScale)).add(rotP);
			orientedBoxBounds.setPosition(loc.x, loc.y);
			orientedBoxBounds.setScale(xScale, yScale);
			orientedBoxBounds.rotate(rotation + baseRotation);
			break;
		case MinimalPolygonBounds:
		case PolygonBounds:

			break;
		default:
			break;
		}	
	}

	public void update (Vector2 location){
		this.location = location;
		update();
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


	private boolean collidesWithCircle(Circle circleOther ){
		boolean collision = false;
		switch (maskType){
		case AxisAlignedBoundingBox:
			collision = IntersectorExtended.overlapCircleRectangle(circleOther, axisAlignedBoxBounds);
			break;
		case BoundingCircle:
			collision = IntersectorExtended.overlapCircles(circleOther, circleBounds);
			break;
		case MinimalOrientedBoundingBox:
		case OrientedBoundingBox:
			collision = IntersectorExtended.overlapPolygonCircle(orientedBoxBounds, circleOther);
			break;
		case MinimalPolygonBounds:
		case PolygonBounds:
			collision = IntersectorExtended.overlapCircleConcavePolygon(circleOther, generalPolygon);
			break;
		default:
			break;
		}
		return collision;
	}

	private boolean collidesWithAABB (Rectangle boxOther){
		boolean collision = false;
		switch (maskType){
		case AxisAlignedBoundingBox:
			collision = IntersectorExtended.overlapRectangles(axisAlignedBoxBounds, boxOther);
			break;
		case BoundingCircle:
			collision = IntersectorExtended.overlapCircleRectangle(circleBounds, boxOther);
			break;
		case MinimalOrientedBoundingBox:
		case OrientedBoundingBox:
			collision = IntersectorExtended.overlapConvexPolygonRectangle(orientedBoxBounds, boxOther);
			break;
		case MinimalPolygonBounds:
		case PolygonBounds:
			collision = IntersectorExtended.overlapRectangleConcavePolygon(boxOther, generalPolygon);
			break;
		default:
			break;
		}
		return collision;
	}


	private boolean collidesWithOBB (Polygon boxOther){
		boolean collision = false;
		switch (maskType){
		case AxisAlignedBoundingBox:
			collision = IntersectorExtended.overlapConvexPolygonRectangle(boxOther, axisAlignedBoxBounds);
			break;
		case BoundingCircle:
			collision = IntersectorExtended.overlapPolygonCircle(boxOther, circleBounds);
			break;
		case MinimalOrientedBoundingBox:
		case OrientedBoundingBox:
			collision = IntersectorExtended.overlapConvexPolygons(orientedBoxBounds, boxOther);
			break;
		case MinimalPolygonBounds:
		case PolygonBounds:
			collision = IntersectorExtended.overlapPolygonConcavePolygon(boxOther, generalPolygon);
			break;
		default:
			break;
		}
		return collision;
	}

	private boolean collidesWithGeneralPolygon (ConcavePolygon poly){
		boolean collision = false;
		switch (maskType){
		case AxisAlignedBoundingBox:
			collision = IntersectorExtended.overlapRectangleConcavePolygon(axisAlignedBoxBounds, poly);
			break;
		case BoundingCircle:
			collision = IntersectorExtended.overlapCircleConcavePolygon(circleBounds, poly);
			break;
		case MinimalOrientedBoundingBox:
		case OrientedBoundingBox:
			collision = IntersectorExtended.overlapPolygonConcavePolygon(orientedBoxBounds, poly);
			break;
		case MinimalPolygonBounds:
		case PolygonBounds:
			collision = IntersectorExtended.overlapConcavePolygons(poly, generalPolygon);
			break;
		default:
			break;
		}
		return collision;
	}

	public boolean checkPointCollision(Vector2 point){
		boolean collision = false;
		switch(maskType){
		case AxisAlignedBoundingBox:
			if (axisAlignedBoxBounds.contains(point.x, point.y)){
				collision = true;
			}
			break;
		case BoundingCircle:
			if (circleBounds.contains(point.x, point.y)){
				collision = true;
			}
			break;
		case MinimalOrientedBoundingBox:
		case OrientedBoundingBox:
			if (orientedBoxBounds.contains(point.x, point.y)){
				collision = true;
			}
			break;
		case MinimalPolygonBounds:
		case PolygonBounds:
			if (generalPolygon.contains(point.x, point.y)){
				collision = true;
			}
			break;
		default:
			break;
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
		switch (maskType){
		case BoundingCircle:
			boundingCircle = new Circle();
			break;
		case AxisAlignedBoundingBox:
		case MinimalOrientedBoundingBox:
		case OrientedBoundingBox:
			float radius = (float)Math.sqrt(width * width + height*height)/2;
			boundingCircle = new Circle(width/2, height/2, radius);
			break;
		case MinimalPolygonBounds:
		case PolygonBounds:
			//TODO sort this out
		default:
			boundingCircle = new Circle();
			break;
		}

		float scaling = Math.abs(xScale);
		if (Math.abs(yScale) > scaling){
			scaling = Math.abs(yScale);
		}
		boundingCircle.radius *= scaling;

		return boundingCircle;
	}

}

