package util;

import model.AdvancedOptionsModel;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Field;

/**
 * Created by fedor on 23/03/15.
 */
public class BackgroundWorker extends SwingWorker<Integer, Void> {

    private static JTextArea logTextArea;

    public BackgroundWorker(JTextArea logTextArea) {
        this.logTextArea = logTextArea;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        Runtime exec = Runtime.getRuntime();
        // Calling ParSyn

        String parsynCall = AdvancedOptionsModel.getParsynBinPath() + " -l " +
                                AdvancedOptionsModel.getDrealBinPath() + " " +
                                    AdvancedOptionsModel.getParsynOptions() + " model.xml --dreal " +
                                        AdvancedOptionsModel.getDrealOptions();

        Process parsyn = exec.exec(parsynCall);

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
                logTextArea.append("Message: " + message);
            }

            InputStream cerr = parsyn.getErrorStream();
            BufferedReader cerrReader = new BufferedReader(new InputStreamReader(cout));
            line = "";
            message = "";
            while ((line = coutReader.readLine()) != null) {
                message += line + "\n";
            }
            if(!message.isEmpty()) {
                logTextArea.append("Message: " + message);
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
}
