package ruphus.media.dlna.model;

import org.teleal.cling.support.model.DIDLObject.Property;

public class SEC {
	
	public static final String USER_AGENT="SEC_HHP";
	
    public interface NAMESPACE extends Property.NAMESPACE {
        public static final String URI = "http://www.sec.co.kr/";
        public static final String PREFIX = "sec";
    }
    
    public static enum Element {
    	CAPTION_INFO("CaptionInfo"),
    	CAPTION_INFO_EX("CaptionInfoEx"),	//external subtitles?
    	DCM_INFO("dcmInfo"),				//playback resuming CREATIONDATE=0,FOLDER=The.Change-Up.UNRATED.1080p.BluRay.x264-BLOW,BM=201
    	META_FILE_INFO("MetaFileInfo");
    	
    	private String suffix;
    	
    	private Element(String suffix) { this.suffix = suffix; }
    }

    protected String value;
    protected Element element;
    protected String type;
    
    public String getValue() {
		return value;
	}
    
    public String getType() {
		return type;
	}
    
    public SEC(Element element, String value) {
    	this(element, value, null);
	}
    
    public SEC(Element element, String value, String type) {
    	this.element = element;
    	this.value = value;
    	this.type = type;
	}
    
    public String getTagName() { 
    	return NAMESPACE.PREFIX +":"+ element.suffix; 
    }
}
