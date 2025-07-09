package it.unical.gestorelibreria.utils;

public class ISBNUtils {

    public static String cleanIsbn(String raw) {
        return raw.replaceAll("[-\\s]", ""); // Rimuove trattini e spazi
    }

    public static boolean isValidIsbn(String isbn) {
        isbn = cleanIsbn(isbn);
        if (isbn.length() == 10)
            return isValidIsbn10(isbn);
        if (isbn.length() == 13)
            return isValidIsbn13(isbn);
        return false;
    }

    private static boolean isValidIsbn10(String isbn) {
        if (!isbn.matches("\\d{9}[\\dXx]")) return false;
        int sum = 0;
        for (int i = 0; i < 9; i++)
            sum += (isbn.charAt(i) - '0') * (i + 1);
        char last = isbn.charAt(9);
        sum += (last == 'X' || last == 'x') ? 10 * 10 : (last - '0') * 10;
        return sum % 11 == 0;
    }

    private static boolean isValidIsbn13(String isbn) {
        if (!isbn.matches("\\d{13}")) return false;
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = isbn.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int check = (10 - (sum % 10)) % 10;
        return check == (isbn.charAt(12) - '0');
    }
}
