import java.awt.Image;
import java.awt.Color;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import java.util.Date;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;


public class MenuUtama extends javax.swing.JFrame {
    private int currentBuyerId; // ID pembeli dari sesi login
    private String activeCategory = null; // Menyimpan kategori aktif
    private String activeSearchText = null; // Menyimpan teks pencarian aktif
    private JCheckBox[] checkboxes; // Simpan semua checkbox
    private int[] productIds; // Simpan ID produk yang sesuai dengan checkbox

    
    public MenuUtama() {
        this.currentBuyerId = 4; // Nilai default
        initComponents();
        keranjangHal.setVisible(false);
        resepHal.setVisible(false);

        isiMenu.removeAll(); 
        isiMenu.setLayout(new BorderLayout());
        isiMenu.add(beranda, BorderLayout.CENTER); 
        isiMenu.revalidate(); 
        isiMenu.repaint();
        displayProducts(null, null, null);
        
        waktuPengantaran.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.DAY_OF_WEEK));
        waktuPengantaran.setEditor(new JSpinner.DateEditor(waktuPengantaran, "dd/MM/yyyy HH:mm"));

    }

    public MenuUtama(int buyerId) {
        this.currentBuyerId = buyerId; // Nilai default
        initComponents();
        keranjangHal.setVisible(false);
        resepHal.setVisible(false);

        isiMenu.removeAll(); 
        isiMenu.setLayout(new BorderLayout());
        isiMenu.add(beranda, BorderLayout.CENTER); 
        isiMenu.revalidate(); 
        isiMenu.repaint();
        displayProducts(null, null, null);
        
        waktuPengantaran.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.DAY_OF_WEEK));
        waktuPengantaran.setEditor(new JSpinner.DateEditor(waktuPengantaran, "dd/MM/yyyy HH:mm"));
    }
    
    private void displayProducts(String searchText, String priceFilter, String categoryFilter) {
        int totalProducts = 0;
        
        // Hitung total produk
        try (Connection conn = DatabaseConnection.getConnection()) {
            String countQuery = "SELECT COUNT(*) AS total FROM products WHERE status = 'Available'";
            PreparedStatement countStmt = conn.prepareStatement(countQuery);
            ResultSet countRs = countStmt.executeQuery();

            if (countRs.next()) {
                totalProducts = countRs.getInt("total");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menghitung total produk: " + ex.getMessage());
        }
        
        String orderBy = "RAND()"; // Default acak
        String whereClause = "status = 'Available'";
        boolean hasFilter = false;

        // Tambahkan kondisi filter pencarian
        if (searchText != null && !searchText.isEmpty()) {
            whereClause += " AND name LIKE ?";
            hasFilter = true;
        }

        // Tambahkan kondisi filter kategori
        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            whereClause += " AND category = ?";
            hasFilter = true;
        }

        // Tambahkan urutan berdasarkan filter harga
        if (priceFilter != null && !priceFilter.isEmpty()) {
            if ("Harga Terendah".equals(priceFilter)) {
                orderBy = "price ASC";
            } else if ("Harga Tertinggi".equals(priceFilter)) {
                orderBy = "price DESC";
            }
            hasFilter = true;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, name, price, image_path FROM products WHERE " + whereClause + " ORDER BY " + orderBy;
            PreparedStatement stmt = conn.prepareStatement(query);

            // Set parameter sesuai filter
            int paramIndex = 1;
            if (searchText != null && !searchText.isEmpty()) {
                stmt.setString(paramIndex++, "%" + searchText + "%");
            }
            if (categoryFilter != null && !categoryFilter.isEmpty()) {
                stmt.setString(paramIndex++, categoryFilter);
            }

            ResultSet rs = stmt.executeQuery();

            productPanelContainer.removeAll(); // Bersihkan produk lama
            productPanelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // FlowLayout tanpa celah bawah       

            while (rs.next()) {
                int productId = rs.getInt("id");
                String productName = rs.getString("name");
                double productPrice = rs.getDouble("price");

                // Membaca data BLOB dari kolom image_path
                byte[] imageBytes = rs.getBytes("image_path");
                ImageIcon icon = null;
                if (imageBytes != null && imageBytes.length > 0) {
                    try {
                        Image image = new ImageIcon(imageBytes).getImage();

                        // Menjaga aspect ratio gambar
                        int targetWidth = 150; // Lebar maksimum
                        int targetHeight = 100; // Tinggi maksimum
                        int imgWidth = image.getWidth(null);
                        int imgHeight = image.getHeight(null);

                        // Hitung ukuran baru berdasarkan aspect ratio
                        if (imgWidth > 0 && imgHeight > 0) {
                            double aspectRatio = (double) imgWidth / imgHeight;
                            if (aspectRatio > (double) targetWidth / targetHeight) {
                                targetHeight = (int) (targetWidth / aspectRatio);
                            } else {
                                targetWidth = (int) (targetHeight * aspectRatio);
                            }
                        }

                        Image scaledImage = image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(scaledImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Panel untuk setiap produk
                JPanel productPanel = new JPanel(new BorderLayout());
                productPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)); // Border tipis
                productPanel.setPreferredSize(new Dimension(180, 200)); // Ukuran panel lebih kecil

                // Nama Produk
                JLabel nameLabel = new JLabel(productName, JLabel.CENTER);
                nameLabel.setFont(nameLabel.getFont().deriveFont(14f)); // Font lebih kecil
                nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Margin untuk jarak

                // Gambar Produk
                JLabel imageLabel = new JLabel();
                imageLabel.setHorizontalAlignment(JLabel.CENTER);
                if (icon != null) {
                    imageLabel.setIcon(icon);
                } else {
                    imageLabel.setText("No Image");
                    imageLabel.setHorizontalAlignment(JLabel.CENTER);
                }

                productPanel.add(new JLabel("Rp " + String.format("%,.2f", productPrice))); // Harga produk

                // Tombol Wishlist dengan Ikon Love
                JButton wishlistButton = new JButton(new ImageIcon(
                    new ImageIcon(getClass().getResource("/deinarystore/img/love.png"))
                    .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)
                ));
                wishlistButton.setBorder(BorderFactory.createEmptyBorder()); // Hilangkan border
                wishlistButton.setContentAreaFilled(false); // Hilangkan background
                wishlistButton.addActionListener(e -> addToWishlist(productId));

                // Panel untuk wishlist di atas gambar
                JPanel wishlistPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                wishlistPanel.setOpaque(false); // Transparan
                wishlistPanel.add(wishlistButton);
                
                JLabel priceLabel = new JLabel("Rp " + String.format("%,.2f", productPrice));

                // Spinner untuk jumlah
                JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
                quantitySpinner.setMaximumSize(new Dimension(50, 25));

                // Tombol Tambah ke Keranjang
                JButton addButton = new JButton("Add");
                addButton.setFont(addButton.getFont().deriveFont(12f)); // Font lebih kecil
                addButton.addActionListener(e -> addToCart(productId, (int) quantitySpinner.getValue()));
                
                // Panel atas untuk nama di kiri dan harga di kanan
                JPanel topPanel = new JPanel(new BorderLayout());
                JLabel nameLabelLeft = new JLabel(productName, JLabel.LEFT); // Nama produk di kiri atas
                nameLabelLeft.setFont(nameLabelLeft.getFont().deriveFont(14f)); // Font lebih kecil
                nameLabelLeft.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0)); // Padding kiri
                topPanel.add(nameLabelLeft, BorderLayout.WEST);
                
                JLabel priceLabelRight = new JLabel("Rp " + String.format("%,.2f", productPrice), JLabel.RIGHT); // Harga di kanan atas
                priceLabelRight.setFont(priceLabelRight.getFont().deriveFont(14f)); // Font lebih kecil
                priceLabelRight.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5)); // Padding kanan
                topPanel.add(priceLabelRight, BorderLayout.EAST);

                /// Panel bawah (harga, spinner, tombol Add)
                JPanel bottomPanel = new JPanel(new BorderLayout());
                JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                spinnerPanel.add(quantitySpinner);
                bottomPanel.add(spinnerPanel, BorderLayout.CENTER); // Spinner di tengah
                bottomPanel.add(addButton, BorderLayout.EAST); // Tombol Add di kanan

                // Panel tengah untuk gambar dan wishlist
                JPanel centerPanel = new JPanel(new BorderLayout());
                centerPanel.add(wishlistPanel, BorderLayout.NORTH); // Wishlist di atas gambar
                centerPanel.add(imageLabel, BorderLayout.CENTER);

                // Tambahkan komponen ke panel produk
                productPanel.add(topPanel, BorderLayout.NORTH);
                productPanel.add(centerPanel, BorderLayout.CENTER);
                productPanel.add(bottomPanel, BorderLayout.PAGE_END);

                // Tambahkan panel produk ke container utama
                productPanelContainer.add(productPanel);
            }

            // Atur tinggi panelContainer berdasarkan jumlah produk
            int rowCount = (int) Math.ceil((double) totalProducts / 3); // 3 produk per baris
            int panelHeight = rowCount * 200; // 200 adalah tinggi setiap baris
            productPanelContainer.setPreferredSize(new Dimension(productPanelContainer.getWidth(), panelHeight));
            
            productPanelContainer.revalidate();
            productPanelContainer.repaint();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat produk: " + ex.getMessage());
        }
    }

    private void addToCart(int productId, int quantity) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Cek apakah produk sudah ada di keranjang
            String checkQuery = "SELECT quantity FROM carts WHERE id_buyer = ? AND product_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, currentBuyerId); // ID buyer
            checkStmt.setInt(2, productId);     // ID produk
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Jika produk sudah ada, update jumlahnya
                int existingQuantity = rs.getInt("quantity");
                String updateQuery = "UPDATE carts SET quantity = ? WHERE id_buyer = ? AND product_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, existingQuantity + quantity); // Tambahkan jumlah
                updateStmt.setInt(2, currentBuyerId);
                updateStmt.setInt(3, productId);
                updateStmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Jumlah produk di keranjang diperbarui!");
            } else {
                // Jika produk belum ada, tambahkan ke keranjang
                String insertQuery = "INSERT INTO carts (id_buyer, product_id, quantity) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, currentBuyerId);
                insertStmt.setInt(2, productId);
                insertStmt.setInt(3, quantity);
                insertStmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan ke keranjang!");
            }

            // Perbarui tampilan keranjang setelah perubahan
            displayCartItems();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan ke keranjang: " + ex.getMessage());
        }
    }

    private void addToWishlist(int productId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Cek apakah produk sudah ada di wishlist
            String checkQuery = "SELECT 1 FROM wishlists WHERE id_buyer = ? AND product_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, currentBuyerId); // ID buyer
            checkStmt.setInt(2, productId);     // ID produk
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Jika produk sudah ada, tampilkan pesan
                JOptionPane.showMessageDialog(this, "Produk sudah ada di wishlist Anda!");
            } else {
                // Jika produk belum ada, tambahkan ke wishlist
                String insertQuery = "INSERT INTO wishlists (id_buyer, product_id) VALUES (?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, currentBuyerId);
                insertStmt.setInt(2, productId);
                insertStmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan ke wishlist!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan ke wishlist: " + ex.getMessage());
        }
    }
    
    private JCheckBox[] getCheckboxesFromCart() {
        return checkboxes;
    }
    
    private int getProductIdFromCheckbox(JCheckBox checkbox) {
        for (int i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i] == checkbox) {
                return productIds[i];
            }
        }
        return -1; // Tidak ditemukan
    }
    
    private void displayCartItems() {
        panelKeranjangContainer.removeAll();
        panelKeranjangContainer.revalidate();
        panelKeranjangContainer.repaint();
        
        // Validasi: jika panel sudah memiliki komponen, hentikan proses
        if (panelKeranjangContainer.getComponentCount() > 0) {
            return; // Keluar dari metode untuk mencegah pengisian ulang
        }
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Query untuk mendapatkan data produk dalam keranjang
            String query = "SELECT carts.product_id, products.name, products.price, products.image_path, carts.quantity " +
                           "FROM carts " +
                           "INNER JOIN products ON carts.product_id = products.id " +
                           "WHERE carts.id_buyer = ?";

            // Membuat PreparedStatement dengan ResultSet yang dapat digulir
            PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setInt(1, currentBuyerId); // Mengatur ID pembeli

            ResultSet rs = stmt.executeQuery();

            // Mengecek apakah ada data di ResultSet
            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(this, "Keranjang kosong.");
                return;
            }

            // Hitung jumlah produk dalam keranjang untuk menginisialisasi array
            rs.last(); // Pindahkan cursor ke baris terakhir
            int productCount = rs.getRow(); // Ambil jumlah baris
            rs.beforeFirst(); // Kembali ke posisi awal

            checkboxes = new JCheckBox[productCount]; // Inisialisasi array checkboxes
            productIds = new int[productCount]; // Inisialisasi array untuk ID produk
            
            panelKeranjangContainer.removeAll();
            panelKeranjangContainer.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(15, 10, 15, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;

            int row = 0;
            Font font = new Font("Arial", Font.PLAIN, 17);
            
            int index = 0;

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                String productName = rs.getString("name");
                double productPrice = rs.getDouble("price");
                int quantity = rs.getInt("quantity");

                byte[] imageBytes = rs.getBytes("image_path");
                ImageIcon icon = null;
                if (imageBytes != null && imageBytes.length > 0) {
                    Image image = new ImageIcon(imageBytes).getImage();
                    icon = new ImageIcon(image.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                }

                JPanel cartItemPanel = new JPanel(new GridBagLayout());
                cartItemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                cartItemPanel.setPreferredSize(new Dimension(panelKeranjangContainer.getWidth(), 150));
                cartItemPanel.setMinimumSize(new Dimension(panelKeranjangContainer.getWidth(), 150));

                GridBagConstraints itemGbc = new GridBagConstraints();
                itemGbc.insets = new Insets(10, 10, 10, 10);
                itemGbc.fill = GridBagConstraints.HORIZONTAL;

                // Checkbox
                JCheckBox checkbox = new JCheckBox();
                checkboxes[index] = checkbox; // Simpan checkbox di array
                productIds[index] = productId; // Simpan ID produk di array
                itemGbc.gridx = 0;
                cartItemPanel.add(checkbox, itemGbc);

                // Gambar produk
                itemGbc.gridx = 1;
                itemGbc.weightx = 0.2;
                JLabel imageLabel = new JLabel();
                if (icon != null) {
                    imageLabel.setIcon(icon);
                } else {
                    imageLabel.setText("No Image");
                }
                cartItemPanel.add(imageLabel, itemGbc);

                // Informasi produk (nama dan harga)
                itemGbc.gridx = 2;
                itemGbc.weightx = 0.4;
                itemGbc.anchor = GridBagConstraints.WEST;
                JPanel productInfoPanel = new JPanel(new GridLayout(2, 1));
                JLabel nameLabel = new JLabel(productName);
                nameLabel.setFont(font);
                JLabel priceLabel = new JLabel("Harga: Rp " + String.format("%,.2f", productPrice));
                priceLabel.setForeground(Color.RED);
                productInfoPanel.add(nameLabel);
                productInfoPanel.add(priceLabel);
                cartItemPanel.add(productInfoPanel, itemGbc);

                // Total Harga
                itemGbc.gridx = 4;
                itemGbc.weightx = 0.2;
                itemGbc.anchor = GridBagConstraints.EAST;
                JLabel totalPriceLabel = new JLabel("Rp " + String.format("%,.2f", productPrice * quantity));
                totalPriceLabel.setPreferredSize(new Dimension(100, 30));
                totalPriceLabel.setFont(font);
                cartItemPanel.add(totalPriceLabel, itemGbc);
                
                // Spinner untuk Kuantitas
                itemGbc.gridx = 3;
                itemGbc.weightx = 0.1;
                JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(quantity, 0, 100, 1));
                quantitySpinner.setPreferredSize(new Dimension(50, 30));
                quantitySpinner.addChangeListener(e -> {
                    int newQuantity = (int) quantitySpinner.getValue();

                    // Jika kuantitas mencapai 1 dan user mencoba mengurangi lagi
                    if (newQuantity < 1) {
                        int confirmation = JOptionPane.showConfirmDialog(this,
                                "Apakah Anda ingin menghapus produk ini dari keranjang?",
                                "Konfirmasi Hapus",
                                JOptionPane.YES_NO_OPTION);
                        if (confirmation == JOptionPane.YES_OPTION) {
                            // Hapus produk dari database
                            try (Connection deleteConn = DatabaseConnection.getConnection()) {
                                String deleteQuery = "DELETE FROM carts WHERE id_buyer = ? AND product_id = ?";
                                PreparedStatement deleteStmt = deleteConn.prepareStatement(deleteQuery);
                                deleteStmt.setInt(1, currentBuyerId);
                                deleteStmt.setInt(2, productId); // ID produk dari checkbox atau spinner
                                deleteStmt.executeUpdate();
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(this, "Gagal menghapus produk: " + ex.getMessage());
                            }
                            displayCartItems(); // Refresh keranjang setelah menghapus produk
                            return; // Keluar dari listener untuk mencegah eksekusi lebih lanjut
                        } else {
                            // Jika user memilih tidak, kembalikan spinner ke nilai 1
                            quantitySpinner.setValue(1);
                        }
                    } else {
                        // Perbarui kuantitas produk di database
                        updateCartQuantity(productId, newQuantity);

                        // Jika checkbox aktif, hitung ulang total harga keseluruhan
                        if (checkbox.isSelected()) {
                            calculateTotalPrice(checkboxes);
                        }

                        // Update total harga produk
                        totalPriceLabel.setText("Rp " + String.format("%,.2f", productPrice * newQuantity));
                    }
                });



                cartItemPanel.add(quantitySpinner, itemGbc);

                // Tambahkan ActionListener ke Checkbox
                checkbox.addActionListener(e -> {
                    calculateTotalPrice(checkboxes); // Hitung ulang total harga keseluruhan
                });

                gbc.gridy = row++;
                panelKeranjangContainer.add(cartItemPanel, gbc);

                index++;
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat keranjang: " + ex.getMessage());
        } finally {
            panelKeranjangContainer.revalidate();
            panelKeranjangContainer.repaint();
        }
    }

    private void updateCartQuantity(int productId, int quantity) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE carts SET quantity = ? WHERE id_buyer = ? AND product_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, quantity);
            stmt.setInt(2, currentBuyerId);
            stmt.setInt(3, productId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui kuantitas: " + ex.getMessage());
        }
    }

    private void calculateTotalPrice(JCheckBox[] checkboxes) {
        double totalPrice = 0;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT carts.product_id, products.price, carts.quantity " +
                           "FROM carts " +
                           "INNER JOIN products ON carts.product_id = products.id " +
                           "WHERE carts.id_buyer = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, currentBuyerId);
            ResultSet rs = stmt.executeQuery();

            int index = 0;
            while (rs.next()) {
                if (checkboxes[index] != null && checkboxes[index].isSelected()) {
                    double productPrice = rs.getDouble("price");
                    int quantity = rs.getInt("quantity");
                    totalPrice += productPrice * quantity;
                }
                index++;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menghitung total harga: " + ex.getMessage());
        }

        // Update total harga keseluruhan di field
        totalHargaField.setText("Rp " + String.format("%,.2f", totalPrice));
    }

    private String generateTransactionId() {
        String prefix = "TG-DNRYSTR";
        String newTransactionId = "";
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT MAX(CAST(SUBSTRING(id_transaction, LENGTH(?) + 1) AS UNSIGNED)) AS max_id FROM transactions";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, prefix);
            ResultSet rs = stmt.executeQuery();
            int lastNumber = 0;
            if (rs.next()) {
                lastNumber = rs.getInt("max_id");
            }
            int newNumber = lastNumber + 1;
            newTransactionId = prefix + newNumber;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menghasilkan ID transaksi: " + ex.getMessage());
        }
        return newTransactionId;
    }

    private int getCartQuantity(int productId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT quantity FROM carts WHERE id_buyer = ? AND product_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, currentBuyerId); // ID pembeli saat ini
            stmt.setInt(2, productId);     // ID produk
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("quantity");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil jumlah produk: " + ex.getMessage());
        }
        return 0; // Jika tidak ditemukan, kembalikan 0
    }

    private void generateNota() {
        String transactionId = generateTransactionId(); // Generate ID transaksi
        int pointsEarned;
        StringBuilder nota = new StringBuilder();
        nota.append("========== Deinary Store ==========\n");
        nota.append("ID Buyer: ").append(currentBuyerId).append("\n");
        
        // Validasi ulang apakah ID transaksi sudah ada di database
        try (Connection conn = DatabaseConnection.getConnection()) {
            String validateQuery = "SELECT COUNT(*) AS count FROM transactions WHERE id_transaction = ?";
            PreparedStatement validateStmt = conn.prepareStatement(validateQuery);
            validateStmt.setString(1, transactionId);
            ResultSet validateRs = validateStmt.executeQuery();

            if (validateRs.next() && validateRs.getInt("count") > 0) {
                transactionId = generateTransactionId(); // Generate ulang ID jika sudah ada
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memvalidasi ID transaksi: " + ex.getMessage());
            return;
        }
    
        nota.append("ID Transaksi: ").append(transactionId).append("\n");
        nota.append("Tanggal: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())).append("\n");
        nota.append("===================================\n");

        double totalHarga = 0;
        
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Ambil alamat pengantaran default
            String defaultAddressQuery = "SELECT full_name, phone_number, province, city, district, subdistrict, postal_code, address_detail "
                                        + "FROM addresses WHERE id_buyer = ? AND is_default = 1";
            PreparedStatement addressStmt = conn.prepareStatement(defaultAddressQuery);
            addressStmt.setInt(1, currentBuyerId);
            ResultSet addressRs = addressStmt.executeQuery();

            if (addressRs.next()) {
                nota.append("Alamat Pengantaran:\n");
                nota.append("Nama: ").append(addressRs.getString("full_name")).append("\n");
                nota.append("No. Telp: ").append(addressRs.getString("phone_number")).append("\n");
                nota.append("Alamat: ").append(addressRs.getString("address_detail")).append("\n");
                nota.append(addressRs.getString("city")).append(", ").append(addressRs.getString("province"))
                    .append(", Kode Pos: ").append(addressRs.getString("postal_code")).append("\n");
                nota.append("===================================\n");
            } else {
                JOptionPane.showMessageDialog(this, "Alamat default tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ambil driver secara acak
            String randomDriverQuery = "SELECT username FROM drivers ORDER BY RAND() LIMIT 1";
            PreparedStatement driverStmt = conn.prepareStatement(randomDriverQuery);
            ResultSet driverRs = driverStmt.executeQuery();

            String driverUsername = "";
            if (driverRs.next()) {
                driverUsername = driverRs.getString("username");
                nota.append("Driver Pengirim: ").append(driverUsername).append("\n");
                nota.append("===================================\n");
            } else {
                JOptionPane.showMessageDialog(this, "Tidak ada driver tersedia.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Hitung total harga dan proses transaksi
            for (int i = 0; i < checkboxes.length; i++) {
                if (checkboxes[i] != null && checkboxes[i].isSelected()) {
                    int productId = productIds[i];
                    String query = "SELECT name, price FROM products WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, productId);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String namaProduk = rs.getString("name");
                        double harga = rs.getDouble("price");
                        int jumlah = getCartQuantity(productId);
                        double subTotal = harga * jumlah;
                        totalHarga += subTotal;

                        nota.append(namaProduk).append(" | ").append(jumlah).append(" x ").append(harga)
                            .append(" = ").append(String.format("%,.2f", subTotal)).append("\n");

                        // Kurangi stok produk
                        String updateStockQuery = "UPDATE products SET stock = stock - ? WHERE id = ?";
                        PreparedStatement updateStockStmt = conn.prepareStatement(updateStockQuery);
                        updateStockStmt.setInt(1, jumlah);
                        updateStockStmt.setInt(2, productId);
                        updateStockStmt.executeUpdate();

                        // Hapus item dari keranjang
                        String deleteCartQuery = "DELETE FROM carts WHERE id_buyer = ? AND product_id = ?";
                        PreparedStatement deleteCartStmt = conn.prepareStatement(deleteCartQuery);
                        deleteCartStmt.setInt(1, currentBuyerId);
                        deleteCartStmt.setInt(2, productId);
                        deleteCartStmt.executeUpdate();
                    }
                }
            }
            
            // Validasi jumlah uang
            double jumlahUang = Double.parseDouble(uangField.getText());
            double kembalian = jumlahUang - totalHarga;

            if (jumlahUang < totalHarga) {
                JOptionPane.showMessageDialog(this, "Uang anda kurang untuk melakukan pembelian!", "Error", JOptionPane.ERROR_MESSAGE);

                // Kembali ke keranjangHal tanpa perubahan
                keranjangHal.setVisible(true);
                isiMenu.removeAll();
                isiMenu.add(keranjangHal);
                isiMenu.revalidate();
                isiMenu.repaint();

                return; // Transaksi dibatalkan
            } else if (jumlahUang > totalHarga) {
                nota.append("===================================\n");
                nota.append("Total Harga: Rp ").append(String.format("%,.2f", totalHarga)).append("\n");
                nota.append("Jumlah Uang: Rp ").append(String.format("%,.2f", jumlahUang)).append("\n");
                nota.append("Kembalian: Rp ").append(String.format("%,.2f", kembalian)).append("\n");
            } else {
                nota.append("===================================\n");
                nota.append("Total Harga: Rp ").append(String.format("%,.2f", totalHarga)).append("\n");
                nota.append("Jumlah Uang: Rp ").append(String.format("%,.2f", jumlahUang)).append("\n");
                nota.append("Uang Anda pas. Tidak ada kembalian.\n");
            }

            
            // Tambahkan poin ke tabel buyers
            pointsEarned = (int) (totalHarga / 10000); // Contoh: 1 poin per Rp 10.000
            String updatePointsQuery = "UPDATE buyers SET points = points + ? WHERE id_buyer = ?";
            PreparedStatement updatePointsStmt = conn.prepareStatement(updatePointsQuery);
            updatePointsStmt.setInt(1, pointsEarned);
            updatePointsStmt.setInt(2, currentBuyerId);
            updatePointsStmt.executeUpdate();
            
            // Simpan transaksi ke database
            saveTransactionToDatabase(transactionId, currentBuyerId, totalHarga, (Date) waktuPengantaran.getValue(),
                    jumlahUang, kembalian, driverUsername, pointsEarned); 
            
            updateBuyerLevel(currentBuyerId);

         // Tambahkan detail nota
            nota.append("Poin yang Diperoleh: ").append(pointsEarned).append("\n");
            nota.append("===================================\n");
            nota.append("Terima Kasih Telah Membeli di Toko Kami\n");

            JOptionPane.showMessageDialog(this, nota.toString(), "Nota Digital", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal membuat nota: " + ex.getMessage());
        }
    }
    
    private boolean isValidAmount(String amount) {
        try {
            Double.parseDouble(amount);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private void saveTransactionToDatabase(String transactionId, int buyerId, double totalPrice, Date deliverySchedule, double buyerPayment, double changeAmount, String driverUsername, int pointsEarned) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Query untuk mendapatkan id_driver berdasarkan username
            String getDriverIdQuery = "SELECT id_user FROM drivers WHERE username = ?";
            PreparedStatement driverStmt = conn.prepareStatement(getDriverIdQuery);
            driverStmt.setString(1, driverUsername);
            ResultSet driverRs = driverStmt.executeQuery();

            int driverId = 0;
            if (driverRs.next()) {
                driverId = driverRs.getInt("id_user");
            } else {
                JOptionPane.showMessageDialog(this, "Driver tidak ditemukan dalam database.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Query untuk menyimpan transaksi
            String query = "INSERT INTO transactions (id_transaction, id_buyer, total_price, delivery_schedule, buyer_payment, change_amount, id_driver, points_earned) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, transactionId);
            stmt.setInt(2, buyerId);
            stmt.setDouble(3, totalPrice);
            stmt.setTimestamp(4, new java.sql.Timestamp(deliverySchedule.getTime()));
            stmt.setDouble(5, buyerPayment);
            stmt.setDouble(6, changeAmount);
            stmt.setInt(7, driverId); // Tambahkan id_driver
            stmt.setDouble(8, pointsEarned);
            stmt.executeUpdate();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBuyerLevel(int buyerId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Ambil poin saat ini
            String query = "SELECT points FROM buyers WHERE id_buyer = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, buyerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int points = rs.getInt("points");
                String level;

                // Tentukan level berdasarkan poin
                if (points < 1000) {
                    level = "Bronze";
                } else if (points < 5000) {
                    level = "Silver";
                } else if (points < 10000) {
                    level = "Gold";
                } else if (points < 20000) {
                    level = "Platinum";
                } else {
                    level = "Diamond";
                }

                // Perbarui level di database
                String updateQuery = "UPDATE buyers SET level_name = ? WHERE id_buyer = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, level);
                updateStmt.setInt(2, buyerId);
                updateStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal memperbarui level buyer: " + e.getMessage());
        }
    }

    
    private void updateProductStock(int productId, int quantity) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE products SET stock = stock - ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate stok produk: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollBar1 = new javax.swing.JScrollBar();
        jPanel1 = new javax.swing.JPanel();
        homeBtn = new javax.swing.JButton();
        resepHalBtn = new javax.swing.JButton();
        profilBtn = new javax.swing.JButton();
        keranjangBtn = new javax.swing.JButton();
        isiMenu = new javax.swing.JPanel();
        beranda = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        cmbFilter = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        buahBtn = new javax.swing.JButton();
        sayurBtn = new javax.swing.JButton();
        serealiaBtn = new javax.swing.JButton();
        kacangBtn = new javax.swing.JButton();
        umbiBtn = new javax.swing.JButton();
        rempahBtn = new javax.swing.JButton();
        kategoriLabel = new javax.swing.JLabel();
        searchBtn = new javax.swing.JButton();
        logoLabel = new javax.swing.JLabel();
        searchBarField = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        productPanelContainer = new javax.swing.JPanel();
        resepHal = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        keranjangHal = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        hapusBtn = new javax.swing.JButton();
        checkOutBtn = new javax.swing.JButton();
        totalHargaField = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        panelKeranjangContainer = new javax.swing.JPanel();
        uangLabel = new javax.swing.JLabel();
        uangField = new javax.swing.JTextField();
        jadwalLabel = new javax.swing.JLabel();
        waktuPengantaran = new javax.swing.JSpinner();
        jPanel5 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        homeBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/deinarystore/img/homeIcon.png"))); // NOI18N
        homeBtn.setMaximumSize(new java.awt.Dimension(48, 43));
        homeBtn.setMinimumSize(new java.awt.Dimension(48, 43));
        homeBtn.setPreferredSize(new java.awt.Dimension(48, 43));
        homeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeBtnActionPerformed(evt);
            }
        });
        jPanel1.add(homeBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 436, 200, 68));

        resepHalBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/deinarystore/img/resepIcon.png"))); // NOI18N
        resepHalBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resepHalBtnActionPerformed(evt);
            }
        });
        jPanel1.add(resepHalBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 436, 200, 68));

        profilBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/deinarystore/img/profilIcon.png"))); // NOI18N
        profilBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profilBtnActionPerformed(evt);
            }
        });
        jPanel1.add(profilBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(625, 436, 230, 68));

        keranjangBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/deinarystore/img/keranjangIcon.png"))); // NOI18N
        keranjangBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keranjangBtnActionPerformed(evt);
            }
        });
        jPanel1.add(keranjangBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(416, 436, 210, 68));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        cmbFilter.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        cmbFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Harga Terendah", "Harga Tertinggi", "Vitamin", " " }));
        cmbFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbFilterActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel2.setText("Filter");

        buahBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/deinarystore/img/fruitIcon.png"))); // NOI18N
        buahBtn.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        buahBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buahBtnActionPerformed(evt);
            }
        });

        sayurBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/deinarystore/img/sayurIcon.png"))); // NOI18N
        sayurBtn.setPreferredSize(new java.awt.Dimension(57, 57));
        sayurBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sayurBtnActionPerformed(evt);
            }
        });

        serealiaBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/deinarystore/img/serealiaIcon.png"))); // NOI18N
        serealiaBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serealiaBtnActionPerformed(evt);
            }
        });

        kacangBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/deinarystore/img/kacangIcon.png"))); // NOI18N
        kacangBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kacangBtnActionPerformed(evt);
            }
        });

        umbiBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/deinarystore/img/umbiIcon.png"))); // NOI18N
        umbiBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                umbiBtnActionPerformed(evt);
            }
        });

        rempahBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/deinarystore/img/rempahIcon.png"))); // NOI18N
        rempahBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rempahBtnActionPerformed(evt);
            }
        });

        kategoriLabel.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        kategoriLabel.setText("Kategori");

        searchBtn.setText("Search");
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });

        logoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/deinarystore/img/icon vege.png"))); // NOI18N

        searchBarField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBarFieldActionPerformed(evt);
            }
        });

        productPanelContainer.setBackground(new java.awt.Color(255, 255, 255));
        productPanelContainer.setLayout(new java.awt.GridBagLayout());
        jScrollPane5.setViewportView(productPanelContainer);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(kategoriLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buahBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sayurBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(serealiaBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(kacangBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(umbiBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rempahBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(searchBarField, javax.swing.GroupLayout.PREFERRED_SIZE, 728, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchBtn))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 796, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchBarField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(searchBtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(cmbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(logoLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(kategoriLabel)
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addGap(0, 13, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(buahBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(sayurBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(serealiaBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(kacangBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(umbiBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rempahBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 832, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(241, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel2);

        javax.swing.GroupLayout berandaLayout = new javax.swing.GroupLayout(beranda);
        beranda.setLayout(berandaLayout);
        berandaLayout.setHorizontalGroup(
            berandaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(berandaLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 864, Short.MAX_VALUE)
                .addContainerGap())
        );
        berandaLayout.setVerticalGroup(
            berandaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
        );

        resepHal.setPreferredSize(new java.awt.Dimension(870, 439));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "IdResep", "Nama Resep"
            }
        ));
        jScrollPane3.setViewportView(jTable2);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane4.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );

        jLabel10.setFont(new java.awt.Font("Poppins SemiBold", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(67, 85, 38));
        jLabel10.setText("Detail Resep");

        jLabel11.setFont(new java.awt.Font("Poppins SemiBold", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(67, 85, 38));
        jLabel11.setText("Deinary's Resep");

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/deinarystore/img/resepMenu.png"))); // NOI18N
        jLabel13.setPreferredSize(new java.awt.Dimension(435526, 435526));

        jLabel1.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(67, 85, 38));
        jLabel1.setText("Id Resep");

        jButton1.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jButton1.setText("Cari");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(430, 430, 430)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(373, 373, 373)
                                .addComponent(jLabel10))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 825, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton1))
                                .addGap(280, 280, 280)
                                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 1022, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel10))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout resepHalLayout = new javax.swing.GroupLayout(resepHal);
        resepHal.setLayout(resepHalLayout);
        resepHalLayout.setHorizontalGroup(
            resepHalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        resepHalLayout.setVerticalGroup(
            resepHalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        hapusBtn.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        hapusBtn.setText("Hapus");
        hapusBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hapusBtnActionPerformed(evt);
            }
        });

        checkOutBtn.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        checkOutBtn.setText("Check Out");
        checkOutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkOutBtnActionPerformed(evt);
            }
        });

        totalHargaField.setEditable(false);

        jLabel12.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel12.setText("Total Harga");

        javax.swing.GroupLayout panelKeranjangContainerLayout = new javax.swing.GroupLayout(panelKeranjangContainer);
        panelKeranjangContainer.setLayout(panelKeranjangContainerLayout);
        panelKeranjangContainerLayout.setHorizontalGroup(
            panelKeranjangContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 834, Short.MAX_VALUE)
        );
        panelKeranjangContainerLayout.setVerticalGroup(
            panelKeranjangContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 350, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(panelKeranjangContainer);

        uangLabel.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        uangLabel.setText("Masukkan Jumlah Uang");

        uangField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uangFieldActionPerformed(evt);
            }
        });

        jadwalLabel.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jadwalLabel.setText("Jadwal Pengantaran");

        waktuPengantaran.setModel(new javax.swing.SpinnerDateModel());

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(checkOutBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(hapusBtn))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(46, 46, 46)
                                .addComponent(totalHargaField, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(157, 157, 157)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(uangLabel)
                            .addComponent(jadwalLabel))
                        .addGap(39, 39, 39)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(uangField)
                            .addComponent(waktuPengantaran)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(totalHargaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jadwalLabel)
                        .addComponent(waktuPengantaran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(checkOutBtn)
                        .addComponent(hapusBtn))
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(uangField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(uangLabel)))
                .addGap(37, 37, 37))
        );

        javax.swing.GroupLayout keranjangHalLayout = new javax.swing.GroupLayout(keranjangHal);
        keranjangHal.setLayout(keranjangHalLayout);
        keranjangHalLayout.setHorizontalGroup(
            keranjangHalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        keranjangHalLayout.setVerticalGroup(
            keranjangHalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(keranjangHalLayout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 396, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 870, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 430, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout isiMenuLayout = new javax.swing.GroupLayout(isiMenu);
        isiMenu.setLayout(isiMenuLayout);
        isiMenuLayout.setHorizontalGroup(
            isiMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 873, Short.MAX_VALUE)
            .addGroup(isiMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(isiMenuLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(isiMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(beranda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(isiMenuLayout.createSequentialGroup()
                            .addGap(3, 3, 3)
                            .addGroup(isiMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(resepHal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(keranjangHal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        isiMenuLayout.setVerticalGroup(
            isiMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 844, Short.MAX_VALUE)
            .addGroup(isiMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(isiMenuLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(isiMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(resepHal, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(keranjangHal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(beranda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jPanel1.add(isiMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

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

    private void resepHalBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resepHalBtnActionPerformed
        beranda.setVisible(false);
        keranjangHal.setVisible(false);
        resepHal.setVisible(true);
        // Hapus semua komponen dalam isiAdmin
        isiMenu.removeAll();

        // Set tata letak baru
        isiMenu.setLayout(new BorderLayout());

        // Tambahkan panel KelolaProduk
        isiMenu.add(resepHal, BorderLayout.CENTER);

        // Tampilkan ulang panel
        isiMenu.repaint();
        isiMenu.revalidate();       
    }//GEN-LAST:event_resepHalBtnActionPerformed

    private void cmbFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFilterActionPerformed
        String priceFilter = (String) cmbFilter.getSelectedItem();
        displayProducts(activeSearchText, priceFilter, activeCategory);
    }//GEN-LAST:event_cmbFilterActionPerformed

    private void buahBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buahBtnActionPerformed
        if ("Buah".equals(activeCategory)) {
            // Reset ke tampilan default (produk acak)
            activeCategory = null;
            activeSearchText = null; // Reset pencarian
            displayProducts(null, null, null);
        } else {
            // Atur kategori aktif dan reset pencarian
            activeCategory = "Buah";
            activeSearchText = null;
            displayProducts(null, null, activeCategory); // Filter kategori
        }
    }//GEN-LAST:event_buahBtnActionPerformed

    private void kacangBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kacangBtnActionPerformed
        if ("Kacang-Kacangan".equals(activeCategory)) {
            // Reset ke tampilan default (produk acak)
            activeCategory = null;
            activeSearchText = null; // Reset pencarian
            displayProducts(null, null, null);
        } else {
            // Atur kategori aktif dan reset pencarian
            activeCategory = "Kacang-Kacangan";
            activeSearchText = null;
            displayProducts(null, null, activeCategory); // Filter kategori
        }
    }//GEN-LAST:event_kacangBtnActionPerformed

    private void umbiBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_umbiBtnActionPerformed
        if ("Umbi-Umbian".equals(activeCategory)) {
            // Reset ke tampilan default (produk acak)
            activeCategory = null;
            activeSearchText = null; // Reset pencarian
            displayProducts(null, null, null);
        } else {
            // Atur kategori aktif dan reset pencarian
            activeCategory = "Umbi-Umbian";
            activeSearchText = null;
            displayProducts(null, null, activeCategory); // Filter kategori
        }
    }//GEN-LAST:event_umbiBtnActionPerformed

    private void keranjangBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keranjangBtnActionPerformed
        beranda.setVisible(false);
        resepHal.setVisible(false);
        keranjangHal.setVisible(true);
        
        // Hapus semua komponen dalam isiAdmin
        isiMenu.removeAll();

        // Set tata letak baru
        isiMenu.setLayout(new BorderLayout());

        displayCartItems();
        
        // Tambahkan panel KelolaProduk
        isiMenu.add(keranjangHal, BorderLayout.CENTER);
        
        // Panggil ulang metode untuk menampilkan item di keranjang
        displayCartItems();

        // Refresh tampilan
        panelKeranjangContainer.revalidate();
        panelKeranjangContainer.repaint();
    }//GEN-LAST:event_keranjangBtnActionPerformed

    private void rempahBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rempahBtnActionPerformed
        if ("Rempah".equals(activeCategory)) {
            // Reset ke tampilan default (produk acak)
            activeCategory = null;
            activeSearchText = null; // Reset pencarian
            displayProducts(null, null, null);
        } else {
            // Atur kategori aktif dan reset pencarian
            activeCategory = "Sayur";
            activeSearchText = null;
            displayProducts(null, null, activeCategory); // Filter kategori
        }
    }//GEN-LAST:event_rempahBtnActionPerformed

    private void homeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeBtnActionPerformed
        keranjangHal.setVisible(false);
        resepHal.setVisible(false);
        beranda.setVisible(true);

        // Hapus semua komponen dalam isiAdmin
        isiMenu.removeAll();

        // Set tata letak baru
        isiMenu.setLayout(new BorderLayout());

        // Tambahkan panel KelolaProduk
        isiMenu.add(beranda, BorderLayout.CENTER);

        // Tampilkan ulang panel
        isiMenu.repaint();
        isiMenu.revalidate();
    }//GEN-LAST:event_homeBtnActionPerformed

    private void profilBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profilBtnActionPerformed
        ProfilPage Profil = new ProfilPage(currentBuyerId); // Membuka halaman
        Profil.setVisible(true);
        this.dispose(); // Tutup halaman 
    }//GEN-LAST:event_profilBtnActionPerformed

    private void searchBarFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBarFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchBarFieldActionPerformed

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        activeSearchText = searchBarField.getText().trim(); // Simpan teks pencarian aktif
        activeCategory = null; // Reset kategori
        displayProducts(activeSearchText, null, activeCategory);
    }//GEN-LAST:event_searchBtnActionPerformed

    private void serealiaBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serealiaBtnActionPerformed
        if ("Serealia".equals(activeCategory)) {
            // Reset ke tampilan default (produk acak)
            activeCategory = null;
            activeSearchText = null; // Reset pencarian
            displayProducts(null, null, null);
        } else {
            // Atur kategori aktif dan reset pencarian
            activeCategory = "Serealia";
            activeSearchText = null;
            displayProducts(null, null, activeCategory); // Filter kategori
        }
    }//GEN-LAST:event_serealiaBtnActionPerformed

    private void sayurBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sayurBtnActionPerformed
        if ("Sayur".equals(activeCategory)) {
            // Reset ke tampilan default (produk acak)
            activeCategory = null;
            activeSearchText = null; // Reset pencarian
            displayProducts(null, null, null);
        } else {
            // Atur kategori aktif dan reset pencarian
            activeCategory = "Sayur";
            activeSearchText = null;
            displayProducts(null, null, activeCategory); // Filter kategori
        }
    }//GEN-LAST:event_sayurBtnActionPerformed

    private void hapusBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hapusBtnActionPerformed
        // Cek apakah ada checkbox yang dipilih
        boolean isAnySelected = false;
        for (JCheckBox checkbox : checkboxes) {
            if (checkbox != null && checkbox.isSelected()) {
                isAnySelected = true;
                break;
            }
        }

        if (!isAnySelected) {
            JOptionPane.showMessageDialog(this, "Tidak ada produk yang dipilih untuk dihapus.");
            return;
        }

        // Tampilkan dialog konfirmasi
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus produk yang dipilih?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                for (int i = 0; i < checkboxes.length; i++) {
                    if (checkboxes[i] != null && checkboxes[i].isSelected()) {
                        int productId = productIds[i]; // ID produk yang sesuai dengan checkbox
                        String deleteQuery = "DELETE FROM carts WHERE id_buyer = ? AND product_id = ?";
                        PreparedStatement stmt = conn.prepareStatement(deleteQuery);
                        stmt.setInt(1, currentBuyerId);
                        stmt.setInt(2, productId);
                        stmt.executeUpdate();
                    }
                }
                JOptionPane.showMessageDialog(this, "Produk yang dipilih telah dihapus dari keranjang.");
                displayCartItems(); // Refresh tampilan keranjang
                calculateTotalPrice(checkboxes); // Hitung ulang total harga
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus produk: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Penghapusan produk dibatalkan.");
        }
    }//GEN-LAST:event_hapusBtnActionPerformed

    private void checkOutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkOutBtnActionPerformed
        if (checkboxes == null || checkboxes.length == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada produk yang dipilih, harap memilih produk terlebih dahulu!");
            return;
        }
        if (waktuPengantaran.getValue() == null) {
            JOptionPane.showMessageDialog(this, "Anda harus mengatur jadwal pengantaran terlebih dahulu!");
            return;
        }
        if (!isValidAmount(uangField.getText())) {
            JOptionPane.showMessageDialog(this, "Jumlah uang harus berupa angka yang valid!");
            return;
        }      
        
        Date jadwalPengantaran = (Date) waktuPengantaran.getValue();
            if (jadwalPengantaran.before(new Date())) {
                JOptionPane.showMessageDialog(this, "Jadwal pengantaran tidak bisa diatur di masa lalu!");
                return;
            }
            
        int confirmation = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda ingin membeli produk yang telah dipilih?",
            "Konfirmasi Checkout", 
            JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            // Lanjutkan ke pembuatan nota
            generateNota();
        }
        
        this.dispose(); // Tutup halaman registrasi
        MenuUtama Menu = new MenuUtama(currentBuyerId); // Membuka halaman login
        Menu.setVisible(true);
        
    }//GEN-LAST:event_checkOutBtnActionPerformed

    private void uangFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uangFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_uangFieldActionPerformed

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
            java.util.logging.Logger.getLogger(MenuUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuUtama().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel beranda;
    private javax.swing.JButton buahBtn;
    private javax.swing.JButton checkOutBtn;
    private javax.swing.JComboBox<String> cmbFilter;
    private javax.swing.JButton hapusBtn;
    private javax.swing.JButton homeBtn;
    private javax.swing.JPanel isiMenu;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel jadwalLabel;
    private javax.swing.JButton kacangBtn;
    private javax.swing.JLabel kategoriLabel;
    private javax.swing.JButton keranjangBtn;
    private javax.swing.JPanel keranjangHal;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel panelKeranjangContainer;
    private javax.swing.JPanel productPanelContainer;
    private javax.swing.JButton profilBtn;
    private javax.swing.JButton rempahBtn;
    private javax.swing.JPanel resepHal;
    private javax.swing.JButton resepHalBtn;
    private javax.swing.JButton sayurBtn;
    private javax.swing.JTextField searchBarField;
    private javax.swing.JButton searchBtn;
    private javax.swing.JButton serealiaBtn;
    private javax.swing.JTextField totalHargaField;
    private javax.swing.JTextField uangField;
    private javax.swing.JLabel uangLabel;
    private javax.swing.JButton umbiBtn;
    private javax.swing.JSpinner waktuPengantaran;
    // End of variables declaration//GEN-END:variables
}
