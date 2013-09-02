package com.me.Engine.Structures;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.LongMap;
import com.me.Engine.Camera.GameCamera;
import com.me.Engine.Collision.CollisionGrid;
import com.me.Engine.Data.Drawable;

/**
 * @author Kieran
 *
 */
public class Level implements Screen{

	/**
	 * Reference to the game instance
	 */
	public Game gameInstance;
	
	/**
	 * TextureAtlas containing all the images for the game
	 */
	public static TextureAtlas imageAtlas;
	
	/**
	 * Keeps count of the GameObjects added to the level.
	 * GameObject ID is generated using this counter
	 */
	public static long runningObjCount;
	
	/**
	 * List of objects that will be removed from the level 
	 * at the end of the step
	 */
	public static LongArray removalGameObjects;
	
	
	/**
	 * List of objects that be added to the level at the
	 * end of the step
	 */
	public static Array<GameObject> addGameObjects;
	
	// List used to draw the objects using depth order
	private Array<Drawable> orderedDrawingList;
	
	/**
	 * Stores the background image for the level
	 */
	public Texture backgroundImage;
	
	//Draws the background image
	private SpriteBatch spriteBatch;
	
	/**
	 * Debugging shape renderers
	 */
	public static ShapeRenderer lineRenderer = new ShapeRenderer();
	public static ShapeRenderer circleRenderer = new ShapeRenderer();
	
	private boolean isActive;
	
	/**
	 * Used to turn fps logging on and off
	 */
	protected boolean logFPS = false;
	
	
	/**
	 * Stores the level camera
	 */
	public static GameCamera camera;
	
	/**
	 * Stores the level width
	 */
	public float levelWidth;
	/**
	 * Stores the level height
	 */
	public float levelHeight;
	
	/**
	 * Stores the screen width
	 */
	public float screenWidth;
	/**
	 * Stores the screen height
	 */
	public float screenHeight;
	
	/**
	 * Stores the GameObjects currently in the level
	 */
	public static LongMap<GameObject> gameObjects;
	
	/**
	 * Stores the GameSprites currently in the level.
	 * Used to store sprites that dont need game object
	 * logic
	 */
	public static Array<GameSprite> gameSprites;
	
	/**
	 * Stores the standard cell size for tiles in the
	 * world
	 */
	public static float worldCellSize;
	
	/**
	 * Grid storing which objects can collide
	 */
	public static CollisionGrid collisionGrid;
	
	FPSLogger fps = new FPSLogger();
	
	// TODO create fields to store Pather
	
	///////////////////////////////////////////////
	//				Creation Methods
	///////////////////////////////////////////////
	/**
	 * Create a new level
	 * @param game
	 * @param levelWidth
	 * @param levelHeight
	 * @param screenWidth
	 * @param screenHeight
	 * @param collisionCellSize
	 * @param worldCellSize
	 */
	public Level(Game game, int levelWidth, int levelHeight, int screenWidth, int screenHeight, int collisionCellSize, float worldCellSize){
		gameInstance = game;	
		removalGameObjects = new LongArray();
		addGameObjects = new Array<GameObject>();
		
		gameObjects = new LongMap<GameObject>();
		gameSprites = new Array<GameSprite>();
		
		// TODO Setup Pathing here
		Level.collisionGrid = new CollisionGrid(levelWidth, levelHeight, collisionCellSize);
		
		runningObjCount = 1;
		
		this.levelWidth = levelWidth;
		this.levelHeight = levelHeight;
		
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		Level.worldCellSize = worldCellSize;
		
		isActive = false;
		orderedDrawingList = new Array<Drawable>();
		
		camera = new GameCamera();
		
		onCreate();
	}
	
	
	/**
	 * Overridable method called on the creation of a Level object
	 */
	public void onCreate(){
		
	}
	

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public final void render(float delta) {
		if (logFPS){
			fps.log();
		}
		if (isActive){
			//Draw the scene
			draw();
			//Update all objects in the scene
			update();
		}
	}
	
	
	
	public final void draw(){
		//Clear the screen and update the camera
		camera.onUpdate();
		spriteBatch.setProjectionMatrix(camera.combined);
		lineRenderer.setProjectionMatrix(camera.combined);
		circleRenderer.setProjectionMatrix(camera.combined);
		
		orderedDrawingList.clear();
		
		/*
		 * Insert GameObjects into the drawing array. Sorted by depth
		 * so that we can have layered scenes
		 */
		for (GameObject cGameObject:gameObjects.values()){
			insertIntoDrawingList(cGameObject);
		}

		/*
		 * Insert GameSprites into the drawing array. Sorted by depth
		 * so that we can have layered scenes
		 */
		for (int i = 0 ; i < gameSprites.size; i ++){
			GameSprite cGameSprite = gameSprites.get(i);
			//cGameSprite.draw();
			insertIntoDrawingList(cGameSprite);
		}
		
	
		//spriteBatch.setProjectionMatrix(camera.combined);
		circleRenderer.setColor(Color.GREEN);
		circleRenderer.begin(ShapeType.Line);
		lineRenderer.setColor(Color.MAGENTA);
		lineRenderer.begin(ShapeType.Line);
		spriteBatch.begin();
		
		
		drawBackground();
		
		for (int i = orderedDrawingList.size-1; i >= 0 ; i --){
			if (orderedDrawingList.get(i).autoDraw){
				orderedDrawingList.get(i).draw(spriteBatch);
			}
		}
		
		spriteBatch.end();
		lineRenderer.end();
		circleRenderer.end();
	}
	
	private void insertIntoDrawingList(Drawable drawable){
		if (orderedDrawingList.size == 0){
			orderedDrawingList.add(drawable);
			return;
		}
		int insertInd = 0;
		for (int i = 0; i < orderedDrawingList.size; i ++){
			insertInd = i;
			if (drawable.depth < orderedDrawingList.get(i).depth){		
				break;
			}
		}
		
		orderedDrawingList.insert(insertInd, drawable);
	}
	
	private void drawBackground(){
		if (backgroundImage != null){
			
		}
	}
	
	
	public final void update (){
		//Overridable method for extension of this object
		preStep();

		//Remove game objects from level
		removeGameObjects();
		//Add new game objects to level
		addGameObjects();

		/*
		 * Run PreUpdate Methods for each object in the level
		 */
		for (GameObject cGameObject:gameObjects.values()){
			cGameObject.preUpdate();
		}

		/*
		 * Run Update Methods for each object in the level
		 */
		for (GameObject cGameObject:gameObjects.values()){
			cGameObject.update();
		}
		
		/*
		 * Run EndUpdate Methods for each object in the level
		 */
		for (GameObject cGameObject:gameObjects.values()){
			cGameObject.endUpdate();
		}

		/*
		 * Run Update Methods for each sprite in the level
		 */
		for (GameSprite cGameSprite:gameSprites){
			cGameSprite.update();
		}		

		//Overridable method for extension of this object
		onStep();

		//Overridable method for extension of this object
		postStep();
	}
	
	/**
	 * Overridable method called after draw but before step
	 */
	public void preStep(){
		
	}
	
	/**
	 * Overridable method called after preStep but before postStep
	 */
	public void onStep(){
		
	}
	
	/**
	 * Overridable method called last in the game loop
	 */
	public void postStep(){
		
	}
	
	/**
	 * Add any new GameObject's to the level
	 */
	private void addGameObjects(){
		for(int i = 0; i < addGameObjects.size; i ++){
			GameObject currentObj = addGameObjects.get(i);
			if(!gameObjects.containsKey(currentObj.ID)){
				gameObjects.put(currentObj.ID, currentObj);
			}
		}
		addGameObjects.clear();
	}
	
	/**
	 * Remove necessary Game Objects from the level
	 */
	private void removeGameObjects(){
		for (int i = 0; i < removalGameObjects.size; i ++){
			long objId = removalGameObjects.get(i);
			try{
				GameObject removal = gameObjects.get(objId);
				gameObjects.remove(objId);
				Level.collisionGrid.removeFromGrid(removal);
			}
			catch(Exception e){
				e.printStackTrace();
				// TODO change exception to a useful exception
			}
		}
		removalGameObjects.clear();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		
		camera.setToOrtho(true,screenWidth,screenHeight);
		camera.position.set(screenWidth/2,screenHeight/2,0);
		camera.bounds = new Rectangle(0,0,screenWidth,screenHeight);
		
		Gdx.graphics.setDisplayMode((int)screenWidth, (int)screenHeight, false);
		
		spriteBatch = new SpriteBatch();
		isActive = true;
	}

	@Override
	public void hide() {
		isActive = false;
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		/*
		 * Dispose the background texture
		 */
		backgroundImage.dispose();
		
		/*
		 * Run dispose Methods for each object in the level
		 */
		for (long i = 0 ; i < gameObjects.size; i ++){
			GameObject cGameObject = gameObjects.get(i);
			cGameObject.dispose();
		}
		
		/*
		 * Run dispose Methods for each sprite in the level
		 */
		for (int i = 0 ; i < gameSprites.size; i ++){
			GameSprite cGameSprite = gameSprites.get(i);
			cGameSprite.dispose();
		}
		
	}

}
