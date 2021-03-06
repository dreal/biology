package gui;

import model.TimeSeriesModel;
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
public class PlotArea extends JPanel {

    private double xScale = 1;
    private double yScale = 1;
    private int panelWidth = 400;
    private int panelHeight = 400;
    private int panelLeft = 170;
    private int panelTop = 50;
    private int legendWidth = 400;
    private int legendHeight = 400;
    private int legendLeft = panelLeft + panelWidth + 50;
    private int legendTop = panelTop;
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

    private Color currentColor = new Color(227, 114, 90);
    private Color satColor = new Color(0,0,0);
    private Color unsatColor = new Color(255,255,255);
    private Color undetColor = new Color(180,180,180);
    private Color domainColor = new Color(200, 234, 255);

    private JComboBox timeComboBox;

    public PlotArea(Box domain) {
        super();
        this.domain = domain;
        xScale = panelWidth / domain.getIntervals().get(0).getWidth();
        yScale = panelHeight / domain.getIntervals().get(1).getWidth();
    }

    public PlotArea() {
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
        g2.setColor(domainColor);
        g2.fillRect(panelLeft, panelTop, panelWidth, panelHeight);
        g2.setColor(new Color(0, 0, 0));
        g2.drawRect(panelLeft, panelTop, panelWidth, panelHeight);

        drawBoxes(g2);
        drawNet(g2);
        drawLabels(g2);
        drawLegend(g2);
        //repaint();
    }

    public void updateBoxes(List<Box> boxes) {
        this.boxes = boxes;
        repaint();
    }

    public void drawBoxes(Graphics2D g2) {
        for(int i = 0; i < boxes.size(); i++) {
            if(boxes.get(i).getType() == Box.BoxType.SAT) {
                if(boxes.get(i).getTime() == TimeSeriesModel.getTimePoints().get(TimeSeriesModel.getTimePoints().size() - 1))
                {
                    g2.setColor(satColor);
                } else {
                    g2.setColor(currentColor);
                }
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
        g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, 18));
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
            g2.drawString(df.format(domain.getIntervals().get(1).getLeft() + i * domain.getIntervals().get(1).getWidth() / yGrid), panelLeft - 80, panelTop + panelHeight - i * (panelHeight / yGrid) + 10);
        }
    }

    public void drawLabels(Graphics2D g2) {

        g2.setFont(new Font(g2.getFont().getFontName(), Font.ITALIC, 36));
        AffineTransform at = new AffineTransform();
        g2.drawString(domain.getIntervals().get(0).getName(), panelLeft + panelWidth / 2, panelTop + panelHeight + 100);
        at.rotate(-Math.PI / 2);
        g2.setTransform(at);
        g2.drawString(domain.getIntervals().get(1).getName(), -(panelTop + panelHeight / 2), 60);
        at.rotate(Math.PI / 2);
        g2.setTransform(at);
    }

    public void drawLegend(Graphics2D g2) {

        //g2.setColor(new Color(0,0,0));
        //g2.drawRect(legendLeft, legendTop, legendWidth, legendHeight);
        //g2.setFont(new Font(g2.getFont().getFontName(), Font.ITALIC, 36));
        //g2.drawString("Legend:", legendLeft + legendWidth / 2, legendTop + 30);

        g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, 18));

        g2.setColor(domainColor);
        g2.fillRect(legendLeft + 30, legendTop + 50, 50, 50);
        g2.setColor(new Color(0,0,0));
        g2.drawRect(legendLeft + 30, legendTop + 50, 50, 50);
        g2.drawString("Unexplored boxes", legendLeft + 90, legendTop + 85);

        g2.setColor(currentColor);
        g2.fillRect(legendLeft + 30, legendTop + 100, 50, 50);
        g2.setColor(new Color(0,0,0));
        g2.drawRect(legendLeft + 30, legendTop + 100, 50, 50);
        g2.drawString("Currently explored boxes", legendLeft + 90, legendTop + 135);

        g2.setColor(satColor);
        g2.fillRect(legendLeft + 30, legendTop + 150, 50, 50);
        g2.setColor(new Color(0,0,0));
        g2.drawRect(legendLeft + 30, legendTop + 150, 50, 50);
        g2.drawString("SAT boxes", legendLeft + 90, legendTop + 185);

        g2.setColor(undetColor);
        g2.fillRect(legendLeft + 30, legendTop + 200, 50, 50);
        g2.setColor(new Color(0,0,0));
        g2.drawRect(legendLeft + 30, legendTop + 200, 50, 50);
        g2.drawString("Undetermined boxes", legendLeft + 90, legendTop + 235);

        g2.setColor(unsatColor);
        g2.fillRect(legendLeft + 30, legendTop + 250, 50, 50);
        g2.setColor(new Color(0,0,0));
        g2.drawRect(legendLeft + 30, legendTop + 250, 50, 50);
        g2.drawString("UNSAT boxes", legendLeft + 90, legendTop + 285);

    }

}
