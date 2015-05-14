package util;

import model.AdvancedOptionsModel;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Field;

/**
 * Created by fedor on 23/03/15.
 */
public class BackgroundWorker extends SwingWorker<Integer, Void> {

    private static LogTable logTable;

    private Process parsyn;

    public BackgroundWorker(LogTable logTable) {
        this.logTable = logTable;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        Runtime exec = Runtime.getRuntime();
        // Calling ParSyn

        String parsynCall = AdvancedOptionsModel.getParsynBinPath() + " -l " +
                                AdvancedOptionsModel.getDrealBinPath() + " " +
                                    AdvancedOptionsModel.getParsynOptions() + " model.xml --dreal " +
                                        AdvancedOptionsModel.getDrealOptions();

        parsyn = exec.exec(parsynCall);

        // getting ParSyn PID on unix/linux systems
        if(parsyn.getClass().getName().equals("java.lang.UNIXProcess")) {
            try {
                Field f = parsyn.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                AdvancedOptionsModel.setParsynPID(f.getInt(parsyn));
            } catch (Throwable ex) {
            }
        }

        try {

            InputStream cout = parsyn.getInputStream();
            BufferedReader coutReader = new BufferedReader(new InputStreamReader(cout));

            String line;
            String message = "";
            while ((line = coutReader.readLine()) != null) {
                message += line + "\n";
            }
            if(!message.isEmpty()) {
                logTable.addEntry("ParSyn", message);
            }

            InputStream cerr = parsyn.getErrorStream();
            BufferedReader cerrReader = new BufferedReader(new InputStreamReader(cerr));
            line = "";
            message = "";
            while ((line = cerrReader.readLine()) != null) {
                message += line + "\n";
            }
            if(!message.isEmpty()) {
                logTable.addEntry("ParSyn", message);
            }

            cout.close();
            coutReader.close();
            cerr.close();
            cerrReader.close();

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return new Integer(0);
    }

    public Process getParsyn() {
        return parsyn;
    }

}
