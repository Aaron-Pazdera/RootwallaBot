package statistics.hypergeo.data;

public class UnivariateData extends HyperGeoData {
	private int N, K, k1, k2, n;

	public UnivariateData(int N, int K, int k1, int k2, int n) {
		this.N = N;
		this.K = K;
		this.k1 = k1;
		this.k2 = k2;
		this.n = n;
	}

	public UnivariateData(int N, int K, int k, int n) {
		this.N = N;
		this.K = K;
		this.k1 = k;
		this.k2 = k;
		this.n = n;
	}

	public int getN() {
		return N;
	}

	public int getK() {
		return K;
	}

	public int getk1() {
		return k1;
	}

	public int getk2() {
		return k2;
	}

	public int getn() {
		return n;
	}
}
