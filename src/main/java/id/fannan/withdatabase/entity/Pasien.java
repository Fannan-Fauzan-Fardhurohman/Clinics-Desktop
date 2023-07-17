package id.fannan.withdatabase.entity;

public class Pasien {
    private String nama;
    private String alamat;
    private String nik;
    private String tanggalLahir;

    public Pasien(String nama, String alamat, String nik, String tanggalLahir) {
        this.nama = nama;
        this.alamat = alamat;
        this.nik = nik;
        this.tanggalLahir = tanggalLahir;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(String tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }
}