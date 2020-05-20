package hilfe.guiHilfe;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * TabbedPaneUI veraendert das Aussehen der JTabbedPane-Komponente, da diese
 * bei Nutzung des Numbus-L und F nicht zufriedenstellend dargestellt wird
 *
 * da lediglich Methoden von BasicTabbedPaneUI ueberschrieben werden, erfolgt
 * keine weitere Dokumentation
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 * @version 1.0
 */
public final class TabbedPaneUI extends BasicTabbedPaneUI
{
    private static final Insets NO_INSETS = new Insets(0, 10, 0, 0);
    private ColorSet selectedColorSet;
    private ColorSet backgroundColorSet;
    private ColorSet hoverColorSet;
    private ColorSet unselectedColorSet;
    private boolean contentTopBorderDrawn = true;
    private Insets contentInsets = new Insets(10, 10, 10, 10);
    private int lastRollOverTab = -1;

    public static ComponentUI createUI(JComponent c)
    {
        return new TabbedPaneUI();
    }

    public TabbedPaneUI()
    {
        this.selectedColorSet = new ColorSet();
        this.selectedColorSet.topGradColor1 = new Color(255, 200, 0);
        this.selectedColorSet.topGradColor2 = new Color(255, 220, 0);

        this.selectedColorSet.bottomGradColor1 = new Color(255, 200, 0);
        this.selectedColorSet.bottomGradColor2 = new Color(255, 230, 0);

        this.backgroundColorSet = new ColorSet();
        this.backgroundColorSet.topGradColor1 = new Color(245, 245, 245);
        this.backgroundColorSet.topGradColor2 = new Color(245, 245, 245);

        this.backgroundColorSet.bottomGradColor1 = new Color(245, 245, 245);
        this.backgroundColorSet.bottomGradColor2 = new Color(255, 255, 255);

        this.hoverColorSet = new ColorSet();
        this.hoverColorSet.topGradColor1 = new Color(244, 244, 244);
        this.hoverColorSet.topGradColor2 = new Color(223, 223, 223);

        this.hoverColorSet.bottomGradColor1 = new Color(211, 211, 211);
        this.hoverColorSet.bottomGradColor2 = new Color(235, 235, 235);

        this.unselectedColorSet = new ColorSet();
        this.unselectedColorSet.topGradColor1 = new Color(244, 244, 244);
        this.unselectedColorSet.topGradColor2 = new Color(223, 223, 223);

        this.unselectedColorSet.bottomGradColor1 = new Color(221, 221, 221);
        this.unselectedColorSet.bottomGradColor2 = new Color(245, 245, 245);

        maxTabHeight = 30;

        setContentInsets(0);
    }


    public void setContentTopBorderDrawn(boolean b)
    {
        this.contentTopBorderDrawn = b;
    }


    public void setContentInsets(Insets i)
    {
        this.contentInsets = i;
    }


    public void setContentInsets(int i)
    {
        this.contentInsets = new Insets(i, i, i, i);
    }


    @Override
    public int getTabRunCount(JTabbedPane pane)
    {
        return 1;
    }

    @Override
    protected void installDefaults()
    {
        super.installDefaults();

        RollOverListener l = new RollOverListener();
        tabPane.addMouseListener(l);
        tabPane.addMouseMotionListener(l);

        tabAreaInsets = NO_INSETS;
        tabInsets = new Insets(0, 0, 0, 0);
    }


    protected boolean scrollableTabLayoutEnabled()
    {
        return false;
    }


    @Override
    protected Insets getContentBorderInsets(int tabPlacement)
    {
        return this.contentInsets;
    }


    @Override
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight)
    {
        return 26;
    }


    @Override
    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics)
    {
        int w = super.calculateTabWidth(tabPlacement, tabIndex, metrics);
        w += 10;
        return w;
    }


    @Override
    protected int calculateMaxTabHeight(int tabPlacement)
    {
        return 26;
    }


    @Override
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(new GradientPaint(0, 0, this.backgroundColorSet.topGradColor1,  
        	0, 10, this.backgroundColorSet.topGradColor2));
        g2d.fillRect(0, 0, tabPane.getWidth(), 20);

        g2d.setPaint(new GradientPaint(0, 10, this.backgroundColorSet.bottomGradColor1,  
        	0, 26, this.backgroundColorSet.bottomGradColor2));
        g2d.fillRect(0, 13, tabPane.getWidth(), 13);
        super.paintTabArea(g, tabPlacement, selectedIndex);

        if (this.contentTopBorderDrawn)
        {
            g2d.setColor(SpecialColor.LIGHTORANGE);
            g2d.drawLine(0, 25, tabPane.getWidth() - 1, 25);
        }
    }


    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, 
    	int tabIndex, int x, int y, int w, int h, boolean isSelected)
    {
        Graphics2D g2d = (Graphics2D) g;
        ColorSet colorSet;

        Rectangle rect = rects[tabIndex];

        if (isSelected)
        {
            colorSet = this.selectedColorSet;
        }
        else if (getRolloverTab() == tabIndex)
        {
            colorSet = this.hoverColorSet;
        } else
        {
            colorSet = this.unselectedColorSet;
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
        	RenderingHints.VALUE_ANTIALIAS_ON);

        int width = rect.width;
        int xpos = rect.x;

        g2d.setPaint(new GradientPaint(xpos, 0, colorSet.topGradColor1, xpos, 10, 
        	colorSet.topGradColor2));
        g2d.fillRoundRect(xpos, 0, width, 20,5,5);

        g2d.setPaint(new GradientPaint(0, 10, colorSet.bottomGradColor1, 0, 26, 
        	colorSet.bottomGradColor2));
        g2d.fillRect(xpos, 13, width, 13);

        if (this.contentTopBorderDrawn)
        {
            g2d.setColor(SpecialColor.LIGHTORANGE);
            g2d.drawLine(rect.x, 25, rect.x + rect.width - 1, 25);
        }
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, 
        int x, int y, int w, int h, boolean isSelected) {}
    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, 
        int selectedIndex, int x, int y, int w, int h) {}
    @Override
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, 
        int selectedIndex, int x, int y, int w, int h) {}
    @Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, 
        int selectedIndex, int x, int y, int w, int h) {}
    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, 
        int selectedIndex, int x, int y, int w, int h) {}
    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, 
        int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {}


    @Override
    protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected)
    {
        return 0;
    }


    public Insets getTabInsets()
    {
        return  new Insets(0, 7, 0, 0);
    }


    @Override
    protected Insets getTabAreaInsets(int tabPlacement)
    {
        return  new Insets(0, 7, 0, 0);
    }


    @Override
    protected Insets getSelectedTabPadInsets(int tabPlacement)
    {
        return  new Insets(0, 0, 0, 0);
    }

    private class ColorSet
    {
        Color topGradColor1;
        Color topGradColor2;
        Color bottomGradColor1;
        Color bottomGradColor2;
    }


    private class RollOverListener implements MouseMotionListener, MouseListener
    {
        @Override
        public void mouseDragged(MouseEvent e) {}
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseMoved(MouseEvent e)
        {
                checkRollOver();
        }

        @Override
        public void mouseEntered(MouseEvent e)
        {
                checkRollOver();
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
                tabPane.repaint();
        }

        private void checkRollOver()
        {
            int currentRollOver = getRolloverTab();
            if (currentRollOver != lastRollOverTab)
            {
                lastRollOverTab = currentRollOver;
                Rectangle tabsRect = new Rectangle(0, 0, tabPane.getWidth(), 25);
                tabPane.repaint(tabsRect);
            }
        }
    }
}