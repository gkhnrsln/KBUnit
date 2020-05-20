package main.panel.navigation;

import hilfe.guiHilfe.SpecialColor;
import main.hauptfenster.testCaseInfo.TestCaseInfo;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;

/**
 * TreeRenderer zeichnet die Baumstruktur zur Anzeige der in der Anwendung
 * geoeffneten Testfaelle
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */

public class TreeRenderer extends DefaultTreeCellRenderer
{
  	private static final long serialVersionUID = 1L;

  	// Symbole
  	private Icon ownLeafIcon = new ImageIcon(
  	    Toolkit.getDefaultToolkit()
  	        .createImage(new File("icons\\testcase.png").getAbsolutePath()));
  	private Icon folderOpenedIcon = new ImageIcon(
  		Toolkit.getDefaultToolkit()
  		    .createImage(new File("icons\\open_file.png").getAbsolutePath()));
  	private Icon folderClosedIcon = new ImageIcon(
  		Toolkit.getDefaultToolkit()
  		    .createImage(new File("icons\\open_file_c.png").getAbsolutePath()));
	
    /**
     * Konstruktor
     */
    public TreeRenderer()
    {
        super();
        
        setBorderSelectionColor(new Color(145,0,0));
        setBackgroundSelectionColor(SpecialColor.WHITEGRAY);
    }


    /**
     * liefert die Zelle in Form eines Panels zurück, auf dem ein Label zur
     * Anzeige des Baumknotens platziert ist
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, 
        boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        BasicTreeUI basicTreeUi = (BasicTreeUI) tree.getUI();
        try
        {
            Field fld = BasicTreeUI.class.getDeclaredField("paintLines");
            fld.setAccessible(true);
            fld.set(basicTreeUi, Boolean.TRUE);
        } 
        catch (SecurityException | IllegalArgumentException | 
            NoSuchFieldException | IllegalAccessException e)
        { }

        basicTreeUi.setExpandedIcon(new ImageIcon("D:/treeMinus.gif"));
        basicTreeUi.setCollapsedIcon(new ImageIcon("D:/treePlus.gif"));

        basicTreeUi.setExpandedIcon(null);
        basicTreeUi.setCollapsedIcon(null);

        JPanel pnl = new JPanel();
        pnl.setOpaque(false);

        JLabel lbl;

        pnl.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 3);

        DefaultMutableTreeNode dtmn = (DefaultMutableTreeNode) value;

        if (dtmn.getUserObject().getClass().getSuperclass() == TestCaseInfo.class)
        {
            TestCaseInfo tci = (TestCaseInfo) dtmn.getUserObject();
            String text = tci.getIdentifierName();
            String tooltipText = tci.getPackageName() + "." + tci.getClassName() + "." + tci.getIdentifierName();
            if(tci != null && tci.getTesttype() == TestCaseInfo.TESTTYPE_JUNIT_5)
            {
                if(tci.getDisplayName() != null && !tci.getDisplayName().equals(""))
                {
                    text = tci.getDisplayName();
                }
                if(tci.isDisabled()){
                    String disabledMessage = "";
                    disabledMessage = tci.getDisabledMessage();
                    if(!disabledMessage.equals("")){
                        tooltipText += " (" + disabledMessage + ")";
                    }
                    else{
                        tooltipText += " (" + "Disabled" + ")";
                    }
                }
            }
            pnl.setToolTipText(tooltipText);
            lbl = new JLabel(text);
            if(tci != null && tci.isDisabled())
                lbl.setForeground(Color.GRAY);

            lbl.setIcon(this.ownLeafIcon);
            lbl.setIconTextGap(5);

            gbc.ipadx=2000;
            gbc.gridx = 0;
            pnl.add(lbl,gbc);
        }
        else
        {
            lbl = new JLabel(dtmn.getUserObject().toString());
            if (expanded)
            {
                lbl.setIcon(folderOpenedIcon);
            }
            else
            {
                lbl.setIcon(folderClosedIcon);
            }
            lbl.setIconTextGap(5);
            gbc.ipadx=2000;
            pnl.add(lbl,gbc);
        }
        lbl.setOpaque(false);
        return pnl;
    }
}