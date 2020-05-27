package utility;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import curvatureComputer.Distance;
import ij.gui.*;
import pluginTools.InteractiveEmbryo;

public class DisplayAuto {

	public static void Display(final InteractiveEmbryo parent) {
		
		
		parent.overlay.clear();
		if (parent.ZTRois.size() > 0) {

			for (Map.Entry<String, Roiobject> entry : parent.ZTRois.entrySet()) {

				Roiobject currentobject = entry.getValue();
				if ( currentobject.thirdDimension == parent.thirdDimension) {
		


					if (currentobject.resultlineroi != null) {
						for (int i = 0; i < currentobject.resultlineroi.size(); ++i) {

							Line ellipse = currentobject.resultlineroi.get(i);
							ellipse.setStrokeColor(parent.colorLineA);

							ellipse.setStrokeWidth(1);
							parent.overlay.add(ellipse);

						}

					}
					
					if (currentobject.resultcurvelineroi != null ) {
						for (int i = 0; i < currentobject.resultcurvelineroi.size(); ++i) {

							OvalRoi ellipse = currentobject.resultcurvelineroi.get(i);
							ellipse.setStrokeColor(parent.colorPoints);

							
							parent.overlay.add(ellipse);

						}

					}
					
					if (currentobject.segmentrect != null && parent.displayIntermediateBox) {
						for (int i = 0; i < currentobject.segmentrect.size(); ++i) {

							Roi rect = currentobject.segmentrect.get(i);
							
							
							rect.setStrokeColor(parent.colorLineA);

							
							parent.overlay.add(rect);
						

						}

					}
					
					
					


					break;
				}

			}

			parent.imp.setOverlay(parent.overlay);
			parent.imp.updateAndDraw();

				
					
					DisplaySelected.markAll(parent);
					DisplaySelected.selectAll(parent);
					
			

		}
	}


	
	public static ArrayList<OvalRoi> DisplayPointInliers(ArrayList<double[]> currentlist) {

		ArrayList<OvalRoi> pointline = new ArrayList<OvalRoi>();

		for (double[] point : currentlist) {


			OvalRoi line = new OvalRoi(point[0], point[1], 1, 0);
			pointline.add(line);

		}

		return pointline;

	}


	

	
	public static void mark(final InteractiveEmbryo parent) {

		if (parent.ml != null)
			parent.imp.getCanvas().removeMouseMotionListener(parent.ml);
		parent.imp.getCanvas().addMouseMotionListener(parent.ml = new MouseMotionListener() {

			final ImageCanvas canvas = parent.imp.getWindow().getCanvas();
			Roi lastnearest = null;

			@Override
			public void mouseMoved(MouseEvent e) {

				int x = canvas.offScreenX(e.getX());
				int y = canvas.offScreenY(e.getY());

				final HashMap<Integer, double[]> loc = new HashMap<Integer, double[]>();

				loc.put(0, new double[] { x, y });

				double distmin = Double.MAX_VALUE;
				if (parent.tablesize > 0) {
					NumberFormat f = NumberFormat.getInstance();
					for (int row = 0; row < parent.tablesize; ++row) {
						String CordX = (String) parent.table.getValueAt(row, 1);
						String CordY = (String) parent.table.getValueAt(row, 2);

						String CordZ = (String) parent.table.getValueAt(row, 3);

						double dCordX = 0, dCordZ = 0, dCordY = 0;
						try {
							dCordX = f.parse(CordX).doubleValue();

							dCordY = f.parse(CordY).doubleValue();
							dCordZ = f.parse(CordZ).doubleValue();
						} catch (ParseException e1) {

						}
						double dist = Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y });
						if (Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y }) < distmin
								&& parent.thirdDimension == (int) dCordZ && parent.ndims > 3) {

							parent.rowchoice = row;
							distmin = dist;

						}
						if (Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y }) < distmin
								&& parent.ndims <= 3) {

							parent.rowchoice = row;
							distmin = dist;

						}

					}

					parent.table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
						@Override
						public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
								boolean hasFocus, int row, int col) {

							super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
							if (row == parent.rowchoice) {
								setBackground(Color.green);

							} else {
								setBackground(Color.white);
							}
							return this;
						}
					});

					parent.table.validate();
					parent.scrollPane.validate();
					parent.panelSecond.repaint();
					parent.panelSecond.validate();

				}

			}

			@Override
			public void mouseDragged(MouseEvent e) {

			}

		});

	}
}
