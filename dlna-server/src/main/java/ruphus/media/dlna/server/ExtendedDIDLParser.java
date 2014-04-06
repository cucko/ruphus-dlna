package ruphus.media.dlna.server;

import static org.teleal.cling.model.XMLUtil.appendNewElement;
import static org.teleal.cling.model.XMLUtil.appendNewElementIfNotNull;

import org.teleal.cling.support.contentdirectory.DIDLParser;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.DescMeta;
import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ruphus.media.dlna.model.SEC;
import ruphus.media.dlna.model.SECMovie;


/**
 * Modern Samsung TVs are able to play movie with subtitles from DLNA server, 
 * but subtitles require some non-standard extensions of DLNA protocol. 
 * What it means: using standard DLNA server you can watch movie, though without subtitles.
 * This patch adds some of Samsung proprietary extensions, just enough for subtitles to work.
 * What it does:
 * 1. 	When listing a folder, append non-standard <sec:CaptionInfoEx> tag to each video item. 
 * 		This tag servers only as a hint for Samsung client to check for subtitle download URL 
 * 		using HTTP HEAD method.
 * 2. 	Before playing movie, Samsung device issues HTTP HEAD request on video item. 
 * 		In FileRequestHandler::get_info method we look for subtitle file and return it's 
 * 		download URL in CaptionInfo.sec header.
 * 3.	The device retrieves subtitles from URL provided in CaptionInfo.sec header. This is 
 * 		handled thanks to existing blind request feature.
 */
public class ExtendedDIDLParser extends DIDLParser{
	protected boolean secResponse;
	
	public ExtendedDIDLParser(boolean secResponse) {
		super();
		this.secResponse = secResponse;
	}

	protected void generateRoot(DIDLContent content, Document descriptor, boolean nestedItems) {
        Element rootElement = descriptor.createElementNS(DIDLContent.NAMESPACE_URI, "DIDL-Lite");
        descriptor.appendChild(rootElement);

        rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:upnp", DIDLObject.Property.UPNP.NAMESPACE.URI);
        rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:dc", DIDLObject.Property.DC.NAMESPACE.URI);
        
        if (secResponse) {
        	rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:"+SEC.NAMESPACE.PREFIX, SEC.NAMESPACE.URI);
        }
        
        for (Container container : content.getContainers()) {
            if (container == null) continue;
            generateContainer(container, descriptor, rootElement, nestedItems);
        }

        for (Item item : content.getItems()) {
            if (item == null) continue;
            generateItem(item, descriptor, rootElement);
        }

        for (DescMeta<?> descMeta : content.getDescMetadata()) {
            if (descMeta == null) continue;
            generateDescMetadata(descMeta, descriptor, rootElement);
        }
        
    }
	
	protected void generateSec(SEC sec, Document descriptor, Element parent) {
        if (sec.getValue() == null) {
            throw new RuntimeException("Missing sec URI value" + sec);
        }
        
        Element secElement = appendNewElement(descriptor, parent, sec.getTagName(), sec.getValue());
        
        if (sec.getType() != null) secElement.setAttribute("sec:type", sec.getType());
	}
	
	 protected void generateItem(Item item, Document descriptor, Element parent) {

	        if (item.getTitle() == null) {
	            throw new RuntimeException("Missing 'dc:title' element for item: " + item.getId());
	        }
	        if (item.getClazz() == null) {
	            throw new RuntimeException("Missing 'upnp:class' element for item: " + item.getId());
	        }

	        Element itemElement = appendNewElement(descriptor, parent, "item");

	        if (item.getId() == null)
	            throw new NullPointerException("Missing id on item: " + item);
	        itemElement.setAttribute("id", item.getId());

	        if (item.getParentID() == null)
	            throw new NullPointerException("Missing parent id on item: " + item);
	        itemElement.setAttribute("parentID", item.getParentID());

	        if (item.getRefID() != null)
	            itemElement.setAttribute("refID", item.getRefID());
	        itemElement.setAttribute("restricted", Boolean.toString(item.isRestricted()));

	        appendNewElementIfNotNull(
	                descriptor,
	                itemElement,
	                "dc:title",
	                item.getTitle(),
	                DIDLObject.Property.DC.NAMESPACE.URI
	        );

	        appendNewElementIfNotNull(
	                descriptor,
	                itemElement,
	                "dc:creator",
	                item.getCreator(),
	                DIDLObject.Property.DC.NAMESPACE.URI
	        );

	        appendNewElementIfNotNull(
	                descriptor,
	                itemElement,
	                "upnp:writeStatus",
	                item.getWriteStatus(),
	                DIDLObject.Property.UPNP.NAMESPACE.URI
	        );

	        appendClass(descriptor, itemElement, item.getClazz(), "upnp:class", false);

	        appendProperties(descriptor, itemElement, item, "upnp", DIDLObject.Property.UPNP.NAMESPACE.class, DIDLObject.Property.UPNP.NAMESPACE.URI);
	        appendProperties(descriptor, itemElement, item, "dc", DIDLObject.Property.DC.NAMESPACE.class, DIDLObject.Property.DC.NAMESPACE.URI);

	        for (Res resource : item.getResources()) {
	            if (resource == null) continue;
	            generateResource(resource, descriptor, itemElement);
	        }

	        for (DescMeta<?> descMeta : item.getDescMetadata()) {
	            if (descMeta == null) continue;
	            generateDescMetadata(descMeta, descriptor, itemElement);
	        }
	        
	        if (secResponse && item instanceof SECMovie) {
	        	SECMovie movieItem = (SECMovie)item;
	        	
	        	for (SEC sec : movieItem.getSecs()) {
	        		generateSec(sec, descriptor, itemElement);
	            }
	        }
	    }
	
}
