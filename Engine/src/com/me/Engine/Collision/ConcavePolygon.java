package com.me.Engine.Collision;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.me.Engine.Collision.PolygonCreation.EdgeDetector;

public class ConcavePolygon {
	
	//TODO comment this bad boy

	public Polygon originalPoly;
	public Array<Polygon> convexPolygons;
	private boolean isConvex;
	private static EarClippingTriangulator earClipper = new EarClippingTriangulator();
	
	public ConcavePolygon(float[] vertices) {
		convexPolygons = new Array<Polygon>();
		isConvex = isConvex(vertices);
		originalPoly = new Polygon(vertices);
		if (isConvex){
			setPolygonArray(ConcavePolygon.earClipper.computeTriangles(getVectorRepresentation(vertices)));
		}
		else {
			convexPolygons.add(new Polygon(vertices));
		}
		
	}
	
	public static ConcavePolygon createFromOutline(String image, float hullTolerance, int alphaTolerance){
		Vector2[][] edges = getEdges(image, hullTolerance, alphaTolerance);
		return createFromVector2Edges(edges);
	}
	
	public static ConcavePolygon createFromOutline(AtlasRegion image, float hullTolerance, int alphaTolerance){
		Vector2[][] edges = getEdges(image, hullTolerance, alphaTolerance);
		return createFromVector2Edges(edges);
	}
	
	private static ConcavePolygon createFromVector2Edges(Vector2 [][] edges){
		float [] edgesVertices;
		
		edgesVertices = new float [edges[0].length * 2];
		for (int i = 0; i < edges[0].length * 2; i +=2 ){
			edgesVertices[i] = edges[0][i/2].x;
			edgesVertices[i+1] = edges[0][i/2].y;
		}
		return new ConcavePolygon(edgesVertices);
	}
	
	private static Vector2 [][] getEdges(String image, float hullTolerance, int alphaTolerance){
		EdgeDetector edge = new EdgeDetector();
		Vector2 [][] edges = edge.doMarch(image, hullTolerance, alphaTolerance);
		return edges;
	}
	
	private static Vector2 [][] getEdges(AtlasRegion image, float hullTolerance, int alphaTolerance){
		EdgeDetector edge = new EdgeDetector();
		Vector2 [][] edges = edge.doMarch(image, hullTolerance, alphaTolerance);
		return edges;
	}
	
	private ArrayList<Vector2> getVectorRepresentation(float[]vertices){
		ArrayList<Vector2> vertices2 = new ArrayList<Vector2>();
		for (int i = 0; i < vertices.length ; i +=2 ){
			vertices2.add(new Vector2(vertices[i], vertices[i+1]));
		}
		return vertices2;
	}
	
	private void setPolygonArray(List<Vector2> triangles){
		float [] vertices = new float [triangles.size() * 6];
		Vector2 v1,v2,v3;
		int size = triangles.size();
		for (int i = 0; i < triangles.size(); i +=3){
			v1 = triangles.get(i);
			v2 = triangles.get(i+1);
			v3 = triangles.get(i+2);
			vertices[i*6] = v1.x;
			vertices[i*6+1] = v1.y;
			vertices[(i*6+2)%size] = v2.x;
			vertices[(i*6+3)%size] = v2.y;
			vertices[(i*6+4)%size] = v3.x;
			vertices[(i*6+5)%size] = v3.y;
			convexPolygons.add(new Polygon(vertices));
		}
	}
	
	private boolean isConvex (float[] vertices){
		boolean isConvex = true;
		float x,x1,x2,y,y1,y2;
		float dx1,dy1,dx2,dy2;
		int nVertices = vertices.length;
		float previousSign = Integer.MIN_VALUE;
		float zCross = 0;
		float newSign = 0;
		for (int i = 0; i < nVertices ; i +=2 ){
			x = vertices[i];
			y = vertices[i+1];
			x1 = vertices[(i+2)%nVertices];
			y1 = vertices[(i+3)%nVertices];
			x2 = vertices[(i+4)%nVertices];
			y2 = vertices[(i+5)%nVertices];
			
			dx1 = x1 - x;
			dy1 = y1 - y;
			dx2 = x2 - x1;
			dy2 = y2 - y1;
			zCross = dx1*dy2 - dy1*dx2;
			newSign = Math.signum(zCross);
			if (newSign!=previousSign && newSign != 0 && previousSign != Integer.MIN_VALUE){
				isConvex = false;
				break;
			}
			else if (newSign!=0){
				previousSign = newSign;
			}
		}
		
		return isConvex;
		
	}
	
	public boolean contains(float x, float y){
		return originalPoly.contains(x, y);
	}
	
	
	
	public void setOrigin(float x, float y){
		for (int i = 0; i < convexPolygons.size; i ++){
			convexPolygons.get(i).setOrigin(x, y);
		}
		originalPoly.setOrigin(x, y);
	}
	
	public void setPosition(float x, float y){
		for (int i = 0; i < convexPolygons.size; i ++){
			convexPolygons.get(i).setPosition(x, y);
		}
		originalPoly.setPosition(x, y);
	}
	
	public void translate(float x, float y){
		for (int i = 0; i < convexPolygons.size; i ++){
			convexPolygons.get(i).translate(x, y);
		}
		originalPoly.translate(x, y);
	}
	
	public void setRotation(float degrees){
		for (int i = 0; i < convexPolygons.size; i ++){
			convexPolygons.get(i).setRotation(degrees);
		}
		originalPoly.setRotation(degrees);
	}
	
	public void rotate(float degrees){
		for (int i = 0; i < convexPolygons.size; i ++){
			convexPolygons.get(i).rotate(degrees);
		}
		originalPoly.rotate(degrees);
	}
	
	public void setScale(float xScale, float yScale){
		for (int i = 0; i < convexPolygons.size; i ++){
			convexPolygons.get(i).setScale(xScale, yScale);
		}
		originalPoly.setScale(xScale, yScale);
	}
	
	public float getXScale(){
		return convexPolygons.get(0).getScaleX();
	}
	
	public float getYScale(){
		return convexPolygons.get(0).getScaleY();
	}

	public float getFurthestPoint (float x, float y){
		float max = 0;
		for(Polygon p:convexPolygons){
			float [] vertices = p.getTransformedVertices();
			for (int i = 0; i < vertices.length; i += 2){
				float dist = (float) (Math.pow(x-vertices[i],2) + Math.pow(y-vertices[i+1],2));
				if (dist>max){
					max = dist;
				}
			}
		}
		return (float) Math.sqrt(max);
	}

}
