package ruphus.media.indexer.picture;


import java.io.File;
import java.util.UnknownFormatConversionException;

import net.sourceforge.filebot.mediainfo.MediaInfo.StreamKind;
import ruphus.media.indexer.MediaInfoParser;
import ruphus.media.indexer.db.model.Picture;

public class PictureInfoParser extends MediaInfoParser<Picture> {

	private String getImageInfo(String parameter) {
		return getInfo(StreamKind.Image, 0, parameter);
	}
	
	@Override
	public boolean isInspectable(File file) throws UnknownFormatConversionException {
		return getMediaType(file) == StreamKind.Image;
	}

	@Override
	public Picture parse(File file){
		try {
			info.open(file);

			Picture picture = new Picture();
			picture.setTitle( getGeneralInfo("FileName") );
			fillMediumProperties(picture, file);

			if (info.streamCount(StreamKind.Image) > 0) {
				picture.setResolution( getImageInfo("Width") +"x"+ getImageInfo("Height") );
				
				String bitDepth = getImageInfo("BitDepth");
				if (bitDepth != null) picture.setColorDepth(bitDepth);
			}
			
			return picture;
		}
		finally {
			if (info != null) info.close();
		}
	}

}
