package ruphus.media.indexer.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ruphus.media.indexer.db.model.Album;
import ruphus.media.indexer.db.model.Song;

public class SongDao extends AudioDao<Song> {
	
	@Override
	protected Song getAssetInstance() {
		return new Song();
	}
	
	public List<Song> retrieveSubassets(String parentId) throws Exception {
		return retrieveSubassets(parentId, "position, title");
	}
	
	@Override
	protected void addInfos(Song song, Map<String, Object> columnValues) {
		super.addInfos(song, columnValues);
		
		if (song.getPerformer() != null) {
			columnValues.put("performer", song.getPerformer());
		}
		
		if (song.getPosition() != null) {
			columnValues.put("position", song.getPosition());
		}
		
		if (song.getAlbum().getTitle() != null) {
			columnValues.put("album_title", song.getAlbum().getTitle());
		}

		if (song.getAlbum().getNTracks() != null) {
			columnValues.put("album_tracks", song.getAlbum().getNTracks());
		}
		
		if (song.getAlbum().getPerformer() != null) {
			columnValues.put("album_performer", song.getAlbum().getPerformer());
		}
		
		if (song.getAlbum().getReleased() != null) {
			columnValues.put("album_released", song.getAlbum().getReleased());
		}
	}
	
	@Override
	protected void fillAssetProperties(Song song, ResultSet rs)	throws SQLException {
		super.fillAssetProperties(song, rs);
		
		Album album = new Album();
		album.setNTracks( rs.getString("album_tracks") );
		album.setPerformer( rs.getString("album_performer") );
		album.setReleased( rs.getString("album_released") );
		album.setTitle( rs.getString("album_title") );
		
		song.setPerformer( rs.getString("performer") );
		song.setPosition( rs.getString("position") );
		song.setAlbum(album);
	}

	public List<String> retrieveArtistList() throws Exception {
		ResultSet rs = null;
		try {

			String artistSql = new StringBuffer()
				.append("SELECT DISTINCT IF (album_performer IS NULL, performer, album_performer) AS artist")
			    .append(" FROM ").append(getTable())
			    .append(" WHERE album_performer IS NOT NULL OR performer IS NOT NULL")
			    .append(" ORDER BY REPLACE(artist, 'The ', '')")
				.toString()
			;
			
			rs = query(artistSql);
			
			List<String> artistList = new ArrayList<String>();
			while (rs.next()) {
				artistList.add( rs.getString("artist") );
			}
			
			return artistList;
		}
		finally {
			closeSession(rs);
		}
	}

	public List<Song> retrieveNoArtistSongs() throws Exception {
		ResultSet rs = null;
		try {

			String songSql = new StringBuffer()
				.append("SELECT * FROM ").append(getTable())
				.append(" WHERE album_performer IS NULL AND performer IS NULL")
				.append(" ORDER BY title")
				.toString()
			;
			
			rs = query(songSql);
			
			List<Song> songList = new ArrayList<Song>();
			while (rs.next()) {
				Song song = getAssetInstance();
				fillAssetProperties(song, rs);
				songList.add(song);
			}
			
			return songList;
		}
		finally {
			closeSession(rs);
		}
	}
	
	public List<Album> retrieveAlbumsByPerformer(String performer) throws Exception {
		ResultSet rs = null;
		try {

			String albumSql = new StringBuffer()
				.append("SELECT album_title, album_released, count(*) as children")
				.append(" FROM ").append( getTable() )
				.append(" WHERE ? in (album_performer, performer)")
				.append(" 	AND album_title IS NOT NULL")
				.append(" GROUP BY album_title, album_performer, performer")
				.append(" ORDER BY album_released, album_title")
				.toString()
			;
			
			rs = query(albumSql, performer);
			
			List<Album> albumList = new ArrayList<Album>();
			while (rs.next()) {
				Album album = new Album();
				album.setTitle( rs.getString("album_title") );
				album.setReleased( rs.getString("album_released") );
				album.setNTracks( rs.getString("children") );
				album.setPerformer(performer);
				
				albumList.add(album);
			}
			
			return albumList;
		}
		finally {
			closeSession(rs);
		}
	}

	public List<Song> retrieveNonAlbumTracks(String performer) throws Exception {
		ResultSet rs = null;
		try {

			String songSql = new StringBuffer()
				.append("SELECT * FROM ").append(getTable())
				.append(" WHERE album_title IS NULL")
				.append(" AND ? in (album_performer, performer)")
				.append(" ORDER BY title")
				.toString()
			;
			
			rs = query(songSql, performer);
			
			List<Song> songList = new ArrayList<Song>();
			while (rs.next()) {
				Song song = getAssetInstance();
				fillAssetProperties(song, rs);
				songList.add(song);
			}
			
			return songList;
		}
		finally {
			closeSession(rs);
		}
	}

	public List<Song> retrieveAlbumTracks(String albumTitle) throws Exception {
		ResultSet rs = null;
		try {
			
			String songSql = new StringBuffer()
			.append("SELECT * FROM ").append(getTable())
			.append(" WHERE album_title = ?")
			.append(" ORDER BY position, title")
			.toString()
			;
			
			rs = query(songSql, albumTitle);
			
			List<Song> songList = new ArrayList<Song>();
			while (rs.next()) {
				Song song = getAssetInstance();
				fillAssetProperties(song, rs);
				songList.add(song);
			}
			
			return songList;
		}
		finally {
			closeSession(rs);
		}
	}
	
}
