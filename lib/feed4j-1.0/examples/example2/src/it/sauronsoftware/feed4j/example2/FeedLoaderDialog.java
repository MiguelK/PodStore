package it.sauronsoftware.feed4j.example2;

import it.sauronsoftware.feed4j.FeedException;
import it.sauronsoftware.feed4j.FeedParser;
import it.sauronsoftware.feed4j.bean.Feed;

import java.awt.BorderLayout;
import java.awt.Color;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FeedLoaderDialog extends JDialog implements Runnable {

	private static final long serialVersionUID = 1L;
	
	private URL url;
	
	private Feed feed = null;
	
	private FeedException exception = null;

	public FeedLoaderDialog(JFrame owner, URL url) {
		super(owner, true);
		this.url = url;
		JLabel wait = new JLabel();
		wait.setIcon(new ImageIcon(getClass().getResource("wait.gif")));
		JPanel all = new JPanel();
		all.setLayout(new BorderLayout(3, 3));
		all.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createLineBorder(Color.BLACK), BorderFactory
				.createEmptyBorder(10, 10, 10, 10)));
		all.setBackground(Color.WHITE);
		all.add(wait, BorderLayout.CENTER);
		setContentPane(all);
		setUndecorated(true);
		pack();
		setResizable(false);
		setLocationRelativeTo(this);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	public Feed parseTheFeed() throws FeedException {
		Thread t = new Thread(this);
		t.start();
		setVisible(true);
		if (feed == null) {
			throw exception;
		} else {
			return feed;
		}
	}

	public void run() {
		try {
			feed = FeedParser.parse(url);
		} catch (FeedException e) {
			exception = e;
		}
		dispose();
	}

}
