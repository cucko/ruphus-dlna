package ruphus.media.indexer;

import java.io.File;

import ruphus.media.indexer.music.SongInfoParser;

public class LibTest {

	public static void main(String[] args) {
		SongInfoParser parser = new SongInfoParser();
		parser.info.open(new File("/share/Multimedia/mp3/Offlaga Disco Pax - Onomastica.mp3"));
		System.out.println( parser.getGeneralInfo("Album") );
		parser.info.close();
	}

}
