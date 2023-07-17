package id.fannan.withdatabase.utils;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppUtils {
    public static String formatDate(String inputDate) {
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = inputDateFormat.parse(inputDate);

            DateFormat outputDateFormat = new SimpleDateFormat("yyyy-MMM-dd");
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void showAlertDialog(Frame frame, String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
    }

}
