package hproject;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import statistics.hypergeo.Group;
import statistics.hypergeo.MultiHyperGeo;
import statistics.hypergeo.UniHyperGeo;

public class Tests {

	@Test
	public void compareHyperGeo() {

		List<Group> glist = new ArrayList<>();
		glist.add(new Group(24, 2, 4));
		glist.add(new Group(60 - 24, 0, 7));
		
		// These may not be exactly equal, there could be a floating point rounding error.
		assertEquals(MultiHyperGeo.probability(glist, 7), UniHyperGeo.probability(60, 24, 7, 2, 4), 0.00000001);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void emptyMultiHyperGeo() {
		List<Group> glist = new ArrayList<>();
		MultiHyperGeo.probability(glist, 7); 
	}
	
}
