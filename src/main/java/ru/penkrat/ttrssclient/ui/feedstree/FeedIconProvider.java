package ru.penkrat.ttrssclient.ui.feedstree;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.github.russ_p.ico4jfx.ICODecoder;

import javafx.scene.image.Image;
import ru.penkrat.ttrssclient.api.TTRSSClient;

@Component
public class FeedIconProvider {

	private TTRSSClient client;

	private Map<String, Image> cache = new HashMap<>();

	@Inject
	public FeedIconProvider(TTRSSClient client) {
		this.client = client;
	}

	public Image getImage(String feedId) {
		if (cache.containsKey(feedId)) {
			return cache.get(feedId);
		} else {
			String url = client.getIconURL(feedId);
			Image image = loadImage(url);
			cache.put(feedId, image);
			return image;
		}
	}

	private Image loadImage(String url) {
		Image image = new Image(url, false);
		if (image.getException() == null) {
			return image;
		}
		try (InputStream istream = new URL(url).openStream();) {
			InputStream bais = new ByteArrayInputStream(StreamUtils.copyToByteArray(istream));
			return ICODecoder.read(bais).stream()
					.max(Comparator.comparing(Image::getHeight))
					.get();
		} catch (Exception e) {
			// TODO:
		}
		return null;
	}

}
