package primenumbers.editors;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class IntegerTextField extends JTextField {

	public IntegerTextField() {
		super();
	}
@Override
protected Document createDefaultModel() {
	return new IntegerDocument();
}

private static class IntegerDocument extends PlainDocument {
	@Override
	public void insertString(int offs, String str, AttributeSet a) 
	throws BadLocationException {
		
		if (str == null) {
			return;
		}
		char[] upper = str.toCharArray();
		boolean isValid=true;
		
		for (int i = 0; i < upper.length; i++) {
			
			if(!Character.isDigit(upper[i]) && upper[i]!='-' )
			{
				isValid=false;
				break;
			}
		
		}
		if(isValid)
			super.insertString(offs, new String(upper), a);
	}
}

public void setText(int n){
	
	setText(""+n);
}

public int getvalue(){
	
	String str=getText();
	if(str==null || str.length()==0)
		return 0;
	
	return Integer.parseInt(str);
	
}

}