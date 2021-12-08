package pl.fhframework.dp.commons.fh.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import pl.fhframework.dp.commons.fh.utils.rest.facade.GenericFacadeService;
import pl.fhframework.dp.transport.msg.DeclarationMessageDto;
import pl.fhframework.dp.transport.msg.DeclarationMessageDtoQuery;
import pl.fhframework.dp.transport.service.IDeclarationMessageService;
import pl.fhframework.core.services.FhService;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.event.dto.NotificationEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@FhService
public class DeclarationMessageService extends GenericFacadeService<String, DeclarationMessageDto, DeclarationMessageDto, DeclarationMessageDtoQuery> {

    public DeclarationMessageService() {
        super(IDeclarationMessageService.class);
    }

    @Override
    public List<DeclarationMessageDto> listDto(DeclarationMessageDtoQuery query) {
        return super.listDto(query);
    }

    public void downloadDeclarationMessageFile(DeclarationMessageDto messageDto, EventRegistry eventRegistry) {
        try {
            Path tempFile = Files.createTempFile(null, ".xml");

            File file = tempFile.toFile();

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(messageDto.getMessageContent());
            bw.close();

            FileSystemResource fileSystemResource = new FileSystemResource(tempFile);

            eventRegistry.fireDownloadEvent(fileSystemResource);

            //todo: usuwanie pliku tymczasowego

        } catch (IOException e) {
            eventRegistry.fireNotificationEvent(NotificationEvent.Level.ERROR, "Wystąpił problem z pobieraniem komunikatu.");
            e.printStackTrace();
        }
    }
}
