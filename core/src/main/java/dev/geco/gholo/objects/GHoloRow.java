package dev.geco.gholo.objects;

import dev.geco.gholo.GHoloMain;

public class GHoloRow {

    private String content;

    private double height;

    public GHoloRow(String Content) { this(Content, GHoloMain.getInstance().getCManager().SPACE_BETWEEN_LINES); }

    public GHoloRow(String Content, double Height) {

        content = Content;
        height = Height;
    }

    public String getContent() { return content; }

    public void setContent(String Content) { content = Content; }

    public double getHeight() { return height; }

    public void setHeight(double Height) { height = Height; }

}