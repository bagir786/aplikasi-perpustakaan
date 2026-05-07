package master;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;
import java.sql.*;
import koneksi.koneksi;

/**
 *
 * @author Antigravity
 */
public class Fbuku extends javax.swing.JFrame {

    /**
     * Creates new form Fbuku
     */
    private String imagePath = null;
    private DefaultTableModel model;

    // Pagination variables
    private int currentPage = 1;
    private int pageSize = 5;
    private int totalPages = 1;
    private java.util.List<Object[]> filteredData = new java.util.ArrayList<>();

    public Fbuku() {
        initComponents();
        setIcons();
        initPagination();
        initActionListeners();
        populateKategori();
        setupDynamicPageSize();
        tampilData("");
        styleComponents();

        // Form dimulai dalam keadaan kosong (user harus pilih data di tabel dulu)
        clearForm();
    }

    private void setupDynamicPageSize() {
        jScrollPane1.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                calculatePageSize();
            }
        });
    }

    private void calculatePageSize() {
        int rowHeight = tblBuku.getRowHeight();
        if (rowHeight <= 0)
            rowHeight = 35; // Default if not set yet

        int availableHeight = jScrollPane1.getViewport().getHeight();
        if (availableHeight > 0) {
            pageSize = Math.max(1, availableHeight / rowHeight);
            tampilData(txtCari.getText());
        }
    }

    private void initPagination() {
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

    private void styleComponents() {
        // Table Header Styling - Matching Image Reference (Light Blue Background, Dark
        // Blue Text)
        tblBuku.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                javax.swing.JLabel label = (javax.swing.JLabel) super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                label.setBackground(new java.awt.Color(234, 241, 248)); // Light blue/gray background
                label.setForeground(new java.awt.Color(0, 51, 102)); // Dark blue text
                label.setFont(new java.awt.Font("Tahoma", 1, 12));
                label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                label.setBorder(
                        javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 1, new java.awt.Color(204, 204, 204)));
                return label;
            }
        });
        tblBuku.setRowHeight(35); // Increased row height for better readability
        tblBuku.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 35));
        tblBuku.setGridColor(new java.awt.Color(230, 230, 230));
        tblBuku.setSelectionBackground(new java.awt.Color(235, 245, 255));
        tblBuku.setSelectionForeground(java.awt.Color.BLACK);

        // Adjust Column Widths
        tblBuku.getColumnModel().getColumn(0).setPreferredWidth(80); // ID Buku
        tblBuku.getColumnModel().getColumn(1).setPreferredWidth(300); // Judul Buku (Widened)
        tblBuku.getColumnModel().getColumn(2).setPreferredWidth(150); // Pengarang
        tblBuku.getColumnModel().getColumn(3).setPreferredWidth(150); // Penerbit
        tblBuku.getColumnModel().getColumn(4).setPreferredWidth(60); // Tahun
        tblBuku.getColumnModel().getColumn(5).setPreferredWidth(100); // Kategori
        tblBuku.getColumnModel().getColumn(6).setPreferredWidth(50); // Stok (Narrowed)

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Search TextField Styling
        txtCari.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)),
                javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // Cari Button Styling - Matching Image (Blue Background)
        btnCari.setBackground(new java.awt.Color(0, 102, 204));
        btnCari.setForeground(java.awt.Color.WHITE);
        btnCari.setFont(new java.awt.Font("Tahoma", 1, 12));
        btnCari.setFocusPainted(false);
        btnCari.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // Search Placeholder Logic
        txtCari.setText("Cari Judul / Pengarang...");
        txtCari.setForeground(java.awt.Color.GRAY);
        txtCari.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtCari.getText().equals("Cari Judul / Pengarang...")) {
                    txtCari.setText("");
                    txtCari.setForeground(java.awt.Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtCari.getText().isEmpty()) {
                    txtCari.setText("Cari Judul / Pengarang...");
                    txtCari.setForeground(java.awt.Color.GRAY);
                }
            }
        });

        // Pagination Styling - Matching Image
        javax.swing.JButton[] pageBtns = { btnFirst, btnPrev, btnNext, btnLast };
        for (javax.swing.JButton btn : pageBtns) {
            btn.setFocusPainted(false);
            btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            btn.setPreferredSize(new java.awt.Dimension(35, 30));
        }

        pnlPages.setBackground(new java.awt.Color(245, 247, 247));
        ((java.awt.FlowLayout) pnlPages.getLayout()).setHgap(5);
        ((java.awt.FlowLayout) pnlPages.getLayout()).setVgap(0);

        // Form Fields Styling
        JTextField[] fields = { txtIdBuku, txtJudul, txtPengarang, txtPenerbit, txtTahun, txtStok };
        for (JTextField f : fields) {
            f.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)),
                    javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        }
        cbKategori.setBackground(java.awt.Color.WHITE);
        txtIdBuku.setEditable(false);

        // Cover Panel Styling
        pnlCover.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)),
                "Cover Buku",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 1, 11)));

        // Action Buttons Styling
        JButton[] actionBtns = { btnSimpan, btnEdit, btnHapus, btnClear, btnBatal };
        Color[] btnColors = {
                new Color(0, 120, 242), // Simpan: Blue
                new Color(255, 165, 0), // Edit: Orange
                new Color(220, 53, 69), // Hapus: Red
                new Color(0, 153, 153), // Clear: Teal/Cyan
                new Color(108, 117, 125) // Batal: Gray
        };

        for (int i = 0; i < actionBtns.length; i++) {
            actionBtns[i].setBackground(btnColors[i]);
            actionBtns[i].setForeground(Color.WHITE);
            actionBtns[i].setFont(new Font("Tahoma", Font.BOLD, 11));
            actionBtns[i].setFocusPainted(false);
            actionBtns[i].setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            actionBtns[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        // Numerical Validation for Tahun and Stok
        txtTahun.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) || txtTahun.getText().length() >= 4) {
                    e.consume();
                }
            }
        });

        txtStok.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
    }

    private void setIcons() {
        jLabelIcon.setIcon(resizeIcon("/assets/table-list.png", 24, 24));
    }

    private ImageIcon resizeIcon(String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabelIcon = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtIdBuku = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtJudul = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtPengarang = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtPenerbit = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTahun = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cbKategori = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        txtStok = new javax.swing.JTextField();
        pnlCover = new javax.swing.JPanel();
        lblCover = new javax.swing.JLabel();
        btnSimpan = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBuku = new javax.swing.JTable();
        btnFirst = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();
        pnlPages = new javax.swing.JPanel();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        lblTotalData = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Data Buku");

        jPanel1.setBackground(new java.awt.Color(51, 51, 255));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Sistem Informasi Manajemen Perpustakaan");

        jPanel2.setBackground(new java.awt.Color(245, 247, 247));

        jLabel2.setText("ID Buku");

        jLabel3.setText("Judul Buku");

        jLabel4.setText("Pengarang");

        jLabel5.setText("Penerbit");

        jLabel6.setText("Tahun Terbit");

        jLabel7.setText("Kategori");

        cbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Pilih Kategori --" }));

        jLabel11.setText("Stok");

        pnlCover.setBackground(new java.awt.Color(255, 255, 255));
        pnlCover.setBorder(javax.swing.BorderFactory.createTitledBorder("Cover Buku"));

        lblCover.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCover.setText("No Image");
        lblCover.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblCover.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCoverMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlCoverLayout = new javax.swing.GroupLayout(pnlCover);
        pnlCover.setLayout(pnlCoverLayout);
        pnlCoverLayout.setHorizontalGroup(
                pnlCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblCover, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE));
        pnlCoverLayout.setVerticalGroup(
                pnlCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblCover, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE));

        btnSimpan.setBackground(new java.awt.Color(0, 120, 242));
        btnSimpan.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setText("Simpan");

        btnEdit.setBackground(new java.awt.Color(255, 165, 0));
        btnEdit.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnEdit.setText("Edit");

        btnHapus.setBackground(new java.awt.Color(220, 53, 69));
        btnHapus.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setText("Hapus");

        btnClear.setText("Clear");

        btnBatal.setBackground(new java.awt.Color(108, 117, 125));
        btnBatal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnBatal.setForeground(new java.awt.Color(255, 255, 255));
        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setText("Data Buku");

        btnCari.setBackground(new java.awt.Color(0, 102, 206));
        btnCari.setForeground(new java.awt.Color(255, 255, 255));
        btnCari.setText("Cari");

        tblBuku.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {

                },
                new String[] {
                        "ID Buku", "Judul Buku", "Pengarang", "Penerbit", "Tahun", "Kategori", "Stok"
                }) {
            boolean[] canEdit = new boolean[] {
                    false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblBuku);

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
                                .addGap(25, 25, 25)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addGroup(jPanel2Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel2)
                                                                        .addComponent(jLabel3)
                                                                        .addComponent(jLabel4)
                                                                        .addComponent(jLabel5)
                                                                        .addComponent(jLabel6)
                                                                        .addComponent(jLabel7)
                                                                        .addComponent(jLabel11))
                                                                .addGap(20, 20, 20)
                                                                .addGroup(jPanel2Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(txtIdBuku,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                250, Short.MAX_VALUE)
                                                                        .addComponent(txtJudul,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                250, Short.MAX_VALUE)
                                                                        .addComponent(txtPengarang,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                250, Short.MAX_VALUE)
                                                                        .addComponent(txtPenerbit,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                250, Short.MAX_VALUE)
                                                                        .addComponent(txtTahun,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                250, Short.MAX_VALUE)
                                                                        .addComponent(cbKategori, 0, 250,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(txtStok,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                100,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addComponent(jLabel8))
                                                .addGap(30, 30, 30)
                                                .addComponent(pnlCover, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50,
                                                        Short.MAX_VALUE)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addComponent(btnSimpan, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnEdit, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnHapus, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnBatal, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                120, Short.MAX_VALUE)))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(txtCari, javax.swing.GroupLayout.DEFAULT_SIZE, 400,
                                                        Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 100,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout
                                                .createSequentialGroup()
                                                .addComponent(btnFirst)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnPrev)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(pnlPages, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnNext)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnLast)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(lblTotalData)))
                                .addGap(25, 25, 25)));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel8)
                                .addGap(20, 20, 20)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel2)
                                                        .addComponent(txtIdBuku, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel3)
                                                        .addComponent(txtJudul, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel4)
                                                        .addComponent(txtPengarang,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel5)
                                                        .addComponent(txtPenerbit,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel6)
                                                        .addComponent(txtTahun, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel7)
                                                        .addComponent(cbKategori,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel11)
                                                        .addComponent(txtStok, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(pnlCover, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(25, 25, 25)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnFirst)
                                        .addComponent(btnPrev)
                                        .addComponent(pnlPages, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnNext)
                                        .addComponent(btnLast)
                                        .addComponent(lblTotalData))
                                .addGap(20, 20, 20)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(jLabelIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 24,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)
                                .addContainerGap(570, Short.MAX_VALUE)));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel1Layout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabelIcon, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(10, 10, 10)
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnBatalActionPerformed
        // Kembali ke Dashboard
        new tampilanawal.dashboard().setVisible(true);
        this.dispose();
    }// GEN-LAST:event_btnBatalActionPerformed

    private void lblCoverMouseClicked(java.awt.event.MouseEvent evt) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Gambar (JPG, PNG, JPEG)", "jpg", "png", "jpeg");
        chooser.setFileFilter(filter);

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            imagePath = file.getAbsolutePath();

            try {
                // Scale image to fit lblCover (140x180)
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage().getScaledInstance(lblCover.getWidth(), lblCover.getHeight(),
                        Image.SCALE_SMOOTH);
                lblCover.setIcon(new ImageIcon(img));
                lblCover.setText(""); // Remove "No Image" text
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal memuat gambar: " + e.getMessage());
            }
        }
    }

    private void tampilData(String cari) {
        filteredData.clear();
        String sql;
        if (cari.equals("Cari Judul / Pengarang...") || cari.isEmpty()) {
            sql = "SELECT id_buku, judul_buku, pengarang, penerbit, tahun_terbit, kategori, stok, cover FROM buku";
        } else {
            sql = "SELECT id_buku, judul_buku, pengarang, penerbit, tahun_terbit, kategori, stok, cover FROM buku "
                    + "WHERE id_buku LIKE ? OR judul_buku LIKE ? OR pengarang LIKE ? "
                    + "OR penerbit LIKE ? OR tahun_terbit LIKE ? OR kategori LIKE ? OR stok LIKE ?";
        }

        try (Connection conn = koneksi.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            if (!cari.equals("Cari Judul / Pengarang...") && !cari.isEmpty()) {
                String p = "%" + cari + "%";
                for (int i = 1; i <= 7; i++)
                    pst.setString(i, p);
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                filteredData.add(new Object[] {
                        rs.getString("id_buku"),
                        rs.getString("judul_buku"),
                        rs.getString("pengarang"),
                        rs.getString("penerbit"),
                        rs.getString("tahun_terbit"),
                        rs.getString("kategori"),
                        rs.getString("stok"),
                        rs.getString("cover")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
        }

        totalPages = (int) Math.ceil((double) filteredData.size() / pageSize);
        if (totalPages == 0)
            totalPages = 1;
        currentPage = 1;

        updateTable();
    }

    private void autonumber() {
        try (Connection conn = koneksi.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT id_buku FROM buku ORDER BY id_buku DESC LIMIT 1")) {

            if (rs.next()) {
                String lastId = rs.getString("id_buku");
                // Mengambil hanya angka dari ID terakhir
                String numberPart = lastId.replaceAll("[^0-9]", "");

                if (numberPart.isEmpty()) {
                    txtIdBuku.setText("B001");
                } else {
                    int num = Integer.parseInt(numberPart) + 1;
                    txtIdBuku.setText(String.format("B%03d", num));
                }
            } else {
                txtIdBuku.setText("B001");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal generate ID: " + e.getMessage());
        }
    }

    private void fillForm(int rowIndex) {
        int dataIndex = (currentPage - 1) * pageSize + rowIndex;
        if (dataIndex >= filteredData.size())
            return;

        Object[] rowData = filteredData.get(dataIndex);

        txtIdBuku.setText(getString(rowData[0]));
        txtJudul.setText(getString(rowData[1]));
        txtPengarang.setText(getString(rowData[2]));
        txtPenerbit.setText(getString(rowData[3]));
        txtTahun.setText(getString(rowData[4]));
        cbKategori.setSelectedItem(getString(rowData[5]));
        txtStok.setText(getString(rowData[6]));

        // Load Cover Image
        imagePath = getString(rowData[7]);
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File file = new File(imagePath);
                if (file.exists()) {
                    ImageIcon icon = new ImageIcon(imagePath);
                    Image img = icon.getImage().getScaledInstance(lblCover.getWidth(), lblCover.getHeight(),
                            Image.SCALE_SMOOTH);
                    lblCover.setIcon(new ImageIcon(img));
                    lblCover.setText("");
                } else {
                    lblCover.setIcon(null);
                    lblCover.setText("No Image");
                }
            } catch (Exception e) {
                lblCover.setIcon(null);
                lblCover.setText("No Image");
            }
        } else {
            lblCover.setIcon(null);
            lblCover.setText("No Image");
        }
    }

    // Fungsi pembantu agar tidak error NullPointerException
    private String getString(Object obj) {
        return (obj == null) ? "" : obj.toString();
    }

    private void populateKategori() {
        String[] categories = { "Teknik", "Seni", "Filsafat", "Sastra", "Fantasi", "Sejarah", "Psikologi", "Bisnis" };
        cbKategori.removeAllItems();
        cbKategori.addItem("-- Pilih Kategori --");
        for (String cat : categories) {
            cbKategori.addItem(cat);
        }
    }

    private void clearForm() {
        txtIdBuku.setText("");
        txtJudul.setText("");
        txtPengarang.setText("");
        txtPenerbit.setText("");
        txtTahun.setText("");
        cbKategori.setSelectedIndex(0);
        txtStok.setText("");
        imagePath = null;
        lblCover.setIcon(null);
        lblCover.setText("No Image");
        autonumber();
        txtJudul.requestFocus();
    }

    private void initActionListeners() {
        tblBuku.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tblBuku.getSelectedRow();
                fillForm(row);
            }
        });

        btnClear.addActionListener(e -> {
            clearForm();
        });

        btnSimpan.addActionListener(e -> {
            String judul = txtJudul.getText();
            String pengarang = txtPengarang.getText();
            String penerbit = txtPenerbit.getText();
            String tahun = txtTahun.getText();
            String kategori = cbKategori.getSelectedItem().toString();
            String stok = txtStok.getText();

            if (judul.isEmpty() || pengarang.isEmpty() || penerbit.isEmpty() || tahun.isEmpty()
                    || kategori.equals("-- Pilih Kategori --") || stok.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Harap lengkapi semua data!");
                return;
            }

            if (tahun.length() != 4) {
                JOptionPane.showMessageDialog(this, "Format Tahun tidak valid! Gunakan format YYYY (contoh: 2026)");
                txtTahun.requestFocus();
                return;
            }

            String sql = "INSERT INTO buku (id_buku, judul_buku, pengarang, penerbit, tahun_terbit, kategori, stok, cover) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = koneksi.getConnection();
                    PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, txtIdBuku.getText());
                pst.setString(2, judul);
                pst.setString(3, pengarang);
                pst.setString(4, penerbit);
                pst.setString(5, tahun);
                pst.setString(6, kategori);
                pst.setString(7, stok);
                pst.setString(8, imagePath);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data Berhasil Disimpan");
                tampilData("");
                clearForm();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal simpan data: " + ex.getMessage());
            }
        });

        btnEdit.addActionListener(e -> {
            String id = txtIdBuku.getText();
            String judul = txtJudul.getText();
            String pengarang = txtPengarang.getText();
            String penerbit = txtPenerbit.getText();
            String tahun = txtTahun.getText();
            String kategori = cbKategori.getSelectedItem().toString();
            String stok = txtStok.getText();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih data yang akan diedit!");
                return;
            }

            if (tahun.length() != 4) {
                JOptionPane.showMessageDialog(this, "Format Tahun tidak valid! Gunakan format YYYY (contoh: 2026)");
                txtTahun.requestFocus();
                return;
            }

            String sql = "UPDATE buku SET judul_buku=?, pengarang=?, penerbit=?, tahun_terbit=?, kategori=?, stok=?, cover=? WHERE id_buku=?";
            try (Connection conn = koneksi.getConnection();
                    PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, judul);
                pst.setString(2, pengarang);
                pst.setString(3, penerbit);
                pst.setString(4, tahun);
                pst.setString(5, kategori);
                pst.setString(6, stok);
                pst.setString(7, imagePath);
                pst.setString(8, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data Berhasil Diperbarui");
                tampilData("");
                clearForm();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal update data: " + ex.getMessage());
            }
        });

        btnHapus.addActionListener(e -> {
            String id = txtIdBuku.getText();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Hapus data buku dengan ID: " + id + "?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM buku WHERE id_buku=?";
                try (Connection conn = koneksi.getConnection();
                        PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, id);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Data Berhasil Dihapus");
                    tampilData("");
                    clearForm();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Gagal hapus data: " + ex.getMessage());
                }
            }
        });

        btnCari.addActionListener(e -> tampilData(txtCari.getText()));

        // Real-time Search Listener
        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                tampilData(txtCari.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                tampilData(txtCari.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                tampilData(txtCari.getText());
            }
        });
    }

    private void updateTable() {
        model = (DefaultTableModel) tblBuku.getModel();
        model.setRowCount(0);

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, filteredData.size());

        for (int i = start; i < end; i++) {
            model.addRow(filteredData.get(i));
        }

        // Fill empty rows to maintain table height if needed
        while (model.getRowCount() < pageSize) {
            model.addRow(new Object[] { "", "", "", "", "", "", "" });
        }

        renderPaginationButtons();
        lblTotalData.setText("Total Data: " + filteredData.size());

        // Enable/Disable buttons based on page
        btnFirst.setEnabled(currentPage > 1);
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
        btnLast.setEnabled(currentPage < totalPages);
    }

    private void renderPaginationButtons() {
        pnlPages.removeAll();

        int maxVisiblePages = 5;
        int startPage = Math.max(1, currentPage - (maxVisiblePages / 2));
        int endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

        if (endPage - startPage + 1 < maxVisiblePages) {
            startPage = Math.max(1, endPage - maxVisiblePages + 1);
        }

        for (int i = startPage; i <= endPage; i++) {
            final int page = i;
            JButton btn = new JButton(String.valueOf(i));
            btn.setPreferredSize(new java.awt.Dimension(35, 30));
            btn.setFont(new java.awt.Font("Tahoma", 1, 11));
            btn.setFocusPainted(false);
            btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

            if (i == currentPage) {
                btn.setFont(new java.awt.Font("Tahoma", 1, 11));
                btn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
            } else {
                btn.setFont(new java.awt.Font("Tahoma", 0, 11));
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Fbuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Fbuku().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JComboBox<String> cbKategori;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCover;
    private javax.swing.JLabel lblTotalData;
    private javax.swing.JPanel pnlCover;
    private javax.swing.JPanel pnlPages;
    private javax.swing.JTable tblBuku;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtIdBuku;
    private javax.swing.JTextField txtJudul;
    private javax.swing.JTextField txtPenerbit;
    private javax.swing.JTextField txtPengarang;
    private javax.swing.JTextField txtStok;
    private javax.swing.JTextField txtTahun;
    // End of variables declaration//GEN-END:variables
}
