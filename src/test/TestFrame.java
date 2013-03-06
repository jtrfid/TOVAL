package test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestFrame extends JFrame {

	public TestFrame() {
		setLayout(new FlowLayout());
		final JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(100,100));
		panel.setBackground(Color.green);
		add(panel);
		JButton button = new JButton("button");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				panel.setPreferredSize(new Dimension(200,200));

				TestFrame.this.pack();
			}
			
		});
		add(button);
		pack();
		setVisible(true);
	}
	

	public static void main(String[] args) {
		new TestFrame();
	}

}
