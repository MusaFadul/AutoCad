package application_frames;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import core_components.ToolIconButton;
import custom_components.CustomJFrame;
import database.DatabaseConnection;
import java.sql.SQLException;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays the connected databases, tables and the layers.<br>
 * Layers can also be added to the drawing panel directly 
 * and can be deleted from the database as well.<br>
 * <p>
 * Realised using the JTree, it composed of three major nodes (steps).<br>
 * The top node is the connected database name, the next node is the table name<br>
 * where all the drawn items grouped in layers are found, the last node is for the
 * list of all the layer names stored in the database table.<br>
 * <p>
 * database name			// : node level 1 <br>
 * 		table name			// : node level 2 <br>
 * 			layer 1			// : node level 3 <br>
 * 			layer 2			// : node level 3 <br>
 * 			layer n			// : node level 3 <br>
 * <p>
 * At each launch, it uses the database connection at the mainframe and retrives all the
 * data needed to display the required information as described earlier.<br>
 * <p>
 * It implements tree selection listener which turns  the "add" and "delete" button off 
 * and on based on the positon of the currently selected node.<br>
 * <p>
 * Because the database and the geotaable cannot be deleted during the session, the add and 
 * delete buttons are only available when a layer (lowest node) is selected.<br>
 * <p>
 * It does not support drag and drop.
 * 
 * @author Olumide Igbiloba
 * 
 * @since Dec 14, 2017
 * @version
 * a. Dec 17, 2017 - Commented the functionality to create a new empty layer from the catalog due to 
 * inability if the database to store empty layer without features.
 * b. Dec 28, 2017 - Auto-refresh node tree when new layer have been added to the database
 *
 */
public class DatabaseCatalog extends CustomJFrame implements TreeSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2250678265542531251L;

	/**JTree representing rhe database, table and the layers contained*/
	private JTree dbTree = null;
	private DefaultMutableTreeNode geoTable;
	/**Button for deleting layer on the JTree*/
	private ToolIconButton delete; //addLayer;
	/**Button adding selected layers to the drawing panel*/
	private ToolIconButton addToPanel;
	

	/**
	 * Creates the Frame for the Database Catalog
	 * @param dbConnection the database connection object
	 */
	public DatabaseCatalog(DatabaseConnection dbConnection) {
		
		super("Database");
		
		JPanel contentPane;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(SettingsFrame.window.getBounds().x + SettingsFrame.window.getBounds().width - 300, MainFrame.panel.getBounds().y, 300, 750);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		addDatabaseContents(dbConnection);
		
		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.inactiveCaption);
		panel.setBounds(10, 11, 274, 699);
		contentPane.add(panel);
		panel.setLayout(null);
		
		dbTree.addTreeSelectionListener(this);
		
		JScrollPane scrollPane = new JScrollPane(dbTree);
		scrollPane.setBounds(0, 42, 274, 657);
		panel.add(scrollPane);
		
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		/*addLayer = new ToolIconButton("Add new layer", "/images/add_layer.png", 30, 30);
		addLayer.setEnabled(false);
		addLayer.setBounds(244, 6, 30, 30);
		panel.add(addLayer);*/
		
		delete = new ToolIconButton("Delete", "/images/delete.png", 25, 25);
		delete.setBounds(234, 6, 30, 30);
		delete.setEnabled(false);
		panel.add(delete);
		
		addToPanel = new ToolIconButton("Add to panel", "/images/plus.png", 20, 25);
		addToPanel.setEnabled(false);
		addToPanel.setBounds(194, 6, 30, 30);
		panel.add(addToPanel);
		
		addToPanel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				addSelectedLayerToDrawingPanel((TreePath[]) dbTree.getSelectionPaths());
			}
		});
		
		JLabel infoLabel = new JLabel("Connected database(s)");
		infoLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		infoLabel.setBounds(10, 11, 139, 20);
		panel.add(infoLabel);
		
		// Expand the rows
		expandAllRows();
		
		
		
		/*addLayer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				try {
					// 1. Create list of possible geometry
					// -----------------------------------
					String[] geom = {Settings.POLYGON_GEOMETRY, Settings.POLYLINE_GEOMETRY, Settings.POINT_GEOMETRY};
					
					// 2. Put list inside a combo box
					// ------------------------------
					JComboBox<String> geomList = new JComboBox<String>(geom);
					
				    // a. Create a Jpanel and set the layout
					
				    JPanel layerPanel = new JPanel();
				    layerPanel.setLayout(new GridLayout(4,1));
				  
				    // b. Create the text fields
				    String autoGeneratedLayerName = "New_Layer" + TableOfContents.getNewLayerID();
				    JTextField layerNameTextField = new JTextField(autoGeneratedLayerName);
				   
				    // c. Add componets to panel
				    layerPanel.add(new JLabel("Add a new layer"));
				    layerPanel.add(new JSeparator());
				    layerPanel.add(geomList);
				    layerPanel.add(layerNameTextField);
					
					// 3. Show a JOption pane to select a new geometry type
					// -----------------------------------------------------
					int response = JOptionPane.showConfirmDialog( null, layerPanel , "Choose geometry type", JOptionPane.OK_CANCEL_OPTION);
					
					// 4. On OK option
					//-------------------------------------
					if(response == JOptionPane.OK_OPTION) {
						
						// 4.1 Get the selected item on the combo box
						String layerName = layerNameTextField.getText().toString();
						
						// 4.2 If text field was empty, use the autogenerated name
						if(layerName.length() < 1) { layerName = autoGeneratedLayerName; };
						
						// 4.3 Create an empty layer
						Layer layer = new Layer(TableOfContents.getNewLayerID(), true, geomList.getSelectedItem().toString(), layerName);
						
						
						// 4.4 Proceed to add to the JTree
						
						// a. Get the model and the root of the JTree
						DefaultTreeModel model = (DefaultTreeModel) (dbTree.getModel());
						DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
						
						// b. Create a JTree node for the new layer
						DefaultMutableTreeNode newlayerNode = new DefaultMutableTreeNode(layer.getLayerName());
						newlayerNode.setUserObject(layer.getLayerType());
						
						// c. Get the current selected path (this should be on a table name)!
						TreePath treePath = (TreePath) dbTree.getSelectionPath();
						
						// d. Get the table node that was clicked
						DefaultMutableTreeNode tableNameNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
						
						// e. Insert the new layer into the table name
						model.insertNodeInto(newlayerNode, tableNameNode, tableNameNode.getChildCount());
						
						// f. Notify the model and update the root on changes
						model.reload(root);
						
						// 4.5 Finally add to DB
						MainFrame.saveLayerToDB(layer);
						
						System.out.println((MainFrame.dbConnection.getTables()).size());
						for(String table : MainFrame.dbConnection.getTables()) {
							System.out.println(table);
						}
					}
				} 
				catch (Exception e1) {
					e1.printStackTrace();
					infoLabel.setText(e1.getMessage());
				}
			}
		});
		*/
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteSelectedLayers(dbTree.getSelectionPaths());	
			}
		});
	}
	
	/**
	 * Returns the dbTree
	 * @return the dbTree
	 */
	public JTree getDbTree() {
		return dbTree;
	}

	/**
	 * Sets the dbTree
	 * @param dbTree the dbTree to set
	 */
	public void setDbTree(JTree dbTree) {
		this.dbTree = dbTree;
	}

	/**
	 * Add new layer to the geo table node
	 * @param layerName new layer name
	 */
	public void addNewLayerToNode(String layerName) {
		
		DefaultMutableTreeNode layers = new DefaultMutableTreeNode(layerName);
		layers.setUserObject(layerName);
		geoTable.add(layers);
		
		DefaultTreeModel model = (DefaultTreeModel)dbTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)dbTree.getModel().getRoot();
		model.reload(root);
		dbTree.revalidate();
		expandAllRows();
	}

	/**
	 * Expands the JTree.<br>
	 * This is used when a new node is added to keep the entirely JTree open
	 */
	private void expandAllRows() {
		
		for(int i=0;i<dbTree.getRowCount();i++)
		{
			dbTree.expandRow(i);
		}
	}

	/**
	 * Deletes selected layers on the tree.<br>
	 * Attempts to delete from the database first,
	 * if layer have been successfully deleted from the database
	 * then it removes the layer from the JTree.
	 * @param treePaths the currently selected tree paths to delete
	 */
	private void deleteSelectedLayers(TreePath[] treePaths) {
		
		// Retrieve the JTree model
		DefaultTreeModel model = (DefaultTreeModel) (dbTree.getModel());
		
		// Loop through the tree paths
		for(TreePath tree : treePaths) {
			// Ensure the the 3 level is selected i.e the layer
			if(tree.getPath().length > 2) {
				// Get the path
				Object[] layerPath = tree.getPath();
				// The table name is at the index of 2
				String tableName = String.valueOf(layerPath[2]);
				
				// Try to delete from the database first
				boolean deletedFromDB = false;
				try {
					
					MainFrame.dbConnection.dropTable(tableName);
					deletedFromDB = true;
					
				} catch (SQLException e1) {
					e1.printStackTrace();
					deletedFromDB = false;
					JOptionPane.showMessageDialog(null, "Error", "Cannot delete table from Database", JOptionPane.OK_OPTION);
				}
				// If layer have been successfully deleted from the database
				// Remove the layer form the JTree
				if(deletedFromDB) {
					model.removeNodeFromParent((DefaultMutableTreeNode) (tree.getLastPathComponent()));
				}
			} 
		}
	}

	/**
	 * Uses the database connection to show the currently connected database, table and list of available layers
	 * @param dbConnection database connection object to use
	 */
	private void addDatabaseContents(DatabaseConnection dbConnection) {
		
		// 0. Empty the main node and the geo table
		DefaultMutableTreeNode db = null;
		geoTable = null;
		
		// 1. Only if the connection object is valid/ ok
		if(dbConnection != null) {
	
			try {
				// 1.1 Set up the names of the main node (db) and the geo table
				db = new DefaultMutableTreeNode(DatabaseConnection.dbName);
				geoTable = new DefaultMutableTreeNode("geo_data");
				
				// 1.2 Get all the names available in the database
				for(String[] table : dbConnection.getTables()){
					
					// Create a node from the table name
					DefaultMutableTreeNode layers = new DefaultMutableTreeNode(table[0]);
					
					// Embed the name as a property
					layers.setUserObject(table[0]);
					
					// Add to the geo table
					geoTable.add(layers);		
				};
				// 1.3 Add all to the main node
				db.add(geoTable);
				// 1.4 Create a new dbTree and add the main node
				dbTree = new JTree(db);
				dbTree.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			} catch (SQLException e) {
				MainFrame.log("An error occured : " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Adds the currently selected node to the drawing panel.<br>
	 * Supports multiple layer selection.
	 * @param treePaths current db selection path
	 */
	private void addSelectedLayerToDrawingPanel(TreePath[] treePaths) {
		
		//TreePath[] treePaths = (TreePath[]) dbTree.getSelectionPaths();
		try {
			for(TreePath tree : treePaths) {
				
				if(tree.getPath().length > 2) {

					Object[] layerPath = tree.getPath();
					
					String tableName = String.valueOf(layerPath[2]);
					
					MainFrame.createLayerFromResultSet(MainFrame.dbConnection.readTable(tableName), tableName);
					
				}
			}
		
		} catch(SQLException e1) {
			MainFrame.log("An error has occured: " + e1.getMessage());
			e1.printStackTrace();
		}
	}

	/**
	 * Method for handling Changed Values 
	 * @param e the TreeSelectionEvent to set
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e) {

		TreePath[] node = (TreePath[]) dbTree.getSelectionPaths();
		
		try {
			for(TreePath tree : node) {
				
				if(tree.getPath().length > 2) {
	
					delete.setEnabled(true);
					addToPanel.setEnabled(true);
					
				} else {
					delete.setEnabled(false);
					addToPanel.setEnabled(false);
				}
				
				/*if(tree.getPath().length == 2) {
					addLayer.setEnabled(true);
				} else
					addLayer.setEnabled(false);*/
					
			}
		} catch(Exception e1) {
			
		}
	}
}
