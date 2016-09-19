package it.sauronsoftware.feed4j.example2;

import it.sauronsoftware.feed4j.bean.FeedHeader;
import it.sauronsoftware.feed4j.bean.FeedImage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class FeedHeaderPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel imageLabel = new JLabel();

	private ImageLoader imageLoader = new ImageLoader();

	public FeedHeaderPanel(FeedHeader header) {
		URL url = header.getURL();
		String title = header.getTitle();
		URL link = header.getLink();
		String description = header.getDescription();
		String language = header.getLanguage();
		Date date = header.getPubDate();
		FeedImage image = header.getImage();

		JPanel infoPanel = new JPanel();
		infoPanel.setBackground(Color.WHITE);
		infoPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(3, 3, 3, 3);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		infoPanel.add(getTextArea("URL:", url), c);
		c.gridy = 1;
		infoPanel.add(getTextArea("Title:", title), c);
		c.gridy = 2;
		infoPanel.add(getTextArea("Link:", link), c);
		c.gridy = 3;
		infoPanel.add(getTextArea("Language:", language), c);
		c.gridy = 4;
		infoPanel.add(getTextArea("PubDate:", date), c);
		c.gridy = 5;
		infoPanel.add(getTextArea("Description:", description), c);
		if (image != null) {
			imageLabel.setBackground(Color.WHITE);
			imageLabel.setForeground(Color.BLACK);
			imageLabel.setBorder(null);
			imageLabel.setIcon(new ImageIcon(getClass().getResource(
					"wait_small.gif")));
			imageLabel.setHorizontalAlignment(JLabel.CENTER);
			imageLabel.setVerticalAlignment(JLabel.CENTER);
			c.gridx = 1;
			c.gridy = 0;
			c.weightx = 0;
			c.weighty = 1;
			c.gridheight = 6;
			infoPanel.add(imageLabel);
			imageLoader.load(image.getURL());
		}
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
		setLayout(new BorderLayout(3, 3));
		add(infoPanel, BorderLayout.CENTER);
	}

	private JComponent getTextArea(String label, Object value) {
		JLabel c1 = new JLabel(label);
		JTextArea c2 = new JTextArea(value != null ? value.toString() : "N/A");
		c1.setBackground(Color.WHITE);
		c1.setForeground(Color.BLACK);
		c2.setBackground(Color.WHITE);
		c2.setForeground(Color.BLACK);
		c2.setEditable(false);
		c2.setLineWrap(true);
		c2.setWrapStyleWord(true);
		JPanel all = new JPanel();
		all.setLayout(new GridBagLayout());
		all.setBackground(Color.WHITE);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(3, 3, 3, 3);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 0;
		all.add(c1, c);
		c.weightx = 1;
		c.gridx = 1;
		c.gridy = 0;
		all.add(c2, c);
		return all;
	}

	private class ImageLoader implements Runnable {

		private Thread thread;

		private URL url;

		public void load(URL url) {
			this.url = url;
			thread = new Thread(this);
			thread.start();
		}

		public void run() {
			try {
				Image image = ImageIO.read(url);
				ImageIcon icon = new ImageIcon(image);
				imageLabel.setIcon(icon);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

	}

}
