package photonics.wg.bend;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import mathLib.util.CustomJFileChooser;
import mathLib.util.MathUtils;
import photonics.wg.bend.gds2.Bend180degBezierGDSModule;
import photonics.wg.bend.gds2.Bend180degCircularGDSModule;
import photonics.wg.bend.gds2.Bend180degEulerGDSModule;
import photonics.wg.bend.gds2.Bend180degHybridGDSModule;
import photonics.wg.bend.gds2.Bend180degOptimalGDSModule;
import photonics.wg.bend.gds2.Bend90degBezierGDSModule;
import photonics.wg.bend.gds2.Bend90degCircularGDSModule;
import photonics.wg.bend.gds2.Bend90degEulerGDSModule;
import photonics.wg.bend.gds2.Bend90degHybridGDSModule;
import photonics.wg.bend.gds2.Bend90degOptimalGDSModule;

import java.awt.Toolkit;

public class BendWgGUI extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = -8071101530008067621L;
	private JPanel contentPane;
	private JTextField path;
	private JTextField aTextField;
	private JTextField bTextField;
	private JTextField R0TextField;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	double a, b, R0, width ;
	int numPoints ;
	String filePath;
	private JRadioButton rdbtnBezier90Degree;
	private JRadioButton rdbtnClothoid90Degree;
	private JRadioButton rdbtnCircular90Degree;
	private JRadioButton rdbtnBezier180Degree;
	private JRadioButton rdbtnOptimal90Degree;
	private JRadioButton rdbtnOptimal180Degree;
	private JRadioButton rdbtnEuler90Degree;
	private JRadioButton rdbtnEuler180Degree;
	private JRadioButton rdbtnCircular180Degree;
	private JRadioButton rdbtnClothoid180Degree;
	private JLabel lblWum;
	private JTextField wTextField;
	private JLabel lblPoints;
	private JTextField pointsTextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BendWgGUI frame = new BendWgGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public BendWgGUI() {
		setTitle("Bend Creator V1.0");
		setIconImage(Toolkit.getDefaultToolkit().getImage(BendWgGUI.class.getResource("/com/sun/javafx/scene/control/skin/modena/HTMLEditor-Paste-Black.png")));
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 421, 483);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 168, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "gds file path", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		contentPane.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel lblSaveGds = new JLabel("save folder : ");
		GridBagConstraints gbc_lblSaveGds = new GridBagConstraints();
		gbc_lblSaveGds.insets = new Insets(0, 5, 0, 5);
		gbc_lblSaveGds.anchor = GridBagConstraints.EAST;
		gbc_lblSaveGds.gridx = 0;
		gbc_lblSaveGds.gridy = 0;
		panel.add(lblSaveGds, gbc_lblSaveGds);

		path = new JTextField();
		GridBagConstraints gbc_path = new GridBagConstraints();
		gbc_path.insets = new Insets(0, 0, 0, 5);
		gbc_path.fill = GridBagConstraints.HORIZONTAL;
		gbc_path.gridx = 1;
		gbc_path.gridy = 0;
		panel.add(path, gbc_path);
		path.setColumns(10);

		JButton choose = new JButton("choose...");
		choose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseActionPerformed(e);
			}
		});

		GridBagConstraints gbc_choose = new GridBagConstraints();
		gbc_choose.gridx = 2;
		gbc_choose.gridy = 0;
		panel.add(choose, gbc_choose);

		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 1;
		contentPane.add(panel_2, gbc_panel_2);
		panel_2.setBorder(new TitledBorder(null, "loss model", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		JLabel lblA = new JLabel("a (dB/cm) : ");
		GridBagConstraints gbc_lblA = new GridBagConstraints();
		gbc_lblA.insets = new Insets(0, 0, 5, 5);
		gbc_lblA.anchor = GridBagConstraints.EAST;
		gbc_lblA.gridx = 0;
		gbc_lblA.gridy = 0;
		panel_2.add(lblA, gbc_lblA);

		aTextField = new JTextField();
		aTextField.setText("181.98");
		GridBagConstraints gbc_aTextField = new GridBagConstraints();
		gbc_aTextField.insets = new Insets(0, 0, 5, 0);
		gbc_aTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_aTextField.gridx = 1;
		gbc_aTextField.gridy = 0;
		panel_2.add(aTextField, gbc_aTextField);
		aTextField.setColumns(10);

		JLabel lblB = new JLabel("b : ");
		GridBagConstraints gbc_lblB = new GridBagConstraints();
		gbc_lblB.anchor = GridBagConstraints.EAST;
		gbc_lblB.insets = new Insets(0, 0, 0, 5);
		gbc_lblB.gridx = 0;
		gbc_lblB.gridy = 1;
		panel_2.add(lblB, gbc_lblB);

		bTextField = new JTextField();
		bTextField.setText("2.49");
		GridBagConstraints gbc_bTextField = new GridBagConstraints();
		gbc_bTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_bTextField.gridx = 1;
		gbc_bTextField.gridy = 1;
		panel_2.add(bTextField, gbc_bTextField);
		bTextField.setColumns(10);

		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 2;
		contentPane.add(panel_3, gbc_panel_3);
		panel_3.setBorder(new TitledBorder(null, "type of bend", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_panel_3.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel_3.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_3.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_3.setLayout(gbl_panel_3);

		rdbtnCircular90Degree = new JRadioButton("Circular 90 degree");
		buttonGroup.add(rdbtnCircular90Degree);
		GridBagConstraints gbc_rdbtnCircular90Degree = new GridBagConstraints();
		gbc_rdbtnCircular90Degree.anchor = GridBagConstraints.WEST;
		gbc_rdbtnCircular90Degree.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnCircular90Degree.gridx = 0;
		gbc_rdbtnCircular90Degree.gridy = 0;
		panel_3.add(rdbtnCircular90Degree, gbc_rdbtnCircular90Degree);

		rdbtnCircular180Degree = new JRadioButton("Circular 180 degree");
		buttonGroup.add(rdbtnCircular180Degree);
		GridBagConstraints gbc_rdbtnCircular180Degree = new GridBagConstraints();
		gbc_rdbtnCircular180Degree.anchor = GridBagConstraints.WEST;
		gbc_rdbtnCircular180Degree.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnCircular180Degree.gridx = 1;
		gbc_rdbtnCircular180Degree.gridy = 0;
		panel_3.add(rdbtnCircular180Degree, gbc_rdbtnCircular180Degree);


		rdbtnOptimal90Degree = new JRadioButton("Optimal 90 degree");
		buttonGroup.add(rdbtnOptimal90Degree);
		GridBagConstraints gbc_rdbtnOptimal90Degree = new GridBagConstraints();
		gbc_rdbtnOptimal90Degree.anchor = GridBagConstraints.WEST;
		gbc_rdbtnOptimal90Degree.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnOptimal90Degree.gridx = 0;
		gbc_rdbtnOptimal90Degree.gridy = 1;
		panel_3.add(rdbtnOptimal90Degree, gbc_rdbtnOptimal90Degree);

		rdbtnOptimal180Degree = new JRadioButton("Optimal 180 degree");
		buttonGroup.add(rdbtnOptimal180Degree);
		GridBagConstraints gbc_rdbtnOptimal180Degree = new GridBagConstraints();
		gbc_rdbtnOptimal180Degree.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnOptimal180Degree.anchor = GridBagConstraints.WEST;
		gbc_rdbtnOptimal180Degree.gridx = 1;
		gbc_rdbtnOptimal180Degree.gridy = 1;
		panel_3.add(rdbtnOptimal180Degree, gbc_rdbtnOptimal180Degree);

		rdbtnBezier90Degree = new JRadioButton("Bezier 90 degree");
		buttonGroup.add(rdbtnBezier90Degree);
		GridBagConstraints gbc_rdbtnBezier90Degree = new GridBagConstraints();
		gbc_rdbtnBezier90Degree.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnBezier90Degree.anchor = GridBagConstraints.WEST;
		gbc_rdbtnBezier90Degree.gridx = 0;
		gbc_rdbtnBezier90Degree.gridy = 2;
		panel_3.add(rdbtnBezier90Degree, gbc_rdbtnBezier90Degree);

		rdbtnBezier180Degree = new JRadioButton("Bezier 180 degree");
		buttonGroup.add(rdbtnBezier180Degree);
		GridBagConstraints gbc_rdbtnBezier180Degree = new GridBagConstraints();
		gbc_rdbtnBezier180Degree.anchor = GridBagConstraints.WEST;
		gbc_rdbtnBezier180Degree.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnBezier180Degree.gridx = 1;
		gbc_rdbtnBezier180Degree.gridy = 2;
		panel_3.add(rdbtnBezier180Degree, gbc_rdbtnBezier180Degree);

		rdbtnEuler90Degree = new JRadioButton("Euler 90 degree");
		buttonGroup.add(rdbtnEuler90Degree);
		GridBagConstraints gbc_rdbtnEuler90Degree = new GridBagConstraints();
		gbc_rdbtnEuler90Degree.anchor = GridBagConstraints.WEST;
		gbc_rdbtnEuler90Degree.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnEuler90Degree.gridx = 0;
		gbc_rdbtnEuler90Degree.gridy = 3;
		panel_3.add(rdbtnEuler90Degree, gbc_rdbtnEuler90Degree);

		rdbtnEuler180Degree = new JRadioButton("Euler 180 degree");
		buttonGroup.add(rdbtnEuler180Degree);
		GridBagConstraints gbc_rdbtnEuler180Degree = new GridBagConstraints();
		gbc_rdbtnEuler180Degree.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnEuler180Degree.anchor = GridBagConstraints.WEST;
		gbc_rdbtnEuler180Degree.gridx = 1;
		gbc_rdbtnEuler180Degree.gridy = 3;
		panel_3.add(rdbtnEuler180Degree, gbc_rdbtnEuler180Degree);

		rdbtnClothoid90Degree = new JRadioButton("Hybrid 90 degree");
		buttonGroup.add(rdbtnClothoid90Degree);
		GridBagConstraints gbc_rdbtnClothoid90Degree = new GridBagConstraints();
		gbc_rdbtnClothoid90Degree.anchor = GridBagConstraints.WEST;
		gbc_rdbtnClothoid90Degree.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnClothoid90Degree.gridx = 0;
		gbc_rdbtnClothoid90Degree.gridy = 4;
		panel_3.add(rdbtnClothoid90Degree, gbc_rdbtnClothoid90Degree);

		rdbtnClothoid180Degree = new JRadioButton("Hybrid 180 degree");
		buttonGroup.add(rdbtnClothoid180Degree);
		GridBagConstraints gbc_rdbtnClothoid180Degree = new GridBagConstraints();
		gbc_rdbtnClothoid180Degree.anchor = GridBagConstraints.WEST;
		gbc_rdbtnClothoid180Degree.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnClothoid180Degree.gridx = 1;
		gbc_rdbtnClothoid180Degree.gridy = 4;
		panel_3.add(rdbtnClothoid180Degree, gbc_rdbtnClothoid180Degree);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 3;
		contentPane.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 30, 0, 0, 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		lblWum = new JLabel("W (um) : ");
		GridBagConstraints gbc_lblWum = new GridBagConstraints();
		gbc_lblWum.fill = GridBagConstraints.VERTICAL;
		gbc_lblWum.anchor = GridBagConstraints.EAST;
		gbc_lblWum.insets = new Insets(0, 0, 5, 5);
		gbc_lblWum.gridx = 0;
		gbc_lblWum.gridy = 0;
		panel_1.add(lblWum, gbc_lblWum);

		wTextField = new JTextField();
		wTextField.setText("0.4");
		wTextField.setColumns(10);
		GridBagConstraints gbc_wTextField = new GridBagConstraints();
		gbc_wTextField.anchor = GridBagConstraints.SOUTH;
		gbc_wTextField.insets = new Insets(0, 0, 5, 5);
		gbc_wTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_wTextField.gridx = 1;
		gbc_wTextField.gridy = 0;
		panel_1.add(wTextField, gbc_wTextField);

		JLabel lblR = new JLabel("R0 (um) : ");
		GridBagConstraints gbc_lblR = new GridBagConstraints();
		gbc_lblR.insets = new Insets(0, 0, 5, 5);
		gbc_lblR.anchor = GridBagConstraints.EAST;
		gbc_lblR.gridx = 0;
		gbc_lblR.gridy = 1;
		panel_1.add(lblR, gbc_lblR);

		R0TextField = new JTextField();
		GridBagConstraints gbc_R0TextField = new GridBagConstraints();
		gbc_R0TextField.insets = new Insets(0, 0, 5, 5);
		gbc_R0TextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_R0TextField.gridx = 1;
		gbc_R0TextField.gridy = 1;
		panel_1.add(R0TextField, gbc_R0TextField);
		R0TextField.setColumns(10);

		JButton gdsButton = new JButton("create GDS");
		gdsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gdsButtonActionPerformed(e);
			}
		});

		lblPoints = new JLabel("Points: ");
		GridBagConstraints gbc_lblPoints = new GridBagConstraints();
		gbc_lblPoints.anchor = GridBagConstraints.EAST;
		gbc_lblPoints.insets = new Insets(0, 0, 5, 5);
		gbc_lblPoints.gridx = 0;
		gbc_lblPoints.gridy = 2;
		panel_1.add(lblPoints, gbc_lblPoints);

		pointsTextField = new JTextField();
		pointsTextField.setText("100");
		pointsTextField.setColumns(10);
		GridBagConstraints gbc_pointsTextField = new GridBagConstraints();
		gbc_pointsTextField.insets = new Insets(0, 0, 5, 5);
		gbc_pointsTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_pointsTextField.gridx = 1;
		gbc_pointsTextField.gridy = 2;
		panel_1.add(pointsTextField, gbc_pointsTextField);

		GridBagConstraints gbc_gdsButton = new GridBagConstraints();
		gbc_gdsButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_gdsButton.insets = new Insets(0, 0, 0, 5);
		gbc_gdsButton.gridx = 1;
		gbc_gdsButton.gridy = 3;
		panel_1.add(gdsButton, gbc_gdsButton);
	}

	protected void chooseActionPerformed(ActionEvent e) {
		CustomJFileChooser fChooser = new CustomJFileChooser();
		fChooser.openDirectory();
		try {
			filePath = fChooser.getCurrentPath();
		} catch (Exception ex) {
			filePath = fChooser.getSelectedDir();
		}

		path.setText(filePath);
	}

	protected void gdsButtonActionPerformed(ActionEvent e) {
		a = MathUtils.evaluate(aTextField.getText());
		b = MathUtils.evaluate(bTextField.getText());
		R0 = MathUtils.evaluate(R0TextField.getText());
		width = MathUtils.evaluate(wTextField.getText()) ;
		numPoints = (int) MathUtils.evaluate(pointsTextField.getText()) ;
		if (rdbtnCircular90Degree.isSelected()) {
			Bend90degCircularGDSModule circ90Deg = new Bend90degCircularGDSModule(a, b, R0);
			circ90Deg.setWidth(width);
			circ90Deg.setNumPoints(numPoints);
			circ90Deg.createGDS(filePath, false);
		}
		if (rdbtnOptimal90Degree.isSelected()) {
			Bend90degOptimalGDSModule opt90Deg = new Bend90degOptimalGDSModule(a, b, R0);
			opt90Deg.setWidth(width);
			opt90Deg.setNumPoints(numPoints);
			opt90Deg.createGDS(filePath, false);
		}
		if(rdbtnEuler90Degree.isSelected()) {
			Bend90degEulerGDSModule euler90Deg = new Bend90degEulerGDSModule(a, b, R0) ;
			euler90Deg.setWidth(width);
			euler90Deg.setNumPoints(numPoints);
			euler90Deg.createGDS(filePath, false);
		}
		if (rdbtnClothoid90Degree.isSelected()) {
			Bend90degHybridGDSModule clothoid90Deg = new Bend90degHybridGDSModule(a, b, R0) ;
			clothoid90Deg.setWidth(width);
			clothoid90Deg.setNumPoints(numPoints);
			clothoid90Deg.createGDS(filePath, false);
		}
		if (rdbtnBezier90Degree.isSelected()) {
			Bend90degBezierGDSModule bezier90Deg = new Bend90degBezierGDSModule(a, b, R0);
			bezier90Deg.setWidth(width);
			bezier90Deg.setNumPoints(numPoints);
			bezier90Deg.createGDS(filePath, false);
		}
		if (rdbtnCircular180Degree.isSelected()) {
			Bend180degCircularGDSModule circ180Deg = new Bend180degCircularGDSModule(a, b, R0);
			circ180Deg.setWidth(width);
			circ180Deg.setNumPoints(numPoints);
			circ180Deg.createGDS(filePath, false);
		}
		if (rdbtnBezier180Degree.isSelected()) {
			Bend180degBezierGDSModule bezier180Deg = new Bend180degBezierGDSModule(a, b, R0);
			bezier180Deg.setWidth(width);
			bezier180Deg.setNumPoints(numPoints);
			bezier180Deg.createGDS(filePath, false);
		}
		if (rdbtnEuler180Degree.isSelected()) {
			Bend180degEulerGDSModule euler180Deg = new Bend180degEulerGDSModule(a, b, R0);
			euler180Deg.setWidth(width);
			euler180Deg.setNumPoints(numPoints);
			euler180Deg.createGDS(filePath, false);
		}
		if(rdbtnOptimal180Degree.isSelected()) {
			Bend180degOptimalGDSModule opt180Deg = new Bend180degOptimalGDSModule(a, b, R0) ;
			opt180Deg.setWidth(width);
			opt180Deg.setNumPoints(numPoints);
			opt180Deg.createGDS(filePath, false);
		}
		if(rdbtnClothoid180Degree.isSelected()){
			Bend180degHybridGDSModule hybrid180Deg = new Bend180degHybridGDSModule(a, b, R0) ;
			hybrid180Deg.setWidth(width);
			hybrid180Deg.setNumPoints(numPoints);
			hybrid180Deg.createGDS(filePath, false);
		}
	}
}
