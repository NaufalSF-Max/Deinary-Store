import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import java.sql.Statement;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class Admin extends javax.swing.JFrame {
    private String currentUsername;

    public Admin() {
        this.currentUsername = "Admin123";
        initComponents();
        displayProducts();
        
        // Set nama pengguna ke label
        nameLabel.setText(currentUsername);
    
        customerService.setVisible(false);
        resepAdmin.setVisible(false);

    }
    
    public Admin(String username) {
        this.currentUsername = username;
        initComponents();
        displayProducts();
        
        // Set nama pengguna ke label
        nameLabel.setText(username);
    
        customerService.setVisible(false);
        resepAdmin.setVisible(false);

    }
    
    private byte []imageToByteArray(ImageIcon icon) {
        try {
            // Konversi ImageIcon menjadi BufferedImage
            Image image = icon.getImage();
            BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_RGB
            );
            bufferedImage.getGraphics().drawImage(image, 0, 0, null);

            // Tuliskan BufferedImage ke ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos); // Gunakan format JPG
            return baos.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void searchProducts(String keyword) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Query pencarian berdasarkan ID atau nama produk
            String query = "SELECT * FROM products WHERE id LIKE ? OR name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + keyword + "%"); // Pencarian ID
            stmt.setString(2, "%" + keyword + "%"); // Pencarian Nama

            ResultSet rs = stmt.executeQuery();

            // Reset tabel
            DefaultTableModel model = (DefaultTableModel) productTable.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String category = rs.getString("category");
                int stock = rs.getInt("stock");
                double price = rs.getDouble("price");
                String status = rs.getString("status");

                model.addRow(new Object[]{id, name, category, stock, price, status});
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Tidak ada produk yang ditemukan!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mencari produk: " + ex.getMessage());
        }
    }

    private void loadProductsToTable() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM products";
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(query);

            DefaultTableModel model = (DefaultTableModel) productTable.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String category = rs.getString("category");
                int stock = rs.getInt("stock");
                double price = rs.getDouble("price");
                String status = rs.getString("status");

                model.addRow(new Object[]{id, name, category, stock, price, status});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal memuat data produk!");
        }
    }
    
    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String imagePath = selectedFile.getAbsolutePath(); // Dapatkan path gambar

            // Tampilkan gambar di JLabel sebagai preview
            ImageIcon icon = new ImageIcon(new ImageIcon(imagePath).getImage()
                    .getScaledInstance(lblImage.getWidth(), lblImage.getHeight(), Image.SCALE_SMOOTH));
            lblImage.setIcon(icon);
        }
    }
  
    private void deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product deleted successfully!");
            displayProducts(); // Refresh tabel
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete product: " + ex.getMessage());
        }
    }
 
    private void displayProducts() {
        String sql = "SELECT * FROM products";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            DefaultTableModel model = (DefaultTableModel) productTable.getModel();
            model.setRowCount(0); // Reset tabel

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String category = rs.getString("category");
                int stock = rs.getInt("stock");
                double price = rs.getDouble("price");
                String status = rs.getString("status");

                model.addRow(new Object[]{id, name, category, stock, price, status});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve products: " + ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jFrame2 = new javax.swing.JFrame();
        jFileChooser2 = new javax.swing.JFileChooser();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jPanel1 = new javax.swing.JPanel();
        Menu = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        kelolaProdukBtn = new javax.swing.JButton();
        customerSbtn = new javax.swing.JButton();
        tambahResep = new javax.swing.JButton();
        isiAdmin = new javax.swing.JPanel();
        KelolaProduk = new javax.swing.JPanel();
        idField = new javax.swing.JTextField();
        nameField = new javax.swing.JTextField();
        categoryComboBox = new javax.swing.JComboBox<>();
        stockField = new javax.swing.JTextField();
        priceField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        addButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        statusComboBox = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        productTable = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        CS = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        importButton = new javax.swing.JButton();
        lblImage = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        searchField = new javax.swing.JTextArea();
        searchButton = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        BackgroundKelolaProduk = new javax.swing.JLabel();
        resepAdmin = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jLabel18 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        BackgroundResep = new javax.swing.JLabel();
        customerService = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jawabKeluhanBtn = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        backgroundCustomerService = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jFrame2Layout = new javax.swing.GroupLayout(jFrame2.getContentPane());
        jFrame2.getContentPane().setLayout(jFrame2Layout);
        jFrame2Layout.setHorizontalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame2Layout.setVerticalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        jMenu3.setText("File");
        jMenuBar2.add(jMenu3);

        jMenu4.setText("Edit");
        jMenuBar2.add(jMenu4);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Menu.setBackground(new java.awt.Color(67, 85, 38));

        jLabel1.setFont(new java.awt.Font("Poppins ExtraBold", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("DEINARY STORE");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Welcome,");

        nameLabel.setBackground(new java.awt.Color(0, 51, 51));
        nameLabel.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        nameLabel.setForeground(new java.awt.Color(255, 255, 255));

        kelolaProdukBtn.setBackground(new java.awt.Color(210, 227, 157));
        kelolaProdukBtn.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        kelolaProdukBtn.setForeground(new java.awt.Color(67, 85, 38));
        kelolaProdukBtn.setText("Kelola Produk");
        kelolaProdukBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kelolaProdukBtnActionPerformed(evt);
            }
        });

        customerSbtn.setBackground(new java.awt.Color(210, 227, 157));
        customerSbtn.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        customerSbtn.setForeground(new java.awt.Color(67, 85, 38));
        customerSbtn.setText("Customer Service");
        customerSbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerSbtnActionPerformed(evt);
            }
        });

        tambahResep.setBackground(new java.awt.Color(210, 227, 157));
        tambahResep.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        tambahResep.setForeground(new java.awt.Color(67, 85, 38));
        tambahResep.setText("Tambah Resep");
        tambahResep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahResepActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MenuLayout = new javax.swing.GroupLayout(Menu);
        Menu.setLayout(MenuLayout);
        MenuLayout.setHorizontalGroup(
            MenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuLayout.createSequentialGroup()
                .addGroup(MenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MenuLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(MenuLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(MenuLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(MenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tambahResep, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customerSbtn)
                    .addComponent(kelolaProdukBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MenuLayout.setVerticalGroup(
            MenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(nameLabel))
                .addGap(94, 94, 94)
                .addComponent(kelolaProdukBtn)
                .addGap(18, 18, 18)
                .addComponent(customerSbtn)
                .addGap(18, 18, 18)
                .addComponent(tambahResep)
                .addContainerGap(197, Short.MAX_VALUE))
        );

        isiAdmin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        KelolaProduk.setBackground(new java.awt.Color(0, 51, 51));
        KelolaProduk.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        idField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idFieldActionPerformed(evt);
            }
        });
        KelolaProduk.add(idField, new org.netbeans.lib.awtextra.AbsoluteConstraints(133, 289, 153, -1));

        nameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameFieldActionPerformed(evt);
            }
        });
        KelolaProduk.add(nameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(133, 329, 153, -1));

        categoryComboBox.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        categoryComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sayur", "Buah", "Serealia", "Umbi-umbian", "Kacang-Kacangan", "Rempah" }));
        categoryComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryComboBoxActionPerformed(evt);
            }
        });
        KelolaProduk.add(categoryComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(133, 369, 153, -1));

        stockField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockFieldActionPerformed(evt);
            }
        });
        KelolaProduk.add(stockField, new org.netbeans.lib.awtextra.AbsoluteConstraints(471, 289, 148, -1));

        priceField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                priceFieldActionPerformed(evt);
            }
        });
        KelolaProduk.add(priceField, new org.netbeans.lib.awtextra.AbsoluteConstraints(471, 329, 148, -1));

        jLabel4.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(67, 85, 38));
        jLabel4.setText("Id Product");
        KelolaProduk.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 292, 77, -1));

        jLabel5.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(67, 85, 38));
        jLabel5.setText("Nama Produk");
        KelolaProduk.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 332, -1, -1));

        jLabel6.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(67, 85, 38));
        jLabel6.setText("Stok");
        KelolaProduk.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(393, 292, 53, -1));

        jLabel7.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(67, 85, 38));
        jLabel7.setText("Harga");
        KelolaProduk.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(393, 332, 72, -1));

        jLabel8.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(67, 85, 38));
        jLabel8.setText("Kategori");
        KelolaProduk.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 372, 77, -1));

        addButton.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        KelolaProduk.add(addButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(133, 420, -1, -1));

        updateButton.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        updateButton.setText("Update");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });
        KelolaProduk.add(updateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(238, 420, -1, -1));

        deleteButton.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        deleteButton.setText("Hapus");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        KelolaProduk.add(deleteButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(393, 420, -1, -1));

        statusComboBox.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        statusComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Available", "Not available" }));
        statusComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusComboBoxActionPerformed(evt);
            }
        });
        KelolaProduk.add(statusComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(471, 369, 148, -1));

        productTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Id Product", "Nama Produk", "Kategori", "Stok", "Harga", "Status"
            }
        ));
        jScrollPane1.setViewportView(productTable);

        KelolaProduk.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 71, 930, 200));

        jLabel9.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(67, 85, 38));
        jLabel9.setText("Status");
        KelolaProduk.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(393, 372, 53, -1));

        CS.setBackground(new java.awt.Color(255, 255, 255));
        CS.setLayout(null);

        jLabel10.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(67, 85, 38));
        jLabel10.setText("Kelola Produk");
        CS.add(jLabel10);
        jLabel10.setBounds(6, 6, 110, 19);

        importButton.setText("Import");
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });
        CS.add(importButton);
        importButton.setBounds(742, 443, 72, 23);

        lblImage.setBackground(new java.awt.Color(255, 255, 255));
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        CS.add(lblImage);
        lblImage.setBounds(712, 287, 150, 150);

        searchField.setColumns(20);
        searchField.setRows(5);
        jScrollPane2.setViewportView(searchField);

        CS.add(jScrollPane2);
        jScrollPane2.setBounds(56, 34, 835, 24);

        searchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/deinarystore/img/searchIcon.png"))); // NOI18N
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        CS.add(searchButton);
        searchButton.setBounds(897, 25, 34, 33);

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(67, 85, 38));
        jLabel12.setText("Cari");
        CS.add(jLabel12);
        jLabel12.setBounds(7, 34, 43, 24);
        CS.add(BackgroundKelolaProduk);
        BackgroundKelolaProduk.setBounds(0, 0, 960, 510);

        KelolaProduk.add(CS, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 940, 500));

        isiAdmin.add(KelolaProduk, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 940, 500));

        resepAdmin.setBackground(new java.awt.Color(255, 255, 255));
        resepAdmin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        jScrollPane5.setViewportView(jTable2);

        resepAdmin.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 40, 1020, 207));

        jLabel11.setFont(new java.awt.Font("Poppins SemiBold", 0, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 204, 204));
        jLabel11.setText("Resepku");
        resepAdmin.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 0, 120, -1));
        resepAdmin.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(134, 277, 268, -1));

        jButton1.setText("Add");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        resepAdmin.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 430, -1, -1));

        jButton2.setText("Hapus");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        resepAdmin.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 430, -1, -1));

        jButton3.setText("Edit");
        resepAdmin.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 430, -1, -1));

        jLabel17.setBackground(new java.awt.Color(255, 204, 51));
        jLabel17.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(67, 85, 38));
        jLabel17.setText("Nama Resep");
        resepAdmin.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 277, -1, -1));

        jTextArea3.setColumns(20);
        jTextArea3.setRows(5);
        jScrollPane6.setViewportView(jTextArea3);

        resepAdmin.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 290, 268, 99));

        jLabel18.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(67, 65, 27));
        jLabel18.setText("Tambah Resep ");
        resepAdmin.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 270, -1, -1));

        jLabel22.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel22.setText("Id Resep");
        jLabel22.setToolTipText("");
        resepAdmin.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, -1, -1));
        resepAdmin.add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 320, 260, -1));
        resepAdmin.add(BackgroundResep, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        isiAdmin.add(resepAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 940, 500));

        customerService.setBackground(new java.awt.Color(255, 255, 255));
        customerService.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Poppins SemiBold", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(67, 85, 38));
        jLabel13.setText("Customer Service\n");
        customerService.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, 180, -1));

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane3.setViewportView(jTextArea2);

        customerService.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 368, 335, -1));

        jawabKeluhanBtn.setBackground(new java.awt.Color(67, 85, 38));
        jawabKeluhanBtn.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jawabKeluhanBtn.setForeground(new java.awt.Color(255, 255, 255));
        jawabKeluhanBtn.setText("Kirim");
        jawabKeluhanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jawabKeluhanBtnActionPerformed(evt);
            }
        });
        customerService.add(jawabKeluhanBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(269, 460, -1, -1));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "username", "Nama", "email", "Subject", "Keluhan", "Tanggapan"
            }
        ));
        jScrollPane4.setViewportView(jTable1);

        customerService.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 43, 1029, 250));

        jLabel14.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(67, 85, 38));
        jLabel14.setText("Tanggapan Keluhan");
        customerService.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 312, 145, -1));

        jLabel15.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(67, 85, 38));
        jLabel15.setText("Kepada");
        customerService.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 343, 66, -1));

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        customerService.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 340, 251, -1));
        customerService.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(847, 361, -1, 140));
        customerService.add(backgroundCustomerService, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        isiAdmin.add(customerService, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 950, 500));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(Menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(isiAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(isiAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 7, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void customerSbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customerSbtnActionPerformed
        KelolaProduk.setVisible(false);
        resepAdmin.setVisible(false);
        customerService.setVisible(true);
        // Hapus semua komponen dalam isiAdmin
        isiAdmin.removeAll();

        // Set tata letak baru
        isiAdmin.setLayout(new BorderLayout());

        // Tambahkan panel KelolaProduk
        isiAdmin.add(customerService, BorderLayout.CENTER);

        // Tampilkan ulang panel
        isiAdmin.repaint();
        isiAdmin.revalidate();
    }//GEN-LAST:event_customerSbtnActionPerformed

    private void statusComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_statusComboBoxActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int id = Integer.parseInt(idField.getText());
            deleteProduct(id);
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        try (Connection conn = DatabaseConnection.getConnection()) {
                String idText = idField.getText().trim();
                String nameText = nameField.getText().trim();

                if (idText.isEmpty() && nameText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Gagal mengupdate produk! Masukkan ID atau Nama produk untuk pencarian.");
                    return;
                }

                // Query untuk mencari produk berdasarkan ID atau Nama
                String searchQuery = "SELECT * FROM products WHERE id = ? OR name = ?";
                PreparedStatement searchStmt = conn.prepareStatement(searchQuery);
                if (!idText.isEmpty()) {
                    searchStmt.setInt(1, Integer.parseInt(idText));
                } else {
                    searchStmt.setNull(1, java.sql.Types.INTEGER);
                }
                searchStmt.setString(2, nameText);
                ResultSet rs = searchStmt.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(null, "Produk tidak ditemukan! Periksa kembali ID atau Nama produk.");
                    return;
                }

                // Ambil input dari user untuk data yang akan diupdate
                String newName = nameField.getText().trim();
                String category = categoryComboBox.getSelectedItem().toString();
                String stockText = stockField.getText().trim();
                String priceText = priceField.getText().trim();
                String status = statusComboBox.getSelectedItem().toString();

                // Update hanya field yang diisi
                String updateQuery = "UPDATE products SET ";
                if (!newName.isEmpty()) updateQuery += "name = ?, ";
                if (!category.isEmpty()) updateQuery += "category = ?, ";
                if (!stockText.isEmpty()) updateQuery += "stock = ?, ";
                if (!priceText.isEmpty()) updateQuery += "price = ?, ";
                if (!status.isEmpty()) updateQuery += "status = ?, ";
                updateQuery = updateQuery.substring(0, updateQuery.length() - 2); // Hapus koma terakhir
                updateQuery += " WHERE id = ? OR name = ?";

                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                int paramIndex = 1;

                if (!newName.isEmpty()) updateStmt.setString(paramIndex++, newName);
                if (!category.isEmpty()) updateStmt.setString(paramIndex++, category);
                if (!stockText.isEmpty()) updateStmt.setInt(paramIndex++, Integer.parseInt(stockText));
                if (!priceText.isEmpty()) updateStmt.setDouble(paramIndex++, Double.parseDouble(priceText));
                if (!status.isEmpty()) updateStmt.setString(paramIndex++, status);

                if (!idText.isEmpty()) {
                    updateStmt.setInt(paramIndex++, Integer.parseInt(idText));
                } else {
                    updateStmt.setNull(paramIndex++, java.sql.Types.INTEGER);
                }
                updateStmt.setString(paramIndex, nameText);

                updateStmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Produk berhasil diupdate!");
                loadProductsToTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Gagal mengupdate produk! " + ex.getMessage());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Gagal mengupdate produk! Stok dan Harga harus berupa angka.");
            }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        try (Connection conn = DatabaseConnection.getConnection()) {
                // Ambil input dari user
                String idText = idField.getText().trim();
                String name = nameField.getText().trim();
                String category = categoryComboBox.getSelectedItem().toString();
                String stockText = stockField.getText().trim();
                String priceText = priceField.getText().trim();
                String status = statusComboBox.getSelectedItem().toString();

                // Validasi jika ada field yang kosong
                if (idText.isEmpty() || name.isEmpty() || category.isEmpty() || stockText.isEmpty() || priceText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Gagal menyimpan produk! Anda harus mengisi semua data produk.");
                    return;
                }

                // Validasi ID harus angka
                int id;
                try {
                    id = Integer.parseInt(idText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Gagal menyimpan produk! ID harus berupa angka.");
                    return;
                }

                // Validasi jika gambar belum diimpor
                ImageIcon icon = (ImageIcon) lblImage.getIcon();
                if (icon == null) {
                    JOptionPane.showMessageDialog(null, "Gagal menyimpan produk! Anda harus mengisi semua data produk termasuk gambar.");
                    return;
                }

                // Validasi duplikasi ID atau Nama
                String checkQuery = "SELECT * FROM products WHERE id = ? OR name = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setInt(1, id);
                checkStmt.setString(2, name);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "Gagal menyimpan produk! Id atau Nama produk telah terpakai.");
                    return;
                }

                // Lanjutkan jika semua validasi lolos
                int stock = Integer.parseInt(stockText);
                double price = Double.parseDouble(priceText);

                byte[] imageBytes = imageToByteArray(icon);

                String sql = "INSERT INTO products (id, name, category, stock, price, status, image_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3, category);
                pstmt.setInt(4, stock);
                pstmt.setDouble(5, price);
                pstmt.setString(6, status);
                pstmt.setBytes(7, imageBytes);

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Produk berhasil ditambahkan!");
                loadProductsToTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Gagal menyimpan produk! " + ex.getMessage());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Gagal menyimpan produk! Stok dan Harga harus berupa angka.");
            }
    }//GEN-LAST:event_addButtonActionPerformed

    private void priceFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_priceFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_priceFieldActionPerformed

    private void stockFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stockFieldActionPerformed

    private void categoryComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_categoryComboBoxActionPerformed

    private void nameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameFieldActionPerformed

    private void idFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idFieldActionPerformed

    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png"));
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    Image image = ImageIO.read(file);
                    // Resize gambar agar sesuai dengan ukuran lblImage
                    Image scaledImage = image.getScaledInstance(lblImage.getWidth(), lblImage.getHeight(), Image.SCALE_SMOOTH);
                    lblImage.setIcon(new ImageIcon(scaledImage));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Gagal memuat gambar!");
                }
            }
    }//GEN-LAST:event_importButtonActionPerformed

    private void kelolaProdukBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kelolaProdukBtnActionPerformed
        customerService.setVisible(false);
        resepAdmin.setVisible(false);
        KelolaProduk.setVisible(true);
        // Hapus semua komponen dalam isiAdmin
        isiAdmin.removeAll();

        // Set tata letak baru
        isiAdmin.setLayout(new BorderLayout());

        // Tambahkan panel KelolaProduk
        isiAdmin.add(KelolaProduk, BorderLayout.CENTER);

        // Tampilkan ulang panel
        isiAdmin.repaint();
        isiAdmin.revalidate();     
    }//GEN-LAST:event_kelolaProdukBtnActionPerformed

    private void tambahResepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tambahResepActionPerformed
        customerService.setVisible(false);
        KelolaProduk.setVisible(false);
        resepAdmin.setVisible(true);
        // Hapus semua komponen dalam isiAdmin
        isiAdmin.removeAll();

        // Set tata letak baru
        isiAdmin.setLayout(new BorderLayout());

        // Tambahkan panel KelolaProduk
        isiAdmin.add(resepAdmin, BorderLayout.CENTER);

        // Tampilkan ulang panel
        isiAdmin.repaint();
        isiAdmin.revalidate();
    }//GEN-LAST:event_tambahResepActionPerformed

    private void jawabKeluhanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jawabKeluhanBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jawabKeluhanBtnActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String keyword = searchField.getText().trim();
        searchProducts(keyword);
    }//GEN-LAST:event_searchButtonActionPerformed

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
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Admin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BackgroundKelolaProduk;
    private javax.swing.JLabel BackgroundResep;
    private javax.swing.JPanel CS;
    private javax.swing.JPanel KelolaProduk;
    private javax.swing.JPanel Menu;
    private javax.swing.JButton addButton;
    private javax.swing.JLabel backgroundCustomerService;
    private javax.swing.JComboBox<String> categoryComboBox;
    private javax.swing.JButton customerSbtn;
    private javax.swing.JPanel customerService;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField idField;
    private javax.swing.JButton importButton;
    private javax.swing.JPanel isiAdmin;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JFileChooser jFileChooser2;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JFrame jFrame2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JButton jawabKeluhanBtn;
    private javax.swing.JButton kelolaProdukBtn;
    private javax.swing.JLabel lblImage;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField priceField;
    private javax.swing.JTable productTable;
    private javax.swing.JPanel resepAdmin;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextArea searchField;
    private javax.swing.JComboBox<String> statusComboBox;
    private javax.swing.JTextField stockField;
    private javax.swing.JButton tambahResep;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
