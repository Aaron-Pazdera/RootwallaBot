package statistics.hypergeo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.math3.util.CombinatoricsUtils;

import statistics.hypergeo.Group;

public class MultiHyperGeo {

	/**
	 * Protected Constructor for static method class
	 */
	protected MultiHyperGeo() {
	}

	/********************************/
	/* Probability Functions        */
	/********************************/

	public static double probability(List<Group> glist, int n) throws IllegalArgumentException {
		return probability(glist, n, false);
	}

	public static double probability(List<Group> glist, int n, boolean multithreaded) throws IllegalArgumentException {
		verifyProbabilityArgs(glist, n);

		int N = 0; // The total number of cards
		List<Integer> K = new ArrayList<>(); // Number of balls of each group
		List<Integer> k1 = new ArrayList<>(); // Minimum successes of balls of each group
		List<Integer> k2 = new ArrayList<>(); // Maximum successes of balls of each group

		// Unpack data, determine population size. The unpacked data is now safe.
		// Note that for the purposes of multithreading, these are not declared final,
		// but will never actually change, only be referenced.
		for (Group g : glist) {
			N += g.getK();
			K.add(g.getK());
			k1.add(g.getk1());
			k2.add(g.getk2());
		}

		List<List<Integer>> possibilities = findPossibilities(k1, k2, n);
		double numerator = 0;
		if (multithreaded) {
			// Multithread calculating the chances for all the possibilities
			final int callsPerThread = 50;
			class ProbThread implements Callable<Double> {

				private final int startIndex;

				ProbThread(int startIndex) {
					this.startIndex = startIndex;
				}

				@Override
				public Double call() throws Exception {
					double partialNumerator = 0;

					// Do all the PMFs allocated to this thread
					// Do the pmf to start at, up to the pmf to end at (End of allocated calls, or
					// end of all calls)
					for (int i = startIndex; (i < startIndex + callsPerThread) && (i < possibilities.size()); i++) {
						partialNumerator += pmfNum(K, possibilities.get(i));
					}
					return partialNumerator;
				}

			}

			// Make the correct number of threads
			List<Callable<Double>> tasklist = new ArrayList<>();
			for (int i = 0; i < possibilities.size(); i += callsPerThread) {
				tasklist.add(new ProbThread(i));
			}

			// Execute all threads
			List<Future<Double>> flist = null;
			try {
				ExecutorService threadPool = Executors.newFixedThreadPool(10);
				flist = threadPool.invokeAll(tasklist);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Total up the answers
			for (int i = 0; i < flist.size(); i++) {
				try {
					numerator += flist.get(i).get();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			for (int i = 0; i < possibilities.size(); i++) {
				numerator += pmfNum(K, possibilities.get(i));
			}
		}
		return numerator / CombinatoricsUtils.binomialCoefficientDouble(N, n);
	}

	public static double pmf(int N, List<Integer> K, int n, List<Integer> k) throws IllegalArgumentException {
		verifyPMFArgs(N, K, n, k);

		double numerator = 1;
		int size = K.size();

		for (int i = 0; i < size; i++) {
			numerator *= CombinatoricsUtils.binomialCoefficientDouble(K.get(i), k.get(i));
		}
		return numerator / CombinatoricsUtils.binomialCoefficientDouble(N, n);
	}

	/********************************/
	/* Probabilities with Mulligans */
	/********************************/

	public static double probabilityOnVancouverMullToX(List<Group> glist, int X) throws IllegalArgumentException {
		return calculateProbabilityOnGeneralMullToX(glist, X, true, 7);
	}

	public static double probabilityOnVancouverMullToXWithFree(List<Group> glist, int X)
			throws IllegalArgumentException {
		return 1 - ((1 - probabilityOnVancouverMullToX(glist, X)) * (1 - probability(glist, 7)));
	}
	
	public static double probabilityOnParisMullToX(List<Group> glist, int X) throws IllegalArgumentException {
		return calculateProbabilityOnGeneralMullToX(glist, X, false, 7);
	}

	public static double probabilityOnParisMullToXWithFree(List<Group> glist, int X)
			throws IllegalArgumentException {
		return 1 - ((1 - probabilityOnParisMullToX(glist, X)) * (1 - probability(glist, 7)));
	}
	
	public static double probabilityOnLondonMullToX(List<Group> glist, int X)
			throws IllegalArgumentException {
		return calculateProbabilityOnLondonMullToX(glist, X, 7);
	}
	
	public static double probabilityOnLondonMullToXWithFree(List<Group> glist, int X)
			throws IllegalArgumentException {
		return 1 - ((1 - probabilityOnLondonMullToX(glist, X)) * (1 - probability(glist, 7)));
	}
	
	
	/********************************/
	/* Utility Functions            */
	/********************************/

	protected static double calculateProbabilityOnGeneralMullToX(List<Group> glist, int X, boolean withScries,
			int startingHandSize) throws IllegalArgumentException {

		// This method will also throw the same IllegalArgumentExceptions that
		// probability() will throw to verify its arguments, since this method calls
		// that one. However, there is one new variable to check or correct.

		// We're going to rebuild a new glist that will be safe to be use with this
		// value of x.


		// If X is less than the lowest minimum required successes in the group list,
		// then that means we can actually only mulligan down to k1 cards in hand.
		for (Group g : glist) {
			int k1 = g.getk1();
			if (X < k1) {
				X = k1;
			}
		}

		// If X is more than the starting hand size, then this is wrong.
		if (X > startingHandSize) {
			throw new IllegalArgumentException(
					"X cannot be greater than the starting hand size. You cannot mull down to more cards than you start with.");
		}

		// If it's exactly the starting hand size, then they're not willing to mulligan,
		// and they're looking at exactly that many cards, so just call the other
		// function.
		if (X == startingHandSize) {
			return probability(glist, startingHandSize, false);
		}

		// In these "At least one" Combinatorics problems, the probability of at least
		// one event occurring is the compliment of the event not occurring, which
		// itself
		// is the compliment of the probability of the event happening. So, instead of
		// tracking the probability of success through the problem, we track the
		// probability of failure.

		double probabilityOfFailure = 1;

		// The initial opening hand
		probabilityOfFailure *= 1 - probability(glist, startingHandSize, false);

		// Mulligans (with/without scries)
		for (int mullHandSize = startingHandSize - 1; mullHandSize >= X; mullHandSize--) {
			// If there are scries, then add one to how many cards we're looking at.
			probabilityOfFailure *= 1 - probability(glist, withScries ? mullHandSize + 1 : mullHandSize, false);
		}
		return 1 - probabilityOfFailure;
	}

	protected static double calculateProbabilityOnLondonMullToX(List<Group> glist, int X,
			int startingHandSize) throws IllegalArgumentException {
		// This method is much like calculateProbabilityOnGeneralMullToX(), but with the
		// London Mulligan.

		// Same as that function, this one will also throw the same
		// IllegalArgumentExceptions that probability() will throw to verify its
		// arguments, since this method calls that one. However, there is one new
		// variable to check or correct.


		// If X is less than the lowest minimum required successes in the group list,
		// then that means we can actually only mulligan down to k1 cards in hand.
		for (Group g : glist) {
			int k1 = g.getk1();
			if (X < k1) {
				X = k1;
			}
		}

		// If X is more than the starting hand size, then this is wrong.
		if (X > startingHandSize) {
			throw new IllegalArgumentException(
					"X cannot be greater than the starting hand size. You cannot mull down to more cards than you start with.");
		}

		// If it's exactly the starting hand size, then they're not willing to mulligan,
		// and they're looking at exactly that many cards, so just call the other
		// function.
		if (X == startingHandSize) {
			return probability(glist, startingHandSize);
		}

		// In these "At least one" Combinatorics problems, the probability of at least
		// one event occurring is the compliment of the event not occurring, which
		// itself is the compliment of the probability of the event happening. So,
		// instead of tracking the probability of success through the problem, we track
		// the probability of failure.

		// The initial opening hand, then all mulligans
		double probabilityOfFailure = 1;
		for (int i = X; i <= startingHandSize; i++) {
			probabilityOfFailure *= 1 - probability(glist, startingHandSize);
		}

		return 1 - probabilityOfFailure;
	}
	
	protected static List<List<Integer>> findPossibilities(List<Integer> k1, List<Integer> k2, int n) {

		// Generate every possible combination between k1 and k2
		List<List<Integer>> prev = new ArrayList<>(), newList = new ArrayList<>();
		for (int i = k1.get(0); i <= k2.get(0); i++) {
			ArrayList<Integer> list = new ArrayList<>(1);
			prev.add(list);
			list.add(i);
		}

		for (int i = 1; i < k1.size(); i++) {
			for (int j = k1.get(i); j <= k2.get(i); j++) {
				for (List<Integer> l : prev) {
					ArrayList<Integer> list = new ArrayList<>(l);
					newList.add(list);
					list.add(j);
				}
			}
			prev = newList;
			newList = new ArrayList<>();
		}

		// Pare them down to the ones we care about which sum to n
		List<List<Integer>> possibilities = new ArrayList<>();
		prev.forEach((p) -> {
			if (p.stream().mapToInt(i -> i).sum() == n) {
				possibilities.add(p);
			}

		});

		return possibilities;
	}

	protected static double pmfNum(List<Integer> K, List<Integer> k) {
		// Only called by probability, which should already check everything necessary.
		// Therefore, there are no IllegalArgumentExceptions required here.
		double numerator = 1;
		int size = K.size();
		for (int i = 0; i < size; i++) {
			numerator *= CombinatoricsUtils.binomialCoefficientDouble(K.get(i), k.get(i));
		}
		return numerator;
	}

	/********************************/
	/* Stats Functions              */
	/********************************/

	public static double mean(List<Group> glist, Group meanOf, int n) {
		verifyDistributionArgs(glist, meanOf, n);

		int N = 0;
		for (int i = 0; i < glist.size(); i++) {
			N += glist.get(i).getK();
		}
		return n * ((double) meanOf.getK() / (double) N);
	}

	public static double variance(List<Group> glist, Group meanOf, int n) {
		verifyDistributionArgs(glist, meanOf, n);

		int N = 0;
		for (int i = 0; i < glist.size(); i++) {
			N += glist.get(i).getK();
		}
		final double p1 = ((double) meanOf.getK() / (double) N);
		final double p2 = (1 - p1);
		final double p3 = n * ((double) (N - n) / (double) (N - 1));
		return p1 * p2 * p3;
	}

	public static double standardDeviation(List<Group> glist, Group meanOf, int n) {
		verifyDistributionArgs(glist, meanOf, n);
		return Math.sqrt(variance(glist, meanOf, n));
	}

	/********************************/
	/* Verify Arguments             */
	/********************************/

	private static void verifyProbabilityArgs(List<Group> glist, int n) {
		verifyGroupList(glist, n);
	}

	private static void verifyGroupList(List<Group> glist, int n) throws IllegalArgumentException {
		
		if (glist.isEmpty()) {
			throw new IllegalArgumentException("The group list must contain at least one group.");
		}

		// Total the number of cards in the group list. This will be used to verify the
		// group list is all in order.
		int N = 0;
		for (int i = 0; i < glist.size(); i++) {
			if (glist.get(i).getK() < 0) {
				throw new IllegalArgumentException("The number of cards in each group must be greater"
						+ " than or equal to zero. The group " + glist.get(i).getName() + "violates this.");
			}
			N += glist.get(i).getK();

		}

		// Since we just enforced meanOf is contained in the group list, this verifies
		// it also.
		for (int i = 0; i < glist.size(); i++) {
			final int K = glist.get(i).getK();
			final int k1 = glist.get(i).getk1();
			final int k2 = glist.get(i).getk2();

			if (N < 0 || K < 0 || n < 0 || k1 < 0 || k2 < 0) {
				throw new IllegalArgumentException(
						"All arguments must be greater than or equal to zero. Negative numbers don't make sense in this context.");
			}
			// (k2 < k1) is already covered by Group's constructor.
			if (N < K) {
				throw new IllegalArgumentException(
						"N must be greater than or equal to K. There cannot be more successes in the deck than there are cards total.");
			}
			if (N < n) {
				throw new IllegalArgumentException(
						"N must be greater than or equal to n. You cannot look at more cards without replacement than are in the deck.");
			}
			if (n < k1) {
				throw new IllegalArgumentException(
						"n must be greater than or equal to k1. If you look at less cards than you need for a success, you can never have a success.");
			}
		}
	}

	private static void verifyPMFArgs(int N, List<Integer> K, int n, List<Integer> k) throws IllegalArgumentException {
		final int size = K.size();
		if (size != k.size()) {
			throw new IllegalArgumentException(
					"K and k must be the same size. Both should have one entry for each group.");
		}

		for (int i = 0; i < size; i++) {
			if (K.get(i) < k.get(i)) {
				throw new IllegalArgumentException(
						"Each value of K must be greater than or equal to the corresponding value of K. You cannot draw more successes than are in the deck.");
			}
		}
	}

	private static void verifyDistributionArgs(List<Group> glist, Group meanOf, int n) throws IllegalArgumentException {
		if (n < 0) {
			throw new IllegalArgumentException(
					"n must be greater than or equal to zero. You can't look at a negative number of cards.");
		}
		if (!glist.contains(meanOf)) {
			throw new IllegalArgumentException("The group \"meanOf\", in this case " + meanOf.getName()
					+ ", must be contained in the group list.");
		}
		verifyGroupList(glist, n);
	}

}
