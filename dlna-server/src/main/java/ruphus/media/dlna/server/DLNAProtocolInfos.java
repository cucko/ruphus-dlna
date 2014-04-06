package ruphus.media.dlna.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.teleal.cling.support.model.Protocol;
import org.teleal.cling.support.model.ProtocolInfo;
import org.teleal.cling.support.model.ProtocolInfos;

public class DLNAProtocolInfos extends ProtocolInfos { 
	
	private static final long serialVersionUID = 1L;
	private static DLNAProtocolInfos instance;
	
	private Map<String, ProtocolInfo> pim;
	
	private static final ProtocolInfo createProtocolInfo(String mimeType, String dlnaFlags) {
		return new ProtocolInfo(Protocol.HTTP_GET, ProtocolInfo.WILDCARD, mimeType, dlnaFlags);
	}
	
	public static final ProtocolInfo getProtocolInfo(String mimeType) throws IOException {
		ProtocolInfo pi = getInstance().pim.get(mimeType);
		return pi == null? createProtocolInfo(mimeType, ProtocolInfo.WILDCARD) : pi;
	}
	
	public static DLNAProtocolInfos getInstance() throws IOException {
		if (instance == null) instance = new DLNAProtocolInfos();
		return instance;
	}
	
	private DLNAProtocolInfos() throws IOException { 
		super();
		
		InputStream propIs = null;
		try {
			propIs = this.getClass().getResourceAsStream("/protocol-info.properties");

			Properties properties = new Properties();
			properties.load(propIs);
			
			pim = new TreeMap<String, ProtocolInfo>();
			for (Entry<Object, Object> entry : properties.entrySet()) {
				String mimeType = (String) entry.getKey();
				ProtocolInfo pi = createProtocolInfo(mimeType, (String) entry.getValue());
				pim.put(mimeType, pi);
				add(pi);
			}
		}
		finally {
			if (propIs != null) propIs.close();
		}
	}
}
