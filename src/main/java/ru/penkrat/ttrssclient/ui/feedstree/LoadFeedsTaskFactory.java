package ru.penkrat.ttrssclient.ui.feedstree;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import ru.penkrat.ttrssclient.api.TTRSSClient;
import ru.penkrat.ttrssclient.domain.Category;
import ru.penkrat.ttrssclient.domain.Feed;

@Component
public class LoadFeedsTaskFactory extends Service<List<Feed>> {

	private TTRSSClient client;

	@Inject
	public LoadFeedsTaskFactory(TTRSSClient client) {
		this.client = client;
	}

	public void runTask(Category category) {
		Task<List<Feed>> task = new Task<List<Feed>>() {

			@Override
			protected List<Feed> call() throws Exception {
				return client.getFeeds(category.getId());
			}
		};
		task.setOnSucceeded(e -> {
			category.getChildren().setAll(task.getValue());
		});
		this.executeTask(task);
	}

	@Override
	protected Task<List<Feed>> createTask() {
		return new Task<List<Feed>>() {

			@Override
			protected List<Feed> call() throws Exception {
				return null;
			}
		};
	}

}
