package main.panel.navigation;

import hilfe.guiHilfe.SpecialColor;
import main.hauptfenster.testCaseInfo.TestCaseInfo;
import main.panel.SuccessBar;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Enumeration;

/**
 * TreeTableCellRenderer stellt die einzelnen Zellen des Baumes zur 
 * Anzeige der in der Anwendung geoeffneten Testfaelle dar
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */
public class TreeTableCellRenderer extends JPanel implements TableCellRenderer
{
    private static final long serialVersionUID = 1L;
    
    // Wurzelknoten des Baums
	private DefaultMutableTreeNode root;
 
    /**
     * Konstruktor, erwartet den Wurzelknoten des Baumes
     *
     * @param root Wurzelknoten des Baumes
     */
    public TreeTableCellRenderer(DefaultMutableTreeNode root)
    {
        this.root = root;
    }


    /**
     * liefert ein Panel zur Darstellung des Zelleninhaltes zurueck
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
        boolean isSelected, boolean hasFocus, int row, int column)
    {
        this.removeAll();
        this.setOpaque(true);

        if (isSelected)
        {
            this.setBackground(SpecialColor.WHITEGRAY);
        }
        else
        {
            this.setBackground(SpecialColor.WHITE);
        }
        this.setLayout(null);
        this.removeAll();
        if (value.getClass().getSuperclass() == TestCaseInfo.class)
        {
            TestCaseInfo tci = (TestCaseInfo) value;

            int numberOfSuccessfulConfigurations 
                = tci.getNumberOfAllSuccessfulTestConfigurations();
            int numberOfByAssumptionsAbortedConfigurations
                = tci.getNumberOfAllByAssumptionsAbortedTestConfigurations();
            int numberOfSkippedConfigurations
                = tci.getNumberOfAllSkippedTestConfigurations();
            int numberOfConfigurations           
                = tci.getNumberOfAllTestConfigurations();
            int numberOfFailedConfigurations     
                = numberOfConfigurations - numberOfSuccessfulConfigurations
                - numberOfByAssumptionsAbortedConfigurations
                -  numberOfSkippedConfigurations;

            SuccessBar sb = new SuccessBar(numberOfSuccessfulConfigurations, 
                numberOfFailedConfigurations, numberOfByAssumptionsAbortedConfigurations,
                    numberOfSkippedConfigurations, numberOfConfigurations);
            sb.setBounds(5,3,71,19);
            this.add(sb);

            sb.setToolTipText("<html><font color=\"#62DE10\">"
                + numberOfSuccessfulConfigurations + " / <font color=\"#000000\">"
                + numberOfConfigurations+"</html>");
        }
        else
        {
            Enumeration<?> enumeration = this.root.breadthFirstEnumeration();
            DefaultMutableTreeNode dmtn = null;
            while (enumeration.hasMoreElements())
            {
                DefaultMutableTreeNode _dmtn 
                    = (DefaultMutableTreeNode) enumeration.nextElement();
                if (_dmtn.getUserObject().toString().equals(value.toString()))
                {
                    dmtn = _dmtn;
                    break;
                }
            }

            int numberOfConfigurations = 0;
            int numberOfSuccessfulConfigurations = 0;
            int numberOfByAssumptionsAbortedConfigurations = 0;
            int numberOfSkippedConfigurations = 0;

            enumeration = dmtn.depthFirstEnumeration();
            while (enumeration.hasMoreElements())
            {
                DefaultMutableTreeNode _dmtn 
                    = (DefaultMutableTreeNode) enumeration.nextElement();
                if (_dmtn.isLeaf())
                {
                    TestCaseInfo tci = (TestCaseInfo) _dmtn.getUserObject();
                    numberOfConfigurations += tci.getNumberOfAllTestConfigurations();
                    numberOfByAssumptionsAbortedConfigurations +=
                        tci.getNumberOfAllByAssumptionsAbortedTestConfigurations();
                    numberOfSkippedConfigurations += tci.getNumberOfAllSkippedTestConfigurations();
                    numberOfSuccessfulConfigurations 
                        += tci.getNumberOfAllSuccessfulTestConfigurations();
                }
            }

            SuccessBar sb;
            sb = new SuccessBar(numberOfSuccessfulConfigurations,
                numberOfConfigurations - numberOfSuccessfulConfigurations
                    - numberOfByAssumptionsAbortedConfigurations - numberOfSkippedConfigurations
                    , numberOfByAssumptionsAbortedConfigurations, numberOfSkippedConfigurations
                    , numberOfConfigurations);
            sb.setBounds(5,3,71,19);
            this.add(sb);
        }
        return this;
    }
}