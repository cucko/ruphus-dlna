package ruphus.dlna.test;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.teleal.cling.model.message.control.IncomingActionRequestMessage;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.protocol.sync.ReceivingAction;
import org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.teleal.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.teleal.cling.support.contentdirectory.ContentDirectoryException;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.BrowseResult;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.DIDLObject.Property.DC;
import org.teleal.cling.support.model.DIDLObject.Property.UPNP;
import org.teleal.cling.support.model.Protocol;
import org.teleal.cling.support.model.ProtocolInfo;
import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.SortCriterion;
import org.teleal.cling.support.model.container.StorageFolder;
import org.teleal.cling.support.model.item.Movie;

import ruphus.media.dlna.model.SEC;
import ruphus.media.dlna.model.SECMovie;
import ruphus.media.dlna.server.DLNAProtocolInfos;
import ruphus.media.dlna.server.ExtendedDIDLParser;
import ruphus.media.indexer.Constants;

public class MovieContentDirectory extends AbstractContentDirectoryService {
	
	private final static Logger log = Logger.getLogger(MovieContentDirectory.class.getName());
	private final static String MEDIA_URL = "http://192.168.0.10:8088/dlna/stream?fileName=";
	private final static String MEDIA_CREATOR = "ruphus";
	
	
	@Override
	public BrowseResult browse(
		String objectID, BrowseFlag browseFlag, String filter, 
		long firstResult, long maxResults, SortCriterion[] orderby
    ) throws ContentDirectoryException {
		
		try {
			
			boolean secRequest = false;

			IncomingActionRequestMessage message = ReceivingAction.getRequestMessage();
			String userAgent = message.getHeaders().get(UpnpHeader.Type.USER_AGENT.getHttpName()).get(0);
			
			if (userAgent.contains(SEC.USER_AGENT)) {
				log.finer("Samsung device request");
				secRequest = true;
			}
			
			if (log.isLoggable(Level.FINER)) {
				for (String header : message.getHeaders().keySet()) {
					for (String value : message.getHeaders().get(header)) {
						log.finer(header+" : "+value);
					}
				}
			}
			
			log.fine("browse "+browseFlag.name()+" with objectId "+objectID);
			log.finer(filter);
			
			DIDLContent didl = new DIDLContent();
			
			if (objectID.equals(Constants.ROOT_FOLDER_ID) && browseFlag == BrowseFlag.METADATA) {

				StorageFolder videoFolder = new StorageFolder();
				videoFolder.setId("1");
				videoFolder.setChildCount(1);
				videoFolder.setParentID(objectID);
				videoFolder.setTitle("Videos");
				videoFolder.setSearchable(true);
				
				didl.addContainer(videoFolder);
			}
			else {
				String movieName = "Breaking_Bad.mkv";
				String srtName = "Breaking_Bad.srt";

				Res movieRes = new Res();
				movieRes.setSize(364580304l);
				movieRes.setDuration("0:42:45.811");
				movieRes.setBitsPerSample(128000l);
				movieRes.setBitrate(996000l);
				movieRes.setColorDepth(8l);
				movieRes.setProtection("no");
				movieRes.setProtocolInfo(new ProtocolInfo(Protocol.HTTP_GET, ProtocolInfo.WILDCARD, "video/x-mkv", "*"));
				movieRes.setValue(MEDIA_URL + movieName);
				movieRes.setResolution("640x360");
				movieRes.setSampleFrequency(48000l);
				movieRes.setNrAudioChannels(2l);
				movieRes.setImportUri( new URI(MEDIA_URL+movieName) );
				
				Movie movie = secRequest ? new SECMovie() : new Movie();
				movie.setId("1");
				movie.setParentID(Constants.ROOT_FOLDER_ID);
				movie.setCreator(MEDIA_CREATOR);
				movie.setTitle(movieName);
				movie.addProperty(new DC.DATE("2014-01-01T00:00:00"));
				movie.addProperty(new UPNP.GENRE("Sconosciuto"));
				movie.addProperty(new UPNP.ALBUM("movie"));
				movie.addResource(movieRes);
				
				if (secRequest){
					SECMovie secMovie = (SECMovie) movie;
					secMovie.addSec( new SEC(SEC.Element.CAPTION_INFO_EX, MEDIA_URL+srtName, "srt") );
				}
				else {
					Res subsRes = new Res(DLNAProtocolInfos.getProtocolInfo("text/srt"), 37271l, MEDIA_URL + srtName);
					movie.addResource(subsRes);
				}
				
				didl.addItem(movie);
			}
			
			String outXml = new ExtendedDIDLParser(secRequest).generate(didl);
			
			log.finer("Response -> " +outXml);
			
			return new BrowseResult(outXml, 1, 1);
		}
		catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
            throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS, e.getMessage());
        }
	}

}
