package pl.fhframework.dp.commons.fh.print;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.fhframework.dp.transport.drs.repository.*;
import pl.fhframework.dp.transport.prints.PrintRequestType;
import pl.fhframework.dp.transport.prints.PrintResponseType;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 27.12.2019
 */
@Service
@Slf4j
public class PrintRestClient {

	@Value("${print.rest.url:}")
	private String url;


	public PrintResponseType print(PrintRequestType request) {
		String uri = UriComponentsBuilder
				.fromUriString(url)
				.pathSegment("prints", "print")
				.encode()
				.toUriString();
		ResponseEntity<PrintResponseType> ret = PrintRestTemplateConfig.
				restTemplate.postForEntity(uri, request, PrintResponseType.class);
		return ret.getBody();
	}

}
