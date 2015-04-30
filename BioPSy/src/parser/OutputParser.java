package parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import util.Box;
import util.Interval;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fedor on 28/04/15.
 */
public class OutputParser {

    private static double progress = 0;
    private static List<Box> boxes;

    public static synchronized double getProgress() {
        return progress;
    }

    public static synchronized List<Box> getBoxes() {
        return boxes;
    }

    public static synchronized void parse(String filename) throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        boxes = new ArrayList<Box>();

        File outputFile = new File(filename);
        if(outputFile.exists()) {
            Document dom = db.parse(outputFile);
            Element root = dom.getDocumentElement();
            // parsing progress
            progress = Double.parseDouble(root.getAttribute("progress"));
            // parsing boxes
            NodeList times = root.getElementsByTagName("point");
            for (int j = 0; j < times.getLength(); j++) {
                double timeValue = Double.parseDouble(times.item(j).getAttributes().getNamedItem("time").getNodeValue());
                NodeList boxesList = times.item(j).getChildNodes();
                for (int i = 0; i < boxesList.getLength(); i++) {
                    if (boxesList.item(i).getNodeName().equals("box")) {
                        if (boxesList.item(i).getAttributes().getNamedItem("type").getNodeValue().equals("sat")) {
                            List<Interval> intervals = new ArrayList<Interval>();
                            for(int k = 0; k < boxesList.item(i).getChildNodes().getLength(); k++) {
                                if(boxesList.item(i).getChildNodes().item(k).getAttributes() != null) {
                                    intervals.add(new Interval(Double.parseDouble(boxesList.item(i).getChildNodes().item(k).getAttributes().getNamedItem("left").getNodeValue()),
                                                                Double.parseDouble(boxesList.item(i).getChildNodes().item(k).getAttributes().getNamedItem("right").getNodeValue()),
                                                                    boxesList.item(i).getChildNodes().item(k).getAttributes().getNamedItem("var").getNodeValue()));

                                }
                            }
                            boxes.add(new Box(intervals, timeValue, Box.BoxType.SAT));
                        }
                        if (boxesList.item(i).getAttributes().getNamedItem("type").getNodeValue().equals("unsat")) {
                            List<Interval> intervals = new ArrayList<Interval>();
                            for(int k = 0; k < boxesList.item(i).getChildNodes().getLength(); k++) {
                                if(boxesList.item(i).getChildNodes().item(k).getAttributes() != null) {
                                    intervals.add(new Interval(Double.parseDouble(boxesList.item(i).getChildNodes().item(k).getAttributes().getNamedItem("left").getNodeValue()),
                                                                Double.parseDouble(boxesList.item(i).getChildNodes().item(k).getAttributes().getNamedItem("right").getNodeValue()),
                                                                    boxesList.item(i).getChildNodes().item(k).getAttributes().getNamedItem("var").getNodeValue()));

                                }
                            }
                            boxes.add(new Box(intervals, timeValue, Box.BoxType.UNSAT));
                        }
                        if (boxesList.item(i).getAttributes().getNamedItem("type").getNodeValue().equals("undec")) {
                            List<Interval> intervals = new ArrayList<Interval>();
                            for(int k = 0; k < boxesList.item(i).getChildNodes().getLength(); k++) {
                                if(boxesList.item(i).getChildNodes().item(k).getAttributes() != null) {
                                    intervals.add(new Interval(Double.parseDouble(boxesList.item(i).getChildNodes().item(k).getAttributes().getNamedItem("left").getNodeValue()),
                                                                Double.parseDouble(boxesList.item(i).getChildNodes().item(k).getAttributes().getNamedItem("right").getNodeValue()),
                                                                    boxesList.item(i).getChildNodes().item(k).getAttributes().getNamedItem("var").getNodeValue()));

                                }
                            }
                            boxes.add(new Box(intervals, timeValue, Box.BoxType.UNDET));
                        }
                    }
                }
            }
        }

        Collections.sort(boxes);
    }
}
