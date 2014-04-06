package ruphus.media.indexer;

import java.io.File;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class Utils {
	private static final NumberFormat nTrackFormatter = NumberFormat.getInstance();
	
	static {
		nTrackFormatter.setMinimumIntegerDigits(2);
	}

	public static int getNTrack(String albumPart, String position) {
		return Integer.parseInt(albumPart) * 100 + Integer.parseInt(position);
	}
	
	public static void checkFolder(String path) throws Exception {
		File folder = new File(path);
		if (!folder.exists()) throw new Exception("Path "+path+" not found");
		else if (!folder.isDirectory()) throw new Exception("Path "+path+" is not a directory");
		else if (!folder.canRead()) throw new Exception("Directory "+path+" is not readable");
	}
	
	public static String getAssetRelativePath(String rootFolderPath, File child) throws ParseException {
		MessageFormat mf = new MessageFormat(rootFolderPath+"{0}");
		
		return (String) mf.parse(child.getAbsolutePath())[0];
	}
	
	public static String getFilePrefix(File file) {
		return file.getName().substring(0, file.getName().lastIndexOf("."));
	}
	
}
