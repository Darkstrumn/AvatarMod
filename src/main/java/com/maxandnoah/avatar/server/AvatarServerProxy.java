package com.maxandnoah.avatar.server;

import com.maxandnoah.avatar.common.AvatarCommonProxy;
import com.maxandnoah.avatar.common.IKeybindingManager;
import com.maxandnoah.avatar.common.network.PacketHandlerServer;
import com.maxandnoah.avatar.common.network.IPacketHandler;

public class AvatarServerProxy implements AvatarCommonProxy {
	
	private AvatarKeybindingServer keys;
	
	@Override
	public void preInit() {
		keys = new AvatarKeybindingServer();
	}

	@Override
	public IKeybindingManager getKeyHandler() {
		return keys;
	}

	@Override
	public IPacketHandler getClientPacketHandler() {
		return null;
	}

	@Override
	public double getPlayerReach() {
		return 0;
	}
	
}
