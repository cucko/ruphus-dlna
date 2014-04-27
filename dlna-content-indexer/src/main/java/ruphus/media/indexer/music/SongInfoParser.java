package ruphus.media.indexer.music;

import java.io.File;
import java.util.UnknownFormatConversionException;

import net.sourceforge.filebot.mediainfo.MediaInfo.StreamKind;
import ruphus.media.indexer.AudioInfoParser;
import ruphus.media.indexer.Utils;
import ruphus.media.indexer.db.model.Album;
import ruphus.media.indexer.db.model.Song;

public class SongInfoParser extends AudioInfoParser<Song> {
	
	@Override
	public boolean isInspectable(File file) throws UnknownFormatConversionException{
		return getMediaType(file) == StreamKind.Audio;
	}
	
	protected Song getSong(File file) throws UnknownFormatConversionException {
		Song song = null;
		
		String format = getGeneralInfo("Format");
		String profile = getAudioInfo(0, "Format_Profile");
		if ( 
			"MPEG Audio".equals(format) && "Layer 3".equals(profile) || 
			"Wave OGG".contains(format)
		) {
			song = new Song();
			fillMediumProperties(song, file);
			fillAudioProperties(song, file);
		}
		else throw new UnknownFormatConversionException(file.getName());
		
		return song;
	}
	
	@Override
	public Song parse(File file) throws Exception {
		try {
			info.open(file);

			Song song = getSong(file);
			
			String filePrefix = getGeneralInfo("FileName");
			
			Album album = new Album();
			album.setTitle( getGeneralInfo("Album") );
			album.setPerformer( getGeneralInfo("Album/Performer") );
			
			String released = getGeneralInfo("Original/Released_Date");
			if (released == null) released = getGeneralInfo("Recorded_Date");
			album.setReleased(released);
			
			String nTracks = getGeneralInfo("Track/Position_Total");
			album.setNTracks( nTracks );
			
			song.setAlbum(album);
			song.setPerformer( getGeneralInfo("Performer") );
			String track = getGeneralInfo("Track");
			song.setTitle( track == null ? filePrefix : track );
			
			String position = getGeneralInfo("Track/Position");
			if (position != null) {
				String albumPart = getGeneralInfo("Part/Position");
				if (albumPart == null) albumPart = "1";

				song.setPosition( Utils.getNTrack(albumPart, position) );
			}
			
			song.setDuration( getGeneralInfo("Duration/String3") );
			
			return song;
		}
		finally {
			if (info != null) info.close();
		}
	}
	
}
