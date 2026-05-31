/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;
import koneksi.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ASUS
 */
public class FormPetugas extends javax.swing.JFrame {
    Connection conn;
    Statement st;
    ResultSet rs;

    // Pagination variables
    private int currentPage = 1;
    private int pageSize = 5;
    private int totalPages = 1;
    private java.util.List<Object[]> filteredData = new java.util.ArrayList<>();

    /**
     * Creates new form FormPetugas
     */
    public FormPetugas() {
        initComponents();
        conn = koneksi.getConnection();
        initPagination();
        setLocationRelativeTo(null);
        styleComponents();
        tabelPetugas.setShowGrid(false);
        tabelPetugas.setIntercellSpacing(new java.awt.Dimension(0,0));
        setupDynamicPageSize();
        initActionListeners();
        clearForm();
        tampilData("");
    }

    private void setupDynamicPageSize() {
        javax.swing.Timer resizeTimer = new javax.swing.Timer(300, e -> calculatePageSize());
        resizeTimer.setRepeats(false);
        
        jScrollPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                resizeTimer.restart();
            }
        });
    }

    private void calculatePageSize() {
        int rowHeight = tabelPetugas.getRowHeight();
        if (rowHeight <= 0)
            rowHeight = 35; // Default if not set yet

        int availableHeight = jScrollPane1.getViewport().getHeight();
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

    private void fillForm(int row) {
        int dataIndex = (currentPage - 1) * pageSize + row;
        if (dataIndex >= filteredData.size() || dataIndex < 0)
            return;

        Object[] rowData = filteredData.get(dataIndex);

        jTextField1.setText(getString(rowData[0]));
        jTextField2.setText(getString(rowData[1]));
        jTextField3.setText(getString(rowData[2]));
        jTextField5.setText(getString(rowData[3]));
        
        try {
            String id = getString(rowData[0]);
            PreparedStatement pst = conn.prepareStatement("SELECT password FROM petugas WHERE id_petugas=?");
            pst.setString(1, id);
            ResultSet rsPass = pst.executeQuery();
            if(rsPass.next()) {
                jTextField4.setText(rsPass.getString("password"));
            }
        } catch (Exception e) {
            System.out.println("Error fetching password: " + e.getMessage());
        }
    }

    private String getString(Object obj) {
        return (obj == null) ? "" : obj.toString();
    }

    private void updateTable() {
        DefaultTableModel tblModel = (DefaultTableModel) tabelPetugas.getModel();
        tblModel.setRowCount(0);

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, filteredData.size());

        for (int i = start; i < end; i++) {
            tblModel.addRow(filteredData.get(i));
        }

        while (tblModel.getRowCount() < pageSize) {
            tblModel.addRow(new Object[] { "", "", "", "" });
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

    private void styleComponents() {
        // Table Header Styling - Matching Image Reference (Light Blue Background, Dark
        // Blue Text)
        tabelPetugas.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
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
        tabelPetugas.setRowHeight(35); // Increased row height for better readability
        tabelPetugas.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 35));
        tabelPetugas.setGridColor(new java.awt.Color(230, 230, 230));
        tabelPetugas.setSelectionBackground(new java.awt.Color(235, 245, 255));
        tabelPetugas.setSelectionForeground(java.awt.Color.BLACK);
        
        // Adjust Column Widths
        tabelPetugas.getColumnModel().getColumn(0).setPreferredWidth(100); // ID 
        tabelPetugas.getColumnModel().getColumn(1).setPreferredWidth(300); // nama
        tabelPetugas.getColumnModel().getColumn(2).setPreferredWidth(150); // username
        tabelPetugas.getColumnModel().getColumn(3).setPreferredWidth(180); // No

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Search Placeholder Logic
        txtCari.setText("Cari Nama / ID Petugas...");
        txtCari.setForeground(java.awt.Color.GRAY);
        txtCari.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtCari.getText().equals("Cari Nama / ID Petugas...")) {
                    txtCari.setText("");
                    txtCari.setForeground(java.awt.Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtCari.getText().isEmpty()) {
                    txtCari.setText("Cari Nama / ID Petugas...");
                    txtCari.setForeground(java.awt.Color.GRAY);
                }
            }
        });
        
        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
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
            pnlPages.setBackground(new java.awt.Color(247, 247, 245));
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        txtCari = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelPetugas = new javax.swing.JTable();
        btnFirst = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();
        pnlPages = new javax.swing.JPanel();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        lblTotalData = new javax.swing.JLabel();

        btnFirst.setText("|<");
        btnPrev.setText("<");
        btnNext.setText(">");
        btnLast.setText(">|");
        lblTotalData.setText("Total Data: 0");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(247, 247, 245));

        jPanel2.setBackground(new java.awt.Color(99, 102, 241));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/user-injured.png"))); // NOI18N
        jLabel1.setText(" Sistem Informasi Manajemen Perpustakaan");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setText("Data Petugas");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("ID Petugas");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Nama Petugas");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Username");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Password");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("No. Telp");

        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jTextField2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jTextField3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jTextField4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jTextField5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jButton6.setBackground(new java.awt.Color(40, 167, 69));
        jButton6.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/bxs-book-add.png"))); // NOI18N
        jButton6.setText("Tambah");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(0, 120, 242));
        jButton7.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/save.png"))); // NOI18N
        jButton7.setText("Simpan");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(255, 165, 0));
        jButton8.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/file-edit.png"))); // NOI18N
        jButton8.setText("Edit");

        jButton9.setBackground(new java.awt.Color(220, 53, 69));
        jButton9.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/delete-forever.png"))); // NOI18N
        jButton9.setText("Hapus");

        jButton10.setBackground(new java.awt.Color(108, 117, 125));
        jButton10.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/arrow-back-up-double.png"))); // NOI18N
        jButton10.setText("Kembali");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        txtCari.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jButton1.setBackground(new java.awt.Color(0, 120, 242));
        jButton1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/book-search.png"))); // NOI18N
        jButton1.setText("Cari");

        tabelPetugas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tabelPetugas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID Petugas", "Nama Petugas", "Username", "No Telp"
            }
        ));
        jScrollPane1.setViewportView(tabelPetugas);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(87, 87, 87)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)))
                        .addGap(0, 179, Short.MAX_VALUE)))
                .addGap(24, 24, 24))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(54, 54, 54)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFirst)
                    .addComponent(btnPrev)
                    .addComponent(pnlPages, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNext)
                    .addComponent(btnLast)
                    .addComponent(lblTotalData))
                .addGap(0, 20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void autonumber() {
        try {
            st = conn.createStatement();
            rs = st.executeQuery("SELECT MAX(id_petugas) FROM petugas");
            if (rs.next() && rs.getString(1) != null) {
                int maxId = rs.getInt(1);
                jTextField1.setText(String.valueOf(maxId + 1));
            } else {
                jTextField1.setText("1");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal auto number: " + e.getMessage());
        }
    }

    private void clearForm() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        txtCari.setText("Cari Nama / ID Petugas...");
        txtCari.setForeground(java.awt.Color.GRAY);
        autonumber();
        jTextField2.requestFocus();
    }

    private void tampilData(String cari) {
        filteredData.clear();
        try {
            String sql;
            if (cari.equals("Cari Nama / ID Petugas...") || cari.isEmpty()) {
                sql = "SELECT id_petugas, nama_petugas, username, no_telp FROM petugas";
            } else {
                sql = "SELECT id_petugas, nama_petugas, username, no_telp FROM petugas WHERE nama_petugas LIKE ? OR id_petugas LIKE ?";
            }
            
            PreparedStatement pst = conn.prepareStatement(sql);
            if (!cari.equals("Cari Nama / ID Petugas...") && !cari.isEmpty()) {
                String p = "%" + cari + "%";
                pst.setString(1, p);
                pst.setString(2, p);
            }
            rs = pst.executeQuery();

            while (rs.next()) {
                filteredData.add(new Object[]{
                    rs.getString("id_petugas"),
                    rs.getString("nama_petugas"),
                    rs.getString("username"),
                    rs.getString("no_telp")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
        }
        
        totalPages = (int) Math.ceil((double) filteredData.size() / pageSize);
        if (totalPages == 0)
            totalPages = 1;
        currentPage = 1;

        updateTable();
    }

    private void initActionListeners() {
        tabelPetugas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tabelPetugas.getSelectedRow();
                fillForm(row);
            }
        });

        jButton8.addActionListener(e -> { 
            String id = jTextField1.getText();
            String nama = jTextField2.getText();
            String username = jTextField3.getText();
            String password = jTextField4.getText();
            String noTelp = jTextField5.getText();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih data yang akan diedit!");
                return;
            }

            try {
                String sql = "UPDATE petugas SET nama_petugas=?, username=?, password=?, no_telp=? WHERE id_petugas=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, nama);
                pst.setString(2, username);
                pst.setString(3, password);
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

        jButton9.addActionListener(e -> { 
            String id = jTextField1.getText();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Apakah anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM petugas WHERE id_petugas=?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, id);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Data Berhasil Dihapus");
                    tampilData(txtCari.getText());
                    clearForm();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal hapus data, pastikan petugas tidak terikat data lain. \n" + ex.getMessage());
                }
            }
        });

        jButton1.addActionListener(e -> tampilData(txtCari.getText()));
        
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                tampilData(txtCari.getText());
            }
        });
    }

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        clearForm();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        String id = jTextField1.getText();
        String nama = jTextField2.getText();
        String username = jTextField3.getText();
        String password = jTextField4.getText();
        String noTelp = jTextField5.getText();

        if (nama.isEmpty() || username.isEmpty() || password.isEmpty() || noTelp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harap lengkapi semua data!");
            return;
        }

        try {
            String sql = "INSERT INTO petugas (nama_petugas, username, password, no_telp) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, nama);
            pst.setString(2, username);
            pst.setString(3, password);
            pst.setString(4, noTelp);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Berhasil Disimpan");
            tampilData(txtCari.getText());
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal simpan data: " + ex.getMessage());
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        new tampilanawal.dashboard().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton10ActionPerformed

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
            java.util.logging.Logger.getLogger(FormPetugas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormPetugas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormPetugas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormPetugas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormPetugas().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnPrev;
    private javax.swing.JPanel pnlPages;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnLast;
    private javax.swing.JLabel lblTotalData;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTable tabelPetugas;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables
}
