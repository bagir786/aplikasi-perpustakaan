/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transaksi;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import koneksi.koneksi;

/**
 *
 * @author Anisya
 */
public class peminjaman extends javax.swing.JFrame {
    DefaultTableModel model;

    // Master database/collection for checked out books in the cart
    private java.util.List<Object[]> cartData = new java.util.ArrayList<>();
    // Cart pagination state
    private int cartCurrentPage = 1;
    private int cartPageSize = 5; // default size, dynamic via resize listener
    private int cartTotalPages = 1;

    // Premium cart pagination UI elements
    private javax.swing.JButton btnCartFirst;
    private javax.swing.JButton btnCartPrev;
    private javax.swing.JButton btnCartNext;
    private javax.swing.JButton btnCartLast;
    private javax.swing.JPanel pnlCartPages;

    /**
     * Creates new form peminjaman
     */
    public peminjaman() {
        initComponents();
        model = (DefaultTableModel) tabelPinjam.getModel();
        model.setRowCount(0);
        styleComponents();
        setLocationRelativeTo(null);
        tTotal.setText("0");
        tIdPinjam.setEditable(false);
        tNama.setEditable(false);
        dcTgl.setDate(null);
        autonumber();
        dcTgl.addPropertyChangeListener("date", new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                updateTableDates();
            }
        });
    }

    private void updateTableDates() {
        java.util.Date tglPinjam = dcTgl.getDate();
        String tglPinjamStr = "";
        if (tglPinjam != null) {
            tglPinjamStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(tglPinjam);
        }
        for (Object[] row : cartData) {
            row[3] = tglPinjamStr;
        }
        updateCartTable();
    }

    private void btnCariAnggotaActionPerformed(java.awt.event.ActionEvent evt) {
        JDialog dialog = new JDialog(this, "Pilih Anggota", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new java.awt.BorderLayout());

        // Search panel at the Top
        javax.swing.JPanel topPanel = new javax.swing.JPanel(new java.awt.GridBagLayout());
        topPanel.setBackground(new java.awt.Color(245, 247, 247));
        topPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();

        javax.swing.JLabel lblSearch = new javax.swing.JLabel("Cari Anggota (Nama / ID / Alamat / Telp): ");
        lblSearch.setFont(new java.awt.Font("Segoe UI Semibold", 1, 13));
        lblSearch.setForeground(new java.awt.Color(0, 51, 102));

        javax.swing.JTextField txtSearch = new javax.swing.JTextField(25);
        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 13));
        txtSearch.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)),
                javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        topPanel.add(lblSearch, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(0, 10, 0, 0);
        topPanel.add(txtSearch, gbc);

        dialog.add(topPanel, java.awt.BorderLayout.NORTH);

        JTable table = new JTable();
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        DefaultTableModel dialogModel = new DefaultTableModel(
                new Object[] { "ID Anggota", "Nama Anggota", "Jenis Kelamin", "No. Telepon", "Alamat" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(dialogModel);

        // Table styling matching main table
        table.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                javax.swing.JLabel label = (javax.swing.JLabel) super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                label.setBackground(new java.awt.Color(234, 241, 248)); // Light blue
                label.setForeground(new java.awt.Color(0, 51, 102)); // Dark blue
                label.setFont(new java.awt.Font("Segoe UI", 1, 12));
                label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                label.setBorder(
                        javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 1, new java.awt.Color(204, 204, 204)));
                return label;
            }
        });
        table.setRowHeight(35);
        table.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 35));
        table.setGridColor(new java.awt.Color(230, 230, 230));
        table.setSelectionBackground(new java.awt.Color(235, 245, 255));
        table.setSelectionForeground(java.awt.Color.BLACK);
        table.setFont(new java.awt.Font("Segoe UI", 0, 13));

        // Pagination setup components
        javax.swing.JPanel southContainer = new javax.swing.JPanel();
        southContainer.setLayout(new javax.swing.BoxLayout(southContainer, javax.swing.BoxLayout.Y_AXIS));
        southContainer.setBackground(new java.awt.Color(245, 247, 247));

        javax.swing.JPanel paginationPanel = new javax.swing.JPanel(new java.awt.BorderLayout(10, 0));
        paginationPanel.setBackground(new java.awt.Color(245, 247, 247));
        paginationPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 15, 5, 15));

        javax.swing.JLabel lblTotalData = new javax.swing.JLabel("Total Data: 0");
        lblTotalData.setFont(new java.awt.Font("Segoe UI Semibold", 1, 12));
        lblTotalData.setForeground(new java.awt.Color(0, 51, 102));
        paginationPanel.add(lblTotalData, java.awt.BorderLayout.EAST);

        javax.swing.JPanel navPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        navPanel.setOpaque(false);

        javax.swing.JButton btnFirst = new javax.swing.JButton("<<");
        javax.swing.JButton btnPrev = new javax.swing.JButton("<");
        javax.swing.JPanel pnlPages = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 0));
        pnlPages.setOpaque(false);
        javax.swing.JButton btnNext = new javax.swing.JButton(">");
        javax.swing.JButton btnLast = new javax.swing.JButton(">>");

        java.util.List<javax.swing.JButton> navButtons = java.util.Arrays.asList(btnFirst, btnPrev, btnNext, btnLast);
        for (javax.swing.JButton btn : navButtons) {
            btn.setPreferredSize(new java.awt.Dimension(40, 30));
            btn.setFont(new java.awt.Font("Segoe UI", 1, 11));
            btn.setFocusPainted(false);
            btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            btn.setBackground(java.awt.Color.WHITE);
            btn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        }

        navPanel.add(btnFirst);
        navPanel.add(btnPrev);
        navPanel.add(pnlPages);
        navPanel.add(btnNext);
        navPanel.add(btnLast);

        paginationPanel.add(navPanel, java.awt.BorderLayout.WEST);
        southContainer.add(paginationPanel);

        // Pagination state
        int[] currentPage = {1};
        int[] pageSize = {5};
        int[] totalPages = {1};
        java.util.List<Object[]> filteredData = new java.util.ArrayList<>();

        Runnable[] updateTable = new Runnable[1];
        Runnable[] renderPagination = new Runnable[1];

        updateTable[0] = () -> {
            dialogModel.setRowCount(0);
            int start = (currentPage[0] - 1) * pageSize[0];
            int end = Math.min(start + pageSize[0], filteredData.size());

            for (int i = start; i < end; i++) {
                dialogModel.addRow(filteredData.get(i));
            }

            while (dialogModel.getRowCount() < pageSize[0]) {
                dialogModel.addRow(new Object[] { "", "", "", "", "" });
            }

            renderPagination[0].run();
            lblTotalData.setText("Total Data: " + filteredData.size());

            btnFirst.setEnabled(currentPage[0] > 1);
            btnPrev.setEnabled(currentPage[0] > 1);
            btnNext.setEnabled(currentPage[0] < totalPages[0]);
            btnLast.setEnabled(currentPage[0] < totalPages[0]);
        };

        renderPagination[0] = () -> {
            pnlPages.removeAll();
            int maxVisiblePages = 5;
            int startPage = Math.max(1, currentPage[0] - (maxVisiblePages / 2));
            int endPage = Math.min(totalPages[0], startPage + maxVisiblePages - 1);

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

                if (i == currentPage[0]) {
                    btn.setBackground(new java.awt.Color(0, 120, 242));
                    btn.setForeground(java.awt.Color.WHITE);
                    btn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 120, 242)));
                } else {
                    btn.setBackground(java.awt.Color.WHITE);
                    btn.setForeground(java.awt.Color.BLACK);
                    btn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
                }

                btn.addActionListener(e -> {
                    currentPage[0] = page;
                    updateTable[0].run();
                });

                pnlPages.add(btn);
            }
            pnlPages.revalidate();
            pnlPages.repaint();
        };

        btnFirst.addActionListener(e -> {
            currentPage[0] = 1;
            updateTable[0].run();
        });
        btnPrev.addActionListener(e -> {
            if (currentPage[0] > 1) {
                currentPage[0]--;
                updateTable[0].run();
            }
        });
        btnNext.addActionListener(e -> {
            if (currentPage[0] < totalPages[0]) {
                currentPage[0]++;
                updateTable[0].run();
            }
        });
        btnLast.addActionListener(e -> {
            currentPage[0] = totalPages[0];
            updateTable[0].run();
        });

        Runnable loadData = () -> {
            filteredData.clear();
            String keyword = txtSearch.getText().trim();
            String query = "SELECT id_anggota, nama_anggota, jenis_kelamin, no_telp, alamat FROM anggota";
            if (!keyword.isEmpty()) {
                query += " WHERE nama_anggota LIKE ? OR id_anggota LIKE ? OR alamat LIKE ? OR no_telp LIKE ?";
            }
            Connection conn = koneksi.getConnection();
            if (conn != null) {
                try (PreparedStatement pst = conn.prepareStatement(query)) {
                    if (!keyword.isEmpty()) {
                        String param = "%" + keyword + "%";
                        pst.setString(1, param);
                        pst.setString(2, param);
                        pst.setString(3, param);
                        pst.setString(4, param);
                    }
                    try (ResultSet rs = pst.executeQuery()) {
                        while (rs.next()) {
                            filteredData.add(new Object[] {
                                    rs.getString("id_anggota"),
                                    rs.getString("nama_anggota"),
                                    rs.getString("jenis_kelamin"),
                                    rs.getString("no_telp"),
                                    rs.getString("alamat")
                            });
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Gagal load data anggota: " + e.getMessage());
                }
            }

            totalPages[0] = (int) Math.ceil((double) filteredData.size() / pageSize[0]);
            if (totalPages[0] == 0) {
                totalPages[0] = 1;
            }
            currentPage[0] = 1;
            updateTable[0].run();
        };

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                loadData.run();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        javax.swing.Timer resizeTimer = new javax.swing.Timer(300, e -> {
            int rowHeight = table.getRowHeight();
            if (rowHeight <= 0) rowHeight = 35;
            int availableHeight = scrollPane.getViewport().getHeight();
            if (availableHeight > 0) {
                pageSize[0] = Math.max(1, availableHeight / rowHeight);
                loadData.run();
            }
        });
        resizeTimer.setRepeats(false);

        scrollPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                resizeTimer.restart();
            }
        });

        // Double click handler to select
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        Object valId = table.getValueAt(row, 0);
                        if (valId != null && !valId.toString().trim().isEmpty()) {
                            String idAnggota = valId.toString();
                            String nama = table.getValueAt(row, 1).toString();
                            tIdAnggota.setText(idAnggota);
                            tNama.setText(nama);
                            dialog.dispose();
                        }
                    }
                }
            }
        });

        // Bottom panel
        javax.swing.JPanel bottomPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        bottomPanel.setBackground(new java.awt.Color(245, 247, 247));
        bottomPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));

        javax.swing.JLabel lblStatus = new javax.swing.JLabel("Double-klik baris untuk memilih anggota.");
        lblStatus.setFont(new java.awt.Font("Segoe UI", 2, 12));
        lblStatus.setForeground(new java.awt.Color(100, 100, 100));
        bottomPanel.add(lblStatus, java.awt.BorderLayout.WEST);

        javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        javax.swing.JButton btnSelect = new javax.swing.JButton("Pilih");
        btnSelect.setBackground(new java.awt.Color(40, 167, 69)); // Green
        btnSelect.setForeground(java.awt.Color.WHITE);
        btnSelect.setFont(new java.awt.Font("Segoe UI Semibold", 1, 13));
        btnSelect.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSelect.setFocusPainted(false);
        btnSelect.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                Object valId = table.getValueAt(row, 0);
                if (valId != null && !valId.toString().trim().isEmpty()) {
                    String idAnggota = valId.toString();
                    String nama = table.getValueAt(row, 1).toString();
                    tIdAnggota.setText(idAnggota);
                    tNama.setText(nama);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Pilih salah satu anggota dari tabel!");
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Pilih salah satu anggota dari tabel!");
            }
        });

        javax.swing.JButton btnCancel = new javax.swing.JButton("Batal");
        btnCancel.setBackground(new java.awt.Color(108, 117, 125)); // Gray
        btnCancel.setForeground(java.awt.Color.WHITE);
        btnCancel.setFont(new java.awt.Font("Segoe UI Semibold", 1, 13));
        btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSelect);
        buttonPanel.add(btnCancel);
        bottomPanel.add(buttonPanel, java.awt.BorderLayout.EAST);

        southContainer.add(bottomPanel);
        dialog.add(southContainer, java.awt.BorderLayout.SOUTH);
        dialog.add(scrollPane, java.awt.BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void autonumber() {
        Connection conn = koneksi.getConnection();
        if (conn == null) return;
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT id_pinjam FROM peminjaman ORDER BY id_pinjam DESC LIMIT 1")) {
            if (rs.next()) {
                String lastId = rs.getString("id_pinjam");
                String numberPart = lastId.replaceAll("[^0-9]", "");
                if (numberPart.isEmpty()) {
                    tIdPinjam.setText("P001");
                } else {
                    int num = Integer.parseInt(numberPart) + 1;
                    tIdPinjam.setText(String.format("P%03d", num));
                }
            } else {
                tIdPinjam.setText("P001");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal generate ID: " + e.getMessage());
        }
    }

    private void styleComponents() {
        // Table Header Styling - Matching Image Reference (Light Blue Background, Dark Blue Text)
        tabelPinjam.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
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
        tabelPinjam.setRowHeight(35); // Increased row height for better readability
        tabelPinjam.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 35));
        tabelPinjam.setGridColor(new java.awt.Color(230, 230, 230));
        tabelPinjam.setSelectionBackground(new java.awt.Color(235, 245, 255));
        tabelPinjam.setSelectionForeground(java.awt.Color.BLACK);

        // Adjust Column Widths
        tabelPinjam.getColumnModel().getColumn(0).setPreferredWidth(50); //
        tabelPinjam.getColumnModel().getColumn(1).setPreferredWidth(100); //
        tabelPinjam.getColumnModel().getColumn(2).setPreferredWidth(300); //
        tabelPinjam.getColumnModel().getColumn(3).setPreferredWidth(200); //
        tabelPinjam.getColumnModel().getColumn(4).setPreferredWidth(200); //

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Change jPanel4 to BorderLayout to easily hold the scroll pane and pagination footer
        jPanel4.setLayout(new java.awt.BorderLayout(0, 10));

        // Re-arrange components
        jPanel4.removeAll();
        jPanel4.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        // Construct Premium Pagination Panel
        javax.swing.JPanel paginationPanel = new javax.swing.JPanel(new java.awt.BorderLayout(10, 0));
        paginationPanel.setBackground(new java.awt.Color(247, 247, 245));
        paginationPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        javax.swing.JPanel navPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        navPanel.setOpaque(false);

        btnCartFirst = new javax.swing.JButton("|<");
        btnCartPrev = new javax.swing.JButton("<");
        pnlCartPages = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pnlCartPages.setOpaque(false);
        btnCartNext = new javax.swing.JButton(">");
        btnCartLast = new javax.swing.JButton(">|");

        java.util.List<javax.swing.JButton> navButtons = java.util.Arrays.asList(btnCartFirst, btnCartPrev, btnCartNext, btnCartLast);
        for (javax.swing.JButton btn : navButtons) {
            btn.setPreferredSize(new java.awt.Dimension(35, 30));
            btn.setFont(new java.awt.Font("Segoe UI", 1, 11));
            btn.setFocusPainted(false);
            btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        }

        navPanel.add(btnCartFirst);
        navPanel.add(btnCartPrev);
        navPanel.add(pnlCartPages);
        navPanel.add(btnCartNext);
        navPanel.add(btnCartLast);

        paginationPanel.add(navPanel, java.awt.BorderLayout.WEST);

        // Add events to navigation buttons
        btnCartFirst.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (cartCurrentPage > 1) {
                    cartCurrentPage = 1;
                    updateCartTable();
                }
            }
        });

        btnCartPrev.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (cartCurrentPage > 1) {
                    cartCurrentPage--;
                    updateCartTable();
                }
            }
        });

        btnCartNext.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (cartCurrentPage < cartTotalPages) {
                    cartCurrentPage++;
                    updateCartTable();
                }
            }
        });

        btnCartLast.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (cartCurrentPage < cartTotalPages) {
                    cartCurrentPage = cartTotalPages;
                    updateCartTable();
                }
            }
        });

        jPanel4.add(paginationPanel, java.awt.BorderLayout.SOUTH);
        jPanel4.revalidate();
        jPanel4.repaint();

        // Add dynamic resize listener for cart JScrollPane
        javax.swing.Timer cartResizeTimer = new javax.swing.Timer(300, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int rowHeight = tabelPinjam.getRowHeight();
                if (rowHeight <= 0) rowHeight = 35;
                int availableHeight = jScrollPane1.getViewport().getHeight();
                if (availableHeight > 0) {
                    cartPageSize = Math.max(1, availableHeight / rowHeight);
                    updateCartTable();
                }
            }
        });
        cartResizeTimer.setRepeats(false);

        jScrollPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                cartResizeTimer.restart();
            }
        });
    }

    private void updateCartTable() {
        model.setRowCount(0);

        // Safety checks for total pages calculation
        cartTotalPages = (int) Math.ceil((double) cartData.size() / cartPageSize);
        if (cartTotalPages == 0) {
            cartTotalPages = 1;
        }
        if (cartCurrentPage > cartTotalPages) {
            cartCurrentPage = cartTotalPages;
        }
        if (cartCurrentPage < 1) {
            cartCurrentPage = 1;
        }

        int start = (cartCurrentPage - 1) * cartPageSize;
        int end = Math.min(start + cartPageSize, cartData.size());

        // Show only the paginated slice
        for (int i = start; i < end; i++) {
            Object[] row = cartData.get(i);
            // row[0] is the sequential number (No.)
            row[0] = i + 1; // update index to 1-based sequential number of all items
            model.addRow(row);
        }

        // Fill empty rows to maintain layout consistency
        while (model.getRowCount() < cartPageSize) {
            model.addRow(new Object[] { "", "", "", "", "" });
        }

        // Update total counter
        tTotal.setText(String.valueOf(cartData.size()));

        // Toggle standard buttons
        btnCartFirst.setEnabled(cartCurrentPage > 1);
        btnCartPrev.setEnabled(cartCurrentPage > 1);
        btnCartNext.setEnabled(cartCurrentPage < cartTotalPages);
        btnCartLast.setEnabled(cartCurrentPage < cartTotalPages);

        renderCartPaginationButtons();
    }

    private void renderCartPaginationButtons() {
        pnlCartPages.removeAll();

        int startPage = Math.max(1, cartCurrentPage - 2);
        int endPage = Math.min(cartTotalPages, startPage + 4);
        if (endPage - startPage < 4) {
            startPage = Math.max(1, endPage - 4);
        }

        for (int i = startPage; i <= endPage; i++) {
            final int pageNum = i;
            javax.swing.JButton btnPage = new javax.swing.JButton(String.valueOf(i));
            btnPage.setPreferredSize(new java.awt.Dimension(35, 30));
            btnPage.setFont(new java.awt.Font("Segoe UI", 1, 11));
            btnPage.setFocusPainted(false);
            btnPage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

            if (i == cartCurrentPage) {
                btnPage.setFont(new java.awt.Font("Segoe UI", 1, 11));
                btnPage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
            } else {
                btnPage.setFont(new java.awt.Font("Segoe UI", 0, 11));
                btnPage.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
            }

            btnPage.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    cartCurrentPage = pageNum;
                    updateCartTable();
                }
            });

            pnlCartPages.add(btnPage);
        }

        pnlCartPages.revalidate();
        pnlCartPages.repaint();
    }

    // =========================
    // MEMBUAT HEADER TABEL
    // =========================

    // =========================
    // HITUNG TOTAL
    // =========================
    private void hitungTotal() {
        tTotal.setText(String.valueOf(cartData.size()));
        for (int i = 0; i < cartData.size(); i++) {
            cartData.get(i)[0] = i + 1;
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

        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tIdPinjam = new javax.swing.JTextField();
        tNama = new javax.swing.JTextField();
        dcTgl = new com.toedter.calendar.JDateChooser();
        tIdAnggota = new javax.swing.JTextField();
        btnCariAnggota = new javax.swing.JButton();
        javax.swing.JPanel panelAnggotaSearch = new javax.swing.JPanel(new java.awt.BorderLayout(5, 0));
        panelAnggotaSearch.setOpaque(false);
        jLabel7 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelPinjam = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        tTotal = new javax.swing.JTextField();
        btnPilihBuku = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(99, 102, 241));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/tasks-app.png"))); // NOI18N
        jLabel6.setText(" Sistem Informasi Manajemen Perpustakaan");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE));
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        jPanel1.setBackground(new java.awt.Color(247, 247, 245));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Peminjaman Buku");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("ID Pinjam");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Tanggal Pinjam");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("ID Anggota");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Nama Anggota");

        tIdPinjam.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tIdPinjam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tIdPinjamActionPerformed(evt);
            }
        });

        tNama.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        tIdAnggota.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tIdAnggota.setEditable(false);
        tIdAnggota.setBackground(new java.awt.Color(255, 255, 255));
        tIdAnggota.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)),
            javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        btnCariAnggota.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnCariAnggota.setBackground(new java.awt.Color(0, 120, 242));
        btnCariAnggota.setForeground(java.awt.Color.WHITE);
        btnCariAnggota.setText("Cari");
        btnCariAnggota.setFocusPainted(false);
        btnCariAnggota.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCariAnggota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariAnggotaActionPerformed(evt);
            }
        });

        panelAnggotaSearch.add(tIdAnggota, java.awt.BorderLayout.CENTER);
        panelAnggotaSearch.add(btnCariAnggota, java.awt.BorderLayout.EAST);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Daftar Buku yang Dipinjam");

        jPanel4.setBackground(new java.awt.Color(247, 247, 245));

        tabelPinjam.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tabelPinjam.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null }
                },
                new String[] {
                        "No", "ID Buku", "Judul Buku", "Tgl Pinjam", "Keterangan"
                }));
        tabelPinjam.setVerifyInputWhenFocusTarget(false);
        jScrollPane1.setViewportView(tabelPinjam);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 800,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(14, Short.MAX_VALUE)));
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 199,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Total Buku Dipinjam");

        tTotal.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tTotalActionPerformed(evt);
            }
        });

        btnPilihBuku.setBackground(new java.awt.Color(40, 167, 69));
        btnPilihBuku.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnPilihBuku.setForeground(new java.awt.Color(255, 255, 255));
        btnPilihBuku.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/bxs-book-add.png"))); // NOI18N
        btnPilihBuku.setText("Pilih Buku");
        btnPilihBuku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPilihBukuActionPerformed(evt);
            }
        });

        btnSimpan.setBackground(new java.awt.Color(0, 120, 242));
        btnSimpan.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/save.png"))); // NOI18N
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnHapus.setBackground(new java.awt.Color(255, 165, 0));
        btnHapus.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/delete-forever.png"))); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnClear.setBackground(new java.awt.Color(220, 53, 69));
        btnClear.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/clear-formatting.png"))); // NOI18N
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnBatal.setBackground(new java.awt.Color(108, 117, 125));
        btnBatal.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnBatal.setForeground(new java.awt.Color(255, 255, 255));
        btnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/arrow-back-up-double.png"))); // NOI18N
        btnBatal.setText("Kembali");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout
                                                .createSequentialGroup()
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel8)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(tTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 123,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(16, 16, 16)))
                                .addContainerGap())
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jLabel3)
                                                        .addComponent(jLabel4)
                                                        .addComponent(jLabel5))
                                                .addGap(79, 79, 79)
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addComponent(panelAnggotaSearch, 0,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(tIdPinjam)
                                                        .addComponent(tNama)
                                                        .addComponent(dcTgl, javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, 305,
                                                                Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addComponent(btnPilihBuku,
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnSimpan,
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnHapus,
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnClear,
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnBatal,
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 120,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(50, 50, 50))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel1)
                                                        .addComponent(jLabel7))
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        Short.MAX_VALUE)))));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(tIdPinjam,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                30,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel2,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                30,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jLabel3,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(dcTgl, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(panelAnggotaSearch,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(tNama, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(btnPilihBuku, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(22, 22, 22)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 197,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(45, Short.MAX_VALUE)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 47,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tTotalActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tTotalActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_tTotalActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnBatalActionPerformed
        // TODO add your handling code here:
        new tampilanawal.dashboard().setVisible(true);
        this.dispose();
    }// GEN-LAST:event_btnBatalActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnClearActionPerformed
        autonumber();
        tNama.setText("");
        dcTgl.setDate(null);
        tIdAnggota.setText("");
        cartData.clear();
        cartCurrentPage = 1;
        updateCartTable();
    }// GEN-LAST:event_btnClearActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnHapusActionPerformed
        int row = tabelPinjam.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data dulu!");
        } else {
            int cartDataIndex = (cartCurrentPage - 1) * cartPageSize + row;
            if (cartDataIndex >= 0 && cartDataIndex < cartData.size()) {
                cartData.remove(cartDataIndex);
                hitungTotal();
                updateCartTable();
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris data buku yang valid!");
            }
        }
    }// GEN-LAST:event_btnHapusActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSimpanActionPerformed
        if (tIdPinjam.getText().isEmpty() || tIdAnggota.getText().isEmpty() || dcTgl.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Harap lengkapi semua data!");
            return;
        }
        if (cartData.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Belum ada buku dipilih!");
            return;
        }

        Connection conn = koneksi.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Koneksi database gagal!");
            return;
        }
        try {
            conn.setAutoCommit(false);
            try {
                String sqlPinjam = "INSERT INTO peminjaman (id_pinjam, tanggal_pinjam, id_anggota) VALUES (?, ?, ?)";
                try (PreparedStatement pst = conn.prepareStatement(sqlPinjam)) {
                    pst.setString(1, tIdPinjam.getText());
                    pst.setString(2, new java.text.SimpleDateFormat("yyyy-MM-dd").format(dcTgl.getDate()));
                    String idAnggota = tIdAnggota.getText().trim();
                    pst.setString(3, idAnggota);
                    pst.executeUpdate();
                }

                String sqlDetail = "INSERT INTO detail_peminjaman (id_pinjam, id_buku, tanggal_kembali, status) VALUES (?, ?, ?, ?)";
                String sqlUpdateStok = "UPDATE buku SET stok = stok - 1 WHERE id_buku = ?";
                String sqlCheckStok = "SELECT stok FROM buku WHERE id_buku = ?";
                try (PreparedStatement pstDetail = conn.prepareStatement(sqlDetail);
                        PreparedStatement pstStok = conn.prepareStatement(sqlUpdateStok);
                        PreparedStatement pstCheck = conn.prepareStatement(sqlCheckStok)) {

                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(dcTgl.getDate());
                    cal.add(java.util.Calendar.DAY_OF_MONTH, 7);
                    String tglKembali = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

                    for (Object[] row : cartData) {
                        String idBuku = row[1].toString();
                        String judul = row[2].toString();
                        String status = row[4].toString();

                        // Live stock validation
                        pstCheck.setString(1, idBuku);
                        try (java.sql.ResultSet rs = pstCheck.executeQuery()) {
                            if (rs.next()) {
                                int stok = rs.getInt("stok");
                                if (stok <= 0) {
                                    throw new java.sql.SQLException("Stok buku '" + judul + "' habis di database! Peminjaman dibatalkan.");
                                }
                            } else {
                                throw new java.sql.SQLException("Buku '" + judul + "' tidak ditemukan di database!");
                            }
                        }

                        pstDetail.setString(1, tIdPinjam.getText());
                        pstDetail.setString(2, idBuku);
                        pstDetail.setString(3, tglKembali);
                        pstDetail.setString(4, status);
                        pstDetail.addBatch();

                        pstStok.setString(1, idBuku);
                        pstStok.addBatch();
                    }

                    pstDetail.executeBatch();
                    pstStok.executeBatch();
                }

                conn.commit();
                JOptionPane.showMessageDialog(this, "Data peminjaman berhasil disimpan.");
                btnClearActionPerformed(evt);
            } catch (Exception e) {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Gagal simpan data: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error koneksi: " + e.getMessage());
        }
    }// GEN-LAST:event_btnSimpanActionPerformed

    private void btnPilihBukuActionPerformed(java.awt.event.ActionEvent evt) {
        JDialog dialog = new JDialog(this, "Pilih Buku", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new java.awt.BorderLayout());

        // Search and Status panel at the Top
        javax.swing.JPanel topPanel = new javax.swing.JPanel(new java.awt.GridBagLayout());
        topPanel.setBackground(new java.awt.Color(245, 247, 247));
        topPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();

        javax.swing.JLabel lblSearch = new javax.swing.JLabel("Cari Buku (Judul / ID / Pengarang / Penerbit): ");
        lblSearch.setFont(new java.awt.Font("Segoe UI Semibold", 1, 13));
        lblSearch.setForeground(new java.awt.Color(0, 51, 102));

        javax.swing.JTextField txtSearch = new javax.swing.JTextField(25);
        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 13));
        txtSearch.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)),
                javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        topPanel.add(lblSearch, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(0, 10, 0, 0);
        topPanel.add(txtSearch, gbc);

        dialog.add(topPanel, java.awt.BorderLayout.NORTH);

        JTable table = new JTable();
        table.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        DefaultTableModel dialogModel = new DefaultTableModel(
                new Object[] { "ID Buku", "Judul Buku", "Pengarang", "Penerbit", "Stok" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(dialogModel);

        // Table styling matching main table
        table.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                javax.swing.JLabel label = (javax.swing.JLabel) super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                label.setBackground(new java.awt.Color(234, 241, 248)); // Light blue
                label.setForeground(new java.awt.Color(0, 51, 102)); // Dark blue
                label.setFont(new java.awt.Font("Segoe UI", 1, 12));
                label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                label.setBorder(
                        javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 1, new java.awt.Color(204, 204, 204)));
                return label;
            }
        });
        table.setRowHeight(35);
        table.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 35));
        table.setGridColor(new java.awt.Color(230, 230, 230));
        table.setSelectionBackground(new java.awt.Color(235, 245, 255));
        table.setSelectionForeground(java.awt.Color.BLACK);
        table.setFont(new java.awt.Font("Segoe UI", 0, 13));

        // Pagination setup components
        javax.swing.JPanel southContainer = new javax.swing.JPanel();
        southContainer.setLayout(new javax.swing.BoxLayout(southContainer, javax.swing.BoxLayout.Y_AXIS));
        southContainer.setBackground(new java.awt.Color(245, 247, 247));

        javax.swing.JPanel paginationPanel = new javax.swing.JPanel(new java.awt.BorderLayout(10, 0));
        paginationPanel.setBackground(new java.awt.Color(245, 247, 247));
        paginationPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 15, 5, 15));

        javax.swing.JLabel lblTotalData = new javax.swing.JLabel("Total Data: 0");
        lblTotalData.setFont(new java.awt.Font("Segoe UI Semibold", 1, 12));
        lblTotalData.setForeground(new java.awt.Color(0, 51, 102));
        paginationPanel.add(lblTotalData, java.awt.BorderLayout.EAST);

        javax.swing.JPanel navPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        navPanel.setOpaque(false);

        javax.swing.JButton btnFirst = new javax.swing.JButton("<<");
        javax.swing.JButton btnPrev = new javax.swing.JButton("<");
        javax.swing.JPanel pnlPages = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 0));
        pnlPages.setOpaque(false);
        javax.swing.JButton btnNext = new javax.swing.JButton(">");
        javax.swing.JButton btnLast = new javax.swing.JButton(">>");

        java.util.List<javax.swing.JButton> navButtons = java.util.Arrays.asList(btnFirst, btnPrev, btnNext, btnLast);
        for (javax.swing.JButton btn : navButtons) {
            btn.setPreferredSize(new java.awt.Dimension(40, 30));
            btn.setFont(new java.awt.Font("Segoe UI", 1, 11));
            btn.setFocusPainted(false);
            btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            btn.setBackground(java.awt.Color.WHITE);
            btn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        }

        navPanel.add(btnFirst);
        navPanel.add(btnPrev);
        navPanel.add(pnlPages);
        navPanel.add(btnNext);
        navPanel.add(btnLast);

        paginationPanel.add(navPanel, java.awt.BorderLayout.WEST);
        southContainer.add(paginationPanel);

        // Pagination state
        int[] currentPage = {1};
        int[] pageSize = {5};
        int[] totalPages = {1};
        java.util.List<Object[]> filteredData = new java.util.ArrayList<>();

        Runnable[] updateTable = new Runnable[1];
        Runnable[] renderPagination = new Runnable[1];

        updateTable[0] = () -> {
            dialogModel.setRowCount(0);
            int start = (currentPage[0] - 1) * pageSize[0];
            int end = Math.min(start + pageSize[0], filteredData.size());

            for (int i = start; i < end; i++) {
                dialogModel.addRow(filteredData.get(i));
            }

            while (dialogModel.getRowCount() < pageSize[0]) {
                dialogModel.addRow(new Object[] { "", "", "", "", "" });
            }

            renderPagination[0].run();
            lblTotalData.setText("Total Data: " + filteredData.size());

            btnFirst.setEnabled(currentPage[0] > 1);
            btnPrev.setEnabled(currentPage[0] > 1);
            btnNext.setEnabled(currentPage[0] < totalPages[0]);
            btnLast.setEnabled(currentPage[0] < totalPages[0]);
        };

        renderPagination[0] = () -> {
            pnlPages.removeAll();
            int maxVisiblePages = 5;
            int startPage = Math.max(1, currentPage[0] - (maxVisiblePages / 2));
            int endPage = Math.min(totalPages[0], startPage + maxVisiblePages - 1);

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

                if (i == currentPage[0]) {
                    btn.setBackground(new java.awt.Color(0, 120, 242));
                    btn.setForeground(java.awt.Color.WHITE);
                    btn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 120, 242)));
                } else {
                    btn.setBackground(java.awt.Color.WHITE);
                    btn.setForeground(java.awt.Color.BLACK);
                    btn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
                }

                btn.addActionListener(e -> {
                    currentPage[0] = page;
                    updateTable[0].run();
                });

                pnlPages.add(btn);
            }
            pnlPages.revalidate();
            pnlPages.repaint();
        };

        btnFirst.addActionListener(e -> {
            currentPage[0] = 1;
            updateTable[0].run();
        });
        btnPrev.addActionListener(e -> {
            if (currentPage[0] > 1) {
                currentPage[0]--;
                updateTable[0].run();
            }
        });
        btnNext.addActionListener(e -> {
            if (currentPage[0] < totalPages[0]) {
                currentPage[0]++;
                updateTable[0].run();
            }
        });
        btnLast.addActionListener(e -> {
            currentPage[0] = totalPages[0];
            updateTable[0].run();
        });

        Runnable loadData = () -> {
            filteredData.clear();
            String keyword = txtSearch.getText().trim();
            String query = "SELECT id_buku, judul_buku, pengarang, penerbit, stok FROM buku";
            if (!keyword.isEmpty()) {
                query += " WHERE judul_buku LIKE ? OR id_buku LIKE ? OR pengarang LIKE ? OR penerbit LIKE ?";
            }
            Connection conn = koneksi.getConnection();
            if (conn != null) {
                try (PreparedStatement pst = conn.prepareStatement(query)) {
                    if (!keyword.isEmpty()) {
                        String param = "%" + keyword + "%";
                        pst.setString(1, param);
                        pst.setString(2, param);
                        pst.setString(3, param);
                        pst.setString(4, param);
                    }
                    try (ResultSet rs = pst.executeQuery()) {
                        while (rs.next()) {
                            filteredData.add(new Object[] {
                                    rs.getString("id_buku"),
                                    rs.getString("judul_buku"),
                                    rs.getString("pengarang"),
                                    rs.getString("penerbit"),
                                    rs.getInt("stok")
                            });
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Gagal load data buku: " + e.getMessage());
                }
            }

            totalPages[0] = (int) Math.ceil((double) filteredData.size() / pageSize[0]);
            if (totalPages[0] == 0) {
                totalPages[0] = 1;
            }
            currentPage[0] = 1;
            updateTable[0].run();
        };

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                loadData.run();
            }
        });

        // Bottom controls panel
        javax.swing.JPanel bottomPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        bottomPanel.setBackground(new java.awt.Color(245, 247, 247));
        bottomPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));

        javax.swing.JLabel lblStatus = new javax.swing.JLabel("Tips: Klik CTRL/SHIFT untuk memilih banyak buku.");
        lblStatus.setFont(new java.awt.Font("Segoe UI", 2, 12));
        lblStatus.setForeground(new java.awt.Color(100, 100, 100));
        bottomPanel.add(lblStatus, java.awt.BorderLayout.WEST);

        javax.swing.JPanel buttonPanel = new javax.swing.JPanel(
                new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        javax.swing.JButton btnAddSelected = new javax.swing.JButton("Tambah");
        btnAddSelected.setBackground(new java.awt.Color(40, 167, 69)); // Green
        btnAddSelected.setForeground(java.awt.Color.WHITE);
        btnAddSelected.setFont(new java.awt.Font("Segoe UI Semibold", 1, 13));
        btnAddSelected.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddSelected.setFocusPainted(false);

        javax.swing.JButton btnDone = new javax.swing.JButton("Selesai");
        btnDone.setBackground(new java.awt.Color(108, 117, 125)); // Gray
        btnDone.setForeground(java.awt.Color.WHITE);
        btnDone.setFont(new java.awt.Font("Segoe UI Semibold", 1, 13));
        btnDone.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDone.setFocusPainted(false);

        buttonPanel.add(btnAddSelected);
        buttonPanel.add(btnDone);
        bottomPanel.add(buttonPanel, java.awt.BorderLayout.EAST);

        southContainer.add(bottomPanel);
        dialog.add(southContainer, java.awt.BorderLayout.SOUTH);

        // Add book action helper
        java.util.function.BiConsumer<String, String> addBookToCart = (idBuku, judul) -> {
            java.util.Date tglPinjam = dcTgl.getDate();
            String tglPinjamStr = "";
            if (tglPinjam != null) {
                tglPinjamStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(tglPinjam);
            }

            boolean exists = false;
            for (Object[] r : cartData) {
                if (r[1].toString().equals(idBuku)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                cartData.add(new Object[] { cartData.size() + 1, idBuku, judul, tglPinjamStr, "Dipinjam" });
                updateCartTable();
                lblStatus.setForeground(new java.awt.Color(40, 167, 69));
                lblStatus.setText("Berhasil ditambahkan: " + judul);
            } else {
                lblStatus.setForeground(new java.awt.Color(220, 53, 69));
                lblStatus.setText("Sudah ada di daftar: " + judul);
            }
        };

        // Double click handler
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        Object valId = table.getValueAt(row, 0);
                        if (valId != null && !valId.toString().trim().isEmpty()) {
                            String idBuku = valId.toString();
                            String judul = table.getValueAt(row, 1).toString();
                            int stok = Integer.parseInt(table.getValueAt(row, 4).toString());
                            if (stok > 0) {
                                addBookToCart.accept(idBuku, judul);
                            } else {
                                lblStatus.setForeground(new java.awt.Color(220, 53, 69));
                                lblStatus.setText("Stok habis untuk buku: " + judul);
                                JOptionPane.showMessageDialog(dialog, "Stok buku '" + judul + "' habis! Tidak dapat dipinjam.", "Stok Kosong", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                }
            }
        });

        // Add selected handler
        btnAddSelected.addActionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length == 0) {
                lblStatus.setForeground(new java.awt.Color(220, 53, 69));
                lblStatus.setText("Pilih minimal satu buku dari tabel!");
                return;
            }

            int addedCount = 0;
            int skippedCount = 0;
            java.util.List<String> outOfStockBooks = new java.util.ArrayList<>();

            for (int row : selectedRows) {
                Object valId = table.getValueAt(row, 0);
                if (valId != null && !valId.toString().trim().isEmpty()) {
                    String idBuku = valId.toString();
                    String judul = table.getValueAt(row, 1).toString();
                    int stok = Integer.parseInt(table.getValueAt(row, 4).toString());

                    if (stok > 0) {
                        // Check duplicate inline to count correctly
                        boolean exists = false;
                        for (Object[] r : cartData) {
                            if (r[1].toString().equals(idBuku)) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            java.util.Date tglPinjam = dcTgl.getDate();
                            String tglPinjamStr = "";
                            if (tglPinjam != null) {
                                tglPinjamStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(tglPinjam);
                            }
                            cartData.add(new Object[] { cartData.size() + 1, idBuku, judul, tglPinjamStr, "Dipinjam" });
                            addedCount++;
                        } else {
                            skippedCount++;
                        }
                    } else {
                        outOfStockBooks.add(judul);
                        skippedCount++;
                    }
                } else {
                    skippedCount++;
                }
            }
            updateCartTable();

            lblStatus.setForeground(new java.awt.Color(40, 167, 69));
            lblStatus.setText(
                    "Status: Berhasil menambah " + addedCount + " buku. (Lewati/Duplikat/Habis: " + skippedCount + ")");

            if (!outOfStockBooks.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Buku berikut tidak dapat ditambahkan karena stok habis:\n- " + String.join("\n- ", outOfStockBooks), "Stok Habis", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Done handler
        btnDone.addActionListener(e -> dialog.dispose());

        JScrollPane scrollPane = new JScrollPane(table);
        javax.swing.Timer resizeTimer = new javax.swing.Timer(300, e -> {
            int rowHeight = table.getRowHeight();
            if (rowHeight <= 0) rowHeight = 35;
            int availableHeight = scrollPane.getViewport().getHeight();
            if (availableHeight > 0) {
                pageSize[0] = Math.max(1, availableHeight / rowHeight);
                loadData.run();
            }
        });
        resizeTimer.setRepeats(false);

        scrollPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                resizeTimer.restart();
            }
        });

        dialog.add(scrollPane, java.awt.BorderLayout.CENTER);
        dialog.setVisible(true);
    }// GEN-LAST:event_btnPilihBukuActionPerformed

    private void tIdPinjamActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tIdPinjamActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_tIdPinjamActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel.
         * For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(peminjaman.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(peminjaman.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(peminjaman.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(peminjaman.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new peminjaman().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnPilihBuku;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnCariAnggota;
    private javax.swing.JTextField tIdAnggota;
    private com.toedter.calendar.JDateChooser dcTgl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField tIdPinjam;
    private javax.swing.JTextField tNama;
    private javax.swing.JTextField tTotal;
    private javax.swing.JTable tabelPinjam;
    // End of variables declaration//GEN-END:variables
}
