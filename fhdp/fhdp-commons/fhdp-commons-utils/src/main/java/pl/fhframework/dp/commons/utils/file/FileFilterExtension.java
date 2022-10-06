package pl.fhframework.dp.commons.utils.file;


import java.io.File;
import java.io.FileFilter;

public class FileFilterExtension implements FileFilter {

	String fileExtension = "";
	

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
