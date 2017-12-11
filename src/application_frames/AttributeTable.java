package application_frames;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import core_classes.Feature;
import core_classes.Layer;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Shows attribute table of all features in a layer
 * 
 * WORK IN PROGRESS
 * 
 * TODO: Support for showing more than one layer
 * TODO: Add table listener to show selected feature on the panel
 * 
 * @author OlumideEnoch
 *
 */
public class AttributeTable extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2510826749504059745L;
	
	private JPanel contentPane;
	
	private JTable table = new JTable();
	
	private DefaultTableModel tableModel;
	
	private Layer layer;

	/**
	 * Create the frame.
	 * @param features 
	 */
	public AttributeTable(Layer layer) {
		
		this.layer = layer;
		
		addWindowListener(new WindowAdapter() {
			@Override 
			public void windowClosing(WindowEvent e) { 
				handleWindowClosingEvent(e);
			} 
		});
		
		seTable();
		setAlwaysOnTop(true);
		setBounds(100, 100, 450, 750);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.inactiveCaption);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setBounds(10, 11, 414, 655);
		contentPane.add(scrollPane);
		
		JButton btnButton = new JButton("Close");
		btnButton.setBounds(312, 677, 112, 23);
		contentPane.add(btnButton);
		
		btnButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	protected void handleWindowClosingEvent(WindowEvent e) {
		dispose();
	}

	private void seTable() {
		
		String[] columnNames = { "FID*", "ID", "Geometry", "LayerName", "LayeID"};
		
		Object[][] data = { };
		
		table = new JTable();
		tableModel = new DefaultTableModel(data, columnNames);
		table.setModel(tableModel);
		
		// FID
		table.getColumnModel().getColumn(0).setPreferredWidth(5);
		
		// ID
		table.getColumnModel().getColumn(1).setPreferredWidth(5);
		
		int count = 0;
		for(Feature feature : layer.getListOfFeatures()) {
			
			Object[] fdata = {count, feature.getId(), layer.getLayerType(), layer.getLayerName(), layer.getId() } ;
			tableModel.addRow(fdata);
			
			count++;
		}
	}
}
