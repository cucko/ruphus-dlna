package ruphus.media.indexer;

import java.io.File;

import ruphus.media.indexer.music.SongInfoParser;


public class MediaInfoTest {
	
	public static void main(String[] args) throws Exception {
		
//		String fileName = "D:\\DATA\\Documents\\Downloads\\Breaking_Bad.mkv";
//		String fileName = "Y:\\photos\\edward_hopper02.jpg";
//		String fileName = "D:\\DATA\\Documents\\Downloads\\Nums_5dot1_24_48000.wav";
		String fileName = "D:\\DATA\\Documents\\Downloads\\dlna\\mp3\\08 - Get Lucky.mp3";
//		String fileName = "\\\\QNAS\\Multimedia\\mp3\\MEDLEY MOMENTI MATRIMONIO.mp3";
//		String fileName = "D:\\DATA\\Documents\\Downloads\\dlna\\mp3\\Peter Gabriel\\Secret World Live (CD 2)\\03-Secret World.mp3";
		
//		Configuration.getInstance().setVideosPath("D:\\DATA\\Documents\\Downloads\\dlna\\videos");
//		Configuration.getInstance().setMusicPath("D:\\DATA\\Documents\\Downloads\\dlna\\mp3");
//		Configuration.getInstance().setPicturesPath("D:\\DATA\\Documents\\Downloads\\dlna\\photos");
		
		SongInfoParser parser = new SongInfoParser();
		parser.info.open(new File(fileName));
		System.out.println( parser.getGeneralInfo("InternetMediaType") );
		
//		parser.parse(new File(fileName));
		parser.info.close();
	}
	
	
}
