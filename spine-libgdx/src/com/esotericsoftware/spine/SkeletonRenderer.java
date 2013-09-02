
package com.esotericsoftware.spine;

import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;

import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class SkeletonRenderer {
	public void draw (SpriteBatch batch, Skeleton skeleton) {
		Array<Slot> drawOrder = skeleton.drawOrder;
		boolean additive = false;
		for (int i = 0, n = drawOrder.size; i < n; i++) {
			Slot slot = drawOrder.get(i);
			Attachment attachment = slot.attachment;
			onDrawAttachment(batch, slot, attachment, additive);
		}
		if (additive) batch.setBlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public boolean onDrawAttachment(SpriteBatch batch, Slot slot, Attachment attachment, boolean additive){
		if (attachment instanceof RegionAttachment) {
			drawRegionAttachment(batch, slot, attachment, additive);
			return true;
		}
		return false;
	}
	
	public void drawRegionAttachment(SpriteBatch batch, Slot slot, Attachment attachment, boolean additive){
		RegionAttachment regionAttachment = (RegionAttachment)attachment;
		regionAttachment.updateVertices(slot);
		float[] vertices = regionAttachment.getVertices();
		if (slot.data.getAdditiveBlending() != additive) {
			additive = !additive;
			if (additive)
				batch.setBlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			else
				batch.setBlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		batch.draw(regionAttachment.getRegion().getTexture(), vertices, 0, vertices.length);
	}
}
