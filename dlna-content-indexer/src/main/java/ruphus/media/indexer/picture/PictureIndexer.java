package ruphus.media.indexer.picture;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import ruphus.media.indexer.Configuration;
import ruphus.media.indexer.Constants;
import ruphus.media.indexer.MediaIndexer;
import ruphus.media.indexer.db.dao.PictureDao;
import ruphus.media.indexer.db.model.Folder;
import ruphus.media.indexer.db.model.Picture;

public class PictureIndexer extends MediaIndexer<Picture> {
	
	private final static Logger log = Logger.getLogger(PictureIndexer.class.getName());

	public PictureIndexer() throws Exception {
		mediumDao = new PictureDao();
		infoParser = new PictureInfoParser();
		
		setName("DLNA Indexer - Pictures");
	}
	
	@Override
	protected void initRootFolder() throws Exception {
		String path = Configuration.getInstance().getPicturesPath();
		checkFolder(path);
		
		rootFolder = folderDao.retrieveAssetById(Constants.PICTURES_FOLDER_ID);
		if (rootFolder == null) {
			rootFolder = new Folder();
			rootFolder.setId(Constants.PICTURES_FOLDER_ID);
			rootFolder.setParentId(Constants.ROOT_FOLDER_ID);
			rootFolder.setPath(path);
			rootFolder.setTitle("Pictures");
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
		
		List<Picture> subpictures = mediumDao.retrieveSubassets(parentId);
		for (Picture subpicture : subpictures) {
			File pictureFile = new File( subpicture.getPath() );
			if (!pictureFile.exists()) {
				mediumDao.deleteAsset( subpicture.getId() );
				log.finer("Removed "+subpicture.getPath());
			}
		}
	}
	
	protected void inspectChild(File child, String parentId) throws Exception {
		String filePath = child.getAbsolutePath();
		Picture picture = mediumDao.retrieveAssetByPath(filePath);
		
		if (picture == null) {
			log.finer("Found new picture: "+child.getAbsolutePath());
			picture = infoParser.parse(child);
			picture.setPath(filePath);
			picture.setParentId(parentId);
			
			mediumDao.storeAsset(picture);
		}
	}
	
}
