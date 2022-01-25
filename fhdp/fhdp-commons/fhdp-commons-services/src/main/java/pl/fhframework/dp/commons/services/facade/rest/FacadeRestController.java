package pl.fhframework.dp.commons.services.facade.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.fhframework.dp.commons.base.model.Heartbeat;
import pl.fhframework.dp.commons.rest.*;
import pl.fhframework.dp.commons.rest.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 2019-07-25
 */
@Controller
@RequestMapping(value = "/restFacade")
public class FacadeRestController {

    final IFacadeService facadeServiceCtl;

    @Value("${build.branch}")
    private String version;

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    public FacadeRestController(IFacadeService facadeServiceCtl) {
        this.facadeServiceCtl = facadeServiceCtl;
    }

    @GetMapping("/heartbeat")
    @ResponseBody
    public Heartbeat heartbeat(@RequestParam(name="name", required=false, defaultValue="Stranger") String name) {
        return new Heartbeat(counter.incrementAndGet(), String.format(template, name), version);
    }

    @PostMapping(path = "/list", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public EntityRestResponse list( @RequestBody EntityRestRequest request) {
        return facadeServiceCtl.list(request);
    }
    @PostMapping(path = "/count", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public EntityRestResponse count(@RequestBody EntityRestRequest request) {
        return facadeServiceCtl.count(request);
    }

    @PostMapping(path = "/getDto", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public EntityRestResponse getDto( @RequestBody EntityRestRequest request) {
        return facadeServiceCtl.getDto(request);
    }

    @PostMapping(path = "/deleteDto", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public EntityRestResponse deleteDto( @RequestBody EntityRestRequest request) {
        return facadeServiceCtl.deleteDto(request);
    }

    @PostMapping(path = "/persistDto", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public EntityRestResponse persistDto(@RequestBody EntityRestRequest request) {
        return facadeServiceCtl.persistDto(request);
    }

    @PostMapping(path = "/listCodeList", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public EntityRestResponse listCodeList(@RequestBody CodelistRestRequest request) {
        return facadeServiceCtl.listCodeList(request);
    }

    @PostMapping(path = "/countCodeList", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public EntityRestResponse countCodeList(@RequestBody CodelistRestRequest request) {
        return facadeServiceCtl.countCodeList(request);
    }

    @PostMapping(path = "/getCode", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public EntityRestResponse getCode(@RequestBody CodelistRestRequest request) {
        return facadeServiceCtl.getCode(request);
    }

    @PostMapping(path = "/performOperation", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public OperationRestResponse performOperation(@RequestBody OperationRestRequest request) {
        return facadeServiceCtl.performOperation(request);
    }

    @PostMapping(path = "/getOperationState", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public OperationStateRestResponse getOperationState(@RequestBody OperationStateRestRequest request) {
        return facadeServiceCtl.getOperationState(request);
    }

    @PostMapping(path = "/getOperationData", consumes = "application/json", produces = "application/json")
    @ResponseBody()
    public OperationDataRestResponse getOperationData(@RequestBody OperationDataRestRequest request) {
        return facadeServiceCtl.getOperationData(request);
    }

    @PostMapping(path = "/print", consumes = "application/json", produces = "application/pdf")
    @ResponseBody()
    public ResponseEntity<InputStreamResource> printDocument(@RequestBody PrintRequest printRequest) throws IOException {

        byte[] bytes = null;
        //TODO:connect to prints service
//        bytes = printClientService.print(printRequest.getDocumentType(), printRequest.getTemplateCode(), printRequest.getContentXml());
        Resource resource = new ByteArrayResource(bytes);

        final InputStream inp = resource.getInputStream();


        long contentLength = 0L;
        return ResponseEntity.ok()
                .header("content-disposition","attachment; filename = " + printRequest.getDocumentType() + ".pdf")
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .contentLength(contentLength)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(inp));

    }
}
