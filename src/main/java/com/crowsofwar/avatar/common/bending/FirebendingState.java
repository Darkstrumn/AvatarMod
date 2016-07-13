package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class FirebendingState implements IBendingState {

	private EntityFireArc fireArc;
	private AvatarPlayerData data;
	
	public FirebendingState(AvatarPlayerData data) {
		fireArc = null;
		this.data = data;
	}
	
	public int getFireArcId() {
		return fireArc == null ? -1 : fireArc.getId();
	}
	
	public EntityFireArc getFireArc() {
		return fireArc;
	}
	
	public boolean isManipulatingFire() {
		return fireArc != null;
	}
	
	public void setFireArc(EntityFireArc arc) {
		fireArc = arc;
	}
	
	public void setNoFireArc() {
		setFireArc(null);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		// TODO Implement saving fire arcs
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(getFireArcId());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		System.out.println(data.getState().getPlayerEntity().worldObj.loadedEntityList);
		fireArc = EntityFireArc.findFromId(data.getState().getPlayerEntity().worldObj, buf.readInt());
	}

	@Override
	public int getId() {
		return BendingManager.BENDINGID_FIREBENDING;
	}
	
}
