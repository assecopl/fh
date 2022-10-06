package pl.fhframework.dp.transport.service;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 17/12/2021
 */
public interface IMaxIdServiceLong {
    Long getMaxId(String key) throws InterruptedException;
}
