package ru.penkrat.ttrssclient.ui.articleview;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map.Entry;

import org.junit.Test;

public class AmpSearcherTest {

	private AmpSearcher search = new AmpSearcher();

	@Test
	public void testFindLink() throws Exception {
		Entry<Integer, String> linkData = search.findLink(
				"https://techcrunch.com/2020/05/03/intel-to-buy-smart-urban-transit-startup-moovit-for-1b-to-boost-its-autonomous-car-division/");

		assertThat(linkData.getValue()).isEqualTo(
				"https://techcrunch.com/2020/05/03/intel-to-buy-smart-urban-transit-startup-moovit-for-1b-to-boost-its-autonomous-car-division/amp/");
	}

}
