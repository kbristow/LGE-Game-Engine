package com.me.Engine.Collision;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

public class IntersectorExtended{

	public static boolean overlapCircleRectangle(Circle c, Rectangle r){
		return Intersector.overlaps(c,r);
	}
	
	public static boolean overlapRectangles(Rectangle r1, Rectangle r2){
		return Intersector.overlaps(r1,r2);
	}
	
	public static boolean overlapCircles(Circle c1, Circle c2){
		return Intersector.overlaps(c1,c2);
	}
	
	public static boolean overlapConvexPolygons(Polygon p1, Polygon p2){
		return Intersector.overlapConvexPolygons(p1, p2);
	}
	
	public static boolean overlapConvexPolygonRectangle(Polygon p, Rectangle r){
		float [] rectCorners = new float [8];
		
		rectCorners [0] = r.x;
		rectCorners [1] = r.y;
		
		rectCorners [2] = r.x;
		rectCorners [3] = r.y + r.height;
		
		rectCorners [4] = r.x+r.width;
		rectCorners [5] = r.y + r.height;
		
		rectCorners [6] = r.x+r.width;
		rectCorners [7] = r.y;
		
		return Intersector.overlapConvexPolygons(rectCorners, p.getTransformedVertices(), null);
	}
	
	public static boolean overlapPolygonCircle(Polygon p, Circle c){
		float [] polyPoints = p.getTransformedVertices();
		boolean collides = false;

		for (int i = 0; i < polyPoints.length; i += 2){
			if (c.contains(polyPoints[i], polyPoints[i+1])){
				collides = true;
				break;
			}
		}

		return collides;
	}

	public static boolean overlapPolygonConcavePolygon(Polygon p, ConcavePolygon cp){
		boolean collision = false;
		for (int i = 0; i < cp.convexPolygons.size; i ++){
			if( Intersector.overlapConvexPolygons(p,cp.convexPolygons.get(i))){
				collision = true;
				break;
			}
		}
		return collision;
	}

	public static boolean overlapCircleConcavePolygon(Circle c, ConcavePolygon cp){
		boolean collision = false;
		for (int i = 0; i < cp.convexPolygons.size; i ++){
			if( IntersectorExtended.overlapPolygonCircle(cp.convexPolygons.get(i),c) ){
				collision = true;
				break;
			}
		}
		return collision;
	}

	public static boolean overlapRectangleConcavePolygon(Rectangle r, ConcavePolygon cp){
		boolean collision = false;
		for (int i = 0; i < cp.convexPolygons.size; i ++){
			if( IntersectorExtended.overlapConvexPolygonRectangle(cp.convexPolygons.get(i), r) ){
				collision = true;
				break;
			}
		}
		return collision;
	}
	
	public static boolean overlapConcavePolygons(ConcavePolygon cp1, ConcavePolygon cp2){
		for (int i = 0; i < cp1.convexPolygons.size; i ++){
			for (int j = 0; j < cp2.convexPolygons.size; j ++){
				if( Intersector.overlapConvexPolygons(cp1.convexPolygons.get(i), cp2.convexPolygons.get(j)) ){
					return true;
				}
			}
		}
		return false;
	}
	
	// TODO intersector for concave poly vs maybe oboundingbox? think about that. Then need documentation
}
