package ruphus.media.indexer;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.sourceforge.filebot.mediainfo.MediaInfo.StreamKind;
import ruphus.media.indexer.music.SongInfoParser;


public class MediaInfoTest {
	
	public static void main(String[] args) throws Exception {
		
		String fileName = "Y:\\video\\film\\La.Dolce.Vita.1960.720p.BluRay.AC3.x264-vHD.mkv";
		
		SongInfoParser parser = new SongInfoParser();
		parser.info.open(new File(fileName));
		
		Map<StreamKind, List<Map<String, String>>> infos = parser.info.snapshot();
		for (StreamKind sk : infos.keySet()) {
			System.out.println("###### "+sk.name()+" ######");
			
			for (Map<String, String> props : infos.get(sk) ) {
				for (String name : props.keySet()) {
					System.out.println("\t"+name+": "+props.get(name));
				}
			}
			
			System.out.println();
			System.out.println();
		}

		parser.info.close();
	}
	
	
}
