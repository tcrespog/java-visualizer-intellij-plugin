package com.aegamesi.java_visualizer.ui;

import com.aegamesi.java_visualizer.model.HeapEntity;
import com.aegamesi.java_visualizer.model.HeapList;
import com.aegamesi.java_visualizer.model.HeapMap;
import com.aegamesi.java_visualizer.model.HeapObject;
import com.aegamesi.java_visualizer.model.Value;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class HeapEntityComponent extends JPanel {
	private VisualizationPanel viz;
	private HeapEntity entity;
	private List<ValueComponent> valueComponents = new ArrayList<>();

	/**
	 * Component position on the screen
	 */
	private Point screenLocation;
	/**
	 * Component position in the relative coordinate system
	 */
	private Point componentLocation;


	HeapEntityComponent(VisualizationPanel viz, HeapEntity entity, ComponentFormat format) {
		this.viz = viz;
		this.entity = entity;

		setOpaque(false);
		setLayout(new BorderLayout());
		setMouseListeners();
		// setBorder(JBUI.Borders.empty(8));

		JLabel topLabel = new CustomJLabel(entity.label);
		topLabel.setFont(Constants.fontUISmall);
		topLabel.setForeground(Constants.colorHeapLabel);
		add(topLabel, BorderLayout.NORTH);

		JPanel mainPanel = null;
		if (entity instanceof HeapObject) {
			mainPanel = new PanelObject((HeapObject) entity, format);
		} else if (entity instanceof HeapList) {
			mainPanel = new PanelList((HeapList) entity);
		} else if (entity instanceof HeapMap) {
			mainPanel = new PanelMap((HeapMap) entity, format);
		}

		if (mainPanel != null) {
			add(mainPanel, BorderLayout.WEST);
		}
	}

	private void setMouseListeners() {
		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				// When the mouse is first pressed, save the original positions
				screenLocation = e.getLocationOnScreen();
				componentLocation = getLocation();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				// When the mouse is released, compute the pointer paths and repaint the panel to adjust the pointer positions
				viz.computePointerPaths();
				viz.repaint();
			}
		});

		addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// Compute how much the component has been moved
				int deltaX = e.getXOnScreen() - screenLocation.x;
				int deltaY = e.getYOnScreen() - screenLocation.y;

				// Compute the new position based on the original position
				int newX = componentLocation.x + deltaX;
				int newY = componentLocation.y + deltaY;
				// Set the new position
				setLocation(newX, newY);
			}
		});
	}

	HeapEntity getEntity() {
		return entity;
	}

	List<ValueComponent> getValueComponents() {
		return valueComponents;
	}

	private class PanelObject extends KVComponent {
		PanelObject(HeapObject e, ComponentFormat format) {
			super(format);

			List<JLabel> keys = new ArrayList<>();
			List<ValueComponent> vals = new ArrayList<>();
			for (Map.Entry<String, Value> local : e.fields.entrySet()) {
				JLabel key = new CustomJLabel(local.getKey(), JLabel.RIGHT);
				key.setFont(Constants.fontUI);
				key.setForeground(Constants.colorText);

				ValueComponent val = new ValueComponent(viz, local.getValue());
				valueComponents.add(val);
				keys.add(key);
				vals.add(val);
			}

			setColors(Constants.colorHeapKey, Constants.colorHeapVal, Constants.colorHeapBorder);
			setPadding(Constants.padHeapMap);
			setComponents(keys, vals);
			build();
		}
	}

	private class PanelMap extends KVComponent {
		PanelMap(HeapMap e, ComponentFormat format) {
			super(format);

			List<ValueComponent> keys = new ArrayList<>();
			List<ValueComponent> vals = new ArrayList<>();
			for (HeapMap.Pair entry : e.pairs) {
				ValueComponent key = new ValueComponent(viz, entry.key);
				ValueComponent val = new ValueComponent(viz, entry.val);
				valueComponents.add(val);
				valueComponents.add(val);
				keys.add(key);
				vals.add(val);
			}

			setColors(Constants.colorHeapKey, Constants.colorHeapVal, Constants.colorHeapBorder);
			setPadding(Constants.padHeapMap);
			setComponents(keys, vals);
			build();
		}
	}

	private class PanelList extends JPanel {
		private int[] splits;

		PanelList(HeapList e) {
			setBackground(Constants.colorHeapVal);
			setLayout(null);
			splits = new int[e.items.size()];

			int height = 0;
			int x = 0;
			for (int i = 0; i < e.items.size(); i++) {
				splits[i] = x;
				ValueComponent value = new ValueComponent(viz, e.items.get(i));
				valueComponents.add(value);
				Dimension size = value.getPreferredSize();
				JLabel indexLabel = new CustomJLabel(Integer.toString(i));
				indexLabel.setFont(Constants.fontUISmall);
				indexLabel.setForeground(Constants.colorHeapLabel);
				Dimension indexSize = indexLabel.getPreferredSize();
				indexLabel.setBounds(x + 4, 4, indexSize.width, indexSize.height);
				add(indexLabel);
				x += 8;
				value.setBounds(x, 4 + indexSize.height + 4, size.width, size.height);
				x += size.width + 8;
				add(value);
				height = Math.max(height, indexSize.height + size.height);
			}
			height += 8 + 8;
			setPreferredSize(new Dimension(x, height));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			g.setColor(Constants.colorHeapBorder);
			g.drawLine(1, getHeight() - 1, getWidth(), getHeight() - 1);
			for (int s : splits) {
				g.drawLine(s + 1, 0, s + 1, getHeight() - 1);
			}
		}
	}
}
