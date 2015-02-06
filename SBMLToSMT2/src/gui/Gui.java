package gui;

import java.awt.AWTError;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import model.ODEModel;

import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.AssignmentRule;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.RateRule;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Rule;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.Species;

import parser.SMT2SettingsParser;
import parser.TraceParser;
import util.ModelSettings;
import util.SMT2Settings;
import util.Utility;
import util.Utility.Tuple;

public class Gui implements ActionListener {
	private JFrame gui;

	private JTextField sbml, series, noise, precision, delta;

	private JButton browseSBML, browseSeries, generateSMT2, run;

	private JScrollPane paramsScroll, speciesScroll;

	private JLabel sbmlLabel, seriesLabel, noiseLabel, precisionLabel, deltaLabel;

	private JFileChooser fc;

	private JPanel paramsPanel, speciesPanel;

	public Gui() {

		fc = new JFileChooser();

		// Create the frame
		gui = new JFrame("BioPSy");
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		sbml = new JTextField(30);
		sbmlLabel = new JLabel("SBML File:");
		browseSBML = new JButton("Browse");
		browseSBML.addActionListener(this);
		series = new JTextField(30);
		seriesLabel = new JLabel("Time Series File:");
		browseSeries = new JButton("Browse");
		browseSeries.addActionListener(this);
		paramsScroll = new JScrollPane();
		paramsScroll.setMinimumSize(new Dimension(600, 150));
		paramsScroll.setPreferredSize(new Dimension(600, 150));
		speciesScroll = new JScrollPane();
		speciesScroll.setMinimumSize(new Dimension(600, 150));
		speciesScroll.setPreferredSize(new Dimension(600, 150));
		noise = new JTextField("0.1", 10);
		noiseLabel = new JLabel("Noise:");
		precision = new JTextField("0.000001", 10);
		precisionLabel = new JLabel("Precision:");
		delta = new JTextField("0.000001", 10);
		deltaLabel = new JLabel("Delta:");
		generateSMT2 = new JButton("Generate SMT2");
		generateSMT2.addActionListener(this);
		run = new JButton("Run");
		run.addActionListener(this);

		// Create panels for the inputs and buttons
		JPanel topPanel = new JPanel(new GridLayout(2, 2));
		JPanel sbmlPanel = new JPanel();
		JPanel seriesPanel = new JPanel();
		paramsPanel = new JPanel();
		speciesPanel = new JPanel();
		JPanel bottomPanel = new JPanel(new GridLayout(3, 2));
		JPanel buttonsPanel = new JPanel();
		JPanel middlePanel = new JPanel(new BorderLayout());
		JPanel mainPanel = new JPanel(new BorderLayout());

		paramsScroll.setViewportView(paramsPanel);
		speciesScroll.setViewportView(speciesPanel);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Parameters", paramsScroll);
		tabbedPane.addTab("Variables", speciesScroll);

		sbmlPanel.add(sbml);
		sbmlPanel.add(browseSBML);
		seriesPanel.add(series);
		seriesPanel.add(browseSeries);
		topPanel.add(sbmlLabel);
		topPanel.add(sbmlPanel);
		topPanel.add(seriesLabel);
		topPanel.add(seriesPanel);
		bottomPanel.add(noiseLabel);
		bottomPanel.add(noise);
		bottomPanel.add(precisionLabel);
		bottomPanel.add(precision);
		bottomPanel.add(deltaLabel);
		bottomPanel.add(delta);
		//buttonsPanel.add(generateSMT2);
		buttonsPanel.add(run);
		middlePanel.add(tabbedPane, BorderLayout.CENTER);
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
					List<String> assignments = new ArrayList<String>();
					for (Rule rule : document.getModel().getListOfRules()) {
						if (rule.isAssignment()) {
							AssignmentRule aRule = ((AssignmentRule) rule);
							assignments.add(aRule.getVariable());
						}
					}
					List<String> parameters = new ArrayList<String>();
					List<String> vars = new ArrayList<String>();
					for (Parameter param : document.getModel().getListOfParameters()) {
						if (!assignments.contains(param.getId())) {
							parameters.add(param.getId());
						}
					}
					for (Species s : document.getModel().getListOfSpecies()) {
						if (!assignments.contains(s.getId())) {
							vars.add(s.getId());
						}
					}
					for (Rule r : document.getModel().getListOfRules()) {
						if (r.isRate()) {
							String var = ((RateRule) r).getVariable();
							if (parameters.contains(var)) {
								parameters.remove(var);
							}
							if (!vars.contains(var) && !assignments.contains(var)) {
								vars.add(var);
							}
						}
					}
					for (Reaction reaction : document.getModel().getListOfReactions()) {
						for (LocalParameter parameter : reaction.getKineticLaw()
								.getListOfLocalParameters()) {
							String newName = reaction.getId() + "_" + parameter.getId();
							parameters.add(newName);
						}
					}
					Collections.sort(parameters);
					Collections.sort(vars);
					paramsPanel.removeAll();
					paramsPanel.setLayout(new GridLayout(parameters.size() + 1, 4));
					paramsPanel.add(new JLabel("Synthesize"));
					paramsPanel.add(new JLabel("Name"));
					paramsPanel.add(new JLabel("Lower Bound"));
					paramsPanel.add(new JLabel("Upper Bound"));
					for (String p : parameters) {
						paramsPanel.add(new JCheckBox());
						paramsPanel.add(new JLabel(p));
						paramsPanel.add(new JTextField("0"));
						paramsPanel.add(new JTextField("1000000000"));
					}
					paramsPanel.revalidate();
					speciesPanel.removeAll();
					speciesPanel.setLayout(new GridLayout(vars.size() + 1, 3));
					speciesPanel.add(new JLabel("Name"));
					speciesPanel.add(new JLabel("Lower Bound"));
					speciesPanel.add(new JLabel("Upper Bound"));
					for (String s : vars) {
						speciesPanel.add(new JLabel(s));
						speciesPanel.add(new JTextField("0"));
						speciesPanel.add(new JTextField("1000000000"));
					}
					speciesPanel.revalidate();
				}
				catch (XMLStreamException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch (IOException e1) {
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
				List<String> params = new ArrayList<String>();
				for (int i = 4; i < paramsPanel.getComponentCount(); i += 4) {
					if (((JCheckBox) paramsPanel.getComponent(i)).isSelected()) {
						params.add(((JLabel) paramsPanel.getComponent(i + 1)).getText());
					}
				}
				System.out.println(Utility.writeSMT2ToString(new ModelSettings(sbml.getText()
						.trim(), series.getText().trim(), params, Double.parseDouble(noise
						.getText().trim()), Double.parseDouble(precision.getText().trim()))));
			}
			catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (XMLStreamException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == run) {
			try {
				Map<String, Tuple<Double, Double>> variables = new HashMap<String, Tuple<Double, Double>>();
				Map<String, ASTNode> odes = new HashMap<String, ASTNode>();
				List<String> params = new ArrayList<String>();
				for (int i = 4; i < paramsPanel.getComponentCount(); i += 4) {
					if (((JCheckBox) paramsPanel.getComponent(i)).isSelected()) {
						variables.put(
								((JLabel) paramsPanel.getComponent(i + 1)).getText(),
								new Tuple<Double, Double>(Double
										.parseDouble(((JTextField) paramsPanel.getComponent(i + 2))
												.getText().trim()), Double
										.parseDouble(((JTextField) paramsPanel.getComponent(i + 3))
												.getText().trim())));
						params.add(((JLabel) paramsPanel.getComponent(i + 1)).getText());
					}
				}
				ODEModel model = new ODEModel(SBMLReader.read(new File(sbml.getText().trim())),
						params);
				for (int i = 3; i < speciesPanel.getComponentCount(); i += 3) {
					variables.put(
							((JLabel) speciesPanel.getComponent(i)).getText(),
							new Tuple<Double, Double>(Double.parseDouble(((JTextField) speciesPanel
									.getComponent(i + 1)).getText().trim()), Double
									.parseDouble(((JTextField) speciesPanel.getComponent(i + 2))
											.getText().trim())));
					odes.put(((JLabel) speciesPanel.getComponent(i)).getText(),
							model.getODE(((JLabel) speciesPanel.getComponent(i)).getText()));
				}
				SMT2SettingsParser.writeSettingsToFile(
						"model.xml",
						new SMT2Settings(variables, "t", odes, TraceParser
								.parseCopasiOutput(new File(series.getText().trim())), Double
								.parseDouble(precision.getText().trim()), Double.parseDouble(delta
								.getText().trim()), Double.parseDouble(noise.getText().trim())));
				Runtime exec = Runtime.getRuntime();
				Process parsyn = exec.exec("ParSyn model.xml");
				String error = "";
				PrintWriter out = new PrintWriter("BioPSy_output.txt");
				try {
					InputStream par = parsyn.getInputStream();
					InputStreamReader isr = new InputStreamReader(par);
					BufferedReader br = new BufferedReader(isr);
					// int count = 0;
					String line;
					while ((line = br.readLine()) != null) {
						System.out.println(line);
						out.println(line);
						out.flush();
					}
					InputStream par2 = parsyn.getErrorStream();
					int read = par2.read();
					while (read != -1) {
						error += (char) read;
						read = par2.read();
					}
					br.close();
					isr.close();
					par.close();
					par2.close();
				}
				catch (Exception e1) {
					// e.printStackTrace();
				}
				if (!error.equals("")) {
					JOptionPane.showMessageDialog(gui, "Error in execution.", "ERROR",
							JOptionPane.ERROR_MESSAGE);
					System.err.println(error);
				}
				out.close();
			}
			catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (XMLStreamException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (TransformerFactoryConfigurationError e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (TransformerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
