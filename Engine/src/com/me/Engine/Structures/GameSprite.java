package com.me.Engine.Structures;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.me.Engine.Collision.AxisAlignedBoxCollisionMask;
import com.me.Engine.Collision.CircleCollisionMask;
import com.me.Engine.Collision.CollisionMask;
import com.me.Engine.Collision.ConcavePolygon;
import com.me.Engine.Collision.GeneralPolygonCollisionMask;
import com.me.Engine.Collision.OrientedBoxCollisionMask;
import com.me.Engine.Data.Drawable;
import com.me.Engine.Data.OriginType;
import com.me.Engine.Data.State;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameSprite extends Drawable {

	/**
	 * Stores the depth of the sprite used for drawing it.
	 * Lower is drawn in front
	 */
	public float depth;
	
	
	/**
	 * Stores a name for the sprite instance
	 */
	public String name;
	
	/**
	 * Number of columns in the sprite sheet
	 */
	public int columns;
	
	/**
	 * Number of rows in the sprite sheet
	 */
	public int rows;
	
	/**
	 * Stores the total number of frames in the sprite
	 */
	public int nFrames;
	
	/**
	 * Origin of the sprite
	 */
	//public Vector2 origin;
	public float originX, originY;
	
	/**
	 * Stores the location of the sprite
	 */
	//public Vector2 location;
	public float x,y;
	
	/**
	 * Stores the rotation of this object
	 */
	public float rotation;
	
	/**
	 * Stores the x-scale of the sprite
	 */
	public float xScale;
	
	/**
	 * Stores the y-scale of the sprite
	 */
	public float yScale;
	
	
	public CollisionMask mask;
	
	/**
	 * A list that stores the bitMask of the sprite for pixel perfect collisions
	 * Stores a minimal rectangle area around the image trimming invisible space
	 * TODO need to figure out if this is necessary and how this works for this framework
	 */
	//private Array<boolean []> bitMask;
	
	/**
	 * Top left corner of the bitmask
	 */
	//public Vector2 topLeft; TODO maybe need these still
	
	/**
	 * Bottom right corner of the bitmask
	 */
	//public Vector2 bottomRight; TODO maybe need these still
	
	/**
	 * Boolean describing whether we are going to use states for the sprite
	 */
	public boolean useStates;
	
	/**
	 * States of the animation to use
	 */
	public Array<State> animationStates;
	
	
	/**
	 * Used to detect which objects need to be checked for collisions
	 * using the collision grid	 
	 */
	public Circle boundingCollisionCircle;
	
	/**
	 * Stores the width of the sprite frames
	 */
	public int frameWidth;
	
	/**
	 * Stores the height of the sprite frames
	 */
	public int frameHeight;
	
	/**
	 * Stores the speed the sprite animation runs through at
	 */
	public float imageSpeed;
	
	/**
	 * Color used to render the sprite with
	 */
	public Color hue;
	
	/**
	 * Texture 2D storing the sprite sheet of the game sprite
	 */
	private TextureRegion [][] spriteSheet;
	
	//Stores the current frame of the sprite to show
	private int currentFrame;
	
	//Stores the current frame in float form used for the float image speed
	private float currentFrameFloat;
	
	/**
	 * If true will draw the collision mask
	 */
	public boolean debug = false;
	
	//Determines if the the sprite has an image. Useful for empty sprites which are essentially just collision masks
	private boolean hasImage = false;
	
	/**
	 * U co-ordinate of the texture. Relative to textureRegion uv co-ords
	 */
	public float u;
	/**
	 * U2 co-ordinate of the texture. Relative to textureRegion uv co-ords
	 */
	public float u2;
	/**
	 * V co-ordinate of the texture. Relative to textureRegion uv co-ords
	 */
	public float v;
	/**
	 * V2 co-ordinate of the texture. Relative to textureRegion uv co-ords
	 */
	public float v2;
	
	
	
	
	/**
	 * Create an empty GameSprite. Used for invisible sprites that collide (IE a controller object)
	 */
	public GameSprite (){
		initialise (0,0,0);
	}
	
	/**
	 * Create a GameSprite
	 * @param fWidth Frame-width for the sprite
	 * @param fHeight Frame-height for the sprite
	 * @param spriteSheetName String location of the spriteSheet image
	 */
	public GameSprite (int fWidth, int fHeight, String spriteSheetName){
		initialise (0,0,0);
		frameWidth = fWidth;
		frameHeight = fHeight;
		loadSprite2(spriteSheetName);
	}
	
	/**
	 * Create a GameSprite
	 * @param rows Number of rows in the sprite sheet
	 * @param columns Number of columns in the sprite sheet
	 * @param nFrames Number of frames in the sprite sheet
	 * @param spriteSheetName String location of the spriteSheet image
	 */
	public GameSprite (int rows, int columns, int nFrames, String spriteSheetName){
		initialise (rows, columns, nFrames);
		loadSprite(spriteSheetName);
	}
	
	/**
	 * Initialise the GameSprite
	 * @param rows Number of rows in the sprite sheet
	 * @param columns Number of columns in the sprite sheet
	 * @param nFrames Number of frames in the sprite sheet
	 */
	private void initialise(int rows, int columns, int nFrames)
    {
        name = "";
        this.columns = columns;
        this.rows = rows;
        this.nFrames = nFrames;
        this.rotation = 0;
        //this.location = Vector2.Zero;
        x = 0;
        y = 0;
        
        useStates = false;
        currentFrame = 0;
        currentFrameFloat = 0;
        animationStates = new Array<State>();
        //origin = Vector2.Zero;
        originX = 0;
        originY = 0;
        
        xScale = 1.0f;
        yScale = 1.0f;
        imageSpeed = 1f;
        //bitMask = new Array<boolean[]>(); TODO maybe recreate this
        depth = 0.001f;
        hue = Color.WHITE;
        
        //TODO instantiate the dubug information
        mask = null;
        setAABBCollisionMask(0, 0, 0, 0);
        //lineCol = Color.Green;
        
        u=0;
        u2=0;
        v=0;
        v2=0;
    }
	

	/* (non-Javadoc)
	 * @see com.me.GameEngine.Tools.Drawable#draw(com.badlogic.gdx.graphics.g2d.SpriteBatch)
	 */
	@Override
	public final void draw(SpriteBatch spriteBatch){
		if (!Level.camera.viewContains(x,y)){
			return;
		}
		if (hasImage){
			int row = currentFrame/columns;
			int column = currentFrame%columns;
			
			float iU, iU2, iV, iV2;
	
			iU = spriteSheet[row][column].getU();
			iU2 = spriteSheet[row][column].getU2();
			iV = spriteSheet[row][column].getV();
			iV2 = spriteSheet[row][column].getV2();
			
			setUVCoords(row, column, iU, iU2, iV, iV2);
			
			/*Rectangle sourceRectangle = new Rectangle(frameWidth*column, frameHeight*row, frameWidth, frameHeight);
			Vector2 min = location;
			Vector2 max = new Vector2(frameWidth * Math.abs(xScale), frameHeight * Math.abs(yScale));
			Rectangle destinationRectangle = new Rectangle(min.x, min.y,max.x,max.y);*/
			
			if (!spriteSheet[row][column].isFlipY() && yScale > 0){
				spriteSheet[row][column].flip(false, true);
			}
			else if (spriteSheet[row][column].isFlipY() && yScale < 0){
				spriteSheet[row][column].flip(false, false);
			}
			
			if (!spriteSheet[row][column].isFlipX() && xScale > 0){
				spriteSheet[row][column].flip(false, false);
			}
			else if (spriteSheet[row][column].isFlipX() && xScale < 0){
				spriteSheet[row][column].flip(true, false);
			}
			
			spriteBatch.draw(spriteSheet[row][column], x - originX, y - originY, originX, originY, frameWidth, frameHeight, xScale, yScale, rotation);
			
			currentFrameFloat += imageSpeed;
			currentFrameFloat %= nFrames;
			currentFrame = (int)currentFrameFloat;
			
			spriteSheet[row][column].setU(iU);
			spriteSheet[row][column].setU2(iU2);
			spriteSheet[row][column].setV(iV);
			spriteSheet[row][column].setV2(iV2);
		}
		
		if (debug){
			mask.onDraw();
		}	
		
	}
	
	/**
	 * Set the new uv co-ordinates
	 */
	private void setUVCoords(int row, int column, float iU, float iU2, float iV, float iV2){
		if (u!=0){
			float nU = u/spriteSheet[row][column].getTexture().getWidth();
			if (iU + nU < iU2 && iU + nU >= iU){
				spriteSheet[row][column].setU(iU + nU);
			}
		}
		if (u2!=0){
			float nU2 = u2/spriteSheet[row][column].getTexture().getWidth();
			if (iU + nU2 <= iU2 && iU + nU2 > iU){
				spriteSheet[row][column].setU2(iU + nU2);
			}
		}
		if (v!=0){
			float nV = v/spriteSheet[row][column].getTexture().getHeight();
			if (iV + nV < iV2 && iV + nV >= iV){
				spriteSheet[row][column].setV(iV + nV);
			}
		}
		if (v2!=0){
			float nV2 = v2/spriteSheet[row][column].getTexture().getHeight();
			if (iV + nV2 <= iV2 && iV + nV2 > iV){
				spriteSheet[row][column].setV2(iV + nV2);
			}
		}
	}
	
	
	/**
	 * Update the sprite
	 */
	public final void update(){
		mask.rotation = rotation;
		mask.xScale = xScale;
		mask.yScale = yScale;
		mask.update(x - originX, y - originY);
	}
	
	
	
	/**
	 * Dispose the sprite
	 */
	public void dispose(){
		
	}

	/**
	 * Load a sprite using with the columns and rows defined
	 * @param spriteSheetName String location of the spriteSheet image
	 */
	public void loadSprite(String spriteSheetName)
    {
		String spriteSheetNoExtension = spriteSheetName.substring(0, spriteSheetName.lastIndexOf('.'));
		AtlasRegion temp = Level.imageAtlas.findRegion(spriteSheetNoExtension);
		//Level.imageAtlas.findRegion(spriteSheetName).
		//frameWidth = temp.getRegionWidth()/columns;
        //frameHeight = temp.getRegionHeight()/rows;
		frameWidth = temp.originalWidth/columns;
        frameHeight = temp.originalHeight/rows;
        spriteSheet = temp.split(frameWidth, frameHeight);
        

        //setupBitMask();

        //Standardly set origin to the center
        setOrigin(OriginType.Center);
        
        hasImage = true;
    }

	/**
	 * Load a sprite using with the frame-width and frame-height defined
	 * @param spriteSheetName String location of the spriteSheet image
	 */
    public void loadSprite2(String spriteSheetName)
    {
    	String spriteSheetNoExtension = spriteSheetName.substring(0, spriteSheetName.lastIndexOf('.'));
    	AtlasRegion temp = Level.imageAtlas.findRegion(spriteSheetNoExtension);
    	spriteSheet = temp.split(frameWidth, frameHeight);
    	columns = spriteSheet[0].length;
        rows = spriteSheet.length;
        nFrames = columns * rows;

        //setupBitMask();

        setOrigin(OriginType.Center);
        
        hasImage = true;
    }
    
    /**
     * Set the sprite origin
     * @param origin The new origin
     */
    public void setOrigin(float originX, float originY)
    {
        this.originX = originX;
        this.originY = originY;
    }

    /**
     * Set the origin of the sprite using a preset origin type
     * @param originT Preset origin type
     */
    public void setOrigin(OriginType originT)
    {
        if (originT == OriginType.Center)
        {
        	originX = frameWidth/2;
        	originY = frameHeight/2;
        }
        else if (originT == OriginType.BottomCellCenter)
        {
            originX = frameWidth * 0.5f;
            originY = frameHeight - Level.worldCellSize*0.5f;
        }
        else if (originT == OriginType.LeftCenter)
        {
            originX = 0;
            originY = frameHeight/2;
        }
    }

    /**
     * Get the center of the sprite
     * @return Vector2 defining the sprite center
     */
    public Vector2 getCenter()
    {
        return new Vector2(frameWidth / 2, frameHeight / 2);
    }
	
	
	/**
	 * Return the current frame of the sprite
	 * @return the currentFrame
	 */
	public int getCurrentFrame() {
		return currentFrame;
	}

	/**
	 * Sets the current frame of the sprite
	 * @param currentFrame the currentFrame to set
	 */
	public void setCurrentFrame(int currentFrame) {
		currentFrameFloat = currentFrame;
		this.currentFrame = currentFrame;
	}
	
	/**
	 * Sets the xScale by scaling it to a given width
	 */
	public void setXScaleByWidth(float desiredWidth){
		if (frameWidth!=0){
			xScale = desiredWidth/frameWidth;
		}	
	}
	
	
	/**
	 * Sets the yScale by scaling it to a given width
	 */
	public void setYScaleByHeight(float desiredHeight){
		if (frameHeight!=0){
			yScale = desiredHeight/frameHeight;
		}	
	}
	
	
	/**
	 * @return the u
	 */
	public float getU() {
		return u;
	}

	/**
	 * @param u the u to set
	 */
	public void setU(float u) {
		this.u = u;
	}

	/**
	 * @return the u2
	 */
	public float getU2() {
		return u2;
	}

	/**
	 * @param u2 the u2 to set
	 */
	public void setU2(float u2) {
		this.u2 = u2;
	}

	/**
	 * @return the v
	 */
	public float getV() {
		return v;
	}

	/**
	 * @param v the v to set
	 */
	public void setV(float v) {
		this.v = v;
	}

	/**
	 * @return the v2
	 */
	public float getV2() {
		return v2;
	}

	/**
	 * @param v2 the v2 to set
	 */
	public void setV2(float v2) {
		this.v2 = v2;
	}

	public boolean collidesWith (float pX, float pY){
		return mask.checkPointCollision(pX, pY);
	}
	
	public boolean collidesWith (GameSprite otherSprite){
		boolean collision = false;
		if (mask!=null){
			collision = mask.collidesWith(otherSprite.mask);
		}
		return collision;
	}
	
	public void setAABBCollisionMask (float width, float height, float offsetX, float offsetY){
		mask = new AxisAlignedBoxCollisionMask(width, height, offsetX, offsetY);
		mask.spriteWidth = frameWidth;
		boundingCollisionCircle = mask.getBoundingCircle();
	}
	
	public void setCircleCollisionMask (float radius, float offsetX, float offsetY){
		mask = new CircleCollisionMask(radius, offsetX, offsetY);
		mask.spriteWidth = frameWidth;
		boundingCollisionCircle = mask.getBoundingCircle();
	}
	
	public void setOBBCollisionMask(float width, float height, float offsetX, float offsetY, float rotation){
		mask = new OrientedBoxCollisionMask(width, height, offsetX, offsetY, rotation);
		mask.spriteWidth = frameWidth;
		boundingCollisionCircle = mask.getBoundingCircle();
	}
	
	public void setGeneralPolygonCollisionMask(float [] vertices,float offsetX, float offsetY, float rotation, float rotationPointX, float rotationPointY){
		mask = new GeneralPolygonCollisionMask(vertices, offsetX, offsetY, rotation, rotationPointX, rotationPointY);
		mask.spriteWidth = frameWidth;
		boundingCollisionCircle = mask.getBoundingCircle();
	}
	
	public void setGeneralPolygonCollisionMask(ConcavePolygon poly,float offsetX, float offsetY, float rotation, float rotationPointX, float rotationPointY){
		mask = new GeneralPolygonCollisionMask(poly, offsetX, offsetY, rotation, rotationPointX, rotationPointY);
		mask.spriteWidth = frameWidth;
		boundingCollisionCircle = mask.getBoundingCircle();
	}
	
	
	public void setMinimalOBBCollisionMask (Vector2 offset){
		//TODO look into making this since we no longer have topright and top bottom
	}
	
	public void setMinimalOBBCollisionMask (Vector2 offset, Vector2 rotationPoint){
		//TODO look into making this since we no longer have topright and top bottom
	}
	
	
	//TODO there are couple method here in the c# project that I think are to do with pixel perfect collisions that may need to be implemented
	
}
