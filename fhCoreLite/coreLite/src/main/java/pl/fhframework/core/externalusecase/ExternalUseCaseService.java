package pl.fhframework.core.externalusecase;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fhframework.ExternalUseCaseRegistry;

/**
 * REST service for notification about completion of external use case.
 * @author Tomasz.Kozlowski (created on 2017-11-02)
 */
@RestController
@RequiredArgsConstructor
public class ExternalUseCaseService {

    public static final String EXTERNAL_INVOKE_COMPLETED_PATH = "/externalInvokeCompleted";
    private static final String EXTERNAL_INVOKE_SUCCESS_PATH = EXTERNAL_INVOKE_COMPLETED_PATH + "/success";
    private static final String EXTERNAL_INVOKE_REJECT_PATH = EXTERNAL_INVOKE_COMPLETED_PATH + "/reject";

    private final ExternalUseCaseRegistry registry;

    @PostMapping(value = EXTERNAL_INVOKE_COMPLETED_PATH)
    public ResponseEntity<?> externalInvokeCompleted(@RequestParam String uuid, @RequestParam String status) {
        registry.finishUseCase(uuid, status.contains(HttpStatus.OK.name()));
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = EXTERNAL_INVOKE_SUCCESS_PATH)
    public ResponseEntity<?> externalInvokeSuccess(@RequestParam String uuid) {
        registry.finishUseCase(uuid, true);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = EXTERNAL_INVOKE_REJECT_PATH)
    public ResponseEntity<?> externalInvokeReject(@RequestParam String uuid) {
        registry.finishUseCase(uuid, false);
        return ResponseEntity.ok().build();
    }

}
