package id.fannan.withoutdatabase.ui;


import com.toedter.calendar.JDateChooser;
import id.fannan.withoutdatabase.entity.Pasien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class App {

    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField namaField;
    private JTextField alamatField;
    private JTextField nikField;

    private List<Pasien> pasienList;

    private JDateChooser jDateChooser;


    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                App window = new App();
                window.frame.setVisible(true);
                window.initData();
                window.tampilkanData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public App() {
        pasienList = new ArrayList<>();
        initialize();

    }

    private void initialize() {

        frame = new JFrame();
        frame.setTitle("Klinik");
        frame.setBounds(100, 100, 500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.NORTH);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JLabel lblNewLabel = new JLabel("Nama:");
        panel.add(lblNewLabel);

        namaField = new JTextField();
        panel.add(namaField);
        namaField.setColumns(15);

        JLabel lblNewLabel_1 = new JLabel("Alamat:");
        panel.add(lblNewLabel_1);

        alamatField = new JTextField();
        panel.add(alamatField);
        alamatField.setColumns(15);

        JLabel lblNewLabel_2 = new JLabel("NIK:");
        panel.add(lblNewLabel_2);

        nikField = new JTextField();
        panel.add(nikField);
        nikField.setColumns(10);

        JLabel lblNewLabel_3 = new JLabel("Tanggal Lahir:");
        panel.add(lblNewLabel_3);

        jDateChooser = new JDateChooser();
        jDateChooser.setDateFormatString("yyyy-MMM-dd");
        jDateChooser.setBounds(329, 10, 171, 24);
        panel.add(jDateChooser);

        JButton btnTambah = new JButton("Tambah");
        panel.add(btnTambah);
        btnTambah.addActionListener(e -> tambahPasien());

        JButton btnUpdate = new JButton("Update");
        panel.add(btnUpdate);
        btnUpdate.addActionListener(e -> {
            try {
                updatePasien();
                System.out.println("update");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton btnHapus = new JButton("Hapus");
        panel.add(btnHapus);
        btnHapus.addActionListener(e -> hapusPasien());

        JButton btnDaftar = new JButton("Daftar Pasien");
        panel.add(btnDaftar);

        btnDaftar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    tampilkanDataPasien();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton btnCari = new JButton("Cari");
        panel.add(btnCari);
        btnCari.addActionListener(e -> {
            String keyword = JOptionPane.showInputDialog(frame, "Masukkan NIK atau Nama Pasien:");
            if (keyword != null) {
                try {
                    cariPasien(keyword);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton btnTampilkanSemua = new JButton("Tampilkan Semua");
        panel.add(btnTampilkanSemua);
        btnTampilkanSemua.addActionListener(e -> {
            tampilkanData();
        });

        JButton btnSebelum = new JButton("Sebelumnya");
        panel.add(btnSebelum);
        btnSebelum.addActionListener(e -> {
            try {
                navigateRecord(false);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton btnSesudah = new JButton("Sesudahnya");
        panel.add(btnSesudah);
        btnSesudah.addActionListener(e -> {
            try {
                navigateRecord(true);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton btnKeluar = new JButton("Keluar");
        panel.add(btnKeluar);
        btnKeluar.addActionListener(e -> {
            int confirmResult = JOptionPane.showConfirmDialog(frame, "Apakah Anda yakin ingin keluar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirmResult == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });


        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"No", "Nama Pasien", "NIK", "Tanggal Lahir", "Alamat"});
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    displayAlertDialog(frame, "Error", "Tidak ada pasien yang dipilih.");
                    return;
                }

                // Get data from the selected row
                String nama = table.getValueAt(selectedRow, 1).toString();
                String alamat = table.getValueAt(selectedRow, 4).toString();
                String nik = table.getValueAt(selectedRow, 2).toString();
                String tanggalLahirString = table.getValueAt(selectedRow, 3).toString();

                // Set data to the text fields
                namaField.setText(nama);
                alamatField.setText(alamat);
                nikField.setText(nik);
                jDateChooser.setDate(parseTanggalLahir(tanggalLahirString));
            }
        });

        tampilkanData();
        scrollPane.setViewportView(table);

    }

    private void tampilkanDataPasien() {

        try {
            JFrame daftarPasienFrame = new JFrame();
            daftarPasienFrame.setTitle("Daftar Pasien");
            daftarPasienFrame.setBounds(100, 100, 500, 300);
            daftarPasienFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JScrollPane scrollPane = new JScrollPane();
            daftarPasienFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);

            JTable daftarPasienTable = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"No", "Nama Pasien", "NIK", "Tanggal Lahir", "Alamat"}));
            DefaultTableModel daftarPasienTableModel = (DefaultTableModel) daftarPasienTable.getModel();

            int no = 1;
            for (Pasien pasien : pasienList) {
                System.out.println(pasien.getAlamat());
                daftarPasienTableModel.addRow(new Object[]{no++, pasien.getNama(), pasien.getNik(), pasien.getTanggalLahir(), pasien.getAlamat()});
            }

            scrollPane.setViewportView(daftarPasienTable);
            daftarPasienFrame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateRecord(boolean next) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            displayAlertDialog(frame, "Error", "Tidak ada pasien yang dipilih.");
            return;
        }

        int columnCount = table.getColumnCount();
        Object[] selectedRowData = new Object[columnCount];
        for (int i = 0; i < columnCount; i++) {
            selectedRowData[i] = table.getValueAt(selectedRow, i);
        }

        int targetRow = next ? selectedRow + 1 : selectedRow - 1;
        if (targetRow >= 0 && targetRow < table.getRowCount()) {
            table.setRowSelectionInterval(targetRow, targetRow);
            selectedRow = targetRow; // Update nilai selectedRow

            // Mengisi data ke dalam JTextField
            namaField.setText(table.getValueAt(selectedRow, 1).toString());
            alamatField.setText(table.getValueAt(selectedRow, 4).toString());
            nikField.setText(table.getValueAt(selectedRow, 2).toString());

            String tanggalLahirString = table.getValueAt(selectedRow, 3).toString();
            String inputPattern = "yyyy-MMM-dd";
            String outputPattern = "yyyy-MMM-dd";

            SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputPattern);
            try {
                Date inputDate = inputDateFormat.parse(tanggalLahirString);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(inputDate);
                calendar.add(Calendar.DAY_OF_YEAR, -348);
                String formattedTanggalLahir = outputDateFormat.format(calendar.getTime());
                System.out.println("Converted Date: " + formattedTanggalLahir);
                jDateChooser.setDate(inputDate);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void initData() {
        // Data awal pasien
        Pasien pasien1 = new Pasien("John Doe", "Jl. Raya Badung", "482950173648219", "1990-May-15");
        Pasien pasien2 = new Pasien("Jane Smith", "Jl. Sudirman ", "726014835917326", "1985-Apr-20");
        Pasien pasien3 = new Pasien("Michael Johnson", "Jl. Gatot Subroto ", "193827456092837", "1978-Feb-07");

        // Menambahkan data pasien ke dalam List pasienList
        pasienList.add(pasien1);
        pasienList.add(pasien2);
        pasienList.add(pasien3);

        // Menampilkan data awal ke dalam tabel
        tampilkanData();
    }

    private void tampilkanData() {
//        menghapus semua baris pada table
        tableModel.setRowCount(0);
        int no = 1;
        for (Pasien pasien : pasienList) {
            tableModel.addRow(new Object[]{no++, pasien.getNama(), pasien.getNik(), pasien.getTanggalLahir(), pasien.getAlamat()});
        }
    }


    private void cariPasien(String keyword) {
        tableModel.setRowCount(0); // Menghapus semua baris pada tabel
        int no = 1;
        keyword = keyword.toLowerCase();
        for (Pasien pasien : pasienList) {
            if (pasien.getNik().toLowerCase().contains(keyword) || pasien.getNama().toLowerCase().contains(keyword)) {
                tableModel.addRow(new Object[]{no++, pasien.getNama(), pasien.getNik(), pasien.getTanggalLahir(), pasien.getAlamat()});
            }
        }
    }

    private void tambahPasien() {
        try {
            String nama = namaField.getText();
            String alamat = alamatField.getText();
            String nik = nikField.getText();
            String tanggalLahirString = String.valueOf(jDateChooser.getDate()); // Mengambil teks dari JTextField
            String inputPattern = "EEE MMM dd HH:mm:ss zzz yyyy";
            String outputPattern = "yyyy-MMM-dd";

            SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputPattern);

            try {
                Date inputDate;
                try {
                    inputDate = inputDateFormat.parse(tanggalLahirString);
                } catch (ParseException e) {
                    displayAlertDialog(frame, "Error", "Format tanggal salah. Harap masukkan format yang benar, contoh: 2022-May-25");
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(inputDate);
                calendar.add(Calendar.DAY_OF_YEAR, -348);
                String formattedTanggalLahir = outputDateFormat.format(calendar.getTime());
                System.out.println("Converted Date: " + formattedTanggalLahir);


                // Validate NIK: must contain 15 digits
                if (!nik.matches("\\d{15}")) {
                    displayAlertDialog(frame, "Error", "NIK harus berisi 15 angka.");
                    return;
                }

                // Validate the month (English only)
                if (!isValidMonth(formattedTanggalLahir)) {
                    System.out.println(tanggalLahirString);
                    displayAlertDialog(frame, "Error", "Nama bulan harus dalam bahasa Inggris. Contoh: 2022-Jun-25");
                    return;
                }


                if (nama.isEmpty() || alamat.isEmpty() || nik.isEmpty() || formattedTanggalLahir.isEmpty()) {
                    displayAlertDialog(frame, "Error", "Mohon lengkapi semua field.");
                    return;
                }


                for (Pasien pasien : pasienList) {
                    if (pasien.getNik().equals(nik)) {
                        displayAlertDialog(frame, "Error", "NIK telah digunakan. Silakan masukkan NIK yang berbeda.");
                        return;
                    }
                }

                Pasien newPasien = new Pasien(nama, alamat, nik, formattedTanggalLahir);
//                String tanggal_asal = newPasien.getTanggalLahir();
//                String tanggalLahirFinal = formatDate(tanggal_asal);
                pasienList.add(newPasien);
                tableModel.addRow(new Object[]{pasienList.size(), nama, nik, formattedTanggalLahir, alamat});
                clearForm();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePasien() throws ParseException {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            displayAlertDialog(frame, "Error", "Tidak ada pasien yang dipilih.");
            return;
        }

        String nama = namaField.getText();
        String alamat = alamatField.getText();
        String nik = nikField.getText();
        String tanggalLahirString = String.valueOf(jDateChooser.getDate()); // Mengambil teks dari JTextField

        if (nama.isEmpty() || alamat.isEmpty() || nik.isEmpty() || tanggalLahirString.isEmpty()) {
            displayAlertDialog(frame, "Error", "Mohon lengkapi semua field.");
            return;
        }

        // Validate NIK: must contain 15 digits
        if (!nik.matches("\\d{15}")) {
            displayAlertDialog(frame, "Error", "NIK harus berisi 15 angka.");
            return;
        }

        for (Pasien pasien : pasienList) {
            if (!pasien.equals(pasienList.get(selectedRow)) && pasien.getNik().equals(nik)) {
                displayAlertDialog(frame, "Error", "NIK sudah digunakan oleh pasien lain.");
                return;
            }
        }

        // Parse the date with exception handling
        String inputPattern = "EEE MMM dd HH:mm:ss zzz yyyy";
        String outputPattern = "yyyy-MMM-dd";

        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputPattern);

        try {
            Date inputDate;
            try {
                inputDate = inputDateFormat.parse(tanggalLahirString);
            } catch (ParseException e) {
                displayAlertDialog(frame, "Error", "Format tanggal salah. Harap masukkan format yang benar, contoh: 2022-May-25");
                return;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(inputDate);
            calendar.add(Calendar.DAY_OF_YEAR, -348);
            String formattedTanggalLahir = outputDateFormat.format(calendar.getTime());
            System.out.println("Converted Date: " + formattedTanggalLahir);

            // Validate the month (English only)
            if (!isValidMonth(formattedTanggalLahir)) {
                displayAlertDialog(frame, "Error", "Nama bulan harus dalam bahasa Inggris. Contoh: 2022-May-25");
                return;
            }
            try {
                Pasien pasien = pasienList.get(selectedRow);
                pasien.setNama(nama);
                pasien.setAlamat(alamat);
                pasien.setNik(nik);
                jDateChooser.setDate(parseTanggalLahir(formattedTanggalLahir));
                System.out.println(formattedTanggalLahir);

                tableModel.setValueAt(nama, selectedRow, 1);
                tableModel.setValueAt(nik, selectedRow, 2);
//            String tanggalLahirFinal = formatDate(tanggalLahir);
                tableModel.setValueAt(formattedTanggalLahir, selectedRow, 3);
                tableModel.setValueAt(alamat, selectedRow, 4);
                clearForm();

            } catch (Exception e) {
                e.printStackTrace();
                displayAlertDialog(frame, "Error", "Format tanggal salah. Harap masukkan format yang benar, contoh: 2022-May-25");
                return;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hapusPasien() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            displayAlertDialog(frame, "Error", "Tidak ada pasien yang dipilih.");
            return;
        }

        int confirmResult = JOptionPane.showConfirmDialog(frame, "Apakah Anda yakin ingin menghapus pasien ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirmResult == JOptionPane.YES_OPTION) {
            pasienList.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            clearForm();
        }
    }

    private void clearForm() {
        namaField.setText("");
        alamatField.setText("");
        nikField.setText("");
        jDateChooser.setDate(null);
        table.clearSelection();
    }

    public void displayAlertDialog(Frame frame, String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private Date parseTanggalLahir(String tanggalLahirString) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MMM-dd");
        try {
            return inputDateFormat.parse(tanggalLahirString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isValidMonth(String date) {
        String[] dateParts = date.split("-");
        if (dateParts.length != 3) {
            return false;
        }

        String month = dateParts[1].trim().toLowerCase();
        String[] validMonths = new String[]{"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};
        for (String validMonth : validMonths) {
            if (month.equalsIgnoreCase(validMonth)) {
                return true;
            }
        }

        return false;
    }

}
