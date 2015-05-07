package gui;

import parser.TraceParser;
import util.Trace;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

/**
 * Created by fedor on 07/05/15.
 */
public class TimeSeriesPanel extends JPanel implements ActionListener {

    private JTable table;
    private JButton saveButton, loadButton;
    private JTextField series;
    private boolean isLoaded = false;
    private boolean isEdited = false;

    public TimeSeriesPanel() {
        super();
        setLayout(new BorderLayout());
        table = new JTable(new DefaultTableModel());
        add(new JScrollPane(table), BorderLayout.CENTER);
        /*
        JPanel bottomPanel = new JPanel();
        loadButton = new JButton("Load");
        loadButton.addActionListener(this);
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        saveButton.setEnabled(false);
        series = new JTextField(20);
        bottomPanel.add(loadButton);
        bottomPanel.add(series);
        bottomPanel.add(saveButton);
        add(bottomPanel, BorderLayout.SOUTH);
        */
    }

    public void updateTimeSeriesTable(Trace trace) {
        double[][] data = trace.getData();
        double[] timePoints = trace.getTimePoints();
        String[] vars = trace.getVariables();
        if((data.length > 0) && (data[0].length > 0)) {
            String[][] rowData = new String[data[0].length][data.length + 1];

            for(int i = 0; i < timePoints.length; i++) {
                rowData[i][0] = Double.toString(timePoints[i]);
            }

            for(int i = 0; i < data.length; i++) {
                for(int j = 0; j < data[i].length; j++) {
                    rowData[j][i + 1] = Double.toString(data[i][j]);
                }
            }

            String[] colNames = new String[vars.length + 1];
            colNames[0] = "Time";
            for(int i = 0; i < vars.length; i++) {
                colNames[i+1] = vars[i];
            }

            table.setModel(new DefaultTableModel(rowData, colNames));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loadButton) {
            JFileChooser fc = new JFileChooser();
            FileFilter csvFilter = new FileNameExtensionFilter("CSV File","csv");
            fc.resetChoosableFileFilters();
            fc.setFileFilter(csvFilter);
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                series.setText(fc.getSelectedFile().getAbsolutePath());
                // Output time series to text area
                try {
                    Trace trace = TraceParser.parseCopasiOutput(new File(series.getText().trim()));
                    if (trace == null) {
                        JOptionPane.showMessageDialog(this,
                                "Error occurred while parsing time series data.",
                                "Time series parser",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        saveButton.setEnabled(true);
                        String[][] data = new String[trace.getData().length][];
                        for(int i = 0; i < trace.getData().length; i++) {
                            data[i] = new String[trace.getData()[i].length];
                            for(int j = 0; j < trace.getData()[i].length; j++) {
                                data[i][j] = Double.toString(trace.getData()[i][j]);
                            }
                        }
                        table.setModel(new DefaultTableModel(data, trace.getVariables()));
                        /*
                        for (int i = 4; i < speciesPanel.getComponentCount(); i += 4) {
                            double varMin = trace.getMinForVar(((JLabel) speciesPanel.getComponent(i)).getText());
                            double varMax = trace.getMaxForVar(((JLabel) speciesPanel.getComponent(i)).getText());
                            double interval = varMax - varMin;
                            if (interval != 0) {
                                ((JTextField) speciesPanel.getComponent(i + 1)).setText(Double.toString(varMin - 0.1 * interval));
                                ((JTextField) speciesPanel.getComponent(i + 2)).setText(Double.toString(varMax + 0.1 * interval));
                            } else {
                                ((JTextField) speciesPanel.getComponent(i + 1)).setText(Double.toString(0.9 * varMin));
                                ((JTextField) speciesPanel.getComponent(i + 2)).setText(Double.toString(1.1 * varMax));
                            }
                        }
                        tabbedPane.setSelectedIndex(1);
                        timeSeriesTextArea.read(new FileReader(series.getText()), null);
                        logTextArea.append("Application: loaded time series " + fc.getSelectedFile().getAbsolutePath() + " " + new Date() + "\n");
                        */
                    }
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(this,
                            "Error occurred while parsing time series data.",
                            "Time series parser",
                            JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        } else {
            if (e.getSource() == saveButton) {
                JFileChooser fc = new JFileChooser();
                FileFilter csvFilter = new FileNameExtensionFilter("CSV File","csv");
                fc.resetChoosableFileFilters();
                fc.setFileFilter(csvFilter);
                int returnVal = fc.showSaveDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {

                }
            }
        }
    }
}
