package freenet.clients.http.updateableelements;

import java.text.NumberFormat;

import freenet.client.FetchException;
import freenet.clients.http.FProxyFetchResult;
import freenet.clients.http.FProxyFetchTracker;
import freenet.clients.http.ToadletContext;
import freenet.keys.FreenetURI;
import freenet.l10n.L10n;
import freenet.support.Base64;
import freenet.support.Logger;

public class ProgressBarElement extends BaseUpdateableElement {

	private FProxyFetchTracker	tracker;
	private FreenetURI			key;
	private long				maxSize;

	public ProgressBarElement(FProxyFetchTracker tracker, FreenetURI key, long maxSize, String requestUniqueName, ToadletContext ctx) {
		super("div", "class", "progressbar", requestUniqueName, ctx);
		this.tracker = tracker;
		this.key = key;
		this.maxSize = maxSize;
		init();
	}

	@Override
	public void updateState() {
		children.clear();

		FProxyFetchResult fr = tracker.getFetcher(key, maxSize).getResult();
		if (fr == null) {
			addChild("div", "No fetcher found");
		} else {
			int total = fr.requiredBlocks;
			int fetchedPercent = (int) (fr.fetchedBlocks / (double) total * 100);
			int failedPercent = (int) (fr.failedBlocks / (double) total * 100);
			int fatallyFailedPercent = (int) (fr.fatallyFailedBlocks / (double) total * 100);

			addChild("div", new String[] { "class", "style" }, new String[] { "progressbar-done", "width: " + fetchedPercent + "%;" });

			if (fr.failedBlocks > 0) addChild("div", new String[] { "class", "style" }, new String[] { "progressbar-failed", "width: " + failedPercent + "%;" });
			if (fr.fatallyFailedBlocks > 0) addChild("div", new String[] { "class", "style" }, new String[] { "progressbar-failed2", "width: " + fatallyFailedPercent + "%;" });

			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(1);
			String prefix = '(' + Integer.toString(fr.fetchedBlocks) + "/ " + Integer.toString(total) + "): ";
			if (fr.finalizedBlocks) {
				addChild("div", new String[] { "class", "title" }, new String[] { "progress_fraction_finalized", prefix + L10n.getString("QueueToadlet.progressbarAccurate") }, nf.format((int) ((fr.fetchedBlocks / (double) total) * 1000) / 10.0) + '%');
			} else {
				String text = nf.format((int) ((fr.fetchedBlocks / (double) total) * 1000) / 10.0) + '%';
				text = "" + fr.fetchedBlocks + " (" + text + "??)";
				addChild("div", new String[] { "class", "title" }, new String[] { "progress_fraction_not_finalized", prefix + L10n.getString("QueueToadlet.progressbarNotAccurate") }, text);
			}
		}
	}

	@Override
	public String getUpdaterId() {
		return getId(key);
	}

	public static String getId(FreenetURI uri) {
		return Base64.encodeStandard(("progressbar[URI:" + uri.toString() + "]").getBytes());
	}
}
