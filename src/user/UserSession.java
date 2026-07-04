package user;

public class UserSession {
    private static String namaPetugas;

    public static void setNamaPetugas(String nama) {
        namaPetugas = nama;
    }

    public static String getNamaPetugas() {
        return namaPetugas;
    }
}
