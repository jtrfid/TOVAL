package de.invation.code.toval.graphic;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;



@SuppressWarnings("serial")
public class DisplayFrame extends JFrame {
	
	private static final Dimension defaultDimension = new Dimension(400, 400);
	
	public DisplayFrame(JPanel content, boolean adjustDimensionToContent) {
		if(content == null)
			throw new NullPointerException();
		if(!adjustDimensionToContent){
			setSize(defaultDimension);
		}
		setLocationRelativeTo(null);
		JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		if(!adjustDimensionToContent){
			scroll.setPreferredSize(defaultDimension);
		}
		scroll.setViewportView(content);
		setContentPane(scroll);	
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
	}

}
