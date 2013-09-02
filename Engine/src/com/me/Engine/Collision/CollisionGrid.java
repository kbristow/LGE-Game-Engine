package com.me.Engine.Collision;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongArray;
import com.me.Engine.Data.IntPair;
import com.me.Engine.Structures.GameObject;
import com.me.Engine.Structures.Level;

public class CollisionGrid {

	public LongArray [][] grid;
	
	public Rectangle [][] boundChecks = new Rectangle[3][3];
	
	public int cellSize;
	
	public int gridWidth;
	
	public int gridHeight;
	
	/**
	 * Create a new CollisionGrid to track which GameObject's can collide
	 * @param width Width of the grid in pixels
	 * @param height Height of the grid in pixels
	 * @param cellSize Size of a grid cell, should be the size of the maximum dimension a sprite can have
	 */
	public CollisionGrid(int width, int height, int cellSize){
		this.cellSize = cellSize;
		gridWidth = (width/cellSize) + 1;
		gridHeight = (height/cellSize) + 1;
		
		grid = new LongArray [gridWidth][gridHeight];
		
		for (int i = 0; i < 3; i ++){
			for (int j = 0; j < 3; j ++){
				//Use 9 bounding boxes to determine which cell the object falls into. Think we should only need 4 so I should look into this at some point TODO
				boundChecks[i][j] = new Rectangle(i * cellSize, j * cellSize, cellSize, cellSize);
			}
		}
	}
	
	/**
	 * Add a GameObject to the grid. We determine which cells its bounds fall within and add it to the list of objects that are in that cell
	 * @param obj
	 */
	public void addToGrid (GameObject obj){
		int gridX = (int) (obj.x/cellSize);
		int gridY = (int) (obj.y/cellSize);
		
		//First we setup the bounding boxes we will use to find it's location
		for (int i = 0; i < 3; i ++){
			for (int j = 0; j < 3; j ++){
				int gridIndexX = i + gridX - 1;
				int gridIndexY = j + gridY - 1;
				boundChecks[i][j].setX(gridIndexX * cellSize);
				boundChecks[i][j].setY(gridIndexY * cellSize);
			}
		}
		
		//Now find the location. Should be able to combine these 2 loops so I must look into this
		for (int i = 0; i < 3; i ++){
			for (int j = 0; j < 3; j ++){
				int gridIndexX = i + gridX - 1;
				int gridIndexY = j + gridY - 1;
				if (gridIndexX >= 0 && gridIndexX < gridWidth && gridIndexY >= 0 && gridIndexY < gridHeight){
					Circle temp = obj.getBoundingCollisionCircle();
					if(IntersectorExtended.overlapCircleRectangle( temp, boundChecks[i][j])){
						addAt(gridIndexX, gridIndexY, obj.ID);
						//Store which cells the game object is in within the game object. Makes for less searching when we need to remove it
						obj.gridLocations.add(new IntPair(gridIndexX, gridIndexY));
					}
				}
			}
		}
	}
	
	/**
	 * Add a GameObjects level ID to the list of objects in the given cell
	 * @param i Cells x location
	 * @param j Cells y location
	 * @param value GameObject level ID to add to the grid
	 */
	private void addAt(int i, int j, long value){
		if(grid[i][j] == null){
			grid[i][j] = new LongArray();
		}
		
		grid[i][j].add(value);
	}
	
	/**
	 * Remove the given GameObject from all cells that contain it
	 * @param obj
	 */
	public void removeFromGrid(GameObject obj){
		for (int i = 0; i < obj.gridLocations.size; i ++){
			grid[obj.gridLocations.get(i).one][obj.gridLocations.get(i).two].removeValue(obj.ID);
		}
		obj.gridLocations.clear();
	}
	
	/**
	 * Find which game ojects may collide with a given point using the CollisionGrid
	 * @param location Point location
	 * @param otherType The type of the GameObject
	 * @return
	 */
	public Array<GameObject> findGameObjectsForPointCollision(float x, float y, String otherType){
		int gridX = (int)x/cellSize;
		int gridY = (int)y/cellSize;
		Array<GameObject> collisionObj = new Array<GameObject>();
		
		//Make sure point is within grid bounds
		if (grid.length <= gridX || grid[0].length <= gridY || gridX < 0 || gridY < 0){
			return collisionObj;
		}
		//Check first whether the grid cell has been initialised
		if (grid[gridX][gridY] != null){
			for (int i = 0; i < grid[gridX][gridY].size; i ++){
				GameObject objAtLoc = Level.gameObjects.get(grid[gridX][gridY].get(i));
				//Add the object to the list that we will return if it is of the type specified
				if (objAtLoc.objTypes.contains(otherType, true)){
					collisionObj.add(objAtLoc);
				}
			}
		}
		
		return collisionObj;
	}
	
	/**
	 * Find which game ojects may collide with a given GameObject using the CollisionGrid
	 * @param obj GameObject to check for collisions
	 * @param otherType Type of objects it may collide with
	 * @return
	 */
	public Array<GameObject> findGameObjectsForCollision(GameObject obj, String otherType){
		Array<GameObject> collisionObj = new Array<GameObject>();
		
		boolean checkType = otherType.length() > 0;
		
		for (int i = 0; i < obj.gridLocations.size; i ++ ){
			IntPair gLoc = obj.gridLocations.get(i);
			for (int j = 0; j < grid[gLoc.one][gLoc.two].size; j ++){
				GameObject objAtLoc = Level.gameObjects.get(grid[gLoc.one][gLoc.two].get(j));
				
				if (objAtLoc.ID != obj.ID && (!checkType || objAtLoc.objTypes.contains(otherType, false))){
					collisionObj.add(objAtLoc);
				}
			}
			
		}
		
		return collisionObj;
	}
	
	/**
	 * Find which game objects may collide with a given GameObject using the CollisionGrid whithout specifying a type
	 * @param obj GameObject to check for collisions
	 * @return
	 */
	public Array<GameObject> findGameObjectsForCollision(GameObject obj){
		return findGameObjectsForCollision(obj,"");
	}
	
}
