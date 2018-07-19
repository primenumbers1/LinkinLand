package primenumbers.main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class HelpPanel extends JDialog implements ActionListener {

	public static final String SCENE_EDITOR_HELP_TEXT="<html><body>" +
			
		    
	        
		       
	        "<p>Create floor using the menu Other-> Create box </p>" +
	        "<p>Select cells with left mouse button, select objects and walls" +
	        " with right mouse button</p>"+
	        "<p>You can also mouse drag to select floor cells.</p>" +
	        "<br>View keys:<br>" +
	        "<ul>"+
	        "<li>f1: zoom in</li>"+
	        "<li>f2: zoom out</li>"+
	        "<li>q rotate +</li>"+
	        "<li>w rotate-</li>"+
	        "</ul>"+ 
	   
		"</body></html>";
	
	

	

	private JButton exit=null;
	private JPanel bottom=null;
	private JEditorPane center;
	
	public HelpPanel(int w, int h,int locX,int locY,String text,JFrame owner){
		
		super(owner);
		setTitle("Help");
		setSize(w,h);
		
		center=new JEditorPane("text/html",text);
	
		
		JScrollPane jscp=new JScrollPane(center);
		add(jscp);
		setLocation(locX,locY);
		center.setCaretPosition(0);
		
		bottom=new JPanel();
		
		exit=new JButton("Exit");
		exit.addActionListener(this);
		bottom.add(exit);
		
		add(BorderLayout.SOUTH,bottom);
		
		setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object o = arg0.getSource();
		
		
		if(o==exit)
			exit();
	}

	
	private void exit(){
		
		dispose();
		
	}
	
}