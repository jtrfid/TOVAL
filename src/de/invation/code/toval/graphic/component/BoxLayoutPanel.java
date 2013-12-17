package de.invation.code.toval.graphic.component;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class BoxLayoutPanel extends JPanel {

	public BoxLayoutPanel() {
		super();
		setLayout(BoxLayout.LINE_AXIS);
	}
	
	public BoxLayoutPanel(int axis) {
		super();
		setLayout(axis);
	}

	public BoxLayoutPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		setLayout(BoxLayout.LINE_AXIS);
	}
	
	public BoxLayoutPanel(boolean isDoubleBuffered, int axis) {
		super(isDoubleBuffered);
		setLayout(axis);
	}

	private void setLayout(int axis) {
		BoxLayout layout = new BoxLayout(this, axis);
		setLayout(layout);
	}
	
}
