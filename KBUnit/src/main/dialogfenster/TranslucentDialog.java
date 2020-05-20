package main.dialogfenster;

import hilfe.guiHilfe.SpecialColor;
import main.hauptfenster.BasicFrameView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

/**
 * TranslucentDialog ist eine Klasse, die selbst von JDialog erbt,
 * ihre Komponenten aber nicht auf ein Dialogfenster, sondern das GlassPane
 * des Besitzerfensters zeichnet, wodurch die Dialogelemente immer zentriert
 * im Besitzerfenster vor ausgegrautem Hintergrund erscheinen; KBUnits Dialoge
 * erben allesamt vom TranslucentDialog, um diese Darstellung zu verwenden.
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 * <br>
 */

class TranslucentDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	// Panel, auf dem die einzelnen GUI-Komponenten platziert werden
    private TranslucentPanel pnl;

    private ContentPanel pnlContent;
    private GridBagLayout gbl;
    private GridBagConstraints gbc;

    // gibt an, ob der Dialog mittels OK/Ja geschlossen wurde
    private boolean accepted;

    private class ContentPanel extends JPanel
    {
        private static final long serialVersionUID = 1L;
		String title;

		/**
		 * erstellt ein Panel fuer den Inhalt des Dialogfensters mit dem vorgegebenen Titel
		 * @param title , enthaelt den Titel fuer das ContentPanel
		 */
        public ContentPanel(String title)
        {
            super();
            this.setOpaque(false);
            this.title = title;
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            g.setColor(new Color(0, 0, 0, 0));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(new Color(Math.min(255, SpecialColor.ORANGE.getRed() + 20), 
            	Math.min(255, SpecialColor.ORANGE.getGreen() + 20), 
            	Math.min(255,SpecialColor.ORANGE.getBlue() + 20)));
            g.fillRoundRect(0, 0, getWidth()-1, 15, 5, 5);

            g.setColor(Color.WHITE);
            g.drawLine(1, 15, getWidth() - 2, 15);
            g.fillRect(1, 16, getWidth() - 2, getHeight() - 17);

            // Text soll antialiased sein
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
            	RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString(title, 8, 12);

            g.setColor(SpecialColor.ORANGE);
            g.drawRoundRect(0, 0, getWidth()-1, getHeight() - 1, 5, 5);
        }

        @Override
        public void paint(Graphics g)
        {
            super.paint(g);
            g.setColor(SpecialColor.ORANGE);
            g.drawRoundRect(0, 0, getWidth()-1, getHeight() - 1, 5, 5);
        }

        public void setTitle(String title)
        {
            this.title = title;
        }
    }


    private class TranslucentPanel extends JPanel
    { 
  		private static final long serialVersionUID = 1L;

		public TranslucentPanel()
        {
            super();
            this.addMouseListener(new MouseAdapter() {});
            this.addKeyListener(new KeyAdapter() {});
            this.addMouseMotionListener(new MouseMotionAdapter() {});
            this.addFocusListener(new FocusAdapter() {});
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            g.setColor(new Color(230, 230, 230, 180));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
     

    public TranslucentDialog(String title, int width, int height)
    {
        JFrame frame = (JFrame)BasicFrameView.getFrames()[0];
        this.pnl = new TranslucentPanel();
        this.pnl.setOpaque(false);
        this.pnl.setVisible(true);
        this.gbl = new GridBagLayout();
        this.pnl.setLayout(this.gbl);

        this.gbc = new GridBagConstraints();
        this.gbc.ipadx = width;
        this.gbc.ipady = height;
        this.gbc.fill = GridBagConstraints.NONE;
        this.gbc.weightx = 0;
        this.gbc.weighty = 0;

        this.pnlContent = new ContentPanel(title);
        this.pnlContent.setVisible(true);
        this.pnlContent.setLayout(null);

        this.pnl.add(this.pnlContent,this.gbc);

        frame.setGlassPane(this.pnl);
        this.setModalityType(ModalityType.DOCUMENT_MODAL);
        this.setUndecorated(true);
    }

    
    @Override
    public void setSize(int width, int height)
    {
        // gridbagconstarints aendern
        this.gbc.ipadx = width;
        this.gbc.ipady = height;
        this.gbc.fill = GridBagConstraints.NONE;
        this.gbc.weightx = 0;
        this.gbc.weighty = 0;
        this.gbl.setConstraints(this.pnlContent, this.gbc);
        repaint();
    }


    @Override
    public Component add(Component comp)
    {
        return this.pnlContent.add(comp);
    }


    @Override
    public void setLayout(LayoutManager manager)
    {
        if (this.pnlContent != null)
        {
            this.pnlContent.setLayout(manager);
        }
    }


    @Override
    public void add(Component comp, Object constraints)
    {
        if (this.pnlContent != null)
        {
            this.pnlContent.add(comp, constraints);
        }
    }


    @Override
    public void repaint()
    {
        super.repaint();
        this.pnl.repaint();
        JFrame frame = (JFrame) BasicFrameView.getFrames()[0];
        frame.getGlassPane().setVisible(false);
        frame.getGlassPane().setVisible(true);
    }
 

    @Override
    public void setTitle(String title)
    {
        this.pnlContent.setTitle(title);
        repaint();
    }


    @Override
    public void setVisible(boolean b)
    {
        JFrame frame = (JFrame) BasicFrameView.getFrames()[0];
        frame.setGlassPane(this.pnl);
        frame.getGlassPane().setVisible(b);
        super.setVisible(b);  
    }


    public boolean accepted()
    {
        this.accepted = false;
        this.setVisible(true);
        return this.accepted;
    }
}
