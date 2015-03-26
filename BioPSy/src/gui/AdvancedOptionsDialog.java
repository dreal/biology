package gui;

import model.AdvancedOptionsModel;
import model.ODEModel;
import org.sbml.jsbml.*;
import parser.SMT2SettingsParser;
import parser.TraceParser;
import util.ModelSettings;
import util.SMT2Settings;
import util.Utility;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import java.awt.*;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

/**
 * Created by Fedor Shmarov on 23/03/15.
 */
public class AdvancedOptionsDialog extends JDialog implements ActionListener {

    private JTabbedPane tabbedPane;

    private JPanel  mainPanel, parsynOptionsPanel, drealOptionsPanel, buttonPanel;

    private JLabel drealOptionsLabel, parsynOptionsLabel, drealBinLabel, parsynBinLabel;

    private JButton drealButton, parsynButton, advancedOptionsButton, okButton, cancelButton;

    private JTextField drealOptions, parsynOptions, drealBinPath, parsynBinPath;

    private JFileChooser fc;

    private JFrame parent;

    AdvancedOptionsDialog(JFrame parent, String title) {

        super(parent, title, true);

        this.parent = parent;

        fc = new JFileChooser();

        parsynOptionsLabel = new JLabel("ParSyn options:");
        parsynOptions = new JTextField();
        parsynBinLabel = new JLabel("ParSyn binary:");
        parsynBinPath = new JTextField();
        parsynButton = new JButton("Browse");
        parsynButton.addActionListener(this);

        drealOptionsLabel = new JLabel("dReal options:");
        drealOptions = new JTextField();
        drealBinLabel = new JLabel("dReal binary:");
        drealBinPath = new JTextField();
        drealButton = new JButton("Browse");
        drealButton.addActionListener(this);

        parsynOptionsPanel = new JPanel(new GridLayout(2, 3));
        parsynOptionsPanel.add(parsynBinLabel);
        parsynOptionsPanel.add(parsynBinPath);
        parsynOptionsPanel.add(parsynButton);
        parsynOptionsPanel.add(parsynOptionsLabel);
        parsynOptionsPanel.add(parsynOptions);
        parsynOptionsPanel.add(new JLabel());

        drealOptionsPanel = new JPanel(new GridLayout(2, 3));
        drealOptionsPanel.add(drealBinLabel);
        drealOptionsPanel.add(drealBinPath);
        drealOptionsPanel.add(drealButton);
        drealOptionsPanel.add(drealOptionsLabel);
        drealOptionsPanel.add(drealOptions);
        drealOptionsPanel.add(new JLabel());

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("ParSyn", parsynOptionsPanel);
        tabbedPane.addTab("dReal", drealOptionsPanel);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        getRootPane().setDefaultButton(okButton);
        okButton.requestFocus();

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(420, 120));
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setResizable(false);
        pack();

        refresh();
    }

    private void setDefaultSizeAndLocation() {

        // Get the screen dimensions
        Dimension screenSize;
        try {
            Toolkit tk = Toolkit.getDefaultToolkit();
            screenSize = tk.getScreenSize();
        } catch (AWTError awe) {
            screenSize = new Dimension(640, 480);
        }

        // Get/set the frame dimensions
        Dimension frameSize = getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
            setSize(frameSize);
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
            setSize(frameSize);
        }

        // Position the frame
        int x = screenSize.width / 2 - frameSize.width / 2;
        int y = screenSize.height / 2 - frameSize.height / 2;
        setLocation(x, y);
    }

    public void refresh() {
        setDefaultSizeAndLocation();
        parsynOptions.setText(AdvancedOptionsModel.getParsynOptions());
        parsynBinPath.setText(AdvancedOptionsModel.getParsynBinPath());
        drealOptions.setText(AdvancedOptionsModel.getDrealOptions());
        drealBinPath.setText(AdvancedOptionsModel.getDrealBinPath());
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            AdvancedOptionsModel.setParsynOptions(parsynOptions.getText());
            AdvancedOptionsModel.setParsynBinPath(parsynBinPath.getText());
            AdvancedOptionsModel.setDrealOptions(drealOptions.getText());
            AdvancedOptionsModel.setDrealBinPath(drealBinPath.getText());
            setVisible(false);
        } else if (e.getSource() == cancelButton) {
            setVisible(false);
        } else if (e.getSource() == drealButton) {
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                drealBinPath.setText(fc.getSelectedFile().getAbsolutePath());
            }
        } else if (e.getSource() == parsynButton) {
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                parsynBinPath.setText(fc.getSelectedFile().getAbsolutePath());
            }
        }
    }

}
