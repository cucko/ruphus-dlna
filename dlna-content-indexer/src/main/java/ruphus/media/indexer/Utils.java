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

	public static String getNTrack(String albumPart, String position) {
		return String.valueOf( Integer.parseInt(albumPart) * 100 + Integer.parseInt(position) );
	}
	
	public static String getAssetRelativePath(String rootFolderPath, File child) throws ParseException {
		MessageFormat mf = new MessageFormat(rootFolderPath+"{0}");
		
		return (String) mf.parse(child.getAbsolutePath())[0];
	}
	
	public static String getFilePrefix(File file) {
		return file.getName().substring(0, file.getName().lastIndexOf("."));
	}
	
}
