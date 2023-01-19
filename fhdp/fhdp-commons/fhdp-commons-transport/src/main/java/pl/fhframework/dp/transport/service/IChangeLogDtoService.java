/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.fhframework.dp.transport.service;

import pl.fhframework.dp.transport.changelog.ChangeLogDto;
import pl.fhframework.dp.transport.changelog.ChangeLogDtoQuery;

/**
 *
 * @author jacek.borowiec@asseco.pl
 */
public interface IChangeLogDtoService extends IDtoService <String, ChangeLogDto, ChangeLogDto, ChangeLogDtoQuery>{
    
}
