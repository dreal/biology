package util;

import model.AdvancedOptionsModel;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.EventListener;

/**
 * Created by fedor on 23/03/15.
 */
public class BackgroundWorker extends SwingWorker<Integer, Void> {

    private static JTextArea outTextArea;

    public BackgroundWorker(JTextArea outTextArea) {
        this.outTextArea = outTextArea;
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
            InputStream par = parsyn.getInputStream();
            InputStreamReader isr = new InputStreamReader(par);
            BufferedReader br = new BufferedReader(isr);

            String line;
            outTextArea.setText("");
            while ((line = br.readLine()) != null) {
                outTextArea.append(line + "\n");
            }

            String error = "";
            InputStream par2 = parsyn.getErrorStream();
            int read = par2.read();
            while (read != -1) {
                error += (char) read;
                read = par2.read();
            }
            if (error != "") {
                outTextArea.append("Error message: " + error + "\n");
            }
            br.close();
            isr.close();
            par.close();
            par2.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return new Integer(0);
    }
}
