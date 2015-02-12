package de.invation.code.toval.graphic.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.invation.code.toval.validate.Validate;

public class ColorChooserPanel extends JPanel {

	static final long serialVersionUID = 8557745082580816515L;
	
	private static final int PREFERRED_HEIGHT = 20;
	
	private JLabel lblClear;
	private ColorChooserLabel lblColor;
	private Color chosenColor = null;

	public ColorChooserPanel(ColorMode colorMode){
		this(colorMode, null);
	}
	
	public ColorChooserPanel(ColorMode colorMode, Color initialColor){
		super(new BorderLayout());
		Validate.notNull(colorMode);
		lblClear = new JLabel("X", JLabel.CENTER);
		lblClear.setPreferredSize(new Dimension(PREFERRED_HEIGHT, PREFERRED_HEIGHT));
		lblClear.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(chosenColor != null){
					lblColor.updateColor(null);
				}
			}
			
		});
		add(lblClear, BorderLayout.LINE_START);
		lblColor = new ColorChooserLabel(colorMode, initialColor);
		lblColor.setPreferredSize(new Dimension(PREFERRED_HEIGHT * 4, PREFERRED_HEIGHT));
		add(lblColor, BorderLayout.CENTER);
		setBorder(BorderFactory.createLineBorder(Color.black, 1));
	}
	
	private class ColorChooserLabel extends JLabel {

		private static final long serialVersionUID = -1319109332327149106L;
		
		private ColorMode colorMode = null;
		
		public ColorChooserLabel(ColorMode colorMode){
			Validate.notNull(colorMode);
			this.colorMode = colorMode;
			setHorizontalAlignment(JLabel.CENTER);
			setOpaque(true);
			updateColor(null);
			
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					Color color = JColorChooser.showDialog(SwingUtilities.getWindowAncestor(ColorChooserPanel.this), "Choose Color", null);
					if(color != null)
						ColorChooserLabel.this.updateColor(color);
				}
			});
		}
		
		public ColorChooserLabel(ColorMode colorMode, Color color){
			this(colorMode);
			updateColor(color);
		}
		
		public void updateColor(Color color){
			chosenColor = color;
			setOpaque(color != null);
			if(chosenColor != null){
				if(colorMode == ColorMode.HEX){
					setText(hexString(chosenColor));
				} else {
					setText(String.format("(%s,%s,%s)", chosenColor.getRed(), chosenColor.getGreen(), chosenColor.getBlue()));
				}
				setBackground(chosenColor);
			} else {
				setText("---");
			}
		}
		
		private String hexString(Color color){
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			return String.format("#%02X%02X%02X", r, g, b);
		}
		
	}
	
	public Color getChosenColor(){
		return chosenColor;
	}
	
	public enum ColorMode {
		RGB, HEX;
	}

}
