package ruphus.media.indexer.video;

import java.io.File;
import java.util.UnknownFormatConversionException;
import java.util.logging.Logger;

import net.sourceforge.filebot.mediainfo.MediaInfo.StreamKind;
import ruphus.media.indexer.AudioInfoParser;
import ruphus.media.indexer.db.model.Video;

public class VideoInfoParser extends AudioInfoParser<Video> {

	private static final Logger log = Logger.getLogger(VideoInfoParser.class.getName());
		
	private String getVideoInfo(int index, String parameter) {
		return getInfo(StreamKind.Video, index, parameter);
	}
	
	@Override
	public boolean isInspectable(File file) throws UnknownFormatConversionException {
		return getOnlyMimeType(file).startsWith("video");
	}
	
	@Override
	public Video parse(File file) throws Exception {
		try {
			info.open(file);
			String filePrefix = getGeneralInfo("FileName");

			Video video = new Video();
			
			fillMediumProperties(video, file);
			fillAudioProperties(video, file);

			int videoStreams = info.streamCount(StreamKind.Video);
				
			if (videoStreams > 1) log.warning("Media item "+file.getAbsolutePath()+" has "+videoStreams+" video streams");
			else if (videoStreams == 1) {
				video.setTitle(filePrefix);
				video.setBitrate( Integer.parseInt(getVideoInfo(0,"BitRate")) );
				video.setColorDepth( Integer.parseInt(getVideoInfo(0, "BitDepth")) );
				video.setResolution( getVideoInfo(0, "Width") +"x"+ getVideoInfo(0, "Height") );
				video.setDuration( getGeneralInfo("Duration/String3") );
			}
			
			return video;
		}
		finally {
			if (info != null) info.close();
		}
	}

}
