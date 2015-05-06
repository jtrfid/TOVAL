package de.invation.code.toval.graphic.component;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import de.invation.code.toval.icons.IconFactory;
import de.invation.code.toval.thread.ExecutorListener;
import de.invation.code.toval.thread.SingleThreadExecutorService;
import de.invation.code.toval.validate.Validate;

public class ExecutorLabel extends JLabel implements ExecutorListener {
	
	private static final long serialVersionUID = 8368680392887232591L;
	private static final Dimension DEFAULT_SIZE = new Dimension(40,40);
	public static final Color COLOR_INITIAL = Color.BLUE;
	public static final Color COLOR_DONE = Color.GREEN;
	public static final Color COLOR_CANCELLED = Color.RED;
	
	protected SingleThreadExecutorService executorService = null;
	
	private boolean running = false;
	
	public ExecutorLabel(){
		super();
		
		setPreferredSize(DEFAULT_SIZE);
		setMinimumSize(DEFAULT_SIZE);
		setMaximumSize(DEFAULT_SIZE);
		setOpaque(true);
		setBackground(COLOR_INITIAL);
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(!running){
					// Start executor service
					try {
						startExecutor();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(ExecutorLabel.this), "Exception while starting execution.\nReason: " + e1.getMessage(), "Execution exception", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					// Stop executor service
					try {
						stopExecutor();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(ExecutorLabel.this), "Exception while stopping execution.\nReason: " + e1.getMessage(), "Execution exception", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
	}

	public ExecutorLabel(SingleThreadExecutorService executorService){
		this();
		setExecutor(executorService);
	}
	
	public void setExecutor(SingleThreadExecutorService executorService){
		Validate.notNull(executorService);
		executorService.addListener(this);
		this.executorService = executorService;
	}
	
	protected synchronized void startExecutor() throws Exception{
		executorService.setUpAndRun();
	}
	
	protected void setGraphicsRunning() {
		setOpaque(false);
		setIcon(getLoadingDotsIcon());
		if(executorService.isDone())
			setGraphicsFinished();
	}
	
	protected synchronized void stopExecutor() throws Exception{
		executorService.stop();
	}
	
	protected void setGraphicsCancelled() {
		setOpaque(true);
		setIcon(null);
		setBackground(COLOR_CANCELLED);
	}
	
	protected void setGraphicsFinished() {
		setOpaque(true);
		setIcon(null);
		setBackground(COLOR_DONE);
	}

	static ImageIcon getLoadingDotsIcon() {
		String path = "loading-dots.gif";
		URL imageURL = IconFactory.class.getResource(path);
		return new ImageIcon(imageURL);
	}

	@Override
	public void executorStarted() {
		running = true;
		setGraphicsRunning();
	}

	@Override
	public void executorStopped() {
		running = false;
		setGraphicsCancelled();
	}

	@Override
	public void executorFinished() {
		running = false;
		setGraphicsFinished();
	}

}
