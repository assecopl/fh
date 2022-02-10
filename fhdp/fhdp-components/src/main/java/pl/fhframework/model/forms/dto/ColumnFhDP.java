package pl.fhframework.model.forms.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Collection;


public class ColumnFhDP {
    @Getter
    @Setter
    private String field;

    @Getter
    @Setter
    private String title;

    @Setter
    @Getter
    private boolean sortable;

    @Getter
    @Setter
    private String align;

    @Getter
    @Setter
    /**
     * If in column should be select element columnWithSelect = true
     */
    private boolean columnWithSelect;

    @Getter
    @Setter
    private boolean checkbox;

    @Getter
    @Setter
    private Collection<OptionFhDP> selectOptions;

    @Getter
    @Setter
    private Collection<Integer> selectApplicableTo;

    public ColumnFhDP(String field, String title){
        this.field = field;
        this.title = title;

    }
    public ColumnFhDP(String field, String title, boolean sortable){
        this.field = field;
        this.title = title;
        this.sortable = sortable;
    }


    public ColumnFhDP(String field, String title, boolean sortable, String align){
        this.field = field;
        this.title = title;
        this.sortable = sortable;
        this.align = align;
    }

    public ColumnFhDP(String field, String title, boolean columnWithSelect, Collection<OptionFhDP> selectOptions){
        this.field = field;
        this.title = title;
        this.columnWithSelect = columnWithSelect;
        this.selectOptions = selectOptions;
    }

    public ColumnFhDP(String field, String title, boolean columnWithSelect, Collection<OptionFhDP> selectOptions, Collection<Integer> selectApplicableTo){
        this.field = field;
        this.title = title;
        this.columnWithSelect = columnWithSelect;
        this.selectOptions = selectOptions;
        this.selectApplicableTo = selectApplicableTo;
    }

    public ColumnFhDP(String field, String title, boolean sortable, String align, boolean columnWithSelect, Collection<OptionFhDP> selectOptions, Collection<Integer> selectApplicableTo, boolean checkbox){
        this.field = field;
        this.title = title;
        this.sortable = sortable;
        this.align = align;
        this.columnWithSelect = columnWithSelect;
        this.selectOptions = selectOptions;
        this.selectApplicableTo = selectApplicableTo;
        this.checkbox = checkbox;
    }
}
