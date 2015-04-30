package util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fedor on 30/04/15.
 */
public class BoxTable extends JTable {

    //private List<Box> boxes;
    //private Box domain;
    //private String[][] data;
    private String[] colNames;

    public BoxTable() {
        super(new DefaultTableModel());
    }

    public BoxTable(Box domain, List<Box> boxes) {
        super(new DefaultTableModel());

        colNames = domain.namesAsTableEntry();
        updateRows(boxes);
    }

    public BoxTable(Box domain) {
        this(domain, new ArrayList<Box>());
    }

    public void updateColNames(String[] colNames) {
        this.colNames = colNames;
        String[][] data = new String[0][colNames.length];
        setModel(new DefaultTableModel(data, colNames));
    }

    public void setDomain(Box domain) {
        this.colNames = domain.namesAsTableEntry();
        String[][] data = new String[1][];
        data[0] = domain.dataAsTableEntry();
        setModel(new DefaultTableModel(data, colNames));
    }

    public synchronized void updateRows(List<Box> boxes) {
        String[][] data = new String[boxes.size()][];
        for(int i = 0; i < boxes.size(); i++) {
            data[i] = boxes.get(i).dataAsTableEntry();
        }
        setModel(new DefaultTableModel(data, colNames));
    }


}
