package dev.geco.gholo.manager;

import java.awt.Color;
import java.util.*;
import java.util.regex.*;

import org.bukkit.command.*;

import dev.geco.gholo.GHoloMain;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;

public class MManager {
    
    private GHoloMain GPM;
    
    public MManager(GHoloMain GPluginMain) { GPM = GPluginMain; }
    
    private final char C = '&';
    
    private final boolean A = Arrays.stream(ChatColor.class.getMethods()).filter(m -> "of".equals(m.getName())).findFirst().orElse(null) != null;
    
    private String getColoredMessage(String Message) {
        String r = ChatColor.translateAlternateColorCodes(C, Message);
        if(A) {
            Matcher m = Pattern.compile("(#[0-9a-fA-F]{6})").matcher(r);
            while(m.find()) r = r.replace(m.group(), ChatColor.of(m.group()).toString());
        }
        return r;
    }
    
    public String getGradientMessage(String Message) {
        String r = Message;
        if(A) {
            Matcher m = Pattern.compile("(\\[#[0-9a-fA-F]{6}[^#]*#[0-9a-fA-F]{6}\\])").matcher(r);
            while(m.find()) {
                String t = m.group(), re = "";
                t = t.substring(1, t.length() - 1);
                Color c1 = ChatColor.of(t.substring(0, 7)).getColor(), c2 = ChatColor.of(t.substring(t.length() - 7)).getColor();
                t = t.substring(7, t.length() - 7);
                Color[] ca = getGradient(c1, c2, t.length());
                int c = 0;
                for(char i : t.toCharArray()) {
                    re += ChatColor.of(ca[c]).toString() + i;
                    c++;
                }
                r = r.replace(m.group(), re + ChatColor.RESET);
            }
        }
        return getColoredMessage(r);
    }
    
    public void sendMessage(CommandSender S, String Message, Object... ReplaceList) { S.sendMessage(getMessage(Message, ReplaceList)); }
    
    public void sendMessage(CommandSender S, TextComponent... Message) { S.spigot().sendMessage(Message); }
    
    public String getMessage(String Message, Object... ReplaceList) {
        String m = GPM.getMessages().getString(Message, Message);
        return getGradientMessage(replace(m, ReplaceList));
    }
    
    public List<String> getMessageList(String Message, Object... ReplaceList) {
        List<String> m = GPM.getMessages().getStringList(Message), l = new ArrayList<>();
        for(String i : m) l.add(getGradientMessage(replace(i, ReplaceList)));
        return l;
    }
    
    private String replace(String Message, Object... ReplaceList) {
        String m = Message;
        if(ReplaceList.length > 1) for(int i = 0; i < ReplaceList.length; i += 2) if(ReplaceList[i] != null && ReplaceList[i + 1] != null) m = m.replace(ReplaceList[i].toString(), ReplaceList[i + 1].toString());
        return m.replace("[P]", GPM.getPrefix());
    }
    
    private Color[] getGradient(Color C1, Color C2, int S) {
        int b = S < 0 ? 0 : S;
        int a = b + 2;
        Color[] o = new Color[a];
        o[0] = C1;
        o[a - 1] = C2;
        if(b > 0) {
            boolean cr1b = C1.getRed() >= C2.getRed();
            boolean cg1b = C1.getGreen() >= C2.getGreen();
            boolean cb1b = C1.getBlue() >= C2.getBlue();
            int crf = (cr1b ? C1.getRed() - C2.getRed() : C2.getRed() - C1.getRed()) / b;
            int cgf = (cg1b ? C1.getGreen() - C2.getGreen() : C2.getGreen() - C1.getGreen()) / b;
            int cbf = (cb1b ? C1.getBlue() - C2.getBlue() : C2.getBlue() - C1.getBlue()) / b;
            for(int i = 1; i < b + 1; i++) o[i] = new Color(cr1b ? C1.getRed() - crf * i : C1.getRed() + crf * i, cg1b ? C1.getGreen() - cgf * i : C1.getGreen() + cgf * i, cb1b ? C1.getBlue() - cbf * i : C1.getBlue() + cbf * i);
        }
        return o;
    }
    
}