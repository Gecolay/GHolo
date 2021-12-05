package dev.geco.gholo.api;

import java.util.*;

import org.bukkit.Location;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.*;

public class GHoloAPI {
	
	private final GHoloMain g = GHoloMain.getInstance();
	
	/**
	* Returns the Instance
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public GHoloMain getInstance() { return g != null ? g : GHoloMain.getInstance(); }
	
	/**
	* Returns if a Holo exists by Holo Id
	* <p>
	* @param Id The Holo Id
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public boolean existsHolo(String Id) {
		
		return g.getHoloManager().existsHolo(Id);
		
	}
	
	/**
	* Returns a Holo by Holo Id
	* <p>
	* @param Id The Holo Id
	* <p>
	* @return The Holo or null
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public Holo getHolo(String Id) {
		
		return g.getHoloManager().getHolo(Id);
		
	}
	
	/**
	* Returns all Holos
	* <p>
	* @return All Holos as a List
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public HashMap<String, Holo> getHolos() {
		
		return g.getHoloManager().getHolos();
		
	}
	
	/**
	* Inserts a new Holo
	* <p>
	* @param Id The Holo Id
	* @param L The Holo Location
	* @param C The Holo Content
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public void insertHolo(String Id, Location L, List<String> C) {
		
		g.getHoloManager().insertHolo(Id, L, C);
		
	}
	
	/**
	* Removes a Holo by Holo Id
	* <p>
	* @param Id The Holo Id
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public void removeHolo(String Id) {
		
		g.getHoloManager().removeHolo(Id);
		
	}
	
	/**
	* Moves a Holo by Holo Id
	* <p>
	* @param Id The Holo Id
	* @param L The Holo Location
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public void moveHolo(String Id, Location L) {
		
		g.getHoloManager().moveHolo(Id, L);
		
	}
	
	/**
	* Adds Holo Content by Holo Id
	* <p>
	* @param Id The Holo Id
	* @param C The Holo Content
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public void addHoloContent(String Id, String C) {
		
		g.getHoloManager().addHoloContent(Id, C);
		
	}
	
	/**
	* Removes Holo Content by Holo Id
	* <p>
	* @param Id The Holo Id
	* @param R The Holo Row
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public void removeHoloContent(String Id, int R) {
		
		g.getHoloManager().removeHoloContent(Id, R);
		
	}
	
	/**
	* Sets the Holo Content by Holo Id
	* <p>
	* @param Id The Holo Id
	* @param C The Holo Content
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public void setHoloContent(String Id, List<String> C) {
		
		g.getHoloManager().setHoloContent(Id, C);
		
	}
	
	/**
	* Sets the Holo Content by Holo Id
	* <p>
	* @param Id The Holo Id
	* @param R The Holo Row
	* @param C The Holo Content
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public void setHoloContent(String Id, int R, String C) {
		
		g.getHoloManager().setHoloContent(Id, R, C);
		
	}
	
	/**
	* Inserts the Holo Content by Holo Id
	* <p>
	* @param Id The Holo Id
	* @param R The Holo Row
	* @param C The Holo Content
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public void insertHoloContent(String Id, int R, String C) {
		
		g.getHoloManager().insertHoloContent(Id, R, C);
		
	}
	
	/**
	* Sets the ShowCondition of a Holo by Holo Id
	* <p>
	* @param Id The Holo Id
	* @param C The ShowCondition
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public void setHoloCondition(String Id, String C) {
		
		g.getHoloManager().setHoloCondition(Id, C);
		
	}
	
	/**
	* Returns if a HoloAnimation exists by HoloAnimation Id
	* <p>
	* @param Id The HoloAnimation Id
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public boolean existsHoloAnimation(String Id) {
		
		return g.getAnimationManager().existsHoloAnimation(Id);
		
	}
	
	/**
	* Returns a HoloAnimation by HoloAnimation Id
	* <p>
	* @param Id The HoloAnimation Id
	* <p>
	* @return The HoloAnimation or null
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public HoloAnimation getHoloAnimation(String Id) {
		
		return g.getAnimationManager().getHoloAnimation(Id);
		
	}
	
	/**
	* Returns all HoloAnimations
	* <p>
	* @return All HoloAnimations as a List
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public HashMap<String, HoloAnimation> getHoloAnimations() {
		
		return g.getAnimationManager().getHoloAnimations();
		
	}
	
	/**
	* Inserts a new HoloAnimation
	* <p>
	* @param Id The HoloAnimation Id
	* @param T The HoloAnimation Ticks
	* @param C The HoloAnimation Content
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public void insertHoloAnimation(String Id, long T, List<String> C) {
		
		g.getAnimationManager().insertHoloAnimation(Id, T, C);
		
	}
	
	/**
	* Removes a HoloAnimation by HoloAnimation Id
	* <p>
	* @param Id The HoloAnimation Id
	* <p>
	* @since [1.0.0]
	* <p>
	* @author Gecolay
	*/
	public void removeHoloAnimation(String Id) {
		
		g.getAnimationManager().removeHoloAnimation(Id);
		
	}
	
}