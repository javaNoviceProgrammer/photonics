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
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class BendWgGUI extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = -8071101530008067621L;
	private JPanel contentPane;
	private JTextField path;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private final ButtonGroup buttonGroup = new ButtonGroup();

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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 543, 376);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
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
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);

		JLabel lblSaveGds = new JLabel("save GDS : ");
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
				gbl_panel_2.columnWidths = new int[]{0, 0, 0};
				gbl_panel_2.rowHeights = new int[]{0, 0, 0};
				gbl_panel_2.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
				gbl_panel_2.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
				panel_2.setLayout(gbl_panel_2);

						JLabel lblA = new JLabel("a : ");
						GridBagConstraints gbc_lblA = new GridBagConstraints();
						gbc_lblA.insets = new Insets(0, 0, 5, 5);
						gbc_lblA.anchor = GridBagConstraints.EAST;
						gbc_lblA.gridx = 0;
						gbc_lblA.gridy = 0;
						panel_2.add(lblA, gbc_lblA);

								textField = new JTextField();
								GridBagConstraints gbc_textField = new GridBagConstraints();
								gbc_textField.insets = new Insets(0, 0, 5, 0);
								gbc_textField.fill = GridBagConstraints.HORIZONTAL;
								gbc_textField.gridx = 1;
								gbc_textField.gridy = 0;
								panel_2.add(textField, gbc_textField);
								textField.setColumns(10);

										JLabel lblB = new JLabel("b : ");
										GridBagConstraints gbc_lblB = new GridBagConstraints();
										gbc_lblB.anchor = GridBagConstraints.EAST;
										gbc_lblB.insets = new Insets(0, 0, 0, 5);
										gbc_lblB.gridx = 0;
										gbc_lblB.gridy = 1;
										panel_2.add(lblB, gbc_lblB);

												textField_1 = new JTextField();
												GridBagConstraints gbc_textField_1 = new GridBagConstraints();
												gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
												gbc_textField_1.gridx = 1;
												gbc_textField_1.gridy = 1;
												panel_2.add(textField_1, gbc_textField_1);
												textField_1.setColumns(10);

				JPanel panel_3 = new JPanel();
				GridBagConstraints gbc_panel_3 = new GridBagConstraints();
				gbc_panel_3.fill = GridBagConstraints.HORIZONTAL;
				gbc_panel_3.insets = new Insets(0, 0, 5, 0);
				gbc_panel_3.gridx = 0;
				gbc_panel_3.gridy = 2;
				contentPane.add(panel_3, gbc_panel_3);
				panel_3.setBorder(new TitledBorder(null, "type of bend", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				GridBagLayout gbl_panel_3 = new GridBagLayout();
				gbl_panel_3.columnWidths = new int[]{0, 0, 0, 0};
				gbl_panel_3.rowHeights = new int[]{0, 0, 0, 0};
				gbl_panel_3.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
				gbl_panel_3.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
				panel_3.setLayout(gbl_panel_3);

						JRadioButton rdbtnCircularDegree = new JRadioButton("Circular 90 degree");
						buttonGroup.add(rdbtnCircularDegree);
						GridBagConstraints gbc_rdbtnCircularDegree = new GridBagConstraints();
						gbc_rdbtnCircularDegree.anchor = GridBagConstraints.WEST;
						gbc_rdbtnCircularDegree.insets = new Insets(0, 0, 5, 5);
						gbc_rdbtnCircularDegree.gridx = 0;
						gbc_rdbtnCircularDegree.gridy = 0;
						panel_3.add(rdbtnCircularDegree, gbc_rdbtnCircularDegree);

						JRadioButton rdbtnNewRadioButton = new JRadioButton("Bezier 180 degree");
						buttonGroup.add(rdbtnNewRadioButton);
						GridBagConstraints gbc_rdbtnNewRadioButton = new GridBagConstraints();
						gbc_rdbtnNewRadioButton.anchor = GridBagConstraints.WEST;
						gbc_rdbtnNewRadioButton.insets = new Insets(0, 0, 5, 5);
						gbc_rdbtnNewRadioButton.gridx = 1;
						gbc_rdbtnNewRadioButton.gridy = 0;
						panel_3.add(rdbtnNewRadioButton, gbc_rdbtnNewRadioButton);

								JRadioButton rdbtnEulerDegree = new JRadioButton("Euler 90 degree");
								buttonGroup.add(rdbtnEulerDegree);
								GridBagConstraints gbc_rdbtnEulerDegree = new GridBagConstraints();
								gbc_rdbtnEulerDegree.anchor = GridBagConstraints.WEST;
								gbc_rdbtnEulerDegree.insets = new Insets(0, 0, 5, 0);
								gbc_rdbtnEulerDegree.gridx = 2;
								gbc_rdbtnEulerDegree.gridy = 0;
								panel_3.add(rdbtnEulerDegree, gbc_rdbtnEulerDegree);

								JRadioButton rdbtnCircularDegree_1 = new JRadioButton("Circular 180 degree");
								buttonGroup.add(rdbtnCircularDegree_1);
								GridBagConstraints gbc_rdbtnCircularDegree_1 = new GridBagConstraints();
								gbc_rdbtnCircularDegree_1.insets = new Insets(0, 0, 5, 5);
								gbc_rdbtnCircularDegree_1.gridx = 0;
								gbc_rdbtnCircularDegree_1.gridy = 1;
								panel_3.add(rdbtnCircularDegree_1, gbc_rdbtnCircularDegree_1);

								JRadioButton rdbtnOptimalDegree = new JRadioButton("Optimal 90 degree");
								buttonGroup.add(rdbtnOptimalDegree);
								GridBagConstraints gbc_rdbtnOptimalDegree = new GridBagConstraints();
								gbc_rdbtnOptimalDegree.anchor = GridBagConstraints.WEST;
								gbc_rdbtnOptimalDegree.insets = new Insets(0, 0, 5, 5);
								gbc_rdbtnOptimalDegree.gridx = 1;
								gbc_rdbtnOptimalDegree.gridy = 1;
								panel_3.add(rdbtnOptimalDegree, gbc_rdbtnOptimalDegree);

										JRadioButton rdbtnEulerDegree_1 = new JRadioButton("Euler 180 degree");
										buttonGroup.add(rdbtnEulerDegree_1);
										GridBagConstraints gbc_rdbtnEulerDegree_1 = new GridBagConstraints();
										gbc_rdbtnEulerDegree_1.anchor = GridBagConstraints.WEST;
										gbc_rdbtnEulerDegree_1.insets = new Insets(0, 0, 5, 0);
										gbc_rdbtnEulerDegree_1.gridx = 2;
										gbc_rdbtnEulerDegree_1.gridy = 1;
										panel_3.add(rdbtnEulerDegree_1, gbc_rdbtnEulerDegree_1);

										JRadioButton rdbtnBezierDegree = new JRadioButton("Bezier 90 degree");
										buttonGroup.add(rdbtnBezierDegree);
										GridBagConstraints gbc_rdbtnBezierDegree = new GridBagConstraints();
										gbc_rdbtnBezierDegree.insets = new Insets(0, 0, 0, 5);
										gbc_rdbtnBezierDegree.anchor = GridBagConstraints.WEST;
										gbc_rdbtnBezierDegree.gridx = 0;
										gbc_rdbtnBezierDegree.gridy = 2;
										panel_3.add(rdbtnBezierDegree, gbc_rdbtnBezierDegree);

										JRadioButton rdbtnOptimalDegree_1 = new JRadioButton("Optimal 180 degree");
										buttonGroup.add(rdbtnOptimalDegree_1);
										GridBagConstraints gbc_rdbtnOptimalDegree_1 = new GridBagConstraints();
										gbc_rdbtnOptimalDegree_1.insets = new Insets(0, 0, 0, 5);
										gbc_rdbtnOptimalDegree_1.anchor = GridBagConstraints.WEST;
										gbc_rdbtnOptimalDegree_1.gridx = 1;
										gbc_rdbtnOptimalDegree_1.gridy = 2;
										panel_3.add(rdbtnOptimalDegree_1, gbc_rdbtnOptimalDegree_1);

										JPanel panel_1 = new JPanel();
										panel_1.setBorder(new TitledBorder(null, "parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
										GridBagConstraints gbc_panel_1 = new GridBagConstraints();
										gbc_panel_1.fill = GridBagConstraints.BOTH;
										gbc_panel_1.gridx = 0;
										gbc_panel_1.gridy = 3;
										contentPane.add(panel_1, gbc_panel_1);
										GridBagLayout gbl_panel_1 = new GridBagLayout();
										gbl_panel_1.columnWidths = new int[]{0, 0, 0};
										gbl_panel_1.rowHeights = new int[]{0, 0, 0};
										gbl_panel_1.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
										gbl_panel_1.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
										panel_1.setLayout(gbl_panel_1);

										JLabel lblR = new JLabel("R : ");
										GridBagConstraints gbc_lblR = new GridBagConstraints();
										gbc_lblR.insets = new Insets(0, 0, 5, 5);
										gbc_lblR.anchor = GridBagConstraints.EAST;
										gbc_lblR.gridx = 0;
										gbc_lblR.gridy = 0;
										panel_1.add(lblR, gbc_lblR);

										textField_2 = new JTextField();
										GridBagConstraints gbc_textField_2 = new GridBagConstraints();
										gbc_textField_2.insets = new Insets(0, 0, 5, 0);
										gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
										gbc_textField_2.gridx = 1;
										gbc_textField_2.gridy = 0;
										panel_1.add(textField_2, gbc_textField_2);
										textField_2.setColumns(10);

										JButton gdsButton = new JButton("create GDS");
										gdsButton.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent e) {
											}
										});
										GridBagConstraints gbc_gdsButton = new GridBagConstraints();
										gbc_gdsButton.anchor = GridBagConstraints.EAST;
										gbc_gdsButton.gridx = 1;
										gbc_gdsButton.gridy = 1;
										panel_1.add(gdsButton, gbc_gdsButton);
	}

}
