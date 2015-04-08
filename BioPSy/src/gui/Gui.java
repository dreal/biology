package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import com.sun.org.apache.xerces.internal.parsers.XMLParser;
import model.AdvancedOptionsModel;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import parser.SMT2SettingsParser;
import parser.TraceParser;
import util.BackgroundWorker;
import util.ModelSettings;
import util.SMT2Settings;
import util.Utility;
import util.Utility.Tuple;

public class Gui implements ActionListener {
	private JFrame gui;

    private JTextArea outTextArea, timeSeriesTextArea, sbmlTextArea;

	private JTextField sbml, series, noise;

	private JButton browseSBML, browseSeries, generateSMT2, run, advancedOptionsButton, stopButton, okButton;

	private JScrollPane paramsScroll, speciesScroll, outputScroll, timeSeriesScroll, sbmlScroll;

	private JLabel sbmlLabel, seriesLabel, noiseLabel;

	private JFileChooser fc;

    private JTabbedPane tabbedPane;

	private JPanel paramsPanel, speciesPanel;

    private JProgressBar progressBar;

    private BackgroundWorker bgWorker;

    private Thread outputListener;

    boolean isStopped = false;

	public Gui(){

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

		noise = new JTextField("0.1", 10);
		noiseLabel = new JLabel("Noise:");

		//generateSMT2 = new JButton("Generate SMT2");
		//generateSMT2.addActionListener(this);

        // Run button
		run = new JButton("Run");
		run.addActionListener(this);
        gui.getRootPane().setDefaultButton(run);
        run.requestFocus();

        // Advanced Options button
        advancedOptionsButton = new JButton("Advanced Options");
        advancedOptionsButton.addActionListener(this);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        okButton.setVisible(false);

        // Stop button
        stopButton = new JButton("Stop");
        stopButton.addActionListener(this);
        stopButton.setEnabled(false);
        stopButton.setVisible(false);

		// Create panels for the inputs and buttons
		JPanel topPanel = new JPanel(new GridLayout(3, 3));
		paramsPanel = new JPanel();
		speciesPanel = new JPanel();
		JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
		JPanel buttonsPanel = new JPanel();
		JPanel middlePanel = new JPanel(new BorderLayout());
		JPanel mainPanel = new JPanel(new BorderLayout());

        // Create text area for program output
        outTextArea = new JTextArea();
        outTextArea.setText("Program output will be displayed here");
        outTextArea.setEditable(false);
        // Updating the text area
        DefaultCaret caret = (DefaultCaret) outTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // Create text area for SBML file
        sbmlTextArea = new JTextArea();
        sbmlTextArea.setEditable(false);

        // Create text area for time series
        timeSeriesTextArea = new JTextArea();
        timeSeriesTextArea.setEditable(false);

        paramsScroll = new JScrollPane();
        paramsScroll.setMinimumSize(new Dimension(600, 400));
        paramsScroll.setPreferredSize(new Dimension(600, 400));

        speciesScroll = new JScrollPane();
        speciesScroll.setMinimumSize(new Dimension(600, 400));
        speciesScroll.setPreferredSize(new Dimension(600, 400));

        outputScroll = new JScrollPane();
        outputScroll.setMinimumSize(new Dimension(600, 400));
        outputScroll.setPreferredSize(new Dimension(600, 400));

        sbmlScroll = new JScrollPane();
        sbmlScroll.setMinimumSize(new Dimension(600, 400));
        sbmlScroll.setPreferredSize(new Dimension(600, 400));

        timeSeriesScroll = new JScrollPane();
        timeSeriesScroll.setMinimumSize(new Dimension(600, 400));
        timeSeriesScroll.setPreferredSize(new Dimension(600, 400));

        outputScroll.setViewportView(outTextArea);
		paramsScroll.setViewportView(paramsPanel);
		speciesScroll.setViewportView(speciesPanel);
        sbmlScroll.setViewportView(sbmlTextArea);
        timeSeriesScroll.setViewportView(timeSeriesTextArea);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("SBML", sbmlScroll);
        tabbedPane.addTab("Time series", timeSeriesScroll);
        tabbedPane.addTab("Parameters", paramsScroll);
		tabbedPane.addTab("Variables", speciesScroll);
        tabbedPane.addTab("Output", outputScroll);

        topPanel.add(sbmlLabel);
        topPanel.add(sbml);
        topPanel.add(browseSBML);
        topPanel.add(seriesLabel);
        topPanel.add(series);
        topPanel.add(browseSeries);
        topPanel.add(noiseLabel);
        topPanel.add(noise);
        topPanel.add(new JLabel());

        progressBar = new JProgressBar();
        progressBar.setMaximum(100);
        progressBar.setMinimum(0);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);

		buttonsPanel.add(run);
        buttonsPanel.add(advancedOptionsButton);
        buttonsPanel.add(progressBar);
        buttonsPanel.add(stopButton);
        buttonsPanel.add(okButton);

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
		} catch (AWTError awe) {
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
        //System.out.println("Action event: " + e.getActionCommand() + " " + e.getSource());
		if (e.getSource() == browseSBML) {
            FileFilter xmlFilter = new FileNameExtensionFilter("XML File","xml");
            fc.resetChoosableFileFilters();
            fc.setFileFilter(xmlFilter);
			int returnVal = fc.showOpenDialog(gui);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
				sbml.setText(fc.getSelectedFile().getAbsolutePath());
                try {
					SBMLDocument document = SBMLReader.read(new File(sbml
							.getText()));
					List<String> assignments = new ArrayList<String>();
					for (Rule rule : document.getModel().getListOfRules()) {
						if (rule.isAssignment()) {
							AssignmentRule aRule = ((AssignmentRule) rule);
							assignments.add(aRule.getVariable());
						}
					}
					Map<String,String> parameters = new HashMap<String,String>();
					List<String> vars = new ArrayList<String>();
					for (Parameter param : document.getModel()
							.getListOfParameters()) {
						if (!assignments.contains(param.getId())) {
							parameters.put(param.getId(), String.valueOf(param.getValue()));
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
							if (parameters.containsKey(var)) {
								parameters.remove(var);
							}
							if (!vars.contains(var)
									&& !assignments.contains(var)) {
								vars.add(var);
							}
						}
					}
					for (Reaction reaction : document.getModel()
							.getListOfReactions()) {
						for (LocalParameter parameter : reaction
								.getKineticLaw().getListOfLocalParameters()) {
							String newName = reaction.getId() + "_"
									+ parameter.getId();
							parameters.put(newName, String.valueOf(parameter.getValue()));
						}
					}
					List<String> params = new ArrayList<String>(parameters.keySet());
					Collections.sort(params);
					Collections.sort(vars);
					paramsPanel.removeAll();
					paramsPanel.setLayout(new GridLayout(params.size() + 1,
							4));
					paramsPanel.add(new JLabel("Synthesize"));
					paramsPanel.add(new JLabel("Name"));
					paramsPanel.add(new JLabel("Lower Bound"));
					paramsPanel.add(new JLabel("Upper Bound"));
                    for (String p : params) {
                        paramsPanel.add(new JCheckBox());
						paramsPanel.add(new JLabel(p));
						paramsPanel.add(new JTextField(parameters.get(p)));
						paramsPanel.add(new JTextField(parameters.get(p)));
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
						speciesPanel.add(new JTextField("1"));
					}
					speciesPanel.revalidate();

                    // Output sbml to text area
                    tabbedPane.setSelectedIndex(0);
                    sbmlTextArea.read(new FileReader(sbml.getText()), null);
				} catch (XMLStreamException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
		} else if (e.getSource() == browseSeries) {
            FileFilter csvFilter = new FileNameExtensionFilter("CSV File","csv");
            fc.resetChoosableFileFilters();
            fc.setFileFilter(csvFilter);
            int returnVal = fc.showOpenDialog(gui);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				series.setText(fc.getSelectedFile().getAbsolutePath());
                // Output time series to text area
                try {
                    tabbedPane.setSelectedIndex(1);
                    timeSeriesTextArea.read(new FileReader(series.getText()), null);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
		} else if (e.getSource() == generateSMT2) {
			try {
				List<String> params = new ArrayList<String>();
				for (int i = 4; i < paramsPanel.getComponentCount(); i += 4) {
					if (((JCheckBox) paramsPanel.getComponent(i)).isSelected()) {
						params.add(((JLabel) paramsPanel.getComponent(i + 1))
								.getText());
					}
				}
				System.out.println(Utility.writeSMT2ToString(new ModelSettings(
						sbml.getText().trim(), series.getText().trim(), params,
						Double.parseDouble(noise.getText().trim()))));
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (XMLStreamException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getSource() == advancedOptionsButton) {
            new AdvancedOptionsDialog(gui, "Advanced Options");
        } else if (e.getSource() == okButton) {
            isStopped = false;
            run.setVisible(true);
            advancedOptionsButton.setVisible(true);
            progressBar.setVisible(false);
            okButton.setVisible(false);
            browseSBML.setEnabled(true);
            browseSeries.setEnabled(true);
        } else if (e.getSource() == stopButton) {
            // Kill ParSyn process
            try {
                Runtime exec = Runtime.getRuntime();
                String killCall = "pkill -9 -P " + AdvancedOptionsModel.getParsynPID();
                Process kill = exec.exec(killCall);
                isStopped = true;
                stopButton.setEnabled(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } else if (e.getSource() == run) {
			try {
				Map<String, Tuple<Double, Double>> variables = new HashMap<String, Tuple<Double, Double>>();
				Map<String, ASTNode> odes = new HashMap<String, ASTNode>();
				List<String> params = new ArrayList<String>();
				for (int i = 4; i < paramsPanel.getComponentCount(); i += 4) {
					if (((JCheckBox) paramsPanel.getComponent(i)).isSelected()) {
						variables.put(
								((JLabel) paramsPanel.getComponent(i + 1))
										.getText(),
								new Tuple<Double, Double>(Double
										.parseDouble(((JTextField) paramsPanel
												.getComponent(i + 2)).getText()
												.trim()), Double
										.parseDouble(((JTextField) paramsPanel
												.getComponent(i + 3)).getText()
												.trim())));
						params.add(((JLabel) paramsPanel.getComponent(i + 1))
								.getText());
					}
				}
				ODEModel model = new ODEModel(SBMLReader.read(new File(sbml
						.getText().trim())), params);
				for (int i = 3; i < speciesPanel.getComponentCount(); i += 3) {
					variables.put(
							((JLabel) speciesPanel.getComponent(i)).getText(),
							new Tuple<Double, Double>(Double
									.parseDouble(((JTextField) speciesPanel
											.getComponent(i + 1)).getText()
											.trim()), Double
									.parseDouble(((JTextField) speciesPanel
											.getComponent(i + 2)).getText()
											.trim())));
					odes.put(
							((JLabel) speciesPanel.getComponent(i)).getText(),
							model.getODE(((JLabel) speciesPanel.getComponent(i))
									.getText()));
				}
				SMT2SettingsParser.writeSettingsToFile(
						"model.xml",
						new SMT2Settings(variables, "t", odes, TraceParser
								.parseCopasiOutput(new File(series.getText()
										.trim())), Double.parseDouble(noise
								.getText().trim())));

                // Creating a background worker
                bgWorker = new BackgroundWorker(outTextArea);

                // Adding a listener checking the status of background worker
                bgWorker.addPropertyChangeListener(
                        new PropertyChangeListener() {
                            public  void propertyChange(PropertyChangeEvent evt) {
                                if (evt.getNewValue() == SwingWorker.StateValue.STARTED) {
                                    tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
                                    run.setEnabled(false);
                                    advancedOptionsButton.setEnabled(false);
                                    stopButton.setEnabled(true);
                                    browseSBML.setEnabled(false);
                                    browseSeries.setEnabled(false);
                                    // Setting visibility
                                    run.setVisible(false);
                                    advancedOptionsButton.setVisible(false);
                                    stopButton.setVisible(true);
                                    progressBar.setVisible(true);
                                } else if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
                                    run.setEnabled(true);
                                    advancedOptionsButton.setEnabled(true);
                                    stopButton.setEnabled(false);
                                    progressBar.setValue(100);
                                    //browseSBML.setEnabled(true);
                                    //browseSeries.setEnabled(true);
                                    // Setting visibility
                                    //run.setVisible(true);
                                    //advancedOptionsButton.setVisible(true);
                                    okButton.setVisible(true);
                                    stopButton.setVisible(false);
                                    //progressBar.setVisible(false);
                                }
                            }
                        });

                // Executing background worker
                bgWorker.execute();

                outputListener = new Thread() {

                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();

                    @Override
                    public void run() {
                        while(!bgWorker.isDone()) {
                            File outputFile = new File("model.xml.output");
                            if(outputFile.exists()) {
                                try {
                                    Thread.sleep(100);
                                    Document dom = db.parse(outputFile);
                                    Element root = dom.getDocumentElement();
                                    double progress = Double.parseDouble(root.getAttribute("progress"));
                                    progressBar.setValue((int) (progress * 100));
                                    //System.out.println("Progress: " + (progress * 100));
                                    FileReader reader = new FileReader(outputFile);
                                    outTextArea.read(reader, null);
                                    reader.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                } catch (SAXException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        if (isStopped) {
                            outTextArea.append("\nComputation was stopped by the user\n");
                        }
                    }

                };
                outputListener.start();

    		} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (XMLStreamException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformerFactoryConfigurationError e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
