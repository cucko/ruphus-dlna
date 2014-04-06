package ruphus.media.dlna.model;

import java.util.ArrayList;
import java.util.List;

import org.teleal.cling.support.model.item.Movie;

public class SECMovie extends Movie {
	
	protected List<SEC> secs = new ArrayList<SEC>();

	public List<SEC> getSecs() {
		return secs;
	}
	
	public SECMovie setSecs(List<SEC> secs) {
		this.secs = secs;
		return this;
	}
	
	public SECMovie addSec(SEC sec) {
        getSecs().add(sec);
        return this;
    }
	
}
