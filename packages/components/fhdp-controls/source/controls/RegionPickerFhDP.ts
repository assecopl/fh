// <reference path="../../node_modules/fh-forms-handler/dist/source/Forms/HTMLFormComponent.d.ts"/>
import {HTMLFormComponent, LanguageChangeObserver} from "fh-forms-handler";
import * as $ from 'jquery';
import world_mill from '../external/jvector/jvm-world-mill'
import europe_mill from '../external/jvector/jvm-europe-mill'
// import * as marked from "marked";
// import {Renderer} from "marked";
// import * as highlightjs from "highlightjs";



class RegionPickerFhDP extends HTMLFormComponent implements LanguageChangeObserver {
    private map: string;

    private AVALIBLE_MAPS = {
        'world': 'world_mill', 
        'europe': 'europe_mill'
    };

    private translations = {
        pl: {"AF":"Afganistan","AL":"Albania","DZ":"Algieria","AD":"Andora","AO":"Angola","AI":"Anguilla","AQ":"Antarktyda","AG":"Antigua i Barbuda","SA":"Arabia Saudyjska","AR":"Argentyna","AM":"Armenia","AW":"Aruba","AU":"Australia","AT":"Austria","AZ":"Azerbejd\u017can","BS":"Bahamy","BH":"Bahrajn","BD":"Bangladesz","BB":"Barbados","BE":"Belgia","BZ":"Belize","BJ":"Benin","BM":"Bermudy","BT":"Bhutan","BY":"Bia\u0142oru\u015b","BO":"Boliwia","BA":"Bo\u015bnia i Hercegowina","BW":"Botswana","BR":"Brazylia","BN":"Brunei","IO":"Brytyjskie Terytorium Oceanu Indyjskiego","VG":"Brytyjskie Wyspy Dziewicze","BG":"Bu\u0142garia","BF":"Burkina Faso","BI":"Burundi","CL":"Chile","CN":"Chiny","HR":"Chorwacja","CI":"C\u00f4te d\u2019Ivoire","CW":"Cura\u00e7ao","CY":"Cypr","TD":"Czad","ME":"Czarnog\u00f3ra","CZ":"Czechy","UM":"Dalekie Wyspy Mniejsze Stan\u00f3w Zjednoczonych","DK":"Dania","CD":"Demokratyczna Republika Konga","DM":"Dominika","DO":"Dominikana","DJ":"D\u017cibuti","EG":"Egipt","EC":"Ekwador","ER":"Erytrea","EE":"Estonia","SZ":"Eswatini","ET":"Etiopia","FK":"Falklandy","FJ":"Fid\u017ci","PH":"Filipiny","FI":"Finlandia","FR":"Francja","TF":"Francuskie Terytoria Po\u0142udniowe i Antarktyczne","GA":"Gabon","GM":"Gambia","GS":"Georgia Po\u0142udniowa i Sandwich Po\u0142udniowy","GH":"Ghana","GI":"Gibraltar","GR":"Grecja","GD":"Grenada","GL":"Grenlandia","GE":"Gruzja","GU":"Guam","GG":"Guernsey","GY":"Gujana","GF":"Gujana Francuska","GP":"Gwadelupa","GT":"Gwatemala","GN":"Gwinea","GW":"Gwinea Bissau","GQ":"Gwinea R\u00f3wnikowa","HT":"Haiti","ES":"Hiszpania","NL":"Holandia","HN":"Honduras","IN":"Indie","ID":"Indonezja","IQ":"Irak","IR":"Iran","IE":"Irlandia","IS":"Islandia","IL":"Izrael","JM":"Jamajka","JP":"Japonia","YE":"Jemen","JE":"Jersey","JO":"Jordania","KY":"Kajmany","KH":"Kambod\u017ca","CM":"Kamerun","CA":"Kanada","QA":"Katar","KZ":"Kazachstan","KE":"Kenia","KG":"Kirgistan","KI":"Kiribati","CO":"Kolumbia","KM":"Komory","CG":"Kongo","KR":"Korea Po\u0142udniowa","KP":"Korea P\u00f3\u0142nocna","CR":"Kostaryka","CU":"Kuba","KW":"Kuwejt","LA":"Laos","LS":"Lesotho","LB":"Liban","LR":"Liberia","LY":"Libia","LI":"Liechtenstein","LT":"Litwa","LU":"Luksemburg","LV":"\u0141otwa","MK":"Macedonia P\u00f3\u0142nocna","MG":"Madagaskar","YT":"Majotta","MW":"Malawi","MV":"Malediwy","MY":"Malezja","ML":"Mali","MT":"Malta","MP":"Mariany P\u00f3\u0142nocne","MA":"Maroko","MQ":"Martynika","MR":"Mauretania","MU":"Mauritius","MX":"Meksyk","FM":"Mikronezja","MM":"Mjanma (Birma)","MD":"Mo\u0142dawia","MC":"Monako","MN":"Mongolia","MS":"Montserrat","MZ":"Mozambik","NA":"Namibia","NR":"Nauru","NP":"Nepal","BQ":"Niderlandy Karaibskie","DE":"Niemcy","NE":"Niger","NG":"Nigeria","NI":"Nikaragua","NU":"Niue","NF":"Norfolk","NO":"Norwegia","NC":"Nowa Kaledonia","NZ":"Nowa Zelandia","OM":"Oman","PK":"Pakistan","PW":"Palau","PA":"Panama","PG":"Papua-Nowa Gwinea","PY":"Paragwaj","PE":"Peru","PN":"Pitcairn","PF":"Polinezja Francuska","PL":"Polska","PR":"Portoryko","PT":"Portugalia","ZA":"Republika Po\u0142udniowej Afryki","CF":"Republika \u015arodkowoafryka\u0144ska","CV":"Republika Zielonego Przyl\u0105dka","RE":"Reunion","RU":"Rosja","RO":"Rumunia","RW":"Rwanda","EH":"Sahara Zachodnia","KN":"Saint Kitts i Nevis","LC":"Saint Lucia","VC":"Saint Vincent i Grenadyny","BL":"Saint-Barth\u00e9lemy","MF":"Saint-Martin","PM":"Saint-Pierre i Miquelon","SV":"Salwador","WS":"Samoa","AS":"Samoa Ameryka\u0144skie","SM":"San Marino","SN":"Senegal","RS":"Serbia","SC":"Seszele","SL":"Sierra Leone","SG":"Singapur","SX":"Sint Maarten","SK":"S\u0142owacja","SI":"S\u0142owenia","SO":"Somalia","HK":"SRA Hongkong (Chiny)","MO":"SRA Makau (Chiny)","LK":"Sri Lanka","US":"Stany Zjednoczone","SD":"Sudan","SS":"Sudan Po\u0142udniowy","SR":"Surinam","SJ":"Svalbard i Jan Mayen","SY":"Syria","CH":"Szwajcaria","SE":"Szwecja","TJ":"Tad\u017cykistan","TH":"Tajlandia","TW":"Tajwan","TZ":"Tanzania","PS":"Terytoria Palesty\u0144skie","TL":"Timor Wschodni","TG":"Togo","TK":"Tokelau","TO":"Tonga","TT":"Trynidad i Tobago","TN":"Tunezja","TR":"Turcja","TM":"Turkmenistan","TC":"Turks i Caicos","TV":"Tuvalu","UG":"Uganda","UA":"Ukraina","UY":"Urugwaj","UZ":"Uzbekistan","VU":"Vanuatu","WF":"Wallis i Futuna","VA":"Watykan","VE":"Wenezuela","HU":"W\u0119gry","GB":"Wielka Brytania","VN":"Wietnam","IT":"W\u0142ochy","BV":"Wyspa Bouveta","CX":"Wyspa Bo\u017cego Narodzenia","IM":"Wyspa Man","SH":"Wyspa \u015awi\u0119tej Heleny","AX":"Wyspy Alandzkie","CK":"Wyspy Cooka","VI":"Wyspy Dziewicze Stan\u00f3w Zjednoczonych","HM":"Wyspy Heard i McDonalda","CC":"Wyspy Kokosowe","MH":"Wyspy Marshalla","FO":"Wyspy Owcze","SB":"Wyspy Salomona","ST":"Wyspy \u015awi\u0119tego Tomasza i Ksi\u0105\u017c\u0119ca","ZM":"Zambia","ZW":"Zimbabwe","AE":"Zjednoczone Emiraty Arabskie"},
        en: {"AF":"Afghanistan","AX":"\u00c5land Islands","AL":"Albania","DZ":"Algeria","AS":"American Samoa","AD":"Andorra","AO":"Angola","AI":"Anguilla","AQ":"Antarctica","AG":"Antigua & Barbuda","AR":"Argentina","AM":"Armenia","AW":"Aruba","AU":"Australia","AT":"Austria","AZ":"Azerbaijan","BS":"Bahamas","BH":"Bahrain","BD":"Bangladesh","BB":"Barbados","BY":"Belarus","BE":"Belgium","BZ":"Belize","BJ":"Benin","BM":"Bermuda","BT":"Bhutan","BO":"Bolivia","BA":"Bosnia & Herzegovina","BW":"Botswana","BV":"Bouvet Island","BR":"Brazil","IO":"British Indian Ocean Territory","VG":"British Virgin Islands","BN":"Brunei","BG":"Bulgaria","BF":"Burkina Faso","BI":"Burundi","KH":"Cambodia","CM":"Cameroon","CA":"Canada","CV":"Cape Verde","BQ":"Caribbean Netherlands","KY":"Cayman Islands","CF":"Central African Republic","TD":"Chad","CL":"Chile","CN":"China","CX":"Christmas Island","CC":"Cocos (Keeling) Islands","CO":"Colombia","KM":"Comoros","CG":"Congo - Brazzaville","CD":"Congo - Kinshasa","CK":"Cook Islands","CR":"Costa Rica","CI":"C\u00f4te d\u2019Ivoire","HR":"Croatia","CU":"Cuba","CW":"Cura\u00e7ao","CY":"Cyprus","CZ":"Czechia","DK":"Denmark","DJ":"Djibouti","DM":"Dominica","DO":"Dominican Republic","EC":"Ecuador","EG":"Egypt","SV":"El Salvador","GQ":"Equatorial Guinea","ER":"Eritrea","EE":"Estonia","SZ":"Eswatini","ET":"Ethiopia","FK":"Falkland Islands","FO":"Faroe Islands","FJ":"Fiji","FI":"Finland","FR":"France","GF":"French Guiana","PF":"French Polynesia","TF":"French Southern Territories","GA":"Gabon","GM":"Gambia","GE":"Georgia","DE":"Germany","GH":"Ghana","GI":"Gibraltar","GR":"Greece","GL":"Greenland","GD":"Grenada","GP":"Guadeloupe","GU":"Guam","GT":"Guatemala","GG":"Guernsey","GN":"Guinea","GW":"Guinea-Bissau","GY":"Guyana","HT":"Haiti","HM":"Heard & McDonald Islands","HN":"Honduras","HK":"Hong Kong SAR China","HU":"Hungary","IS":"Iceland","IN":"India","ID":"Indonesia","IR":"Iran","IQ":"Iraq","IE":"Ireland","IM":"Isle of Man","IL":"Israel","IT":"Italy","JM":"Jamaica","JP":"Japan","JE":"Jersey","JO":"Jordan","KZ":"Kazakhstan","KE":"Kenya","KI":"Kiribati","KW":"Kuwait","KG":"Kyrgyzstan","LA":"Laos","LV":"Latvia","LB":"Lebanon","LS":"Lesotho","LR":"Liberia","LY":"Libya","LI":"Liechtenstein","LT":"Lithuania","LU":"Luxembourg","MO":"Macao SAR China","MG":"Madagascar","MW":"Malawi","MY":"Malaysia","MV":"Maldives","ML":"Mali","MT":"Malta","MH":"Marshall Islands","MQ":"Martinique","MR":"Mauritania","MU":"Mauritius","YT":"Mayotte","MX":"Mexico","FM":"Micronesia","MD":"Moldova","MC":"Monaco","MN":"Mongolia","ME":"Montenegro","MS":"Montserrat","MA":"Morocco","MZ":"Mozambique","MM":"Myanmar (Burma)","NA":"Namibia","NR":"Nauru","NP":"Nepal","NL":"Netherlands","NC":"New Caledonia","NZ":"New Zealand","NI":"Nicaragua","NE":"Niger","NG":"Nigeria","NU":"Niue","NF":"Norfolk Island","KP":"North Korea","MK":"North Macedonia","MP":"Northern Mariana Islands","NO":"Norway","OM":"Oman","PK":"Pakistan","PW":"Palau","PS":"Palestinian Territories","PA":"Panama","PG":"Papua New Guinea","PY":"Paraguay","PE":"Peru","PH":"Philippines","PN":"Pitcairn Islands","PL":"Poland","PT":"Portugal","PR":"Puerto Rico","QA":"Qatar","RE":"R\u00e9union","RO":"Romania","RU":"Russia","RW":"Rwanda","WS":"Samoa","SM":"San Marino","ST":"S\u00e3o Tom\u00e9 & Pr\u00edncipe","SA":"Saudi Arabia","SN":"Senegal","RS":"Serbia","SC":"Seychelles","SL":"Sierra Leone","SG":"Singapore","SX":"Sint Maarten","SK":"Slovakia","SI":"Slovenia","SB":"Solomon Islands","SO":"Somalia","ZA":"South Africa","GS":"South Georgia & South Sandwich Islands","KR":"South Korea","SS":"South Sudan","ES":"Spain","LK":"Sri Lanka","BL":"St. Barth\u00e9lemy","SH":"St. Helena","KN":"St. Kitts & Nevis","LC":"St. Lucia","MF":"St. Martin","PM":"St. Pierre & Miquelon","VC":"St. Vincent & Grenadines","SD":"Sudan","SR":"Suriname","SJ":"Svalbard & Jan Mayen","SE":"Sweden","CH":"Switzerland","SY":"Syria","TW":"Taiwan","TJ":"Tajikistan","TZ":"Tanzania","TH":"Thailand","TL":"Timor-Leste","TG":"Togo","TK":"Tokelau","TO":"Tonga","TT":"Trinidad & Tobago","TN":"Tunisia","TR":"Turkey","TM":"Turkmenistan","TC":"Turks & Caicos Islands","TV":"Tuvalu","UM":"U.S. Outlying Islands","VI":"U.S. Virgin Islands","UG":"Uganda","UA":"Ukraine","AE":"United Arab Emirates","GB":"United Kingdom","US":"United States","UY":"Uruguay","UZ":"Uzbekistan","VU":"Vanuatu","VA":"Vatican City","VE":"Venezuela","VN":"Vietnam","WF":"Wallis & Futuna","EH":"Western Sahara","YE":"Yemen","ZM":"Zambia","ZW":"Zimbabwe"},
        lt: {"AF":"Afganistanas","IE":"Airija","AX":"Aland\u0173 Salos","AL":"Albanija","DZ":"Al\u017eyras","AS":"Amerikos Samoa","AD":"Andora","AI":"Angilija","AO":"Angola","AQ":"Antarktida","AG":"Antigva ir Barbuda","AR":"Argentina","AM":"Arm\u0117nija","AW":"Aruba","AU":"Australija","AT":"Austrija","AZ":"Azerbaid\u017eanas","BS":"Bahamos","BH":"Bahreinas","BY":"Baltarusija","BD":"Banglade\u0161as","BB":"Barbadosas","BE":"Belgija","BZ":"Belizas","BJ":"Beninas","BM":"Bermuda","GW":"Bisau Gvin\u0117ja","BO":"Bolivija","BA":"Bosnija ir Hercegovina","BW":"Botsvana","BR":"Brazilija","BN":"Brun\u0117jus","BG":"Bulgarija","BF":"Burkina Fasas","BI":"Burundis","BT":"Butanas","BV":"Buv\u0117 Sala","CF":"Centrin\u0117s Afrikos Respublika","TD":"\u010cadas","CZ":"\u010cekija","CL":"\u010cil\u0117","DK":"Danija","VG":"Did\u017eiosios Britanijos Mergeli\u0173 Salos","DM":"Dominika","DO":"Dominikos Respublika","CI":"Dramblio Kaulo Krantas","JE":"D\u017eersis","DJ":"D\u017eibutis","EG":"Egiptas","EC":"Ekvadoras","ER":"Eritr\u0117ja","EE":"Estija","ET":"Etiopija","FO":"Farer\u0173 Salos","FJ":"Fid\u017eis","PH":"Filipinai","FK":"Folklando Salos","GA":"Gabonas","GY":"Gajana","GM":"Gambija","GH":"Gana","GG":"Gernsis","GI":"Gibraltaras","GR":"Graikija","GD":"Grenada","GL":"Grenlandija","GE":"Gruzija","GU":"Guamas","GP":"Gvadelupa","GT":"Gvatemala","GN":"Gvin\u0117ja","HT":"Haitis","HM":"Herdo ir Makdonaldo Salos","HN":"Hond\u016bras","IN":"Indija","IO":"Indijos Vandenyno Brit\u0173 Sritis","ID":"Indonezija","HK":"Ypatingasis Administracinis Kinijos Regionas Honkongas","MO":"Ypatingasis Administracinis Kinijos Regionas Makao","IQ":"Irakas","IR":"Iranas","IS":"Islandija","ES":"Ispanija","IT":"Italija","IL":"Izraelis","JM":"Jamaika","JP":"Japonija","YE":"Jemenas","JO":"Jordanija","GB":"Jungtin\u0117 Karalyst\u0117","US":"Jungtin\u0117s Valstijos","AE":"Jungtiniai Arab\u0173 Emyratai","UM":"Jungtini\u0173 Valstij\u0173 Ma\u017eosios Tolimosios Salos","VI":"Jungtini\u0173 Valstij\u0173 Mergeli\u0173 Salos","ME":"Juodkalnija","KY":"Kaiman\u0173 Salos","CX":"Kal\u0117d\u0173 Sala","KH":"Kambod\u017ea","CM":"Kamer\u016bnas","CA":"Kanada","BQ":"Karib\u0173 Nyderlandai","QA":"Kataras","KZ":"Kazachstanas","KE":"Kenija","CN":"Kinija","CY":"Kipras","KG":"Kirgizija","KI":"Kiribatis","CW":"Kiurasao","CC":"Kokos\u0173 (Kilingo) Salos","CO":"Kolumbija","KM":"Komorai","CG":"Kongas-Brazavilis","CD":"Kongas-Kin\u0161asa","CR":"Kosta Rika","HR":"Kroatija","CU":"Kuba","CK":"Kuko Salos","KW":"Kuveitas","LA":"Laosas","LV":"Latvija","PL":"Lenkija","LS":"Lesotas","LB":"Libanas","LR":"Liberija","LY":"Libija","LI":"Lichten\u0161teinas","LT":"Lietuva","LU":"Liuksemburgas","MG":"Madagaskaras","YT":"Majotas","MY":"Malaizija","MW":"Malavis","MV":"Maldyvai","ML":"Malis","MT":"Malta","MP":"Marianos \u0160iaurin\u0117s Salos","MA":"Marokas","MH":"Mar\u0161alo Salos","MQ":"Martinika","MU":"Mauricijus","MR":"Mauritanija","MX":"Meksika","IM":"Meno Sala","MM":"Mianmaras (Birma)","FM":"Mikronezija","MD":"Moldova","MC":"Monakas","MN":"Mongolija","MS":"Montseratas","MZ":"Mozambikas","NA":"Namibija","NC":"Naujoji Kaledonija","NZ":"Naujoji Zelandija","NR":"Nauru","NP":"Nepalas","NL":"Nyderlandai","NG":"Nigerija","NE":"Nigeris","NI":"Nikaragva","NU":"Niuj\u0117","NF":"Norfolko sala","NO":"Norvegija","OM":"Omanas","PK":"Pakistanas","PW":"Palau","PS":"Palestinos teritorija","PA":"Panama","PG":"Papua Naujoji Gvin\u0117ja","PY":"Paragvajus","PE":"Peru","ZA":"Piet\u0173 Afrika","GS":"Piet\u0173 D\u017eord\u017eija ir Piet\u0173 Sandvi\u010do salos","KR":"Piet\u0173 Kor\u0117ja","SS":"Piet\u0173 Sudanas","PN":"Pitkerno salos","PT":"Portugalija","FR":"Pranc\u016bzija","GF":"Pranc\u016bzijos Gviana","TF":"Pranc\u016bzijos Piet\u0173 sritys","PF":"Pranc\u016bzijos Polinezija","PR":"Puerto Rikas","GQ":"Pusiaujo Gvin\u0117ja","RE":"Reunjonas","TL":"Ryt\u0173 Timoras","RW":"Ruanda","RO":"Rumunija","RU":"Rusija","SB":"Saliamono Salos","SV":"Salvadoras","WS":"Samoa","SM":"San Marinas","ST":"San Tom\u0117 ir Prinsip\u0117","SA":"Saudo Arabija","SC":"Sei\u0161eliai","BL":"Sen Bartelemi","MF":"Sen Martenas","PM":"Sen Pjeras ir Mikelonas","SN":"Senegalas","KN":"Sent Kitsas ir Nevis","LC":"Sent Lusija","RS":"Serbija","SL":"Siera Leon\u0117","SG":"Singap\u016bras","SX":"Sint Martenas","SY":"Sirija","SK":"Slovakija","SI":"Slov\u0117nija","SO":"Somalis","SD":"Sudanas","FI":"Suomija","SR":"Surinamas","SJ":"Svalbardas ir Janas Majenas","SZ":"Svazilandas","KP":"\u0160iaur\u0117s Kor\u0117ja","MK":"\u0160iaur\u0117s Makedonija","LK":"\u0160ri Lanka","SH":"\u0160v. Elenos Sala","SE":"\u0160vedija","CH":"\u0160veicarija","VC":"\u0160ventasis Vincentas ir Grenadinai","TJ":"Tad\u017eikija","TH":"Tailandas","TW":"Taivanas","TZ":"Tanzanija","TC":"Terkso ir Kaikoso Salos","TG":"Togas","TK":"Tokelau","TO":"Tonga","TT":"Trinidadas ir Tobagas","TN":"Tunisas","TR":"Turkija","TM":"Turkm\u0117nistanas","TV":"Tuvalu","UG":"Uganda","UA":"Ukraina","UY":"Urugvajus","UZ":"Uzbekistanas","EH":"Vakar\u0173 Sachara","VU":"Vanuatu","VA":"Vatikano Miesto Valstyb\u0117","VE":"Venesuela","HU":"Vengrija","VN":"Vietnamas","DE":"Vokietija","WF":"Volisas ir Fut\u016bna","ZM":"Zambija","ZW":"Zimbabv\u0117","CV":"\u017daliasis Ky\u0161ulys"}
    }

    private RATIO_MAP = 1.9672;
    private bgColor: string;
    private code: string;


    private languageWrapped: any;

    // [index: string]: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        console.log('RegionPickerFhDP log', componentObj)
        this.map = this.componentObj.map;
        this.languageWrapped = this.componentObj.language || null;
        this.accessibility = this.componentObj.accessibility || 'HIDDEN';
        this.bgColor = this.componentObj.bgColor;
        this.code = this.componentObj.code;
        
        try {
            ($.fn as any).vectorMap('get', 'mapObject')
        } catch {
            require('jvectormap-next')($);
            require('jquery-mousewheel')($);
            ($.fn as any).vectorMap('addMap', 'world_mill', world_mill);
            ($.fn as any).vectorMap('addMap', 'europe_mill', europe_mill);
        }
    }

    /**
     * @override
     * Funkcja odpowiedzialan za tworzenie komponentu
     */
    create() {

        const wrapper = document.createElement('div');
        wrapper.id = this.id;
        wrapper.classList.add('map-picker');
        this.i18n.subscribe(this);
        
        this.component = wrapper;
        this.wrap(false, false);
        this.addStyles();

        this.createMap();

        this.display();
        this.updateSize();
    };

    /**
     * @Override
     * Aktualizacja kontrolki w przypadku gdy z backendu przyjdą nowe/zaktualizowane dane
     * @param change
     */
    update(change) {
        super.update(change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'language':
                        this.languageWrapped = newValue;
                        break;
                    case 'code':
                        this.code = newValue;
                        break;
                }
            }.bind(this));
        }
    };

    createMap() {
        const mountNode: any = $(this.component);

        (mountNode as any).vectorMap({
            map: this.AVALIBLE_MAPS[this.map],
            backgroundColor: this.bgColor || window.getComputedStyle(document.body).getPropertyValue('--color-bg-light'),
            regionStyle: {
                initial: {
                    fill: window.getComputedStyle(document.body).getPropertyValue('--color-main')
                }
            },
            onRegionTipShow: this.tipShow.bind(this),
            zoomOnScroll: true,
            panOnDrag: true,
            zoomMin: 1,
            zoomMax: 10,
            onRegionClick: (event, code) => {
                this.code = code;
                this.changesQueue.queueAttributeChange('code', code);
                this.fireEventWithLock('countrySelected', code);
            },
        });

        mountNode[0].style.height = (mountNode[0].offsetWidth / this.RATIO_MAP).toFixed(2) + 'px';
    }

    deleteMap() {
        Array.from(this.component.children).forEach((el: HTMLElement) => el.remove())
    }

    languageChanged(code: string) {
        this.languageWrapped = code;
    }

    tipShow(e, el, code) {
        el.html(`${this.translations[this.languageWrapped][code]} (${code.toUpperCase()})`);
    }

    setAccessibility(accessibility) {
        super.setAccessibility(accessibility);
        if (accessibility !== 'HIDDEN') {
            this.i18n.unsubscribe(this);
            this.deleteMap();
            this.i18n.subscribe(this);
            this.createMap();
            const mountNode: any = $(this.component);
            mountNode[0].style.height = (mountNode[0].offsetWidth / this.RATIO_MAP).toFixed(2) + 'px';
            this.updateSize()
        } else {
            this.i18n.unsubscribe(this);
            this.deleteMap();
        }
        this.accessibility = accessibility;
    }

    updateSize() {
        const mountNode: any = $(this.component);
        const mapObj = (mountNode as any).vectorMap('get', 'mapObject');
        if (mapObj) {
            setTimeout(() => {
                mapObj.reset();
                mapObj.scale = 1;
                mapObj.transY = 0;
                mapObj.transX = 0;
            }, 200);
            mapObj.updateSize();
        }
    }

    destroy(removeFromParent){
        this.i18n.unsubscribe(this);
        this.deleteMap();
        super.destroy(removeFromParent);
    }

}

export {RegionPickerFhDP};
