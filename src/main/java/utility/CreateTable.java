package utility;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import embryoDetector.Embryoobject;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveEmbryo;

public class CreateTable {

	
	
	
	
	
	public static void CreateTableView(final InteractiveEmbryo parent) {

		parent.resultPerimeter = new ArrayList<Pair<String, double[]>>();

		for (Pair<String, ArrayList<Embryoobject>> TrackCelltime : parent.Tracklist) {

			ArrayList<Embryoobject> Celltime = TrackCelltime.getB();
			
			
			double perimeter = 0;
			double time = Celltime.get(0).t;
			for (Embryoobject Cell: Celltime)
			
				perimeter += Cell.perimeter;
				
			String ID = TrackCelltime.getA();
			
			parent.resultPerimeter.add(new ValuePair<String, double[]>(ID,
					new double[] { time, perimeter }));
	

		}
		Object[] colnames = new Object[] { "Track Id", "Location X", "Location Y", "Perimeter" };

		Object[][] rowvalues = new Object[0][colnames.length];

		rowvalues = new Object[parent.Finalcurvatureresult.size()][colnames.length];

		parent.table = new JTable(rowvalues, colnames);
		parent.row = 0;
		NumberFormat f = NumberFormat.getInstance();
		for (Map.Entry<String, ArrayList<Embryoobject>> entry : parent.Finalcurvatureresult.entrySet()) {

			ArrayList<Embryoobject> Celltime = entry.getValue();
			double perimeter = 0;
			double time = Celltime.get(0).t;
			for (Embryoobject Cell: Celltime)
			
				perimeter += Cell.perimeter;
			parent.table.getModel().setValueAt(entry.getKey(), parent.row, 0);
			parent.table.getModel().setValueAt(f.format(Celltime.get(0).center[0]), parent.row, 1);
			parent.table.getModel().setValueAt(f.format(Celltime.get(0).center[1]), parent.row, 2);
			parent.table.getModel().setValueAt(f.format(perimeter), parent.row, 3);

			parent.row++;

			parent.tablesize = parent.row;
		}

		parent.PanelSelectFile.removeAll();

		parent.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		

		parent.scrollPane = new JScrollPane(parent.table);
	

		parent.scrollPane.getViewport().add(parent.table);
		parent.scrollPane.setAutoscrolls(true);
		parent.PanelSelectFile.add(parent.scrollPane, BorderLayout.CENTER);

		parent.PanelSelectFile.setBorder(parent.selectfile);

		parent.panelSecond.add(parent.PanelSelectFile, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.add(parent.inputLabel, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.add(parent.inputField, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.add(parent.inputtrackLabel, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.add(parent.inputtrackField, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.add(parent.ChooseDirectory, new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		parent.Original.add(parent.Savebutton, new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		parent.Original.setBorder(parent.origborder);

		parent.panelSecond.add(parent.Original, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
		parent.inputField.setEnabled(true);
		parent.inputtrackField.setEnabled(true);
		parent.Savebutton.setEnabled(true);
		parent.ChooseDirectory.setEnabled(true);
		parent.Original.repaint();
		parent.Original.validate();
		parent.PanelSelectFile.repaint();
		parent.PanelSelectFile.validate();
		parent.table.repaint();
		parent.table.validate();
		parent.panelSecond.repaint();
		parent.panelSecond.validate();
		parent.Cardframe.repaint();
		parent.Cardframe.validate();
	}
	
}
