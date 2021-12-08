package pl.fhframework.dp.transport.msg;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 30/11/2021
 */
public interface IAcceptMessage {
    AcceptMessageResult acceptMessage(AcceptMessageRequest request);
}
