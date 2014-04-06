package ruphus.media.indexer;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UnknownFormatConversionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ruphus.media.indexer.db.dao.FolderDao;
import ruphus.media.indexer.db.dao.MediumDao;
import ruphus.media.indexer.db.model.Asset;
import ruphus.media.indexer.db.model.Folder;
import ruphus.media.indexer.db.model.Medium;

public abstract class MediaIndexer<M extends Medium> extends Thread {
//	static {
//		System.load("D:\\DATA\\Documents\\private\\workspace\\dlna-content-indexer\\src\\main\\resources\\MediaInfo.dll");
//	}
	
	private final static Logger log = Logger.getLogger(MediaIndexer.class.getName());
	
	protected Folder rootFolder;
	protected FolderDao folderDao;
	protected MediumDao<M> mediumDao;
	protected MediaInfoParser<M> infoParser;
	private boolean running;
	
	abstract protected void initRootFolder() throws Exception;
	abstract protected void inspectChild(File child, String parentId) throws Exception;
	
	public MediaIndexer(MediumDao<M> mediumDao, MediaInfoParser<M> infoParser) throws Exception {
		this.folderDao = new FolderDao();
		this.mediumDao = mediumDao;
		this.infoParser = infoParser;

		setName(getClass().getSimpleName());
		initRootFolder();
		
		Utils.checkFolder( rootFolder.getPath() );
		setPriority(Thread.MIN_PRIORITY);
		running = true;
	}
	
	public void exit() {
		running = false;
	}
	
	public void clear() throws Exception{
		mediumDao.deleteAll();
	}
	
	@Override
	public void run() {
		log.info(getClass().getSimpleName()+" started.");
		
		while (running) {
			try {
				checkSubassets( rootFolder.getId() );
				inspectFolder( new File( rootFolder.getPath() ), rootFolder.getId() );
				
				log.info(getClass().getSimpleName()+" finished indexing @ "+new Date());
				Thread.sleep( Configuration.getInstance().getRefreshEveryMillis() );
			} 
			catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage(), e);
				running = false;
			}
		}
		
		log.info(getClass().getSimpleName()+" exiting.");
	}
	
	protected static boolean requiresUpdate(Asset asset, File file) {
		return asset.getLastModified().getTime()/1000 < file.lastModified()/1000;
	}
	
	protected void checkSubassets(String parentId) throws Exception {
		List<Folder> subfolders = folderDao.retrieveSubassets(parentId);
		for (Folder subfolder : subfolders) {
			File folder = new File( subfolder.getPath() );
			if (!folder.exists()) {
				folderDao.deleteAsset( subfolder.getId() );
				log.finer("Removed "+subfolder.getPath());
			}
		}
	}
	
	private void inspectFolder(File folder, String folderId) throws Exception {
		Utils.checkFolder(folder.getAbsolutePath());

		for (File child : folder.listFiles()) {
			String filePath = child.getAbsolutePath();
			try {
				if (!running) break;
				else if (!child.canRead()) throw new IOException("File "+filePath+" is not readable");
				else if (child.isFile() && infoParser.isInspectable(child)) inspectChild(child, folderId);
				else if (child.isDirectory() && !child.getName().startsWith(".")) { 
					Folder subfolder = folderDao.retrieveAssetByPath(filePath);
					long lastModified = child.lastModified();
					
					if (subfolder == null) {
						log.finer("Found new folder: "+filePath);
						subfolder = new Folder();
						subfolder.setParentId(folderId);
						subfolder.setPath(filePath);
						subfolder.setTitle( child.getName() );
						subfolder.setLastModified( new Date(lastModified) );
						
						folderDao.storeAsset(subfolder);
						
						inspectFolder(child, subfolder.getId());
					}
					else if (requiresUpdate(subfolder, child)) {
						log.finer("Updating folder: "+filePath);
						checkSubassets(folderId);
						inspectFolder(child, subfolder.getId());
					}
				}

			}
			catch (UnknownFormatConversionException e) {
//				log.log(Level.WARNING, e.getMessage());
			}
			catch (Exception e) {
				log.log(Level.SEVERE, child.getAbsolutePath(), e);
			}
		}
	}

}
