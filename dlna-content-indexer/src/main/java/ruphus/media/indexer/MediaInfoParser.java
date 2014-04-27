package ruphus.media.indexer;


import java.io.File;
import java.util.Date;
import java.util.UnknownFormatConversionException;

import net.sourceforge.filebot.mediainfo.MediaInfo;
import net.sourceforge.filebot.mediainfo.MediaInfo.StreamKind;
import ruphus.media.indexer.db.model.Medium;

public abstract class MediaInfoParser<M extends Medium> {
	
	protected final MediaInfo info;
	
	abstract public boolean isInspectable(File file) throws Exception;
	
	public MediaInfoParser() {
		info = new MediaInfo();
	}
	
	abstract public M parse(File file) throws Exception;
	
	@Override
	public void finalize() throws Throwable {
		info.dispose();
	}
	
	protected String getInfo(StreamKind kind, int index, String parameter) {
		String value = info.get(kind, index, parameter);
		return value == null || value.trim().equals("") ? null : value;
	}
	
	protected String getGeneralInfo(String parameter) {
		return getInfo(StreamKind.General, 0, parameter);
	}
	
	protected StreamKind getMediaType(File file) throws UnknownFormatConversionException {
		try {
			info.open(file);
			StreamKind sk = null;
			
			if (info.streamCount(StreamKind.Video) > 0) sk = StreamKind.Video;
			else if (info.streamCount(StreamKind.Audio) > 0) sk = StreamKind.Audio;
			else if (info.streamCount(StreamKind.Image) > 0) sk = StreamKind.Image;
			else throw new UnknownFormatConversionException("Unparseable mime type for "+file.getAbsolutePath());
			
			return sk;
		}
		finally {
			if (info != null) info.close();
		}
	}
	
	protected void fillMediumProperties(Medium medium, File file) {
		String mimeType = getGeneralInfo("InternetMediaType");
		if (mimeType == null) {
			String format = getGeneralInfo("Format");
			
			if ("Matroska".equals(format)) mimeType = Constants.MKV_MIME;
			else throw new UnknownFormatConversionException("Unparseable format ("+format+") for "+file.getAbsolutePath());
		}
		medium.setMimeType(mimeType);
		medium.setSize( Long.parseLong(getGeneralInfo("FileSize")) );
		medium.setLastModified( new Date(file.lastModified()) );
	}

}
