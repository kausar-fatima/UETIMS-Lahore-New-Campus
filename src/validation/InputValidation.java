package validation;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class InputValidation {
	
	public static boolean isValidEmail(String email) {
	     // Check if the email has correct syntax with @gmail.com
	     //return email.matches("[a-zA-Z0-9]+@gmail\\.com");
	     return email.matches("[a-zA-Z0-9._%+-]+@(gmail\\.com|([a-zA-Z0-9-]+\\.)+pk)");
	 }

	 public static boolean isValidPassword(String password) {
	     // Check if the password is 8 characters long and contains at least one special character
	     return password.length() == 8 && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
	 }

    public static boolean isValidPositiveNumber(String number) {
        try {
            double parsedNumber = Double.parseDouble(number);
            return parsedNumber > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidPositiveInteger(String number) {
        try {
            int parsedNumber = Integer.parseInt(number);
            return parsedNumber > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidAlphabeticText(String text) {
        return text.matches("^[a-zA-Z ]+$");
    }

    public static boolean areFieldsFilled(Control... controls) {
        for (Control control : controls) {
            if (control instanceof TextField) {
                if (((TextField) control).getText().isEmpty()) {
                    return false;
                }
            } else if (control instanceof ComboBox) {
                if (((ComboBox<?>) control).getValue() == null) {
                    return false;
                }
            } else if (control instanceof DatePicker) {
                if (((DatePicker) control).getValue() == null) {
                    return false;
                }
            }
            // Add more conditions for other types of controls if needed
        }
        return true;
    }

    public static boolean isZeroOrPositiveInteger(String number) {
        try {
            int parsedNumber = Integer.parseInt(number);
            return parsedNumber >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isZero(String number) {
        try {
            int parsedNumber = Integer.parseInt(number);
            return parsedNumber == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isZeroOrPositiveNumber(String number) {
        try {
            double parsedNumber = Double.parseDouble(number);
            return parsedNumber >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isWithinCharacterLimit(String text, int limit) {
        return text.length() <= limit;
    }

    public static boolean isWithinfiftyLimit(String ch) {
        // Assuming a reasonable limit for  length
        return isWithinCharacterLimit(ch, 50);
    }

    public static boolean isWithinNameLimit(String cht) {
        // Assuming a reasonable limit for length
        return isWithinCharacterLimit(cht, 255);
    }
    public static boolean isValidStringLength(String str, int maxLength) {
        return str != null && str.length() <= maxLength;
    }
    public static String isValidYearAndMonth(String year, String month) {
        // Validate year
        if (!isZeroOrPositiveNumber(year) || year.length() != 4) {
            return "Please enter a valid year (4 digits).";
        }

        // Validate month
        int monthValue;
        try {
            monthValue = Integer.parseInt(month);
        } catch (NumberFormatException e) {
            return "Please enter a month (1-12).";
        }

        if (monthValue < 1 || monthValue > 12) {
            return "Please enter a month between 1 and 12.";
        }

        return null; // Indicates validation success
    }
    public static boolean isValidNumber(int number, int maxAllowed) {
        return number > 0 && number <= maxAllowed;
    }

  
    public static boolean isValidItemPrice(double itemPrice) {
        // Check if the itemPrice is within the valid range for DECIMAL(10,2)
        return itemPrice >= 0.00 && itemPrice <= 9999999.99;
    }
    public static boolean isRoomNumberRequired(String roomCategory) {
        // Check if room category is "students" or variations
        return roomCategory.toLowerCase().matches("students?");
    }
    public static void trimSpaces(TextField textField) {
        String trimmedText = textField.getText().trim();
        textField.setText(trimmedText);
    }
  
    public static void trimSpaces(TextArea textField) {
        String trimmedText = textField.getText().trim();
        textField.setText(trimmedText);
    }


}
