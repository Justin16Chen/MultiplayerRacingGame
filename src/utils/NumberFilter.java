package utils;

import javax.swing.text.*;

// numbers only text input
public class NumberFilter extends DocumentFilter {

    public NumberFilter() {
    }

    public static boolean inBounds(int num, int min, int max) {
        return min < num  && num < max;
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text == null) return;

        if (isInteger(text)) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    private boolean isInteger(String text) {
        return text.matches("\\d*"); // Allow only digits
    }
}