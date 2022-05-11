package pl.fhframework.dp.commons.utils.file;

import java.io.File;

public class DirectoryHelper {

    /**
     * Checks, if given argument is a folder. If not, creates it.
     */
    public static final void checkDirectory(File wdir) {
        if (!(wdir != null && ((wdir.exists() && wdir.isDirectory()) || wdir.mkdirs()))) {

            if (wdir != null) {
                throw new RuntimeException("Bad folder: " + wdir.getAbsolutePath());
            } else {
                throw new RuntimeException("Bad folder: <null>");
            }
        } //ok
    }

    public static void checkDirectory(String dir) {
        checkDirectory(new File(dir));
    }

}
