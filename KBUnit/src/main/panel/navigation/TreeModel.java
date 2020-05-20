package main.panel.navigation;

import main.hauptfenster.testCaseInfo.TestCaseInfo;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * TreeModel liefert das Model fuer die Baumansicht geoeffneter Testfaelle
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */
public class TreeModel extends AbstractTreeTableModel
{
	// Namen der Aeste des Baums
    private String [] titles = {"Test"," B "};


    /**
     * Konstruktor, erwartet Wurzelknoten
     *
     * @param root Wurzelknoten des Baums
     */
    public TreeModel(DefaultMutableTreeNode root)
    {
        super(root);
    }


    /**
     * liefert den Namen einer Spalte des Baumes zurueck
     *
     * @param column Nummer der Spalte
     *
     * @return Name der Spalte
     */
    @Override
    public String getColumnName(int column)
    {
        if (column < titles.length)
        {
            return (String) titles[column];
        }
        else
        {
            return "";
        }
    }


    /**
     * liefert die Spaltenzahl des Baumes zurueck
     *
     * @return Spaltenzahl
     */
    @Override
    public int getColumnCount()
    {
        return titles.length;
    }


    /**
     * liefert die Klasse einer Spalte des Baumes zurueck
     *
     * @param column Nummer der Spalte
     *
     * @return Klasse der Spalte
     */
    @Override
    public Class<?> getColumnClass(int column)
    {
        return String.class;
    }


    /**
     * liefert den Wert einer Zelle innerhalb des Knotens zurueck
     */
    @Override
    public Object getValueAt(Object arg0, int arg1)
    {
        if(arg0 instanceof TestCaseInfo)
        {
            TestCaseInfo data = (TestCaseInfo)arg0;
            if(data != null)
            {
                return data;
            }
        }

        if(arg0 instanceof String)
        {
            String data = (String)arg0;
            if(data != null)
            {
                return data;
            }
        }

        if(arg0 instanceof DefaultMutableTreeNode)
        {
            DefaultMutableTreeNode dataNode 
                = (DefaultMutableTreeNode)arg0;

            if(dataNode.getUserObject().getClass().getSuperclass() 
            	== TestCaseInfo.class)
            {
                TestCaseInfo data = (TestCaseInfo) dataNode.getUserObject();
                if(data != null)
                {
                    return data;
                }
            }

            if(dataNode.getUserObject().getClass() == String.class)
            {
                String data = (String) dataNode.getUserObject();
                if(data != null)
                {
                    return data;
                }
            }
        }
        return null;
    }


    /**
     * liefert das Kind eines Knoten zurueck
     */
    @Override
    public Object getChild(Object arg0, int arg1)
    {
        if(arg0 instanceof DefaultMutableTreeNode)
        {
            DefaultMutableTreeNode nodes = (DefaultMutableTreeNode)arg0;
            return nodes.getChildAt(arg1);
        }
        return null;
    }


    /**
     * liefert die Kinderzahl eines Knoten zurueck
     */
    @Override
    public int getChildCount(Object arg0)
    {
        if(arg0 instanceof DefaultMutableTreeNode)
        {
            DefaultMutableTreeNode nodes = (DefaultMutableTreeNode)arg0;
            return nodes.getChildCount();
        }
        return 0;
    }


    @Override
    public int getIndexOfChild(Object arg0, Object arg1)
    {
        return 0;
    }


    /**
     * gibt an, ob es sich bei dem uebergebenen Knoten um ein Blatt des
     * Baumes handelt
     *
     * @param node Knoten, der geprueft werden soll
     * @return Blatt, falls true
     */
    @Override
    public boolean isLeaf(Object node)
    {
        return getChildCount(node) == 0;
    }
}