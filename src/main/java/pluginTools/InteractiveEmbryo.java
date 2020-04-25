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
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.scijava.plugin.Parameter;
import org.scijava.ui.UIService;

import bdv.util.BdvOverlay;
import bdv.util.BdvSource;
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
import listeners.AngleListener;
import listeners.AutoEndListener;
import listeners.AutoStartListener;
import listeners.BackGroundListener;
import listeners.BlackBorderListener;
import listeners.ClearDisplayListener;
import listeners.ClearforManual;
import listeners.ColorListener;
import listeners.CurrentCurvatureListener;
import listeners.CurvatureListener;
import listeners.DegreeListener;
import listeners.DeltasepListener;
import listeners.DisplayBoxListener;
import listeners.DisplayListener;
import listeners.DisplayVisualListener;
import listeners.DoSmoothingListener;
import listeners.DrawListener;
import listeners.ETrackFilenameListener;
import listeners.ETrackMaxSearchListener;
import listeners.ExteriorDistListener;
import listeners.GaussRadiusListener;
import listeners.HighProbListener;
import listeners.IlastikListener;
import listeners.InsideCutoffListener;
import listeners.InsideLocListener;
import listeners.InteriorDistListener;
import listeners.LinescanradiusListener;
import listeners.LostFrameListener;
import listeners.LowProbListener;
import listeners.ManualInterventionListener;
import listeners.MaxTryListener;
import listeners.MaxperimeterListener;
import listeners.MaxsizeListener;
import listeners.MinInlierListener;
import listeners.MinInlierLocListener;
import listeners.MinpercentListener;
import listeners.MinperimeterListener;
import listeners.MinsizeListener;
import listeners.OutsideCutoffListener;
import listeners.RListener;
import listeners.RedoListener;
import listeners.RegionInteriorListener;
import listeners.ResolutionListener;
import listeners.RimLineSelectionListener;
import listeners.RoiListener;
import listeners.RunCelltrackCirclemodeListener;
import listeners.RunCombomodeListener;
import listeners.RunPolymodeListener;
import listeners.RunpixelCelltrackCirclemodeListener;
import listeners.SaverAllListener;
import listeners.SaveDirectory;
import listeners.SaverListener;
import listeners.SecDegreeListener;
import listeners.SmoothSliderListener;
import listeners.TimeListener;
import listeners.TlocListener;
import listeners.TrackidListener;
import listeners.ZListener;
import listeners.ZlocListener;
import mpicbg.imglib.util.Util;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;

public class InteractiveEmbryo extends JPanel implements PlugIn {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	public String usefolder = IJ.getDirectory("imagej");
	public String addToName = "ETrack_";
	public final int scrollbarSize = 1000;
	public int maxError = 3;
	public int degree = 3;
	public String inputstring;
	public int secdegree = 2;
	public double minellipsepoints = 9;
	public double mincirclepoints = 3;
	public int tablesize;
	public double smoothing = 0;
	public ConcurrentHashMap<Integer, List<RealLocalizable>> Listmap = new ConcurrentHashMap<Integer, List<RealLocalizable>>();
	public HashMap<Integer, Integer> CellLabelsizemap = new HashMap<Integer, Integer>();
	public Overlay overlay, clockoverlay;
	public int numSeg = 1;
	public Overlay emptyoverlay;
	public int thirdDimensionslider = 1;
	public int thirdDimensionsliderInit = 1;
	public int fourthDimensionslider = 1;
	public int fourthDimensionsliderInit = 1;
	public int rowchoice;
	public int radiusdetection = 5;
	public int maxtry = 30;
	public float minpercent = 0f;
	public float minpercentINI = 0.65f;
	public float minpercentINIArc = 0.25f;
	public final double minSeperation = 5;
	public String selectedID;
	public float insideCutoff = 15;
	public float maxDist = 3;
	public float outsideCutoff = insideCutoff;
	public HashMap<String, ArrayList<Pair<Integer, Double>>> StripList = new HashMap<String, ArrayList<Pair<Integer, Double>>>();
	public ImagePlus RMStrackImages;
	// Distance between segments
	public int minNumInliers = 60;
	public int depth = 4;
	public int maxsize = 100;
	public int minsize = 10;
	public int span = 2;
	public int minperimeter = 100;
	public int numseg = 10;
	public int maxperimeter = 1000;
	public float lowprob = 0f;
	public float highprob = 1f;

	public float epsilon = 3f;
	public float lowprobmin = 0f;
	public float highprobmin = 0f;

	public ImagePlus clockimp;
	public boolean polynomialfits = false;
	public boolean circlefits = false;
	public boolean distancemethod = false;
	public boolean combomethod = true;
	public boolean celltrackcirclefits = false;
	public boolean pixelcelltrackcirclefits = false;

	public RealLocalizable globalMaxcord;

	public boolean redoing;
	public boolean showWater = false;
	public float lowprobmax = 1.0f;
	public float highprobmax = 1.0f;

	public float insideCutoffmin = 1;
	public float outsideCutoffmin = 1;
	public int smoothslidermin = 0;
	public int smoothslidermax = 1;
	public float minNumInliersmin = 0;
	public float minNumInliersmax = 100;
	public int KymoDimension = 0;
	public int AutostartTime, AutoendTime;
	public float insideCutoffmax = 500;
	public float outsideCutoffmax = 500;
	public int roiindex;
	public int fourthDimension;
	public int thirdDimension;
	public int thirdDimensionSize;
	public int fourthDimensionSize;
	public ImagePlus impA;
	public boolean isDone;
	public int MIN_SLIDER = 0;
	public int MAX_SLIDER = 500;
	public int row;
	public HashMap<String, Integer> Accountedframes;
	public HashMap<String, Integer> AccountedZ;
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

	public ResultsTable rtAll;
	public File inputfile;
	public String inputdirectory;
	public int radiusInt = 2;
	public float radius = 50f;
	public int strokewidth = 1;
	public float radiusMin = radiusInt;
	public float radiusMax = 300f;
	public MouseMotionListener ml;
	public MouseListener mvl;
	public Roi nearestRoiCurr;
	public OvalRoi nearestIntersectionRoiCurr;
	public Roi selectedRoi;
	public TextField inputFieldIter;
	public JTable table;
	public ArrayList<Roi> Allrois;
	public ImagePlus imp;
	public ImagePlus localimp;
	public ImagePlus localwaterimp;
	public ImagePlus resultimp;
	public ImagePlus emptyimp;
	public int ndims;
	public boolean usedefaultrim = true;
	public MouseListener ovalml;
	public double calibration;
	public double timecal;
	public int insidedistance = 20;
	public int regiondistance = 10;
	public int outsidedistance = 0;
	public int[] boundarypoint;
	public int[] midpoint;
	public int maxSearchradius = 100;
	public int maxSearchradiusS = 10;
	public int missedframes = 200;

	public float alphaMin = 0;
	public float alphaMax = 1;
	public float betaMin = 0;
	public float betaMax = 1;
	public int increment = 0;
	public int resolution = 1;
	public int linescanradius = 0;
	public int maxSearchradiusInit = (int) maxSearchradius;
	public float maxSearchradiusMin = 1;
	public float maxSearchradiusMax = maxSearchradius;
	public float maxSearchradiusMinS = 1;
	public float maxSearchradiusMaxS = maxSearchradius;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public RandomAccessibleInterval<FloatType> CurrentViewSmooth;
	public RandomAccessibleInterval<FloatType> CurrentViewSecOrig;
	public RandomAccessibleInterval<FloatType> CurrentResultView;
	public Color confirmedRois = Color.BLUE;
	public Color defaultRois = Color.YELLOW;
	public Color colorChange = Color.GRAY;
	public Color colorInChange = Color.RED;
	public int maxlabel;
	public Color colorOval = Color.CYAN;
	public Color colorDet = Color.GREEN;
	public Color colorLineA = Color.YELLOW;
	public Color colorLineB = Color.YELLOW;
	public Color colorPoints = Color.RED;
	public Color colorresult = Color.magenta;
	public double maxdistance = 10;
	public float alpha = 0.5f;
	public float beta = 0.5f;
	public ImageStack prestack;
	public MouseAdapter mouseadapter;
	public int[] Clickedpoints;
	public int starttime;
	public int endtime;

	public int boxsize;

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
	ColorProcessor cp = null;

	public boolean isCreated = false;
	public RoiManager roimanager;
	public String uniqueID, tmpID, ZID, TID;
	public int gaussradius = 2;
	public RealLocalizable Refcord;
	public HashMap<String, RealLocalizable> AllRefcords;
	public int mindistance = 200;
	public int alphaInit = 1;
	public int maxperi = Integer.MIN_VALUE;
	public int betaInit = 0;
	public int minSizeInit = 50;
	public int maxSizeInit = 500;
	public int maxSearchInit = 1000;
	public int maxframegap = 10;
	public int borderpixel = 0;
	public boolean twochannel;

	public static enum ValueChange {
		ROI, ALL, THIRDDIMmouse, FOURTHDIMmouse, DISPLAYROI, RADIUS, INSIDE, OUTSIDE, RESULT, RectRoi, SEG, Watershow, CURVERESULT
	}

	public void setTime(final int value) {

		fourthDimensionslider = value;
		fourthDimensionsliderInit = 1;
		fourthDimension = 1;
	}

	public int getTimeMax() {

		return thirdDimensionSize;
	}

	public void setZ(final int value) {
		thirdDimensionslider = value;
		thirdDimensionsliderInit = 1;
		thirdDimension = 1;
	}


	public void setOutsidecut(final int value) {

		outsideCutoff = value;
	}

	public int getZMax() {

		return fourthDimensionSize;
	}

	public void setlowprob(final float value) {
		lowprob = value;
		lowprob = computeScrollbarPositionFromValue(lowprob, lowprobmin, lowprobmax, scrollbarSize);
	}

	public double getlowprob(final float value) {

		return lowprob;

	}

	public void setInitialmaxsearchradius(final int value) {
		maxSearchradius = value;
		maxSearchradiusInit = computeScrollbarPositionFromValue(maxSearchradius, maxSearchradiusMin, maxSearchradiusMax,
				scrollbarSize);
	}

	public void setInitialAlpha(final float value) {
		alpha = value;
		alphaInit = computeScrollbarPositionFromValue(alpha, alphaMin, alphaMax, scrollbarSize);
	}

	public double getInitialAlpha(final float value) {

		return alpha;

	}

	public void setInitialBeta(final float value) {
		beta = value;
		betaInit = computeScrollbarPositionFromValue(beta, betaMin, betaMax, scrollbarSize);
	}

	public double getInitialBeta(final float value) {

		return beta;

	}

	public void sethighprob(final float value) {
		highprob = value;
		highprob = computeScrollbarPositionFromValue(highprob, highprobmin, highprobmax, scrollbarSize);
	}

	public double gethighprob(final float value) {

		return highprob;

	}

	public float computeValueFromScrollbarPosition(final int scrollbarPosition, final float min, final float max,
			final int scrollbarSize) {
		return min + (scrollbarPosition / (float) scrollbarSize) * (max - min);
	}

	public int computeScrollbarPositionFromValue(final float sigma, final float min, final float max,
			final int scrollbarSize) {
		return Util.round(((sigma - min) / (max - min)) * scrollbarSize);
	}

	int decimalplaces = 3;

	




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
		nf.setMaximumFractionDigits(decimalplaces);
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
		nf.setMaximumFractionDigits(decimalplaces);
		nf.setGroupingUsed(false);
		this.calibration = calibration;
		this.timecal = timecal;
		this.twochannel = twochannel;
		this.inputstring = inputstring;
	}





	public void run(String arg0) {

		displaymin = 0;
		displaymax = 1;


			

		redoing = false;
		AllRefcords = new HashMap<String, RealLocalizable>();
		pixellist = new HashSet<Integer>();
		rtAll = new ResultsTable();
		jpb = new JProgressBar();
		Allrois = new ArrayList<Roi>();
		Clickedpoints = new int[2];
		HashresultCurvature = new HashMap<Integer, ArrayList<double[]>>();
		SubHashresultCurvature = new HashMap<Integer, List<RealLocalizable>>();
		HashresultSegCurvature = new HashMap<Integer, Double>();
		HashresultSegIntensityA = new HashMap<Integer, Double>();
		HashresultSegIntensityB = new HashMap<Integer, Double>();
		HashresultSegPerimeter = new HashMap<Integer, Double>();

		resultDraw = new HashMap<String, Pair<ArrayList<double[]>, ArrayList<Line>>>();
		resultDrawLine = new HashMap<String, ArrayList<Line>>();
		Accountedframes = new HashMap<String, Integer>();
		AccountedZ = new HashMap<String, Integer>();


		// minNumInliers = (int) Math.round(timecal / calibration);


		if (ndims < 3) {

			thirdDimensionSize = 0;
			fourthDimensionSize = 0;
		}

		if (ndims == 3) {

			fourthDimension = 1;
			thirdDimension = 1;
			fourthDimensionSize = 0;

			thirdDimensionSize = (int) originalimg.dimension(2);
			AutostartTime = thirdDimension;
			AutoendTime = thirdDimensionSize;
			maxframegap = thirdDimensionSize / 4;
		}

		if (ndims == 4) {

			fourthDimension = 1;
			thirdDimension = 1;

			thirdDimensionSize = (int) originalimg.dimension(2);
			fourthDimensionSize = (int) originalimg.dimension(3);
			AutostartTime = fourthDimension;
			AutoendTime = fourthDimensionSize;
			prestack = new ImageStack((int) originalimg.dimension(0), (int) originalimg.dimension(1),
					java.awt.image.ColorModel.getRGBdefault());
		}

		if (ndims > 4) {

			System.out.println("Image has wrong dimensionality, upload an XYT/XYZ/XY image");
			return;
		}

		if (originalimg == null)
			originalimg = originalimg;
		setTime(fourthDimension);
		setZ(thirdDimension);
		CurrentView = utility.Slicer.getCurrentView(originalimg, thirdDimension, thirdDimensionSize, fourthDimension,
				fourthDimensionSize);

	
		if (originalimg != null) {
			CurrentView = utility.Slicer.getCurrentView(originalimg, thirdDimension, thirdDimensionSize,
					fourthDimension, fourthDimensionSize);

		}
		if (Secoriginalimg != null) {

			CurrentViewSecOrig = utility.Slicer.getCurrentView(Secoriginalimg, thirdDimension, thirdDimensionSize,
					fourthDimension, fourthDimensionSize);
		}

		if (Segoriginalimg != null) {

			RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(Segoriginalimg,
					thirdDimension, thirdDimensionSize, fourthDimension, fourthDimensionSize);
			IntType min = new IntType();
			IntType max = new IntType();
			computeMinMax(Views.iterable(CurrentViewInt), min, max);
			// Neglect the background class label
			int currentLabel = min.get();


		}

		imp = ImageJFunctions.show(CurrentView, "Original Image");

		// bdv = BdvFunctions.show( CurrentView, "Cell", Bdv.options().is2D() );
		clockimp = ImageJFunctions.show(CurrentView);

		imp.setTitle("Active Image" + " " + "time point : " + fourthDimension + " " + " Z: " + thirdDimension);
		// Create empty Hyperstack

		updatePreview(ValueChange.ALL);

			lowprobslider.setValue(computeScrollbarPositionFromValue(lowprob, lowprobmin, lowprobmax, scrollbarSize));
			highprobslider
					.setValue(computeScrollbarPositionFromValue(highprob, highprobmin, highprobmax, scrollbarSize));
			lowprob = utility.Slicer.computeValueFromScrollbarPosition(lowprobslider.getValue(), lowprobmin, lowprobmax,
					scrollbarSize);
			highprob = utility.Slicer.computeValueFromScrollbarPosition(highprobslider.getValue(), highprobmin,
					highprobmax, scrollbarSize);

			updatePreview(ValueChange.SEG);


		Cardframe.repaint();
		Cardframe.validate();
		panelFirst.repaint();
		panelFirst.validate();

		Card(false);

			StartCurvatureComputingCurrent();
		saveFile = new java.io.File(".");

	}

	

	public void CleanMe() {

		table.removeAll();
		table.repaint();
		overlay.clear();
		if (imp != null && mvl != null)
			imp.getCanvas().removeMouseListener(mvl);
		if (imp != null && ml != null)
			imp.getCanvas().removeMouseMotionListener(ml);

		displayCircle.setState(false);
		displaySegments.setState(false);
		displayIntermediate = false;
		displayIntermediateBox = false;
		overlay.clear();
		AccountedZ.clear();
		AutostartTime = Integer.parseInt(startT.getText());
		if (AutostartTime <= 0)
			AutostartTime = 1;
		AutoendTime = Integer.parseInt(endT.getText());
		for (int z = AutostartTime; z <= AutoendTime; ++z)
			AccountedZ.put(Integer.toString(z), z);

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
		

		uniqueID = Integer.toString(thirdDimension) + Integer.toString(fourthDimension);
		ZID = Integer.toString(thirdDimension);
		TID = Integer.toString(fourthDimension);
		tmpID = Float.toString(thirdDimension) + Float.toString(fourthDimension);
		overlay = imp.getOverlay();
		clockoverlay = clockimp.getOverlay();
		if (overlay == null) {

			overlay = new Overlay();
			imp.setOverlay(overlay);

		}

		if (clockoverlay != null)
			clockoverlay.clear();
		if (clockoverlay == null) {

			clockoverlay = new Overlay();
			clockimp.setOverlay(clockoverlay);

		}
		

		if (change == ValueChange.SEG) {

				RandomAccessibleInterval<FloatType> tempview = null;

					tempview = utility.Binarization.CreateBinary(CurrentViewSmooth, lowprob, highprob);

				if (localimp == null || !localimp.isVisible()) {
					localimp = ImageJFunctions.show(tempview);

				}

				else {

					final float[] pixels = (float[]) localimp.getProcessor().getPixels();
					final Cursor<FloatType> c = Views.iterable(tempview).cursor();

					for (int i = 0; i < pixels.length; ++i)
						pixels[i] = c.next().get();

					localimp.updateAndDraw();

				}

					localimp.setTitle(
							"Seg Image" + " " + "time point : " + fourthDimension + " " + " Z: " + thirdDimension);

			}

		if (change == ValueChange.RESULT) {

			prestack = new ImageStack((int) originalimg.dimension(0), (int) originalimg.dimension(1),
					java.awt.image.ColorModel.getRGBdefault());

			String ID = (String) table.getValueAt(rowchoice, 0);
			ArrayList<double[]> resultlist = new ArrayList<double[]>();
			ArrayList<Line> resultline = new ArrayList<Line>();

			Pair<ArrayList<double[]>, ArrayList<Line>> resultpair;
		
			resultpair = new ValuePair<ArrayList<double[]>, ArrayList<Line>>(resultlist, resultline);
			resultDraw.put(ID, resultpair);

			if (originalimg.numDimensions() > 3) {
				for (int time = 1; time <= fourthDimensionSize; ++time)
					prestack.addSlice(resultimp.getImageStack().getProcessor(time).convertToRGB());

				for (double[] current : resultDraw.get(ID).getA()) {
					Overlay resultoverlay = new Overlay();
					int time = (int) current[0];
					int Z = (int) current[1];
					double IntersectionX = current[2];
					double IntersectionY = current[3];
					int radius = 3;
					ShowResultView showcurrent = new ShowResultView(this, time, Z);
					showcurrent.shownew();

					cp = (ColorProcessor) (prestack.getProcessor(time).duplicate());
					cp.reset();

					resultimp.setOverlay(resultoverlay);

					OvalRoi selectedRoi = new OvalRoi(Util.round(IntersectionX - radius),
							Util.round(IntersectionY - radius), Util.round(2 * radius), Util.round(2 * radius));
					resultoverlay.add(selectedRoi);

					cp.setColor(colorLineA);
					cp.setLineWidth(4);
					cp.draw(selectedRoi);

					if (prestack != null)
						prestack.setPixels(cp.getPixels(), time);

				}
			}

			else {

				if (originalimg == null)
					resultimp = ImageJFunctions.show(originalimg);
				else
					resultimp = ImageJFunctions.show(originalimg);
				Overlay resultoverlay = new Overlay();
				for (int time = 1; time <= thirdDimensionSize; ++time)
					prestack.addSlice(resultimp.getImageStack().getProcessor(time).convertToRGB());
				for (double[] current : resultDraw.get(ID).getA()) {
					int Z = (int) current[1];
					double IntersectionX = current[2];
					double IntersectionY = current[3];
					int radius = 3;

					String currentID = Integer.toString(Z) + Integer.toString((int) current[0]);
					ShowResultView showcurrent = new ShowResultView(this, Z);
					showcurrent.shownew();

					cp = (ColorProcessor) (prestack.getProcessor(Z).duplicate());
					cp.reset();

					resultimp.setOverlay(resultoverlay);

					OvalRoi selectedRoi = new OvalRoi(Util.round(IntersectionX - radius),
							Util.round(IntersectionY - radius), Util.round(2 * radius), Util.round(2 * radius));
					resultoverlay.add(selectedRoi);

					Roiobject currentobject = ZTRois.get(currentID);
					Line nearestline = null;
					for (Line currentline : resultDraw.get(ID).getB()) {
						cp.setColor(colorLineA);
						cp.setLineWidth(4);
						Line nearest = kalmanTracker.NearestRoi.getNearestLineRois(currentobject,
								new double[] { IntersectionX, IntersectionY }, this);
						if (nearest == currentline) {
							cp.draw(currentline);
							nearestline = currentline;
						}

					}
					if (nearestline != null)
						currentobject.resultlineroi.remove(nearestline);
					for (Line currentline : resultDraw.get(ID).getB()) {
						cp.setColor(colorLineA);
						cp.setLineWidth(4);
						Line nearest = kalmanTracker.NearestRoi.getNearestLineRois(currentobject,
								new double[] { IntersectionX, IntersectionY }, this);
						if (nearest == currentline) {
							cp.draw(currentline);
						}

					}
					currentobject.resultlineroi.add(nearestline);

					cp.setColor(colorresult);
					cp.setLineWidth(4);
					cp.draw(selectedRoi);

					if (prestack != null)
						prestack.setPixels(cp.getPixels(), Z);

				}

			}
			new ImagePlus("TrackID" + table.getValueAt(row, 0), prestack).show();
			resultimp.close();

		}

		if (change == ValueChange.ROI) {
			roimanager = RoiManager.getInstance();

			if (roimanager == null) {
				roimanager = new RoiManager();
			}
			IJ.run("Select None");
			DefaultZTRois.clear();
			// roimanager.runCommand("show all");
			Roi[] Rois = roimanager.getRoisAsArray();
			Roiobject CurrentRoi = new Roiobject(Rois, thirdDimension, fourthDimension, true);

			DefaultZTRois.put(uniqueID, CurrentRoi);

			Accountedframes.put(TID, fourthDimension);

			AccountedZ.put(ZID, thirdDimension);

			ZTRois.put(uniqueID, CurrentRoi);

			Display();

		}

		if (change == ValueChange.THIRDDIMmouse || change == ValueChange.FOURTHDIMmouse) {
			
			if (Tracklist.size() > 0 ) {

				ComputeCurvature.CurvedLineage(this);

			}

			if (StripList.size() > 0) {

				for (Map.Entry<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>> entryZ : parentdensegraphZ
						.entrySet()) {
					TrackModel model = new TrackModel(entryZ.getValue());
					RandomAccessibleInterval<FloatType> StripImage = new ArrayImgFactory<FloatType>()
							.create(CurrentView, new FloatType());
					RandomAccess<FloatType> ranacStrip = StripImage.randomAccess();

					for (final Integer id : model.trackIDs(true)) {
						String targetid = id + entryZ.getKey();

						for (Map.Entry<String, ArrayList<Pair<Integer, Double>>> item : StripList.entrySet()) {
							String TrackID = item.getKey();
							if (TrackID.equals(targetid)) {
								for (Pair<Integer, Double> singleitem : item.getValue()) {

									String TimeId = Integer.toString(thirdDimension);

									ArrayList<Intersectionobject> currentlist = sortedMappair.get(TrackID + TimeId);

									for (Intersectionobject currentobject : currentlist) {

										ArrayList<double[]> sortedlinelist = currentobject.linelist;

										int i = singleitem.getA();

										if (sortedlinelist.size() > i) {
											ranacStrip.setPosition(new long[] { (long) sortedlinelist.get(i)[0],
													(long) sortedlinelist.get(i)[1] });

											HyperSphere<FloatType> hyperSphere = new HyperSphere<FloatType>(StripImage,
													ranacStrip, 3);
											HyperSphereCursor<FloatType> localcursor = hyperSphere.localizingCursor();

											while (localcursor.hasNext()) {

												localcursor.fwd();

												ranacStrip.setPosition(localcursor);

												ranacStrip.get().setReal(ranacStrip.get().get() + singleitem.getB());
											}

										}
									}

								}

							}
						}

						if (RMStrackImages == null || !RMStrackImages.isVisible()) {

							RMStrackImages = ImageJFunctions.show(StripImage);

						} else {

							final float[] pixels = (float[]) RMStrackImages.getProcessor().getPixels();
							final Cursor<FloatType> c = Views.iterable(StripImage).cursor();

							for (int i = 0; i < pixels.length; ++i)
								pixels[i] = c.next().get();

							RMStrackImages.updateAndDraw();

						}

						RMStrackImages.setTitle("Root Mean square of Curvature" + targetid);

					}
				}

			}

				updatePreview(ValueChange.SEG);

				if (originalimg != null)
					CurrentViewSmooth = utility.Slicer.getCurrentView(originalimg, thirdDimension,
							thirdDimensionSize, fourthDimension, fourthDimensionSize);
				if (originalimg != null)
					CurrentView = utility.Slicer.getCurrentView(originalimg, thirdDimension,
							thirdDimensionSize, fourthDimension, fourthDimensionSize);

				if (originalimg != null)
					CurrentView = utility.Slicer.getCurrentView(originalimg, thirdDimension, thirdDimensionSize,
							fourthDimension, fourthDimensionSize);

				if (Secoriginalimg != null)

					CurrentViewSecOrig = utility.Slicer.getCurrentView(Secoriginalimg, thirdDimension,
							thirdDimensionSize, fourthDimension, fourthDimensionSize);


			
				repaintView(imp, CurrentView);

				DisplayAuto.Display(this);
			imp.setTitle("Active image" + " " + "time point : " + fourthDimension + " " + " Z: " + thirdDimension);

		}

	}

	

	public void StartCurvatureComputing(File savefile) {
			ComputeCurvature compute = new ComputeCurvature(this, jpb, batchmode, savefile);

			curvesupermode = true;

			compute.execute();

		


	}

	public RandomAccessibleInterval<BitType> CreateBinaryBit(RandomAccessibleInterval<FloatType> source, double lowprob,
			double highprob) {

		RandomAccessibleInterval<BitType> copyoriginal = new ArrayImgFactory<BitType>().create(source, new BitType());

		final RandomAccess<BitType> ranac = copyoriginal.randomAccess();
		final Cursor<FloatType> cursor = Views.iterable(source).localizingCursor();

		while (cursor.hasNext()) {

			cursor.fwd();

			ranac.setPosition(cursor);
			if (cursor.get().getRealDouble() > lowprob && cursor.get().getRealDouble() < highprob) {

				ranac.get().setOne();
			} else {
				ranac.get().setZero();
			}

		}

		return copyoriginal;

	}



	public void StartCurvatureComputingCurrent() {

		ComputeCurvatureCurrent compute = new ComputeCurvatureCurrent(this, jpb);

		compute.execute();
	}

	public void Display() {

		overlay.clear();

		if (ZTRois.size() > 0) {

			for (Map.Entry<String, Roiobject> entry : ZTRois.entrySet()) {

				Roiobject currentobject = entry.getValue();

				if (currentobject.fourthDimension == fourthDimension
						&& currentobject.thirdDimension == thirdDimension) {

					if (currentobject.roilist != null) {
						for (int indexx = 0; indexx < currentobject.roilist.length; ++indexx) {

							Roi or = currentobject.roilist[indexx];
							or.setStrokeColor(confirmedRois);
							overlay.add(or);
						}
					}

					if (currentobject.resultroi != null) {

						for (int i = 0; i < currentobject.resultroi.size(); ++i) {

							EllipseRoi ellipse = currentobject.resultroi.get(i);
							ellipse.setStrokeColor(colorInChange);
							overlay.add(ellipse);

						}

					}

					if (currentobject.resultovalroi != null) {

						for (int i = 0; i < currentobject.resultovalroi.size(); ++i) {

							OvalRoi ellipse = currentobject.resultovalroi.get(i);
							ellipse.setStrokeColor(colorDet);
							overlay.add(ellipse);

						}

					}

					if (currentobject.resultlineroi != null) {

						for (int i = 0; i < currentobject.resultlineroi.size(); ++i) {

							Line ellipse = currentobject.resultlineroi.get(i);
							ellipse.setStrokeColor(colorLineA);

							overlay.add(ellipse);

						}

					}

					break;
				}

			}
			imp.updateAndDraw();

				DisplayAuto.mark(this);
				DisplayAuto.select(this);

		}
	}

	public void DisplayOnly() {

		overlay.clear();

		if (ZTRois.size() > 0) {

			for (Map.Entry<String, Roiobject> entry : ZTRois.entrySet()) {

				Roiobject currentobject = entry.getValue();

				if (currentobject.fourthDimension == fourthDimension
						&& currentobject.thirdDimension == thirdDimension) {

					for (int indexx = 0; indexx < currentobject.roilist.length; ++indexx) {

						Roi or = currentobject.roilist[indexx];

						if (or == nearestRoiCurr) {

							or.setStrokeColor(colorInChange);

						}

						overlay.add(or);
					}

					if (currentobject.resultroi != null) {

						for (int i = 0; i < currentobject.resultroi.size(); ++i) {

							Roi ellipse = currentobject.resultroi.get(i);
							ellipse.setStrokeColor(colorInChange);
							overlay.add(ellipse);

						}

					}
					if (currentobject.resultovalroi != null) {

						for (int i = 0; i < currentobject.resultovalroi.size(); ++i) {

							OvalRoi ellipse = currentobject.resultovalroi.get(i);
							ellipse.setStrokeColor(colorDet);
							overlay.add(ellipse);

						}

					}

					if (currentobject.resultlineroi != null) {

						for (int i = 0; i < currentobject.resultlineroi.size(); ++i) {

							Line ellipse = currentobject.resultlineroi.get(i);
							ellipse.setStrokeColor(colorLineA);
							overlay.add(ellipse);

						}

					}
					break;
				}

			}
			imp.updateAndDraw();

		}
	}

	public void DisplayDefault() {

		overlay.clear();
		if (DefaultZTRois.size() > 0) {

			for (Map.Entry<String, Roiobject> entry : DefaultZTRois.entrySet()) {

				Roiobject currentobject = entry.getValue();

				for (int indexx = 0; indexx < currentobject.roilist.length; ++indexx) {

					Roi or = currentobject.roilist[indexx];
					or.setStrokeColor(defaultRois);
					overlay.add(or);
				}

				break;

			}
			imp.updateAndDraw();
				DisplayAuto.mark(this);
				DisplayAuto.select(this);

		}
	}

	public JFrame Cardframe = new JFrame("Intersection angle or curvature measurment");
	public JPanel panelCont = new JPanel();
	public JPanel panelFirst = new JPanel();
	public JPanel panelSecond = new JPanel();
	public JPanel Timeselect = new JPanel();
	public JPanel Zselect = new JPanel();
	public JPanel Roiselect = new JPanel();
	public JPanel Probselect = new JPanel();
	public JPanel Angleselect = new JPanel();
	public JPanel KalmanPanel = new JPanel();
	public JPanel ManualIntervention = new JPanel();
	public JCheckBox IlastikAuto = new JCheckBox("Show Watershed Image", showWater);

	public TextField inputFieldT, inputtrackField, minperimeterField, maxperimeterField, gaussfield, numsegField,
			cutoffField, minInlierField, degreeField, secdegreeField;//, resolutionField;
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

	public JButton Roibutton = new JButton("Confirm current roi selection");
	public JButton DisplayRoibutton = new JButton("Display roi selection");
	public JButton Anglebutton = new JButton("Fit Ellipses and track angles");
	public JButton Curvaturebutton = new JButton("Measure Local Curvature");
	public JButton Displaybutton = new JButton("Display Visuals (time)");
	public JButton CurrentCurvaturebutton = new JButton("Measure Current Curvature");
	public JButton Savebutton = new JButton("Save Track");
	public JButton Batchbutton = new JButton("Save Parameters for batch mode and exit");
	public JButton SaveAllbutton = new JButton("Save All Tracks");
	public JButton Redobutton = new JButton("Compute/Recompute for current view");

	public JButton Smoothbutton = new JButton("Do Gaussian Smoothing");
	public JButton Clearmanual = new JButton("Clear Current View");
	public JButton ManualCompute = new JButton("Manual Computation");
	public String timestring = "Current T";
	public String zstring = "Current Z";
	public String zgenstring = "Current Z / T";
	public String rstring = "Radius";
	public String insidestring = "Cutoff distance";
	public String outsidestring = "Cutoff distance";
	public String smoothsliderstring = "Ratio of functions ";
	public String mininlierstring = "Box Size(um)";

	public Label timeText = new Label("Current T = " + 1, Label.CENTER);
	public Label zText = new Label("Current Z = " + 1, Label.CENTER);
	public Label zgenText = new Label("Current Z / T = " + 1, Label.CENTER);
	public Label rText = new Label("Alt+Left Click selects a Roi");
	public Label contText = new Label("After making all roi selections");
	public Label insideText = new Label("Cutoff distance  = " + insideCutoff, Label.CENTER);
	public Label degreeText = new Label("Choose degree of polynomial");
	public Label resolutionText = new Label("Measurement Resolution (px)");
	//public Label radiusText = new Label("LineScan radius (px)");
	//public Label indistText = new Label("LineScan length (px)");
	public Label regionText = new Label("Intensity region (px)");
	public Label outdistText = new Label("Intensity Exterior region (px)");

	public Label secdegreeText = new Label("Choose degree of second polynomial");
	public Label minInlierText = new Label(mininlierstring + " = " + minNumInliers, Label.CENTER);

	final Label outsideText = new Label("Cutoff distance = " + outsideCutoff, Label.CENTER);

	final Label minperiText = new Label("Minimum ellipse perimeter");
	final Label maxperiText = new Label("Maximum ellipse perimeter");
	public final Label maxsizeText = new Label("Maximum region size (px)");
	public final Label minsizeText = new Label("Minimum region size (px)");
	final Label numsegText = new Label("Number of segments");
	final Label lowprobText = new Label("Lower probability level = " + lowprob, Label.CENTER);
	final Label highporbText = new Label("Higher probability level = " + highprob, Label.CENTER);

	final String lowprobstring = "Lower probability level";
	final String highprobstring = "Higher probability level";

	final String minperimeterstring = "Minimum ellipse perimeter";
	final String maxperimeterstring = "Maximum ellipse perimeter";

	public final Insets insets = new Insets(10, 0, 0, 0);
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();

	public JScrollPane scrollPane;
	public JFileChooser chooserA = new JFileChooser();
	public String choosertitleA;
	public JScrollBar timeslider = new JScrollBar(Scrollbar.HORIZONTAL, fourthDimensionsliderInit, 10, 0,
			scrollbarSize + 10);
	public JScrollBar zslider = new JScrollBar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
			10 + scrollbarSize);
	public JScrollBar rslider = new JScrollBar(Scrollbar.HORIZONTAL, radiusInt, 10, 0, 10 + scrollbarSize);
	public JScrollBar insideslider = new JScrollBar(Scrollbar.HORIZONTAL, 0, 10, 0, 10 + scrollbarSize);
	public JScrollBar smoothslider = new JScrollBar(Scrollbar.HORIZONTAL, 0, 10, 0, 10 + scrollbarSize);
	public JScrollBar maxdistslider = new JScrollBar(Scrollbar.HORIZONTAL, 0, 10, 0, 10 + scrollbarSize);
	public JScrollBar minInlierslider = new JScrollBar(Scrollbar.HORIZONTAL, 0, 10, 0, 10 + scrollbarSize);
	public JScrollBar outsideslider = new JScrollBar(Scrollbar.HORIZONTAL, 0, 10, 0, 10 + scrollbarSize);
	public JScrollBar lowprobslider = new JScrollBar(Scrollbar.HORIZONTAL, 0, 10, 0, 10 + scrollbarSize);
	public JScrollBar highprobslider = new JScrollBar(Scrollbar.HORIZONTAL, 0, 10, 0, 10 + scrollbarSize);

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
	final String alphastring = "Weightage for distance based cost";
	final String cutoffstring = insideText.getName();
	final String betastring = "Weightage for pixel ratio based cost";
	final String maxSearchstring = "Maximum search radius";
	final String maxSearchstringS = "Maximum search radius";
	final String initialSearchstring = "Initial search radius";

	CheckboxGroup curvaturemode = new CheckboxGroup();

	final Checkbox polymode = new Checkbox("Polynomial Fits", curvaturemode, polynomialfits);

	final Checkbox circlemode = new Checkbox("Track Segment Circle Fits", curvaturemode, circlefits);
	public final Checkbox distancemode = new Checkbox("Use Distance Method", curvaturemode, distancemethod);
	//public final Checkbox IntegerSegment = new Checkbox("Different Box Sizes");
	
	public final Checkbox Pixelcelltrackcirclemode = new Checkbox("Use Circle Fits", curvaturemode,
			pixelcelltrackcirclefits);
	public final Checkbox Combomode = new Checkbox("Use Combo Circle-Distance Method", curvaturemode, combomethod);

	public boolean displayIntermediate = true;
	public boolean displayIntermediateBox = true;
	public Checkbox displayCircle = new Checkbox("Display Intermediate Circles", displayIntermediateBox);
	public Checkbox displaySegments = new Checkbox("Display Segments", displayIntermediateBox);
	public JButton ClearDisplay = new JButton("Clear Display");
	public JButton SelectRim = new JButton("Rim selection for Intensity");

	Label maxSearchText = new Label(maxSearchstring + " = " + maxSearchInit, Label.CENTER);
	Label maxSearchTextS = new Label(maxSearchstring + " = " + maxSearchInit, Label.CENTER);
	Label alphaText = new Label(alphastring + " = " + alphaInit, Label.CENTER);
	Label betaText = new Label(betastring + " = " + betaInit, Label.CENTER);
	public Label smoothText = new Label("Ratio of functions = " + smoothing, Label.CENTER);

	public Border timeborder = new CompoundBorder(new TitledBorder("Select time"), new EmptyBorder(c.insets));
	public Border zborder = new CompoundBorder(new TitledBorder("Select Z"), new EmptyBorder(c.insets));
	public Border roitools = new CompoundBorder(new TitledBorder("Roi and ellipse finder tools"),
			new EmptyBorder(c.insets));

	public Border probborder = new CompoundBorder(new TitledBorder("Automation block"), new EmptyBorder(c.insets));
	public Border ellipsetools = new CompoundBorder(new TitledBorder("Ransac and Angle computer"),
			new EmptyBorder(c.insets));
	public Border circletools = new CompoundBorder(new TitledBorder("Curvature computer"), new EmptyBorder(c.insets));

	public Border Kalmanborder = new CompoundBorder(new TitledBorder("Kalman Filter Search for angle tracking"),
			new EmptyBorder(c.insets));

	public Border ManualInterventionborder = new CompoundBorder(new TitledBorder("Manual Intervention"),
			new EmptyBorder(c.insets));

	int textwidth = 5;

	public void Card(boolean hide) {

		minInlierText = new Label(mininlierstring + " = " + minNumInliers, Label.CENTER);
		lostlabel = new Label("Number of frames for loosing the track");
		lostframe = new TextField(1);
		lostframe.setText(Integer.toString(maxframegap));

		blackcorrectionlabel = new Label("Expand border");
		bordercorrection = new TextField(textwidth);
		bordercorrection.setText(Float.toString(borderpixel));

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
		Zselect.setLayout(layout);
		Original.setLayout(layout);
		Roiselect.setLayout(layout);
		Probselect.setLayout(layout);
		Angleselect.setLayout(layout);
		KalmanPanel.setLayout(layout);
		ManualIntervention.setLayout(layout);
		inputFieldZ = new TextField(textwidth);
		inputFieldZ.setText(Integer.toString(thirdDimension));

		inputFieldT = new TextField(textwidth);
		inputFieldT.setText(Integer.toString(fourthDimension));

		cutoffField = new TextField(textwidth);
		cutoffField.setText(Double.toString(insideCutoff));

		minInlierField = new TextField(textwidth);
		minInlierField.setText(Integer.toString(minNumInliers));

		//SpecialminInlierField = new TextField(textwidth);
		//SpecialminInlierField.setText(Integer.toString(minNumInliers));

		inputtrackField = new TextField(textwidth);

		inputFieldIter = new TextField(textwidth);
		inputFieldIter.setText(Integer.toString(maxtry));

		minperimeterField = new TextField(textwidth);
		minperimeterField.setText(Integer.toString(minperimeter));

		maxSizeField = new TextField(textwidth);
		maxSizeField.setText(Integer.toString(maxsize));

		minSizeField = new TextField(textwidth);
		minSizeField.setText(Integer.toString(minsize));

		numsegField = new TextField(textwidth);
		numsegField.setText(Integer.toString(depth));

		maxperimeterField = new TextField(textwidth);
		maxperimeterField.setText(Integer.toString(maxperimeter));

		gaussfield = new TextField(textwidth);
		gaussfield.setText(Integer.toString(gaussradius));

		degreeField = new TextField(textwidth);
		degreeField.setText(Integer.toString(degree));

		//resolutionField = new TextField(textwidth);
		//resolutionField.setText(Integer.toString(resolution));

		//radiusField = new TextField(textwidth);
		//radiusField.setText(Integer.toString(linescanradius));

		interiorfield = new TextField(textwidth);
		interiorfield.setText(Integer.toString(insidedistance));

		regioninteriorfield = new TextField(textwidth);
		regioninteriorfield.setText(Integer.toString(regiondistance));

		exteriorfield = new TextField(textwidth);
		exteriorfield.setText(Integer.toString(outsidedistance));

		secdegreeField = new TextField(textwidth);
		secdegreeField.setText(Integer.toString(secdegree));

		inputLabelIter = new Label("Max. attempts to find ellipses");
		final JScrollBar maxSearchS = new JScrollBar(Scrollbar.HORIZONTAL, maxSearchInit, 10, 0, 10 + scrollbarSize);

		final JScrollBar alphaS = new JScrollBar(Scrollbar.HORIZONTAL, alphaInit, 10, 0, 10 + scrollbarSize);
		final JScrollBar betaS = new JScrollBar(Scrollbar.HORIZONTAL, betaInit, 10, 0, 10 + scrollbarSize);

		maxSearchradius = (int) utility.ETrackScrollbarUtils.computeValueFromScrollbarPosition(maxSearchS.getValue(),
				maxSearchradiusMin, maxSearchradiusMax, scrollbarSize);

		alpha = utility.ETrackScrollbarUtils.computeValueFromScrollbarPosition(alphaS.getValue(), alphaMin, alphaMax,
				scrollbarSize);
		beta = utility.ETrackScrollbarUtils.computeValueFromScrollbarPosition(betaS.getValue(), betaMin, betaMax,
				scrollbarSize);

		String[] DrawType = { "Closed Loops", "Semi-Closed Loops" };

		ChooseMethod = new JComboBox<String>(DrawType);

		String[] DrawColor = { "Grey", "Red", "Blue", "Pink" };

		ChooseColor = new JComboBox<String>(DrawColor);

		inputLabelmaxellipse = new Label("Max. number of ellipses");
		inputtrackLabel = new Label("Enter trackID to save");
		inputcellLabel = new Label("Enter CellLabel to save");
		inputFieldminpercent = new TextField(5);
		inputFieldminpercent.setText(Float.toString(minpercent));

		inputLabelminpercent = new Label("Min. percent points to lie on ellipse");
		Object[] colnames;
		Object[][] rowvalues;

		colnames = new Object[] { "Track Id", "Location X", "Location Y", "Location Z/T", "Perimeter" };

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
		// panelFirst.add(Timeselect, new GridBagConstraints(0, 0, 5, 1, 0.0, 0.0,
		// GridBagConstraints.EAST,
		// GridBagConstraints.HORIZONTAL, insets, 0, 0));

		// Put z slider
		if (ndims > 3)
			Zselect.add(zText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
		else
			Zselect.add(zgenText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Zselect.add(zslider, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Zselect.add(inputFieldZ, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Zselect.setBorder(zborder);
		panelFirst.add(Zselect, new GridBagConstraints(0, 0, 5, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		if (ndims < 4) {

			timeslider.setEnabled(false);
			inputFieldT.setEnabled(false);
		}
		if (ndims < 3) {

			zslider.setEnabled(false);
			inputFieldZ.setEnabled(false);
		}

	

			Probselect.add(autoTstart, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Probselect.add(startT, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			Probselect.add(autoTend, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			Probselect.add(endT, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

			Probselect.setBorder(probborder);

			panelFirst.add(Probselect, new GridBagConstraints(5, 0, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));


	

				SliderBoxGUI combominInlier = new SliderBoxGUI(mininlierstring, minInlierslider, minInlierField,
						minInlierText, scrollbarSize, minNumInliers, minNumInliersmax);

				Angleselect.add(distancemode, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				Angleselect.add(Pixelcelltrackcirclemode, new GridBagConstraints(2, 0, 2, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

				
				Angleselect.add(Combomode, new GridBagConstraints(2, 2, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));

				Angleselect.add(combominInlier.BuildDisplay(), new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));


				Angleselect.add(regionText, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));

				Angleselect.add(regioninteriorfield, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

				Angleselect.add(Curvaturebutton, new GridBagConstraints(2, 4, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));


				Angleselect.add(Displaybutton, new GridBagConstraints(2, 5, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));

				Angleselect.setBorder(circletools);
				panelFirst.add(Angleselect, new GridBagConstraints(0, 1, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
		timeslider.addAdjustmentListener(new TimeListener(this, timeText, timestring, fourthDimensionsliderInit,
				fourthDimensionSize, scrollbarSize, timeslider));
		if (ndims > 3)
			zslider.addAdjustmentListener(new ZListener(this, zText, zstring, thirdDimensionsliderInit,
					thirdDimensionSize, scrollbarSize, zslider));
		else
			zslider.addAdjustmentListener(new ZListener(this, zgenText, zgenstring, thirdDimensionsliderInit,
					thirdDimensionSize, scrollbarSize, zslider));
		rslider.addAdjustmentListener(
				new RListener(this, rText, rstring, radiusMin, radiusMax, scrollbarSize, rslider));

		insideslider.addAdjustmentListener(new InsideCutoffListener(this, insideText, insidestring, insideCutoffmin,
				insideCutoffmax, scrollbarSize, insideslider));

		smoothslider.addAdjustmentListener(new SmoothSliderListener(this, smoothText, smoothsliderstring,
				smoothslidermin, smoothslidermax, scrollbarSize, smoothslider));

		outsideslider.addAdjustmentListener(new OutsideCutoffListener(this, outsideText, outsidestring,
				outsideCutoffmin, outsideCutoffmax, scrollbarSize, outsideslider));
		minInlierslider.addAdjustmentListener(new MinInlierListener(this, minInlierText, mininlierstring,
				minNumInliersmin, scrollbarSize, minInlierslider));

		distancemode.addItemListener(new RunCelltrackCirclemodeListener(this));
		Pixelcelltrackcirclemode.addItemListener(new RunpixelCelltrackCirclemodeListener(this));
		Combomode.addItemListener(new RunCombomodeListener(this));
		polymode.addItemListener(new RunPolymodeListener(this));

		gaussfield.addTextListener(new GaussRadiusListener(this));
		bordercorrection.addTextListener(new BlackBorderListener(this));
		lostframe.addTextListener(new LostFrameListener(this));
		interiorfield.addTextListener(new InteriorDistListener(this, false));
		exteriorfield.addTextListener(new ExteriorDistListener(this));

		regioninteriorfield.addTextListener(new RegionInteriorListener(this, false));

		Batchbutton.addActionListener(new SaveBatchListener(this));
		ClearDisplay.addActionListener(new ClearDisplayListener(this));
		SelectRim.addActionListener(new RimLineSelectionListener(this));
		degreeField.addTextListener(new DegreeListener(this, false));
		//resolutionField.addTextListener(new ResolutionListener(this, false));

		//radiusField.addTextListener(new LinescanradiusListener(this, false));
		secdegreeField.addTextListener(new SecDegreeListener(this, false));
		Smoothbutton.addActionListener(new DoSmoothingListener(this));
		displayCircle.addItemListener(new DisplayListener(this));
		displaySegments.addItemListener(new DisplayBoxListener(this));
		CurrentCurvaturebutton.addActionListener(new CurrentCurvatureListener(this));
		Curvaturebutton.addActionListener(new CurvatureListener(this));
		Displaybutton.addActionListener(new DisplayVisualListener(this, true));
		Anglebutton.addActionListener(new AngleListener(this));
		startT.addTextListener(new AutoStartListener(this));
		endT.addTextListener(new AutoEndListener(this));
		Redobutton.addActionListener(new RedoListener(this));
		Roibutton.addActionListener(new RoiListener(this));
		inputFieldZ.addTextListener(new ZlocListener(this, false));
		cutoffField.addTextListener(new InsideLocListener(this, false));
		minInlierField.addTextListener(new MinInlierLocListener(this, false));
		minperimeterField.addTextListener(new MinperimeterListener(this));
		maxSizeField.addTextListener(new MaxsizeListener(this));
		minSizeField.addTextListener(new MinsizeListener(this));
		numsegField.addTextListener(new DeltasepListener(this));
		maxperimeterField.addTextListener(new MaxperimeterListener(this));
		inputFieldT.addTextListener(new TlocListener(this, false));
		inputtrackField.addTextListener(new TrackidListener(this));
		inputFieldminpercent.addTextListener(new MinpercentListener(this));
		inputFieldIter.addTextListener(new MaxTryListener(this));
		ChooseDirectory.addActionListener(new SaveDirectory(this));
		inputField.addTextListener(new ETrackFilenameListener(this));
		Savebutton.addActionListener(new SaverListener(this));
		SaveAllbutton.addActionListener(new SaverAllListener(this));
		ChooseMethod.addActionListener(new DrawListener(this, ChooseMethod));
		ChooseColor.addActionListener(new ColorListener(this, ChooseColor));

		IlastikAuto.addItemListener(new IlastikListener(this));
		lowprobslider.addAdjustmentListener(new LowProbListener(this, lowprobText, lowprobstring, lowprobmin,
				lowprobmax, scrollbarSize, lowprobslider));
		highprobslider.addAdjustmentListener(new HighProbListener(this, highporbText, highprobstring, highprobmin,
				highprobmax, scrollbarSize, highprobslider));

		Clearmanual.addActionListener(new ClearforManual(this));
		ManualCompute.addActionListener(new ManualInterventionListener(this));
		maxSearchS.addAdjustmentListener(new ETrackMaxSearchListener(this, maxSearchText, maxSearchstring,
				maxSearchradiusMin, maxSearchradiusMax, scrollbarSize, maxSearchS));

		panelFirst.setVisible(true);
		cl.show(panelCont, "1");
		Cardframe.add(panelCont, "Center");
		Cardframe.add(jpb, "Last");

		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		if (!hide)
			Cardframe.setVisible(true);

	}

	public static <T extends Comparable<T> & Type<T>> void computeMinMax(final Iterable<T> input, final T min,
			final T max) {
		// create a cursor for the image (the order does not matter)
		final Iterator<T> iterator = input.iterator();

		// initialize min and max with the first image value
		T type = iterator.next();

		min.set(type);
		max.set(type);

		// loop over the rest of the data and determine min and max value
		while (iterator.hasNext()) {
			// we need this type more than once
			type = iterator.next();

			if (type.compareTo(min) < 0)
				min.set(type);

			if (type.compareTo(max) > 0)
				max.set(type);
		}
	}

	public static int round(double value) {
		return (int) (value + 0.5D * Math.signum(value));
	}


}
