package org.example.Barnes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class BarnesAndNobleTest {

    @Test
    @DisplayName("specification-based: Retrieve book and calculate price correctly")
    void testRetrieveBook() {
        BookDatabase mockDatabase = Mockito.mock(BookDatabase.class);
        BuyBookProcess mockProcess = Mockito.mock(BuyBookProcess.class);

        Book book = new Book("12345", 10, 5); // Price = 10, Quantity = 5
        Mockito.when(mockDatabase.findByISBN("12345")).thenReturn(book);

        BarnesAndNoble store = new BarnesAndNoble(mockDatabase, mockProcess);
        Map<String, Integer> order = new HashMap<>();
        order.put("12345", 2); // Buying 2 books

        PurchaseSummary summary = store.getPriceForCart(order);

        assertNotNull(summary);
        assertEquals(20, summary.getTotalPrice()); // 2 * 10 = 20
        assertTrue(summary.getUnavailable().isEmpty()); // No unavailable books

        Mockito.verify(mockProcess).buyBook(book, 2);
    }

    @Test
    @DisplayName("specification-based: Handle unavailable book quantity")
    void testBookPartiallyUnavailable() {
        BookDatabase mockDatabase = Mockito.mock(BookDatabase.class);
        BuyBookProcess mockProcess = Mockito.mock(BuyBookProcess.class);

        Book book = new Book("54321", 15, 1); // Only 1 copy available
        Mockito.when(mockDatabase.findByISBN("54321")).thenReturn(book);

        BarnesAndNoble store = new BarnesAndNoble(mockDatabase, mockProcess);
        Map<String, Integer> order = new HashMap<>();
        order.put("54321", 3); // Trying to buy 3 books

        PurchaseSummary summary = store.getPriceForCart(order);

        assertNotNull(summary);
        assertEquals(15, summary.getTotalPrice()); // Only 1 * 15 = 15 was possible
        assertEquals(2, summary.getUnavailable().get(book)); // 2 books unavailable

        Mockito.verify(mockProcess).buyBook(book, 1);
    }

    @Test
    @DisplayName("specification-based: Handle book completely unavailable")
    void testBookCompletelyUnavailable() {
        BookDatabase mockDatabase = Mockito.mock(BookDatabase.class);
        BuyBookProcess mockProcess = Mockito.mock(BuyBookProcess.class);

        Book book = new Book("99999", 20, 0); // No copies available
        Mockito.when(mockDatabase.findByISBN("99999")).thenReturn(book);

        BarnesAndNoble store = new BarnesAndNoble(mockDatabase, mockProcess);
        Map<String, Integer> order = new HashMap<>();
        order.put("99999", 2);

        PurchaseSummary summary = store.getPriceForCart(order);

        assertNotNull(summary);
        assertEquals(0, summary.getTotalPrice()); // No books could be bought
        assertEquals(2, summary.getUnavailable().get(book)); // All 2 books unavailable

        Mockito.verify(mockProcess, Mockito.never()).buyBook(book, 2);
    }
    @Test
    @DisplayName("structural-based: Handle null input order")
    void testNullOrder() {
        BookDatabase mockDatabase = Mockito.mock(BookDatabase.class);
        BuyBookProcess mockProcess = Mockito.mock(BuyBookProcess.class);

        BarnesAndNoble store = new BarnesAndNoble(mockDatabase, mockProcess);
        assertNull(store.getPriceForCart(null));
    }

    @Test
    @DisplayName("structural-based: Handle empty cart")
    void testEmptyCart() {
        BookDatabase mockDatabase = Mockito.mock(BookDatabase.class);
        BuyBookProcess mockProcess = Mockito.mock(BuyBookProcess.class);

        BarnesAndNoble store = new BarnesAndNoble(mockDatabase, mockProcess);
        PurchaseSummary summary = store.getPriceForCart(Collections.emptyMap());

        assertNotNull(summary);
        assertEquals(0, summary.getTotalPrice());
        assertTrue(summary.getUnavailable().isEmpty());
    }

    @Test
    @DisplayName("structural-based: Ensure PurchaseSummary stores unavailable books correctly")
    void testPurchaseSummaryStoresUnavailableBooks() {
        PurchaseSummary summary = new PurchaseSummary();
        Book book = new Book("11111", 10, 0);

        summary.addUnavailable(book, 3);
        assertEquals(3, summary.getUnavailable().get(book));
    }

}


