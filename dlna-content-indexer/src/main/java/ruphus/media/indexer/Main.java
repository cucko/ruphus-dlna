package ruphus.media.indexer;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import ruphus.media.indexer.db.dao.FolderDao;
import ruphus.media.indexer.db.dao.VideoDao;
import ruphus.media.indexer.db.model.Folder;
import ruphus.media.indexer.video.VideoIndexer;

public class Main extends TCPListener {
	
	private final static Logger log = Logger.getLogger(Main.class.getName());
	private VideoIndexer videoIndexer;
//	private MusicIndexer musicIndexer;
//	private PictureIndexer pictureIndexer;
	private boolean indexing;
	
	public Main(String configPath, int port) throws Exception {
		super(port);
		setName("DLNA Indexer - MAIN");
		
		Configuration.PATH = configPath;
		initDatabase();
	}

	private static void initDatabase() throws Exception {
		log.info("Initializing database...");
		
		FolderDao dao = new FolderDao();
		Folder rootFolder = dao.retrieveAssetById(Constants.ROOT_FOLDER_ID);
		if (rootFolder == null) {
			rootFolder = new Folder();
			rootFolder.setId(Constants.ROOT_FOLDER_ID);
			rootFolder.setParentId("-1");
			rootFolder.setPath(Constants.ROOT_FOLDER_ID);
			rootFolder.setTitle("ROOT FOLDER");
			rootFolder.setLastModified(new Date());
			
			dao.storeAsset(rootFolder);
		}
		
		log.info("Database ready");
	}
	
	private void startIndexing() throws Exception{
		videoIndexer = new VideoIndexer();
//		musicIndexer = new MusicIndexer();
//		pictureIndexer = new PictureIndexer();
		
		videoIndexer.start();
//		musicIndexer.start();
//		pictureIndexer.start();
		
		indexing = true;
		log.info("Indexing started");
	}
	
	private void stopIndexing() throws InterruptedException{
		log.info("Stopping indexing...");
		
		videoIndexer.exit();
//		musicIndexer.exit();
//		pictureIndexer.exit();
		
		videoIndexer.join();
//		musicIndexer.join();
//		pictureIndexer.join();
		
		indexing = false;
		log.info("Indexing now stopped");
	}
	
	private void clearDatabase() throws Exception {
		log.info("Clearing database...");
		
		boolean wasIndexing = indexing;
		if (indexing) stopIndexing();
		
		new FolderDao().deleteAll();
		new VideoDao().deleteAll();
		
		log.info("Database is now empty");
		
		if (wasIndexing) startIndexing();
	}
	
	public void exit() {
		try {
			stopIndexing();
    		
			super.exit();
    		
    		join();
    		log.info("Shutdown completed. Exiting.");
		} 
    	catch (Throwable e) {
    		log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@Override
	public String serveRequest(String request) {
		String response = null;
		try {
			Configuration conf = Configuration.getInstance();

			if ("getStatus".equals(request)){
				if (indexing) response = "started";
				else response = "stopped";
			}
			else if ("startIndexing".equals(request)) {
				log.info("Indexer start request received.");
				startIndexing();
				
				response = "started";
			}
			else if ("clearDatabase".equals(request)) {
				log.info("Database clear request received.");
				clearDatabase();
				
				if (indexing) response = "started";
				else response = "stopped";
			}
			else if ("stopIndexing".equals(request)) {
				log.info("Indexer stop request received.");
				stopIndexing();
				
				response = "stopped";
			}
			else if (request.contains(Configuration.MUSIC_PATH)) {
				log.info("MUSIC_PATH request received.");
				if (request.contains("=")){
					conf.setMusicPath( request.split("=")[1] );
					response = "set";
				}
				else response = conf.getMusicPath();
			}
			else if (request.contains(Configuration.PICTURES_PATH)) {
				log.info("PICTURES_PATH request received.");
				if (request.contains("=")){
					conf.setPicturesPath( request.split("=")[1] );
					response = "set";
				}
				else response = conf.getPicturesPath();
			}
			else if (request.contains(Configuration.VIDEOS_PATH)) {
				log.info("VIDEOS_PATH request received.");
				if (request.contains("=")){
					conf.setVideosPath( request.split("=")[1] );
					response = "set";
				}
				else response = conf.getVideosPath();
			}
			else if (request.contains(Configuration.REINDEX_SECONDS)) {
				log.info("REINDEX_SECONDS request received.");
				if (request.contains("=")){
					conf.setRefreshEveryMillis( Long.parseLong(request.split("=")[1]) );
					response = "set";
				}
				else response = String.valueOf( conf.getRefreshEveryMillis() );
			}
			
		}
		catch (Exception e){
			response = "error: "+e.getMessage();
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		
		return response;
	}

	public static void main(String[] args) {
		try {
			final Main mainThread = new Main(args[0], Integer.parseInt(args[1]));
		
			Runtime.getRuntime().addShutdownHook(new Thread() {
	            @Override
	            public void run() {
	            	log.info("Shutdown requested...");
	            	mainThread.exit();
	            }
	        });
			
			mainThread.start();
		}
		catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
