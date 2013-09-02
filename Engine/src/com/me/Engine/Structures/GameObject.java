package com.me.Engine.Structures;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.Array;
import com.me.Engine.Data.Drawable;
import com.me.Engine.Data.IntPair;

public class GameObject extends Drawable {

	/**
	 * Unique ID used to identify the GameObject
	 */
	public long ID;
	
	/**
	 * An int used to determine the drawing order of the GameObject 
	 */
	//public float depth;
	
	
	/**
	 * Location of the object
	 */
	//public Vector2 location;
	public float x,y;
	
	//Previous location of the object
	private float previousX, previousY;
	//private Vector2 previousLocation;
	
	/**
	 * Stores which cells of the collision grid the object is in
	 */
	public Array<IntPair> gridLocations;
	
	/**
	 * Speed squared of the object
	 */
	public float speed2;
	/**
	 * Vertical speed of the object
	 */
	public float vSpeed;
	/**
	 * Horizontal speed of the object
	 */
	public float hSpeed;
	/**
	 * Value of gravity to apply to the game object
	 */
	public float gravity;
	/**
	 * Direction of the gravity applied to this object. 
	 * -1 = Up
	 * 1 = Down
	 * 0 = None
	 */
	public int gravityDir;
	
	/**
	 * The x-scale of the game object. Negative flips the object
	 */
	public float xScale;
	/**
	 * The y-scale of the game object. Negative flips the object
	 */
	public float yScale;
	
	/**
	 * Stores the a number of strings that classify what types the object falls under
	 */
	public Array<String> objTypes;
	
	/**
	 * Used to specify a name for the game object
	 */
	public String name;
	
	/**
	 * Used to decide whether or not the object should be drawn and updated.
	 */
	public boolean isActive;

	
	/**
	 * Stores sprites usable by the game object. More useful if the game object consists 
	 * of one sprite displayed at a time
	 */
	public Array<GameSprite> sprites;
	
	//Stores the current sprite index to to display
	private int spriteIndex;
	// TODO Consider changing this to an array to display multiple arrays.
	//This however would require multiple image scales and rotations too
	
	//The direction the sprite is moving in
	private float direction;
	
	//The speed the object is travelling at
	private float speed;

	//Used to determine whether this object collides or not
	private boolean collides;

	//The rotation of the sprite object
	private float rotation;
	
	/**
	 * Creates a new game object and instatiates all necessary variables
	 */
	public GameObject(){
		x = 0;
		y = 0;
		previousX = 0;
		previousY = 0;

		
		gridLocations = new Array<IntPair>();
		sprites = new Array<GameSprite>();		
		
		
		vSpeed = 0;
		hSpeed = 0;
		speed = 0;
		speed2 = 0;
		gravity = 0;
		gravityDir = 0;
		
		xScale = 1;
		yScale = 1;
		depth = Integer.MAX_VALUE;
		isActive = true;
		objTypes = new Array<String>();
		ID = -1;
		
		setDirection(0);
		setRotation (0);
		setSpriteIndex(0);
		setCollides(false);
		
		onCreate();
		addToLevel();
	}
	
	/**
	 * Overridable method called on the creation of a GameObject
	 */
	public void onCreate(){
		
	}
	
	
	/**
	 * Called before the update function in the game loop
	 */
	public final void preUpdate(){
		if (isActive){
			preStep();
		}
	}
	
	/**
	 * Overridable method for extension of GameObject. Called before the 
	 * step and after draw for each GameObject in the level.
	 */
	public void preStep(){
		
	}
	
	/**
	 * Update the GameObject
	 */
	public final void update(){
		if (isActive){
			updateKinematics ();
			onStep();
			updateSprite();
		}
	}
	
	/**
	 * Update the GameObjects kinematics
	 */
	private void updateKinematics (){
		y += vSpeed;
		x += hSpeed;
		
		if ((previousX != x || previousY != y) && getCollides()){
			Level.collisionGrid.removeFromGrid(this);
			Circle temp = getBoundingCollisionCircle();
			temp.set(x, y, temp.radius);
			//setBoundingCollisionCircle(temp);
			Level.collisionGrid.addToGrid(this);
		}
		
		previousX = x;
		previousY = y;
		
		float newSpeed = vSpeed * vSpeed + hSpeed * hSpeed;
		if (newSpeed != speed2){
			speed2 = newSpeed;
			direction = (float)Math.atan2(vSpeed, hSpeed);
		}
		
		vSpeed += gravity * gravityDir;
	}
	
	/**
	 * Update the sprite using the GameObjects updated kinematic informations
	 */
	public void updateSprite(){
		if (sprites.size > 0){
			sprites.get(spriteIndex).x = x;
			sprites.get(spriteIndex).y = y;
			sprites.get(spriteIndex).rotation = rotation;
			sprites.get(spriteIndex).xScale = xScale;
			sprites.get(spriteIndex).yScale = yScale;
			sprites.get(spriteIndex).update();
		}
	}
	
	
	/**
	 * Overridable method for extension of GameObject. Called each game loop
	 * after draw and preStep.
	 */
	public void onStep (){
		
	}
	
	/**
	 * Runs at the end of the update method
	 */
	public final void endUpdate(){
		postStep();
	}
	
	/**
	 * Runs at the end of the update method
	 */
	public void postStep(){
		
	}
	
	
	
	/**
	 * Draws the game object sprites
	 */
	public final void draw(SpriteBatch spriteBatch){
		if (isActive && sprites.size > 0){
			
			if (sprites.get(spriteIndex).getCurrentFrame() == 0){
				onAnimationStart();
			}
						
			sprites.get(spriteIndex).draw(spriteBatch);	
			
			onDraw(spriteBatch);			
			
			if (sprites.get(spriteIndex).getCurrentFrame() == sprites.get(spriteIndex).nFrames -1){
				onAnimationEnd();
			}
		}
		else if (isActive){
			onDraw(spriteBatch);
		}
	}
	
	/**
	 * Called at the end of the draw method of the game
	 * @param spriteBatch the level SpriteBatch object
	 */
	public void onDraw (SpriteBatch spriteBatch){
		
	}
	
	/**
	 * Called at the start of a sprite animation
	 */
	public void onAnimationStart (){
		
	}
	
	/**
	 * Called at the end of a sprite animation
	 */
	public void onAnimationEnd (){
		
	}
	
	/**
	 * Add the object to the list of objects to add to the level
	 */
	public void addToLevel(){
		Level.addGameObjects.add(this);
		ID = Level.runningObjCount;
		Level.runningObjCount ++;
	}
	
	/**
	 * Add the object to the list of objects to remove from the level
	 */
	public void removeFromLevel(){
		Level.removalGameObjects.add(ID);
	}
	
	
	/**
	 * Dispose of the game object
	 */
	public void dispose(){
		
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	////////////////////////////////////////////////////////////
	//					Setters and Getters                   //
	////////////////////////////////////////////////////////////
	
	/**
	 * Get the index of the sprite in Sprites array being displayed
	 * @return the spriteIndex
	 */
	public int getSpriteIndex() {
		return spriteIndex;
	}


	/**
	 * Set the index of the sprite in Sprites array to display
	 * @param spriteIndex the spriteIndex to set
	 */
	public void setSpriteIndex(int spriteIndex) {
		int previousInd = this.spriteIndex;
		this.spriteIndex = spriteIndex;
		if (sprites != null && sprites.size > 0){
			sprites.get(spriteIndex).x = x;
			sprites.get(spriteIndex).y = y;
			sprites.get(spriteIndex).rotation = rotation;
			sprites.get(spriteIndex).xScale = xScale;
			sprites.get(spriteIndex).yScale = yScale;
			if (spriteIndex != previousInd){
				sprites.get(spriteIndex).setCurrentFrame(0);
				Circle temp = getBoundingCollisionCircle();
				temp.set(x, y, temp.radius);
			}
		}
	}


	/**
	 * Get the direction the object is traveling in
	 * @return the direction
	 */
	public float getDirection() {
		return direction;
	}


	/**
	 * Set the direction the object will travel in
	 * @param direction the direction to set
	 */
	public void setDirection(float direction) {
		this.direction = direction;
		vSpeed = (float)(speed*Math.sin(direction));
		hSpeed = (float)(speed*Math.cos(direction));
	}


	/**
	 * Get the speed of the GameObject
	 * @return the speed
	 */
	public float getSpeed() {
		speed = (float)Math.sqrt(speed2);
		return speed;
	}


	/**
	 * Set the speed of the GameObject
	 * @param speed the speed to set
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
		speed2 = speed*speed;
		vSpeed = (float)(speed * Math.sin(direction));
		hSpeed = (float)(speed * Math.cos(direction));
	}


	/**
	 * Gets the bounding circle used for the sprite to check for collisions
	 * with the collision grid
	 * @return the boundingCollisionCircle
	 */
	public Circle getBoundingCollisionCircle() {
		if (sprites.size > spriteIndex){
			return sprites.get(spriteIndex).boundingCollisionCircle;
		}
		return new Circle();
	}


	/**
	 * Sets the bounding circle used for the sprite to check for collisions
	 * with the collision grid
	 * @param boundingCollisionCircle the boundingCollisionCircle to set
	 */
	public void setBoundingCollisionCircle(Circle boundingCollisionCircle) {
		if (sprites.size > 0){
			sprites.get(spriteIndex).boundingCollisionCircle = boundingCollisionCircle;
		}
	}


	/**
	 * Gets whether the GameObject collides
	 * @return the collides
	 */
	public boolean getCollides() {
		return collides;
	}


	/**
	 * Sets whether the GameObject collides
	 * @param collides the collides to set
	 */
	public void setCollides(boolean collides) {
		boolean previous = this.collides;
		this.collides = collides;
		if (!previous && collides && ID !=-1){
			Level.collisionGrid.addToGrid(this);
		}
		else if (previous && !collides){
			Level.collisionGrid.removeFromGrid(this);
		}
	}


	/**
	 * Gets the rotation of the sprite drawn for this GameObject
	 * @return the rotation
	 */
	public float getRotation() {
		if (sprites.size > 0){
			return sprites.get(spriteIndex).rotation;
		}
		return rotation;
	}


	/**
	 * Sets the rotation of the sprite drawn for this GameObject
	 * @param rotation the rotation to set
	 */
	public void setRotation(float rotation) {
		if (sprites != null && sprites.size > 0){
			sprites.get(spriteIndex).rotation = rotation;
			this.rotation = rotation;
		}
	}

	public long getCollision(String type){
		long collision = -1;
		for (GameObject obj: Level.collisionGrid.findGameObjectsForCollision(this, type)){
			if (this != obj && collidesWith(obj)){
				collision = obj.ID;
				break;
			}
		}
		return collision;
	}
	
	
	public boolean collidesWith(float pX, float pY){
		boolean collision = false;
		collision = sprites.get(spriteIndex).collidesWith(pX, pY);
		return collision;
	}
	
	public boolean collidesWith(GameObject otherObj){
		boolean collision = false;
		collision = sprites.get(spriteIndex).collidesWith(otherObj.sprites.get(otherObj.spriteIndex));
		return collision;
	}
	
	public boolean collidesWith(String type){
		boolean collision = false;
		for (GameObject obj: Level.collisionGrid.findGameObjectsForCollision(this, type)){
			if (this != obj && collidesWith(obj)){
				collision = true;
				break;
			}
		}
		return collision;
	}
	
	public boolean checkCollision(int xLoc, int yLoc, String objType){
		boolean collision = false;
		sprites.get(spriteIndex).mask.update(xLoc - sprites.get(spriteIndex).originX, yLoc - sprites.get(spriteIndex).originY);

		collision = collidesWith (objType);
		sprites.get(spriteIndex).mask.update(sprites.get(spriteIndex).x - sprites.get(spriteIndex).originX,
				sprites.get(spriteIndex).y - sprites.get(spriteIndex).originY);

		return collision;
	}
	
	
}
