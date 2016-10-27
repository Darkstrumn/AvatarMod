package com.crowsofwar.gorecore.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crowsofwar.gorecore.chat.FormattingState.ChatFormat;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ChatSender {
	
	private static final ChatSender instance;
	
	private static final Map<String, ChatMessage> referenceToChatMessage;
	private static final Map<String, ChatMessage> translateKeyToChatMessage;
	
	/**
	 * Cause static block to be called
	 */
	public static void load() {}
	
	static {
		instance = new ChatSender();
		MinecraftForge.EVENT_BUS.register(instance);
		referenceToChatMessage = new HashMap<String, ChatMessage>();
		translateKeyToChatMessage = new HashMap<String, ChatMessage>();
	}
	
	private ChatSender() {}
	
	public static ChatMessage newChatMessage(String translateKey, String... translateArgs) {
		return newChatMessage(MessageConfiguration.DEFAULT, translateKey, translateArgs);
	}
	
	public static ChatMessage newChatMessage(MessageConfiguration config, String translateKey,
			String... translateArgs) {
		ChatMessage cm = new ChatMessage(config, translateKey, translateArgs);
		translateKeyToChatMessage.put(translateKey, cm);
		return cm;
	}
	
	private Object[] getFormatArgs(TextComponentTranslation message) {
		return ObfuscationReflectionHelper.getPrivateValue(TextComponentTranslation.class, message, 1);
	}
	
	private String getKey(TextComponentTranslation message) {
		return ObfuscationReflectionHelper.getPrivateValue(TextComponentTranslation.class, message, 0);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void processClientChat(ClientChatReceivedEvent e) {
		if (e.getMessage() instanceof TextComponentTranslation) {
			TextComponentTranslation message = (TextComponentTranslation) e.getMessage();
			
			String result = "";
			
			List<ITextComponent> comps = new ArrayList();
			
			Object[] cloneFormatArgs = getFormatArgs(message);
			comps.add(new TextComponentTranslation(getKey(message), cloneFormatArgs));
			
			comps.addAll(e.getMessage().getSiblings());
			boolean changed = false;
			
			for (ITextComponent chat : comps) {
				String processed = processChatComponent(chat);
				if (processed != null) {
					changed = true;
					result += processed;
				}
			}
			if (changed) e.setMessage(new TextComponentTranslation(result));
		}
		
	}
	
	private String processChatComponent(ITextComponent chat) {
		String result = null;
		if (chat instanceof TextComponentTranslation) {
			TextComponentTranslation translate = (TextComponentTranslation) chat;
			String key = (String) getKey(translate);
			ChatMessage cm = translateKeyToChatMessage.get(key);
			
			if (cm != null) {
				
				try {
					result = processText(translate.getUnformattedText(), cm, translate.getFormatArgs());
				} catch (ProcessingException e) {
					result = "Error processing text; see log for details";
					e.printStackTrace();
				}
				
			}
			
		}
		
		return result;
		
	}
	
	private String processText(String text, ChatMessage cm, Object... formatArgs) {
		MessageConfiguration cfg = cm.getConfig();
		
		String[] translateArgs = cm.getTranslationArgs();
		for (int i = 0; i < translateArgs.length; i++) {
			text = text.replace("${" + translateArgs[i] + "}", formatArgs[i].toString());
		}
		
		Set<Map.Entry<String, String>> consts = cfg.getAllConstants();
		for (Map.Entry<String, String> entry : consts) {
			text = text.replace("${" + entry.getKey() + "}", entry.getValue());
		}
		
		FormattingState format = new FormattingState();
		
		String newText = "";
		String[] split = text.split("[\\[\\]]");
		for (int i = 0; i < split.length; i++) {
			boolean recievedFormatInstruction = false;
			String item = split[i];
			if (item.equals("")) continue;
			if (ChatFormat.isFormatFor(item)) {
				
				format.pushFormat(ChatFormat.lookup(item));
				recievedFormatInstruction = true;
				
			} else if (item.startsWith("/")) {
				
				if (item.substring(1).equals(format.topFormat().getName())) {
					
				} else {
					throw new ProcessingException(
							"Error processing message; closing tag does not match last opened tag: " + text);
				}
				
			} else if (item.startsWith("translate=")) {
				
				String key = item.substring("translate=".length());
				item = processText(I18n.format(key), cm, formatArgs);
				
			}
			
			// If any formats changed, must re add all chat formats
			if (recievedFormatInstruction) {
				newText += TextFormatting.RESET;
				newText += format.getColor(); // For some reason, color must
												// come before bold
				newText += format.isBold() ? TextFormatting.BOLD : "";
				newText += format.isItalic() ? TextFormatting.ITALIC : "";
			} else {
				newText += item;
			}
			
		}
		text = newText;
		
		return newText;
	}
	
	static void send(ICommandSender sender, ChatMessage message, Object... args) {
		sender.addChatMessage(message.getChatMessage(args));
	}
	
	static class ProcessingException extends RuntimeException {
		
		public ProcessingException(String message) {
			super(message);
		}
		
	}
	
}
