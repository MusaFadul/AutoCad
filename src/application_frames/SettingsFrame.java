package application_frames;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import custom_components.CustomJFrame;
import tester.MainFrame;
import toolset.Settings;
import toolset.Tools;

import java.awt.Color;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JToggleButton;

/**
 * Panel for making general drawing settings
 * 
 * WORK IN PROGRESS
 * 
 * @author OlumideEnoch
 *
 */

public class SettingsFrame extends CustomJFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Dimension appSize = new Dimension(750,750);

	/**
	 * Create the frame.
	 */
	public SettingsFrame() {
		
		addWindowListener(new WindowAdapter() {
			@Override 
			public void windowClosing(WindowEvent e) { 
				handleWindowClosingEvent();
			} 
		});
		
		setBounds((Settings.windowSize.width - appSize.width) / 2, (Settings.windowSize.height - appSize.height) / 2, appSize.width, appSize.height);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(0, 0, 744, 221);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(Tools.getIconImage("/images/settings.png", 125, 125));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(594, 24, 133, 143);
		panel.add(lblNewLabel);
		
		JLabel lblGeneralSeetings = new JLabel("General settings");
		lblGeneralSeetings.setForeground(Color.GRAY);
		lblGeneralSeetings.setFont(new Font("Tahoma", Font.BOLD, 22));
		lblGeneralSeetings.setBounds(24, 24, 273, 52);
		panel.add(lblGeneralSeetings);
		
		JLabel lblSetUpGeneral = new JLabel("Set up general drawing preferences");
		lblSetUpGeneral.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblSetUpGeneral.setBounds(49, 87, 328, 20);
		panel.add(lblSetUpGeneral);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 232, 744, 410);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(10, 11, 278, 388);
		panel_1.add(panel_2);
		panel_2.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("Grid");
		lblNewLabel_1.setBounds(10, 11, 146, 27);
		panel_2.add(lblNewLabel_1);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JLabel lblSnapping = new JLabel("Snapping");
		lblSnapping.setBounds(10, 100, 146, 27);
		panel_2.add(lblSnapping);
		lblSnapping.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JLabel lblGrid = new JLabel("Ortho mode");
		lblGrid.setBounds(10, 181, 146, 27);
		panel_2.add(lblGrid);
		lblGrid.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JLabel lblAutoTracking = new JLabel("Auto tracking with tips");
		lblAutoTracking.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblAutoTracking.setBounds(10, 267, 146, 27);
		panel_2.add(lblAutoTracking);
		
		JToggleButton gridToggle = new JToggleButton("ON");
		gridToggle.setSelected(true);
		gridToggle.setBounds(10, 49, 71, 40);
		gridToggle.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent ev) {
				
				MainFrame.panel.toggleGrid();
				
				if(ev.getStateChange()== ItemEvent.SELECTED){
					gridToggle.setBackground(Color.RED);
					gridToggle.setText("ON");
					
			      } 

				if(ev.getStateChange() == ItemEvent.DESELECTED){
					gridToggle.setBackground(Color.DARK_GRAY);
			    	 gridToggle.setText("OFF");
			    	 gridToggle.setForeground(Color.WHITE);
			    	 System.out.println("DisEnabled");
			      }
			}
			
		});
		
		panel_2.add(gridToggle);
		
		JPanel conclusionPanel = new JPanel();
		conclusionPanel.setBounds(10, 653, 724, 68);
		contentPane.add(conclusionPanel);
		conclusionPanel.setLayout(null);
		
		JButton closeButton = new JButton("Close");
		closeButton.setForeground(Color.WHITE);
		closeButton.setBackground(Color.RED);
		closeButton.setBounds(460, 11, 126, 44);
		conclusionPanel.add(closeButton);
		
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				handleWindowClosingEvent();
			}
			
		});
		
		JButton btnFinish = new JButton("Finish");
		btnFinish.setForeground(Color.WHITE);
		btnFinish.setBackground(Color.BLACK);
		btnFinish.setBounds(596, 11, 118, 44);
		conclusionPanel.add(btnFinish);
		
		btnFinish.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				handleWindowClosingEvent();
			}
			
		});
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.DARK_GRAY);
		separator.setBounds(0, 652, 744, 2);
		contentPane.add(separator);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setForeground(Color.DARK_GRAY);
		separator_1.setBounds(0, 223, 744, 2);
		contentPane.add(separator_1);
	}

	private void handleWindowClosingEvent() {
		dispose();
	}
}
