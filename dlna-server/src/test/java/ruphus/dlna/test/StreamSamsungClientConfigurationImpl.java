package ruphus.dlna.test;

import org.teleal.cling.transport.impl.StreamClientConfigurationImpl;

public class StreamSamsungClientConfigurationImpl extends StreamClientConfigurationImpl {

	@Override
	public String getUserAgentValue(int majorVersion, int minorVersion) {
		return "DLNADOC/1.50 SEC_HHP_[BD]D5500/1.0";
	}
	
}
