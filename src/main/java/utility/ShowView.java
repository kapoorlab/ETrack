package utility;

import ij.IJ;
import pluginTools.InteractiveEmbryo;

public class ShowView {

	
	final InteractiveEmbryo parent;
	
	
	public ShowView(final InteractiveEmbryo parent) {
		
		this.parent = parent;
		
	}
	
	
	public void shownewZ() {

		if (parent.thirdDimension > parent.thirdDimensionSize) {
			IJ.log("Max T stack exceeded, moving to last T instead");
			parent.thirdDimension = parent.thirdDimensionSize;
			
			
			parent.CurrentView = utility.Slicer.getCurrentView(parent.originalimg, (int)parent.thirdDimension,
					(int)parent.thirdDimensionSize);
			
		} else {

			parent.CurrentView = utility.Slicer.getCurrentView(parent.originalimg, (int)parent.thirdDimension,
					(int)parent.thirdDimensionSize);
			
		}

		
	}

	
	
	public void shownewT() {

		if (parent.thirdDimension > parent.thirdDimensionSize) {
			IJ.log("Max time point exceeded, moving to last time point instead");
			parent.thirdDimension = parent.thirdDimensionSize;
			
			
			parent.CurrentView = utility.Slicer.getCurrentView(parent.originalimg,(int) parent.thirdDimension,
					(int)parent.thirdDimensionSize);
			
		} else {

			parent.CurrentView = utility.Slicer.getCurrentView(parent.originalimg,(int) parent.thirdDimension,
					(int)parent.thirdDimensionSize);
			
		}

		
		
	

		
	}
	
}
