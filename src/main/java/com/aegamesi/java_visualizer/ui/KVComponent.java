package com.aegamesi.java_visualizer.ui;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

/**
 * A component that displays a table of two columns of components with
 * some additional drawing
 */
class KVComponent extends JPanel {
	private ComponentFormat format;

	private int hsplit;
	private int[] vsplits;

	private int vsplit;
	private int[] hsplits;

	private int padding;
	private Color colorLeft;
	private Color colorRight;
	private Color colorBorder;

	private List<? extends JComponent> keys;
	private List<? extends JComponent> vals;

	KVComponent() {
		setLayout(null);
		this.format = ComponentFormat.LIST;
	}

	KVComponent(ComponentFormat format) {
		this();
		this.format = format;
	}

	void setPadding(int padding) {
		this.padding = padding;
	}

	void setComponents(List<? extends JComponent> keys, List<? extends JComponent> vals) {
		if (keys.size() != vals.size()) {
			throw new IllegalArgumentException();
		}
		this.keys = keys;
		this.vals = vals;
	}

	void setColors(Color colorLeft, Color colorRight, Color colorBorder) {
		this.colorLeft = colorLeft;
		this.colorRight = colorRight;
		this.colorBorder = colorBorder;
	}

	void build() {
		if (format == ComponentFormat.LIST) {
			buildAsList();
		} else {
			buildAsTable();
		}
	}

	private void buildAsList() {
		int keyWidth = 0;
		int valueWidth = 0;

		// Calculate key and value max widths
		int n = keys.size();
		for (int i = 0; i < n; i++) {
			keyWidth = Math.max(keyWidth, keys.get(i).getPreferredSize().width);
			valueWidth = Math.max(valueWidth, vals.get(i).getPreferredSize().width);
		}

		int y = 0;
		vsplits = new int[n];
		for (int i = 0; i < n; i += 1) {
			JComponent key = keys.get(i);
			JComponent val = vals.get(i);
			Dimension keySize = key.getPreferredSize();
			Dimension valSize = val.getPreferredSize();
			// Calculate max height
			int h = Math.max(keySize.height, valSize.height);

			add(key);
			add(val);
			y += padding;
			key.setBounds(padding + keyWidth - keySize.width, y, keySize.width, h);
			val.setBounds((padding * 3) + keyWidth, y, valueWidth, h);
			y += h + padding;
			vsplits[i] = y;
		}

		setPreferredSize(new Dimension((padding * 4) + keyWidth + valueWidth, y));
		hsplit = keyWidth + (padding * 2);
	}

	private void buildAsTable() {
		int keyHeight = 0;
		int valueHeight = 0;

		// Calculate key and value max heights
		int n = keys.size();
		for (int i = 0; i < n; i++) {
			keyHeight = Math.max(keyHeight, keys.get(i).getPreferredSize().height);
			valueHeight = Math.max(valueHeight, vals.get(i).getPreferredSize().height);
		}

		int x = 0;
		hsplits = new int[n];
		for (int i = 0; i < n; i += 1) {
			JComponent key = keys.get(i);
			JComponent val = vals.get(i);
			Dimension keySize = key.getPreferredSize();
			Dimension valSize = val.getPreferredSize();
			// Calculate max width
			int w = Math.max(keySize.width, valSize.width);

			add(key);
			add(val);
			x += padding;
			key.setBounds(x, padding + keyHeight - keySize.height, w, keySize.height);
			val.setBounds(x, (padding * 3) + keyHeight, w, valueHeight);
			x += w + padding;
			hsplits[i] = x;
		}

		setPreferredSize(new Dimension(x, (padding * 4) + keyHeight + valueHeight));
		vsplit = keyHeight + (padding * 2);
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (format == ComponentFormat.LIST) {
			paintAsList(g);
		} else {
			paintAsTable(g);
		}
	}

	private void paintAsList(Graphics g) {
		if (colorLeft != null) {
			g.setColor(Constants.colorHeapKey);
			g.fillRect(0, 0, hsplit, getHeight());
		}

		if (colorRight != null) {
			g.setColor(Constants.colorHeapVal);
			g.fillRect(hsplit, 0, getWidth() - hsplit, getHeight());
		}

		if (colorBorder != null) {
			g.setColor(Constants.colorHeapBorder);
			g.drawLine(hsplit, 0, hsplit, getHeight() - 1);
			for (int s : vsplits) {
				g.drawLine(0, s - 1, getWidth(), s - 1);
			}
		}
	}

	private void paintAsTable(Graphics g) {
		if (colorLeft != null) {
			g.setColor(Constants.colorHeapKey);
			g.fillRect(0, 0, getWidth(), vsplit);
		}

		if (colorRight != null) {
			g.setColor(Constants.colorHeapVal);
			g.fillRect(0, vsplit, getWidth(), getHeight() - vsplit);
		}

		if (colorBorder != null) {
			g.setColor(Constants.colorHeapBorder);
			g.drawLine(0, vsplit, getWidth() - 1, vsplit);
			for (int s : hsplits) {
				g.drawLine(s - 1, 0, s - 1, getHeight());
			}
		}
	}
}
