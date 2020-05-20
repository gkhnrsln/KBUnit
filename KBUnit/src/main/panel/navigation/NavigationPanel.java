/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.panel.navigation;

import main.dialogfenster.ProgressDialog;
import main.hauptfenster.BasicFrameView;
import main.hauptfenster.testCaseInfo.TestCaseInfo;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;

/**
 * Klasse, welche das Panel fuer eine Navigation zur Verfuegung stellt.
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */
public class NavigationPanel extends JPanel{
    
  	private static final long serialVersionUID = 1L;
  	
  	// ScrollBars fuer die Navigation
	private JScrollPane scpNavigation;
	// Navigationsbaum
    private JXTreeTable trNavigation;
    // Basisknoten des Navigationsbaums
    private DefaultMutableTreeNode trRoot;
    // Daten zum Navigationsbaum
    private TreeModel trModel;
    // Renderer zum Navigationsbaum
    private TreeRenderer trRenderer;
    
    // Fenster, welches das Panel als Element hat
    private BasicFrameView parent;
    
    /**
     * erstellt das Navigationspanel fuer das Hauptfenster
     * @param parent , enthaelt die Adresse des Hauptfenster-Views
     */
    public NavigationPanel(final BasicFrameView parent)
    {        
        this.parent = parent;
        this.initComponents();
        this.initListener();
    }    
    
    // erstellt die Komponenten des Navigationspanels
    private void initComponents()
    {        
        this.trRoot = new DefaultMutableTreeNode("CF");
     
        this.trModel = new TreeModel(this.trRoot);
        this.trNavigation = new JXTreeTable(this.trModel);
        this.trRenderer = new TreeRenderer();  
        this.trNavigation.setTreeCellRenderer(this.trRenderer);
        this.trNavigation.setDefaultRenderer(Object.class, 
        	new TreeTableCellRenderer(this.trRoot));
        this.trNavigation.setCellSelectionEnabled(false);
        this.trNavigation.setRowSelectionAllowed(true);
        this.trNavigation.setRowHeight(25);
        this.trNavigation.getTableHeader().setPreferredSize(
            new Dimension(this.trNavigation.getTableHeader().getWidth(),25));
        this.trNavigation.getTableHeader().setReorderingAllowed(false);
        this.trNavigation.setShowVerticalLines(true);
        
        UIDefaults trui = new UIDefaults();
        this.trNavigation.putClientProperty("Nimbus.Overrides.InheritDefaults",false);
        this.trNavigation.putClientProperty("Nimbus.Overrides",trui);
        this.trNavigation.setShowsRootHandles(false);
        this.trNavigation.setTableHeader(null);
    }
    
    // erstellt die Listener des Navigationspanels
    private void initListener()
    {
        this.trNavigation.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e)
            {
                super.mouseMoved(e);
                trNavigation.setEnabled(false);
            }
            @Override
            public void mouseMoved(MouseEvent e)
            {
                super.mouseMoved(e);
                trNavigation.setEnabled(true);
            }
        });

        this.trNavigation.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mousePressed(e);
                if (e.getButton() == 1)
                {
                    TreePath tp 
                        = trNavigation.getPathForLocation(e.getX(), e.getY());

                    if (tp != null)
                    {
                        DefaultMutableTreeNode dmtn 
                            = (DefaultMutableTreeNode) tp.getLastPathComponent();
                        int clickedRowNumber = trNavigation.getRowForPath(tp);

                        if (e.isControlDown())
                        {
                            DefaultMutableTreeNode father 
                                = (DefaultMutableTreeNode) dmtn.getParent();
                            TreePath tpFather =  new TreePath(father.getPath());
                            int fatherRowNumber = trNavigation.getRowForPath(tpFather);

                            ListSelectionModel selectionModel 
                                = trNavigation.getSelectionModel();
                            if(selectionModel.isSelectedIndex(fatherRowNumber))
                            {
                                selectionModel
                                    .addSelectionInterval(clickedRowNumber, clickedRowNumber);
                            }
                            else
                            {
                                boolean hasToBeSelected 
                                    = selectionModel.isSelectedIndex(clickedRowNumber);
                                Enumeration<?> enumeration = dmtn.breadthFirstEnumeration();
                                while(enumeration.hasMoreElements())
                                {
                                    DefaultMutableTreeNode dmtnNeu 
                                        = (DefaultMutableTreeNode) enumeration.nextElement();
                                    TreePath _tp = new TreePath(dmtnNeu.getPath());
                                    int rowNumber = trNavigation.getRowForPath(_tp);
                                    if (rowNumber >= 0)
                                    {
                                        if (hasToBeSelected)
                                        {
                                            selectionModel
                                                .addSelectionInterval(rowNumber, rowNumber);
                                        }
                                        else
                                        {
                                            selectionModel
                                                .removeSelectionInterval(rowNumber, rowNumber);
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            Enumeration<?> enumeration = dmtn.breadthFirstEnumeration();
                            while(enumeration.hasMoreElements())
                            {
                                DefaultMutableTreeNode dmtnNeu 
                                    = (DefaultMutableTreeNode) enumeration.nextElement();
                                TreePath tpNeu = new TreePath(dmtnNeu.getPath());
                                int rowNumber = trNavigation.getRowForPath(tpNeu);
                                if (rowNumber >= 0)
                                {
                                    ListSelectionModel selectionModel 
                                        = trNavigation.getSelectionModel();
                                    selectionModel.addSelectionInterval(rowNumber, rowNumber);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                if (e.getButton() == 1 && e.getClickCount() == 2)
                {
                   TreePath tp = trNavigation.getPathForLocation(e.getX(), e.getY());

                   if (tp != null)
                   {
                       DefaultMutableTreeNode fmtn 
                           = (DefaultMutableTreeNode) tp.getLastPathComponent();
                       if (fmtn.getUserObject().getClass().getSuperclass() == TestCaseInfo.class)
                       {
                           parent.openTestCaseInDetail((TestCaseInfo)fmtn.getUserObject());
                       }
                       else
                       {
                           if (trNavigation.isExpanded(tp))
                           {
                               trNavigation.collapsePath(tp);
                           }
                           else
                           {
                               trNavigation.expandPath(tp);
                           }
                       }
                   }
                   mousePressed(e);
                }
               
                if (e.getButton() == 3)
                {
                    TreePath tp = trNavigation.getPathForLocation(e.getX(), e.getY());
                    if (tp != null)
                    {
                        int mouseOverRow = trNavigation.getRowForPath(tp);
                        if (!trNavigation.isRowSelected(mouseOverRow))
                        {
                            ListSelectionModel selectionModel = trNavigation.getSelectionModel();
                            selectionModel.setSelectionInterval(mouseOverRow, mouseOverRow);
                        }

                        // alle markierten an popup uebergeben
                        int[] selectedRows = trNavigation.getSelectedRows();
                        // statt der ueblichen ArrayList wird zur Speicherung der 
                        // im Baum gefundenen Tests ein 
                        // HashSet verwendet, sodass doppelte Eintragungen von vornherein 
                        // vermieden werden
                        HashSet<TestCaseInfo> tciset = new HashSet<>();
                        for (int i=0; i<selectedRows.length; i++)
                        {
                            TreePath tpNeu = trNavigation.getPathForRow(selectedRows[i]);
                            DefaultMutableTreeNode dmtn 
                                = (DefaultMutableTreeNode) tpNeu.getLastPathComponent();

                            if (dmtn.isLeaf())
                            {
                                // falls es sich um ein Blatt handelt, kann der enthaltene 
                            	// Testfall direkt der Testliste hinzugefuegt werden
                                tciset.add((TestCaseInfo) dmtn.getUserObject());
                            }
                            else
                            {
                                // falls es sich um einen Wurzelknoten handelt, muessen
                            	// alle Kinder durchsucht werden, 
                                // gefundene leafs sind der Testliste hinzuzufuegen
                                Enumeration<?> enumeration = dmtn.depthFirstEnumeration();
                                while(enumeration.hasMoreElements())
                                {
                                    DefaultMutableTreeNode dmtnNeu 
                                        = (DefaultMutableTreeNode) enumeration.nextElement();

                                    if (dmtnNeu.isLeaf())
                                    {
                                        tciset.add((TestCaseInfo) dmtnNeu.getUserObject());
                                    }
                     
                                    // alle sichtbaren Kinder eines Knotens sollen in der 
                                    // Anzeige auch selektiert werden, 
                                    // damit der Nutzer weiss, was er bei Auswahl eines 
                                    // Ordners gewaehlt hat
                                    TreePath tpNeuNeu = new TreePath(dmtnNeu.getPath());
                                    int rowNumber = trNavigation.getRowForPath(tpNeuNeu);
                                    if (rowNumber >= 0)
                                    {
                                        ListSelectionModel selectionModel 
                                            = trNavigation.getSelectionModel();
                                        selectionModel.addSelectionInterval(rowNumber, rowNumber);
                                    }
                                }
                            }
                        }

                        // Inhalte aus Hashset in ArrayList uebertragen und diese an 
                        // Popupmenue uebergeben
                        ArrayList<TestCaseInfo> tcilist = new ArrayList<>();
                        tcilist.addAll(tciset);

                        TestCasePopupMenu tcpm = new TestCasePopupMenu(parent, tcilist);
                        tcpm.show(trNavigation, e.getX(), e.getY());
                    }
                }
            }
        });
    }
    
   /**
    * setzt fuer den Navigationsbaum ein ScrollPane
    * @param gbc GridBagConstraints, welches die Layoutangaben enthaelt
    */
    public void setScollPaneNavigation(GridBagConstraints gbc)
    {  
        this.scpNavigation = new JScrollPane(this.trNavigation);
        this.scpNavigation.setPreferredSize(new Dimension(300,300));
        this.scpNavigation.setBorder(null);
        this.add(this.scpNavigation, gbc);
    }
    
    /**
     * gibt das JXTreeTable - Objekt der Navigation heraus
     * @return JXTreeTable - Objekt der Navigation heraus
     */
    public JXTreeTable getTrNavigation()
    {
        return this.trNavigation;
    }  
    
        
    /**
     * laed die vorgegebene Testfallliste in den Navigationsbaum
     * @param tcilist ArrayList , welche die zu ladenen Testfaelle enthaelt
     * @param rpd ProgressDialog, das Fenster, welches den Ladefortschritt anzeigt
     * @param progress , enthaelt die Anzahl erfolgreicher Testfallkonfigurationen
     */
    public void loadTestCase(ArrayList<TestCaseInfo> tcilist, ProgressDialog rpd,
        int progress)
    {        
        this.trRoot.removeAllChildren();
        TreePath firstPath = null;
        int size = tcilist.size();
        for (int i = 0; i < size; i++) 
        {
            // Baumstruktur aufbauen
            TestCaseInfo tci = tcilist.get(i);
            String path = tci.getPath();
            DefaultMutableTreeNode node = this.trRoot;
            while (path.contains(".")) 
            {
                String subpath = path.substring(0, path.indexOf("."));

                boolean subpathExists = false;
                for (int j = 0; j < node.getChildCount(); j++) 
                {
                    DefaultMutableTreeNode n 
                        = (DefaultMutableTreeNode) node.getChildAt(j);
                    if (n.getUserObject().toString().equals(subpath)) 
                    {
                        node = n;
                        subpathExists = true;
                    }
                }
                if (!subpathExists) 
                {
                    //node.add(new DefaultMutableTreeNode(subpath));
                    node.add(new DefaultMutableTreeNode(subpath));
                    node = (DefaultMutableTreeNode) 
                    	node.getChildAt(node.getChildCount() - 1);
                }
                path = path.substring(path.indexOf(".") + 1);
            }
            DefaultMutableTreeNode dmtn;
            dmtn = new DefaultMutableTreeNode(tcilist.get(i));
            node.add(dmtn);

            if (i == 0) 
            {
                TreePath tp = new TreePath(dmtn.getPath());
                firstPath = tp;
                this.trNavigation.expandPath(tp);
                this.trNavigation.repaint();
            }
            progress++;
            rpd.setNumberOfSuccesses(progress);
        }
        this.trNavigation.setTreeTableModel(new DefaultTreeTableModel());
        this.trNavigation.setTreeTableModel(this.trModel);
        this.trNavigation.expandPath(firstPath.getParentPath());
        TableColumn col = this.trNavigation.getColumnModel().getColumn(1);
        col.setMinWidth(85);
        col.setPreferredWidth(85);
        col.setMaxWidth(85);
        col.setResizable(false);
    }
}
