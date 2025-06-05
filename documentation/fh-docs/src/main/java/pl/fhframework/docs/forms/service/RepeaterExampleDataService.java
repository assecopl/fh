package pl.fhframework.docs.forms.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import pl.fhframework.docs.forms.model.example.Student;

import java.util.*;

/**
 * Created by krzysztof.kobylarek on 2016-12-01.
 */
public class RepeaterExampleDataService {

    private static RepeaterExampleDataService INSTANCE = new RepeaterExampleDataService();

    public RepeaterExampleDataService() {
        _basket = new Basket();
    }

    public static List<Student> getStudents() {
        return StudentService.findAll();
    }

    List<String> links = Arrays.asList("http://google.pl", "http://www.bing.com", "https://search.yahoo.com", "https://www.yandex.com/");

    String link = links.stream().findAny().get();

    public static void shuffleLinks() {
        Collections.shuffle(INSTANCE.links);
    }

    public static String getLink() {
        return INSTANCE.links.get(new Random().nextInt(INSTANCE.links.size()));
    }

    @AllArgsConstructor
    public static class Book {
        @Getter
        private String title;
        @Getter
        private String author;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Book book = (Book) o;
            return Objects.equals(title, book.title) &&
                    Objects.equals(author, book.author);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, author);
        }
    }

    public class Basket {
        @Getter
        private LinkedList<BasketItem> books = new LinkedList<>();

        public void addBook(Book book) {
            Optional<BasketItem> book_ = books.stream().filter(c -> c.book.equals(book)).findAny();
            if (book_.isPresent()) {
                book_.get().quantity += 1;
            } else {
                books.add(new BasketItem(book, 1));
            }
        }

        public void delBook(BasketItem book) {
            book.quantity -= 1;

            if (book.quantity == 0) {
                books.remove(book);
            }
        }
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public class BasketItem {
        private Book book;
        private Integer quantity;
    }

    private List<Book> _books = Arrays.asList(
            new Book("Make me", "Lee Child"),
            new Book("The Bone Clocks", "David Mitchell"),
            new Book("The Girl with the Dragon Tattoo", "Stieg Larsson"),
            new Book("American Gods", "Neil Gaiman")
    );

    public static List<Book> getBooks() {
        return INSTANCE._books;
    }

    private Basket _basket;

    public static Basket getBasket() {
        return INSTANCE._basket;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class CurrencyItem {
        String currencyName;
        String currencyShort;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class CurrencyCalc {
        private CurrencyItem from;
        private CurrencyItem to;
        private Double convertValue;
        private String fromValue = "", toValue = "";

        private void invert() {
            CurrencyItem temp = from;
            from = to;
            to = temp;
            convertValue = 1 / convertValue;
        }
    }

    private static CurrencyItem PLN = new CurrencyItem("Polish zloty", "PLN");
    private static CurrencyItem GBP = new CurrencyItem("Pound sterling", "GBP");
    private static CurrencyItem EUR = new CurrencyItem("European euro", "EUR");
    private static CurrencyItem USD = new CurrencyItem("United States dollar", "USD");

    private static List<CurrencyCalc> currencyCalcs = Arrays.asList(
            currencyCalcGet(PLN, GBP, 0.1875), currencyCalcGet(PLN, EUR, 0.22), currencyCalcGet(PLN, USD, 0.23)
    );

    public static List<CurrencyCalc> getCurrencyCalcs() {
        return currencyCalcs;
    }

    private static CurrencyCalc currencyCalcGet(CurrencyItem from, CurrencyItem to, Double convertValue) {
        return new CurrencyCalc(from, to, convertValue, "", "");
    }
}
