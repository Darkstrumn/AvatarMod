package com.crowsofwar.avatar.common.network.packets;

import com.crowsofwar.avatar.common.network.IAvatarPacket;
import com.crowsofwar.avatar.common.network.PacketRedirector;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketSUseBendingController implements IAvatarPacket<PacketSUseBendingController> {
	
	private int id;
	
	public PacketSUseBendingController() {}
	
	public PacketSUseBendingController(int id) {
		this.id = id;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
	}
	
	@Override
	public IMessage onMessage(PacketSUseBendingController message, MessageContext ctx) {
		return PacketRedirector.redirectMessage(message, ctx);
	}
	
	@Override
	public Side getRecievedSide() {
		return Side.SERVER;
	}
	
	public int getBendingControllerId() {
		return id;
	}
	
}
