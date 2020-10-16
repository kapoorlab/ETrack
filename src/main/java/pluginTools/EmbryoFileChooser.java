package pluginTools;

import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.BasicConfigurator;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import io.scif.img.ImgIOException;
import listeners.ChooseOrigMap;
import listeners.ChoosesecOrigMap;
import listeners.ChoosesuperProbMap;
import listeners.DoubleChannelListener;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.simplifiedio.SimplifiedIO;

public class EmbryoFileChooser extends JPanel {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	  public JFrame Cardframe = new JFrame("Deformation measuring Wizard");
	  public JPanel panelCont = new JPanel();
	  public ImagePlus impOrig, impsuper, impSec;
	  public File impAfile, impOrigfile, impsuperfile, impsecfile;
	  public JPanel panelFirst = new JPanel();
	  public JPanel Panelfile = new JPanel();
	  public JPanel Panelsuperfile = new JPanel();
	  public JPanel Panelfileoriginal = new JPanel();
	  public JPanel Paneldone = new JPanel();
	  public JPanel Panelrun = new JPanel();
	  public JPanel Microscope = new JPanel();
	  public Label inputLabelcalX, wavesize;
	  public double calibration, Wavesize;

	  public TextField inputFieldcalX, Fieldwavesize, channelAidentifier, channelBidentifier, segmentationidentifier;
	  public final Insets insets = new Insets(10, 0, 0, 0);
	  public final GridBagLayout layout = new GridBagLayout();
	  public final GridBagConstraints c = new GridBagConstraints();
	  public final String[] imageNames, blankimageNames;
	  public JComboBox<String> ChooseImage;
	  public JComboBox<String> ChoosesuperImage;
	  public JComboBox<String> ChooseoriginalImage;
	  public JComboBox<String> ChoosesecImage;
	  public JButton Done =  new JButton("Finished choosing files, launch Wizard-of-OZ");
	  public boolean twochannel = false;
	  File[] Ch1_AllMovies;
	  File[] Ch2_AllMovies;
	  File[] Seg_AllMovies;
	  public CheckboxGroup runmode = new CheckboxGroup();
	  
	  public Checkbox Godouble = new Checkbox("Load a second channel", twochannel);
	  
	  
	  
	  
	  public Border choosefile = new CompoundBorder(new TitledBorder("Probability Map chooser"),
				new EmptyBorder(c.insets));
	  public Border choosesuperfile = new CompoundBorder(new TitledBorder("Choose Segmentation Image"),
				new EmptyBorder(c.insets));
	  public Border chooseoriginalfile = new CompoundBorder(new TitledBorder("Choose original Image"),
				new EmptyBorder(c.insets));
	  public Border choosedirectory = new CompoundBorder(new TitledBorder("Choose image directories"),
				new EmptyBorder(c.insets));
	  public Border LoadEtrack = new CompoundBorder(new TitledBorder("Done Selection"),
				new EmptyBorder(c.insets));
	  public Border runmodetrack = new CompoundBorder(new TitledBorder("Runmode"),
				new EmptyBorder(c.insets));
	  public 	Border microborder = new CompoundBorder(new TitledBorder("Microscope parameters"), new EmptyBorder(c.insets));
	  public EmbryoFileChooser() {
		
		  System.setProperty("scijava.log.level", "None");
		  
		   panelFirst.setLayout(layout);
		   Panelfile.setLayout(layout);
		   Microscope.setLayout(layout);
		   Panelsuperfile.setLayout(layout);
		   Panelfileoriginal.setLayout(layout);
		   Paneldone.setLayout(layout);
		   Panelrun.setLayout(layout);
	       CardLayout cl = new CardLayout();
	       inputLabelcalX = new Label("Pixel calibration in X,Y (um)");
	       inputFieldcalX = new TextField(5);
		   inputFieldcalX.setText("1");
			
		   wavesize = new Label("Pixel calibration in T (s)");
		   Fieldwavesize = new TextField(5);
		   Fieldwavesize.setText("1");
		
		   
		    panelCont.setLayout(cl);
			panelCont.add(panelFirst, "1");
			imageNames = WindowManager.getImageTitles();
			blankimageNames = new String[imageNames.length + 1];
			blankimageNames[0] = " " ;
			
			for(int i = 0; i < imageNames.length; ++i)
				blankimageNames[i + 1] = imageNames[i];
			
			ChooseoriginalImage = new JComboBox<String>(blankimageNames);
			ChoosesecImage = new JComboBox<String>(blankimageNames);
			ChoosesuperImage = new JComboBox<String>(blankimageNames);
			calibration = Float.parseFloat(inputFieldcalX.getText());
			Wavesize = Float.parseFloat(Fieldwavesize.getText());
			
			panelFirst.add(Panelrun, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			
			Panelfileoriginal.add(Godouble,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Panelfileoriginal.add(ChooseoriginalImage, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			if(twochannel)
			Panelfileoriginal.add(ChoosesecImage,  new GridBagConstraints(3, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Panelfileoriginal.setBorder(chooseoriginalfile);
			panelFirst.add(Panelfileoriginal, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			
			panelFirst.add(Panelfile, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			Panelsuperfile.add(ChoosesuperImage, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Panelsuperfile.setBorder(choosesuperfile);
			panelFirst.add(Panelsuperfile, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			Microscope.add(inputLabelcalX, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			Microscope.add(inputFieldcalX, new GridBagConstraints(0, 1, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.RELATIVE, insets, 0, 0));
			
			Microscope.add(wavesize, new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			Microscope.add(Fieldwavesize, new GridBagConstraints(3, 1, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.RELATIVE, insets, 0, 0));
			
	
			
			Microscope.setBorder(microborder);
			panelFirst.add(Microscope, new GridBagConstraints(0, 3, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
			
			Paneldone.add(Done, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Paneldone.setBorder(LoadEtrack);
			panelFirst.add(Paneldone, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			
			
			ChooseoriginalImage.addActionListener(new ChooseOrigMap(this, ChooseoriginalImage));
			ChoosesecImage.addActionListener(new ChoosesecOrigMap(this, ChoosesecImage));
			inputFieldcalX.addTextListener(new CalXListener());
			Fieldwavesize.addTextListener(new WaveListener());
			Done.addActionListener(new DoneListener());
			ChoosesuperImage.addActionListener(new ChoosesuperProbMap(this, ChoosesuperImage));
			
			panelFirst.setVisible(true);
			cl.show(panelCont, "1");
			Cardframe.add(panelCont, "Center");
			Panelsuperfile.setEnabled(true);
			ChoosesuperImage.setEnabled(true);
		    Godouble.setEnabled(true);
			Godouble.addItemListener(new DoubleChannelListener(this));
			Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Cardframe.pack();
			Cardframe.setVisible(true);
		}
	  
	  public JButton DirA;
	  public JButton DirSeg;
	  public JButton DirB;
	  public String chAIdentifier;
	  public String chBIdentifier;
	  public String chSegIdentifier;
	  public JButton RunBatch;
	  
	 public Label LoadDirectoryA = new Label("Load channel 1 directory");	
	 public Label LoadDirectoryB = new Label("Load channel 2 directory");	
	 public Label LoadDirectorySeg = new Label("Load segmentation image directory");	
	  
	 public Label channelA = new Label("Channel 1 identifier name");
	 public Label channelB = new Label("Channel 2 identifier name");
	 public Label channelSeg = new Label("Segmentation image identifier name");
	  protected class RuninBatchListener implements ItemListener {
		  
		  
		  final EmbryoFileChooser parent;
		  
		  public RuninBatchListener(EmbryoFileChooser parent) {
			  
			  this.parent = parent;
		  }

		@Override
		public void itemStateChanged(ItemEvent e) {
			
           panelFirst.removeAll();
		   Panelfileoriginal.removeAll();	
		
		  
		  DirA = new JButton(LoadDirectoryA.getText());
		  DirB = new JButton(LoadDirectoryB.getText());
		  DirSeg = new JButton(LoadDirectorySeg.getText());
	       channelAidentifier = new TextField(5);
	       channelAidentifier.setText("C1");
			chAIdentifier = channelAidentifier.getText();
			
			
	       channelBidentifier = new TextField(5);
	       channelBidentifier.setText("C2");
	       chBIdentifier = channelBidentifier.getText();
	       segmentationidentifier = new TextField(5);
	       segmentationidentifier.setText("Cseg");
	       chSegIdentifier = segmentationidentifier.getText();
			Panelfileoriginal.add(DirA, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
			
			
			
			Panelfileoriginal.add(DirSeg, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
			Panelfileoriginal.setBorder(choosedirectory);
			
			Panelfileoriginal.add(channelA, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Panelfileoriginal.add(channelAidentifier, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
			
			Panelfileoriginal.add(channelSeg, new GridBagConstraints(3, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Panelfileoriginal.add(segmentationidentifier, new GridBagConstraints(3, 5, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			
			
			Panelfileoriginal.add(RunBatch,  new GridBagConstraints(2, 6, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			panelFirst.add(Panelfileoriginal, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
			
			channelAidentifier.addTextListener(new ChAIdentifierListener());
			channelBidentifier.addTextListener(new ChBIdentifierListener());
			segmentationidentifier.addTextListener(new ChSegIdentifierListener());
		  DirA.addActionListener(new ChannelAListener(Cardframe));
		  DirB.addActionListener(new ChannelBListener(Cardframe));
		  DirSeg.addActionListener(new ChannelSegListener(Cardframe));
		  panelFirst.repaint();
		  panelFirst.validate();
		  Panelfileoriginal.repaint();
		  Panelfileoriginal.validate();
		}
		  
		  
		  
	  }
	  
	 
	  
	  public class ChAIdentifierListener implements TextListener {

			
			
			
			@Override
			public void textValueChanged(TextEvent e) {
				final TextComponent tc = (TextComponent)e.getSource();
			    String s = tc.getText();
			   
			    if (s.length() > 0)
			    	chAIdentifier = s;
				
			}
			
	  }
	  
	  public class ChBIdentifierListener implements TextListener {

			
			
			
			@Override
			public void textValueChanged(TextEvent e) {
				final TextComponent tc = (TextComponent)e.getSource();
			    String s = tc.getText();
			   
			    if (s.length() > 0)
			    	chBIdentifier = s;
				
			}
			
	  }
	  public class ChSegIdentifierListener implements TextListener {

			
			
			
			@Override
			public void textValueChanged(TextEvent e) {
				final TextComponent tc = (TextComponent)e.getSource();
			    String s = tc.getText();
			   
			    if (s.length() > 0)
			    	chSegIdentifier = s;
				
			}
			
	  }
	  
	  JFileChooser chooserA;
	  protected class ChannelAListener implements ActionListener {

		  
		  final Frame parent;
		  
		  public ChannelAListener(Frame parent) {
			  
			  this.parent = parent;
		  }
		  
		@Override
		public void actionPerformed(ActionEvent e) {
		
			
			chooserA = new JFileChooser();
			
			if(chooserB!=null)
				chooserA.setCurrentDirectory(chooserB.getSelectedFile());
			else
			chooserA.setCurrentDirectory(new java.io.File("."));
			
			chooserA.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooserA.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "tif", "nd2");

			chooserA.setFileFilter(filter);
			chooserA.showOpenDialog(parent);
			if(chooserA.getSelectedFile()!=null)
			Ch1_AllMovies = chooserA.getSelectedFile().listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File pathname, String filename) {
					
					return filename.endsWith(".tif");
				}
			});
		}
		  
		  
		  
		  
		  
	  }
	  
	  JFileChooser chooserB;
	  protected class ChannelBListener implements ActionListener {

		  
		  final Frame parent;
		  
		  public ChannelBListener(Frame parent) {
			  
			  this.parent = parent;
		  }
		  
		@Override
		public void actionPerformed(ActionEvent e) {
		
			
			chooserB = new JFileChooser();
			
			if(chooserA!=null)
				chooserB.setCurrentDirectory(chooserA.getSelectedFile());
			else
				chooserB.setCurrentDirectory(new java.io.File("."));
			
			chooserB.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooserB.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "tif", "nd2");

			chooserB.setFileFilter(filter);
			chooserB.showOpenDialog(parent);
			if(chooserB.getSelectedFile()!=null)
			Ch2_AllMovies = chooserB.getSelectedFile().listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File pathname, String filename) {
					
					return filename.endsWith(".tif");
				}
			});
		}
		  
		  
		  
		  
		  
	  }
	  
	  
	  JFileChooser chooserSeg;
	  protected class ChannelSegListener implements ActionListener {

		  
		  final Frame parent;
		  
		  public ChannelSegListener(Frame parent) {
			  
			  this.parent = parent;
		  }
		  
		@Override
		public void actionPerformed(ActionEvent e) {
		
			
			chooserSeg = new JFileChooser();
			
			if(chooserA!=null)
				chooserSeg.setCurrentDirectory(chooserA.getSelectedFile());
			else
				chooserSeg.setCurrentDirectory(new java.io.File("."));
			
			chooserSeg.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooserSeg.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "tif", "nd2");

			chooserSeg.setFileFilter(filter);
			chooserSeg.showOpenDialog(parent);
			if(chooserSeg.getSelectedFile()!=null)
				Seg_AllMovies = chooserSeg.getSelectedFile().listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File pathname, String filename) {
					
					return filename.endsWith(".tif");
				}
			});
		}
		  
		  
		  
		  
		  
	  }
	  
	  
	  
	  public class CalXListener implements TextListener {

			
		
			
			@Override
			public void textValueChanged(TextEvent e) {
				final TextComponent tc = (TextComponent)e.getSource();
			    String s = tc.getText();
			   
			    if (s.length() > 0)
				calibration = Double.parseDouble(s);
			}
			
	  }
	  
	  public class WaveListener implements TextListener {

			
			
			
			@Override
			public void textValueChanged(TextEvent e) {
				final TextComponent tc = (TextComponent)e.getSource();
			    String s = tc.getText();
			   
			    if (s.length() > 0)
				Wavesize = Float.parseFloat(s);
				
			}
			
	  }
	  
	  public class DoneListener implements ActionListener{
		  
		  
		  @Override
			public void actionPerformed(ActionEvent e) {
			  
			  
			  try {
					BasicConfigurator.configure();
				DoneCurr(Cardframe);
			} catch (ImgIOException e1) {

				// TODO Auto-generated catch block

			
			}
		  }
		  
		  
		  
	  }
	  
	
	  public void DoneCurr(Frame parent) throws ImgIOException{
			
		
		  
			RandomAccessibleInterval<FloatType> imagebefore = SimplifiedIO.openImage(impOrig.getOriginalFileInfo().directory + impOrig.getOriginalFileInfo().fileName, new FloatType());
		
			String name = impOrig.getOriginalFileInfo().fileName;
			
			WindowManager.closeAllWindows();
			
				
			if(!twochannel) {
				// Activate curvature measurment super
			
				RandomAccessibleInterval<IntType> imagesuper = SimplifiedIO.openImage(impsuper.getOriginalFileInfo().directory + impsuper.getOriginalFileInfo().fileName, new IntType());
				new InteractiveEmbryo(imagebefore, imagesuper,calibration, Wavesize, impOrig.getOriginalFileInfo().directory, twochannel, name).run(null);
			}
			
			
			if(twochannel) {
				// Activate curvature measurment super
			
				RandomAccessibleInterval<IntType> imagesuper = SimplifiedIO.openImage(impsuper.getOriginalFileInfo().directory + impsuper.getOriginalFileInfo().fileName, new IntType());
				RandomAccessibleInterval<FloatType> secimage = SimplifiedIO.openImage(impSec.getOriginalFileInfo().directory + impSec.getOriginalFileInfo().fileName, new FloatType());
				new InteractiveEmbryo(imagebefore, secimage, imagesuper,calibration, Wavesize, impOrig.getOriginalFileInfo().directory, twochannel, name).run(null);
			}
			
			close(parent);
			
			
		}
	  protected final void close(final Frame parent) {
			if (parent != null)
				parent.dispose();

			
		}

	
	
	
}
