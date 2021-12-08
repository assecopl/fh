package pl.fhframework.dp.transport.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.dp.transport.drs.repository.Document;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeclarationMessageDto extends MessageDto {
    private Document doc;
    private String messageContent;
}
