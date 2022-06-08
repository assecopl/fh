/*
 * JAPIS 2020.
 */
package pl.fhframework.dp.commons.prints;

/**
 * Constants
 *
 * @author <a href="mailto:pawel.kasprzak@asseco.pl">Paweł Kasprzak</a>
 * @version $Revision: 3734 $, $Date: 2020-12-08 11:29:57 +0100 (wto) $
 */
public interface Constants {

    String PREFIX = "japis.printserver";
    String PDF_PREFIX = PREFIX + ".pdf";
    String PDF_METADATA_PREFIX = PDF_PREFIX + ".metadata";
    String PDF_METADATA_CREATOR = PDF_METADATA_PREFIX + ".creator";
    String PDF_METADATA_TITLE = PDF_METADATA_PREFIX + ".title";
    String PDF_METADATA_AUTHOR = PDF_METADATA_PREFIX + ".author";
    String PDF_METADATA_SUBJECT = PDF_METADATA_PREFIX + ".subject";
    String PDF_TAG = PDF_PREFIX + ".tag";
    String PDF_TAG_LANGUAGE = PDF_TAG + ".language";
    String PDF_CONFORMANCE = PDF_PREFIX + ".conformance";
}
