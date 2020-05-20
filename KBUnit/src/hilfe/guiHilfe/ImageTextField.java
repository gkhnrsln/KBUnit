package hilfe.guiHilfe;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * ImageTextField ist ein Textfeld, an dessen Ende ein Icon aufgeblendet werden
 * kann, um beispielsweise dem Nutzer sofort falsche Eingaben anzeigen zu koennen
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */

public class ImageTextField extends JTextField
{
  	private static final long serialVersionUID = 1L;

	// definiert das im Bedarfsfall anzuzeigende Icon
    ImageIcon inputErrorIcon 
        = new ImageIcon(Toolkit.getDefaultToolkit().createImage(
        new File("icons\\exclamation.png").getAbsolutePath()));
    
    // gibt an, ob das Icon angezeigt werden soll
    boolean showAlertSymbol = true;

    /**
     * Konstruktor, legt ein neues Eingabefeld an
     *
     * @param text Text, welcher im Eingabefeld erscheinen soll
     */
    public ImageTextField(String text)
    {
        super(text);
        this.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased (KeyEvent e)
            {
                if (ImageTextField.this.getInputVerifier() != null)
                {
                    InputVerifier iv = ImageTextField.this.getInputVerifier();
                    iv.verify(ImageTextField.this);
                }
            }
        });

        EmptyBorder innerBorder = new EmptyBorder(0, 2, 0, 2);
        LineBorder outerBorder  = new LineBorder(SpecialColor.LIGHTGRAY, 1);
        Border border = BorderFactory.createCompoundBorder(outerBorder, innerBorder);
        this.setBorder(border);
    }


    /**
     * legt fest, ob das Icon am Ende des Eingabefeldes angezeigt wird
     *
     * @param showAlertSymbol wenn true, wird das Icon angezeigt
     */
    public void showAlertSymbol(boolean showAlertSymbol)
    {
        this.showAlertSymbol = showAlertSymbol;
    }


    /**
     * zeichnet das Textfeld einschliesslich Umrandung und Icon
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (showAlertSymbol)
        {
            EmptyBorder innerBorder = new EmptyBorder(0, 2, 0, 25);
            LineBorder outerBorder  = new LineBorder(SpecialColor.LIGHTGRAY, 1);
            Border border = BorderFactory.createCompoundBorder(outerBorder, innerBorder);
            this.setBorder(border);
            g.drawImage(this.inputErrorIcon.getImage(), getWidth() - 20, 
                getHeight() / 2 - this.inputErrorIcon.getIconHeight() / 2, this);
        }
        else
        {
            EmptyBorder innerBorder = new EmptyBorder(0, 2, 0, 2);
            LineBorder outerBorder  = new LineBorder(SpecialColor.LIGHTGRAY, 1);
            Border border = BorderFactory.createCompoundBorder(outerBorder, innerBorder);
            this.setBorder(border);
        }
    }
}