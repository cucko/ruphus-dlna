package ruphus.media.dlna.server;

import java.util.logging.Logger;

import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;

public class DLNARegistryListener extends DefaultRegistryListener {
	
	private final static Logger log = Logger.getLogger(DLNARegistryListener.class.getName());

	public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
		String type = device.getType().getType();
    	if (type.equals("MediaRenderer") || type.equals("RemoteControlReceiver")) {
    		log.finer(
    			"New remote "+type+" available: "+device.getDisplayString()+
    			" (UUID: "+device.getIdentity().getUdn().getIdentifierString()+
    			") @ "+device.getIdentity().getDescriptorURL().getHost()
    		);
	        log.finest("\n\tSpecs: "+device.getIdentity().getDescriptorURL());
    	}
    }

    public void localDeviceAdded(Registry registry, LocalDevice device) {
       log.finer("Local device added: " + device.getDisplayString());
    }

}
