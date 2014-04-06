package ruphus.dlna.test;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class SamsungClient {
	
//	private final static Logger log = Logger.getLogger(SamsungClient.class.getName());
	
	public static void main(String[] args) throws InterruptedException, ClientProtocolException, IOException {
		
//		STAllHeader header = new STAllHeader();
//        header.setValue(NotificationSubtype.ALL);
//        
//        final UpnpService upnpService = new UpnpServiceImpl(
//    		new UpnpServiceConfiguration()
//    	);
//		
//		upnpService.getControlPoint().search(header);
//		
//		Registry registry = upnpService.getRegistry();
//		
//		Thread.sleep(3000);
//		
//		RemoteDevice device = registry.getRemoteDevice(new UDN("55076f6e-6b79-4d65-64eb-00089bc9666e"), false);
//		log.info(device.getDisplayString());
//		
//		RemoteService service = device.findService(new ServiceType("schemas-upnp-org", "ContentDirectory"));
//		
//		ActionCallback complexBrowseAction = new Browse(service, "0$3$33$254$377R205066", BrowseFlag.METADATA) {
//			
//			@Override
//			public void received(ActionInvocation actionInvocation,	DIDLContent didl) {
//				UpnpHeaders headers = ReceivingAction.getExtraResponseHeaders();
//				log.info("ok");
////			        Item item1 = didl.getItems().get(0);
////			        assertEquals(
////			                item1.getTitle(),
////			                "All Secrets Known"
////			        );
////			        assertEquals(
////			                item1.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM.class),
////			                "Black Gives Way To Blue"
////			        );
////			        assertEquals(
////			                item1.getFirstResource().getProtocolInfo().getContentFormatMimeType().toString(),
////			                "audio/mpeg"
////			        );
////			        assertEquals(
////			                item1.getFirstResource().getValue(),
////			                "http://10.0.0.1/files/101.mp3"
////			        );
//
//			}
//			
//
//		    public void updateStatus(Status status) {
//		        // Called before and after loading the DIDL content
//		    }
//
//		    @Override
//		    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
//		    	log.info(defaultMsg);
//		    }
//		};
//		upnpService.getControlPoint().execute(complexBrowseAction);
		
		HttpHead httpget = new HttpHead("http://192.168.0.3:9000/disk/DLNA-PNAVI-OP01-FLAGS01700000/O0$3$27I205066.avi");
//		HttpGet httpget = new HttpGet("http://192.168.0.3:9000/disk/DLNA-PNAVI-OP01-FLAGS01700000/O0$3$27I205066.avi");
		
		httpget.addHeader("User-Agent", "DLNADOC/1.50 SEC_HHP_[BD]D5500/1.0");
		
//		httpget.addHeader("getcaptioninfo.sec", "1");
		httpget.addHeader("getcontentfeatures.dlna.org", "1");
//		httpget.addHeader("transfermode.dlna.org", "1");
//		httpget.addHeader("getMediaInfo.sec", "1");
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		httpclient.execute(httpget, new ResponseHandler<String>() {

			@Override
			public String handleResponse(HttpResponse response)	throws ClientProtocolException, IOException {
				System.out.println("Response headers:");
				for (Header header : response.getAllHeaders()) {
					String headerName = header.getName();
					StringBuffer print = new StringBuffer(headerName).append(" : ");
					
					for (Header headerValue : response.getHeaders(headerName)) {
						print.append(headerValue.getValue()).append(" | ");
					}
					
					System.out.println(print.toString());
				}
				System.out.println(response.getEntity());
				
				return null;
			}
		});
	}
}
