package com.aegamesi.java_visualizer.ui;

import com.aegamesi.java_visualizer.model.ExecutionTrace;
import com.aegamesi.java_visualizer.model.Frame;
import com.aegamesi.java_visualizer.model.HeapEntity;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JVPanel extends JPanel {
	private ExecutionTrace trace = null;

	public JVPanel() {
		setBackground(Constants.colorBackground);
		// setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

	}

	public void setTrace(ExecutionTrace t) {
		this.trace = t;
		removeAll();

		Box boxStackFrames = Box.createVerticalBox();
		JLabel frameLabel = new JLabel("Frames");
		frameLabel.setFont(Constants.fontMessage);
		frameLabel.setHorizontalAlignment(JLabel.RIGHT);
		boxStackFrames.add(frameLabel);
		boxStackFrames.add(Box.createVerticalStrut(24));
		for (Frame f : trace.frames) {
			boxStackFrames.add(new StackFrameComponent(f));
			boxStackFrames.add(Box.createVerticalStrut(8));
		}
		add(boxStackFrames);

		Box boxHeap = Box.createVerticalBox();
		for (HeapEntity e : trace.heap.values()) {
			boxHeap.add(new HeapEntityComponent(e));
			boxHeap.add(Box.createVerticalStrut(8));
		}
		add(boxHeap);

		revalidate();
		repaint();
	}
}
