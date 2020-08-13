package pl.fhframework;

import pl.fhframework.model.forms.*;

import java.util.*;

import javax.xml.stream.XMLStreamReader;

//TODO: KKOZ class is not used in project. Is Deprecated?
@Deprecated
public class StaxParserExample {
    private final static String FORMATKA = "Form";
    private final static String KONFIGURACJA_DOSTEPNOSCI = "AvailabilityConfiguration";
    private final static String EDYCJA = "Edit";
    private final static String TYLKO_ODCZYT = "TylkoOdczyt";
    private final static String NIEWIDOCZNY = "Invisible";
    private final static String USTALA_PROGRAMISTA = "SetByProgrammer";
    private final static String WARIANT = "Variant";
    private final static String POLE_TEKSTOWE = "TextField";
    private final static String GRUPA = "Group";
    private final static String PRZYCISK = "PrzyciskOLD";
    private final static String LABEL = "Etykieta";
    private final static String TABELA = "Tabela";
    private final static String KOLUMNA = "Kolumna";

    private final static Map<String, Class<?>> nazwaNaKlaseKontrolki = new HashMap<>();


    public static List<AccessibilityRule> Wczytaj(Form formatka, String nazwaPliku) {
        List<AccessibilityRule> wszystkieRegulyDostepnosci = new ArrayList<>();
//        try {
//
//
//            Stack<ElementGrupujacy> elementyGrupujace = new Stack<>();
//            elementyGrupujace.push(form);
//
////        List<Employee> employees = null;
////        Employee empl = null;
//            String text = null;
//
//            XMLInputFactory factory = XMLInputFactory.newInstance();
//            XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(new File(nazwaPliku)));
//            final XmlAttributeReader czytnikAtrybutow = new XmlAttributeReader(reader);
//            boolean wKonfiguracjiDostepnosci = false;
//            String wariant = "";
//            String regulaGdyUstalonaWartosc = null;
//            String regulaGdyBrakWartosci = null;
//            String regulaGdy = null;
//
//
//            while (reader.hasNext()) {
//                int Event = reader.next();
//
//                switch (Event) {
//                    case XMLStreamConstants.START_ELEMENT: {
//                        String tag = reader.getLocalName();
//                        if (wKonfiguracjiDostepnosci) {
//                            if (WARIANT.equals(tag)) {
//                                wariant = czytnikAtrybutow.getAttributeValue("id");
//                            }
//                            regulaGdyUstalonaWartosc = get("gdyUstalonaWartosc", reader);
//                            regulaGdyBrakWartosci = get("gdyBrakWartosci", reader);
//                            regulaGdy = get("gdy", reader);
//                            continue;
//                        } else {
//                            if (KONFIGURACJA_DOSTEPNOSCI.equals(tag)) {
//                                wKonfiguracjiDostepnosci = true;
//                                continue;
//                            }
//                            if (nazwaNaKlaseKontrolki.containsKey(tag)) {
//                                ElementFormatki formElement = (ElementFormatki) nazwaNaKlaseKontrolki.get(tag).getConstructor(Formatka.class, XmlAttributeReader.class, ElementGrupujacy.class, boolean.class).newInstance(form, czytnikAtrybutow, elementyGrupujace.peek(), true);
//                                if (formElement instanceof ElementGrupujacy<?>) {
//                                    elementyGrupujace.push((ElementGrupujacy<?>) formElement);
//                                }
//                                continue;
//                            }
//
//                            if (FORMATKA.equals(tag)) {
//                                form.setId(get("id", reader));
//                                form.setLabel(get("label", reader));
//                                form.setKontener(get("kontener", reader));
//                                String proponowanyUklad = get("layout", reader);
//                                if (proponowanyUklad != null) {
//                                    form.setUklad(proponowanyUklad.toUpperCase());
//                                }
//
//                                form.setModalna("true".equalsIgnoreCase(get("modalna", reader)));
////                        empl = new Employee();
////                        empl.setID(reader.getAttributeValue(0));
//                            } else if (TABELA.equals(tag)) {
//                                String id = get("id", reader);
//                                String kolekcja = get("kolekcja", reader);
//                                String iterator = get("iterator", reader);
//                                String wybranyElement = get("wybranyElement", reader);
//                                Tabela nowaTabela = elementyGrupujace.peek().dodajTabele(id, kolekcja, iterator, wybranyElement, czytnikAtrybutow, elementyGrupujace.peek());
//                                elementyGrupujace.push(nowaTabela);
//                            } else if (KOLUMNA.equals(tag)) {
//                                String id = get("id", reader);
//                                String label = get("label", reader);
//                                String wartosc = get("wartosc", reader);
//                                Kolumna nowaKolumna = ((Tabela) elementyGrupujace.peek()).dodajKolumne(id, label, wartosc);
//                                elementyGrupujace.push(nowaKolumna.getPrototyp());
//                            }
//                        }
//                        break;
//
//                    }
//                    case XMLStreamConstants.CHARACTERS: {
//                        text = reader.getText().trim();
//                        break;
//                    }
//                    case XMLStreamConstants.END_ELEMENT: {
//                        String tekstZebrany = text;
//                        text = "";
//                        String nazwaTaga = reader.getLocalName();
//                        String nazwaTagaObecnegoKontenera = elementyGrupujace.peek().getTyp();
//                        if (wKonfiguracjiDostepnosci) {
//                            if (KONFIGURACJA_DOSTEPNOSCI.equals(nazwaTaga)) {
//                                wKonfiguracjiDostepnosci = false;
//                            } else if (WARIANT.equals(nazwaTaga)) {
//                                wariant = "";
//                            } else {
//                                String[] idElementow = tekstZebrany.split("\\s*,\\s*");
//                                for (String idElementu : idElementow) {
//                                    RegulaDostepnosci dodawanaRegula = null;
//                                    if (TYLKO_ODCZYT.equals(nazwaTaga)) {
//                                        dodawanaRegula = RegulaDostepnosci.createStaticRule(idElementu, wariant, AccessibilityEnum.VIEW, form, regulaGdy, regulaGdyUstalonaWartosc, regulaGdyBrakWartosci);
//                                    } else if (EDIT.equals(nazwaTaga)) {
//                                        dodawanaRegula = RegulaDostepnosci.createStaticRule(idElementu, wariant, AccessibilityEnum.EDIT, form, regulaGdy, regulaGdyUstalonaWartosc, regulaGdyBrakWartosci);
//                                    } else if (HIDDEN.equals(nazwaTaga)) {
//                                        dodawanaRegula = RegulaDostepnosci.createStaticRule(idElementu, wariant, AccessibilityEnum.HIDDEN, form, regulaGdy, regulaGdyUstalonaWartosc, regulaGdyBrakWartosci);
//                                    } else if (USTALA_PROGRAMISTA.endsWith(nazwaTaga)) {
//                                        dodawanaRegula = RegulaDostepnosci.createRuleDevEstablishes(idElementu, wariant, form);
//                                    }
//                                    if (dodawanaRegula != null) {
//                                        wszystkieRegulyDostepnosci.add(dodawanaRegula);
//                                    }
//                                }
//                            }
//
//                            continue;
//                        } else if (nazwaTagaObecnegoKontenera.equals(nazwaTaga)) {
//                            elementyGrupujace.pop();
//                        } else if (elementyGrupujace.peek() instanceof KomorkaTabeli && nazwaTaga.equals(KOLUMNA)) {
//                            elementyGrupujace.pop();
//                        }
//
////                        for (String nazwaIterowanegoTaga = elementyGrupujace.pop().getTyp(); !elementyGrupujace.empty() && nazwaIterowanegoTaga!=nazwaTaga; nazwaIterowanegoTaga = elementyGrupujace.pop().getTyp()){
////                            FhLogger.debug(this.getClass(), "Iterowany TAG {}", nazwaIterowanegoTaga);
////                        }
//                        if (elementyGrupujace.empty()) {
//                            return wszystkieRegulyDostepnosci;
//                        }
////                        switch (nazwaTaga) {
////                            case FORMATKA: {
////                                return;
////                            }
////                            case TABELA:
////                            case KOLUMNA:
////                            case GRUPA: {
////                                elementyGrupujace.pop();
////                                break;
////                            }
////                        }
//                        break;
//                    }
//                }
//            }
//        } catch (Exception exc) {
//            throw new RuntimeException(exc);
//        }

        return wszystkieRegulyDostepnosci;
    }

    private static String get(String nazwaAtrybutu, XMLStreamReader reader) {
        return reader.getAttributeValue("", nazwaAtrybutu);
    }

    public static void dodajKontrolki(Class... klasyKontrolek) {
        for (Class<FormElement> klasaKontrolki : klasyKontrolek) {
            String nazwaKontrolki = klasaKontrolki.getSimpleName();
            nazwaNaKlaseKontrolki.put(nazwaKontrolki, klasaKontrolki);
        }
    }


}