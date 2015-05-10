package gui;

import parser.TraceParser;
import util.Trace;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.Date;

/**
 * Created by fedor on 07/05/15.
 */
public class TimeSeriesPanel extends JPanel implements ActionListener {

    private JTable table;
    private JButton saveButton, saveAsButton;
    private boolean isLoaded = false;
    private boolean isEdited = false;
    private String filename;

    public TimeSeriesPanel() {
        super();
        setLayout(new BorderLayout());
        table = new JTable(new DefaultTableModel());
        add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel();
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        saveButton.setVisible(false);
        saveButton.setEnabled(false);
        saveAsButton = new JButton("Save As");
        saveAsButton.addActionListener(this);
        saveAsButton.setVisible(false);
        bottomPanel.add(saveButton);
        bottomPanel.add(saveAsButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
        saveButton.setVisible(true);
        saveAsButton.setVisible(true);
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
            table.getModel().addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent tableModelEvent) {
                    if(tableModelEvent.getType() == tableModelEvent.UPDATE) {
                        saveButton.setEnabled(true);
                    }
                }
            });
        }
    }

    public void saveSeriesToFile(String filename) {
        try {
            PrintWriter file = new PrintWriter(filename, "UTF-8");
            String line = "";
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for(int i = 0; i < model.getColumnCount() - 1; i++) {
                line += model.getColumnName(i) + ",";
            }
            line += model.getColumnName(model.getColumnCount() - 1);
            file.println(line);
            for(int i = 0; i < model.getRowCount(); i++) {
                line = "";
                for(int j = 0; j < model.getColumnCount() - 1; j++) {
                    line += model.getValueAt(i,j) + ",";
                }
                line += model.getValueAt(i,model.getColumnCount() - 1);
                file.println(line);
            }
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveAsButton) {
            JFileChooser fc = new JFileChooser();
            FileFilter csvFilter = new FileNameExtensionFilter("CSV File","csv");
            fc.resetChoosableFileFilters();
            fc.setFileFilter(csvFilter);
            int returnVal = fc.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                saveSeriesToFile(fc.getSelectedFile().getAbsolutePath() + ".csv");
            }
        } else if (e.getSource() == saveButton){
            int res = JOptionPane.showConfirmDialog(this,
                    "Do you want to overwrite " + filename,
                    "Time series",
                    JOptionPane.YES_NO_OPTION);
            if(res == JOptionPane.YES_OPTION)
            {
                saveSeriesToFile(filename);
                saveButton.setEnabled(false);
            }
        }
    }
}
