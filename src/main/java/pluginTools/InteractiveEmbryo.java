package pluginTools;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.scijava.plugin.Parameter;
import org.scijava.ui.UIService;

import bdv.util.BdvOverlay;
import bdv.util.BdvSource;
import comboSliderTextbox.SliderBoxGUI;
import curvatureComputer.ComputeCurvatureCurrent;
import embryoDetector.Curvatureobject;
import embryoDetector.Embryoobject;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ColorProcessor;
import kalmanGUI.CovistoKalmanPanel;
import listeners.AutoEndListener;
import listeners.AutoStartListener;
import listeners.CirclemodeListener;
import listeners.ClearDisplayListener;
import listeners.CombomodeListener;
import listeners.CurrentCurvatureListener;
import listeners.CurvatureListener;
import listeners.DisplayVisualListener;
import listeners.DistancemodeListener;
import listeners.ETrackFilenameListener;
import listeners.LinescanradiusListener;
import listeners.MinSegDistListener;
import listeners.MinSegDistLocListener;
import listeners.RegionInteriorListener;
import listeners.ResolutionListener;
import listeners.SaverAllListener;
import listeners.SaveDirectory;
import listeners.SaverListener;
import listeners.TimeListener;
import listeners.TlocListener;
import listeners.TrackidListener;
import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.InteractiveEmbryo.ValueChange;
import utility.Roiobject;

public class InteractiveEmbryo extends JPanel implements PlugIn {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	public String usefolder = IJ.getDirectory("imagej");
	public String addToName = "ETrack_";
	public final int scrollbarSize = 1000;
	public String inputstring;
	public double minellipsepoints = 9;
	public double mincirclepoints = 3;
	public int tablesize;
	public double smoothing = 0;
	public ConcurrentHashMap<Integer, List<RealLocalizable>> Listmap = new ConcurrentHashMap<Integer, List<RealLocalizable>>();
	public HashMap<String, Integer> CellLabelsizemap = new HashMap<String, Integer>();
	public Overlay overlay, clockoverlay;
	public int numSeg = 1;
	public ImageJ ij; 
	public Overlay emptyoverlay;
	public int thirdDimensionslider = 1;
	public int thirdDimensionsliderInit = 1;
	public int rowchoice;
	public String selectedID;
	public HashMap<String, ArrayList<Pair<Integer, Double>>> StripList = new HashMap<String, ArrayList<Pair<Integer, Double>>>();
	public ImagePlus RMStrackImages;
	// Distance between segments
	public int minSegDist = 60;
	public int maxSegDist = 600;
	public int depth = 4;
	public int maxsize = 100;
	public int minsize = 10;


	public ImagePlus clockimp;
	public boolean circlefits = false;
	public boolean distancemethod = false;
	public boolean combomethod = true;

	public RealLocalizable globalMaxcord;


	public int KymoDimension = 0;
	public int AutostartTime, AutoendTime;
	public float insideCutoffmax = 500;
	public float outsideCutoffmax = 500;
	public int roiindex;
	public int thirdDimension;
	public int thirdDimensionSize;
	public ImagePlus impA;
	public boolean isDone;
	public int MIN_SLIDER = 0;
	public int MAX_SLIDER = 500;
	public int row;
	public HashMap<String, Integer> Accountedframes;
	public JProgressBar jpb;
	public JLabel label = new JLabel("Fitting..");
	public int Progressmin = 0;
	public int Progressmax = 100;
	public int max = Progressmax;
	public File userfile;
	public File saveFile;

	public NumberFormat nf;

	public double displaymin, displaymax;
	public boolean batchmode = false;
	public JFreeChart contchart;
	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<FloatType> Secoriginalimg;
	public RandomAccessibleInterval<IntType> Segoriginalimg;
	public ArrayList<ValuePair<String, Embryoobject>> EmbryoTracklist;
	public File inputfile;
	public String inputdirectory;
	public float radius = 50f;
	public int strokewidth = 1;
	public MouseMotionListener ml;
	public MouseListener mvl;
	public Roi nearestRoiCurr;
	public OvalRoi nearestIntersectionRoiCurr;
	public Roi selectedRoi;
	public TextField inputFieldIter;
	public JTable table;
	public ArrayList<Roi> Allrois;
	public ImagePlus imp;
	public int ndims;
	public MouseListener ovalml;
	public double calibration;
	public double timecal;
	public int regiondistance = 10;
	public int[] boundarypoint;
	public int[] midpoint;
	public int maxSearchradius = 100;
	public int maxSearchradiusS = 10;
	public int missedframes = 200;
	public HashMap<String, Integer> AccountedT;
	public int increment = 0;
	public int resolution = 1;
	public int linescanradius = 0;
	public int maxSearchradiusInit = (int) maxSearchradius;
	public float maxSearchradiusMin = 1;
	public float maxSearchradiusMax = maxSearchradius;
	public float maxSearchradiusMinS = 1;
	public float maxSearchradiusMaxS = maxSearchradius;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public RandomAccessibleInterval<FloatType> CurrentViewSecOrig;
	public int maxlabel;
	public Color colorLineA = Color.YELLOW;
	public Color colorLineB = Color.YELLOW;
	public Color colorPoints = Color.RED;
	public double maxdistance = 10;
	public ImageStack prestack;
	public MouseAdapter mouseadapter;
	public int[] Clickedpoints;
	public int starttime;
	public int endtime;
	public HashMap<String, Roiobject> ZTRois;
	public ArrayList<RealLocalizable> AllEmbryocenter;
	public ArrayList<RealLocalizable> ChosenEmbryocenter;
	public HashMap<String, RealLocalizable> SelectedAllRefcords;
	public HashMap<String, ArrayList<Curvatureobject>> AllEmbryos;
	public HashMap<String, Integer> EmbryoLastTime;
	public ArrayList<OvalRoi> EmbryoOvalRois;
	public ArrayList<Pair<String, double[]>> resultAngle;
	public ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>> resultCurvature;
	public ArrayList<Pair<String, Pair<Integer, List<RealLocalizable>>>> SubresultCurvature;

	public ArrayList<Pair<String, Pair<Integer, Double>>> resultSegCurvature;
	public ArrayList<Pair<String, Pair<Integer, Double>>> resultSegIntensityA;
	public ArrayList<Pair<String, Pair<Integer, Double>>> resultSegIntensityB;
	public ArrayList<Pair<String, Pair<Integer, Double>>> resultSegPerimeter;
	public HashMap<String, Pair<ArrayList<double[]>, ArrayList<Line>>> resultDraw;
	public HashMap<String, ArrayList<Line>> resultDrawLine;
	public KeyListener kl;
	public HashMap<Integer, ArrayList<double[]>> HashresultCurvature;
	public HashMap<Integer, List<RealLocalizable>> SubHashresultCurvature;
	public HashMap<Integer, Double> HashresultSegCurvature;
	public HashMap<Integer, Double> HashresultSegIntensityA;
	public HashMap<Integer, Double> HashresultSegIntensityB;
	public HashMap<Integer, Double> HashresultSegPerimeter;
	public Set<Integer> pixellist;
	public HashMap<String, Curvatureobject> Finalcurvatureresult;
	public boolean isCreated = false;
	public RoiManager roimanager;
	public String uniqueID, TID;
	public RealLocalizable Refcord;
	public HashMap<String, RealLocalizable> AllRefcords;
	public int mindistance = 200;
	public int maxperi = Integer.MIN_VALUE;
	public int minSizeInit = 50;
	public int maxSizeInit = 500;
	public int maxSearchInit = 1000;
	public int maxframegap = 10;
	public boolean twochannel;
	public int insidedistance = 10;
	public static enum ValueChange {
		
		THIRDDIMmouse, All;
		
	}
	
	public void setTime(final int value) {
		thirdDimensionslider = value;
		thirdDimensionsliderInit = 1;
		thirdDimension = 1;
	}
	
	
	public int getTimeMax() {

		return thirdDimensionSize;
	}

	public InteractiveEmbryo(RandomAccessibleInterval<FloatType> originalimg,
			RandomAccessibleInterval<IntType> Segoriginalimg,
			final double calibration, final double timecal, String inputdirectory,
			boolean twochannel, String inputstring) {
		this.inputfile = null;
		this.inputdirectory = inputdirectory;
		this.originalimg = originalimg;
		this.Segoriginalimg = Segoriginalimg;
		this.ndims = originalimg.numDimensions();
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		nf.setGroupingUsed(false);
		this.calibration = calibration;
		this.timecal = timecal;
		this.twochannel = twochannel;
		this.inputstring = inputstring;
	}

	public InteractiveEmbryo(RandomAccessibleInterval<FloatType> originalimg,
			RandomAccessibleInterval<FloatType> Secoriginalimg, RandomAccessibleInterval<IntType> Segoriginalimg,
			final double calibration, final double timecal, String inputdirectory,
			boolean twochannel, String inputstring) {
		this.inputfile = null;
		this.inputdirectory = inputdirectory;
		this.originalimg = originalimg;
		this.Secoriginalimg = Secoriginalimg;
		this.Segoriginalimg = Segoriginalimg;
		this.ndims = originalimg.numDimensions();
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		nf.setGroupingUsed(false);
		this.calibration = calibration;
		this.timecal = timecal;
		this.twochannel = twochannel;
		this.inputstring = inputstring;
	}





	public void run(String arg0) {

		EmbryoLastTime = new HashMap<String, Integer>();
		AllRefcords = new HashMap<String, RealLocalizable>();
		AllEmbryocenter = new ArrayList<RealLocalizable>();
		ChosenEmbryocenter = new ArrayList<RealLocalizable>();
		EmbryoOvalRois = new ArrayList<OvalRoi>();
		SelectedAllRefcords = new HashMap<String, RealLocalizable>();
		ZTRois = new HashMap<String, Roiobject>();
		AccountedT = new HashMap<String, Integer>();
		jpb = new JProgressBar();
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		nf.setGroupingUsed(false);
		Clickedpoints = new int[2];
		pixellist = new HashSet<Integer>();
		EmbryoTracklist = new ArrayList<ValuePair<String, Embryoobject>>();
		AllEmbryos = new HashMap<String, ArrayList<Curvatureobject>>();
		Finalcurvatureresult = new HashMap<String, Curvatureobject>();
		ij = new ImageJ();
		ij.ui().showUI();
		if (ndims == 3) {

			thirdDimension = 1;

			thirdDimensionSize = (int) originalimg.dimension(2);
			AutostartTime = thirdDimension;
			AutoendTime = thirdDimensionSize;
			maxframegap = thirdDimensionSize / 4;
		}
		setTime(thirdDimension);
		CurrentView = utility.EmbryoSlicer.getCurrentEmbryoView(originalimg, thirdDimension, thirdDimensionSize);

		imp = ImageJFunctions.show(CurrentView, "Original Image");
		imp.setTitle("Active Image" + " " + "time point : " + thirdDimension);
	
		clockimp = ImageJFunctions.show(CurrentView, "Wizard Clock");
		clockimp.setTitle("Wizard Clock" );
		Cardframe.repaint();
		Cardframe.validate();
		panelFirst.repaint();
		panelFirst.validate();
		saveFile = new java.io.File(".");
		
		
		Card();
		updatePreview(ValueChange.THIRDDIMmouse);
		StartDisplayer();

	}

	


	public void repaintView(ImagePlus Activeimp, RandomAccessibleInterval<FloatType> Activeimage) {
		if (Activeimp == null || !Activeimp.isVisible()) {
			Activeimp = ImageJFunctions.show(Activeimage);

		}

		else {

			final float[] pixels = (float[]) Activeimp.getProcessor().getPixels();
			final Cursor<FloatType> c = Views.iterable(Activeimage).cursor();

			for (int i = 0; i < pixels.length; ++i)
				pixels[i] = c.next().get();

			Activeimp.updateAndDraw();

		}

	}

	public void updatePreview(final ValueChange change) {
		
		
      
		if (overlay == null) {

			overlay = new Overlay();
			imp.setOverlay(overlay);
			
		}
		
		if (clockoverlay == null) {

			clockoverlay = new Overlay();
			clockimp.setOverlay(clockoverlay);
			
		}
		
		if (change == ValueChange.THIRDDIMmouse)
		{
			
			
			imp.setTitle("Active Image" + " " + "time point : " + thirdDimension);
			String TID = Integer.toString( thirdDimension);
			AccountedT.put(TID,  thirdDimension);
			CurrentView = utility.EmbryoSlicer.getCurrentEmbryoView(originalimg, thirdDimension, thirdDimensionSize);
		repaintView(CurrentView);
		if(Curvaturebutton.isEnabled()) {
			imp.getOverlay().clear();
			imp.updateAndDraw();
			StartDisplayer();
			
		}

		}
	}

	

	

	public void StartDisplayer() {
		
		clockoverlay.clear();
		clockimp.updateAndDraw();
		ComputeCurvatureCurrent display = new ComputeCurvatureCurrent(this, jpb);


		display.execute();

	}
	
public void repaintView( RandomAccessibleInterval<FloatType> Activeimage) {
		
		
		
		if (imp == null || !imp.isVisible()) {
			imp = ImageJFunctions.show(Activeimage);

		}

		else {
		
				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				
				final Cursor<FloatType> c = Views.iterable(Activeimage).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

		}
			
			imp.updateAndDraw();

		}

	

	

	public JFrame Cardframe = new JFrame("Embryo Deformation Measuring Tool");
	public JPanel panelCont = new JPanel();
	public JPanel panelFirst = new JPanel();
	public JPanel panelSecond = new JPanel();
	public JPanel Timeselect = new JPanel();
	public JPanel Roiselect = new JPanel();
	public JPanel Curvatureselect = new JPanel();
	public JPanel KalmanPanel = new JPanel();

	public TextField inputFieldT, inputtrackField, minperimeterField, maxperimeterField, gaussfield, numsegField,
			cutoffField, minSegDistField, degreeField, secdegreeField;//, resolutionField;
			//radiusField,  SpecialminInlierField;

	public TextField inputFieldZ, startT, endT, maxSizeField, minSizeField;
	public TextField inputFieldmaxtry, interiorfield, exteriorfield, regioninteriorfield;
	public TextField inputFieldminpercent;
	public TextField inputFieldmaxellipse, backField;

	public Label inputLabelmaxellipse;
	public Label inputLabelminpercent, backLabel;
	public Label inputLabelIter, inputtrackLabel, inputcellLabel;
	public JPanel Original = new JPanel();
	public int SizeX = 500;
	public int SizeY = 500;

	public int smallSizeX = 200;
	public int smallSizeY = 200;

	public JButton Curvaturebutton = new JButton("Measure Local Curvature");
	public JButton Displaybutton = new JButton("Display Visuals (time)");
	public JButton Savebutton = new JButton("Save Track");
	public JButton Batchbutton = new JButton("Save Parameters for batch mode and exit");
	public JButton SaveAllbutton = new JButton("Save All Tracks");

	public String timestring = "Current T";
	public String rstring = "Radius";
	public String minSegDiststring = "Box Size(um)";

	public Label timeText = new Label("Current T = " + 1, Label.CENTER);
	public Label resolutionText = new Label("Measurement Resolution (px)");
	public Label regionText = new Label("Intensity region (px)");
	public Label outdistText = new Label("Intensity Exterior region (px)");

	public Label minSegDistText = new Label(minSegDiststring + " = " + minSegDist, Label.CENTER);



	public final Insets insets = new Insets(10, 0, 0, 0);
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();

	public JScrollPane scrollPane;
	public JFileChooser chooserA = new JFileChooser();
	public String choosertitleA;
	public JScrollBar timeslider = new JScrollBar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
			scrollbarSize + 10);
	public JScrollBar minSegDistslider= new JScrollBar(Scrollbar.HORIZONTAL, minSegDist, 10, 0,
			scrollbarSize + 10);

	public JPanel PanelSelectFile = new JPanel();
	public JPanel PanelBatch = new JPanel();
	public Border selectfile = new CompoundBorder(new TitledBorder("Select Track"), new EmptyBorder(c.insets));
	public Border selectcell = new CompoundBorder(new TitledBorder("Select Cell"), new EmptyBorder(c.insets));
	public JLabel inputLabel = new JLabel("Filename:");
	public TextField inputField = new TextField();
	public final JButton ChooseDirectory = new JButton("Choose Directory to save results in");
	public JComboBox<String> ChooseMethod;
	public JComboBox<String> ChooseColor;
	public Label lostlabel, autoTstart, autoTend, blackcorrectionlabel;
	public TextField lostframe, bordercorrection;
	public Border origborder = new CompoundBorder(new TitledBorder("Enter filename for results files"),
			new EmptyBorder(c.insets));
	public JPanel controlprev = new JPanel();
	JPanel controlnext = new JPanel();
	CheckboxGroup curvaturemode = new CheckboxGroup();


	final Checkbox circlemode = new Checkbox("Use Circle Fits", curvaturemode, circlefits);
	public final Checkbox distancemode = new Checkbox("Use Distance Method", curvaturemode, distancemethod);
	
	public final Checkbox Combomode = new Checkbox("Use Combo Circle-Distance Method", curvaturemode, combomethod);

	public boolean displayIntermediate = true;
	public boolean displayIntermediateBox = true;
	public JButton ClearDisplay = new JButton("Clear Display");

	public Border timeborder = new CompoundBorder(new TitledBorder("Select time"), new EmptyBorder(c.insets));

	public Border circletools = new CompoundBorder(new TitledBorder("Curvature computer"), new EmptyBorder(c.insets));



	int textwidth = 5;

	public void Card() {

		minSegDistText = new Label(minSegDiststring + " = " + minSegDist, Label.CENTER);
		lostlabel = new Label("Number of frames for loosing the track");
		lostframe = new TextField(1);
		lostframe.setText(Integer.toString(maxframegap));


		autoTstart = new Label("Start time for automation");
		startT = new TextField(textwidth);
		startT.setText(Integer.toString(AutostartTime));

		autoTend = new Label("End time for automation");
		endT = new TextField(textwidth);
		endT.setText(Integer.toString(AutoendTime));

		CardLayout cl = new CardLayout();

		c.insets = new Insets(5, 5, 5, 5);
		panelCont.setLayout(cl);

		panelCont.add(panelFirst, "1");
		panelCont.add(panelSecond, "2");
		panelFirst.setName("Angle Tool for ellipsoids");

		panelFirst.setLayout(layout);

		panelSecond.setLayout(layout);

		Timeselect.setLayout(layout);
		controlprev.setLayout(layout);
		controlnext.setLayout(layout);
		Original.setLayout(layout);
		Roiselect.setLayout(layout);
		Curvatureselect.setLayout(layout);
		KalmanPanel.setLayout(layout);
		inputFieldT = new TextField(textwidth);
		inputFieldT.setText(Integer.toString(thirdDimension));


		minSegDistField = new TextField(textwidth);
		minSegDistField.setText(Integer.toString(minSegDist));


		inputtrackField = new TextField(textwidth);
		regioninteriorfield = new TextField(textwidth);
		regioninteriorfield.setText(Integer.toString(regiondistance));

		maxSizeField = new TextField(textwidth);
		maxSizeField.setText(Integer.toString(maxsize));

		minSizeField = new TextField(textwidth);
		minSizeField.setText(Integer.toString(minsize));

		numsegField = new TextField(textwidth);
		numsegField.setText(Integer.toString(depth));

		inputLabelIter = new Label("Max. attempts to find ellipses");
		final JScrollBar maxSearchS = new JScrollBar(Scrollbar.HORIZONTAL, maxSearchInit, 10, 0, 10 + scrollbarSize);


		maxSearchradius = (int) utility.ETrackScrollbarUtils.computeValueFromScrollbarPosition(maxSearchS.getValue(),
				maxSearchradiusMin, maxSearchradiusMax, scrollbarSize);




		inputtrackLabel = new Label("Enter trackID to save");
		inputcellLabel = new Label("Enter CellLabel to save");

		Object[] colnames;
		Object[][] rowvalues;

		colnames = new Object[] { "Track Id", "Location X", "Location Y", "Location T", "Perimeter" };

		rowvalues = new Object[0][colnames.length];

	
		if (Finalcurvatureresult != null && Finalcurvatureresult.size() > 0) {

			rowvalues = new Object[Finalcurvatureresult.size()][colnames.length];

		}

		table = new JTable(rowvalues, colnames);

		c.anchor = GridBagConstraints.BOTH;
		c.ipadx = 35;

		c.gridwidth = 10;
		c.gridheight = 10;
		c.gridy = 1;
		c.gridx = 0;

		// Put time slider

		Timeselect.add(timeText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Timeselect.add(timeslider, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Timeselect.add(inputFieldT, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Timeselect.setBorder(timeborder);
		Timeselect.add(timeText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
		panelFirst.add(Timeselect, new GridBagConstraints(0, 0, 5, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));



	

	

				SliderBoxGUI combominInlier = new SliderBoxGUI(minSegDiststring, minSegDistslider, minSegDistField,
						minSegDistText, scrollbarSize, minSegDist, maxSegDist);

				Curvatureselect.add(distancemode, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				Curvatureselect.add(circlemode, new GridBagConstraints(2, 0, 2, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

				
				Curvatureselect.add(Combomode, new GridBagConstraints(2, 2, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));

				Curvatureselect.add(combominInlier.BuildDisplay(), new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));


				Curvatureselect.add(regionText, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));

				Curvatureselect.add(regioninteriorfield, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

				Curvatureselect.add(Curvaturebutton, new GridBagConstraints(2, 4, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));


				Curvatureselect.add(Displaybutton, new GridBagConstraints(2, 5, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));

				Curvatureselect.setBorder(circletools);
				panelFirst.add(Curvatureselect, new GridBagConstraints(0, 1, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));



		
		controlprev.add(new JButton(new AbstractAction("\u22b2Prev") {

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) panelCont.getLayout();
				cl.previous(panelCont);
			}
		}));

		controlnext.add(new JButton(new AbstractAction("Next\u22b3") {

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) panelCont.getLayout();
				cl.next(panelCont);
			}
		}));

		panelSecond.add(controlprev, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));

		

		table.setFillsViewportHeight(true);

		scrollPane = new JScrollPane(table);

		scrollPane.getViewport().add(table);
		scrollPane.setAutoscrolls(true);

		PanelSelectFile.add(scrollPane, BorderLayout.CENTER);

		PanelSelectFile.setBorder(selectfile);
		int size = 100;
		table.getColumnModel().getColumn(0).setPreferredWidth(size);
		table.getColumnModel().getColumn(1).setPreferredWidth(size);
		table.getColumnModel().getColumn(2).setPreferredWidth(size);
		table.getColumnModel().getColumn(3).setPreferredWidth(size);
		table.getColumnModel().getColumn(4).setPreferredWidth(size);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);

		Original.add(inputLabel, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.add(inputField, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.add(inputtrackLabel, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.add(inputtrackField, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.add(ChooseDirectory, new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Original.add(Savebutton, new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.add(SaveAllbutton, new GridBagConstraints(0, 9, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.setBorder(origborder);

		inputField.setEnabled(false);
		inputtrackField.setEnabled(false);
		ChooseDirectory.setEnabled(false);
		Savebutton.setEnabled(false);
		SaveAllbutton.setEnabled(false);
		Batchbutton.setEnabled(false);
		PanelBatch.add(Batchbutton, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		panelFirst.add(PanelSelectFile, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		panelFirst.add(Original, new GridBagConstraints(5, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		panelFirst.add(Batchbutton, new GridBagConstraints(5, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		timeslider.addAdjustmentListener(new TimeListener(this, timeText, timestring, thirdDimensionsliderInit,
				thirdDimensionSize, scrollbarSize, timeslider));

		minSegDistslider.addAdjustmentListener(new MinSegDistListener(this, minSegDistText, minSegDiststring,
				0, scrollbarSize, minSegDistslider));

		distancemode.addItemListener(new DistancemodeListener(this));
		circlemode.addItemListener(new CirclemodeListener(this));
		Combomode.addItemListener(new CombomodeListener(this));

		regioninteriorfield.addTextListener(new RegionInteriorListener(this, false));

		ClearDisplay.addActionListener(new ClearDisplayListener(this));
		Curvaturebutton.addActionListener(new CurvatureListener(this));
		Displaybutton.addActionListener(new DisplayVisualListener(this, true));
		startT.addTextListener(new AutoStartListener(this));
		endT.addTextListener(new AutoEndListener(this));
		minSegDistField.addTextListener(new MinSegDistLocListener(this, false));
		inputFieldT.addTextListener(new TlocListener(this, false));
		inputtrackField.addTextListener(new TrackidListener(this));
		ChooseDirectory.addActionListener(new SaveDirectory(this));
		inputField.addTextListener(new ETrackFilenameListener(this));
		Savebutton.addActionListener(new SaverListener(this));
		SaveAllbutton.addActionListener(new SaverAllListener(this));

		panelFirst.setVisible(true);
		cl.show(panelCont, "1");
		Cardframe.add(panelCont, "Center");
		Cardframe.add(jpb, "Last");

		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);
		
		imp.getCanvas().addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == 27)
					EscapePressed = true;
				
			}
			
			
			
			
			
		});

	}

	public Boolean EscapePressed = false;

}
