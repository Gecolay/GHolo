package dev.geco.gholo.util;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.*;
import javax.imageio.ImageIO;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.values.Values;

public class ImageUtil {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String AVATAR_URL_WITH_OVERLAY = "https://minotar.net/helm/";
    
    private static final String AVATAR_URL_WITHOUT_OVERLAY = "https://minotar.net/avatar/";
    
    private static final int DEFAULT_SIZE = 16;
    
    public static final ChatColor DEFAULT_TRANSPARENCY_COLOR = ChatColor.GRAY;
    
    private final boolean A = Arrays.stream(net.md_5.bungee.api.ChatColor.class.getMethods()).filter(m -> "of".equals(m.getName())).findFirst().orElse(null) != null;
    
    private final int C = 500;
    
    public static final List<String> IMAGE_TYPES = Arrays.asList("avatar", "helm", "file", "url");
    
    public static final File IMAGE_PATH = new File("plugins/" + GHoloMain.getInstance().NAME + "/" + Values.IMAGES_PATH + "/");
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    private final Color[] colors = {
            
        new Color(0, 0, 0),
        new Color(0, 0, 170),
        new Color(0, 170, 0),
        new Color(0, 170, 170),
        new Color(170, 0, 0),
        new Color(170, 0, 170),
        new Color(255, 170, 0),
        new Color(170, 170, 170),
        new Color(85, 85, 85),
        new Color(85, 85, 255),
        new Color(85, 255, 85),
        new Color(85, 255, 255),
        new Color(255, 85, 85),
        new Color(255, 85, 255),
        new Color(255, 255, 85),
        new Color(255, 255, 255)
        
    };
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    private String[] lines;
    
    public List<String> getLines() {
        
        List<String> l = new ArrayList<>();
        
        for(String i : lines) l.add(i);
        
        return l;
        
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public static BufferedImage getBufferedImage(String URL) { try { return ImageIO.read(new URL(URL)); } catch (Exception e) { return null; } }
    
    public static BufferedImage getBufferedImage(OfflinePlayer Player, boolean Overlay) { try { return ImageIO.read(new URL((Overlay ? AVATAR_URL_WITH_OVERLAY : AVATAR_URL_WITHOUT_OVERLAY) + Player.getName() + "/" + DEFAULT_SIZE)); } catch (Exception e) { return null; } }
    
    public static BufferedImage getBufferedImage(File File) { try { return ImageIO.read(File); } catch (Exception e) { return null; } }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public ImageUtil(BufferedImage Image) { lines = toLines(toColorArray(Image, Image.getWidth(), Image.getHeight())); }
    
    public ImageUtil(BufferedImage Image, int Size) {
        
        if(Size <= 0) throw new NumberFormatException();
        if(Size > C) Size = C;
        lines = toLines(toColorArray(Image, Size, Size));
        
    }
    
    public ImageUtil(BufferedImage Image, int Width, int Height) {
        
        if(Width <= 0 || Height <= 0) throw new NumberFormatException();
        if(Width > C) Width = C;
        if(Height > C) Height = C;
        lines = toLines(toColorArray(Image, Width, Height));
        
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    private String[][] toColorArray(BufferedImage Image, int Width, int Height) {
        
        BufferedImage BI = Width != Image.getWidth() && Height != Image.getHeight() ? resizeImage(Image, Width, Height) : Image;
        
        String[][] CI = new String[BI.getWidth()][BI.getHeight()];
        
        for(int x = 0; x < BI.getWidth(); x++) for(int y = 0; y < BI.getHeight(); y++) CI[x][y] = getColor(new Color(BI.getRGB(x, y), true));
        
        return CI;
        
    }
    
    private String[] toLines(String[][] Colors) {
        
        String[] li = new String[Colors[0].length];
        
        String tC = DEFAULT_TRANSPARENCY_COLOR.toString();
        String tS = " [|] ";
        String iS = "[X]";
        
        for(int y = 0; y < Colors[0].length; y++) {
            
            StringBuffer l = new StringBuffer();
            
            String p = ChatColor.RESET.toString();
            
            for(int x = 0; x < Colors.length; x++) {
                
                String cC = Colors[x][y];
                
                if(cC == null) {
                    
                    if (p != tC) {
                        
                        l.append(tC);
                        p = tC;
                        
                    }
                    
                    l.append(tS);
                    
                } else {
                    
                    if (p != cC) {
                        
                        l.append(cC.toString());
                        p = cC;
                        
                    }
                    
                    l.append(iS);
                    
                }
                
            }
            
            li[y] = l.toString();
            
        }
        
        return li;
        
    }
    
    private BufferedImage resizeImage(BufferedImage Image, int Width, int Height) {
        
        AffineTransform af = new AffineTransform();
        
        af.scale(Width / (double) Image.getWidth(), Height / (double) Image.getHeight());
        
        AffineTransformOp operation = new AffineTransformOp(af, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        
        return operation.filter(Image, null);
        
    }
    
    private double getDistance(Color Color1, Color Color2) {
        
        double rmean = (Color1.getRed() + Color2.getRed()) / 2.0;
        double r = Color1.getRed() - Color2.getRed();
        double g = Color1.getGreen() - Color2.getGreen();
        int b = Color1.getBlue() - Color2.getBlue();
        
        return (2 + rmean / 256.0) * r * r + 4.0 * g * g + (2 + (255 - rmean) / 256.0) * b * b;
        
    }
    
    private boolean checkIdentical(Color Color1, Color Color2) { return Math.abs(Color1.getRed() - Color2.getRed()) <= 5 && Math.abs(Color1.getGreen() - Color2.getGreen()) <= 5 && Math.abs(Color1.getBlue() - Color2.getBlue()) <= 5; }
    
    private String getColor(Color Color) {
        
        if(Color.getAlpha() < 128) return null;
        
        if(A) return net.md_5.bungee.api.ChatColor.of(Color).toString();
        
        int index = 0;
        double best = -1;
        
        for(int i = 0; i < colors.length; i++) if(checkIdentical(colors[i], Color)) return ChatColor.values()[i].toString();
        
        for(int i = 0; i < colors.length; i++) {
            
            double distance = getDistance(Color, colors[i]);
            
            if(distance < best || best == -1) {
                best = distance;
                index = i;
            }
            
        }
        
        return ChatColor.values()[index].toString();
        
    }
    
}