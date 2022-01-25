/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.fhframework.dp.transport.service;

import pl.fhframework.dp.transport.auditlog.AuditLogDto;
import pl.fhframework.dp.transport.auditlog.AuditLogDtoQuery;

/**
 *
 * @author ZbiTet
 */
public interface IAuditLogDtoService extends IDtoService <String, AuditLogDto, AuditLogDto, AuditLogDtoQuery>{
    
}
