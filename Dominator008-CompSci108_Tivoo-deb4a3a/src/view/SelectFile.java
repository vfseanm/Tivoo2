package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


public class SelectFile  extends JFrame {

    JEditorPane pane;
    
    public SelectFile(String url) throws IOException
    {
        pane = new JEditorPane();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pane, BorderLayout.CENTER);
        pane.setEditable(false);
        pane.setPreferredSize(new Dimension(800,600));
        pane.addHyperlinkListener(new LinkFollower());
        pack();
        pane.setPage(url);
        setVisible(true);
    }
    
    
    private class LinkFollower implements HyperlinkListener
    {
        public void hyperlinkUpdate (HyperlinkEvent evt)
        {
            if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            {
                // user clicked a link, load it and show it
                try
                {
                    pane.setPage((evt.getURL().toString()));
                }
                catch (Exception e)
                {
                    String s = evt.getURL().toString();
                    JOptionPane.showMessageDialog(SelectFile.this,
                            "loading problem for " + s + " " + e,
                            "Load Problem", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    public static void main(String[] args) throws IOException {
        JFileChooser fc = new JFileChooser();
        
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            
           SelectFile foo = new SelectFile(file.toURI().toString());
            
        } 
        

       
        

    }

}