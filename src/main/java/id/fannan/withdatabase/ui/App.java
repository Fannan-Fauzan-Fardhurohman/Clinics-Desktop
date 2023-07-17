package id.fannan.withdatabase.ui;


import id.fannan.withdatabase.config.DatabaseConfig;
import id.fannan.withdatabase.entity.Pasien;
import id.fannan.withdatabase.utils.AppUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class App {
    private Connection connection;
    private AppUtils appUtils;

    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField namaField;
    private JTextField alamatField;
    private JTextField nikField;
    private JTextField tanggalLahirPicker;

    private List<Pasien> pasienList;


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    App window = new App();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void connect() {
        try {
            DatabaseConfig config = new DatabaseConfig();
            connection = config.getConnection();
            System.out.println("Koneksi ke database berhasil");
            appUtils = new AppUtils();
        } catch (Exception e) {
            System.out.println("Gagal terhubung ke database: " + e.getMessage());
        }
    }

    public App() throws SQLException {
        connect();
        initialize();
        pasienList = new ArrayList<>();
        createTable();
    }

    private void initialize() throws SQLException {

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

        tanggalLahirPicker = new JTextField();
        panel.add(tanggalLahirPicker);
        tanggalLahirPicker.setColumns(10);

        JButton btnTambah = new JButton("Tambah");
        panel.add(btnTambah);
        btnTambah.addActionListener(e -> tambahPasien());

        JButton btnUpdate = new JButton("Update");
        panel.add(btnUpdate);
        btnUpdate.addActionListener(e -> {
            try {
                updatePasien();
            } catch (ParseException ex) {
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
                    tampilkanDaftarPasien();
                } catch (SQLException ex) {
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
                } catch (SQLException ex) {
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
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton btnSesudah = new JButton("Sesudahnya");
        panel.add(btnSesudah);
        btnSesudah.addActionListener(e -> {
            try {
                navigateRecord(true);
            } catch (SQLException ex) {
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



        JScrollPane scrollPane = new JScrollPane();
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        tampilkanKembaliSemuaPasien();
        scrollPane.setViewportView(table);
    }

    private void navigateRecord(boolean next) throws SQLException {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            appUtils.showAlertDialog(frame, "Error", "Tidak ada pasien yang dipilih.");
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

            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MMM-dd");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date tanggalLahir_date = null; // Mengonversi teks menjadi objek Date
            try {
                tanggalLahir_date = inputDateFormat.parse(table.getValueAt(selectedRow, 3).toString());
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
            String formattedTanggalLahir = outputDateFormat.format(tanggalLahir_date); // Memformat tanggal
            tanggalLahirPicker.setText(formattedTanggalLahir);
        }
    }
    private void tampilkanData() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM pasien");

            tableModel.setRowCount(0); // Menghapus semua baris pada tabel

            int no = 1;
            while (resultSet.next()) {
                String nama = resultSet.getString("nama");
                String alamat = resultSet.getString("alamat");
                String nik = resultSet.getString("nik");
                String tanggal_asal = resultSet.getString("tanggal_lahir");
                String tanggalLahir = appUtils.formatDate(tanggal_asal);
                tableModel.addRow(new Object[]{no++, nama, nik, tanggalLahir, alamat});
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Gagal menampilkan data pasien: " + e.getMessage());
        }
    }

    private void tampilkanKembaliSemuaPasien() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM pasien");
            tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"No", "Nama Pasien", "NIK", "Tanggal Lahir", "Alamat"});
            table = new JTable(tableModel);
            int no = 1;
            while (resultSet.next()) {
                String nama = resultSet.getString("nama");
                String alamat = resultSet.getString("alamat");
                String nik = resultSet.getString("nik");
                String tanggal_asal = resultSet.getString("tanggal_lahir");
                String tanggalLahir = appUtils.formatDate(tanggal_asal);
                tableModel.addRow(new Object[]{no++, nama, nik, tanggalLahir, alamat});
            }

            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        // Mengambil nilai dari baris yang dipilih
                        String nama = (String) tableModel.getValueAt(selectedRow, 1);
                        String alamat = (String) tableModel.getValueAt(selectedRow, 4);
                        String nik = (String) tableModel.getValueAt(selectedRow, 2);
                        String tanggalLahir_ori = (String) tableModel.getValueAt(selectedRow, 3);

                        // Mengisi nilai ke dalam JTextField
                        namaField.setText(nama);
                        alamatField.setText(alamat);
                        nikField.setText(nik);

                        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MMM-dd");
                        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date tanggalLahir_date = null; // Mengonversi teks menjadi objek Date
                        try {
                            tanggalLahir_date = inputDateFormat.parse(tanggalLahir_ori);
                        } catch (ParseException ex) {
                            throw new RuntimeException(ex);
                        }
                        String formattedTanggalLahir = outputDateFormat.format(tanggalLahir_date); // Memformat tanggal
                        tanggalLahirPicker.setText(formattedTanggalLahir);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void cariPasien(String keyword) throws SQLException {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM pasien WHERE nik LIKE ? OR nama LIKE ?"
            );
            statement.setString(1, "%" + keyword + "%");
            statement.setString(2, "%" + keyword + "%");
            ResultSet resultSet = statement.executeQuery();

            tableModel.setRowCount(0); // Menghapus semua baris pada tabel

            int no = 1;
            while (resultSet.next()) {
                String nama = resultSet.getString("nama");
                String alamat = resultSet.getString("alamat");
                String nik = resultSet.getString("nik");
                String tanggal_asal = resultSet.getString("tanggal_lahir");
                String tanggalLahir = appUtils.formatDate(tanggal_asal);
                tableModel.addRow(new Object[]{no++, nama, nik, tanggalLahir, alamat});
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Gagal mencari pasien: " + e.getMessage());
        }
    }

    private void createTable() {
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS pasien (nama VARCHAR(20), alamat VARCHAR(50), nik VARCHAR(15), tanggal_lahir DATE)";
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            System.out.println("Gagal membuat tabel: " + e.getMessage());
        }
    }

    private void tambahPasien() {
        try {
            String nama = namaField.getText();
            String alamat = alamatField.getText();
            String nik = nikField.getText();
            String tanggalLahirString = tanggalLahirPicker.getText(); // Mengambil teks dari JTextField
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date tanggalLahir = inputDateFormat.parse(tanggalLahirString); // Mengonversi teks menjadi objek Date
            String formattedTanggalLahir = outputDateFormat.format(tanggalLahir); // Memformat tanggal

            if (nama.isEmpty() || alamat.isEmpty() || nik.isEmpty() || formattedTanggalLahir.isEmpty()) {
                appUtils.showAlertDialog(frame, "Error", "Mohon lengkapi semua field.");
                return;
            }


            PreparedStatement checkStatement = connection.prepareStatement(
                    "SELECT * FROM pasien WHERE nik = ?"
            );

            checkStatement.setString(1, nik);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()) {
                appUtils.showAlertDialog(frame, "Error", "NIK telah digunakan. Silakan masukkan NIK yang berbeda.");
                resultSet.close();
                checkStatement.close();
                return;
            }
            resultSet.close();
            checkStatement.close();

            Pasien newPasien = new Pasien(nama, alamat, nik, formattedTanggalLahir);

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO pasien (nama, alamat, nik, tanggal_lahir) VALUES (?, ?, ?, ?)"
            );
            statement.setString(1, newPasien.getNama());
            statement.setString(2, newPasien.getAlamat());
            statement.setString(3, newPasien.getNik());
            statement.setString(4, newPasien.getTanggalLahir());
            String tanggal_asal = newPasien.getTanggalLahir();
            String tanggalLahirFinal = appUtils.formatDate(tanggal_asal);

            statement.executeUpdate();

            pasienList.add(newPasien);
            tableModel.addRow(new Object[]{pasienList.size(), nama, nik, tanggalLahirFinal, alamat});
            clearForm();

            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePasien() throws ParseException {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            appUtils.showAlertDialog(frame, "Error", "Tidak ada pasien yang dipilih.");
            return;
        }

        String nama = namaField.getText();
        String alamat = alamatField.getText();
        String nik = nikField.getText();
        String tanggalLahirString = tanggalLahirPicker.getText(); // Mengambil teks dari JTextField
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date formattedTanggalLahir = inputDateFormat.parse(tanggalLahirString); // Mengonversi teks menjadi objek Date
        String tanggalLahir = outputDateFormat.format(formattedTanggalLahir); // Memformat tanggal

        if (nama.isEmpty() || alamat.isEmpty() || nik.isEmpty() || tanggalLahir.isEmpty()) {
            appUtils.showAlertDialog(frame, "Error", "Mohon lengkapi semua field.");
            return;
        }
        String nikData = (String) tableModel.getValueAt(selectedRow, 2); // Ambil NIK dari baris yang dipilih

        try {
            PreparedStatement checkStatement = connection.prepareStatement(
                    "SELECT nik FROM pasien WHERE nik = ? AND nik != ?"
            );
            checkStatement.setString(1, nik);
            checkStatement.setString(2, nikData);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                appUtils.showAlertDialog(frame, "Error", "NIK pasien sudah ada dalam database.");
                resultSet.close();
                checkStatement.close();
                return;
            }

            Pasien updatedPasien = new Pasien(nama, alamat, nik, tanggalLahir);

            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE pasien SET nama = ?, alamat = ?, tanggal_lahir = ? WHERE nik = ?"
            );
            statement.setString(1, updatedPasien.getNama());
            statement.setString(2, updatedPasien.getAlamat());
            statement.setString(3, updatedPasien.getTanggalLahir());
            statement.setString(4, nikData);
            statement.executeUpdate();

            tableModel.setValueAt(nama, selectedRow, 1);
            tableModel.setValueAt(nik, selectedRow, 2);

            String tanggalLahirFinal = appUtils.formatDate(tanggalLahir);
            tableModel.setValueAt(tanggalLahirFinal, selectedRow, 3);
            tableModel.setValueAt(alamat, selectedRow, 4);
            clearForm();

            resultSet.close();
            checkStatement.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hapusPasien() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            appUtils.showAlertDialog(frame, "Error", "Tidak ada pasien yang dipilih.");
            return;
        }

        int confirmResult = JOptionPane.showConfirmDialog(frame, "Apakah Anda yakin ingin menghapus pasien ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirmResult == JOptionPane.YES_OPTION) {
            try {
                String nik = (String) tableModel.getValueAt(selectedRow, 2); // Ambil NIK dari baris yang dipilih
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM pasien WHERE nik = ?"
                );
                statement.setString(1, nik);
                statement.executeUpdate();
                tableModel.removeRow(selectedRow);
                clearForm();
                statement.close();
            } catch (SQLException e) {
                System.out.println("Gagal menghapus data pasien: " + e.getMessage());
            }
        }
    }

    private void tampilkanDaftarPasien() throws SQLException {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM pasien");

            JFrame daftarPasienFrame = new JFrame();
            daftarPasienFrame.setTitle("Daftar Pasien");
            daftarPasienFrame.setBounds(100, 100, 500, 300);
            daftarPasienFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JScrollPane scrollPane = new JScrollPane();
            daftarPasienFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);

            JTable daftarPasienTable = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"No", "Nama Pasien", "NIK", "Tanggal Lahir", "Alamat"}));
            DefaultTableModel daftarPasienTableModel = (DefaultTableModel) daftarPasienTable.getModel();

            int no = 1;
            while (resultSet.next()) {
                String nama = resultSet.getString("nama");
                String alamat = resultSet.getString("alamat");
                String nik = resultSet.getString("nik");
                String tanggal_asal = resultSet.getString("tanggal_lahir");
                String tanggalLahir = appUtils.formatDate(tanggal_asal);
                daftarPasienTableModel.addRow(new Object[]{no++, nama, nik, tanggalLahir, alamat});
            }

            scrollPane.setViewportView(daftarPasienTable);

            resultSet.close();
            statement.close();

            daftarPasienFrame.setVisible(true);
        } catch (SQLException e) {
            System.out.println("Gagal menampilkan daftar pasien: " + e.getMessage());
        }
    }


    private void clearForm() {
        namaField.setText("");
        alamatField.setText("");
        nikField.setText("");
        tanggalLahirPicker.setText("");
        table.clearSelection();
    }
}
