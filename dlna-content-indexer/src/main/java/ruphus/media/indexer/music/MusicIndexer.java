package ruphus.media.indexer.music;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import ruphus.media.indexer.Configuration;
import ruphus.media.indexer.Constants;
import ruphus.media.indexer.MediaIndexer;
import ruphus.media.indexer.db.dao.SongDao;
import ruphus.media.indexer.db.model.Folder;
import ruphus.media.indexer.db.model.Song;

public class MusicIndexer extends MediaIndexer<Song>{
	private final static Logger log = Logger.getLogger(MusicIndexer.class.getName());
	
	@Override
	protected void initRootFolder() throws Exception {
		Folder musicRootFolder = folderDao.retrieveAssetById(Constants.MUSIC_FOLDER_ID);
		if (musicRootFolder == null) {
			musicRootFolder = new Folder();
			musicRootFolder.setId(Constants.MUSIC_FOLDER_ID);
			musicRootFolder.setParentId(Constants.ROOT_FOLDER_ID);
			musicRootFolder.setPath(Constants.MUSIC_FOLDER_ID);
			musicRootFolder.setTitle("Music");
			musicRootFolder.setLastModified(new Date());
			folderDao.storeAsset(musicRootFolder);
		}
		
		Folder artistsFolder = folderDao.retrieveAssetById(Constants.MUSIC_ARTISTS_FOLDER_ID);
		if (artistsFolder == null) {
			artistsFolder = new Folder();
			artistsFolder.setId(Constants.MUSIC_ARTISTS_FOLDER_ID);
			artistsFolder.setParentId(musicRootFolder.getId());
			artistsFolder.setPath(Constants.MUSIC_ARTISTS_FOLDER_ID);
			artistsFolder.setTitle("Artists");
			artistsFolder.setLastModified(new Date());
			folderDao.storeAsset(artistsFolder);
		}
		
		String path = Configuration.getInstance().getMusicPath();
		
		rootFolder = folderDao.retrieveAssetById(Constants.MUSIC_TREE_FOLDER_ID);
		if (rootFolder == null) {
			rootFolder = new Folder();
			rootFolder.setId(Constants.MUSIC_TREE_FOLDER_ID);
			rootFolder.setParentId(musicRootFolder.getId());
			rootFolder.setPath(path);
			rootFolder.setTitle("Folders");
			rootFolder.setLastModified( new Date() );
			folderDao.storeAsset(rootFolder);
		}
		else if (!path.equals( rootFolder.getPath() )){
			rootFolder.setPath(path);
			rootFolder.setLastModified(new Date());
			folderDao.updateAsset(rootFolder);
		}
	}
	
	public MusicIndexer() throws Exception {
		super(new SongDao(), new SongInfoParser());
	}
	
	@Override
	protected void checkSubassets(String parentId) throws Exception {
		super.checkSubassets(parentId);
		
		List<Song> subsongs = mediumDao.retrieveSubassets(parentId);
		for (Song subsong : subsongs) {
			File songFile = new File( subsong.getPath() );
			if (!songFile.exists()) {
				mediumDao.deleteAsset( subsong.getId() );
				log.finer("Removed "+subsong.getPath());
			}
		}
	}
	
	protected void inspectChild(File child, String parentId) throws Exception {
		String filePath = child.getAbsolutePath();
		
		Song song = mediumDao.retrieveAssetByPath(filePath);
		
		if (song == null) {
			log.finer("Found new song: "+filePath);
			song = infoParser.parse(child);
			song.setPath(filePath);
			song.setParentId(parentId);
			
			mediumDao.storeAsset(song);
		}
		else if (requiresUpdate(song, child)) {
			log.finer("Updating song: "+filePath);
			song = infoParser.parse(child);
			
			mediumDao.updateAsset(song);
		}
	}
}
