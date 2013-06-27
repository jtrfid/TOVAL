package de.invation.code.toval.test;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
 
public class JScrollRevalidateView {
    private JPanel panel;
    
    private void start() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(50, 50, 100, 100);
        frame.getContentPane().add(new JScrollPane(panel = new JPanel()));
        frame.setVisible(true);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(2000);
                    panel.setPreferredSize(new Dimension(500, 500));
                    panel.revalidate();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    public static void main(String[] args) {
        new JScrollRevalidateView().start();
    }
}