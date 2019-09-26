package statistics.hypergeo;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class UniHyperGeo {

	/** 
	 * Protected Constructor for static method class
	 */
	protected UniHyperGeo() {
	}

	
	/********************************/
	/* Probability Functions        */
	/********************************/

	/**
	 * The UniVariate Hypergeometric Probability function.
	 * 
	 * Let's say you have a bag with 60 balls in it, 24 of which are white, the rest
	 * being any other color or combination thereof. You want to calculate the
	 * probability of drawing between two and four white balls in a sample of seven
	 * balls without replacement. If you draw two, three, or four white balls,
	 * that's a success. To calculate the probability of drawing exactly 3 balls,
	 * for example, see the {@link #pmf(int, int, int, int) pmf} function. This
	 * function calculates the probability of success for these types of questions
	 * concerning a single color of ball. To discern the probability for multiple
	 * colors of balls, please see
	 * {@link statistics.hypergeo.MultiHyperGeo#probability(java.util.List, int)
	 * Multivariate probability}
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param N
	 *            The total population size (There are 60 balls in the bag).
	 * @param K
	 *            The number of successes in the population (The number of balls of
	 *            the color you're counting that are in the bag, 24 balls).
	 * @param n
	 *            The number of items sampled (The number of draws from the bag, 7
	 *            balls).
	 * @param k1
	 *            The minimum number of observed successes required (2 balls).
	 * @param k2
	 *            The maximum number of observed successes required (4 balls).
	 * 
	 * @return probability The probability as a decimal from zero to one that
	 *         between k1 and k2 successes will occur in n draws.
	 * 
	 * @throws IllegalArgumentException
	 *             When (N < 0 || K < 0 || n < 0 || k1 < 0 || k2 < 0), (k2 < k1), (N
	 *             < K), (N < n), or (n < k1).
	 */
	public static double probability(int N, int K, int n, int k1, int k2) throws IllegalArgumentException {

		verifyProbabilityArgs(N, K, n, k1, k2);

		if (K < k1) {
			// If there are less successes in the population than are required, then there's
			// no way to possibly succeed.
			return 0;
		}

		double probability = 0;
		for (int k = k1; k <= k2; k++) {
			probability += pmf(N, K, n, k);
		}
		return probability;
	}

	/**
	 * The inverse of the {@link #probability(int, int, int, int, int) UniVariate
	 * Hypergeometric Probability} function. Given a probability P expressed as a
	 * decimal, it finds the smallest integer K such that, with the other arguments,
	 * the probability function will return a probability greater than or equal to
	 * P.
	 * 
	 * This function can answer the question: "How many copies of a type of card do
	 * I need to play in my N card deck to draw between k1 and k2 of them in n cards
	 * P (as a decimal) percent of the time?"
	 * 
	 * Note that there are likely multiple answers to this question. This method
	 * finds the smallest such answer.
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param P
	 *            The required success rate (as a decimal).
	 * @param N
	 *            The total population size.
	 * @param n
	 *            The number of items sampled.
	 * @param k1
	 *            The minimum number of observed successes required.
	 * @param k2
	 *            The maximum number of observed successes required.
	 * @return K The number of successes in the population necessary to achieve a
	 *         success rate of P.
	 * 
	 * @throws IllegalArgumentException
	 *             When (P < 0 || N < 0 || n < 0 || k1 < 0 || k2 < 0), (P > 1), (k2
	 *             < k1), (N < K), (N < n), or (n < k1).
	 */
	public static int inverseProbability(double P, int N, int n, int k1, int k2) throws IllegalArgumentException {
		if (0 > P || P > 1) {
			throw new IllegalArgumentException("P is a probability, and therefore must be between one and zero.");
		}

		// If we want a zero probability, we need zero.
		if (P == 0) {
			return 0;
		}

		// If we want to be certain, how many do we need in general?
		if (P == 1) {

			// Let's imagine the worst case scenario. Let's say that all the cards that we
			// didn't draw are successes. We still need at least k1 more successes in the
			// population to be sure that the worst case is a success.
			return (N - n) + k1;
		}

		// Otherwise, start iterating. Since as K increases the probability increases
		// and then decreases, and the center could be anywhere, it unfortunately makes
		// most sense just to guess and check each possible value of K in order. I wrote
		// another function that took guesses and honed in on the correct value in
		// log(N - k1) time, similar to the way a binary tree works, but unfortunately since
		// the center could be anywhere, it didn't work out. This is inefficient, but
		// also probably the best solution.
		double currentProb = 0;
		for (int Kguess = k1; Kguess <= N; Kguess++) {
			currentProb = probability(N, Kguess, n, k1, k2);
			if (currentProb >= P) {
				return Kguess;
			}
		}
		return 0;
	}

	/**
	 * The UniVariate Hypergeometric Probability Mass Function (PMF).
	 * 
	 * Given a total population size N, how many successes exist in it K, a number
	 * of objects to sample from the population n, and an exact number of successes
	 * k, we can calculate the probability that you will draw exactly k objects from
	 * the sample of n. If you want to calculate the probability for a range of such
	 * possibilities given by values of k, see
	 * {@link UniHyperGeo#probability(int, int, int, int, int) univariate
	 * probability}.
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param N
	 *            The total population size.
	 * @param K
	 *            The number of successes in the population.
	 * @param n
	 *            The number of items sampled.
	 * @param k
	 *            The exact number of observed successes required.
	 * 
	 * @return pm The probability as a decimal from zero to one that exactly k
	 *         successes will occur in n draws.
	 *
	 * @throws IllegalArgumentException
	 *             If (k1 < 0), (k2 < k1), (k2 > K), (N < K), (N < n), or (n < k1).
	 */
	public static double pmf(int N, int K, int n, int k) throws IllegalArgumentException {

		verifyPMFArgs(N, K, n, k);

		// Decided just to use the Apache Commons library to save time.
		return (CombinatoricsUtils.binomialCoefficientDouble(K, k)
				* CombinatoricsUtils.binomialCoefficientDouble(N - K, n - k))
				/ CombinatoricsUtils.binomialCoefficientDouble(N, n);
	}

	
	/********************************/
	/* Probabilities with Mulligans */
	/********************************/

	/**
	 * Calculates the probability that, in the course of mulliganing to X cards left
	 * in hand using the Vancouver mulligan you'll draw a hand with k1 to k2
	 * successes in it.
	 * 
	 * To account for one free or "friendly" mulligan, see
	 * {@link #probabilityOnVancouverMullToXWithFree(int, int, int, int, int)
	 * probabilityOnVancouverMullToXWithFree()}.
	 * 
	 * For other mulligans, see
	 * {@link #probabilityOnParisMullToX(int, int, int, int, int) Paris} and
	 * {@link #probabilityOnLondonMullToX(int, int, int, int, int) London}.
	 * 
	 * For information about different mulligans, see: <a href=
	 * "https://mtg.gamepedia.com/Mulligan">https://mtg.gamepedia.com/Mulligan</a>
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param N
	 *            The total population size (the number of cards in the deck).
	 * @param K
	 *            The number of successes in the population (copies of a type of
	 *            card).
	 * @param X
	 *            The number of cards in hand that you're willing to mulligan down
	 *            to (between 0 - 7).
	 * @param k1
	 *            The minimum number of observed successes required.
	 * @param k2
	 *            The maximum number of observed successes required.
	 * 
	 * @return probability The probability that, in all your mulligans, you draw
	 *         between k1 and k2 of the type of card.
	 * 
	 * @throws IllegalArgumentException
	 *             When (N < 0 || K < 0 || n < 0 || k1 < 0 || k2 < 0), (X > 7), (k2
	 *             < k1), (N < K), (N < n), or (n < k1).
	 */
	public static double probabilityOnVancouverMullToX(int N, int K, int X, int k1, int k2)
			throws IllegalArgumentException {
		return calculateProbabilityOnGeneralMullToX(N, K, X, k1, k2, true, 7);
	}

	/**
	 * Calculates the probability that, in the course of mulliganing to X cards left
	 * in hand using the Vancouver mulligan you'll draw a hand with k1 to k2
	 * successes in it.
	 * 
	 * This method also takes one extra free mulligan into account. For the normal
	 * probability on Vancouver mulligans, see
	 * {@link #probabilityOnVancouverMullToX(int, int, int, int, int)
	 * probabilityOnVancouverMullToX()}.
	 * 
	 * For other mulligans, see
	 * {@link #probabilityOnParisMullToX(int, int, int, int, int) Paris} and
	 * {@link #probabilityOnLondonMullToX(int, int, int, int, int) London}.
	 * 
	 * For information about different mulligans, see: <a href=
	 * "https://mtg.gamepedia.com/Mulligan">https://mtg.gamepedia.com/Mulligan</a>
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param N
	 *            The total population size (the number of cards in the deck).
	 * @param K
	 *            The number of successes in the population (copies of a type of
	 *            card).
	 * @param X
	 *            The number of cards in hand that you're willing to mulligan down
	 *            to (between 0 - 7).
	 * @param k1
	 *            The minimum number of observed successes required.
	 * @param k2
	 *            The maximum number of observed successes required.
	 * 
	 * @return probability The probability that, in all your mulligans, you draw
	 *         between k1 and k2 of the type of card.
	 * 
	 * @throws IllegalArgumentException
	 *             When (N < 0 || K < 0 || n < 0 || k1 < 0 || k2 < 0), (X > 7), (k2
	 *             < k1), (N < K), (N < n), or (n < k1).
	 */
	public static double probabilityOnVancouverMullToXWithFree(int N, int K, int X, int k1, int k2)
			throws IllegalArgumentException {
		return 1 - ((1 - probabilityOnVancouverMullToX(N, K, X, k1, k2)) * (1 - probability(N, K, 7, k1, k2)));
	}

	/**
	 * Calculates the probability that, in the course of mulliganing to X cards left
	 * in hand using the Paris mulligan you'll draw a hand with k1 to k2 successes
	 * in it.
	 * 
	 * To account for one free or "friendly" mulligan, see
	 * {@link #probabilityOnParisMullToXWithFree(int, int, int, int, int)
	 * probabilityOnParisMullToXWithFree()}.
	 * 
	 * For other mulligans, see
	 * {@link #probabilityOnVancouverMullToX(int, int, int, int, int) Vancouver} and
	 * {@link #probabilityOnLondonMullToX(int, int, int, int, int) London}.
	 * 
	 * For information about different mulligans, see: <a href=
	 * "https://mtg.gamepedia.com/Mulligan">https://mtg.gamepedia.com/Mulligan</a>
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param N
	 *            The total population size (the number of cards in the deck).
	 * @param K
	 *            The number of successes in the population (copies of a type of
	 *            card).
	 * @param X
	 *            The number of cards in hand that you're willing to mulligan down
	 *            to (between 0 - 7).
	 * @param k1
	 *            The minimum number of observed successes required.
	 * @param k2
	 *            The maximum number of observed successes required.
	 * 
	 * @return probability The probability that, in all your mulligans, you draw
	 *         between k1 and k2 of the type of card.
	 * 
	 * @throws IllegalArgumentException
	 *             When (N < 0 || K < 0 || n < 0 || k1 < 0 || k2 < 0), (X > 7), (k2
	 *             < k1), (N < K), (N < n), or (n < k1).
	 */
	public static double probabilityOnParisMullToX(int N, int K, int X, int k1, int k2)
			throws IllegalArgumentException {
		return calculateProbabilityOnGeneralMullToX(N, K, X, k1, k2, false, 7);
	}

	/**
	 * Calculates the probability that, in the course of mulliganing to X cards left
	 * in hand using the Paris mulligan you'll draw a hand with k1 to k2 successes
	 * in it.
	 * 
	 * This method also takes one extra free mulligan into account. For the normal
	 * probability on Paris mulligans, see
	 * {@link #probabilityOnParisMullToX(int, int, int, int, int)
	 * probabilityOnParisMullToX()}.
	 * 
	 * For other mulligans, see
	 * {@link #probabilityOnVancouverMullToX(int, int, int, int, int) Vancouver} and
	 * {@link #probabilityOnLondonMullToX(int, int, int, int, int) London}.
	 * 
	 * For information about different mulligans, see: <a href=
	 * "https://mtg.gamepedia.com/Mulligan">https://mtg.gamepedia.com/Mulligan</a>
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param N
	 *            The total population size (the number of cards in the deck).
	 * @param K
	 *            The number of successes in the population (copies of a type of
	 *            card).
	 * @param X
	 *            The number of cards in hand that you're willing to mulligan down
	 *            to (between 0 - 7).
	 * @param k1
	 *            The minimum number of observed successes required.
	 * @param k2
	 *            The maximum number of observed successes required.
	 * 
	 * @return probability The probability that, in all your mulligans, you draw
	 *         between k1 and k2 of the type of card.
	 * 
	 * @throws IllegalArgumentException
	 *             When (N < 0 || K < 0 || n < 0 || k1 < 0 || k2 < 0), (X > 7), (k2
	 *             < k1), (N < K), (N < n), or (n < k1).
	 */
	public static double probabilityOnParisMullToXWithFree(int N, int K, int X, int k1, int k2)
			throws IllegalArgumentException {
		return 1 - ((1 - probabilityOnParisMullToX(N, K, X, k1, k2)) * (1 - probability(N, K, 7, k1, k2)));
	}

	/**
	 * Calculates the probability that, in the course of mulliganing to X cards left
	 * in hand using the London mulligan you'll draw a hand with k1 to k2 successes
	 * in it.
	 * 
	 * For information about different mulligans, see: <a href=
	 * "https://mtg.gamepedia.com/Mulligan">https://mtg.gamepedia.com/Mulligan</a>
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param N
	 *            The total population size (the number of cards in the deck).
	 * @param K
	 *            The number of successes in the population (copies of a type of
	 *            card).
	 * @param X
	 *            The number of cards in hand that you're willing to mulligan down
	 *            to (between 0 - 7).
	 * @param k1
	 *            The minimum number of observed successes required.
	 * @param k2
	 *            The maximum number of observed successes required.
	 * 
	 * @return probability The probability that, in all your mulligans, you draw
	 *         between k1 and k2 of the type of card.
	 * 
	 * @throws IllegalArgumentException
	 *             When (N < 0 || K < 0 || n < 0 || k1 < 0 || k2 < 0), (X > 7), (k2
	 *             < k1), (N < K), (N < n), or (n < k1).
	 */
	public static double probabilityOnLondonMullToX(int N, int K, int X, int k1, int k2)
			throws IllegalArgumentException {
		return calculateProbabilityOnLondonMullToX(N, K, X, k1, k2, 7);
	}

	/**
	 * Calculates the probability that, in the course of mulliganing to X cards left
	 * in hand using the London mulligan you'll draw a hand with k1 to k2 successes
	 * in it.
	 * 
	 * This method also takes one extra free mulligan into account. For the normal
	 * probability on London mulligans, see
	 * {@link #probabilityOnLondonMullToX(int, int, int, int, int)
	 * probabilityOnLondonMullToX()}.
	 * 
	 * For other mulligans, see
	 * {@link #probabilityOnVancouverMullToX(int, int, int, int, int) Vancouver} and
	 * {@link #probabilityOnParisMullToX(int, int, int, int, int) Paris}.
	 * 
	 * For information about different mulligans, see: <a href=
	 * "https://mtg.gamepedia.com/Mulligan">https://mtg.gamepedia.com/Mulligan</a>
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param N
	 *            The total population size (the number of cards in the deck).
	 * @param K
	 *            The number of successes in the population (copies of a type of
	 *            card).
	 * @param X
	 *            The number of cards in hand that you're willing to mulligan down
	 *            to (between 0 - 7).
	 * @param k1
	 *            The minimum number of observed successes required.
	 * @param k2
	 *            The maximum number of observed successes required.
	 * 
	 * @return probability The probability that, in all your mulligans, you draw
	 *         between k1 and k2 of the type of card.
	 * 
	 * @throws IllegalArgumentException
	 *             When (N < 0 || K < 0 || n < 0 || k1 < 0 || k2 < 0), (X > 7), (k2
	 *             < k1), (N < K), (N < n), or (n < k1).
	 */
	public static double probabilityOnLondonMullToXWithFree(int N, int K, int X, int k1, int k2)
			throws IllegalArgumentException {
		return 1 - ((1 - probabilityOnLondonMullToX(N, K, X, k1, k2)) * (1 - probability(N, K, 7, k1, k2)));
	}

	
	/********************************/
	/* Utility Functions            */
	/********************************/
	
	/**
	 * Calculates the probability that, in the course of mulliganing to X cards left
	 * in hand using the London mulligan you'll draw a hand with k1 to k2 successes
	 * in it. For the Vancouver and Paris mulligans.
	 * 
	 * For the London Mulligan equivalent, see
	 * {@link #calculateProbabilityOnLondonMullToX(int, int, int, int, int, int)
	 * calculateProbabilityOnLondonMullToX()}.
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param N
	 *            The total population size (the number of cards in the deck).
	 * @param K
	 *            The number of successes in the population (copies of a type of
	 *            card).
	 * @param X
	 *            The number of cards in hand that you're willing to mulligan down
	 *            to (between 0 - 7).
	 * @param k1
	 *            The minimum number of observed successes required.
	 * @param k2
	 *            The maximum number of observed successes required.
	 * @param withScries
	 *            True if calculating for the Vancouver Mulligan, False for Paris.
	 * @param startingHandSize
	 *            The initial size of your opening hand.
	 * 
	 * @return probability The probability that, in all your mulligans, you draw
	 *         between k1 and k2 of the type of card.
	 * 
	 * @throws IllegalArgumentException
	 *             When (X > startingHandSize), (N < 0 || K < 0 || n < 0 || k1 < 0
	 *             || k2 < 0), (X > 7), (k2 < k1), (N < K), (N < n), or (n < k1).
	 */
	protected static double calculateProbabilityOnGeneralMullToX(int N, int K, int X, int k1, int k2,
			boolean withScries, int startingHandSize) throws IllegalArgumentException {

		// This method will also throw the same IllegalArgumentExceptions that
		// probability() will throw to verify its arguments, since this method calls
		// that one. However, there is one new variable to check or correct.

		// If X is less than the k1, the minimum required successes, then that means we
		// can only mulligan down to k1 cards in hand.
		// This is different from probability() where we throw an exception on n < k1.
		// Here we can still give a proper answer.
		if (X < k1) {
			X = k1;
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
			return probability(N, K, startingHandSize, k1, k2);
		}

		// In these "At least one" Combinatorics problems, the probability of at least
		// one event occurring is the compliment of the event not occurring, which
		// itself
		// is the compliment of the probability of the event happening. So, instead of
		// tracking the probability of success through the problem, we track the
		// probability of failure.

		double probabilityOfFailure = 1;

		// The initial opening hand
		probabilityOfFailure *= 1 - probability(N, K, startingHandSize, k1, k2);

		// Mulligans (with/without scries)
		for (int mullHandSize = startingHandSize - 1; mullHandSize >= X; mullHandSize--) {
			// If there are scries, then add one to how many cards we're looking at.
			probabilityOfFailure *= 1 - probability(N, K, withScries ? mullHandSize + 1 : mullHandSize, k1, k2);
		}
		return 1 - probabilityOfFailure;
	}

	/**
	 * Calculates the probability that, in the course of mulliganing to X cards left
	 * in hand using the London mulligan you'll draw a hand with k1 to k2 successes
	 * in it. This function does the same thing as
	 * {@link #probabilityOnLondonMullToX(int, int, int, int, int)
	 * #probabilityOnLondonMullToX()}, except it lets you choose your starting hand
	 * size.
	 * 
	 * For other mulligans, see
	 * {@link #calculateProbabilityOnGeneralMullToX(int, int, int, int, int, boolean, int)
	 * calculateProbabilityOnGeneralMullToX()}.
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param N
	 *            The total population size (the number of cards in the deck).
	 * @param K
	 *            The number of successes in the population (copies of a type of
	 *            card).
	 * @param X
	 *            The number of cards in hand that you're willing to mulligan down
	 *            to (between 0 - 7).
	 * @param k1
	 *            The minimum number of observed successes required.
	 * @param k2
	 *            The maximum number of observed successes required.
	 * @param startingHandSize
	 *            The initial size of your opening hand.
	 * 
	 * @return probability The probability that, in all your mulligans, you draw
	 *         between k1 and k2 of the type of card.
	 * 
	 * @throws IllegalArgumentException
	 *             When (X > startingHandSize), (N < 0 || K < 0 || n < 0 || k1 < 0
	 *             || k2 < 0), (X > 7), (k2 < k1), (N < K), (N < n), or (n < k1).
	 */
	protected static double calculateProbabilityOnLondonMullToX(int N, int K, int X, int k1, int k2,
			int startingHandSize) throws IllegalArgumentException {
		// This method is much like calculateProbabilityOnGeneralMullToX(), but with the
		// London Mulligan.

		// Same as that function, this one will also throw the same
		// IllegalArgumentExceptions that probability() will throw to verify its
		// arguments, since this method calls that one. However, there is one new
		// variable to check or correct.

		// If X is less than the k1, the minimum required successes, then that means we
		// can only mulligan down to k1 cards in hand. This is different from
		// probability() where we throw an exception on n < k1. Here we can still give a
		// proper answer.
		if (X < k1) {
			X = k1;
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
			return probability(N, K, startingHandSize, k1, k2);
		}

		// In these "At least one" Combinatorics problems, the probability of at least
		// one event occurring is the compliment of the event not occurring, which
		// itself is the compliment of the probability of the event happening. So,
		// instead of tracking the probability of success through the problem, we track
		// the probability of failure.

		// The initial opening hand, then all mulligans
		double probabilityOfFailure = 1;
		for (int i = X; i <= startingHandSize; i++) {
			probabilityOfFailure *= 1 - probability(N, K, startingHandSize, k1, k2);
		}

		return 1 - probabilityOfFailure;
	}

	
	/********************************/
	/* Stats Functions              */
	/********************************/

	/**
	 * Calculates the average number of expected successes in a sample of size n
	 * from a population of size N with K successes in it.
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param N
	 *            The total population size.
	 * @param K
	 *            The number of successes in the population.
	 * @param n
	 *            The number of items sampled.
	 * 
	 * @return mean The average number of successes.
	 *
	 * @throws IllegalArgumentException
	 *             When (N < 0 || K < 0 || n < 0), (N < K), or (N < n).
	 */
	public static double mean(int N, int K, int n) throws IllegalArgumentException {
		verifyDistributionArgs(N, K, n);

		// Having a population size of zero would cause division by zero, but we can
		// return a zero in that case. If there are no cards in the deck, then you can't
		// draw any, so the answer is zero.
		if (N == 0) {
			return 0.0;
		}
		return (double) (n * K) / (double) N;
	}

	/**
	 * Calculates the variance of a mean with these arguments.
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param N
	 *            The total population size.
	 * @param K
	 *            The number of successes in the population.
	 * @param n
	 *            The number of items sampled.
	 * @return variance A measure of how far the number of successes will spread out
	 *         from the mean.
	 * 
	 * @throws IllegalArgumentException
	 *             When (N < 0 || K < 0 || n < 0), (N < K), or (N < n).
	 */
	public static double variance(int N, int K, int n) throws IllegalArgumentException {

		verifyDistributionArgs(N, K, n);

		// These would cause division by zero, but we can return zero here. We can
		// return zero since we've verified that 0 <= N < n. We know that we're
		// looking at least through every card in the deck. Therefore the variance is
		// zero. There's no longer an element of randomness.
		if (N == 0 || N == 1) {
			return 0.0;
		}

		// Note that if n or K equal zero the answer will be zero.
		return (double) (n * K * (N - K) * (N - n)) / (double) (N * N * (N - 1));
	}

	/**
	 * Calculates the standard deviation of a {@link #mean(int, int, int) mean} with
	 * these arguments.
	 * 
	 * The number of successes that the sample is expected to vary by on average.
	 * This can be used with {@link #mean(int, int, int) Mean} to construct a bell
	 * curve.
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param N
	 *            The total population size.
	 * @param K
	 *            The number of successes in the population.
	 * @param n
	 *            The number of items sampled.
	 * 
	 * @return standardDeviation the standard deviation for the mean with these
	 *         arguments.
	 */
	public static double standardDeviation(int N, int K, int n) throws IllegalArgumentException {
		// No extra checks required here. Handled by variance(). Also, Math.sqrt() can't
		// return anything weird because of those checks.
		return Math.sqrt(variance(N, K, n));
	}

	
	/********************************/
	/* Verify Arguments             */
	/********************************/

	/**
	 * Checks arguments for the Univariate {@link #pmf(int, int, int, int)
	 * Probability Mass Function}.
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param N
	 *            The total population size.
	 * @param K
	 *            The number of successes in the population.
	 * @param n
	 *            The number of items sampled.
	 * @param k
	 *            The exact number of observed successes required.
	 * 
	 * @throws IllegalArgumentException
	 *             When (N < 0 || K < 0 || n < 0 || k < 0), (k > K), (N < K), (N <
	 *             n), or (n < k).
	 */
	private static void verifyPMFArgs(int N, int K, int n, int k) throws IllegalArgumentException {
		if (N < 0 || K < 0 || n < 0 || k < 0) {
			throw new IllegalArgumentException(
					"All arguments must be greater than or equal to zero. Negative numbers don't make sense in this context.");
		}
		if (k > K) {
			throw new IllegalArgumentException(
					"k must be less than or equal to K. You cannot draw more successes than are in the deck.");
		}
		if (N < K) {
			throw new IllegalArgumentException(
					"N must be greater than or equal to K. There cannot be more successes in the deck than there are cards total.");
		}
		if (N < n) {
			throw new IllegalArgumentException(
					"N must be greater than or equal to n. You cannot look at more cards without replacement than are in the deck.");
		}
		if (n < k) {
			throw new IllegalArgumentException(
					"n must be greater than or equal to k. If you look at less cards than you need for a success, you can never have a success.");
		}
		return;
	}

	/**
	 * Checks arguments for the Univariate
	 * {@link #probability(int, int, int, int, int) Probability Function}.
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param N
	 *            The total population size.
	 * @param K
	 *            The number of successes in the population.
	 * @param n
	 *            The number of items sampled.
	 * @param k1
	 *            The minimum number of observed successes required.
	 * @param k2
	 *            The maximum number of observed successes required.
	 * 
	 * @throws IllegalArgumentException
	 *             When (N < 0 || K < 0 || n < 0 || k1 < 0 || k2 < 0), (k2 < k1), (N
	 *             < K), (N < n), or (n < k1).
	 */
	private static void verifyProbabilityArgs(int N, int K, int n, int k1, int k2) throws IllegalArgumentException {
		if (N < 0 || K < 0 || n < 0 || k1 < 0 || k2 < 0) {
			throw new IllegalArgumentException(
					"All arguments must be greater than or equal to zero. Negative numbers don't make sense in this context.");
		}
		if (k2 < k1) {
			throw new IllegalArgumentException(
					"k2 must be greater than or equal to k1. k1 is the minimum number of successes, and k2 is the maximum.");
		}
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

	/**
	 * Checks arguments for the Univariate distribution functions,
	 * {@link #mean(int, int, int) Mean}, {@link #variance(int, int, int) Variance},
	 * and {@link #standardDeviation(int, int, int) Standard Deviation}.
	 * 
	 * @author Aaron Pazdera
	 * 
	 * @param N
	 *            The total population size.
	 * @param K
	 *            The number of successes in the population.
	 * @param n
	 *            The number of items sampled.
	 * 
	 * @throws IllegalArgumentException
	 *             When (N < 0 || K < 0 || n < 0), (N < K), or (N < n).
	 */
	private static void verifyDistributionArgs(int N, int K, int n) throws IllegalArgumentException {
		if (N < 0 || K < 0 || n < 0) {
			throw new IllegalArgumentException(
					"All arguments must be greater than or equal to zero. Negative numbers don't make sense in this context.");
		}
		if (N < K) {
			throw new IllegalArgumentException(
					"N must be greater than or equal to K. There cannot be more successes in the deck than there are cards total.");
		}
		if (N < n) {
			throw new IllegalArgumentException(
					"N must be greater than or equal to n. You cannot look at more cards without replacement than are in the deck.");
		}
	}

}
