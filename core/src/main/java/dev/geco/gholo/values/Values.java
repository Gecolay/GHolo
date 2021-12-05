package dev.geco.gholo.values;

import java.util.Arrays;
import java.util.List;

public class Values {
	
	private boolean papi = false;
	
	public boolean getPAPI() { return papi; }
	
	public void setPAPI(boolean PAPI) { papi = PAPI; }
	
	
	public static final String EMPTY_LINE_TEXT = "[]";
	
	public static final String ICON_LINE_TEXT = "ICON:";
	
	public static final String ENTITY_LINE_TEXT = "ENTITY:";
	
	public static List<String> CONDITION_TYPES = Arrays.asList("permission", "placeholder");
	
	public static final String IMAGES_PATH = "images";
	
	public static final String DATA_PATH = "data";
	
	public static final String LANG_PATH = "lang";
	
	public static final String DATA_FILETYP = ".data";
	
	public static final String YML_FILETYP = ".yml";
	
}