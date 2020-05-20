package hilfe.guiHilfe;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for filtered combo box.
 * Stammt von stackoverflow.com, Post von display_name
 * @see <a href="https://stackoverflow.com/questions/27753375/jcombobox-search-list">JComboBox Search box</a>
 */
public class FilterComboBox extends JComboBox<String>
{
 	private static final long serialVersionUID = 1L;
	/**
     * Entries to the combobox list.
     */
    private List<String> entries;
    
    public List<String> getEntries()
    {
        return entries;
    }

    final JTextField textfield =
        (JTextField) this.getEditor().getEditorComponent();

    public JTextField getTextfield() 
    {
        return textfield;
    }

    public FilterComboBox(List<String> entries)
    {
        super();
        this.entries = entries ;
        this.setEditable(true);
        if(entries != null)
        {	
            for(int i = 0; i < entries.size(); i++)
            {
            	this.addItem(entries.get(i));
            }
        }   		

        /**
         * Listen for key presses.
         */
        textfield.addKeyListener(new KeyAdapter()
        {
            public void keyReleased(KeyEvent ke)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        /**
                         * On key press filter the list.
                         * If there is "text" entered in text field of this combo use that 
                         * "text" for filtering.
                         */
                        comboFilter(textfield.getText());
                    }
                });
            }
        });

    }

    /**
     * Build a list of entries that match the given filter.
     */
    public void comboFilter(String enteredText)
    {
        List<String> entriesFiltered = new ArrayList<String>();

        for (String entry : getEntries())
        {
            if (entry.toLowerCase().contains(enteredText.toLowerCase()))
            {
                entriesFiltered.add(entry);
            }
        }

        if (entriesFiltered.size() > 0)
        {
        	DefaultComboBoxModel<String> dcm= new DefaultComboBoxModel<String>();
        	for(int i = 0; i < entriesFiltered.size(); i++)
        	{
        		dcm.addElement(entriesFiltered.get(i));
        	}
            this.setModel(dcm);
            this.setSelectedItem(enteredText);
            this.showPopup();
        }
        else
        {
            this.hidePopup();
        }
    }
}