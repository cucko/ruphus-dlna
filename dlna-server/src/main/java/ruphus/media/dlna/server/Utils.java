package ruphus.media.dlna.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.container.StorageFolder;
import org.teleal.cling.support.model.item.Movie;
import org.teleal.cling.support.model.item.MusicTrack;
import org.teleal.cling.support.model.item.Photo;

import ruphus.media.dlna.model.SEC;
import ruphus.media.dlna.model.SECMovie;
import ruphus.media.indexer.Constants;
import ruphus.media.indexer.db.model.Asset;
import ruphus.media.indexer.db.model.Audio;
import ruphus.media.indexer.db.model.Folder;
import ruphus.media.indexer.db.model.Medium;
import ruphus.media.indexer.db.model.Picture;
import ruphus.media.indexer.db.model.Song;
import ruphus.media.indexer.db.model.Video;

public class Utils {
	public static final String MEDIA_URL = "http://192.168.0.3/ruphus-dlna/stream.php";

	private static void fillAssetProperties(Asset asset, DIDLObject object) {
		object.setId( asset.getId() );
		object.setParentID( asset.getParentId() );
		object.setTitle( asset.getTitle() );
	}
	
	private static void fillMediumProperties(Medium medium, Res res) throws IOException, URISyntaxException {
		String ext = medium.getPath().substring( medium.getPath().lastIndexOf(".") );
		
		String mediumUrl = new StringBuffer()
			.append(MEDIA_URL).append("?id=").append(medium.getId()).append(ext)
			.toString()
		;
		
		res.setValue(mediumUrl);
		res.setImportUri( URI.create(mediumUrl) );
		res.setSize( medium.getSize() );
		res.setProtection("no");
		res.setProtocolInfo( DLNAProtocolInfos.getProtocolInfo(medium.getMimeType().replace("x-mkv", "x-matroska")) );
	}

	private static void fillAudioProperties(Audio audio, Res res) throws IOException, URISyntaxException {
		fillMediumProperties(audio, res);
		res.setDuration( audio.getDuration() );
		res.setNrAudioChannels( new Long(audio.getNrAudioChannels()) );
		res.setBitsPerSample( new Long(audio.getBitsPerSample()) );
		res.setSampleFrequency( new Long(audio.getSampleFrequency()) );
	}
	
	public static StorageFolder fromFolder(Folder folder) {
		StorageFolder sf = new StorageFolder();
		fillAssetProperties(folder, sf);
		return sf;
	}
	
	public static MusicTrack fromSong(Song song, boolean secRequest) throws IOException, URISyntaxException {
		Res songRes = new Res();
		fillAudioProperties(song, songRes);
		
		MusicTrack mt = new MusicTrack();
		mt.setAlbum( song.getAlbum().getTitle() );
		mt.setCreator( song.getPerformer() );
		mt.setOriginalTrackNumber(Integer.parseInt( song.getPosition() ) );
		mt.addResource(songRes);
		
		fillAssetProperties(song, mt);
		
		return mt;
	}
	
	public static Photo fromPicture(Picture picture, boolean secRequest) throws IOException, URISyntaxException {
		Res pictureRes = new Res();
		pictureRes.setResolution( picture.getResolution() );
		pictureRes.setColorDepth( new Long(picture.getColorDepth()) );
		fillMediumProperties(picture, pictureRes);
		
		Photo photo = new Photo();
		photo.addResource(pictureRes);
		
		fillAssetProperties(picture, photo);
		
		return photo;
	}
	
	public static Movie fromVideo(Video video, boolean secRequest) throws IOException, URISyntaxException {
		
		Res movieRes = new Res();
		movieRes.setBitrate( new Long(video.getBitrate()) );
		movieRes.setColorDepth( new Long(video.getColorDepth()) );
		movieRes.setResolution( video.getResolution() );
		
		fillAudioProperties(video, movieRes);
		
		Movie movie = secRequest ? new SECMovie() : new Movie();
		movie.addResource(movieRes);
		
		fillAssetProperties(video, movie);
		
		String srtName = video.getSubtitlesTitle();
		if (srtName != null) {
			String subsUrl = MEDIA_URL + srtName;
			if (secRequest){
				SEC secElem = new SEC(SEC.Element.CAPTION_INFO_EX, subsUrl, "srt");
				((SECMovie) movie).addSec(secElem);
			}
			else {
				Res subsRes = new Res();
				subsRes.setProtocolInfo( DLNAProtocolInfos.getProtocolInfo(Constants.SRT_MIME) );
				subsRes.setSize( new Long(video.getSubtitlesSize()) );
				subsRes.setValue(subsUrl);
				
				movie.addResource(subsRes);
			}
		}
		
		return movie;
	}
	
	public static int getPosition(Integer nTrack) {
		if (nTrack != null) { 
			nTrack = Integer.parseInt( String.valueOf(nTrack).substring(1) );
		}
		return nTrack;
	}

}
