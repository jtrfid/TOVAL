package graphic.mvc;

import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public abstract class AbstractComponent<M extends AbstractModel> extends JComponent implements PropertyChangeListener{
	
    protected M model;
    protected Dimension size;
    protected Point center;

    public AbstractComponent(M model) {
        this.model = model;
        model.addPropertyChangeListener(this);
    }
    
    public M getModel() {
    	return model;
    }
    
    public void setBounds(){
		setBounds(getLocation().x, getLocation().y, size.width, size.height);
		center = new Point(getLocation().x+size.width/2, getLocation().y+size.height/2);
	}
    
    @Override
    public void setLocation(Point p) {
       	super.setLocation(p);
       	center = new Point(getLocation().x+size.width/2, getLocation().y+size.height/2);
    }
    
    public Point getCenter() {
		return center;
	}


}