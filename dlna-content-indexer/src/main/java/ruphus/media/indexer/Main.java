package ruphus.media.indexer;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import ruphus.media.indexer.db.dao.FolderDao;
import ruphus.media.indexer.db.model.Folder;
import ruphus.media.indexer.music.MusicIndexer;
import ruphus.media.indexer.picture.PictureIndexer;
import ruphus.media.indexer.video.VideoIndexer;

public class Main extends TCPListener{
	
	private final static Logger log = Logger.getLogger(Main.class.getName());
	private VideoIndexer videoIndexer;
	private MusicIndexer musicIndexer;
	private PictureIndexer pictureIndexer;
	
	public Main(String configPath, int port) throws Exception {
		super(port);
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
	}
	
	private void clearDatabase() throws Exception {
		log.info("Clearing database...");
		
		stopIndexing();
		
		new FolderDao().deleteAll();
		videoIndexer.clear();
		musicIndexer.clear();
		pictureIndexer.clear();
		
		log.info("Database is empty");
		
		startIndexing();
	}
	
	private void startIndexing() throws Exception{
		videoIndexer = new VideoIndexer();
		musicIndexer = new MusicIndexer();
		pictureIndexer = new PictureIndexer();
		
		videoIndexer.start();
		musicIndexer.start();
		pictureIndexer.start();
		
		log.info("Indexing started");
	}
	
	private void stopIndexing() throws InterruptedException{
		log.info("Stopping indexing...");
		musicIndexer.exit();
		videoIndexer.exit();
		pictureIndexer.exit();
		
		musicIndexer.join();
		videoIndexer.join();
		pictureIndexer.join();
	}
	
	public void exit() {
		try {
			stopIndexing();
    		
    		super.exit();
    		
    		log.info("Shutdown completed. Exiting.");
		} 
    	catch (Throwable e) {
    		log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public void run() {
		try {
			startIndexing();
	        
	        Runtime.getRuntime().addShutdownHook(new Thread() {
	            @Override
	            public void run() {
	            	log.info("Shutdown requested...");
	            	exit();
	            }
	        });
	        
	        super.run();
		}
        catch (Exception e) {
        	log.log(Level.SEVERE, e.getMessage(), e);
        }
	}
	
	@Override
	public String serveRequest(String request) {
		String response = null;
		try {
			Configuration conf = Configuration.getInstance();

			if (request == null);
			else if ("clearDatabase".equals(request)) {
				log.info("Indexer stop request received.");
				clearDatabase();
				
				response = "cleared";
			}
			else if ("stopIndexing".equals(request)) {
				log.info("Indexer stop request received.");
				exit();
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
			Main mainThread = new Main(args[0], Integer.parseInt(args[1]));
			mainThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
