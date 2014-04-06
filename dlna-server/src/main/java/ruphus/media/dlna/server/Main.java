package ruphus.media.dlna.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import ruphus.media.indexer.Configuration;

public class Main {
	private final static Logger log = Logger.getLogger(Main.class.getName());
	
	public static void main(String[] args) throws Exception {
		Configuration.PATH = args[0];
		final DLNAServer dlnaServer = new DLNAServer();
		final Thread dlnaServerThread = new Thread(dlnaServer, "DLNA Server");
		dlnaServerThread.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
            	log.info("Shutdown requested...");
            	
            	try {
            		dlnaServer.stop();
            		log.info("Shutdown completed. Exiting.");
				} 
            	catch (Throwable e) {
            		log.log(Level.SEVERE, e.getMessage(), e);
				}
            }
        });
        
		dlnaServerThread.join();
    }

}
