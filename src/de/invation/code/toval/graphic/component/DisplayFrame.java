package de.invation.code.toval.graphic.component;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import de.invation.code.toval.validate.Validate;

public class DisplayFrame extends JFrame {
	
	private static final long serialVersionUID = 9056067604570382656L;
	
	private static final Dimension defaultDimension = new Dimension(400, 400);
	
	public DisplayFrame(JPanel content, boolean adjustDimensionToContent) {
		this(content, adjustDimensionToContent, false);
	}
	
	public DisplayFrame(JPanel content, boolean adjustDimensionToContent, final boolean exitOnDispose) {
		Validate.notNull(content);
		
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {}
			
			@Override
			public void windowIconified(WindowEvent e) {}
			
			@Override
			public void windowDeiconified(WindowEvent e) {}
			
			@Override
			public void windowDeactivated(WindowEvent e) {}
			
			@Override
			public void windowClosing(WindowEvent e) {}
			
			@Override
			public void windowClosed(WindowEvent e) {
				if(exitOnDispose)
					System.exit(0);
			}
			
			@Override
			public void windowActivated(WindowEvent e) {}
		});
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
