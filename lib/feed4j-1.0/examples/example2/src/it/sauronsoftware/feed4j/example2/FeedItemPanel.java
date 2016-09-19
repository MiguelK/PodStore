package it.sauronsoftware.feed4j.example2;

import it.sauronsoftware.feed4j.bean.FeedItem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class FeedItemPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public FeedItemPanel(FeedItem item) {
		URL comments = item.getComments();
		String description = item.getDescriptionAsHTML();
		String guid = item.getGUID();
		URL link = item.getLink();
		Date modDate = item.getModDate();
		Date pubDate = item.getPubDate();
		String title = item.getTitle();
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
		infoPanel.add(getTextArea("GUID:", guid), c);
		c.gridy = 1;
		infoPanel.add(getTextArea("Title:", title), c);
		c.gridy = 2;
		infoPanel.add(getTextArea("Link:", link), c);
		c.gridy = 3;
		infoPanel.add(getTextArea("PubDate:", pubDate), c);
		c.gridy = 4;
		infoPanel.add(getTextArea("ModDate:", modDate), c);
		c.gridy = 5;
		infoPanel.add(getHTMLArea("Description:", description), c);
		c.gridy = 6;
		infoPanel.add(getTextArea("URL for comments:", comments), c);
		for (int i = 0; i < item.getEnclosureCount(); i++) {
			c.gridy++;
			infoPanel.add(new FeedEnclosurePanel(item.getEnclosure(i)), c);
		}
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
		setLayout(new BorderLayout(3, 3));
		setBorder(BorderFactory
				.createTitledBorder(title != null ? title : guid));
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

	private JComponent getHTMLArea(String label, Object value) {
		JLabel c1 = new JLabel(label);
		JEditorPane c2 = new JEditorPane();
		c1.setBackground(Color.WHITE);
		c1.setForeground(Color.BLACK);
		c2.setBackground(Color.WHITE);
		c2.setForeground(Color.BLACK);
		c2.setEditable(false);
		c2.setContentType("text/html");
		c2.setText(value != null ? value.toString() : "N/A");
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

}
