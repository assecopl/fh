/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.fhframework.dp.transport.dto.commons;


import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.searchtemplate.SearchTemplateForQuery;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author ZbiTet
 */
@Getter @Setter
public class BaseDtoQuery implements Serializable{

    private String textSearch;
    private boolean wholeWordsOnly = false;
    
    //--atrybuty stronicowania
    private Integer firstRow = 0;
    private Integer size = 200;
    //--atrybuty sortowania
    private String  sortProperty;
    private Boolean ascending;

    //Obiekt do konstrukcji zapytania na podstawie szablonu
    private List<SearchTemplateForQuery> searchTemplateForQuery;

}
