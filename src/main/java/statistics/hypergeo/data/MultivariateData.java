package statistics.hypergeo.data;

import java.util.List;

import statistics.hypergeo.Group;

public class MultivariateData extends HyperGeoData {
	private int n;
	private List<Group> glist;
	
	public MultivariateData(List<Group> glist, int n) {
		this.glist = glist;
		this.n = n;
	}

	public List<Group> getGlist() {
		return glist;
	}

	public int getN() {
		return n;
	}
}
