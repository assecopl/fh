package pl.fhframework.dp.commons.utils.file;
/*
 * FileFilterExtension.java
 *
 * Prawa autorskie do oprogramowania i jego kodów źródłowych
 * przysługują w pełnym zakresie wyłącznie SKG S.A.
 *
 * All copyrights to software and its source code
 * belong fully and exclusively to SKG S.A.
 */

/**
 * 
 * @author Dariusz Skrudlik
 * Implementacja FileFilter dla wyszukiwania plików o podanym rozszerzeniu.
 */
import java.io.File;
import java.io.FileFilter;

public class FileFilterExtension implements FileFilter {

	String fileExtension = "";
	
	/**
	 * 
	 * @param fileExtension - rozszerzenie pliku
	 */
	public FileFilterExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File pathname) {

		if (pathname.isFile()) {
			int beginIndex = pathname.getName().lastIndexOf(".") + 1;
			if (this.fileExtension.equalsIgnoreCase( pathname.getName().substring(beginIndex) )) {
				return true;
			}
		}
		return false;
	}

}
