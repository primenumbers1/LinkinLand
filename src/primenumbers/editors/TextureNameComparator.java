package primenumbers.editors;

import java.io.File;
import java.util.Comparator;

public class TextureNameComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
	
		String f1=((File) o1).getName();
		String f2=((File) o2).getName();
		
		String sIndex1=f1.substring(f1.lastIndexOf("_")+1,f1.lastIndexOf("."));
		String sIndex2=f2.substring(f2.lastIndexOf("_")+1,f2.lastIndexOf("."));
		
		int index1=Integer.parseInt(sIndex1);
		int index2=Integer.parseInt(sIndex2);
		
		return index1<index2?-1:1;
	} 
	
}