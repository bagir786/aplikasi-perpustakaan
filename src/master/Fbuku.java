package master;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

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

    private Object[][] dataDummy = {
            { "B001", "Pemrograman Java", "Budi Raharjo", "Informatika", "2021", "Teknik", "10" },
            { "B002", "Dasar Design Grafis", "Adi Kusuma", "Andi Offset", "2020", "Seni", "5" },
            { "B003", "Sistem Basis Data", "Fathansyah", "Informatika", "2019", "Teknik", "8" },
            { "B004", "Belajar Python", "Eko Kurniawan", "Elex Media", "2022", "Teknik", "15" },
            { "B005", "Filosofi Teras", "Henry Manampiring", "Kompas", "2018", "Filsafat", "12" },
            { "B006", "Laskar Pelangi", "Andrea Hirata", "Bentang Pustaka", "2005", "Sastra", "20" },
            { "B007", "Bumi Manusia", "Pramoedya Ananta Toer", "Hasta Mitra", "1980", "Sastra", "7" },
            { "B008", "Negeri 5 Menara", "Ahmad Fuadi", "Gramedia", "2009", "Sastra", "10" },
            { "B009", "Pulang", "Tere Liye", "Republika", "2015", "Sastra", "25" },
            { "B010", "Hujan", "Tere Liye", "Gramedia", "2016", "Sastra", "30" },
            { "B011", "Dunia Sophie", "Jostein Gaarder", "Mizan", "1991", "Filsafat", "10" },
            { "B012", "Madre", "Dee Lestari", "Bentang", "2011", "Sastra", "15" },
            { "B013", "Perahu Kertas", "Dee Lestari", "Bentang", "2009", "Sastra", "12" },
            { "B014", "Supernova", "Dee Lestari", "Truedee", "2001", "Sastra", "8" },
            { "B015", "Rectoverso", "Dee Lestari", "Goodfaith", "2008", "Sastra", "14" },
            { "B016", "Harry Potter 1", "JK Rowling", "Gramedia", "1997", "Fantasi", "25" },
            { "B017", "Harry Potter 2", "JK Rowling", "Gramedia", "1998", "Fantasi", "20" },
            { "B018", "Harry Potter 3", "JK Rowling", "Gramedia", "1999", "Fantasi", "15" },
            { "B019", "Harry Potter 4", "JK Rowling", "Gramedia", "2000", "Fantasi", "18" },
            { "B020", "Harry Potter 5", "JK Rowling", "Gramedia", "2003", "Fantasi", "22" },
            { "B021", "The Hobbit", "J.R.R. Tolkien", "George Allen", "1937", "Fantasi", "10" },
            { "B022", "1984", "George Orwell", "Secker & Warburg", "1949", "Sastra", "12" },
            { "B023", "Animal Farm", "George Orwell", "Secker & Warburg", "1945", "Sastra", "15" },
            { "B024", "Brave New World", "Aldous Huxley", "Chatto & Windus", "1932", "Sastra", "8" },
            { "B025", "Fahrenheit 451", "Ray Bradbury", "Ballantine", "1953", "Sastra", "20" },
            { "B026", "The Great Gatsby", "F. Scott Fitzgerald", "Scribner", "1925", "Sastra", "14" },
            { "B027", "The Catcher in the Rye", "J.D. Salinger", "Little, Brown", "1951", "Sastra", "11" },
            { "B028", "To Kill a Mockingbird", "Harper Lee", "J.B. Lippincott", "1960", "Sastra", "18" },
            { "B029", "The Alchemist", "Paulo Coelho", "HarperCollins", "1988", "Filsafat", "30" },
            { "B030", "Sapiens", "Yuval Noah Harari", "Harper", "2011", "Sejarah", "25" },
            { "B031", "Homo Deus", "Yuval Noah Harari", "Harper", "2015", "Sejarah", "22" },
            { "B032", "21 Lessons", "Yuval Noah Harari", "Spiegel & Grau", "2018", "Sejarah", "20" },
            { "B033", "Thinking, Fast and Slow", "Daniel Kahneman", "Farrar, Straus", "2011", "Psikologi", "15" },
            { "B034", "The Power of Habit", "Charles Duhigg", "Random House", "2012", "Psikologi", "18" },
            { "B035", "Atomic Habits", "James Clear", "Avery", "2018", "Psikologi", "40" },
            { "B036", "Start with Why", "Simon Sinek", "Portfolio", "2009", "Bisnis", "25" },
            { "B037", "The Lean Startup", "Eric Ries", "Crown Business", "2011", "Bisnis", "15" },
            { "B038", "Zero to One", "Peter Thiel", "Crown Business", "2014", "Bisnis", "20" },
            { "B039", "Rich Dad Poor Dad", "Robert Kiyosaki", "Warner Books", "1997", "Bisnis", "35" },
            { "B040", "The Psychology of Money", "Morgan Housel", "Harriman House", "2020", "Bisnis", "28" },
            { "B041", "Man's Search for Meaning", "Viktor Frankl", "Beacon Press", "1946", "Psikologi", "12" },
            { "B042", "The Subtle Art", "Mark Manson", "HarperOne", "2016", "Psikologi", "45" },
            { "B043", "Everything is F*cked", "Mark Manson", "HarperOne", "2019", "Psikologi", "30" },
            { "B044", "Dilan 1990", "Pidi Baiq", "Pastel Books", "2014", "Sastra", "50" },
            { "B045", "Dilan 1991", "Pidi Baiq", "Pastel Books", "2015", "Sastra", "45" },
            { "B046", "Milea", "Pidi Baiq", "Pastel Books", "2016", "Sastra", "40" },
            { "B047", "Laskar Pelangi", "Andrea Hirata", "Bentang Pustaka", "2005", "Sastra", "25" },
            { "B048", "Sang Pemimpi", "Andrea Hirata", "Bentang Pustaka", "2006", "Sastra", "20" },
            { "B049", "Edensor", "Andrea Hirata", "Bentang Pustaka", "2007", "Sastra", "15" },
            { "B050", "Maryamah Karpov", "Andrea Hirata", "Bentang Pustaka", "2008", "Sastra", "12" }
    };

    public Fbuku() {
        initComponents();
        setIcons();
        initPagination();
        initActionListeners();
        populateKategori();
        setupDynamicPageSize();
        tampilData("");
        styleComponents();

        // Auto-fill form with first record
        if (tblBuku.getRowCount() > 0) {
            tblBuku.setRowSelectionInterval(0, 0);
            fillForm(0);
        }
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

        // Cover Panel Styling
        pnlCover.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)),
                "Cover Buku",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 1, 11)));

        // Action Buttons Styling
        JButton[] actionBtns = { btnTambah, btnSimpan, btnEdit, btnHapus, btnBatal };
        Color[] btnColors = {
                new Color(40, 167, 69), // Tambah: Green
                new Color(0, 120, 242), // Simpan: Blue
                new Color(255, 165, 0), // Edit: Orange
                new Color(220, 53, 69), // Hapus: Red
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
        btnTambah = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
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

        jPanel1.setBackground(new java.awt.Color(0, 51, 102));

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

        btnTambah.setBackground(new java.awt.Color(40, 167, 69));
        btnTambah.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnTambah.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah.setText("Tambah");

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
                                                        .addComponent(btnTambah, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnSimpan, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnEdit, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnHapus, javax.swing.GroupLayout.DEFAULT_SIZE,
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
                                                .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
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
                                .addGap(20, 20, 20)
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
        new master.dashboard().setVisible(true);
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
        String query = cari.toLowerCase();

        for (Object[] row : dataDummy) {
            String id = row[0].toString().toLowerCase();
            String judul = row[1].toString().toLowerCase();
            String pengarang = row[2].toString().toLowerCase();

            if (id.contains(query) || judul.contains(query) || pengarang.contains(query)) {
                filteredData.add(row);
            }
        }

        totalPages = (int) Math.ceil((double) filteredData.size() / pageSize);
        if (totalPages == 0)
            totalPages = 1;
        currentPage = 1;

        updateTable();
    }

    private void fillForm(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= tblBuku.getRowCount())
            return;

        Object id = tblBuku.getValueAt(rowIndex, 0);
        if (id == null || id.toString().isEmpty())
            return;

        txtIdBuku.setText(tblBuku.getValueAt(rowIndex, 0).toString());
        txtJudul.setText(tblBuku.getValueAt(rowIndex, 1).toString());
        txtPengarang.setText(tblBuku.getValueAt(rowIndex, 2).toString());
        txtPenerbit.setText(tblBuku.getValueAt(rowIndex, 3).toString());
        txtTahun.setText(tblBuku.getValueAt(rowIndex, 4).toString());
        cbKategori.setSelectedItem(tblBuku.getValueAt(rowIndex, 5).toString());
        txtStok.setText(tblBuku.getValueAt(rowIndex, 6).toString());
    }

    private void populateKategori() {
        String[] categories = { "Teknik", "Seni", "Filsafat", "Sastra", "Fantasi", "Sejarah", "Psikologi", "Bisnis" };
        cbKategori.removeAllItems();
        cbKategori.addItem("-- Pilih Kategori --");
        for (String cat : categories) {
            cbKategori.addItem(cat);
        }
    }

    private void initActionListeners() {
        tblBuku.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tblBuku.getSelectedRow();
                fillForm(row);
            }
        });

        btnTambah.addActionListener(e -> {
            txtIdBuku.setText("");
            txtJudul.setText("");
            txtPengarang.setText("");
            txtPenerbit.setText("");
            txtTahun.setText("");
            cbKategori.setSelectedIndex(0);
            txtStok.setText("");
            lblCover.setIcon(null);
            lblCover.setText("No Image");
            txtIdBuku.requestFocus();
        });

        btnSimpan.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Data Buku Berhasil Disimpan (Dummy Mode)");
        });

        btnEdit.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Data Buku Berhasil Diperbarui (Dummy Mode)");
        });

        btnHapus.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Hapus data buku ini?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Data Buku Berhasil Dihapus (Dummy Mode)");
            }
        });

        btnCari.addActionListener(e -> tampilData(txtCari.getText()));
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
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnTambah;
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

    
    
    
        
        
        
        
        
        

        
            
            
        
            
            
        

        
            
            
        

        
    

    