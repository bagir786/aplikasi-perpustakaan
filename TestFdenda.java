import transaksi.Fdenda;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;

public class TestFdenda {
    private static Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    public static void main(String[] args) {
        // Initialize connection to database
        koneksi.koneksi.getConnection();
        
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Instantiating Fdenda...");
                Fdenda f = new Fdenda();
                System.out.println("Form Denda initialized.");

                JComboBox<String> cbIdPinjam = (JComboBox<String>) getPrivateField(f, "cbIdPinjam");
                JComboBox<String> cbIdAnggota = (JComboBox<String>) getPrivateField(f, "cbIdAnggota");
                JTextField txtTerlambat = (JTextField) getPrivateField(f, "txtTerlambat");
                JTextField txtTglKembali = (JTextField) getPrivateField(f, "txtTglKembali");
                JTextField txtTotalDenda = (JTextField) getPrivateField(f, "txtTotalDenda");
                JTable tabelData = (JTable) getPrivateField(f, "tabelData");

                System.out.println("cbIdPinjam item count: " + cbIdPinjam.getItemCount());
                System.out.println("cbIdAnggota item count: " + cbIdAnggota.getItemCount());
                
                // Simulate selecting ID Pinjam: P001
                System.out.println("\n--- Selecting P001 ---");
                cbIdPinjam.setSelectedItem("P001");
                
                System.out.println("cbIdPinjam selected: " + cbIdPinjam.getSelectedItem());
                System.out.println("cbIdAnggota selected: " + cbIdAnggota.getSelectedItem());
                System.out.println("cbIdAnggota enabled: " + cbIdAnggota.isEnabled()); // Expect false
                System.out.println("txtTerlambat: " + txtTerlambat.getText());
                
                // Simulate selecting - Pilih -
                System.out.println("\n--- Selecting - Pilih - ---");
                cbIdPinjam.setSelectedIndex(0);
                System.out.println("cbIdAnggota enabled: " + cbIdAnggota.isEnabled()); // Expect true
                
                // Test Fallback Scenario via Table selection
                System.out.println("\n--- Simulating JTable Selection Fallback ---");
                // Let's populate the table model with a test row
                DefaultTableModel model = (DefaultTableModel) tabelData.getModel();
                model.setRowCount(0); // clear existing
                // Row columns: [No, ID Denda, ID Pinjam, Nama Anggota, Terlambat, Total Denda]
                model.addRow(new Object[]{"1", "D001", "P003", "Alip", "2", "Rp 2,000"});
                tabelData.setRowSelectionInterval(0, 0);
                
                // Trigger mouse clicked event on the table
                for (java.awt.event.MouseListener ml : tabelData.getMouseListeners()) {
                    if (ml.getClass().getName().contains("MouseAdapter") || ml.getClass().getName().contains("Fdenda$")) {
                        MouseEvent clickEvent = new MouseEvent(
                            tabelData, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
                            0, 0, 0, 1, false
                        );
                        ml.mouseClicked(clickEvent);
                    }
                }
                
                System.out.println("After Table Selection:");
                System.out.println("cbIdPinjam selected: " + cbIdPinjam.getSelectedItem());
                System.out.println("cbIdAnggota selected: " + cbIdAnggota.getSelectedItem()); // Expect A002 - Alip
                System.out.println("cbIdAnggota enabled: " + cbIdAnggota.isEnabled()); // Expect false (locked)
                
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
