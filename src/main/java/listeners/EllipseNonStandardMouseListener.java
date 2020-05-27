package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import pluginTools.InteractiveEmbryo;
import pluginTools.InteractiveEmbryo.ValueChange;



public class EllipseNonStandardMouseListener implements MouseMotionListener
{
	final InteractiveEmbryo parent;
	final ValueChange change;

	public EllipseNonStandardMouseListener( final InteractiveEmbryo parent, final ValueChange change )
	{
		this.parent = parent;
		this.change = change;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
  
		

		parent.updatePreview(change);
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		
	}
	
}
