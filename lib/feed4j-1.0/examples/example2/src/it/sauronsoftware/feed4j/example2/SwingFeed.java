package it.sauronsoftware.feed4j.example2;

import it.sauronsoftware.feed4j.FeedException;
import it.sauronsoftware.feed4j.bean.Feed;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class SwingFeed extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField addressBar = new JTextField();

	private JButton button = new JButton();

	private FeedPanel feedPanel = new FeedPanel();

	private JScrollPane scrollPane;

	public SwingFeed() {
		setTitle("feed4j demo");
		Dimension d = addressBar.getPreferredSize();
		d.width = 400;
		addressBar.setPreferredSize(d);
		button.setText("Go!");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFeed();
			}
		});
		scrollPane = new JScrollPane(feedPanel);
		scrollPane.setPreferredSize(new Dimension(400, 400));
		scrollPane.getViewport().setBackground(Color.WHITE);
		JRootPane barpane = new JRootPane();
		barpane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(3, 3, 3, 3);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 0;
		barpane.add(new JLabel("Feed URL:"), c);
		c.weightx = 1;
		c.gridx = 1;
		c.gridy = 0;
		barpane.add(addressBar, c);
		c.weightx = 0;
		c.gridx = 2;
		c.gridy = 0;
		barpane.add(button, c);
		barpane.setDefaultButton(button);
		JPanel north = new JPanel();
		north.setLayout(new BorderLayout(3, 3));
		north.add(barpane, BorderLayout.NORTH);
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout(3, 3));
		content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		content.add(north, BorderLayout.NORTH);
		content.add(scrollPane, BorderLayout.CENTER);
		setContentPane(content);
		pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frame = getSize();
		setLocation((screen.width - frame.width) / 2,
				(screen.height - frame.height) / 2);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void openFeed() {
		String address = addressBar.getText().trim();
		URL url;
		try {
			url = new URL(address);
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(this, "Invalid URL", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		FeedLoaderDialog d = new FeedLoaderDialog(this, url);
		Feed feed;
		try {
			feed = d.parseTheFeed();
		} catch (FeedException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Invalid feed", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		feedPanel.removeAll();
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(3, 3, 3, 3);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		feedPanel.add(new FeedHeaderPanel(feed.getHeader()), c);
		for (int i = 0; i < feed.getItemCount(); i++) {
			c.gridy++;
			feedPanel.add(new FeedItemPanel(feed.getItem(i)), c);
		}
		scrollPane.getViewport().revalidate();
	}

	public static void main(String[] args) throws Throwable {
		SwingFeed demo = new SwingFeed();
		demo.setVisible(true);
	}

}
