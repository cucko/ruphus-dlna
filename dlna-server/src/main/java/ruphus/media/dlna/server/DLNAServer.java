package ruphus.media.dlna.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.binding.LocalServiceBindingException;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.DeviceIdentity;
import org.teleal.cling.model.meta.Icon;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.ManufacturerDetails;
import org.teleal.cling.model.meta.ModelDetails;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.NotificationSubtype;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.support.connectionmanager.ConnectionManagerService;


public class DLNAServer implements Runnable {
	
	private final static Logger log = Logger.getLogger(DLNAServer.class.getName());
	private static boolean RUNNING = true;
	
	private UpnpService upnpService;
	
	public void stop() {
    	STAllHeader header = new STAllHeader();
        header.setValue(NotificationSubtype.BYEBYE);
    	upnpService.getControlPoint().search(header);
        upnpService.shutdown();
        
        RUNNING = false;
	}
	
    public void run() {
        try {
        	upnpService = new UpnpServiceImpl(
        		new DLNAServiceConfiguration(),
        		new DLNARegistryListener()
        	);

            upnpService.getRegistry().addDevice( createDevice() );
            
            STAllHeader header = new STAllHeader();
            header.setValue(NotificationSubtype.DISCOVER);
            upnpService.getControlPoint().search(header);
            
            header.setValue(NotificationSubtype.ALIVE);
            
            while (RUNNING) {
            	Thread.sleep(30000);
            	upnpService.getControlPoint().search(header);
            }

        } 
        catch (Exception ex) {
        	log.log(Level.SEVERE, "Exception occured: ", ex);
            System.exit(1);
        }
    }
    
    @SuppressWarnings("unchecked")
	private static LocalDevice createDevice() throws ValidationException, LocalServiceBindingException, IOException, URISyntaxException {

	    DeviceIdentity identity = new DeviceIdentity( UDN.uniqueSystemIdentifier("RuphusDLNAServer") );
	
	    DeviceType type = new UDADeviceType("MediaServer", 1);
	    DeviceDetails details = new DeviceDetails(
            "Ruphus DLNA Server",
            new ManufacturerDetails("Ruphus"),
            new ModelDetails("DLNA Server", "so far...", "1.0")
        );
	
	        
        LocalService<ConnectionManagerService> connService = new AnnotationLocalServiceBinder().read(ConnectionManagerService.class);
        connService.setManager( 
    		new DefaultServiceManager<ConnectionManagerService>(connService, null) {
    	        @Override
    	        protected ConnectionManagerService createServiceInstance() throws Exception {
    	            return new ConnectionManagerService(DLNAProtocolInfos.getInstance(), null);
    	        }
    	    }
        );
        
        LocalService<MediaContentDirectory> movieService = new AnnotationLocalServiceBinder().read(MediaContentDirectory.class);
        movieService.setManager(new DefaultServiceManager<MediaContentDirectory>(movieService, MediaContentDirectory.class));
        
        Icon icon = new Icon("image/png", 48, 48, 32, URI.create("icon.png"), DLNAServer.class.getResourceAsStream("/icon.png") );
        
        return new LocalDevice(identity, type, details, icon, new LocalService[] {connService, movieService});
    }
}
