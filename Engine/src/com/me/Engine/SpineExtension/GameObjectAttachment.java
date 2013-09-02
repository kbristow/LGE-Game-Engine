package com.me.Engine.SpineExtension;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.me.Engine.Structures.GameObject;

/**
 * @author Kieran
 *
 */
public class GameObjectAttachment extends Attachment{

	public GameObject gameObject;
	float xScale = 2.0f;
	float yScale = 2.0f;
	float initialRotation = 0;
	
	/**
	 * @return the gameObject
	 */
	public GameObject getGameObject() {
		return gameObject;
	}

	/**
	 * @param gameObject the gameObject to set
	 */
	public void setGameObject(GameObject gameObject) {
		this.gameObject = gameObject;
		gameObject.autoDraw = false;
	}
	
	
	/**
	 * Create a new GameObjectAttachment. Used to create GameObjects as slot attachments. This makes for easily handling collisions and animation.
	 * @param name Name of the GameObjectAttachment
	 */
	public GameObjectAttachment(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Updates the attached gameobject's rotation, scale and location using the provided slot.
	 * @param slot
	 */
	public void updateFromSlot(Slot slot){
		if (gameObject != null){
			Bone slotBone = slot.getBone();
			int xScaleM;
			if (!slot.getSkeleton().getFlipX()){
				gameObject.setRotation(-slotBone.getWorldRotation() - initialRotation);
				xScaleM = -1;
			}
			else{
				gameObject.setRotation(slotBone.getWorldRotation() + initialRotation);
				xScaleM = 1;
			}
			gameObject.x = slotBone.getWorldX() + slot.getSkeleton().getX();
			gameObject.y = slotBone.getWorldY() + slot.getSkeleton().getY();
			gameObject.xScale = xScaleM * xScale * slotBone.getWorldScaleX();
			gameObject.yScale = yScale * slotBone.getWorldScaleY();
			gameObject.updateSprite();
		}
	}

	
	/**
	 * Draws the attached game object. Used by the extended skeleton renderer. Not universal since the object still scales with the spine root.
	 * @param batch
	 */
	public void draw(SpriteBatch batch){
		if (gameObject != null){
			gameObject.draw(batch);
		}
	}
	
	/**
	 * Set the base xScale of the game object
	 * @param xScale
	 */
	public void setXScale(float xScale){
		this.xScale = xScale;
	}
	
	/**
	 * Set the base xScale of the game object. Not universal since the object still scales with the spine root.
	 * @param xScale
	 */
	public void setYScale(float yScale){
		this.yScale = yScale;
	}

	/**
	 * Gets the base/initial rotation of the game object.
	 * @return the initialRotation
	 */
	public float getInitialRotation() {
		return initialRotation;
	}

	/**
	 * @param initialRotation Sets the base/initial rotation of the game object.
	 */
	public void setInitialRotation(float initialRotation) {
		this.initialRotation = initialRotation;
	}
	
	
	
	
}
