package gui;

import java.awt.AWTError;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.RateRule;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Rule;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

import util.ModelSettings;
import util.Utility;

public class Gui implements ActionListener {
	private JFrame gui;

	private JTextField sbml, series, noise, precision;

	private JList<String> params;

	private JButton browseSBML, browseSeries, generateSMT2, run;

	private JScrollPane paramsScroll;

	private JLabel sbmlLabel, seriesLabel, paramsLabel, noiseLabel, precisionLabel;

	private JFileChooser fc;

	public Gui() {

		fc = new JFileChooser();

		// Create the frame
		gui = new JFrame("SBML To SMT2");
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		sbml = new JTextField(30);
		sbmlLabel = new JLabel("SBML File:");
		browseSBML = new JButton("Browse");
		browseSBML.addActionListener(this);
		series = new JTextField(30);
		seriesLabel = new JLabel("Time Series File:");
		browseSeries = new JButton("Browse");
		browseSeries.addActionListener(this);
		params = new JList<String>();
		params.setLayoutOrientation(JList.VERTICAL_WRAP);
		paramsScroll = new JScrollPane();
		paramsScroll.setMinimumSize(new Dimension(600, 150));
		paramsScroll.setPreferredSize(new Dimension(600, 150));
		paramsScroll.setViewportView(params);
		paramsLabel = new JLabel("Parameters:");
		noise = new JTextField("0.1", 10);
		noiseLabel = new JLabel("Noise:");
		precision = new JTextField("0.0001", 10);
		precisionLabel = new JLabel("Precision:");
		generateSMT2 = new JButton("Generate SMT2");
		generateSMT2.addActionListener(this);
		run = new JButton("Run");
		run.addActionListener(this);

		// Create panels for the inputs and buttons
		JPanel topPanel = new JPanel(new GridLayout(2, 2));
		JPanel sbmlPanel = new JPanel();
		JPanel seriesPanel = new JPanel();
		JPanel paramsPanel = new JPanel();
		JPanel bottomPanel = new JPanel(new GridLayout(2, 2));
		JPanel buttonsPanel = new JPanel();
		JPanel middlePanel = new JPanel(new BorderLayout());
		JPanel mainPanel = new JPanel(new BorderLayout());

		sbmlPanel.add(sbml);
		sbmlPanel.add(browseSBML);
		seriesPanel.add(series);
		seriesPanel.add(browseSeries);
		topPanel.add(sbmlLabel);
		topPanel.add(sbmlPanel);
		topPanel.add(seriesLabel);
		topPanel.add(seriesPanel);
		paramsPanel.add(paramsLabel);
		paramsPanel.add(paramsScroll);
		bottomPanel.add(noiseLabel);
		bottomPanel.add(noise);
		bottomPanel.add(precisionLabel);
		bottomPanel.add(precision);
		buttonsPanel.add(generateSMT2);
		buttonsPanel.add(run);
		middlePanel.add(paramsPanel, BorderLayout.CENTER);
		middlePanel.add(bottomPanel, BorderLayout.SOUTH);
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
		if (e.getSource() == browseSBML) {
			int returnVal = fc.showOpenDialog(gui);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				sbml.setText(fc.getSelectedFile().getAbsolutePath());
				try {
					SBMLDocument document = SBMLReader.read(new File(sbml.getText()));
					List<String> vars = new ArrayList<String>();
					for (Parameter param : document.getModel().getListOfParameters()) {
						vars.add(param.getId());
					}
					for (Rule r : document.getModel().getListOfRules()) {
						if (r.isRate()) {
							String var = ((RateRule) r).getVariable();
							if (vars.contains(var)) {
								vars.remove(var);
							}
						}
					}
					for (Reaction reaction : document.getModel().getListOfReactions()) {
						for (LocalParameter parameter : reaction.getKineticLaw()
								.getListOfLocalParameters()) {
							String newName = reaction.getId() + "-" + parameter.getId();
							vars.add(newName);
						}
					}
					params.setListData(vars.toArray(new String[0]));
				}
				catch (XMLStreamException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		else if (e.getSource() == browseSeries) {
			int returnVal = fc.showOpenDialog(gui);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				series.setText(fc.getSelectedFile().getAbsolutePath());
			}
		}
		else if (e.getSource() == generateSMT2) {
			try {
				System.out.println(Utility.writeSMT2ToString(new ModelSettings(sbml.getText()
						.trim(), series.getText().trim(), params.getSelectedValuesList(), Double
						.parseDouble(noise.getText().trim()), Double.parseDouble(precision
						.getText().trim()))));
			}
			catch (NumberFormatException | XMLStreamException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == run) {
			try {
				System.out.println(Utility.writeSMT2ToString(new ModelSettings(sbml.getText()
						.trim(), series.getText().trim(), params.getSelectedValuesList(), Double
						.parseDouble(noise.getText().trim()), Double.parseDouble(precision
						.getText().trim()))));
			}
			catch (NumberFormatException | XMLStreamException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
