package it.sauronsoftware.feed4j.example2;

import it.sauronsoftware.feed4j.bean.FeedEnclosure;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class FeedEnclosurePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public FeedEnclosurePanel(FeedEnclosure enclosure) {
		long size = enclosure.getLength();
		String mimeType = enclosure.getMimeType();
		String name = enclosure.getName();
		String title = enclosure.getTitle();
		URL url = enclosure.getURL();
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
		infoPanel.add(getTextArea("Title:", title), c);
		c.gridy = 1;
		infoPanel.add(getTextArea("Name:", name), c);
		c.gridy = 2;
		infoPanel.add(getTextArea("URL:", url), c);
		c.gridy = 3;
		infoPanel.add(getTextArea("MIME type:", mimeType), c);
		c.gridy = 4;
		infoPanel
				.add(getTextArea("Size:", size > 0 ? new Long(size) : null), c);
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
		setLayout(new BorderLayout(3, 3));
		setBorder(BorderFactory.createTitledBorder("Enclosure"));
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

}
