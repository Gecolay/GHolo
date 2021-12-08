package dev.geco.gholo.objects;

import java.util.*;

public class HoloAnimation {
    
    private String i;
    
    private long t;
    
    private long a;
    
    private List<String> c = new ArrayList<>();
    
    private int r;
    
    public HoloAnimation(String Id, long T, List<String> C) {
        i = Id;
        t = T;
        a = 0;
        c = C;
        r = 0;
    }
    
    public String getId() { return i; }
    
    public void setId(String Id) { i = Id; }
    
    public long getTicks() { return t; }
    
    public void setTicks(long T) { t = T; }
    
    public long getActiveTicks() { return a; }
    
    public void setActiveTicks(long A) { a = A; }
    
    public List<String> getContext() { return c; }
    
    public void setContext(List<String> C) { c = C; }
    
    public int getRow() { return r; }
    
    public void setRow(int R) { r = R; }
    
}