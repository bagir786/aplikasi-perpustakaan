/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import koneksi.koneksi;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;



/**
 *
 * @author VIO-VIO
 */
public class DataAnggota extends javax.swing.JFrame {
    Connection conn;
    Statement st;
    ResultSet rs;

    DefaultTableModel model;

    // Pagination variables
    private int currentPage = 1;
    private int pageSize = 5;
    private int totalPages = 1;
    private java.util.List<Object[]> filteredData = new java.util.ArrayList<>();

    /**
     * Creates new form DataAnggota
     */
    public DataAnggota() {
        
        initComponents();
        conn = koneksi.getConnection();
        initPagination();
        styleComponents();
        setLocationRelativeTo(null);
        tabelData.setShowGrid(false);

        tabelData.setIntercellSpacing(
        new Dimension(0,0)
        );
        setupDynamicPageSize();
        initActionListeners();
        clearForm();
        tampilData("");
    }
    
    private void setupDynamicPageSize() {
        javax.swing.Timer resizeTimer = new javax.swing.Timer(300, e -> calculatePageSize());
        resizeTimer.setRepeats(false);
        
        jScrollPane3.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                resizeTimer.restart();
            }
        });
    }

    private void calculatePageSize() {
        int rowHeight = tabelData.getRowHeight();
        if (rowHeight <= 0)
            rowHeight = 35; // Default if not set yet

        int availableHeight = jScrollPane3.getViewport().getHeight();
        if (availableHeight > 0) {
            pageSize = Math.max(1, availableHeight / rowHeight);
            tampilData(txtCari.getText());
        }
    }

    private void initPagination() {
        if (btnFirst == null) return;
        btnFirst.addActionListener(e -> {
            currentPage = 1;
            updateTable();
        });
        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateTable();
            }
        });
        btnNext.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                updateTable();
            }
        });
        btnLast.addActionListener(e -> {
            currentPage = totalPages;
            updateTable();
        });
    }

    private void tampilData(String cari){
        filteredData.clear();
        try{
            String sql;
            if(cari.equals("Cari Nama / ID Anggota...") || cari.isEmpty()){
                sql = "SELECT id_anggota, nama_anggota, jenis_kelamin, no_telp, alamat FROM anggota";
            } else {
                sql = "SELECT id_anggota, nama_anggota, jenis_kelamin, no_telp, alamat FROM anggota WHERE id_anggota LIKE ? OR nama_anggota LIKE ? OR alamat LIKE ? OR no_telp LIKE ?";
            }
            
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            if(!cari.equals("Cari Nama / ID Anggota...") && !cari.isEmpty()){
                String p = "%" + cari + "%";
                pst.setString(1, p);
                pst.setString(2, p);
                pst.setString(3, p);
                pst.setString(4, p);
            }
            
            rs = pst.executeQuery();

            while(rs.next()){
                filteredData.add(new Object[]{
                    rs.getString("id_anggota"),
                    rs.getString("nama_anggota"),
                    rs.getString("jenis_kelamin"),
                    rs.getString("no_telp"),
                    rs.getString("alamat")
                });
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Gagal memuat data: " + e.getMessage());
        }

        totalPages = (int) Math.ceil((double) filteredData.size() / pageSize);
        if (totalPages == 0)
            totalPages = 1;
        currentPage = 1;

        updateTable();
    }
    
    private void autonumber() {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id_anggota FROM anggota ORDER BY id_anggota DESC LIMIT 1");

            if (rs.next()) {
                String lastId = rs.getString("id_anggota");
                String numberPart = lastId.replaceAll("[^0-9]", "");

                if (numberPart.isEmpty()) {
                    jTextField1.setText("A001");
                } else {
                    int num = Integer.parseInt(numberPart) + 1;
                    jTextField1.setText(String.format("A%03d", num));
                }
            } else {
                jTextField1.setText("A001");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal generate ID: " + e.getMessage());
        }
    }
    
    private void clearForm() {
        jTextField1.setText("");
        jTextField2.setText("");
        Group1.clearSelection();
        txtNoTelpn.setText("");
        txtAlamat.setText("");
        txtCari.setText("Cari Nama / ID Anggota...");
        txtCari.setForeground(java.awt.Color.GRAY);
        autonumber();
        jTextField2.requestFocus();
    }
    
    private void fillForm(int row) {
        int dataIndex = (currentPage - 1) * pageSize + row;
        if (dataIndex >= filteredData.size())
            return;

        Object[] rowData = filteredData.get(dataIndex);

        jTextField1.setText(getString(rowData[0]));
        jTextField2.setText(getString(rowData[1]));
        
        String jk = getString(rowData[2]);
        if(jk.equals("Laki-laki")) {
            rjk1.setSelected(true);
        } else {
            rjk2.setSelected(true);
        }
        
        txtNoTelpn.setText(getString(rowData[3]));
        txtAlamat.setText(getString(rowData[4]));
    }

    private String getString(Object obj) {
        return (obj == null) ? "" : obj.toString();
    }

    private void updateTable() {
        DefaultTableModel tblModel = (DefaultTableModel) tabelData.getModel();
        tblModel.setRowCount(0);

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, filteredData.size());

        for (int i = start; i < end; i++) {
            tblModel.addRow(filteredData.get(i));
        }

        while (tblModel.getRowCount() < pageSize) {
            tblModel.addRow(new Object[] { "", "", "", "", "" });
        }

        renderPaginationButtons();
        if (lblTotalData != null) {
            lblTotalData.setText("Total Data: " + filteredData.size());
        }

        if (btnFirst != null) {
            btnFirst.setEnabled(currentPage > 1);
            btnPrev.setEnabled(currentPage > 1);
            btnNext.setEnabled(currentPage < totalPages);
            btnLast.setEnabled(currentPage < totalPages);
        }
    }

    private void renderPaginationButtons() {
        if (pnlPages == null) return;
        pnlPages.removeAll();

        int maxVisiblePages = 5;
        int startPage = Math.max(1, currentPage - (maxVisiblePages / 2));
        int endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

        if (endPage - startPage + 1 < maxVisiblePages) {
            startPage = Math.max(1, endPage - maxVisiblePages + 1);
        }

        for (int i = startPage; i <= endPage; i++) {
            final int page = i;
            javax.swing.JButton btn = new javax.swing.JButton(String.valueOf(i));
            btn.setPreferredSize(new java.awt.Dimension(35, 30));
            btn.setFont(new java.awt.Font("Segoe UI", 1, 11));
            btn.setFocusPainted(false);
            btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

            if (i == currentPage) {
                btn.setFont(new java.awt.Font("Segoe UI", 1, 11));
                btn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
            } else {
                btn.setFont(new java.awt.Font("Segoe UI", 0, 11));
                btn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
            }

            btn.addActionListener(e -> {
                currentPage = page;
                updateTable();
            });

            pnlPages.add(btn);
        }

        pnlPages.revalidate();
        pnlPages.repaint();
    }
    
    private void initActionListeners() {
        Group1.add(rjk1);
        Group1.add(rjk2);
        
        tabelData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tabelData.getSelectedRow();
                fillForm(row);
            }
        });

        btnClear.addActionListener(e -> {
            clearForm();
        });

        btnSimpan.addActionListener(e -> {
            String id = jTextField1.getText();
            String nama = jTextField2.getText();
            String jk = "";
            if (rjk1.isSelected()) jk = "Laki-laki";
            if (rjk2.isSelected()) jk = "Perempuan";
            String noTelp = txtNoTelpn.getText();
            String alamat = txtAlamat.getText();

            if (nama.isEmpty() || jk.isEmpty() || noTelp.isEmpty() || alamat.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Harap lengkapi semua data!");
                return;
            }

            try {
                String sql = "INSERT INTO anggota (id_anggota, nama_anggota, jenis_kelamin, alamat, no_telp, tanggal_daftar) VALUES (?, ?, ?, ?, ?, CURDATE())";
                java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, id);
                pst.setString(2, nama);
                pst.setString(3, jk);
                pst.setString(4, alamat);
                pst.setString(5, noTelp);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data Berhasil Disimpan");
                tampilData(txtCari.getText());
                clearForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal simpan data: " + ex.getMessage());
            }
        });

        btnEdit.addActionListener(e -> {
            String id = jTextField1.getText();
            String nama = jTextField2.getText();
            String jk = "";
            if (rjk1.isSelected()) jk = "Laki-laki";
            if (rjk2.isSelected()) jk = "Perempuan";
            String noTelp = txtNoTelpn.getText();
            String alamat = txtAlamat.getText();

            if (nama.isEmpty() || jk.isEmpty() || noTelp.isEmpty() || alamat.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Harap lengkapi semua data!");
                return;
            }

            try {
                String sql = "UPDATE anggota SET nama_anggota=?, jenis_kelamin=?, alamat=?, no_telp=? WHERE id_anggota=?";
                java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, nama);
                pst.setString(2, jk);
                pst.setString(3, alamat);
                pst.setString(4, noTelp);
                pst.setString(5, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data Berhasil Diupdate");
                tampilData(txtCari.getText());
                clearForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal update data: " + ex.getMessage());
            }
        });

        btnHapus.addActionListener(e -> {
            String id = jTextField1.getText();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Apakah anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM anggota WHERE id_anggota=?";
                    java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, id);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Data Berhasil Dihapus");
                    tampilData(txtCari.getText());
                    clearForm();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal hapus data, pastikan anggota ini tidak memiliki riwayat peminjaman. \n" + ex.getMessage());
                }
            }
        });

        btnCari.addActionListener(e -> {
            tampilData(txtCari.getText());
        });
        
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                tampilData(txtCari.getText());
            }
        });
    }
     private void styleComponents() {
        // Table Header Styling - Matching Image Reference (Light Blue Background, Dark
        // Blue Text)
        tabelData.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                javax.swing.JLabel label = (javax.swing.JLabel) super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                label.setBackground(new java.awt.Color(234, 241, 248)); // Light blue/gray background
                label.setForeground(new java.awt.Color(0, 51, 102)); // Dark blue text
                label.setFont(new java.awt.Font("Segoe UI", 1, 12));
                label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                label.setBorder(
                        javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 1, new java.awt.Color(204, 204, 204)));
                return label;
            }
        });
        tabelData.setRowHeight(35); // Increased row height for better readability
        tabelData.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 35));
        tabelData.setGridColor(new java.awt.Color(230, 230, 230));
        tabelData.setSelectionBackground(new java.awt.Color(235, 245, 255));
        tabelData.setSelectionForeground(java.awt.Color.BLACK);
        
        // Adjust Column Widths
        tabelData.getColumnModel().getColumn(0).setPreferredWidth(100); // ID 
        tabelData.getColumnModel().getColumn(1).setPreferredWidth(300); // nama
        tabelData.getColumnModel().getColumn(2).setPreferredWidth(150); // JK
        tabelData.getColumnModel().getColumn(3).setPreferredWidth(180); // No
        tabelData.getColumnModel().getColumn(4).setPreferredWidth(200); // Alamat

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Search Placeholder Logic
        txtCari.setText("Cari Nama / ID Anggota...");
        txtCari.setForeground(java.awt.Color.GRAY);
        txtCari.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtCari.getText().equals("Cari Nama / ID Anggota...")) {
                    txtCari.setText("");
                    txtCari.setForeground(java.awt.Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtCari.getText().isEmpty()) {
                    txtCari.setText("Cari Nama / ID Anggota...");
                    txtCari.setForeground(java.awt.Color.GRAY);
                }
            }
        });
        
        // Validation for No Telp Wali
        txtNoTelpn.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
        
        jTextField1.setEditable(false);

        // Pagination Styling - Matching Image
        if (btnFirst != null && btnPrev != null && btnNext != null && btnLast != null) {
            javax.swing.JButton[] pageBtns = { btnFirst, btnPrev, btnNext, btnLast };
            for (javax.swing.JButton btn : pageBtns) {
                btn.setFocusPainted(false);
                btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                btn.setPreferredSize(new java.awt.Dimension(35, 30));
            }
        }

        if (pnlPages != null) {
            pnlPages.setBackground(new java.awt.Color(245, 247, 247));
            if (pnlPages.getLayout() instanceof java.awt.FlowLayout) {
                ((java.awt.FlowLayout) pnlPages.getLayout()).setHgap(5);
                ((java.awt.FlowLayout) pnlPages.getLayout()).setVgap(0);
            }
        }
     }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        Group1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        rjk1 = new javax.swing.JRadioButton();
        rjk2 = new javax.swing.JRadioButton();
        txtNoTelpn = new javax.swing.JTextField();
        txtCari = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        btnCari = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabelData = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtAlamat = new javax.swing.JTextArea();
        btnFirst = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();
        pnlPages = new javax.swing.JPanel();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        lblTotalData = new javax.swing.JLabel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(99, 102, 241));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/student.png"))); // NOI18N
        jLabel1.setText(" Sistem informasi Manajemen Perpustakaaan");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 691, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
        );

        jPanel2.setBackground(new java.awt.Color(245, 247, 247));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setText("Data Anggota");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Jenis Kelamin");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("No.Telp Wali");

        rjk1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        rjk1.setText("Laki-laki");

        rjk2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        rjk2.setText("Perempuan");

        txtNoTelpn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        txtCari.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCariActionPerformed(evt);
            }
        });

        btnSimpan.setBackground(new java.awt.Color(0, 120, 242));
        btnSimpan.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/save.png"))); // NOI18N
        btnSimpan.setText("Simpan");

        btnEdit.setBackground(new java.awt.Color(255, 165, 0));
        btnEdit.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/file-edit.png"))); // NOI18N
        btnEdit.setText("Edit");

        btnHapus.setBackground(new java.awt.Color(220, 53, 69));
        btnHapus.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/delete-forever.png"))); // NOI18N
        btnHapus.setText("Hapus");

        btnClear.setBackground(new java.awt.Color(0, 152, 155));
        btnClear.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/clear-formatting.png"))); // NOI18N
        btnClear.setText("Clear");

        btnBatal.setBackground(new java.awt.Color(102, 102, 102));
        btnBatal.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnBatal.setForeground(new java.awt.Color(255, 255, 255));
        btnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/arrow-back-up-double.png"))); // NOI18N
        btnBatal.setText("Kembali");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        btnCari.setBackground(new java.awt.Color(0, 102, 206));
        btnCari.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnCari.setForeground(new java.awt.Color(255, 255, 255));
        btnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/book-search.png"))); // NOI18N
        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        tabelData.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tabelData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID Anggota", "Nama Lengkap", "Jenis Kelamin", "No Telp Wali", "Alamat"
            }
        ));
        jScrollPane3.setViewportView(tabelData);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("ID Anggota");

        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Nama Lengkap ");

        jTextField2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Alamat");

        txtAlamat.setColumns(20);
        txtAlamat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtAlamat.setRows(5);
        jScrollPane5.setViewportView(txtAlamat);

        btnFirst.setText("|<");

        btnPrev.setText("<");

        btnNext.setText(">");

        btnLast.setText(">|");

        lblTotalData.setText("Total Data: 0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnFirst)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnPrev)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlPages, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnNext)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnLast)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblTotalData))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6))
                                .addGap(16, 16, 16)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtNoTelpn, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(rjk1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(rjk2))
                                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                                    .addComponent(jTextField2)
                                    .addComponent(jScrollPane5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 150, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnSimpan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnHapus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnBatal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(29, 29, 29))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(47, 47, 47)
                                .addComponent(jLabel9))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rjk1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rjk2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNoTelpn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCari, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCari, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFirst)
                    .addComponent(btnPrev)
                    .addComponent(pnlPages, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNext)
                    .addComponent(btnLast)
                    .addComponent(lblTotalData))
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCariActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        // TODO add your handling code here:
        new tampilanawal.dashboard().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCariActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DataAnggota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DataAnggota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DataAnggota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DataAnggota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
      
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DataAnggota().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup Group1;
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JLabel lblTotalData;
    private javax.swing.JPanel pnlPages;
    private javax.swing.JRadioButton rjk1;
    private javax.swing.JRadioButton rjk2;
    private javax.swing.JTable tabelData;
    private javax.swing.JTextArea txtAlamat;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtNoTelpn;
    // End of variables declaration//GEN-END:variables
}
