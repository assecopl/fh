import {Placement} from '@popperjs/core';
import * as React from 'react';
import * as ReactDOM from 'react-dom';
import {usePopper} from 'react-popper';

interface Props {
    title: string;
    columns: Array<{ title: string, field: string }>;
    rows: any[];
    hookElementId: string;
    isOpen: boolean;
    parent: HTMLElement,
    handleClose: (force?: boolean) => void;
    fireChangePopupEvent: (attr: { name: string, arg: any }[], event?: string) => void;
    recordClick?: (record: any) => void;
    currentPage: number;
    pagesCount: number;
    readOnly: boolean;
    position?: 'left' | 'right';
    backgroundColor?: string;
    translate: any;
    clickInPopup: (arg: boolean) => void;
}

export const DictionaryComboFhDPPopperTable: React.FC<Props> = (props: Props) => {
    const {
        clickInPopup,
        title,
        columns,
        hookElementId,
        isOpen,
        handleClose,
        fireChangePopupEvent,
        recordClick,
        currentPage,
        pagesCount,
        parent,
        readOnly,
        position,
        backgroundColor,
        translate
    } = props;
    let {rows} = props;
    const popperElement = React.useRef(null);
    const [popperElementEx, setPopperElementEx] = React.useState(popperElement.current);
    const [page, setPage] = React.useState(currentPage);
    const [open, setOpen] = React.useState(isOpen);
    const [afterFirstUpdate, setAfterFirstUpdate] = React.useState(false);
    const [totalPages, setTotalPages] = React.useState(pagesCount);
    const [placement, setPlacement] = React.useState(position === 'left' ? 'bottom-start' : position === 'right' ? 'bottom-end' : 'bottom-start');
    const hookElement = document.getElementById(hookElementId);
    const {
        styles,
        attributes,
        forceUpdate
    } = usePopper(hookElement, popperElementEx, {placement: placement as Placement});

    const [actionDebouncing, setActionDecouncing] = React.useState(false);

    const [display, setDisplay] = React.useState({display: 'none',});

    const LIST_OF_BUTTONS = ['.search-icon', '.input-old-value', '#headerMainMenuButton']
    rows = rows.filter(el => el !== null);

    const generateRow = (row: any, columns: Array<{ title: string, field: string }>, key: string | number) => {
        return (
            <tr key={`tr-${key}`} onMouseEnter={rowMouseEnter} onMouseLeave={rowMouseLeave}
                onClick={handleRowClick(row)}>
                {columns.map((column: any, index: number) => <td key={`td-${index}`}
                                                                 style={styles.td}>{row[column.field]}</td>)}
            </tr>
        )
    }

    const shadeColor = (color: string, percent: number) => {

        let R: number | string = parseInt(color.trim().substring(1, 3), 16);
        let G: number | string = parseInt(color.trim().substring(3, 5), 16);
        let B: number | string = parseInt(color.trim().substring(5, 7), 16);

        R = parseInt(`${R * (100 + percent) / 100}`);
        G = parseInt(`${G * (100 + percent) / 100}`);
        B = parseInt(`${B * (100 + percent) / 100}`);

        R = (R < 255) ? R : 255;
        G = (G < 255) ? G : 255;
        B = (B < 255) ? B : 255;
        return {R, G, B};
    }

    const handleRowClick = (rowData: any) => () => {
        console.log('Clicked inside', recordClick, readOnly)
        if (recordClick && !readOnly) {
            recordClick(rowData);
        }
    }

    const rowMouseEnter = (ev: React.MouseEvent<HTMLElement>) => {
        if (!readOnly) {
            const shadered = shadeColor(getComputedStyle(document.getElementById(hookElementId)).getPropertyValue('--color-combo-fhdp'), 0);
            ev.currentTarget.style.backgroundColor = `rgba(${shadered.R},${shadered.G},${shadered.B}, 0.5)`
        }
    }

    const rowMouseLeave = (ev: React.MouseEvent<HTMLElement>) => {
        if (!readOnly) {
            ev.currentTarget.style.backgroundColor = '';
        }
    }

    const generateHeadCell = (cell: { title: string, field: string }, key: string | number) => {
        return (
            <th key={`th-${key}`} style={styles.th}>{cell.title}</th>
        )
    }

    const generatePagination = () => {
        console.log("generate pagination", page, totalPages);
        if (page === -1 || totalPages <= 1) {
            return (null);
        }
        return (
            <div style={styles.pagination}>
                <button onClick={() => {
                    prevPage()
                }} style={styles.pageButton} disabled={page <= 0}>{'<'}</button>
                <span>&nbsp;{page + 1}&nbsp;{translate('of')}&nbsp;{totalPages}&nbsp;</span>
                <button onClick={() => {
                    nextPage()
                }} style={styles.pageButton} disabled={page >= totalPages - 1}>{'>'}</button>
            </div>
        )
    }

    const nextPage = () => {
        // setPage(page + 1);
        if (actionDebouncing) {
            return;
        }
        setTimeout(() => {
            setActionDecouncing(false);
        }, 200);
        setActionDecouncing(true);
        fireChangePopupEvent([], 'nextPage');
    }

    const prevPage = () => {
        // setPage(page - 1);
        if (actionDebouncing) {
            return;
        }
        setTimeout(() => {
            setActionDecouncing(false);
        }, 200);
        setActionDecouncing(true);
        fireChangePopupEvent([], 'prevPage');
    }

    React.useEffect(() => {
        setTotalPages(pagesCount);
    }, [pagesCount])

    React.useEffect(() => {
        console.log("currentPage", currentPage)
        setPage(currentPage);
    }, [currentPage])

    // if (open && page === -1 && rows.length === 0 && !readOnly) {
    //   // fireChangePopupEvent([], 'onClickSearchIcon');
    //   setPage(0);
    // }

    React.useEffect(() => {
        if (forceUpdate) {
            // forceUpdate();
        }
        setPopperElementEx(popperElement.current);
    }, [popperElement.current])

    React.useEffect(() => {
        if (forceUpdate && !afterFirstUpdate) {
            // forceUpdate();
            // setAfterFirstUpdate(true);
        }
    }, [forceUpdate])

    React.useEffect(() => {
        $(document).on('click touchend', closeOnClickOutside);
        LIST_OF_BUTTONS.forEach(selector => {
            $(`${selector}`).on('click touchend', closeOnClickOutside);
        })
        return () => {
            $(document).off('click touchend', closeOnClickOutside);
            LIST_OF_BUTTONS.forEach(selector => {
                $(`${selector}`).off('click touchend', closeOnClickOutside);
            })
        }
    })

    const generateEmptyRows = () => {
        console.log("LanguageResiterer._i18n.__('no-data')", translate('no-data'))
        return (
            <tr>
                <td style={styles.emptyRows} colSpan={columns.length}
                    onClick={unlockClickInPopup}>{translate('no-data')}</td>
            </tr>
        );
    }

    const unlockClickInPopup = (e) => {
        clickInPopup(false);
    }

    const closeOnClickOutside = (e) => {
        if (popperElement.current && (popperElement.current.contains(e.target) || parent.contains(e.target))) {
            clickInPopup(true);
            return;
        } else {
            clickInPopup(false);
        }
        handleClose();
    };

    styles.pageButton = {
        width: '20px',
        height: '20px',
        background: 'transparent',
        color: 'var(--color-font-header-popup)',
        border: '1px solid var(--color-font-header-popup)',
        margin: '4px',
        padding: '0px',
    }
    const shaderedBg = shadeColor(getComputedStyle(document.getElementById(hookElementId)).getPropertyValue('--color-bg-popup'), 0);

    styles.popper = {
        ...styles.popper,
        maxHeight: "40vh",
        minWidth: "400px",
        minHeight: "200px",
        maxWidth: '50vw',
        width: `${columns.length * 100}px`,
        zIndex: 99999,
        display: 'none',
        alignItems: 'flex-end',
        flexDirection: 'column',
        backgroundColor: 'var(--color-bg-popup)',//backgroundColor || `rgb(${shaderedBg.R},${shaderedBg.G},${shaderedBg.B})`//'var(--color-bg)'
        color: 'var(--color-font-popup)',
        boxShadow: '5px 5px 10px 4px rgba(0,0,0,0.2),0px 1px 1px 0px rgba(0,0,0,0.14),0px 1px 3px 0px rgba(0,0,0,0.12)',
        transition: 'box-shadow 300ms cubic-bezier(0.4, 0, 0.2, 1) 0ms, visibility 0s, opacity 0.5s linear',
        border: 'var(--border-dictionary-combo)',

        // transition: 'visibility 0s, opacity 0.5s linear'
    }

    const [popperDisplay, setPopperDisplay] = React.useState({display: 'none'});

    styles.header = {
        width: '100%',
        height: '40px',
        lineHeight: '20px',
        padding: '10px',
        display: 'flex',
        alignItems: 'flex-end',
        borderBottom: 'solid 1px lightgray',
        backgroundColor: 'var(--color-bg-header-popup)',
        color: 'var(--color-font-header-popup)',
        border: 'var(--border-dictionary-combo)',
    }


    // --color-bg-popup: #2b2b2b;
    // --color-bg-header-popup: #eeff00;
    // --color-font-popup: #eeff00;
    // --color-font-header-popup: #000;

    styles.futter = {
        width: '100%',
        height: '50px',
        padding: '15px',
        display: 'flex',
        alignItems: 'flex-end',
        borderTop: 'solid 1px lightgray'
    }

    styles.content = {
        width: '100%',
        maxHeight: "calc(40vh - 50px)",
        flexGrow: 1,
        overflowY: 'auto',
    }

    styles.table = {
        width: '100%',
        height: rows.length === 0 ? 'calc(100% - 21px)' : undefined,
    }

    styles.separator = {
        flexGrow: 1,
        minWidth: '30px'
    }

    styles.th = {
        padding: '5px',
        border: 'solid 1px var(--color-font-popup)',
        borderBottom: 'solid 2px var(--color-font-popup)',
        maxHeight: '22px',
        height: '22px'
    }

    styles.td = {
        padding: '5px',
        border: 'solid 1px var(--color-font-popup)',
        borderTop: 'solid 0px var(--color-font-popup)',
        maxHeight: '22px',
        height: '22px'
    }

    styles.pagination = {
        position: 'relative',
        top: '4px',
        left: '10px',
    }

    styles.closeBtn = {
        fontSize: 'var(--font-size-default)'
    }

    styles.emptyRows = {
        textAlign: 'center'
    }

    React.useEffect(() => {
        setOpen(isOpen);
        console.log("useEffect is open <-------------------------------");
        setPopperDisplay({display: 'flex'})
        if (isOpen) {
            const pos = position === 'left' ? 'bottom-start' : position === 'right' ? 'bottom-end' : 'bottom-start';
            setPlacement(pos);
        } else {
            setPlacement('bottom-start');
        }
    }, [isOpen, position])

    const createPortal = () => {
        // const popups = document.querySelectorAll('[id^="dictionary-combo-popper-"]');
        // // popups.forEach((node) => {
        // //   // node.remove();
        // // })
        // for (const node of Array.from(popups)) {
        //   console.log('node', node.id, node);
        //   // node.remove();
        // }
        console.log('create portal')
        return ReactDOM.createPortal((
            <div ref={popperElement} id={`dictionary-combo-popper-${+new Date()}`} className={'MuiPaper-root'}
                 style={{...styles.popper, ...popperDisplay}} {...attributes.popper} onClick={unlockClickInPopup}>
                <div style={styles.header} onClick={unlockClickInPopup}>
                    <span dangerouslySetInnerHTML={{__html: title}} onClick={unlockClickInPopup}/>
                    {generatePagination()}
                    <div style={styles.separator} onClick={unlockClickInPopup}/>
                    <span style={styles.closeBtn} onClick={() => handleClose(true)}><i className="fa fa-times"/></span>
                </div>
                <div className={'pol'} style={styles.content}>
                    <table style={styles.table} className="table-striped">
                        <thead onClick={unlockClickInPopup}>
                        <tr>
                            {columns.map((headCell: any, index: number) => generateHeadCell(headCell, index))}
                        </tr>
                        </thead>
                        <tbody>
                        {rows.length > 0 && rows.map((row: any, index: number) => generateRow(row, columns, index))}
                        {rows.length === 0 && generateEmptyRows()}
                        </tbody>
                    </table>
                </div>
            </div>), document.getElementById('fh-layout-standard'), `dictionary-combo-popper-portal-key`);
    }

    return (
        <div>
            {open && hookElement && createPortal()}
        </div>
    )
}
