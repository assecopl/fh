package pl.fhframework.dp.transport.msg;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.drs.repository.Correspondent;
import pl.fhframework.dp.transport.drs.repository.OtherMetadata;
import pl.fhframework.dp.transport.dto.commons.BaseDtoQuery;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MessageDtoQuery extends BaseDtoQuery {
    private Long declarationId;
    private String repositoryId;
    private String name;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private LocalDateTime storedFrom;
    private LocalDateTime storedTo;
    private LocalDateTime deliveredFrom;
    private LocalDateTime deliveredTo;
    private String localReferenceNumber;
    private String customsReferenceNumber;
    private String messageIdentification;
    private String contentType;
    protected List<OtherMetadata> otherMetadata = new ArrayList<>();
    protected Correspondent sender = new Correspondent();
    protected Correspondent recipient = new Correspondent();
    protected ArrayList<String> senderNames = new ArrayList<>();
    protected ArrayList<String> recipientNames = new ArrayList<>();
    protected String responseTo;
    protected String attachmentTo;
    private String lrnOrMessageId;
    private boolean showAllMessages = false;
    private boolean isResultHidden;
    private Long groupDeclarationId;
    private String groupRepositoryId;
    private boolean isReplaceSpanNear = false;

    public MessageDtoQuery() {
        setSortProperty("stored");
    }
}
