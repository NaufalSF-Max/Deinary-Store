import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    /**
     * Creates new form Admin
     */
    public Admin() {
        initComponents();
        displayProducts();
        
        addButton.addActionListener(e -> {
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
        });



        updateButton.addActionListener(e -> {
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
        });



        deleteButton.addActionListener(e -> {
            int id = Integer.parseInt(idField.getText());
            deleteProduct(id);
        });

        importButton.addActionListener(e -> {
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
        });

    }
    
    private byte[] imageToByteArray(ImageIcon icon) {
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
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
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
        Resep = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
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

        Menu.setBackground(new java.awt.Color(0, 51, 51));

        jLabel1.setFont(new java.awt.Font("Poppins ExtraBold", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("DEINARY STORE");

        jLabel2.setBackground(new java.awt.Color(0, 51, 51));
        jLabel2.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 51, 51));
        jLabel2.setText("Welcome,");

        jLabel3.setBackground(new java.awt.Color(0, 51, 51));
        jLabel3.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 51, 51));
        jLabel3.setText("Admin123");

        jButton1.setBackground(new java.awt.Color(0, 51, 51));
        jButton1.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Kelola Produk");

        jButton2.setBackground(new java.awt.Color(0, 51, 51));
        jButton2.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Customer Service");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 51, 51));
        jButton3.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Tambah Resep");

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
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 9, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(MenuLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(MenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(jLabel3))
                .addGap(68, 68, 68)
                .addComponent(jButton1)
                .addGap(44, 44, 44)
                .addComponent(jButton2)
                .addGap(34, 34, 34)
                .addComponent(jButton3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Id Product");
        KelolaProduk.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 292, 77, -1));

        jLabel5.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Nama Produk");
        KelolaProduk.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 332, -1, -1));

        jLabel6.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Stok");
        KelolaProduk.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(393, 292, 53, -1));

        jLabel7.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Harga");
        KelolaProduk.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(393, 332, 72, -1));

        jLabel8.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
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

        KelolaProduk.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 0, 930, 271));

        jLabel9.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Status");
        KelolaProduk.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(393, 372, 53, -1));

        CS.setBackground(new java.awt.Color(0, 153, 153));

        jLabel10.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Customer Service");

        importButton.setText("Import");

        lblImage.setBackground(new java.awt.Color(255, 255, 255));
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout CSLayout = new javax.swing.GroupLayout(CS);
        CS.setLayout(CSLayout);
        CSLayout.setHorizontalGroup(
            CSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CSLayout.createSequentialGroup()
                .addGap(400, 400, 400)
                .addComponent(jLabel10)
                .addContainerGap(428, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CSLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(CSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CSLayout.createSequentialGroup()
                        .addComponent(importButton)
                        .addGap(39, 39, 39)))
                .addGap(78, 78, 78))
        );
        CSLayout.setVerticalGroup(
            CSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CSLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 247, Short.MAX_VALUE)
                .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(importButton)
                .addGap(22, 22, 22))
        );

        KelolaProduk.add(CS, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 940, 500));

        Resep.setBackground(new java.awt.Color(255, 153, 0));

        jLabel11.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Resep");

        javax.swing.GroupLayout ResepLayout = new javax.swing.GroupLayout(Resep);
        Resep.setLayout(ResepLayout);
        ResepLayout.setHorizontalGroup(
            ResepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ResepLayout.createSequentialGroup()
                .addGap(426, 426, 426)
                .addComponent(jLabel11)
                .addContainerGap(460, Short.MAX_VALUE))
        );
        ResepLayout.setVerticalGroup(
            ResepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ResepLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel11)
                .addContainerGap(448, Short.MAX_VALUE))
        );

        KelolaProduk.add(Resep, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 930, 500));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(Menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(KelolaProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(KelolaProduk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Menu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void statusComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_statusComboBoxActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_updateButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // TODO add your handling code here:
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
    private javax.swing.JPanel CS;
    private javax.swing.JPanel KelolaProduk;
    private javax.swing.JPanel Menu;
    private javax.swing.JPanel Resep;
    private javax.swing.JButton addButton;
    private javax.swing.JComboBox<String> categoryComboBox;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField idField;
    private javax.swing.JButton importButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JFileChooser jFileChooser2;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JFrame jFrame2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
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
    private javax.swing.JLabel lblImage;
    private javax.swing.JTextField nameField;
    private javax.swing.JTextField priceField;
    private javax.swing.JTable productTable;
    private javax.swing.JComboBox<String> statusComboBox;
    private javax.swing.JTextField stockField;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
