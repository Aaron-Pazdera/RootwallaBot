package statistics.hypergeo;

public class Group {
	private String name;
	private int K;
	private int k1;
	private int k2;

	public Group(int K, int k1, int k2) {
		if (K < k1) {
			throw new IllegalArgumentException("The minimum number of successes required cannot be greater than the number which exist in the deck.");
		}
		this.name = "";
		this.K = K;
		this.k1 = k1;
		this.k2 = k2;
	}
	
	public Group(String name, int K, int k1, int k2) {
		if (K < k1) {
			throw new IllegalArgumentException("The minimum number of successes required cannot be greater than the number which exist in the deck.");
		}
		this.name = name;
		this.K = K;
		this.k1 = k1;
		this.k2 = k2;
	}

	public String getName() {
		return name;
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
}
