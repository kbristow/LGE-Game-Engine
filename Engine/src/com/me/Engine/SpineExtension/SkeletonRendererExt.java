package com.me.Engine.SpineExtension;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;

public class SkeletonRendererExt extends SkeletonRenderer {
	
	
	@Override
	public boolean onDrawAttachment(SpriteBatch batch, Slot slot, Attachment attachment, boolean additive){
		boolean superDrawn = super.onDrawAttachment(batch, slot, attachment, additive);
		if (!superDrawn){
			if (attachment instanceof GameObjectAttachment) {
				drawGameObjectAttachment(batch, slot, attachment, additive);
			}
		}
		return true;
	}
	
	public void drawGameObjectAttachment(SpriteBatch batch, Slot slot, Attachment attachment, boolean additive){
		GameObjectAttachment gameObjectAttachment = (GameObjectAttachment)attachment;
		gameObjectAttachment.updateFromSlot(slot);
		
		gameObjectAttachment.draw(batch);
	}
	
}
