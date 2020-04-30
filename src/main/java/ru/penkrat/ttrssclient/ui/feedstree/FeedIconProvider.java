package ru.penkrat.ttrssclient.ui.feedstree;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import net.sf.image4j.codec.ico.ICODecoder;
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
		if (image.getException() != null) {
			try {
				try (InputStream istream = new URL(url).openStream();) {
					List<BufferedImage> images = ICODecoder.read(istream);
					if (!images.isEmpty())
						image = SwingFXUtils.toFXImage(images.get(0), new WritableImage(16, 16));
				}
			} catch (IOException e) {
				// TODO:
			}
		}
		return image;
	}

}
