package ruphus.media.indexer.video;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import ruphus.media.indexer.Configuration;
import ruphus.media.indexer.Constants;
import ruphus.media.indexer.Utils;
import ruphus.media.indexer.MediaIndexer;
import ruphus.media.indexer.db.dao.VideoDao;
import ruphus.media.indexer.db.model.Folder;
import ruphus.media.indexer.db.model.Video;

public class VideoIndexer extends MediaIndexer<Video> {
	
	private final static Logger log = Logger.getLogger(VideoIndexer.class.getName());
	
	public VideoIndexer() throws Exception {
		super(new VideoDao(), new VideoInfoParser());
		setName("DLNA Indexer - Video");
	}
	
	@Override
	protected void initRootFolder() throws Exception {
		String path = Configuration.getInstance().getVideosPath();
				
		rootFolder = folderDao.retrieveAssetById(Constants.VIDEO_FOLDER_ID);
		if (rootFolder == null) {
			rootFolder = new Folder();
			rootFolder.setId(Constants.VIDEO_FOLDER_ID);
			rootFolder.setParentId(Constants.ROOT_FOLDER_ID);
			rootFolder.setPath(path);
			rootFolder.setTitle("Videos");
			rootFolder.setLastModified(new Date());
			
			folderDao.storeAsset(rootFolder);
		}
		else if (!path.equals( rootFolder.getPath() )){
			rootFolder.setPath(path);
			rootFolder.setLastModified(new Date());
			folderDao.updateAsset(rootFolder);
		}
	}
	
	@Override
	protected void checkSubassets(String parentId) throws Exception {
		super.checkSubassets(parentId);
		
		List<Video> subvideos = mediumDao.retrieveSubassets(parentId);
		for (Video subvideo : subvideos) {
			File videoFile = new File( subvideo.getPath() );
			if (!videoFile.exists()) {
				mediumDao.deleteAsset( subvideo.getId() );
				log.finer("Removed "+subvideo.getPath());
			}
		}
	}
	
	protected void inspectChild(File child, String parentId) throws Exception {
		String filePath = child.getAbsolutePath();
		Video video = mediumDao.retrieveAssetByPath(filePath);

		if (video == null) {
			log.finer("Found new video: "+child.getAbsolutePath());
			
			video = infoParser.parse(child);
			video.setPath(filePath);
			video.setParentId(parentId);
			
			lookForSubtitles(video, child);
			
			mediumDao.storeAsset(video);
		}
		
		if ( video.getSubtitlesTitle() == null && lookForSubtitles(video, child) ){
			mediumDao.updateAsset(video);
		}
		
	}

	private boolean lookForSubtitles(Video video, File child) {
		String filePrefix = Utils.getFilePrefix(child);

		File subs = new File(child.getParent(), filePrefix+".srt");
		if (subs.exists()) {
//			child.renameTo( new File(child.getParent(), filePrefix+".mkv") );
//			video.setPath( relativePath.substring(0, relativePath.indexOf(filePrefix)) + child.getName() );
			video.setSubtitlesTitle(subs.getName());
			video.setSubtitlesSize( new Long(subs.length()).intValue() );
			video.setMimeType(Constants.MKV_MIME);
			
			return true;
		}
		
		else return false;
	}
	
}
