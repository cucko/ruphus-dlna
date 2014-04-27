package ruphus.media.indexer;

import java.io.File;
import java.util.logging.Logger;

import net.sourceforge.filebot.mediainfo.MediaInfo.StreamKind;
import ruphus.media.indexer.db.model.Audio;

public abstract class AudioInfoParser<A extends Audio> extends MediaInfoParser<A> {
	
	private static final Logger log = Logger.getLogger(AudioInfoParser.class.getName());
	
	protected String getAudioInfo(int index, String parameter) {
		return getInfo(StreamKind.Audio, index, parameter);
	}
	
	protected void fillAudioProperties(Audio audio, File file) {
		int audioStreams = info.streamCount(StreamKind.Audio);
		
		if (audioStreams > 1) {
			log.warning("Media item "+file.getAbsolutePath()+" has "+audioStreams+" audio streams");
		}
		
		audio.setBitsPerSample( getAudioInfo(0, "BitRate") );
		audio.setSampleFrequency( getAudioInfo(0, "SamplingRate") );
		audio.setNrAudioChannels( getAudioInfo(0, "Channel(s)") );
	}
}
