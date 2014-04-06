package ruphus.media.indexer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

	public static String PATH;

	private static Configuration instance;
	public static final String MUSIC_PATH="music.path";
	public static final String VIDEOS_PATH="videos.path";
	public static final String PICTURES_PATH="pictures.path";
	public static final String REINDEX_SECONDS="reindex.seconds";
	
	private Properties properties;
	private long refreshEveryMillis;
	private String musicPath;
	private String videosPath;
	private String picturesPath;
	private String dbUrl;
	private String dbUser;
	private String dbPassword;
	
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	
	public String getDbPassword() {
		return dbPassword;
	}
	
	public String getDbUrl() {
		return dbUrl;
	}
	
	public String getDbUser() {
		return dbUser;
	}
	
	public String getPicturesPath() {
		return picturesPath;
	}
	
	public void setPicturesPath(String picturesPath) throws IOException {
		this.picturesPath = picturesPath;
		storeProperty(PICTURES_PATH, picturesPath);
	}
	
	public void setVideosPath(String videosPath) throws IOException {
		this.videosPath = videosPath;
		storeProperty(VIDEOS_PATH, picturesPath);
	}
	
	public void setRefreshEveryMillis(long refreshEveryMillis) throws IOException {
		this.refreshEveryMillis = refreshEveryMillis;
		storeProperty(REINDEX_SECONDS, picturesPath);
	}
	
	public void setMusicPath(String musicPath) throws IOException {
		this.musicPath = musicPath;
		storeProperty(MUSIC_PATH, picturesPath);
	}

	public String getVideosPath() {
		return videosPath;
	}
	
	public String getMusicPath() {
		return musicPath;
	}
	
	public long getRefreshEveryMillis() {
		return refreshEveryMillis;
	}
	
	private void storeProperty(String name, String value) throws IOException{
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(PATH);
			properties.put(name, value);
			properties.store(os, null);
		}
		finally {
			if (os != null) os.close();
		}
	}
	
	private Configuration() throws IOException {
		FileInputStream is = null;
		try {
			is = new FileInputStream(PATH);
			properties = new Properties();
			properties.load(is);
			
			this.musicPath = properties.getProperty(MUSIC_PATH);
			this.picturesPath = properties.getProperty(PICTURES_PATH);
			this.videosPath = properties.getProperty(VIDEOS_PATH);
			this.refreshEveryMillis = Integer.parseInt( properties.getProperty(REINDEX_SECONDS) ) * 1000;
			this.dbPassword = properties.getProperty("db.password");
			this.dbUrl = properties.getProperty("db.url");
			this.dbUser = properties.getProperty("db.user");
		}
		finally {
			if (is != null) is.close();
		}
	}
	
	public static Configuration getInstance() throws FileNotFoundException, IOException {
		if (instance == null) instance = new Configuration();
		return instance;
	}
	
}
