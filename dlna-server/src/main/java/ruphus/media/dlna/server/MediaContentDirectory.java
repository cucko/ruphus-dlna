package ruphus.media.dlna.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.teleal.cling.model.message.UpnpHeaders;
import org.teleal.cling.model.message.control.IncomingActionRequestMessage;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.protocol.sync.ReceivingAction;
import org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.teleal.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.teleal.cling.support.contentdirectory.ContentDirectoryException;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.BrowseResult;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.PersonWithRole;
import org.teleal.cling.support.model.SortCriterion;
import org.teleal.cling.support.model.container.MusicAlbum;
import org.teleal.cling.support.model.container.MusicArtist;
import org.teleal.cling.support.model.container.StorageFolder;
import org.teleal.cling.support.model.item.MusicTrack;

import ruphus.media.dlna.model.SEC;
import ruphus.media.indexer.Constants;
import ruphus.media.indexer.db.dao.FolderDao;
import ruphus.media.indexer.db.dao.PictureDao;
import ruphus.media.indexer.db.dao.SongDao;
import ruphus.media.indexer.db.dao.VideoDao;
import ruphus.media.indexer.db.model.Album;
import ruphus.media.indexer.db.model.Folder;
import ruphus.media.indexer.db.model.Picture;
import ruphus.media.indexer.db.model.Song;
import ruphus.media.indexer.db.model.Video;

public class MediaContentDirectory extends AbstractContentDirectoryService {
	
	private final static Logger log = Logger.getLogger(MediaContentDirectory.class.getName());
	private final static String KIND_SEPARATOR = "$";
	private VideoDao videoDao;
	private FolderDao folderDao;
	private SongDao songDao;
	private PictureDao pictureDao;
	
	
	
	public MediaContentDirectory() throws Exception {
		videoDao = new VideoDao();
		folderDao = new FolderDao();
		songDao = new SongDao();
		pictureDao = new PictureDao();
	}
	
	private static String getHeaderView(UpnpHeaders headers) {
		StringBuffer headerStr = new StringBuffer();
		
		for (String header : headers.keySet()) {
			for (String value : headers.get(header)) {
				headerStr.append(header).append("=").append(value).append(" ");
			}
		}
		
		return headerStr.toString();
	}
	
	private static String getParamsView(
		String objectID, BrowseFlag browseFlag, String filter, 
		long firstResult, long maxResults, SortCriterion[] orderby) {
		
		return new StringBuffer()
			.append("[browsing mode: ").append(browseFlag.name()).append("]")
			.append("[objectId: ").append(objectID).append("]")
			.append("[firstResult: ").append(firstResult).append("]")
			.append("[maxResults: ").append(maxResults).append("]")
			.append("[filter: ").append(filter).append("]")
			.toString()
		;
	}
	
	@Override
	public BrowseResult browse(
		String objectID, BrowseFlag browseFlag, String filter, 
		long firstResult, long maxResults, SortCriterion[] orderby
    ) throws ContentDirectoryException {
		
		IncomingActionRequestMessage message = ReceivingAction.getRequestMessage();
		try {
			String userAgent = message.getHeaders().getFirstHeader(UpnpHeader.Type.USER_AGENT.getHttpName());
			
			boolean secRequest = userAgent.contains(SEC.USER_AGENT);
			
			if (log.isLoggable(Level.FINER)) {
				
				if (log.isLoggable(Level.FINEST)) {
					log.finest( getHeaderView(message.getHeaders()) );
				}
				
				log.finer( getParamsView(objectID, browseFlag, filter, firstResult, maxResults, orderby) );
			}
			
			DIDLContent didl = new DIDLContent();
			
			if (browseFlag == BrowseFlag.METADATA) browseMetadata(didl, objectID, secRequest);
			else if (Constants.ROOT_FOLDER_ID.equals(objectID)) browseRoot(didl);
			else if (Constants.MUSIC_FOLDER_ID.equals(objectID)) browseMusicFolder(didl);
			
			else if (Constants.MUSIC_ARTISTS_FOLDER_ID.equals(objectID)) {
				for (String artist : songDao.retrieveArtistList()) {
					MusicArtist artistSF = new MusicArtist();
//					StorageFolder artistSF = new StorageFolder();
					artistSF.setId(Constants.MUSIC_ARTIST_FOLDER_ID +KIND_SEPARATOR+ artist);
					artistSF.setParentID(objectID);
					artistSF.setTitle(artist);
					didl.addContainer(artistSF);
				}
				
				for (Song song : songDao.retrieveNoArtistSongs()) {
					MusicTrack mt = Utils.fromSong(song, secRequest);
					mt.setParentID(objectID);
					didl.addItem(mt);
				}
			}
			else {
				String kindPath = null;
				if (
					Constants.VIDEO_FOLDER_ID.equals(objectID) ||
					Constants.PICTURES_FOLDER_ID.equals(objectID) ||
					Constants.MUSIC_TREE_FOLDER_ID.equals(objectID)
				) {
					kindPath = objectID;
				}
				else {
					String[] kindTokens = objectID.split("\\"+KIND_SEPARATOR);
					kindPath = kindTokens[0];
					objectID = kindTokens[1];
				}
				
					
				if (kindPath.equals(Constants.MUSIC_ARTIST_FOLDER_ID) ) {
					for (Album album: songDao.retrieveAlbumsByPerformer(objectID)) {
						MusicAlbum albumFolder = new MusicAlbum();
						
//						StorageFolder albumFolder = new StorageFolder();
						albumFolder.setId( Constants.MUSIC_ALBUM_FOLDER_ID + KIND_SEPARATOR + album.getTitle() );
						albumFolder.setParentID(kindPath +KIND_SEPARATOR+ objectID);
						albumFolder.setTitle( album.getTitle() );
						albumFolder.setArtists(new PersonWithRole[]{new PersonWithRole(album.getPerformer())});
						albumFolder.setDate( album.getReleased() );
						albumFolder.setChildCount( Integer.parseInt( album.getNTracks() ) );
						
						didl.addContainer(albumFolder);
					}
					
					for (Song song : songDao.retrieveNonAlbumTracks(objectID)) {
						MusicTrack mt = Utils.fromSong(song, secRequest);
						mt.setParentID(kindPath +KIND_SEPARATOR+ objectID);
						didl.addItem(mt);
					}
				}
				else if (kindPath.equals(Constants.MUSIC_ALBUM_FOLDER_ID) ) {
					for (Song song : songDao.retrieveAlbumTracks(objectID)) {
						MusicTrack mt = new MusicTrack();
						mt = Utils.fromSong(song, secRequest);
						mt.setParentID(kindPath +KIND_SEPARATOR+ objectID);
						didl.addItem(mt);
					}
				}
				else {
					for (Folder folder : folderDao.retrieveSubassets(objectID)) {
						StorageFolder sf = Utils.fromFolder(folder);
						sf.setId(kindPath +KIND_SEPARATOR+ sf.getId());
						didl.addContainer(sf);
					}
					
					if (kindPath.equals(Constants.MUSIC_TREE_FOLDER_ID)) {
						for (Song song : songDao.retrieveSubassets(objectID)) {
							didl.addItem( Utils.fromSong(song, secRequest) );
						}
					}
					else if (kindPath.equals(Constants.VIDEO_FOLDER_ID)) {
						for (Video video : videoDao.retrieveSubassets(objectID)) {
							didl.addItem( Utils.fromVideo(video, secRequest) );
						}
					}
					else if (kindPath.equals(Constants.PICTURES_FOLDER_ID)) {
						for (Picture picture : pictureDao.retrieveSubassets(objectID)) {
							didl.addItem( Utils.fromPicture(picture, secRequest) );
						}
					}
				}
				
				
			}
			
			String outXml = new ExtendedDIDLParser(secRequest).generate(didl);
			log.finer("Response -> " +outXml);
			
			int count = didl.getContainers().size() + didl.getItems().size();

			return new BrowseResult(outXml, count, count);
		}
		catch (Exception e) {
			
			String messageBuf = new StringBuffer("Error managing following message:")
				.append("\n=============================================================")
				.append("\nAction: ").append(message.getAction().getName())
				.append("\nHeaders: ")
				.append(getHeaderView(message.getHeaders()))
				.append("\nParams: ")
				.append(getParamsView(objectID, browseFlag, filter, firstResult, maxResults, orderby))
				.append("\nBody:\n")
				.append(message.getBodyString())
				.append("\n\n=============================================================")
				.toString()
			;
			
			log.log(Level.SEVERE, messageBuf, e);
            throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS, e.getMessage());
        }
	}

	private void browseMusicFolder(DIDLContent didl) throws Exception {
		Folder artistsFolder = folderDao.retrieveAssetById(Constants.MUSIC_ARTISTS_FOLDER_ID);
		StorageFolder artistsSF = Utils.fromFolder(artistsFolder);
		didl.addContainer(artistsSF);
		
		Folder musicTreeFolder = folderDao.retrieveAssetById(Constants.MUSIC_TREE_FOLDER_ID);
		StorageFolder musicTreeSF = Utils.fromFolder(musicTreeFolder);
		didl.addContainer(musicTreeSF);
	}

	private void browseRoot(DIDLContent didl) throws Exception {
		Folder videoFolder = folderDao.retrieveAssetById(Constants.VIDEO_FOLDER_ID);
		StorageFolder videoSF = Utils.fromFolder(videoFolder);
		didl.addContainer(videoSF);
		
		Folder musicFolder = folderDao.retrieveAssetById(Constants.MUSIC_FOLDER_ID);
		StorageFolder musicSF = Utils.fromFolder(musicFolder);
		didl.addContainer(musicSF);
		
		Folder picturesFolder = folderDao.retrieveAssetById(Constants.PICTURES_FOLDER_ID);
		StorageFolder picturesSF = Utils.fromFolder(picturesFolder);
		didl.addContainer(picturesSF);
	}

	private void browseMetadata(DIDLContent didl, String objectID, boolean secRequest) throws Exception {
		if (objectID.contains("$")) objectID = objectID.split("\\$")[1];
		
		Folder folder = folderDao.retrieveAssetById(objectID);
		if (folder != null) didl.addContainer( Utils.fromFolder(folder) );
		else {
			Video video = videoDao.retrieveAssetById(objectID);
			if (video != null) didl.addItem( Utils.fromVideo(video, secRequest) );
			else {
				Song song = songDao.retrieveAssetById(objectID);
				if (song != null) didl.addItem( Utils.fromSong(song, secRequest) );
				else {
					Picture picture = pictureDao.retrieveAssetById(objectID);
					if (picture != null) didl.addItem( Utils.fromPicture(picture, secRequest) );
				}
			}
		}
	}
	
}
