package it.sauronsoftware.feed4j.example2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Scrollable;

public class FeedPanel extends JPanel implements Scrollable {

	private static final long serialVersionUID = 1L;

	public FeedPanel() {
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 10;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 10;
	}

}
