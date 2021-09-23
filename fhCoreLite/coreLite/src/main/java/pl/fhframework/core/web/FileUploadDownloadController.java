package pl.fhframework.core.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.BindingResult;
import pl.fhframework.SessionManager;
import pl.fhframework.UserSession;
import pl.fhframework.io.FileService;
import pl.fhframework.io.IFileMaxSized;
import pl.fhframework.io.IResourced;
import pl.fhframework.io.TemporaryResource;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.FormElement;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * File upload and download web controller.
 */
@Controller
public class FileUploadDownloadController {

    public static final String FILE_UPLOAD_URL = "/fileUpload";

    public static final String FILE_DOWNLOAD_URL = "/fileDownload";

    @Autowired
    private FileService fileService;

    @RequestMapping(value = FILE_UPLOAD_URL, method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<Map<?, ?>> fileUpload(@RequestParam("file") MultipartFile[] file,
                                         @RequestParam("formId") String formId,
                                         @RequestParam("componentId") String componentId) throws IOException {
        UserSession userSession = SessionManager.getUserSession();
        if (userSession == null) {
            FhLogger.error("Must be an authenticated user");
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        final Optional<Form<?>> formOpt = userSession.getUseCaseContainer().getFormsContainer().findActiveFormById(formId);
        if (!formOpt.isPresent()) {
            FhLogger.error("No active currently presented form.");
            return new ResponseEntity<>(null, HttpStatus.PRECONDITION_FAILED);
        } else {
            final FormElement formElement = formOpt.get().getFormElement(componentId);
            if (ClassUtils.isAssignableValue(IFileMaxSized.class, formElement)) {
                final IFileMaxSized fileUploadFormComponent = (IFileMaxSized) formElement;

                for (MultipartFile file_ : file) {
                    final long requestFileMaxSize = file_.getSize();
                    final long formComponentMaxSize = fileUploadFormComponent.getMaxSize();
                    if (requestFileMaxSize > formComponentMaxSize) {
                        FhLogger.error("Uploaded file is too big. Current max for requested component is: " + formComponentMaxSize + ", but sent: " + requestFileMaxSize);
                        return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                    }
                    final String[] fileNameSplit = file_.getOriginalFilename().split("\\.");
                    final String extensionsFromFormElement = fileUploadFormComponent.getExtensions();
                    if (StringUtils.hasText(extensionsFromFormElement) && fileNameSplit.length == 1) {
                        FhLogger.error("Incorrect file extensions. Current component extension is: '" + extensionsFromFormElement + "', but sent empty.");
                        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                    }
                    if (StringUtils.hasText(extensionsFromFormElement)) {
                        final String fileExtension = fileNameSplit[fileNameSplit.length - 1];
                        final String[] extensions = extensionsFromFormElement.replace(".", "").split(",");
                        if (Arrays.stream(extensions).noneMatch(c -> c.equalsIgnoreCase(fileExtension))) {
                            FhLogger.error("Incorrect file extensions. Current component extension is: '" + extensionsFromFormElement + "', but sent '" + fileExtension + "'.");
                            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                        }
                    }
                }
            } else {
                FhLogger.error("Requested component id is not instanceof " + IFileMaxSized.class.getName());
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
        }
        try {
            Map<String, List<String>> map = new HashMap<>();

            List<String> fileIds = new ArrayList<>();
            for (MultipartFile file_ : file) {
                fileIds.add(fileService.save(file_, userSession));
            }
            map.put("ids", fileIds);
            return new ResponseEntity<>(map, HttpStatus.CREATED);
        } catch (IOException exception) {
            FhLogger.error(exception);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = FILE_DOWNLOAD_URL + "/formElement", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void fileDownloadForFormElementId(@RequestParam String id, @RequestParam("formId") String formId, HttpServletResponse response) throws IOException {
        UserSession userSession = SessionManager.getUserSession();
        if (userSession == null) {
            sendError(response, HttpStatus.UNAUTHORIZED, "Must be an authenticated user");
            return;
        }
        final Optional<Form<?>> formOpt = userSession.getUseCaseContainer().getFormsContainer().findActiveFormById(formId);
        if (!formOpt.isPresent()) {
            FhLogger.error("No active currently presented form.");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } else {
            final FormElement formElement = formOpt.get().getFormElement(id);
            if (ClassUtils.isAssignableValue(IResourced.class, formElement)) {
                final IResourced resourced = (IResourced) formElement;
                final Resource resource = resourced.getResource();
                streamResource(resource, response);
            } else {
                FhLogger.error("Requested component id is not instanceof " + IFileMaxSized.class.getName());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
            }
        }
    }

    @RequestMapping(value = FILE_DOWNLOAD_URL + "/binding", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void fileDownloadForBinding(@RequestParam String binding, @RequestParam String formId, HttpServletResponse response) throws IOException {
        UserSession userSession = SessionManager.getUserSession();
        if (userSession == null) {
            sendError(response, HttpStatus.UNAUTHORIZED, "Must be an authenticated user");
            return;
        }
        final Optional<Form<?>> formOpt = userSession.getUseCaseContainer().getFormsContainer().findActiveFormById(formId);
        if (!formOpt.isPresent()) {
            FhLogger.error("No active currently presented form.");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } else {
            final BindingResult bindingResult = formOpt.get().getBindingResult(binding, formOpt.get());
            if (bindingResult != null && ClassUtils.isAssignableValue(Resource.class, bindingResult.getValue())) {
                streamResource((Resource) bindingResult.getValue(), response);
            } else {
                FhLogger.error("Requested component id is not instanceof " + IFileMaxSized.class.getName());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
            }
        }
    }

    @RequestMapping(value = FILE_DOWNLOAD_URL + "/resource/{resourceId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void fileDownloadForResource(@PathVariable String resourceId, HttpServletResponse response) throws IOException {
        UserSession userSession = SessionManager.getUserSession();
        if (userSession == null) {
            sendError(response, HttpStatus.UNAUTHORIZED, "Must be an authenticated user");
            return;
        }
        streamResource(userSession.getDownloadFileIndexes().get(resourceId), response);
    }

    private void streamResource(Resource resource, HttpServletResponse response) throws IOException {
        if (resource == null) {
            FhLogger.warn("File is not bound.");
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        try (InputStream resourceIs = resource.getInputStream()) {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"; filename*=UTF-8''%s",
                    pl.fhframework.core.util.StringUtils.latinize(resource.getFilename()), UriUtils.encode(resource.getFilename(), "UTF-8")));
            calcContentLength(resource, response);
            if (resource instanceof TemporaryResource && !StringUtils.isEmpty(((TemporaryResource) resource).getContentType())) {
                response.setContentType(((TemporaryResource) resource).getContentType());
            }
            else {
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM.getType());
            }

            int readBytes;
            byte[] bytes = new byte[256 * 1024]; // 256kB buffer
            OutputStream responseOs = response.getOutputStream();

            while ((readBytes = resourceIs.read(bytes)) != -1) {
                responseOs.write(bytes, 0, readBytes);
                responseOs.flush();
            }
            responseOs.close();
        }
    }


    private void calcContentLength(Resource resource, HttpServletResponse response) {
        try {
            File file = resource.getFile();
            if (file.exists() && file.isFile()) {
                response.setContentLength((int) file.length());
            }
        } catch (IOException e) {
            // do not set content length
        }
    }

    private void sendError(HttpServletResponse response, HttpStatus status, String errorMessage) throws IOException {
        FhLogger.error(errorMessage);
        response.sendError(status.value(), errorMessage);
    }
}
