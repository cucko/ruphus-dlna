package ruphus.media.dlna.server;

import org.teleal.cling.DefaultUpnpServiceConfiguration;
import org.teleal.cling.transport.impl.StreamClientConfigurationImpl;
import org.teleal.cling.transport.impl.StreamClientImpl;
import org.teleal.cling.transport.impl.StreamServerConfigurationImpl;
import org.teleal.cling.transport.impl.StreamServerImpl;
//import org.teleal.cling.transport.impl.apache.StreamClientConfigurationImpl;
//import org.teleal.cling.transport.impl.apache.StreamClientImpl;
//import org.teleal.cling.transport.impl.apache.StreamServerConfigurationImpl;
//import org.teleal.cling.transport.impl.apache.StreamServerImpl;
import org.teleal.cling.transport.spi.NetworkAddressFactory;
import org.teleal.cling.transport.spi.StreamClient;
import org.teleal.cling.transport.spi.StreamServer;



public class DLNAServiceConfiguration extends DefaultUpnpServiceConfiguration {

	private static final int STREAM_LISTEN_PORT = 43210;
	
	public DLNAServiceConfiguration() {
		super(STREAM_LISTEN_PORT);
	}

	@Override
    public StreamClient<?> createStreamClient() {
		StreamClientConfigurationImpl conf = new StreamClientConfigurationImpl();
		conf.setConnectionTimeoutSeconds(60);
		conf.setDataReadTimeoutSeconds(60);
        return new StreamClientImpl(conf);
    }

    @Override
    public StreamServer<?> createStreamServer(NetworkAddressFactory networkAddressFactory) {
        return new StreamServerImpl(
            new StreamServerConfigurationImpl( networkAddressFactory.getStreamListenPort() )
        );
    }
}
