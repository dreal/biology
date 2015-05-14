package util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Date;

/**
 * Created by fedor on 14/05/15.
 */
public class LogTable extends JTable {

    public LogTable() {
        super(new DefaultTableModel(new String[0][0], new String[]{"Source", "Message", "Time stamp"}));
    }

    public void addEntry(String source, String message) {
        DefaultTableModel model = (DefaultTableModel) getModel();
        model.addRow(new String[]{source, message, (new Date()).toString()});
        setModel(model);
    }


}
