package com.me.Engine.Collision.PolygonCreation;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.me.Engine.Structures.Level;

/**
 * @author Kieran
 * Performs edge detection on an image using the MarchingSquare algorithm. Implementation used can be seen here: 
 * http://devblog.phillipspiess.com/2010/02/23/better-know-an-algorithm-1-marching-squares/
 */
/**
 * @author Kieran
 *
 */
public class EdgeDetector {


	/**
	 * Stores the pixel data for the current image we are working with
	 */
	private Pixmap pixmap;

	/**
	 * SpriteBatch used to draw the images to the framebuffer which we pull the pixel data from 
	 */
	SpriteBatch spriteBatch = new SpriteBatch();

	/**
	 * Width and height of the image
	 */
	int imageWidth, imageHeight;
	
	public EdgeDetector (){

	}

	/**
	 * Takes a texture and returns a list of pixels that
	 * define the perimeter of the upper-left most
	 * object in that texture, using a given hull tolerance
	 * and alpha tolerance to determine the boundary.
	 */
	public Vector2 [][] doMarch(String image, float hullTolerance, int alphaTolerance){
		AtlasRegion region = Level.imageAtlas.findRegion(image);
		return doMarch(region, hullTolerance, alphaTolerance);
	}
	
	/**
	 * Takes a texture and returns a list of pixels that
	 * define the perimeter of the upper-left most
	 * object in that texture, using a given hull tolerance
	 * and alpha tolerance to determine the boundary.
	 */
	public Vector2 [][] doMarch(AtlasRegion image, float hullTolerance, int alphaTolerance){
		getPixels(image);
		return walkPerimeter(imageWidth, imageHeight, hullTolerance, alphaTolerance);
	}
	
	/**
	 * Get the pixel data from the level TextureAtlas for a given image
	 * @param image Image we wish to perform edge detection on
	 */
	public void getPixels(AtlasRegion region){
		imageWidth = region.originalWidth;
		imageHeight = region.originalHeight;
		//We draw the region from the TextureAtlas to a frame buffer from which we then capture the pixels and store them in a pixmap
		FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, imageWidth, imageHeight, false);
		//Need to project the spritebuffer into the orthographic view or else we get really small images
		Matrix4 projectionMatrix = new Matrix4();
		projectionMatrix.setToOrtho2D(0, 0, imageWidth, imageHeight);
		spriteBatch.setProjectionMatrix(projectionMatrix);
		//Call begin to allow drawing to the framebuffer
		fbo.begin();
		spriteBatch.begin();
		spriteBatch.draw(region, 0, 0, 0, 0, imageWidth, imageHeight, 1, 1, 0);
		spriteBatch.end();
		//Capture the pixel data from the current framebuffer
		byte[] pixelData = ScreenUtils.getFrameBufferPixels(false);
		//Initialise the pixmap to which we will load the pixels into
		pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Format.RGBA8888);
		//Get a reference to the pixmap pixel ByteBuffer
		ByteBuffer pixels = pixmap.getPixels();
		//Clear the byte buffer, put the pixel data into the ByteBuffer and the position them
		pixels.clear();
		pixels.put(pixelData);
		pixels.position(0);
		//End the frame buffer we are drawing to
		fbo.end();
		fbo.dispose();
	}

	/**
	 *  Performs the main while loop of the algorithm
	 * @param startX
	 * @param startY
	 * @return
	 */
	private Vector2 [] [] walkPerimeter(int w, int h, float hullTolerance, int alphaTolerance)
	{
        int size = w * h;
        int[] array = new int[size];

        for (int y=0; y<h; y++) {
                for (int x=0; x<w; x++) {
                        int color = pixmap.getPixel(x, y);
                        array[x + y*w] = color;
                }
        }

        Array<Array<Vector2>> outlines;
        try {
        	//outlines = TextureConverter.createPolygon(array, w, h);
        	outlines = TextureConverter.createPolygon(array, w, h, hullTolerance, alphaTolerance, false, false);
        } catch (Exception e) {
        	return null;
        }

        /*TextureRegion region = TextureUtils.getPOTTexture(path);
        float tw = region.getRegionWidth();
        float th = region.getRegionHeight();*/
        float tw = 1;
        float th = 1;

        Vector2[][] polygons = new Vector2[outlines.size][];

        for (int i=0; i<outlines.size; i++) {
                Array<Vector2> outline = outlines.get(i);
                polygons[i] = new Vector2[outline.size];
                for (int ii=0; ii<outline.size; ii++) {
                        polygons[i][ii] = outline.get(ii);
                        polygons[i][ii].x /= tw;
                        polygons[i][ii].y /= tw;
                        polygons[i][ii].y = 1*th/tw - polygons[i][ii].y;
                }
        }

        return polygons;
	}

}
