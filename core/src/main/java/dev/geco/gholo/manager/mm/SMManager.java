package dev.geco.gholo.manager.mm;

import org.bukkit.command.*;
import org.bukkit.entity.*;

import net.md_5.bungee.api.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.manager.*;

public class SMManager extends MManager {

    public SMManager(GHoloMain GPluginMain) { super(GPluginMain); }

    public void sendMessage(CommandSender Target, String Message, Object... ReplaceList) { Target.sendMessage(getLanguageMessage(Message, getLanguage(Target), ReplaceList)); }

    public void sendActionBarMessage(Player Target, String Message, Object... ReplaceList) { if(allowBungeeMessages) Target.spigot().sendMessage(ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(getLanguageMessage(Message, getLanguage(Target), ReplaceList))); }

}