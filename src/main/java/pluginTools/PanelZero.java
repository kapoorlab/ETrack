package pluginTools;

import javax.swing.JFrame;
import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;

public class PanelZero {

	public static void main(String[] args) {

		new ImageJ();
		JFrame frame = new JFrame("");

		ImagePlus impB = new Opener()
				.openImage("/Users/aimachine/Documents/OzEtrack/Raw.tif");
		impB.show();

		ImagePlus impA = new Opener()
				.openImage("/Users/aimachine/Documents/OzEtrack/Mask.tif");
		impA.show();

		EmbryoFileChooser panel = new EmbryoFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());

	}

}
