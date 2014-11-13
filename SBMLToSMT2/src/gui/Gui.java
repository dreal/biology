package gui;

import java.awt.AWTError;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class Gui implements ActionListener {
	private JFrame gui;

	private JTextField sbml, series, noise, precision, boxSize;

	private JList<String> params;

	private JButton browseSBML, browseSeries, generateSMT2, run;

	private JScrollPane paramsScroll;

	private JLabel sbmlLabel, seriesLabel, paramsLabel, noiseLabel, precisionLabel, boxSizeLabel;

	private JFileChooser fc;

	public Gui() {

		fc = new JFileChooser();

		// Create the frame
		gui = new JFrame("SBML To SMT2");
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		sbml = new JTextField(60);
		sbmlLabel = new JLabel("SBML File:");
		browseSBML = new JButton("Browse");
		browseSBML.addActionListener(this);
		series = new JTextField(60);
		seriesLabel = new JLabel("Time Series File:");
		browseSeries = new JButton("Browse");
		browseSeries.addActionListener(this);
		params = new JList<String>();
		paramsScroll = new JScrollPane();
		paramsScroll.setMinimumSize(new Dimension(200, 150));
		paramsScroll.setPreferredSize(new Dimension(200, 150));
		paramsScroll.setViewportView(params);
		paramsLabel = new JLabel("Parameters:");
		noise = new JTextField(60);
		noiseLabel = new JLabel("Noise:");
		precision = new JTextField(60);
		precisionLabel = new JLabel("Precision:");
		boxSize = new JTextField(60);
		boxSizeLabel = new JLabel("Box-size:");
		generateSMT2 = new JButton("Generate SMT2");
		generateSMT2.addActionListener(this);
		run = new JButton("Run");
		run.addActionListener(this);

		// Create panels for the inputs and buttons
		JPanel sbmlPanel = new JPanel();
		JPanel seriesPanel = new JPanel();
		JPanel paramsPanel = new JPanel();
		JPanel noisePanel = new JPanel();
		JPanel precisionPanel = new JPanel();
		JPanel boxSizePanel = new JPanel();
		JPanel buttonsPanel = new JPanel();
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel middlePanel = new JPanel(new BorderLayout());
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		sbmlPanel.add(sbmlLabel);
		sbmlPanel.add(sbml);
		sbmlPanel.add(browseSBML);
		seriesPanel.add(seriesLabel);
		seriesPanel.add(series);
		seriesPanel.add(browseSeries);
		paramsPanel.add(paramsLabel);
		paramsPanel.add(paramsScroll);
		noisePanel.add(noiseLabel);
		noisePanel.add(noise);
		precisionPanel.add(precisionLabel);
		precisionPanel.add(precision);
		boxSizePanel.add(boxSizeLabel);
		boxSizePanel.add(boxSize);
		buttonsPanel.add(generateSMT2);
		buttonsPanel.add(run);
		topPanel.add(sbmlPanel, BorderLayout.NORTH);
		topPanel.add(seriesPanel, BorderLayout.CENTER);
		topPanel.add(paramsPanel, BorderLayout.SOUTH);
		middlePanel.add(noisePanel, BorderLayout.NORTH);
		middlePanel.add(precisionPanel, BorderLayout.CENTER);
		middlePanel.add(boxSizePanel, BorderLayout.SOUTH);
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(middlePanel, BorderLayout.CENTER);
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);		
		
		// Add the main panel to the frame
		gui.setContentPane(mainPanel);
		gui.pack();

		// Get the screen dimensions
		Dimension screenSize;
		try {
			Toolkit tk = Toolkit.getDefaultToolkit();
			screenSize = tk.getScreenSize();
		}
		catch (AWTError awe) {
			screenSize = new Dimension(640, 480);
		}

		// Get/set the frame dimensions
		Dimension frameSize = gui.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
			gui.setSize(frameSize);
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
			gui.setSize(frameSize);
		}

		// Position the frame
		int x = screenSize.width / 2 - frameSize.width / 2;
		int y = screenSize.height / 2 - frameSize.height / 2;
		gui.setLocation(x, y);

		// Display the frame
		gui.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
