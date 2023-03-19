package dev.geco.gholo.util;

import java.util.Map.*;

import org.bukkit.entity.*;

import me.clip.placeholderapi.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.*;

public class FormatUtil {

    private final GHoloMain GPM;

    public FormatUtil(GHoloMain GPluginMain) { GPM = GPluginMain; }

    public String formatPlaceholders(String Text, Player Player) {
        String text = Text;
        for(Entry<String, GHoloAnimation> animation : GPM.getHoloAnimationManager().getAnimationSet().entrySet()) text = text.replace("%" + animation.getKey() + "%", animation.getValue().getCurrentContent());
        text = GPM.getPlaceholderAPILink() != null ? PlaceholderAPI.setPlaceholders(Player, text) : text;
        text = GPM.getMManager().toFormattedMessage(text);
        return text;
    }

    public String formatSymbols(String Text) {
        String text = Text;
        for(Entry<String, String> symbol : GPM.getCManager().SYMBOLS.entrySet()) text = text.replace(symbol.getKey(), symbol.getValue());
        return text;
    }

}