/*
 * JAPIS 2020.
 */
package pl.fhframework.dp.commons.prints.extensions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.repo.DefaultRepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RepositoryService that reads resources from classpath.
 *
 * @author <a href="mailto:pawel.kasprzak@asseco.pl">Paweł Kasprzak</a>
 * @version $Revision: 3734 $, $Date: 2020-12-08 11:29:57 +0100 (wto) $
 */
public class ClasspathRepositoryService extends DefaultRepositoryService {

    private final Logger logger = LoggerFactory.getLogger(ClasspathRepositoryService.class);
    private static final String CLASSPATH_PREFIX = "classpath:";
    private static final int CLASSPATH_PREFIX_LENGTH = CLASSPATH_PREFIX.length();

    public ClasspathRepositoryService(JasperReportsContext jasperReportsContext) {
        super(jasperReportsContext);
        setURLStreamHandlerFactory(proto -> {
            return "classpath".equals(proto) ? new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL url) throws IOException {
                    return new URLConnection(url) {

                        @Override
                        public void connect() throws IOException {
                            // noop
                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            try {
                                return this.getClass().getResourceAsStream(url.toURI().toString().trim().substring(CLASSPATH_PREFIX_LENGTH));
                            } catch (URISyntaxException ex) {
                                throw new IOException("Invalid URI syntax !", ex);
                            }
                        }
                    };
                }
            } : null;
        });
    }
}
