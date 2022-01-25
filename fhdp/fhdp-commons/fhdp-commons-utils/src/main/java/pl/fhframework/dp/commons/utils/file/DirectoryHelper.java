/*
 * DirectoryHelper.java
 *
 * Prawa autorskie do oprogramowania i jego kodów źródłowych
 * przysługują w pełnym zakresie wyłącznie SKG S.A.
 *
 * All copyrights to software and its source code
 * belong fully and exclusively to SKG S.A.
 */

package pl.fhframework.dp.commons.utils.file;

import java.io.File;

/**
 * @author Dariusz Skrudlik
 */
public class DirectoryHelper {

    /**
     * Checks, if given argument is a folder. If not, creates it.
     */
    public static final void checkDirectory(File wdir) {
        if (!(wdir != null && ((wdir.exists() && wdir.isDirectory()) || wdir.mkdirs()))) {

            if (wdir != null) {
                throw new RuntimeException("Błędny katalog: " + wdir.getAbsolutePath());
            } else {
                throw new RuntimeException("Błędny katalog: <null>");
            }
        } //ok
    }

    public static void checkDirectory(String dir) {
        checkDirectory(new File(dir));
    }

}
