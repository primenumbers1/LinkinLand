package primenumbers.editors;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class DoubleTextField extends JTextField {

	public DoubleTextField(int cols) {
		super(cols);
	}

	public DoubleTextField() {
		super();
	}
	@Override
	protected Document createDefaultModel() {
		return new IntegerDocument();
	}

	private class IntegerDocument extends PlainDocument {
		@Override
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {

			if (str == null) {
				return;
			}
			char[] upper = str.toCharArray();
			boolean isValid=true;

			for (int i = 0; i < upper.length; i++) {

				if(!Character.isDigit(upper[i]) && upper[i]!='-' && upper[i]!='.')
				{
					isValid=false;
					break;
				}

			}
			if(isValid) {
				super.insertString(offs, new String(upper), a);
			}
		}
	}

	public void setText(double d){

		setText(""+d);
	}

	public double getvalue(){

		String str=getText();
		if(str==null || str.length()==0) {
			return 0;
		}

		return Double.parseDouble(str);

	}
	
}