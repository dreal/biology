package gui;

import util.*;
import util.Box;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fedor on 24/04/15.
 */
public class PlotPanel extends JPanel {

    private double xScale = 1;
    private double yScale = 1;
    private int panelWidth = 800;
    private int panelHeight = 800;
    private int panelLeft = 200;
    private int panelTop = 50;
    private int xGrid = 5;
    private int yGrid = 5;
    //private List<Box2D> boxes;
    private List<Box> boxes = new ArrayList<Box>();
    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;
    private List<String> params;
    private Box domain;

    private Color satColor = new Color(0,0,0);
    private Color unsatColor = new Color(255,255,255);
    private Color undetColor = new Color(180,180,180);

    public PlotPanel(Box domain) {
        super();
        this.domain = domain;
        xScale = panelWidth / domain.getIntervals().get(0).getWidth();
        yScale = panelHeight / domain.getIntervals().get(1).getWidth();
    }

    public PlotPanel() {
        super();
    }

    public int xCoor(double x) {
        return panelLeft + (int) Math.ceil(xScale * (x - domain.getIntervals().get(0).getLeft()));
    }

    public int yCoor(double y) {
        return panelTop + panelHeight - (int) Math.ceil(yScale * (y - domain.getIntervals().get(1).getLeft()));
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(200, 234, 255));
        g2.fillRect(panelLeft, panelTop, panelWidth, panelHeight);
        g2.setColor(new Color(0, 0, 0));
        g2.drawRect(panelLeft, panelTop, panelWidth, panelHeight);

        drawBoxes(g2);
        drawNet(g2);
        drawLabels(g2);
        //repaint();
    }

    public void updateBoxes(List<Box> boxes) {
        this.boxes = boxes;
        repaint();
    }

    public void drawBoxes(Graphics2D g2) {
        for(int i = 0; i < boxes.size(); i++) {
            if(boxes.get(i).getType() == Box.BoxType.SAT) {
                g2.setColor(satColor);
            }
            if(boxes.get(i).getType() == Box.BoxType.UNSAT) {
                g2.setColor(unsatColor);
            }
            if(boxes.get(i).getType() == Box.BoxType.UNDET) {
                g2.setColor(undetColor);
            }

            if(boxes.get(i).getIntervals().get(0).getName().equals(domain.getIntervals().get(0).getName())) {
                g2.fillRect(xCoor(boxes.get(i).getIntervals().get(0).getLeft()),
                        yCoor(boxes.get(i).getIntervals().get(1).getRight()),
                        (int) Math.ceil(xScale * boxes.get(i).getIntervals().get(0).getWidth()),
                        (int) Math.ceil(yScale * boxes.get(i).getIntervals().get(1).getWidth()));
                //System.out.println("normal");
            } else {
                g2.fillRect(xCoor(boxes.get(i).getIntervals().get(1).getLeft()),
                        yCoor(boxes.get(i).getIntervals().get(0).getRight()),
                        (int) Math.ceil(xScale * boxes.get(i).getIntervals().get(1).getWidth()),
                        (int) Math.ceil(yScale * boxes.get(i).getIntervals().get(0).getWidth()));
                //System.out.println("reversed");
            }

        }
    }

    public void drawNet(Graphics2D g2) {
        g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, 36));
        g2.setColor(new Color(0, 0, 0));
        DecimalFormat df = new DecimalFormat();
        // vertical lines and labels
        for(int i = 0; i <= xGrid; i++) {
            g2.drawLine(panelLeft + i * (panelWidth / xGrid), panelTop, panelLeft + i * (panelWidth / xGrid), panelTop + panelHeight);
            g2.drawString(df.format(domain.getIntervals().get(0).getLeft() + i * domain.getIntervals().get(0).getWidth() / xGrid), panelLeft + i * (panelWidth / xGrid) - 30, panelTop + panelHeight + 40);
        }
        // horizontal lines and labels
        for(int i = 0; i <= yGrid; i++) {
            g2.drawLine(panelLeft, panelTop + i * (panelHeight / yGrid), panelLeft + panelWidth, panelTop + i * (panelHeight / yGrid));
            g2.drawString(df.format(domain.getIntervals().get(1).getLeft() + i * domain.getIntervals().get(1).getWidth() / yGrid), panelLeft - 130, panelTop + panelHeight - i * (panelHeight / yGrid) + 10);
        }
    }

    public void drawLabels(Graphics2D g2) {

        g2.setFont(new Font(g2.getFont().getFontName(), Font.ITALIC, 64));
        AffineTransform at = new AffineTransform();
        g2.drawString(domain.getIntervals().get(0).getName(), panelLeft + panelWidth / 2, panelTop + panelHeight + 100);
        at.rotate(-Math.PI / 2);
        g2.setTransform(at);
        g2.drawString(domain.getIntervals().get(1).getName(), -(panelTop + panelHeight / 2), 60);
        at.rotate(Math.PI / 2);
        g2.setTransform(at);
    }

}
