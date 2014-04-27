package ruphus.media.indexer;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ruphus.media.indexer.db.dao.FolderDao;
import ruphus.media.indexer.db.dao.MediumDao;
import ruphus.media.indexer.db.model.Asset;
import ruphus.media.indexer.db.model.Folder;
import ruphus.media.indexer.db.model.Medium;

public abstract class MediaIndexer<M extends Medium> extends Thread {
	private final static Logger log = Logger.getLogger(MediaIndexer.class.getName());
	
	protected FolderDao folderDao = new FolderDao();
	protected Folder rootFolder;
	protected MediumDao<M> mediumDao;
	protected MediaInfoParser<M> infoParser;
	private boolean running;
	
	abstract protected void initRootFolder() throws Exception;
	abstract protected void inspectChild(File child, String parentId) throws Exception;
	
	protected static void checkFolder(String path) throws Exception {
		File folder = new File(path);
		if (!folder.exists()) throw new Exception("Path "+path+" not found");
		else if (!folder.isDirectory()) throw new Exception("Path "+path+" is not a directory");
		else if (!folder.canRead()) throw new Exception("Directory "+path+" is not readable");
	}
	
	public void exit() {
		running = false;
	}
	
	@Override
	public void run() {
		try {
			initRootFolder();
			running = true;
			
			log.info(getName()+" started.");
			
			while (running) {
				try {
					checkSubassets( rootFolder.getId() );
					inspectFolder( new File( rootFolder.getPath() ), rootFolder.getId() );
					
					log.info(getName()+" finished indexing @ "+new Date());
					Thread.sleep( Configuration.getInstance().getRefreshEveryMillis() );
				} 
				catch (Exception e) {
					log.log(Level.SEVERE, e.getMessage(), e);
					running = false;
				}
			}
			
			log.info(getName()+" exiting.");
		}
		catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
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
		checkFolder(folder.getAbsolutePath());

		for (File child : folder.listFiles()) {
			if (!running) break;
			
			String childPath = child.getAbsolutePath();
			try {
				if (!child.canRead()) throw new IOException("File "+childPath+" is not readable");
				else if (child.isFile() && infoParser.isInspectable(child)) inspectChild(child, folderId);
				else if (child.isDirectory() && !child.getName().startsWith(".")) { 
					Folder subfolder = folderDao.retrieveAssetByPath(childPath);
					long lastModified = child.lastModified();
					
					if (subfolder == null) {
						log.finer("Found new folder: "+childPath);
						subfolder = new Folder();
						subfolder.setParentId(folderId);
						subfolder.setPath(childPath);
						subfolder.setTitle( child.getName() );
						subfolder.setLastModified( new Date(lastModified) );
						
						folderDao.storeAsset(subfolder);
						
						inspectFolder(child, subfolder.getId());
					}
					else if (requiresUpdate(subfolder, child)) {
						log.finer("Updating folder: "+childPath);
						checkSubassets(folderId);
						inspectFolder(child, subfolder.getId());
					}
				}

			}
			catch (Exception e) {
				log.log(Level.SEVERE, child.getAbsolutePath(), e);
			}
		}
	}

}
