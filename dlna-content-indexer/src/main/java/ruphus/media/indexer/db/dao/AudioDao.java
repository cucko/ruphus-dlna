package ruphus.media.indexer.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import ruphus.media.indexer.db.model.Audio;

public abstract class AudioDao<A extends Audio> extends MediumDao<A> {

	@Override
	protected void addInfos(A audio, Map<String, Object> columnValues) {
		super.addInfos(audio, columnValues);
		
		columnValues.put("duration", audio.getDuration());
		columnValues.put("bits_per_sample", audio.getBitsPerSample());
		columnValues.put("sample_frequency", audio.getSampleFrequency());
		columnValues.put("nr_audio_channels", audio.getNrAudioChannels());
	}

	@Override
	protected void fillAssetProperties(A audio, ResultSet rs) throws SQLException {
		super.fillAssetProperties(audio, rs);
		
		audio.setDuration( rs.getString("duration") );
		audio.setBitsPerSample( rs.getInt("bits_per_sample") );
		audio.setSampleFrequency( rs.getInt("sample_frequency") );
		audio.setNrAudioChannels( rs.getInt("nr_audio_channels") );
	}
}
