package pl.fhframework.core.session;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ForgetfulStoreTest {

    private Set<String> forgotten;
    private ForgetfulStore<String> store;

    @Before
    public void init() {
        forgotten = new HashSet<>();
        store = new ForgetfulStore<>();
        store.addListener(forgotten::add);
    }


    @Test
    public void forgettingOfNonexistingElementDoesntDoNothing() {
        store.forgetNow("XXX");
        assertTrue(forgotten.isEmpty());
    }

    @Test
    public void forgettingOfExistingElementCallsListener() {
        store.remember("XXX", 100);
        store.forgetNow("XXX");
        assertTrue(forgotten.contains("XXX"));
        assertEquals(1, forgotten.size());
    }

    @Test
    public void forgetTwiceDoesntBreakAnything() {
        store.remember("XXX", 100);
        store.forgetNow("XXX");
        store.forgetNow("XXX");
        assertTrue(forgotten.contains("XXX"));
        assertEquals(1, forgotten.size());
    }

    @Test
    public void forgetWhenTimePasses() {
        long time = System.currentTimeMillis();
        store.remember("A", 10);
        store.remember("B", 20);
        store.remember("C", 30);
        store.onTimePassed(time + 25);
        assertTrue(forgotten.contains("A"));
        assertTrue(forgotten.contains("B"));
        assertFalse(forgotten.contains("C"));
        store.onTimePassed(time + 35);
        assertTrue(forgotten.contains("C"));
    }

    @Test
    public void prolongRemembering() {
        long time = System.currentTimeMillis();
        store.remember("A", 10);
        store.onTimePassed(time + 5);
        assertFalse(forgotten.contains("A"));
        store.remember("A", 10);
        store.onTimePassed(time + 6); // gdyby nie linijka wyżej to już by zapomniał
        assertFalse(forgotten.contains("A"));
    }

}
