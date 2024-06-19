package pl.fhframework.fhdp.example.lookup.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.fhframework.dp.commons.base.exception.AppMsgRuntimeException;

import java.util.HashMap;

@Service
@Slf4j
public class RestClient {

    @Value("${rest.provider.url:https://api.first.org/data/v1}")
    private String url;


    public CountryApiResponse listCountries(String txt) throws AppMsgRuntimeException {
        try {
            String uri = UriComponentsBuilder
                    .fromUriString(url)
                    .pathSegment("countries")
                    .queryParam("q", txt)
                    .queryParam("limit", "1000")
                    .queryParam("pretty", "true")
                    .encode()
                    .toUriString();
            ResponseEntity<CountryApiResponse> ret = RestTemplateConfig.
                    restTemplate.getForEntity(uri, CountryApiResponse.class);
            return ret.getBody();
        } catch (Exception ex) {
            CountryApiResponse ret = new CountryApiResponse();
            ret.setData(new HashMap<>());
            return ret;
        }
    }


}
