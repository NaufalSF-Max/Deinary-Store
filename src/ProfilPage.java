import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Desktop;
import java.io.File;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.Font;


public class ProfilPage extends javax.swing.JFrame {
    private int currentBuyerId;

    public ProfilPage(int buyerId) {
        initComponents();
        this.currentBuyerId = buyerId;
        customerService.setVisible(false);
        Alamat.setVisible(false);
        wishList.setVisible(false);
        historyPesanan.setVisible(false);
        loadBuyerProfile(); // Memuat data profil
        
        loadDefaultAddress();
        loadAlamatOpsi();
        
        wishlistTabel.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"Nama Produk", "Kategori", "Harga"}
        ) {
            Class[] columnTypes = new Class[] {String.class, String.class, Double.class, Boolean.class};

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Hanya kolom checkbox yang dapat diedit
            }
        });


        
        editBtnDefault.addActionListener(new java.awt.event.ActionListener() {
            private boolean isEditing = false;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Periksa apakah data alamat kosong
                if (!isEditing && (
                    namaLengkapDefaultField.getText().isEmpty() ||
                    noTelpDefaultField.getText().isEmpty() ||
                    provinsiDefaultField.getText().isEmpty() ||
                    kotaDefaultField.getText().isEmpty() ||
                    kabupatenDefaultField.getText().isEmpty() ||
                    kecamatanDefaultField.getText().isEmpty() ||
                    kodePosDefaultField.getText().isEmpty() ||
                    detailDefaultArea.getText().isEmpty()
                )) {
                    JOptionPane.showMessageDialog(null, "Anda tidak bisa edit alamat, karena belum ada data.");
                    return;
                }

                // Jika dalam mode edit, tampilkan konfirmasi sebelum keluar
                if (isEditing) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                        "Apakah anda yakin? Data terbaru tidak akan tersimpan sebelum anda menekan tombol simpan.",
                        "Konfirmasi",
                        JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        setFieldEditable(false); // Nonaktifkan field
                        isEditing = false;
                        JOptionPane.showMessageDialog(null, "Sekarang anda sudah tidak bisa edit data alamat lagi.");
                    }
                    return;
                }

                // Jika belum dalam mode edit
                isEditing = true;
                setFieldEditable(true); // Aktifkan field
                JOptionPane.showMessageDialog(null, "Anda dapat edit data alamat anda sekarang.");
            }
        });
        
        SimpanBtnDefault.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Validasi apakah semua field sudah terisi
                if (namaLengkapDefaultField.getText().isEmpty() ||
                    noTelpDefaultField.getText().isEmpty() ||
                    provinsiDefaultField.getText().isEmpty() ||
                    kotaDefaultField.getText().isEmpty() ||
                    kabupatenDefaultField.getText().isEmpty() ||
                    kecamatanDefaultField.getText().isEmpty() ||
                    kodePosDefaultField.getText().isEmpty() ||
                    detailDefaultArea.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Data belum terisi semua, harap mengisi data alamat anda.");
                    return; // Keluar jika data belum lengkap
                }

                // Simpan data ke database
                try (Connection connection = DatabaseConnection.getConnection()) {
                    String updateQuery = "UPDATE addresses SET full_name = ?, phone_number = ?, province = ?, city = ?, " +
                        "district = ?, subdistrict = ?, postal_code = ?, address_detail = ? WHERE id_buyer = ? AND is_default = 1";

                    PreparedStatement stmt = connection.prepareStatement(updateQuery);
                    stmt.setString(1, namaLengkapDefaultField.getText());
                    stmt.setString(2, noTelpDefaultField.getText());
                    stmt.setString(3, provinsiDefaultField.getText());
                    stmt.setString(4, kotaDefaultField.getText());
                    stmt.setString(5, kabupatenDefaultField.getText());
                    stmt.setString(6, kecamatanDefaultField.getText());
                    stmt.setString(7, kodePosDefaultField.getText());
                    stmt.setString(8, detailDefaultArea.getText());
                    stmt.setInt(9, currentBuyerId);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(null, "Alamat sudah diperbarui.");
                        setFieldEditable(false); // Kunci kembali field setelah disimpan
                    } else {
                        JOptionPane.showMessageDialog(null, "Gagal memperbarui alamat default.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + ex.getMessage());
                }
            }
        });
        
        editDataAlamatOption1.addActionListener(new java.awt.event.ActionListener() {
            private boolean isEditing = false;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Periksa apakah data alamat kosong
                if (!isEditing && (
                    namaLengkapOptionField1.getText().isEmpty() ||
                    noTelpOptionField1.getText().isEmpty() ||
                    provinsiOptionField1.getText().isEmpty() ||
                    kotaOptionField1.getText().isEmpty() ||
                    kabupatenOptionField1.getText().isEmpty() ||
                    kecamatanOptionField1.getText().isEmpty() ||
                    kodePosOptionField1.getText().isEmpty() ||
                    detailOptiontArea1.getText().isEmpty()
                )) {
                    JOptionPane.showMessageDialog(null, "Anda tidak bisa edit alamat, karena belum ada data.");
                    return;
                }

                // Jika dalam mode edit, tampilkan konfirmasi sebelum keluar
                if (isEditing) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                        "Apakah anda yakin? Data terbaru tidak akan tersimpan sebelum anda menekan tombol simpan.",
                        "Konfirmasi",
                        JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        toggleEditAlamat(false, 1); // Nonaktifkan field
                        isEditing = false;
                        JOptionPane.showMessageDialog(null, "Sekarang anda sudah tidak bisa edit data alamat lagi.");
                    }
                    return;
                }

                // Jika belum dalam mode edit
                isEditing = true;
                toggleEditAlamat(true, 1); // Aktifkan field
                JOptionPane.showMessageDialog(null, "Anda dapat edit data alamat anda sekarang.");
            }
        });
        hapusDataAlamatOption1.addActionListener(evt -> deleteAlamatOption(1));
        mainDataAlamatOption1.addActionListener(evt -> setAlamatDefault(1));
        
        editDataAlamatOption2.addActionListener(new java.awt.event.ActionListener() {
            private boolean isEditing = false;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Periksa apakah data alamat kosong
                if (!isEditing && (
                    namaLengkapOptionField2.getText().isEmpty() ||
                    noTelpOptionField2.getText().isEmpty() ||
                    provinsiOptionField2.getText().isEmpty() ||
                    kotaOptionField2.getText().isEmpty() ||
                    kabupatenOptionField2.getText().isEmpty() ||
                    kecamatanOptionField2.getText().isEmpty() ||
                    kodePosOptionField2.getText().isEmpty() ||
                    detailOptiontArea2.getText().isEmpty()
                )) {
                    JOptionPane.showMessageDialog(null, "Anda tidak bisa edit alamat, karena belum ada data.");
                    return;
                }

                // Jika dalam mode edit, tampilkan konfirmasi sebelum keluar
                if (isEditing) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                        "Apakah anda yakin? Data terbaru tidak akan tersimpan sebelum anda menekan tombol simpan.",
                        "Konfirmasi",
                        JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        toggleEditAlamat(false, 2); // Nonaktifkan field
                        isEditing = false;
                        JOptionPane.showMessageDialog(null, "Sekarang anda sudah tidak bisa edit data alamat lagi.");
                    }
                    return;
                }

                // Jika belum dalam mode edit
                isEditing = true;
                toggleEditAlamat(true, 2); // Aktifkan field
                JOptionPane.showMessageDialog(null, "Anda dapat edit data alamat anda sekarang.");
            }
        });
        hapusDataAlamatOption2.addActionListener(evt -> deleteAlamatOption(2));
        mainDataAlamatOption2.addActionListener(evt -> setAlamatDefault(2));

        editDataAlamatOption3.addActionListener(new java.awt.event.ActionListener() {
            private boolean isEditing = false;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Periksa apakah data alamat kosong
                if (!isEditing && (
                    namaLengkapOptionField3.getText().isEmpty() ||
                    noTelpOptionField3.getText().isEmpty() ||
                    provinsiOptionField3.getText().isEmpty() ||
                    kotaOptionField3.getText().isEmpty() ||
                    kabupatenOptionField3.getText().isEmpty() ||
                    kecamatanOptionField3.getText().isEmpty() ||
                    kodePosOptionField3.getText().isEmpty() ||
                    detailOptiontArea3.getText().isEmpty()
                )) {
                    JOptionPane.showMessageDialog(null, "Anda tidak bisa edit alamat, karena belum ada data.");
                    return;
                }

                // Jika dalam mode edit, tampilkan konfirmasi sebelum keluar
                if (isEditing) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                        "Apakah anda yakin? Data terbaru tidak akan tersimpan sebelum anda menekan tombol simpan.",
                        "Konfirmasi",
                        JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        toggleEditAlamat(false, 3); // Nonaktifkan field
                        isEditing = false;
                        JOptionPane.showMessageDialog(null, "Sekarang anda sudah tidak bisa edit data alamat lagi.");
                    }
                    return;
                }

                // Jika belum dalam mode edit
                isEditing = true;
                toggleEditAlamat(true, 3); // Aktifkan field
                JOptionPane.showMessageDialog(null, "Anda dapat edit data alamat anda sekarang.");
            }
        });
        hapusDataAlamatOption3.addActionListener(evt -> deleteAlamatOption(3));
        mainDataAlamatOption3.addActionListener(evt -> setAlamatDefault(3));

        editDataAlamatOption4.addActionListener(new java.awt.event.ActionListener() {
            private boolean isEditing = false;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Periksa apakah data alamat kosong
                if (!isEditing && (
                    namaLengkapOptionField4.getText().isEmpty() ||
                    noTelpOptionField4.getText().isEmpty() ||
                    provinsiOptionField4.getText().isEmpty() ||
                    kotaOptionField4.getText().isEmpty() ||
                    kabupatenOptionField4.getText().isEmpty() ||
                    kecamatanOptionField4.getText().isEmpty() ||
                    kodePosOptionField4.getText().isEmpty() ||
                    detailOptiontArea4.getText().isEmpty()
                )) {
                    JOptionPane.showMessageDialog(null, "Anda tidak bisa edit alamat, karena belum ada data.");
                    return;
                }

                // Jika dalam mode edit, tampilkan konfirmasi sebelum keluar
                if (isEditing) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                        "Apakah anda yakin? Data terbaru tidak akan tersimpan sebelum anda menekan tombol simpan.",
                        "Konfirmasi",
                        JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        toggleEditAlamat(false, 4); // Nonaktifkan field
                        isEditing = false;
                        JOptionPane.showMessageDialog(null, "Sekarang anda sudah tidak bisa edit data alamat lagi.");
                    }
                    return;
                }

                // Jika belum dalam mode edit
                isEditing = true;
                toggleEditAlamat(true, 4); // Aktifkan field
                JOptionPane.showMessageDialog(null, "Anda dapat edit data alamat anda sekarang.");
            }
        });
        hapusDataAlamatOption4.addActionListener(evt -> deleteAlamatOption(4));
        mainDataAlamatOption4.addActionListener(evt -> setAlamatDefault(4));

        editDataAlamatOption5.addActionListener(new java.awt.event.ActionListener() {
            private boolean isEditing = false;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Periksa apakah data alamat kosong
                if (!isEditing && (
                    namaLengkapOptionField5.getText().isEmpty() ||
                    noTelpOptionField5.getText().isEmpty() ||
                    provinsiOptionField5.getText().isEmpty() ||
                    kotaOptionField5.getText().isEmpty() ||
                    kabupatenOptionField5.getText().isEmpty() ||
                    kecamatanOptionField5.getText().isEmpty() ||
                    kodePosOptionField5.getText().isEmpty() ||
                    detailOptiontArea5.getText().isEmpty()
                )) {
                    JOptionPane.showMessageDialog(null, "Anda tidak bisa edit alamat, karena belum ada data.");
                    return;
                }

                // Jika dalam mode edit, tampilkan konfirmasi sebelum keluar
                if (isEditing) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                        "Apakah anda yakin? Data terbaru tidak akan tersimpan sebelum anda menekan tombol simpan.",
                        "Konfirmasi", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        toggleEditAlamat(false, 5); // Nonaktifkan field
                        isEditing = false;
                        JOptionPane.showMessageDialog(null, "Sekarang anda sudah tidak bisa edit data alamat lagi.");
                    }
                    return;
                }

                // Jika belum dalam mode edit
                isEditing = true;
                toggleEditAlamat(true, 5); // Aktifkan field
                JOptionPane.showMessageDialog(null, "Anda dapat edit data alamat anda sekarang.");
            }
        });
        hapusDataAlamatOption5.addActionListener(evt -> deleteAlamatOption(5));
        mainDataAlamatOption5.addActionListener(evt -> setAlamatDefault(5));

        
    }
    
    public ProfilPage() {
        initComponents();
        this.currentBuyerId = 4;
        customerService.setVisible(false);
        Alamat.setVisible(false);
        wishList.setVisible(false);
        historyPesanan.setVisible(false);
        loadBuyerProfile(); // Memuat data profil

        loadDefaultAddress();
        loadAlamatOpsi();
        
        wishlistTabel.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"Nama Produk", "Kategori", "Harga"}
        ) {
            Class[] columnTypes = new Class[] {String.class, String.class, Double.class, Boolean.class};

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Hanya kolom checkbox yang dapat diedit
            }
        });


        
        editBtnDefault.addActionListener(new java.awt.event.ActionListener() {
            private boolean isEditing = false;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Periksa apakah data alamat kosong
                if (!isEditing && (
                    namaLengkapDefaultField.getText().isEmpty() ||
                    noTelpDefaultField.getText().isEmpty() ||
                    provinsiDefaultField.getText().isEmpty() ||
                    kotaDefaultField.getText().isEmpty() ||
                    kabupatenDefaultField.getText().isEmpty() ||
                    kecamatanDefaultField.getText().isEmpty() ||
                    kodePosDefaultField.getText().isEmpty() ||
                    detailDefaultArea.getText().isEmpty()
                )) {
                    JOptionPane.showMessageDialog(null, "Anda tidak bisa edit alamat, karena belum ada data.");
                    return;
                }

                // Jika dalam mode edit, tampilkan konfirmasi sebelum keluar
                if (isEditing) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                        "Apakah anda yakin? Data terbaru tidak akan tersimpan sebelum anda menekan tombol simpan.",
                        "Konfirmasi",
                        JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        setFieldEditable(false); // Nonaktifkan field
                        isEditing = false;
                        JOptionPane.showMessageDialog(null, "Sekarang anda sudah tidak bisa edit data alamat lagi.");
                    }
                    return;
                }

                // Jika belum dalam mode edit
                isEditing = true;
                setFieldEditable(true); // Aktifkan field
                JOptionPane.showMessageDialog(null, "Anda dapat edit data alamat anda sekarang.");
            }
        });
        SimpanBtnDefault.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Validasi apakah semua field sudah terisi
                if (namaLengkapDefaultField.getText().isEmpty() ||
                    noTelpDefaultField.getText().isEmpty() ||
                    provinsiDefaultField.getText().isEmpty() ||
                    kotaDefaultField.getText().isEmpty() ||
                    kabupatenDefaultField.getText().isEmpty() ||
                    kecamatanDefaultField.getText().isEmpty() ||
                    kodePosDefaultField.getText().isEmpty() ||
                    detailDefaultArea.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Data belum terisi semua, harap mengisi data alamat anda.");
                    return; // Keluar jika data belum lengkap
                }

                // Simpan data ke database
                try (Connection connection = DatabaseConnection.getConnection()) {
                    String updateQuery = "UPDATE addresses SET full_name = ?, phone_number = ?, province = ?, city = ?, " +
                        "district = ?, subdistrict = ?, postal_code = ?, address_detail = ? WHERE id_buyer = ? AND is_default = 1";

                    PreparedStatement stmt = connection.prepareStatement(updateQuery);
                    stmt.setString(1, namaLengkapDefaultField.getText());
                    stmt.setString(2, noTelpDefaultField.getText());
                    stmt.setString(3, provinsiDefaultField.getText());
                    stmt.setString(4, kotaDefaultField.getText());
                    stmt.setString(5, kabupatenDefaultField.getText());
                    stmt.setString(6, kecamatanDefaultField.getText());
                    stmt.setString(7, kodePosDefaultField.getText());
                    stmt.setString(8, detailDefaultArea.getText());
                    stmt.setInt(9, currentBuyerId);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(null, "Alamat sudah diperbarui.");
                        setFieldEditable(false); // Kunci kembali field setelah disimpan
                    } else {
                        JOptionPane.showMessageDialog(null, "Gagal memperbarui alamat default.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + ex.getMessage());
                }
            }
        });
        
        editDataAlamatOption1.addActionListener(evt -> toggleEditAlamat(true, 1));
        hapusDataAlamatOption1.addActionListener(evt -> deleteAlamatOption(1));
        mainDataAlamatOption1.addActionListener(evt -> setAlamatDefault(1));
        
        editDataAlamatOption2.addActionListener(evt -> toggleEditAlamat(true, 2));
        hapusDataAlamatOption2.addActionListener(evt -> deleteAlamatOption(2));
        mainDataAlamatOption2.addActionListener(evt -> setAlamatDefault(2));

        editDataAlamatOption3.addActionListener(evt -> toggleEditAlamat(true, 3));
        hapusDataAlamatOption3.addActionListener(evt -> deleteAlamatOption(3));
        mainDataAlamatOption3.addActionListener(evt -> setAlamatDefault(3));

        editDataAlamatOption4.addActionListener(evt -> toggleEditAlamat(true, 4));
        hapusDataAlamatOption4.addActionListener(evt -> deleteAlamatOption(4));
        mainDataAlamatOption4.addActionListener(evt -> setAlamatDefault(4));

        editDataAlamatOption5.addActionListener(evt -> toggleEditAlamat(true, 5));
        hapusDataAlamatOption5.addActionListener(evt -> deleteAlamatOption(5));
        mainDataAlamatOption5.addActionListener(evt -> setAlamatDefault(5));

    }

    public class wishList{
        private String namaProduk;
        private String Kategori;
        private Long harga;
    }
    
    private void loadBuyerProfile() {
        String query = "SELECT u.name AS full_name, u.username, u.email, u.phone_number, u.birth_date, b.points, b.level_name " +
                       "FROM users u " +
                       "JOIN buyers b ON u.id_user = b.id_user " +
                       "WHERE b.id_buyer = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, this.currentBuyerId); // Menggunakan ID pembeli yang login
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Mengisi field profil dengan data dari database
                namaProfilField.setText(rs.getString("full_name"));
                usernameProfilField.setText(rs.getString("username"));
                emailProfilField.setText(rs.getString("email"));
                noHpProfilField.setText(rs.getString("phone_number"));
                pointsProfilField.setText(String.valueOf(rs.getInt("points")));
                levelProfilField.setText(rs.getString("level_name"));

                // Format tanggal lahir sebelum ditampilkan
                Date birthDate = rs.getDate("birth_date");
                if (birthDate != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    tglLahirProfilField.setText(dateFormat.format(birthDate));
                } else {
                    tglLahirProfilField.setText("Tanggal lahir tidak tersedia");
                }

                // Tentukan level dan setel gambar
                int points = rs.getInt("points");
                String imageName;
                if (points < 1000) {
                    imageName = "bronzePoint";
                    levelProfilField.setText("Bronze");
                } else if (points < 5000) {
                    imageName = "silverPoint";
                    levelProfilField.setText("Silver");
                } else if (points < 10000) {
                    imageName = "goldPoint";
                    levelProfilField.setText("Gold");
                } else if (points < 20000) {
                    imageName = "platinumPoint";
                    levelProfilField.setText("Platinum");
                } else {
                    imageName = "diamondPoint";
                    levelProfilField.setText("Diamond");
                }

                // Load gambar dan setel ke label
                ImageIcon icon = new ImageIcon(getClass().getResource("/deinarystore/img/" + imageName + ".png"));
                pointsLabel.setIcon(icon);

            } else {
                JOptionPane.showMessageDialog(this, "Profil tidak ditemukan.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data profil.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadWishlist() {
            DefaultTableModel model = (DefaultTableModel) wishlistTabel.getModel();
            model.setRowCount(0); // Bersihkan tabel sebelum memuat data baru

            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "SELECT p.id AS product_id, p.name, p.category, p.price " +
                               "FROM wishlists w " +
                               "JOIN products p ON w.product_id = p.id " +
                               "WHERE w.id_buyer = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, currentBuyerId); // Menggunakan ID pembeli saat ini
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    String productName = rs.getString("name");
                    String category = rs.getString("category");
                    double price = rs.getDouble("price"); // Pastikan nilai diambil sebagai double

                    // Tambahkan baris baru ke tabel
                    model.addRow(new Object[]{productName, category, price}); // Kolom terakhir adalah checkbox
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal memuat wishlist: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    private int getProductIdFromWishlist(int rowIndex) {
        DefaultTableModel model = (DefaultTableModel) wishlistTabel.getModel();
        // Misalkan kolom pertama (0) menyimpan ID produk secara tersembunyi
        return (int) model.getValueAt(rowIndex, 0);
    }
    
    private void loadHistoryPesanan() {
        DefaultTableModel model = (DefaultTableModel) historyPesanTabel.getModel();
        model.setRowCount(0); // Kosongkan tabel sebelum menambah data baru

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Ambil ID pembeli yang sedang login, misalnya dari sesi atau variabel global
            int idPembeli = currentBuyerId;

            // Query yang difilter berdasarkan ID pembeli
            String query = "SELECT id_transaction, total_price, delivery_schedule FROM transactions WHERE id_buyer = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, idPembeli); // Mengatur ID pembeli dalam prepared statement

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String idTransaction = rs.getString("id_transaction");
                double totalPrice = rs.getDouble("total_price");
                String deliverySchedule = rs.getString("delivery_schedule");

                model.addRow(new Object[]{idTransaction, totalPrice, deliverySchedule, "Download Nota"});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data histori: " + ex.getMessage());
        }

        // Tambahkan renderer dan editor tombol untuk kolom "Nota Digital"
        historyPesanTabel.getColumn("Nota Digital").setCellRenderer(new ButtonRenderer());
        historyPesanTabel.getColumn("Nota Digital").setCellEditor(new ButtonEditor(evt -> {
            int row = historyPesanTabel.getSelectedRow();
            String transactionId = historyPesanTabel.getValueAt(row, 0).toString();
            downloadNota(transactionId); // Panggil metode downloadNota
        }));
    }

    private void downloadNota(String transactionId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT t.id_transaction, t.id_buyer, t.total_price, t.delivery_schedule, " +
               "t.buyer_payment, t.change_amount, d.username AS driver_username, t.points_earned " +
               "FROM transactions t " +
               "LEFT JOIN drivers d ON t.id_driver = d.id_driver " +
               "WHERE t.id_transaction = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, transactionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String idBuyer = rs.getString("id_buyer");
                double totalPrice = rs.getDouble("total_price");
                String deliverySchedule = rs.getString("delivery_schedule");
                String driverUsername = rs.getString("driver_username");
                double buyerPayment = rs.getDouble("buyer_payment");
                double changeAmount = rs.getDouble("change_amount");
                int pointsEarned = rs.getInt("points_earned");
                
                if (driverUsername == null || driverUsername.isEmpty()) {
                    driverUsername = "Driver Tidak Ditentukan";
                }

                // Path file
                String filePath = System.getProperty("java.io.tmpdir") + File.separator + "Nota_" + transactionId + ".pdf";

                // Buat dokumen PDF
                com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                // Tambahkan konten PDF
                document.add(new com.itextpdf.text.Paragraph("========== Deinary Store =========="));
                document.add(new com.itextpdf.text.Paragraph("ID Transaksi: " + transactionId));
                document.add(new com.itextpdf.text.Paragraph("ID Buyer: " + idBuyer));
                document.add(new com.itextpdf.text.Paragraph("Tanggal: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())));
                document.add(new com.itextpdf.text.Paragraph("Jadwal Pengantaran: " + deliverySchedule));
                document.add(new com.itextpdf.text.Paragraph("===================================\n"));
                
                String addressQuery = "SELECT full_name, phone_number, address_detail, city, province, postal_code " +
                      "FROM addresses WHERE id_buyer = ? AND is_default = 1";
                PreparedStatement addressStmt = conn.prepareStatement(addressQuery);
                addressStmt.setInt(1, currentBuyerId);
                ResultSet addressRs = addressStmt.executeQuery();

                if (addressRs.next()) {
                    document.add(new Paragraph("Alamat Pengantaran:"));
                    document.add(new Paragraph("Nama: " + addressRs.getString("full_name")));
                    document.add(new Paragraph("Telepon: " + addressRs.getString("phone_number")));
                    document.add(new Paragraph("Alamat: " + addressRs.getString("address_detail") + ", " +
                                               addressRs.getString("city") + ", " +
                                               addressRs.getString("province") + ", Kode Pos: " +
                                               addressRs.getString("postal_code")));
                } else {
                    JOptionPane.showMessageDialog(this, "Alamat pengantaran tidak ditemukan.");
                }

                document.add(new com.itextpdf.text.Paragraph("===================================\n"));
                document.add(new com.itextpdf.text.Paragraph("Driver: " + driverUsername));
                document.add(new com.itextpdf.text.Paragraph("===================================\n"));
                
                document.add(new com.itextpdf.text.Paragraph("===================================\n"));
                document.add(new com.itextpdf.text.Paragraph("Total Harga: Rp " + String.format("%,.2f", totalPrice))); 
                document.add(new Paragraph("Jumlah Uang Pembeli: Rp " + String.format("%,.2f", buyerPayment)));
                document.add(new Paragraph("Kembalian: Rp " + String.format("%,.2f", changeAmount)));
                document.add(new Paragraph("Poin Diperoleh: " + pointsEarned));
                document.add(new com.itextpdf.text.Paragraph("===================================\n"));
                document.add(new com.itextpdf.text.Paragraph("Terima Kasih Telah Membeli di Deinary Store!"));

                document.close();

                // Arahkan file ke browser atau buka langsungs
                File pdfFile = new File(filePath);
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    JOptionPane.showMessageDialog(this, "Nota telah disimpan di lokasi sementara: " + filePath);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Data transaksi tidak ditemukan.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal membuat PDF: " + ex.getMessage());
        }
    }

    private void loadDefaultAddress() {
        String query = "SELECT full_name, phone_number, address_detail, city, province, postal_code, district, subdistrict "
                     + "FROM addresses WHERE id_buyer = ? AND is_default = 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, this.currentBuyerId); // Set buyer ID for the query
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Populate the default address fields
                namaLengkapDefaultField.setText(rs.getString("full_name"));
                noTelpDefaultField.setText(rs.getString("phone_number"));
                provinsiDefaultField.setText(rs.getString("province"));
                kotaDefaultField.setText(rs.getString("city"));
                kodePosDefaultField.setText(rs.getString("postal_code"));
                kecamatanDefaultField.setText(rs.getString("district"));
                kabupatenDefaultField.setText(rs.getString("subdistrict"));
                detailDefaultArea.setText(rs.getString("address_detail"));
            } else {
                // No default address found; clear fields and notify user
                namaLengkapDefaultField.setText("");
                noTelpDefaultField.setText("");
                provinsiDefaultField.setText("");
                kotaDefaultField.setText("");
                kodePosDefaultField.setText("");
                kecamatanDefaultField.setText("");
                kabupatenDefaultField.setText("");
                detailDefaultArea.setText("");
                JOptionPane.showMessageDialog(this, "Tidak ada alamat default.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat alamat default.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadAlamatOpsi() {
        // Bersihkan semua panel alamat opsi sebelum memuat data baru
        clearAlamatOpsi();

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Query untuk mengambil data alamat opsi (is_default = 0)
            String query = "SELECT full_name, phone_number, province, city, district, subdistrict, postal_code, address_detail "
                         + "FROM addresses WHERE id_buyer = ? AND is_default = 0 ORDER BY id_address ASC"; // Ganti id dengan id_address
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, currentBuyerId);

            ResultSet rs = stmt.executeQuery();

            // Panel field mapping
            JTextField[] namaFields = {namaLengkapOptionField1, namaLengkapOptionField2, namaLengkapOptionField3, namaLengkapOptionField4, namaLengkapOptionField5};
            JTextField[] telpFields = {noTelpOptionField1, noTelpOptionField2, noTelpOptionField3, noTelpOptionField4, noTelpOptionField5};
            JTextField[] provinsiFields = {provinsiOptionField1, provinsiOptionField2, provinsiOptionField3, provinsiOptionField4, provinsiOptionField5};
            JTextField[] kotaFields = {kotaOptionField1, kotaOptionField2, kotaOptionField3, kotaOptionField4, kotaOptionField5};
            JTextField[] kabupatenFields = {kabupatenOptionField1, kabupatenOptionField2, kabupatenOptionField3, kabupatenOptionField4, kabupatenOptionField5};
            JTextField[] kecamatanFields = {kecamatanOptionField1, kecamatanOptionField2, kecamatanOptionField3, kecamatanOptionField4, kecamatanOptionField5};
            JTextField[] kodePosFields = {kodePosOptionField1, kodePosOptionField2, kodePosOptionField3, kodePosOptionField4, kodePosOptionField5};
            JTextArea[] detailFields = {detailOptiontArea1, detailOptiontArea2, detailOptiontArea3, detailOptiontArea4, detailOptiontArea5};

            int index = 0;

            // Isi data ke panel
            while (rs.next() && index < 5) {
                namaFields[index].setText(rs.getString("full_name"));
                telpFields[index].setText(rs.getString("phone_number"));
                provinsiFields[index].setText(rs.getString("province"));
                kotaFields[index].setText(rs.getString("city"));
                kabupatenFields[index].setText(rs.getString("district"));
                kecamatanFields[index].setText(rs.getString("subdistrict"));
                kodePosFields[index].setText(rs.getString("postal_code"));
                detailFields[index].setText(rs.getString("address_detail"));
                index++;
            }

            if (index == 0) {
                JOptionPane.showMessageDialog(null, "Tidak ada alamat opsi yang tersedia.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal memuat alamat opsi: " + e.getMessage());
        }
    }

    private void setFieldEditable(boolean editable) {
        namaLengkapDefaultField.setEditable(editable);
        noTelpDefaultField.setEditable(editable);
        provinsiDefaultField.setEditable(editable);
        kotaDefaultField.setEditable(editable);
        kabupatenDefaultField.setEditable(editable);
        kecamatanDefaultField.setEditable(editable);
        kodePosDefaultField.setEditable(editable);
        detailDefaultArea.setEditable(editable);
    }
    
    private void toggleEditAlamat(boolean editable, int index) {
        JTextField[] namaFields = {namaLengkapOptionField1, namaLengkapOptionField2, namaLengkapOptionField3, namaLengkapOptionField4, namaLengkapOptionField5};
        JTextField[] telpFields = {noTelpOptionField1, noTelpOptionField2, noTelpOptionField3, noTelpOptionField4, noTelpOptionField5};
        JTextField[] provinsiFields = {provinsiOptionField1, provinsiOptionField2, provinsiOptionField3, provinsiOptionField4, provinsiOptionField5};
        JTextField[] kotaFields = {kotaOptionField1, kotaOptionField2, kotaOptionField3, kotaOptionField4, kotaOptionField5};
        JTextField[] kabupatenFields = {kabupatenOptionField1, kabupatenOptionField2, kabupatenOptionField3, kabupatenOptionField4, kabupatenOptionField5};
        JTextField[] kecamatanFields = {kecamatanOptionField1, kecamatanOptionField2, kecamatanOptionField3, kecamatanOptionField4, kecamatanOptionField5};
        JTextField[] kodePosFields = {kodePosOptionField1, kodePosOptionField2, kodePosOptionField3, kodePosOptionField4, kodePosOptionField5};
        JTextArea[] detailFields = {detailOptiontArea1, detailOptiontArea2, detailOptiontArea3, detailOptiontArea4, detailOptiontArea5};

        namaFields[index - 1].setEditable(editable);
        telpFields[index - 1].setEditable(editable);
        provinsiFields[index - 1].setEditable(editable);
        kotaFields[index - 1].setEditable(editable);
        kabupatenFields[index - 1].setEditable(editable);
        kecamatanFields[index - 1].setEditable(editable);
        kodePosFields[index - 1].setEditable(editable);
        detailFields[index - 1].setEditable(editable);
    }
    
    private void deleteAlamatOption(int index) {
        JTextField[] namaFields = {namaLengkapOptionField1, namaLengkapOptionField2, namaLengkapOptionField3, namaLengkapOptionField4, namaLengkapOptionField5};
        JTextField[] telpFields = {noTelpOptionField1, noTelpOptionField2, noTelpOptionField3, noTelpOptionField4, noTelpOptionField5};

        // Konfirmasi penghapusan
        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Apakah Anda yakin ingin menghapus alamat ini?",
            "Konfirmasi Penghapusan",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String deleteQuery = "DELETE FROM addresses WHERE id_buyer = ? AND full_name = ? AND phone_number = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, currentBuyerId);
            deleteStmt.setString(2, namaFields[index - 1].getText());
            deleteStmt.setString(3, telpFields[index - 1].getText());

            int rowsAffected = deleteStmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Alamat berhasil dihapus.");
            } else {
                JOptionPane.showMessageDialog(null, "Alamat tidak ditemukan atau sudah dihapus.");
            }

            loadAlamatOpsi();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menghapus alamat: " + ex.getMessage());
        }
    }

    private void setAlamatDefault(int index) {
        JTextField[] namaFields = {namaLengkapOptionField1, namaLengkapOptionField2, namaLengkapOptionField3, namaLengkapOptionField4, namaLengkapOptionField5};
        JTextField[] telpFields = {noTelpOptionField1, noTelpOptionField2, noTelpOptionField3, noTelpOptionField4, noTelpOptionField5};

        try (Connection connection = DatabaseConnection.getConnection()) {
            String getDefaultQuery = "SELECT id_address FROM addresses WHERE id_buyer = ? AND is_default = 1";
            PreparedStatement getDefaultStmt = connection.prepareStatement(getDefaultQuery);
            getDefaultStmt.setInt(1, currentBuyerId);
            ResultSet defaultRs = getDefaultStmt.executeQuery();

            int defaultAddressId = -1;
            if (defaultRs.next()) {
                defaultAddressId = defaultRs.getInt("id_address");
            } else {
                JOptionPane.showMessageDialog(null, "Alamat default tidak ditemukan.");
                return;
            }

            String getOptionQuery = "SELECT id_address FROM addresses WHERE id_buyer = ? AND full_name = ? AND phone_number = ?";
            PreparedStatement getOptionStmt = connection.prepareStatement(getOptionQuery);
            getOptionStmt.setInt(1, currentBuyerId);
            getOptionStmt.setString(2, namaFields[index - 1].getText());
            getOptionStmt.setString(3, telpFields[index - 1].getText());
            ResultSet optionRs = getOptionStmt.executeQuery();

            int optionAddressId = -1;
            if (optionRs.next()) {
                optionAddressId = optionRs.getInt("id_address");
            } else {
                JOptionPane.showMessageDialog(null, "Alamat tidak valid.");
                return;
            }

            String unsetDefaultQuery = "UPDATE addresses SET is_default = 0 WHERE id_address = ?";
            PreparedStatement unsetDefaultStmt = connection.prepareStatement(unsetDefaultQuery);
            unsetDefaultStmt.setInt(1, defaultAddressId);
            unsetDefaultStmt.executeUpdate();

            String setDefaultQuery = "UPDATE addresses SET is_default = 1 WHERE id_address = ?";
            PreparedStatement setDefaultStmt = connection.prepareStatement(setDefaultQuery);
            setDefaultStmt.setInt(1, optionAddressId);
            setDefaultStmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Alamat berhasil dijadikan default!");

            loadDefaultAddress();
            loadAlamatOpsi();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal mengubah default alamat: " + ex.getMessage());
        }
    }

    private void clearAddressFields() {
    namaLengkapField.setText("");
    noTelpField.setText("");
    provinsiField.setText("");
    kotaField.setText("");
    kabupatenField.setText("");
    kecamatanField.setText("");
    kodePosField.setText("");
    detailAlamatField.setText("");
}

    private void clearAlamatOpsi() {
        JTextField[] namaFields = {namaLengkapOptionField1, namaLengkapOptionField2, namaLengkapOptionField3, namaLengkapOptionField4, namaLengkapOptionField5};
        JTextField[] telpFields = {noTelpOptionField1, noTelpOptionField2, noTelpOptionField3, noTelpOptionField4, noTelpOptionField5};
        JTextField[] provinsiFields = {provinsiOptionField1, provinsiOptionField2, provinsiOptionField3, provinsiOptionField4, provinsiOptionField5};
        JTextField[] kotaFields = {kotaOptionField1, kotaOptionField2, kotaOptionField3, kotaOptionField4, kotaOptionField5};
        JTextField[] kabupatenFields = {kabupatenOptionField1, kabupatenOptionField2, kabupatenOptionField3, kabupatenOptionField4, kabupatenOptionField5};
        JTextField[] kecamatanFields = {kecamatanOptionField1, kecamatanOptionField2, kecamatanOptionField3, kecamatanOptionField4, kecamatanOptionField5};
        JTextField[] kodePosFields = {kodePosOptionField1, kodePosOptionField2, kodePosOptionField3, kodePosOptionField4, kodePosOptionField5};
        JTextArea[] detailFields = {detailOptiontArea1, detailOptiontArea2, detailOptiontArea3, detailOptiontArea4, detailOptiontArea5};

        for (int i = 0; i < 5; i++) {
            namaFields[i].setText("");
            telpFields[i].setText("");
            provinsiFields[i].setText("");
            kotaFields[i].setText("");
            kabupatenFields[i].setText("");
            kecamatanFields[i].setText("");
            kodePosFields[i].setText("");
            detailFields[i].setText("");
        }
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        Header = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pointsLabel = new javax.swing.JLabel();
        levelProfilField = new javax.swing.JTextField();
        backBtnProfil = new javax.swing.JButton();
        MenuProfil = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        hsitoryPesanbtn = new javax.swing.JButton();
        csBtn = new javax.swing.JButton();
        namaProfilField = new javax.swing.JTextField();
        usernameProfilField = new javax.swing.JTextField();
        emailProfilField = new javax.swing.JTextField();
        wishBtn = new javax.swing.JButton();
        alamatBtn = new javax.swing.JButton();
        logOutBtn = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        noHpProfilField = new javax.swing.JTextField();
        tglLahirProfilField = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        label = new javax.swing.JLabel();
        pointsProfilField = new javax.swing.JTextField();
        isiProfil = new javax.swing.JPanel();
        historyPesanan = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        historyPesanTabel = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        customerService = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        csKirimbtn = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        wishList = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        wishlistTabel = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        Alamat = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        namaLengkapField = new javax.swing.JTextField();
        noTelpField = new javax.swing.JTextField();
        provinsiField = new javax.swing.JTextField();
        kotaField = new javax.swing.JTextField();
        kecamatanField = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        detailAlamatField = new javax.swing.JTextArea();
        kodePosField = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        kabupatenField = new javax.swing.JTextField();
        backBtn2 = new javax.swing.JButton();
        tambahAlamatButton = new javax.swing.JButton();
        dataAlamat = new javax.swing.JPanel();
        jScrollPane14 = new javax.swing.JScrollPane();
        panelAlamat = new javax.swing.JPanel();
        tambahAlamatBt = new javax.swing.JButton();
        judulAlmatHal = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        alamatDefault = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        namaLengkapDefaultField = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        noTelpDefaultField = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        provinsiDefaultField = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        kotaDefaultField = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        kabupatenDefaultField = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        kecamatanDefaultField = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        kodePosDefaultField = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        detailDefaultArea = new javax.swing.JTextArea();
        editBtnDefault = new javax.swing.JButton();
        SimpanBtnDefault = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        panelDataAlamat = new javax.swing.JPanel();
        alamatOption1 = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        namaLengkapOptionField1 = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        noTelpOptionField1 = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        provinsiOptionField1 = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        kotaOptionField1 = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        kabupatenOptionField1 = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        kecamatanOptionField1 = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        kodePosOptionField1 = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        detailOptiontArea1 = new javax.swing.JTextArea();
        editDataAlamatOption1 = new javax.swing.JButton();
        mainDataAlamatOption1 = new javax.swing.JButton();
        hapusDataAlamatOption1 = new javax.swing.JButton();
        simpanDataAlamatOption1 = new javax.swing.JButton();
        alamatOption2 = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        namaLengkapOptionField2 = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        noTelpOptionField2 = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        provinsiOptionField2 = new javax.swing.JTextField();
        jLabel61 = new javax.swing.JLabel();
        kotaOptionField2 = new javax.swing.JTextField();
        jLabel62 = new javax.swing.JLabel();
        kabupatenOptionField2 = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        kecamatanOptionField2 = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        kodePosOptionField2 = new javax.swing.JTextField();
        jLabel65 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        detailOptiontArea2 = new javax.swing.JTextArea();
        editDataAlamatOption2 = new javax.swing.JButton();
        mainDataAlamatOption2 = new javax.swing.JButton();
        hapusDataAlamatOption2 = new javax.swing.JButton();
        simpanDataAlamatOption2 = new javax.swing.JButton();
        alamatOption3 = new javax.swing.JPanel();
        jLabel66 = new javax.swing.JLabel();
        namaLengkapOptionField3 = new javax.swing.JTextField();
        jLabel67 = new javax.swing.JLabel();
        noTelpOptionField3 = new javax.swing.JTextField();
        jLabel68 = new javax.swing.JLabel();
        provinsiOptionField3 = new javax.swing.JTextField();
        jLabel69 = new javax.swing.JLabel();
        kotaOptionField3 = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        kabupatenOptionField3 = new javax.swing.JTextField();
        jLabel71 = new javax.swing.JLabel();
        kecamatanOptionField3 = new javax.swing.JTextField();
        jLabel72 = new javax.swing.JLabel();
        kodePosOptionField3 = new javax.swing.JTextField();
        jLabel73 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        detailOptiontArea3 = new javax.swing.JTextArea();
        editDataAlamatOption3 = new javax.swing.JButton();
        mainDataAlamatOption3 = new javax.swing.JButton();
        hapusDataAlamatOption3 = new javax.swing.JButton();
        simpanDataAlamatOption3 = new javax.swing.JButton();
        alamatOption4 = new javax.swing.JPanel();
        jLabel74 = new javax.swing.JLabel();
        namaLengkapOptionField4 = new javax.swing.JTextField();
        jLabel75 = new javax.swing.JLabel();
        noTelpOptionField4 = new javax.swing.JTextField();
        jLabel76 = new javax.swing.JLabel();
        provinsiOptionField4 = new javax.swing.JTextField();
        jLabel77 = new javax.swing.JLabel();
        kotaOptionField4 = new javax.swing.JTextField();
        jLabel78 = new javax.swing.JLabel();
        kabupatenOptionField4 = new javax.swing.JTextField();
        jLabel79 = new javax.swing.JLabel();
        kecamatanOptionField4 = new javax.swing.JTextField();
        jLabel80 = new javax.swing.JLabel();
        kodePosOptionField4 = new javax.swing.JTextField();
        jLabel81 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        detailOptiontArea4 = new javax.swing.JTextArea();
        editDataAlamatOption4 = new javax.swing.JButton();
        mainDataAlamatOption4 = new javax.swing.JButton();
        hapusDataAlamatOption4 = new javax.swing.JButton();
        simpanDataAlamatOption4 = new javax.swing.JButton();
        alamatOption5 = new javax.swing.JPanel();
        jLabel82 = new javax.swing.JLabel();
        namaLengkapOptionField5 = new javax.swing.JTextField();
        jLabel83 = new javax.swing.JLabel();
        noTelpOptionField5 = new javax.swing.JTextField();
        jLabel84 = new javax.swing.JLabel();
        provinsiOptionField5 = new javax.swing.JTextField();
        jLabel85 = new javax.swing.JLabel();
        kotaOptionField5 = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        kabupatenOptionField5 = new javax.swing.JTextField();
        jLabel87 = new javax.swing.JLabel();
        kecamatanOptionField5 = new javax.swing.JTextField();
        jLabel88 = new javax.swing.JLabel();
        kodePosOptionField5 = new javax.swing.JTextField();
        jLabel89 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        detailOptiontArea5 = new javax.swing.JTextArea();
        editDataAlamatOption5 = new javax.swing.JButton();
        mainDataAlamatOption5 = new javax.swing.JButton();
        hapusDataAlamatOption5 = new javax.swing.JButton();
        simpanDataAlamatOption5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Header.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(112, 136, 113));
        jLabel1.setFont(new java.awt.Font("Poppins SemiBold", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(112, 136, 113));
        jLabel1.setText("PROFIL");

        levelProfilField.setEditable(false);

        backBtnProfil.setText("Back");
        backBtnProfil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnProfilActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout HeaderLayout = new javax.swing.GroupLayout(Header);
        Header.setLayout(HeaderLayout);
        HeaderLayout.setHorizontalGroup(
            HeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backBtnProfil)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(144, 144, 144)
                .addComponent(pointsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(levelProfilField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        HeaderLayout.setVerticalGroup(
            HeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(HeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(HeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(pointsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(levelProfilField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(11, Short.MAX_VALUE))
            .addGroup(HeaderLayout.createSequentialGroup()
                .addComponent(backBtnProfil)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        MenuProfil.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jLabel3.setText("Nama : ");

        jLabel4.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel4.setText("Username");

        jLabel5.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel5.setText("Email");

        hsitoryPesanbtn.setBackground(new java.awt.Color(109, 168, 222));
        hsitoryPesanbtn.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        hsitoryPesanbtn.setForeground(new java.awt.Color(255, 255, 255));
        hsitoryPesanbtn.setText("History Pesanan");
        hsitoryPesanbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hsitoryPesanbtnActionPerformed(evt);
            }
        });

        csBtn.setBackground(new java.awt.Color(109, 168, 222));
        csBtn.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        csBtn.setForeground(new java.awt.Color(255, 255, 255));
        csBtn.setText("Customer Service");
        csBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                csBtnActionPerformed(evt);
            }
        });

        namaProfilField.setEditable(false);
        namaProfilField.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        namaProfilField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                namaProfilFieldActionPerformed(evt);
            }
        });

        usernameProfilField.setEditable(false);
        usernameProfilField.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        emailProfilField.setEditable(false);
        emailProfilField.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        emailProfilField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailProfilFieldActionPerformed(evt);
            }
        });

        wishBtn.setBackground(new java.awt.Color(109, 168, 222));
        wishBtn.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        wishBtn.setForeground(new java.awt.Color(255, 255, 255));
        wishBtn.setText("Wishlist");
        wishBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wishBtnActionPerformed(evt);
            }
        });

        alamatBtn.setBackground(new java.awt.Color(109, 168, 222));
        alamatBtn.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        alamatBtn.setForeground(new java.awt.Color(255, 255, 255));
        alamatBtn.setText("Alamat");
        alamatBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alamatBtnActionPerformed(evt);
            }
        });

        logOutBtn.setBackground(new java.awt.Color(153, 153, 0));
        logOutBtn.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        logOutBtn.setText("Log Out");
        logOutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logOutBtnActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel34.setText("Nomer Hp");

        noHpProfilField.setEditable(false);
        noHpProfilField.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        noHpProfilField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noHpProfilFieldActionPerformed(evt);
            }
        });

        tglLahirProfilField.setEditable(false);
        tglLahirProfilField.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        tglLahirProfilField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglLahirProfilFieldActionPerformed(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel35.setText("Tgl. Lahir");

        label.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        label.setText("Points");

        pointsProfilField.setEditable(false);
        pointsProfilField.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        pointsProfilField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pointsProfilFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MenuProfilLayout = new javax.swing.GroupLayout(MenuProfil);
        MenuProfil.setLayout(MenuProfilLayout);
        MenuProfilLayout.setHorizontalGroup(
            MenuProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuProfilLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MenuProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MenuProfilLayout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MenuProfilLayout.createSequentialGroup()
                        .addGroup(MenuProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(logOutBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(alamatBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(wishBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(hsitoryPesanbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(MenuProfilLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(csBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(MenuProfilLayout.createSequentialGroup()
                                .addGroup(MenuProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(MenuProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel35)
                                    .addComponent(label))
                                .addGap(18, 18, Short.MAX_VALUE)
                                .addGroup(MenuProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(pointsProfilField, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                                    .addComponent(namaProfilField, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(usernameProfilField, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(emailProfilField)
                                    .addComponent(noHpProfilField)
                                    .addComponent(tglLahirProfilField))))
                        .addGap(20, 20, 20))))
        );
        MenuProfilLayout.setVerticalGroup(
            MenuProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuProfilLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MenuProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(namaProfilField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(MenuProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(usernameProfilField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(MenuProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(emailProfilField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(MenuProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(noHpProfilField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(MenuProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tglLahirProfilField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addGap(18, 18, 18)
                .addGroup(MenuProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label)
                    .addComponent(pointsProfilField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(76, 76, 76)
                .addComponent(alamatBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hsitoryPesanbtn)
                .addGap(18, 18, 18)
                .addComponent(csBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(wishBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(logOutBtn)
                .addContainerGap(1503, Short.MAX_VALUE))
        );

        historyPesanan.setBackground(new java.awt.Color(255, 255, 255));
        historyPesanan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        historyPesanTabel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id Transaksi", "Total Harga", "Jadwal Pengantaran", "Nota Digital"
            }
        ));
        historyPesanTabel.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                historyPesanTabelAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        jScrollPane1.setViewportView(historyPesanTabel);

        historyPesanan.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 126, 551, 249));

        jPanel2.setBackground(new java.awt.Color(109, 168, 222));
        jPanel2.setPreferredSize(new java.awt.Dimension(545, 111));

        jLabel2.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("HISTORY PEMESANAN");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(202, 202, 202))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel2)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        historyPesanan.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 545, -1));

        customerService.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(112, 136, 113));

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Poppins SemiBold", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Customer Service");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(186, 186, 186)
                .addComponent(jLabel10)
                .addContainerGap(217, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel10)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jLabel6.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel6.setText("Nama");

        jLabel7.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel7.setText("Email");

        jLabel8.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel8.setText("Subject");

        jLabel9.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel9.setText("Keluhan");

        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        csKirimbtn.setText("Kirim");

        jLabel13.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel13.setText("Username");

        javax.swing.GroupLayout customerServiceLayout = new javax.swing.GroupLayout(customerService);
        customerService.setLayout(customerServiceLayout);
        customerServiceLayout.setHorizontalGroup(
            customerServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customerServiceLayout.createSequentialGroup()
                .addGroup(customerServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(customerServiceLayout.createSequentialGroup()
                        .addGroup(customerServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(csKirimbtn)
                            .addGroup(customerServiceLayout.createSequentialGroup()
                                .addGroup(customerServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(customerServiceLayout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addGroup(customerServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(customerServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, customerServiceLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(customerServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                                    .addComponent(jTextField4)
                                    .addComponent(jTextField5)
                                    .addComponent(jTextField6)
                                    .addComponent(jTextField9))))
                        .addGap(0, 42, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        customerServiceLayout.setVerticalGroup(
            customerServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customerServiceLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(customerServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(customerServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(customerServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(customerServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(38, 38, 38)
                .addGroup(customerServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(csKirimbtn)
                .addContainerGap(1230, Short.MAX_VALUE))
        );

        wishList.setBackground(new java.awt.Color(255, 255, 255));

        wishlistTabel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nama Produk", "Kategori", "Harga"
            }
        ));
        jScrollPane4.setViewportView(wishlistTabel);

        jPanel4.setBackground(new java.awt.Color(0, 45, 27));
        jPanel4.setPreferredSize(new java.awt.Dimension(545, 111));

        jLabel14.setFont(new java.awt.Font("Poppins SemiBold", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Deinary Wish");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(213, 213, 213)
                .addComponent(jLabel14)
                .addContainerGap(282, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel14)
                .addContainerGap(47, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout wishListLayout = new javax.swing.GroupLayout(wishList);
        wishList.setLayout(wishListLayout);
        wishListLayout.setHorizontalGroup(
            wishListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wishListLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
                .addGap(14, 14, 14))
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
        );
        wishListLayout.setVerticalGroup(
            wishListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wishListLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(145, Short.MAX_VALUE))
        );

        Alamat.setBackground(new java.awt.Color(255, 255, 255));

        jPanel5.setBackground(new java.awt.Color(210, 227, 157));
        jPanel5.setPreferredSize(new java.awt.Dimension(557, 111));

        jLabel15.setBackground(new java.awt.Color(67, 85, 38));
        jLabel15.setFont(new java.awt.Font("Poppins ExtraBold", 0, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(67, 85, 38));
        jLabel15.setText("Alamat");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(234, 234, 234)
                .addComponent(jLabel15)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel15)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jLabel16.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel16.setText("Nama Lengkap");

        jLabel17.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel17.setText("Nomor Telepon");

        jLabel18.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel18.setText("Provinsi");

        jLabel19.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel19.setText("Kota");

        jLabel20.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel20.setText("Kecamatan");

        jLabel21.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel21.setText("Kode Pos");

        jLabel22.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel22.setText("Detail Alamat");

        namaLengkapField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                namaLengkapFieldActionPerformed(evt);
            }
        });

        kecamatanField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kecamatanFieldActionPerformed(evt);
            }
        });

        detailAlamatField.setColumns(20);
        detailAlamatField.setRows(5);
        jScrollPane3.setViewportView(detailAlamatField);

        jLabel23.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel23.setText("Kabupaten");

        backBtn2.setText("Back");
        backBtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtn2ActionPerformed(evt);
            }
        });

        tambahAlamatButton.setText("Add");
        tambahAlamatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahAlamatButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout AlamatLayout = new javax.swing.GroupLayout(Alamat);
        Alamat.setLayout(AlamatLayout);
        AlamatLayout.setHorizontalGroup(
            AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
            .addGroup(AlamatLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AlamatLayout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(AlamatLayout.createSequentialGroup()
                        .addGroup(AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(AlamatLayout.createSequentialGroup()
                                .addGroup(AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 234, Short.MAX_VALUE)
                                .addGroup(AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane3)
                                    .addComponent(kodePosField)))
                            .addGroup(AlamatLayout.createSequentialGroup()
                                .addGroup(AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(kabupatenField, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                                    .addComponent(kecamatanField)
                                    .addComponent(kotaField)
                                    .addComponent(provinsiField)
                                    .addComponent(noTelpField)
                                    .addComponent(namaLengkapField)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AlamatLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(tambahAlamatButton)
                                .addGap(32, 32, 32)
                                .addComponent(backBtn2)))
                        .addGap(32, 32, 32))))
        );
        AlamatLayout.setVerticalGroup(
            AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AlamatLayout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backBtn2)
                    .addComponent(tambahAlamatButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(namaLengkapField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(18, 18, 18)
                .addGroup(AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(noTelpField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(provinsiField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kotaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(kabupatenField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(kecamatanField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kodePosField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addGap(18, 18, 18)
                .addGroup(AlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addGap(21, 21, 21))
        );

        panelAlamat.setBackground(new java.awt.Color(255, 255, 255));

        tambahAlamatBt.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        tambahAlamatBt.setText("Add");
        tambahAlamatBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahAlamatBtActionPerformed(evt);
            }
        });

        judulAlmatHal.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        judulAlmatHal.setText("Alamat");

        jLabel24.setText("Alamat Default");

        jLabel26.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel26.setText("Nama Lengkap");

        namaLengkapDefaultField.setEditable(false);
        namaLengkapDefaultField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                namaLengkapDefaultFieldActionPerformed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel27.setText("Nomor Telepon");

        noTelpDefaultField.setEditable(false);

        jLabel28.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel28.setText("Provinsi");

        provinsiDefaultField.setEditable(false);

        jLabel29.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel29.setText("Kota");

        kotaDefaultField.setEditable(false);

        jLabel30.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel30.setText("Kabupaten");

        kabupatenDefaultField.setEditable(false);

        jLabel31.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel31.setText("Kecamatan");

        kecamatanDefaultField.setEditable(false);
        kecamatanDefaultField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kecamatanDefaultFieldActionPerformed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel32.setText("Kode Pos");

        kodePosDefaultField.setEditable(false);

        jLabel33.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel33.setText("Detail Alamat");

        detailDefaultArea.setEditable(false);
        detailDefaultArea.setColumns(20);
        detailDefaultArea.setRows(5);
        jScrollPane6.setViewportView(detailDefaultArea);

        editBtnDefault.setText("Edit");
        editBtnDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBtnDefaultActionPerformed(evt);
            }
        });

        SimpanBtnDefault.setText("Simpan");
        SimpanBtnDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SimpanBtnDefaultActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout alamatDefaultLayout = new javax.swing.GroupLayout(alamatDefault);
        alamatDefault.setLayout(alamatDefaultLayout);
        alamatDefaultLayout.setHorizontalGroup(
            alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alamatDefaultLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel27))
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(noTelpDefaultField)
                    .addComponent(provinsiDefaultField)
                    .addComponent(kotaDefaultField)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, alamatDefaultLayout.createSequentialGroup()
                        .addComponent(namaLengkapDefaultField, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 6, Short.MAX_VALUE))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alamatDefaultLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel31)
                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(kecamatanDefaultField)
                            .addComponent(kabupatenDefaultField)
                            .addComponent(kodePosDefaultField, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(alamatDefaultLayout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(editBtnDefault)
                        .addGap(56, 56, 56)
                        .addComponent(SimpanBtnDefault)))
                .addGap(55, 55, 55))
        );
        alamatDefaultLayout.setVerticalGroup(
            alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(alamatDefaultLayout.createSequentialGroup()
                .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alamatDefaultLayout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(provinsiDefaultField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(alamatDefaultLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(alamatDefaultLayout.createSequentialGroup()
                                .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel26)
                                    .addComponent(namaLengkapDefaultField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel27)
                                    .addComponent(noTelpDefaultField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)
                                .addComponent(jLabel28))
                            .addGroup(alamatDefaultLayout.createSequentialGroup()
                                .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(kabupatenDefaultField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel30))
                                .addGap(18, 18, 18)
                                .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(kecamatanDefaultField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel31))
                                .addGap(18, 18, 18)
                                .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(kodePosDefaultField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel32))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel29)
                            .addComponent(kotaDefaultField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(alamatDefaultLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(19, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alamatDefaultLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(alamatDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(editBtnDefault)
                            .addComponent(SimpanBtnDefault))
                        .addGap(66, 66, 66))))
        );

        jLabel25.setText("Pilihan Alamat Lainnya");

        jLabel50.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel50.setText("Nama Lengkap");

        namaLengkapOptionField1.setEditable(false);
        namaLengkapOptionField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                namaLengkapOptionField1ActionPerformed(evt);
            }
        });

        jLabel51.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel51.setText("Nomor Telepon");

        noTelpOptionField1.setEditable(false);

        jLabel52.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel52.setText("Provinsi");

        provinsiOptionField1.setEditable(false);

        jLabel53.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel53.setText("Kota");

        kotaOptionField1.setEditable(false);

        jLabel54.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel54.setText("Kabupaten");

        kabupatenOptionField1.setEditable(false);

        jLabel55.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel55.setText("Kecamatan");

        kecamatanOptionField1.setEditable(false);
        kecamatanOptionField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kecamatanOptionField1ActionPerformed(evt);
            }
        });

        jLabel56.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel56.setText("Kode Pos");

        kodePosOptionField1.setEditable(false);

        jLabel57.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel57.setText("Detail Alamat");

        detailOptiontArea1.setEditable(false);
        detailOptiontArea1.setColumns(20);
        detailOptiontArea1.setRows(5);
        jScrollPane9.setViewportView(detailOptiontArea1);

        editDataAlamatOption1.setText("Edit");
        editDataAlamatOption1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editDataAlamatOption1ActionPerformed(evt);
            }
        });

        mainDataAlamatOption1.setText("Be Main");
        mainDataAlamatOption1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainDataAlamatOption1ActionPerformed(evt);
            }
        });

        hapusDataAlamatOption1.setText("hapus");
        hapusDataAlamatOption1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hapusDataAlamatOption1ActionPerformed(evt);
            }
        });

        simpanDataAlamatOption1.setText("Simpan");
        simpanDataAlamatOption1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simpanDataAlamatOption1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout alamatOption1Layout = new javax.swing.GroupLayout(alamatOption1);
        alamatOption1.setLayout(alamatOption1Layout);
        alamatOption1Layout.setHorizontalGroup(
            alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alamatOption1Layout.createSequentialGroup()
                .addContainerGap(340, Short.MAX_VALUE)
                .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hapusDataAlamatOption1)
                    .addComponent(mainDataAlamatOption1))
                .addGap(54, 54, 54)
                .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(editDataAlamatOption1)
                    .addComponent(simpanDataAlamatOption1))
                .addGap(25, 25, 25))
            .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(alamatOption1Layout.createSequentialGroup()
                    .addGap(27, 27, 27)
                    .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel53, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel52, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel51))
                        .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(noTelpOptionField1)
                        .addComponent(provinsiOptionField1)
                        .addComponent(kotaOptionField1)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, alamatOption1Layout.createSequentialGroup()
                            .addComponent(namaLengkapOptionField1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 5, Short.MAX_VALUE))
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGap(19, 19, 19)
                    .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel55)
                        .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel54))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(kecamatanOptionField1)
                        .addComponent(kabupatenOptionField1)
                        .addComponent(kodePosOptionField1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(28, 28, 28)))
        );
        alamatOption1Layout.setVerticalGroup(
            alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alamatOption1Layout.createSequentialGroup()
                .addContainerGap(163, Short.MAX_VALUE)
                .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mainDataAlamatOption1)
                    .addComponent(simpanDataAlamatOption1))
                .addGap(18, 18, 18)
                .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editDataAlamatOption1)
                    .addComponent(hapusDataAlamatOption1))
                .addGap(62, 62, 62))
            .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(alamatOption1Layout.createSequentialGroup()
                    .addGap(21, 21, 21)
                    .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(alamatOption1Layout.createSequentialGroup()
                            .addGap(75, 75, 75)
                            .addComponent(provinsiOptionField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(alamatOption1Layout.createSequentialGroup()
                            .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(alamatOption1Layout.createSequentialGroup()
                                    .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel50)
                                        .addComponent(namaLengkapOptionField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel51)
                                        .addComponent(noTelpOptionField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(12, 12, 12)
                                    .addComponent(jLabel52))
                                .addGroup(alamatOption1Layout.createSequentialGroup()
                                    .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(kabupatenOptionField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel54))
                                    .addGap(18, 18, 18)
                                    .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(kecamatanOptionField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel55))
                                    .addGap(18, 18, 18)
                                    .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(kodePosOptionField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel56))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel53)
                                .addComponent(kotaOptionField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(alamatOption1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel57)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(21, Short.MAX_VALUE)))
        );

        jLabel58.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel58.setText("Nama Lengkap");

        namaLengkapOptionField2.setEditable(false);
        namaLengkapOptionField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                namaLengkapOptionField2ActionPerformed(evt);
            }
        });

        jLabel59.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel59.setText("Nomor Telepon");

        noTelpOptionField2.setEditable(false);

        jLabel60.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel60.setText("Provinsi");

        provinsiOptionField2.setEditable(false);

        jLabel61.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel61.setText("Kota");

        kotaOptionField2.setEditable(false);

        jLabel62.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel62.setText("Kabupaten");

        kabupatenOptionField2.setEditable(false);

        jLabel63.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel63.setText("Kecamatan");

        kecamatanOptionField2.setEditable(false);
        kecamatanOptionField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kecamatanOptionField2ActionPerformed(evt);
            }
        });

        jLabel64.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel64.setText("Kode Pos");

        kodePosOptionField2.setEditable(false);

        jLabel65.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel65.setText("Detail Alamat");

        detailOptiontArea2.setEditable(false);
        detailOptiontArea2.setColumns(20);
        detailOptiontArea2.setRows(5);
        jScrollPane10.setViewportView(detailOptiontArea2);

        editDataAlamatOption2.setText("Edit");

        mainDataAlamatOption2.setText("Be Main");
        mainDataAlamatOption2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainDataAlamatOption2ActionPerformed(evt);
            }
        });

        hapusDataAlamatOption2.setText("hapus");

        simpanDataAlamatOption2.setText("Simpan");
        simpanDataAlamatOption2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simpanDataAlamatOption2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout alamatOption2Layout = new javax.swing.GroupLayout(alamatOption2);
        alamatOption2.setLayout(alamatOption2Layout);
        alamatOption2Layout.setHorizontalGroup(
            alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alamatOption2Layout.createSequentialGroup()
                .addContainerGap(338, Short.MAX_VALUE)
                .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(alamatOption2Layout.createSequentialGroup()
                        .addComponent(mainDataAlamatOption2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(simpanDataAlamatOption2))
                    .addGroup(alamatOption2Layout.createSequentialGroup()
                        .addComponent(hapusDataAlamatOption2)
                        .addGap(55, 55, 55)
                        .addComponent(editDataAlamatOption2)))
                .addGap(28, 28, 28))
            .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(alamatOption2Layout.createSequentialGroup()
                    .addGap(27, 27, 27)
                    .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel61, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel60, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel59))
                        .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(noTelpOptionField2)
                        .addComponent(provinsiOptionField2)
                        .addComponent(kotaOptionField2)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, alamatOption2Layout.createSequentialGroup()
                            .addComponent(namaLengkapOptionField2, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 5, Short.MAX_VALUE))
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGap(19, 19, 19)
                    .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel63)
                        .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel62))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(kecamatanOptionField2)
                        .addComponent(kabupatenOptionField2)
                        .addComponent(kodePosOptionField2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(28, 28, 28)))
        );
        alamatOption2Layout.setVerticalGroup(
            alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alamatOption2Layout.createSequentialGroup()
                .addContainerGap(163, Short.MAX_VALUE)
                .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mainDataAlamatOption2)
                    .addComponent(simpanDataAlamatOption2))
                .addGap(18, 18, 18)
                .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editDataAlamatOption2)
                    .addComponent(hapusDataAlamatOption2))
                .addGap(62, 62, 62))
            .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(alamatOption2Layout.createSequentialGroup()
                    .addGap(21, 21, 21)
                    .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(alamatOption2Layout.createSequentialGroup()
                            .addGap(75, 75, 75)
                            .addComponent(provinsiOptionField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(alamatOption2Layout.createSequentialGroup()
                            .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(alamatOption2Layout.createSequentialGroup()
                                    .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel58)
                                        .addComponent(namaLengkapOptionField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel59)
                                        .addComponent(noTelpOptionField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(12, 12, 12)
                                    .addComponent(jLabel60))
                                .addGroup(alamatOption2Layout.createSequentialGroup()
                                    .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(kabupatenOptionField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel62))
                                    .addGap(18, 18, 18)
                                    .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(kecamatanOptionField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel63))
                                    .addGap(18, 18, 18)
                                    .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(kodePosOptionField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel64))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel61)
                                .addComponent(kotaOptionField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(alamatOption2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel65)
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(21, Short.MAX_VALUE)))
        );

        jLabel66.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel66.setText("Nama Lengkap");

        namaLengkapOptionField3.setEditable(false);
        namaLengkapOptionField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                namaLengkapOptionField3ActionPerformed(evt);
            }
        });

        jLabel67.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel67.setText("Nomor Telepon");

        noTelpOptionField3.setEditable(false);

        jLabel68.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel68.setText("Provinsi");

        provinsiOptionField3.setEditable(false);

        jLabel69.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel69.setText("Kota");

        kotaOptionField3.setEditable(false);

        jLabel70.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel70.setText("Kabupaten");

        kabupatenOptionField3.setEditable(false);

        jLabel71.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel71.setText("Kecamatan");

        kecamatanOptionField3.setEditable(false);
        kecamatanOptionField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kecamatanOptionField3ActionPerformed(evt);
            }
        });

        jLabel72.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel72.setText("Kode Pos");

        kodePosOptionField3.setEditable(false);

        jLabel73.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel73.setText("Detail Alamat");

        detailOptiontArea3.setEditable(false);
        detailOptiontArea3.setColumns(20);
        detailOptiontArea3.setRows(5);
        jScrollPane11.setViewportView(detailOptiontArea3);

        editDataAlamatOption3.setText("Edit");

        mainDataAlamatOption3.setText("Be Main");
        mainDataAlamatOption3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainDataAlamatOption3ActionPerformed(evt);
            }
        });

        hapusDataAlamatOption3.setText("hapus");

        simpanDataAlamatOption3.setText("Simpan");
        simpanDataAlamatOption3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simpanDataAlamatOption3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout alamatOption3Layout = new javax.swing.GroupLayout(alamatOption3);
        alamatOption3.setLayout(alamatOption3Layout);
        alamatOption3Layout.setHorizontalGroup(
            alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alamatOption3Layout.createSequentialGroup()
                .addContainerGap(338, Short.MAX_VALUE)
                .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(alamatOption3Layout.createSequentialGroup()
                        .addComponent(mainDataAlamatOption3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(simpanDataAlamatOption3))
                    .addGroup(alamatOption3Layout.createSequentialGroup()
                        .addComponent(hapusDataAlamatOption3)
                        .addGap(55, 55, 55)
                        .addComponent(editDataAlamatOption3)))
                .addGap(28, 28, 28))
            .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(alamatOption3Layout.createSequentialGroup()
                    .addGap(27, 27, 27)
                    .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel69, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel68, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel67))
                        .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(noTelpOptionField3)
                        .addComponent(provinsiOptionField3)
                        .addComponent(kotaOptionField3)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, alamatOption3Layout.createSequentialGroup()
                            .addComponent(namaLengkapOptionField3, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 5, Short.MAX_VALUE))
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGap(19, 19, 19)
                    .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel71)
                        .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel70))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(kecamatanOptionField3)
                        .addComponent(kabupatenOptionField3)
                        .addComponent(kodePosOptionField3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(28, 28, 28)))
        );
        alamatOption3Layout.setVerticalGroup(
            alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alamatOption3Layout.createSequentialGroup()
                .addContainerGap(163, Short.MAX_VALUE)
                .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mainDataAlamatOption3)
                    .addComponent(simpanDataAlamatOption3))
                .addGap(18, 18, 18)
                .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editDataAlamatOption3)
                    .addComponent(hapusDataAlamatOption3))
                .addGap(62, 62, 62))
            .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(alamatOption3Layout.createSequentialGroup()
                    .addGap(21, 21, 21)
                    .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(alamatOption3Layout.createSequentialGroup()
                            .addGap(75, 75, 75)
                            .addComponent(provinsiOptionField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(alamatOption3Layout.createSequentialGroup()
                            .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(alamatOption3Layout.createSequentialGroup()
                                    .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel66)
                                        .addComponent(namaLengkapOptionField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel67)
                                        .addComponent(noTelpOptionField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(12, 12, 12)
                                    .addComponent(jLabel68))
                                .addGroup(alamatOption3Layout.createSequentialGroup()
                                    .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(kabupatenOptionField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel70))
                                    .addGap(18, 18, 18)
                                    .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(kecamatanOptionField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel71))
                                    .addGap(18, 18, 18)
                                    .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(kodePosOptionField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel72))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel69)
                                .addComponent(kotaOptionField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(alamatOption3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel73)
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(21, Short.MAX_VALUE)))
        );

        jLabel74.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel74.setText("Nama Lengkap");

        namaLengkapOptionField4.setEditable(false);
        namaLengkapOptionField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                namaLengkapOptionField4ActionPerformed(evt);
            }
        });

        jLabel75.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel75.setText("Nomor Telepon");

        noTelpOptionField4.setEditable(false);

        jLabel76.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel76.setText("Provinsi");

        provinsiOptionField4.setEditable(false);

        jLabel77.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel77.setText("Kota");

        kotaOptionField4.setEditable(false);

        jLabel78.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel78.setText("Kabupaten");

        kabupatenOptionField4.setEditable(false);

        jLabel79.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel79.setText("Kecamatan");

        kecamatanOptionField4.setEditable(false);
        kecamatanOptionField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kecamatanOptionField4ActionPerformed(evt);
            }
        });

        jLabel80.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel80.setText("Kode Pos");

        kodePosOptionField4.setEditable(false);

        jLabel81.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel81.setText("Detail Alamat");

        detailOptiontArea4.setEditable(false);
        detailOptiontArea4.setColumns(20);
        detailOptiontArea4.setRows(5);
        jScrollPane12.setViewportView(detailOptiontArea4);

        editDataAlamatOption4.setText("Edit");

        mainDataAlamatOption4.setText("Be Main");
        mainDataAlamatOption4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainDataAlamatOption4ActionPerformed(evt);
            }
        });

        hapusDataAlamatOption4.setText("hapus");

        simpanDataAlamatOption4.setText("Simpan");
        simpanDataAlamatOption4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simpanDataAlamatOption4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout alamatOption4Layout = new javax.swing.GroupLayout(alamatOption4);
        alamatOption4.setLayout(alamatOption4Layout);
        alamatOption4Layout.setHorizontalGroup(
            alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alamatOption4Layout.createSequentialGroup()
                .addContainerGap(338, Short.MAX_VALUE)
                .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(alamatOption4Layout.createSequentialGroup()
                        .addComponent(mainDataAlamatOption4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(simpanDataAlamatOption4))
                    .addGroup(alamatOption4Layout.createSequentialGroup()
                        .addComponent(hapusDataAlamatOption4)
                        .addGap(55, 55, 55)
                        .addComponent(editDataAlamatOption4)))
                .addGap(28, 28, 28))
            .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(alamatOption4Layout.createSequentialGroup()
                    .addGap(27, 27, 27)
                    .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel77, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel76, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel75))
                        .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(noTelpOptionField4)
                        .addComponent(provinsiOptionField4)
                        .addComponent(kotaOptionField4)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, alamatOption4Layout.createSequentialGroup()
                            .addComponent(namaLengkapOptionField4, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 5, Short.MAX_VALUE))
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGap(19, 19, 19)
                    .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel79)
                        .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel78))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(kecamatanOptionField4)
                        .addComponent(kabupatenOptionField4)
                        .addComponent(kodePosOptionField4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(28, 28, 28)))
        );
        alamatOption4Layout.setVerticalGroup(
            alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alamatOption4Layout.createSequentialGroup()
                .addContainerGap(163, Short.MAX_VALUE)
                .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mainDataAlamatOption4)
                    .addComponent(simpanDataAlamatOption4))
                .addGap(18, 18, 18)
                .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editDataAlamatOption4)
                    .addComponent(hapusDataAlamatOption4))
                .addGap(62, 62, 62))
            .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(alamatOption4Layout.createSequentialGroup()
                    .addGap(21, 21, 21)
                    .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(alamatOption4Layout.createSequentialGroup()
                            .addGap(75, 75, 75)
                            .addComponent(provinsiOptionField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(alamatOption4Layout.createSequentialGroup()
                            .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(alamatOption4Layout.createSequentialGroup()
                                    .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel74)
                                        .addComponent(namaLengkapOptionField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel75)
                                        .addComponent(noTelpOptionField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(12, 12, 12)
                                    .addComponent(jLabel76))
                                .addGroup(alamatOption4Layout.createSequentialGroup()
                                    .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(kabupatenOptionField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel78))
                                    .addGap(18, 18, 18)
                                    .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(kecamatanOptionField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel79))
                                    .addGap(18, 18, 18)
                                    .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(kodePosOptionField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel80))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel77)
                                .addComponent(kotaOptionField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(alamatOption4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel81)
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(21, Short.MAX_VALUE)))
        );

        jLabel82.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel82.setText("Nama Lengkap");

        namaLengkapOptionField5.setEditable(false);
        namaLengkapOptionField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                namaLengkapOptionField5ActionPerformed(evt);
            }
        });

        jLabel83.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel83.setText("Nomor Telepon");

        noTelpOptionField5.setEditable(false);

        jLabel84.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel84.setText("Provinsi");

        provinsiOptionField5.setEditable(false);

        jLabel85.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel85.setText("Kota");

        kotaOptionField5.setEditable(false);

        jLabel86.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel86.setText("Kabupaten");

        kabupatenOptionField5.setEditable(false);

        jLabel87.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel87.setText("Kecamatan");

        kecamatanOptionField5.setEditable(false);
        kecamatanOptionField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kecamatanOptionField5ActionPerformed(evt);
            }
        });

        jLabel88.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel88.setText("Kode Pos");

        kodePosOptionField5.setEditable(false);

        jLabel89.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel89.setText("Detail Alamat");

        detailOptiontArea5.setEditable(false);
        detailOptiontArea5.setColumns(20);
        detailOptiontArea5.setRows(5);
        jScrollPane13.setViewportView(detailOptiontArea5);

        editDataAlamatOption5.setText("Edit");

        mainDataAlamatOption5.setText("Be Main");
        mainDataAlamatOption5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainDataAlamatOption5ActionPerformed(evt);
            }
        });

        hapusDataAlamatOption5.setText("hapus");

        simpanDataAlamatOption5.setText("Simpan");
        simpanDataAlamatOption5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simpanDataAlamatOption5ActionPerformed(evt);
            }
        });
        simpanDataAlamatOption5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                simpanDataAlamatOption5KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout alamatOption5Layout = new javax.swing.GroupLayout(alamatOption5);
        alamatOption5.setLayout(alamatOption5Layout);
        alamatOption5Layout.setHorizontalGroup(
            alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alamatOption5Layout.createSequentialGroup()
                .addContainerGap(338, Short.MAX_VALUE)
                .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(alamatOption5Layout.createSequentialGroup()
                        .addComponent(mainDataAlamatOption5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(simpanDataAlamatOption5))
                    .addGroup(alamatOption5Layout.createSequentialGroup()
                        .addComponent(hapusDataAlamatOption5)
                        .addGap(55, 55, 55)
                        .addComponent(editDataAlamatOption5)))
                .addGap(28, 28, 28))
            .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(alamatOption5Layout.createSequentialGroup()
                    .addGap(27, 27, 27)
                    .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel85, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel84, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel83))
                        .addComponent(jLabel82, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel89, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(noTelpOptionField5)
                        .addComponent(provinsiOptionField5)
                        .addComponent(kotaOptionField5)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, alamatOption5Layout.createSequentialGroup()
                            .addComponent(namaLengkapOptionField5, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 5, Short.MAX_VALUE))
                        .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGap(19, 19, 19)
                    .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel87)
                        .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel86))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(kecamatanOptionField5)
                        .addComponent(kabupatenOptionField5)
                        .addComponent(kodePosOptionField5, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(28, 28, 28)))
        );
        alamatOption5Layout.setVerticalGroup(
            alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alamatOption5Layout.createSequentialGroup()
                .addContainerGap(163, Short.MAX_VALUE)
                .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mainDataAlamatOption5)
                    .addComponent(simpanDataAlamatOption5))
                .addGap(18, 18, 18)
                .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editDataAlamatOption5)
                    .addComponent(hapusDataAlamatOption5))
                .addGap(62, 62, 62))
            .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(alamatOption5Layout.createSequentialGroup()
                    .addGap(21, 21, 21)
                    .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(alamatOption5Layout.createSequentialGroup()
                            .addGap(75, 75, 75)
                            .addComponent(provinsiOptionField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(alamatOption5Layout.createSequentialGroup()
                            .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(alamatOption5Layout.createSequentialGroup()
                                    .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel82)
                                        .addComponent(namaLengkapOptionField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel83)
                                        .addComponent(noTelpOptionField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(12, 12, 12)
                                    .addComponent(jLabel84))
                                .addGroup(alamatOption5Layout.createSequentialGroup()
                                    .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(kabupatenOptionField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel86))
                                    .addGap(18, 18, 18)
                                    .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(kecamatanOptionField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel87))
                                    .addGap(18, 18, 18)
                                    .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(kodePosOptionField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel88))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel85)
                                .addComponent(kotaOptionField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(alamatOption5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel89)
                        .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(21, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout panelDataAlamatLayout = new javax.swing.GroupLayout(panelDataAlamat);
        panelDataAlamat.setLayout(panelDataAlamatLayout);
        panelDataAlamatLayout.setHorizontalGroup(
            panelDataAlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDataAlamatLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDataAlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(alamatOption1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(alamatOption2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(alamatOption3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(alamatOption4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(alamatOption5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        panelDataAlamatLayout.setVerticalGroup(
            panelDataAlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDataAlamatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(alamatOption1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alamatOption2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alamatOption3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alamatOption4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(alamatOption5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(2442, Short.MAX_VALUE))
        );

        jScrollPane5.setViewportView(panelDataAlamat);

        javax.swing.GroupLayout panelAlamatLayout = new javax.swing.GroupLayout(panelAlamat);
        panelAlamat.setLayout(panelAlamatLayout);
        panelAlamatLayout.setHorizontalGroup(
            panelAlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAlamatLayout.createSequentialGroup()
                .addGroup(panelAlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAlamatLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(alamatDefault, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelAlamatLayout.createSequentialGroup()
                        .addGap(242, 242, 242)
                        .addComponent(jLabel24)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelAlamatLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelAlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5)
                            .addGroup(panelAlamatLayout.createSequentialGroup()
                                .addComponent(judulAlmatHal, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(328, 328, 328)
                                .addComponent(tambahAlamatBt)))))
                .addContainerGap())
            .addGroup(panelAlamatLayout.createSequentialGroup()
                .addGap(241, 241, 241)
                .addComponent(jLabel25)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelAlamatLayout.setVerticalGroup(
            panelAlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAlamatLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(judulAlmatHal)
                    .addComponent(tambahAlamatBt))
                .addGap(10, 10, 10)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alamatDefault, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 1496, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane14.setViewportView(panelAlamat);

        javax.swing.GroupLayout dataAlamatLayout = new javax.swing.GroupLayout(dataAlamat);
        dataAlamat.setLayout(dataAlamatLayout);
        dataAlamatLayout.setHorizontalGroup(
            dataAlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane14, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        dataAlamatLayout.setVerticalGroup(
            dataAlamatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dataAlamatLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 1949, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout isiProfilLayout = new javax.swing.GroupLayout(isiProfil);
        isiProfil.setLayout(isiProfilLayout);
        isiProfilLayout.setHorizontalGroup(
            isiProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 608, Short.MAX_VALUE)
            .addGroup(isiProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(historyPesanan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(isiProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(customerService, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(isiProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(isiProfilLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(wishList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(isiProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(Alamat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(isiProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(dataAlamat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        isiProfilLayout.setVerticalGroup(
            isiProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1967, Short.MAX_VALUE)
            .addGroup(isiProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(historyPesanan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(isiProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(customerService, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(isiProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(isiProfilLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(wishList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(isiProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(Alamat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(isiProfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(isiProfilLayout.createSequentialGroup()
                    .addComponent(dataAlamat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(MenuProfil, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(isiProfil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MenuProfil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(isiProfil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
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

    private void hsitoryPesanbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hsitoryPesanbtnActionPerformed
        customerService.setVisible(false);
        Alamat.setVisible(false);
        wishList.setVisible(false);
        historyPesanan.setVisible(true);

        // Hapus semua komponen dalam isiProfil
        isiProfil.removeAll();

        // Set tata letak baru
        isiProfil.setLayout(new BorderLayout());

        // Tambahkan panel historyPesanan
        isiProfil.add(historyPesanan, BorderLayout.CENTER);

        // Tampilkan ulang panel
        isiProfil.repaint();
        isiProfil.revalidate();

        // Muat data histori
        loadHistoryPesanan();
    }//GEN-LAST:event_hsitoryPesanbtnActionPerformed

    private void emailProfilFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailProfilFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailProfilFieldActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void csBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_csBtnActionPerformed
        Alamat.setVisible(false);
        wishList.setVisible(false);
        historyPesanan.setVisible(false);
        customerService.setVisible(true);

        
        // Hapus semua komponen dalam isiAdmin
        isiProfil.removeAll();

        // Set tata letak baru
        isiProfil.setLayout(new BorderLayout());

        // Tambahkan panel KelolaProduk
        isiProfil.add(customerService, BorderLayout.CENTER);

        // Tampilkan ulang panel
        isiProfil.repaint();
        isiProfil.revalidate();
    }//GEN-LAST:event_csBtnActionPerformed

    private void wishBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wishBtnActionPerformed
        Alamat.setVisible(false);
        historyPesanan.setVisible(false);
        customerService.setVisible(false);
        wishList.setVisible(true);

        
        // Hapus semua komponen dalam isiAdmin
        isiProfil.removeAll();

        // Set tata letak baru
        isiProfil.setLayout(new BorderLayout());

        // Tambahkan panel KelolaProduk
        isiProfil.add(wishList, BorderLayout.CENTER);

        // Tampilkan ulang panel
        isiProfil.repaint();
        isiProfil.revalidate();
        
        loadWishlist();
    }//GEN-LAST:event_wishBtnActionPerformed

    private void kecamatanFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kecamatanFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kecamatanFieldActionPerformed

    private void namaLengkapFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namaLengkapFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namaLengkapFieldActionPerformed

    private void alamatBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alamatBtnActionPerformed
        wishList.setVisible(false);
        historyPesanan.setVisible(false);
        customerService.setVisible(false);
        dataAlamat.setVisible(true);

        // Hapus semua komponen dalam isiProfil
        isiProfil.removeAll();

        // Set tata letak baru
        isiProfil.setLayout(new BorderLayout());

        // Tambahkan panel alamat1
        isiProfil.add(dataAlamat, BorderLayout.CENTER);

        // Tampilkan ulang panel
        isiProfil.repaint();
        isiProfil.revalidate();

    }//GEN-LAST:event_alamatBtnActionPerformed

    private void backBtnProfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBtnProfilActionPerformed
        MenuUtama Menu = new MenuUtama(currentBuyerId); // Membuka halaman login
        Menu.setVisible(true);
        this.dispose(); // Tutup halaman registrasi
    }//GEN-LAST:event_backBtnProfilActionPerformed

    private void logOutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logOutBtnActionPerformed
        // Tampilkan dialog konfirmasi
        int response = JOptionPane.showConfirmDialog(
            this, 
            "Apakah Anda yakin ingin log out?", 
            "Konfirmasi Log Out", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE
        );

        // Jika pengguna memilih "Ya"
        if (response == JOptionPane.YES_OPTION) {
            // Navigasi ke menu login
            Login menuLogin = new Login(); // Ganti dengan halaman login Anda
            menuLogin.setVisible(true);
            this.dispose(); // Tutup ProfilPage
        }
        // Jika "Tidak", tetap di ProfilPage (tidak ada tindakan tambahan yang diperlukan)
    }//GEN-LAST:event_logOutBtnActionPerformed

    private void historyPesanTabelAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_historyPesanTabelAncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_historyPesanTabelAncestorAdded

    private void tambahAlamatBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tambahAlamatBtActionPerformed
        wishList.setVisible(false);
        historyPesanan.setVisible(false);
        customerService.setVisible(false);
        Alamat.setVisible(true);
        
        // Hapus semua komponen dalam isiAdmin
        isiProfil.removeAll();

        // Set tata letak baru
        isiProfil.setLayout(new BorderLayout());

        // Tambahkan panel KelolaProduk
        isiProfil.add(Alamat, BorderLayout.CENTER);

        // Tampilkan ulang panel
        isiProfil.repaint();
        isiProfil.revalidate();    
    }//GEN-LAST:event_tambahAlamatBtActionPerformed

    private void backBtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBtn2ActionPerformed
        wishList.setVisible(false);
        historyPesanan.setVisible(false);
        customerService.setVisible(false);
        Alamat.setVisible(true);
        
        // Hapus semua komponen dalam isiAdmin
        isiProfil.removeAll();

        // Set tata letak baru
        isiProfil.setLayout(new BorderLayout());

        // Tambahkan panel KelolaProduk
        isiProfil.add(dataAlamat, BorderLayout.CENTER);

        // Tampilkan ulang panel
        isiProfil.repaint();
        isiProfil.revalidate();    
    }//GEN-LAST:event_backBtn2ActionPerformed

    private void tambahAlamatButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tambahAlamatButtonActionPerformed
        // Validasi apakah semua field sudah terisi
        if (namaLengkapField.getText().isEmpty() ||
            noTelpField.getText().isEmpty() ||
            provinsiField.getText().isEmpty() ||
            kotaField.getText().isEmpty() ||
            kabupatenField.getText().isEmpty() ||
            kecamatanField.getText().isEmpty() ||
            kodePosField.getText().isEmpty() ||
            detailAlamatField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Data belum terisi semua, harap mengisi data alamat anda.");
            return; // Keluar jika data belum lengkap
        }

        // Ambil data dari field
        String fullName = namaLengkapField.getText();
        String phoneNumber = noTelpField.getText();
        String province = provinsiField.getText();
        String city = kotaField.getText();
        String district = kabupatenField.getText();
        String subdistrict = kecamatanField.getText();
        String postalCode = kodePosField.getText();
        String addressDetail = detailAlamatField.getText();

        // Simpan alamat ke database
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Periksa apakah sudah ada alamat default
            String checkDefaultQuery = "SELECT COUNT(*) AS total_default FROM addresses WHERE id_buyer = ? AND is_default = 1";
            PreparedStatement checkStmt = conn.prepareStatement(checkDefaultQuery);
            checkStmt.setInt(1, currentBuyerId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            boolean hasDefault = rs.getInt("total_default") > 0;

            // Tentukan apakah alamat baru ini default
            boolean isDefault = !hasDefault;

            // Simpan alamat baru
            String insertQuery = "INSERT INTO addresses (id_buyer, full_name, phone_number, province, city, district, subdistrict, postal_code, address_detail, is_default) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, currentBuyerId);
            insertStmt.setString(2, fullName);
            insertStmt.setString(3, phoneNumber);
            insertStmt.setString(4, province);
            insertStmt.setString(5, city);
            insertStmt.setString(6, district);
            insertStmt.setString(7, subdistrict);
            insertStmt.setString(8, postalCode);
            insertStmt.setString(9, addressDetail);
            insertStmt.setBoolean(10, isDefault);
            insertStmt.executeUpdate();

            if (isDefault) {
                JOptionPane.showMessageDialog(null, "Alamat berhasil ditambahkan sebagai default.");
            } else {
                JOptionPane.showMessageDialog(null, "Alamat berhasil ditambahkan sebagai opsi.");
            }

            // Bersihkan field setelah penambahan
            clearAddressFields();
            loadAlamatOpsi();
            loadDefaultAddress();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menambahkan alamat: " + e.getMessage());
        }
    }//GEN-LAST:event_tambahAlamatButtonActionPerformed

    private void namaLengkapDefaultFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namaLengkapDefaultFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namaLengkapDefaultFieldActionPerformed

    private void kecamatanDefaultFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kecamatanDefaultFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kecamatanDefaultFieldActionPerformed

    private void editBtnDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBtnDefaultActionPerformed
       
    }//GEN-LAST:event_editBtnDefaultActionPerformed

    private void namaLengkapOptionField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namaLengkapOptionField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namaLengkapOptionField1ActionPerformed

    private void kecamatanOptionField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kecamatanOptionField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kecamatanOptionField1ActionPerformed

    private void mainDataAlamatOption1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainDataAlamatOption1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mainDataAlamatOption1ActionPerformed

    private void namaLengkapOptionField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namaLengkapOptionField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namaLengkapOptionField2ActionPerformed

    private void kecamatanOptionField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kecamatanOptionField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kecamatanOptionField2ActionPerformed

    private void mainDataAlamatOption2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainDataAlamatOption2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mainDataAlamatOption2ActionPerformed

    private void namaLengkapOptionField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namaLengkapOptionField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namaLengkapOptionField3ActionPerformed

    private void kecamatanOptionField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kecamatanOptionField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kecamatanOptionField3ActionPerformed

    private void mainDataAlamatOption3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainDataAlamatOption3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mainDataAlamatOption3ActionPerformed

    private void namaLengkapOptionField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namaLengkapOptionField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namaLengkapOptionField4ActionPerformed

    private void kecamatanOptionField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kecamatanOptionField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kecamatanOptionField4ActionPerformed

    private void mainDataAlamatOption4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainDataAlamatOption4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mainDataAlamatOption4ActionPerformed

    private void namaLengkapOptionField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namaLengkapOptionField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namaLengkapOptionField5ActionPerformed

    private void kecamatanOptionField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kecamatanOptionField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kecamatanOptionField5ActionPerformed

    private void mainDataAlamatOption5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainDataAlamatOption5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mainDataAlamatOption5ActionPerformed

    private void SimpanBtnDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SimpanBtnDefaultActionPerformed
        
    }//GEN-LAST:event_SimpanBtnDefaultActionPerformed

    private void editDataAlamatOption1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editDataAlamatOption1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editDataAlamatOption1ActionPerformed

    private void simpanDataAlamatOption5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_simpanDataAlamatOption5KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_simpanDataAlamatOption5KeyPressed

    private void simpanDataAlamatOption1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simpanDataAlamatOption1ActionPerformed
        JTextField[] fields = {
            namaLengkapOptionField1, noTelpOptionField1, provinsiOptionField1,
            kotaOptionField1, kabupatenOptionField1, kecamatanOptionField1,
            kodePosOptionField1
        };
        JTextArea[] detailFields = {detailOptiontArea1};

        // Validasi data tidak kosong
        for (JTextField field : fields) {
            if (field.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua field wajib diisi.");
                return;
            }
        }
        if (detailFields[0].getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Detail alamat tidak boleh kosong.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String updateQuery = "UPDATE addresses SET full_name = ?, phone_number = ?, province = ?, city = ?, " +
                                 "district = ?, subdistrict = ?, postal_code = ?, address_detail = ? " +
                                 "WHERE id_buyer = ? AND full_name = ? AND phone_number = ?";
            PreparedStatement stmt = connection.prepareStatement(updateQuery);

            // Set parameter
            stmt.setString(1, namaLengkapOptionField1.getText());
            stmt.setString(2, noTelpOptionField1.getText());
            stmt.setString(3, provinsiOptionField1.getText());
            stmt.setString(4, kotaOptionField1.getText());
            stmt.setString(5, kabupatenOptionField1.getText());
            stmt.setString(6, kecamatanOptionField1.getText());
            stmt.setString(7, kodePosOptionField1.getText());
            stmt.setString(8, detailOptiontArea1.getText());
            stmt.setInt(9, currentBuyerId); // ID pembeli
            stmt.setString(10, namaLengkapOptionField1.getText());
            stmt.setString(11, noTelpOptionField1.getText());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Alamat berhasil diperbarui.");
            } else {
                JOptionPane.showMessageDialog(null, "Gagal memperbarui alamat.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan alamat: " + ex.getMessage());
        }
    }//GEN-LAST:event_simpanDataAlamatOption1ActionPerformed

    private void namaProfilFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namaProfilFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namaProfilFieldActionPerformed

    private void noHpProfilFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noHpProfilFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_noHpProfilFieldActionPerformed

    private void tglLahirProfilFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglLahirProfilFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tglLahirProfilFieldActionPerformed

    private void pointsProfilFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pointsProfilFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pointsProfilFieldActionPerformed

    private void hapusDataAlamatOption1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hapusDataAlamatOption1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hapusDataAlamatOption1ActionPerformed

    private void simpanDataAlamatOption2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simpanDataAlamatOption2ActionPerformed
        JTextField[] fields = {
            namaLengkapOptionField2, noTelpOptionField2, provinsiOptionField2,
            kotaOptionField2, kabupatenOptionField2, kecamatanOptionField2,
            kodePosOptionField2
        };
        JTextArea[] detailFields = {detailOptiontArea2};

        // Validasi data tidak kosong
        for (JTextField field : fields) {
            if (field.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua field wajib diisi.");
                return;
            }
        }
        if (detailFields[0].getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Detail alamat tidak boleh kosong.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String updateQuery = "UPDATE addresses SET full_name = ?, phone_number = ?, province = ?, city = ?, " +
                                 "district = ?, subdistrict = ?, postal_code = ?, address_detail = ? " +
                                 "WHERE id_buyer = ? AND full_name = ? AND phone_number = ?";
            PreparedStatement stmt = connection.prepareStatement(updateQuery);

            // Set parameter
            stmt.setString(1, namaLengkapOptionField2.getText());
            stmt.setString(2, noTelpOptionField2.getText());
            stmt.setString(3, provinsiOptionField2.getText());
            stmt.setString(4, kotaOptionField2.getText());
            stmt.setString(5, kabupatenOptionField2.getText());
            stmt.setString(6, kecamatanOptionField2.getText());
            stmt.setString(7, kodePosOptionField2.getText());
            stmt.setString(8, detailOptiontArea2.getText());
            stmt.setInt(9, currentBuyerId); // ID pembeli
            stmt.setString(10, namaLengkapOptionField2.getText());
            stmt.setString(11, noTelpOptionField2.getText());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Alamat berhasil diperbarui.");
            } else {
                JOptionPane.showMessageDialog(null, "Gagal memperbarui alamat.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan alamat: " + ex.getMessage());
        }
    }//GEN-LAST:event_simpanDataAlamatOption2ActionPerformed

    private void simpanDataAlamatOption3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simpanDataAlamatOption3ActionPerformed
        JTextField[] fields = {
            namaLengkapOptionField3, noTelpOptionField3, provinsiOptionField3,
            kotaOptionField3, kabupatenOptionField3, kecamatanOptionField3,
            kodePosOptionField3
        };
        JTextArea[] detailFields = {detailOptiontArea3};

        // Validasi data tidak kosong
        for (JTextField field : fields) {
            if (field.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua field wajib diisi.");
                return;
            }
        }
        if (detailFields[0].getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Detail alamat tidak boleh kosong.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String updateQuery = "UPDATE addresses SET full_name = ?, phone_number = ?, province = ?, city = ?, " +
                                 "district = ?, subdistrict = ?, postal_code = ?, address_detail = ? " +
                                 "WHERE id_buyer = ? AND full_name = ? AND phone_number = ?";
            PreparedStatement stmt = connection.prepareStatement(updateQuery);

            // Set parameter
            stmt.setString(1, namaLengkapOptionField3.getText());
            stmt.setString(2, noTelpOptionField3.getText());
            stmt.setString(3, provinsiOptionField3.getText());
            stmt.setString(4, kotaOptionField3.getText());
            stmt.setString(5, kabupatenOptionField3.getText());
            stmt.setString(6, kecamatanOptionField3.getText());
            stmt.setString(7, kodePosOptionField3.getText());
            stmt.setString(8, detailOptiontArea3.getText());
            stmt.setInt(9, currentBuyerId); // ID pembeli
            stmt.setString(10, namaLengkapOptionField3.getText());
            stmt.setString(11, noTelpOptionField3.getText());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Alamat berhasil diperbarui.");
            } else {
                JOptionPane.showMessageDialog(null, "Gagal memperbarui alamat.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan alamat: " + ex.getMessage());
        }
    }//GEN-LAST:event_simpanDataAlamatOption3ActionPerformed

    private void simpanDataAlamatOption4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simpanDataAlamatOption4ActionPerformed
        JTextField[] fields = {
            namaLengkapOptionField4, noTelpOptionField4, provinsiOptionField4,
            kotaOptionField4, kabupatenOptionField4, kecamatanOptionField4,
            kodePosOptionField4
        };
        JTextArea[] detailFields = {detailOptiontArea4};

        // Validasi data tidak kosong
        for (JTextField field : fields) {
            if (field.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua field wajib diisi.");
                return;
            }
        }
        if (detailFields[0].getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Detail alamat tidak boleh kosong.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String updateQuery = "UPDATE addresses SET full_name = ?, phone_number = ?, province = ?, city = ?, " +
                                 "district = ?, subdistrict = ?, postal_code = ?, address_detail = ? " +
                                 "WHERE id_buyer = ? AND full_name = ? AND phone_number = ?";
            PreparedStatement stmt = connection.prepareStatement(updateQuery);

            // Set parameter
            stmt.setString(1, namaLengkapOptionField4.getText());
            stmt.setString(2, noTelpOptionField4.getText());
            stmt.setString(3, provinsiOptionField4.getText());
            stmt.setString(4, kotaOptionField4.getText());
            stmt.setString(5, kabupatenOptionField4.getText());
            stmt.setString(6, kecamatanOptionField4.getText());
            stmt.setString(7, kodePosOptionField4.getText());
            stmt.setString(8, detailOptiontArea4.getText());
            stmt.setInt(9, currentBuyerId); // ID pembeli
            stmt.setString(10, namaLengkapOptionField4.getText());
            stmt.setString(11, noTelpOptionField4.getText());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Alamat berhasil diperbarui.");
            } else {
                JOptionPane.showMessageDialog(null, "Gagal memperbarui alamat.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan alamat: " + ex.getMessage());
        }
    }//GEN-LAST:event_simpanDataAlamatOption4ActionPerformed

    private void simpanDataAlamatOption5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simpanDataAlamatOption5ActionPerformed
        JTextField[] fields = {
            namaLengkapOptionField5, noTelpOptionField5, provinsiOptionField5,
            kotaOptionField5, kabupatenOptionField5, kecamatanOptionField5,
            kodePosOptionField5
        };
        JTextArea[] detailFields = {detailOptiontArea5};

        // Validasi data tidak kosong
        for (JTextField field : fields) {
            if (field.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua field wajib diisi.");
                return;
            }
        }
        if (detailFields[0].getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Detail alamat tidak boleh kosong.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String updateQuery = "UPDATE addresses SET full_name = ?, phone_number = ?, province = ?, city = ?, " +
                                 "district = ?, subdistrict = ?, postal_code = ?, address_detail = ? " +
                                 "WHERE id_buyer = ? AND full_name = ? AND phone_number = ?";
            PreparedStatement stmt = connection.prepareStatement(updateQuery);

            // Set parameter
            stmt.setString(1, namaLengkapOptionField5.getText());
            stmt.setString(2, noTelpOptionField5.getText());
            stmt.setString(3, provinsiOptionField5.getText());
            stmt.setString(4, kotaOptionField5.getText());
            stmt.setString(5, kabupatenOptionField5.getText());
            stmt.setString(6, kecamatanOptionField5.getText());
            stmt.setString(7, kodePosOptionField5.getText());
            stmt.setString(8, detailOptiontArea5.getText());
            stmt.setInt(9, currentBuyerId); // ID pembeli
            stmt.setString(10, namaLengkapOptionField5.getText());
            stmt.setString(11, noTelpOptionField5.getText());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Alamat berhasil diperbarui.");
            } else {
                JOptionPane.showMessageDialog(null, "Gagal memperbarui alamat.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan alamat: " + ex.getMessage());
        }
    }//GEN-LAST:event_simpanDataAlamatOption5ActionPerformed

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
            java.util.logging.Logger.getLogger(ProfilPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProfilPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProfilPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProfilPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProfilPage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Alamat;
    private javax.swing.JPanel Header;
    private javax.swing.JPanel MenuProfil;
    private javax.swing.JButton SimpanBtnDefault;
    private javax.swing.JButton alamatBtn;
    private javax.swing.JPanel alamatDefault;
    private javax.swing.JPanel alamatOption1;
    private javax.swing.JPanel alamatOption2;
    private javax.swing.JPanel alamatOption3;
    private javax.swing.JPanel alamatOption4;
    private javax.swing.JPanel alamatOption5;
    private javax.swing.JButton backBtn2;
    private javax.swing.JButton backBtnProfil;
    private javax.swing.JButton csBtn;
    private javax.swing.JButton csKirimbtn;
    private javax.swing.JPanel customerService;
    private javax.swing.JPanel dataAlamat;
    private javax.swing.JTextArea detailAlamatField;
    private javax.swing.JTextArea detailDefaultArea;
    private javax.swing.JTextArea detailOptiontArea1;
    private javax.swing.JTextArea detailOptiontArea2;
    private javax.swing.JTextArea detailOptiontArea3;
    private javax.swing.JTextArea detailOptiontArea4;
    private javax.swing.JTextArea detailOptiontArea5;
    private javax.swing.JButton editBtnDefault;
    private javax.swing.JButton editDataAlamatOption1;
    private javax.swing.JButton editDataAlamatOption2;
    private javax.swing.JButton editDataAlamatOption3;
    private javax.swing.JButton editDataAlamatOption4;
    private javax.swing.JButton editDataAlamatOption5;
    private javax.swing.JTextField emailProfilField;
    private javax.swing.JButton hapusDataAlamatOption1;
    private javax.swing.JButton hapusDataAlamatOption2;
    private javax.swing.JButton hapusDataAlamatOption3;
    private javax.swing.JButton hapusDataAlamatOption4;
    private javax.swing.JButton hapusDataAlamatOption5;
    private javax.swing.JTable historyPesanTabel;
    private javax.swing.JPanel historyPesanan;
    private javax.swing.JButton hsitoryPesanbtn;
    private javax.swing.JPanel isiProfil;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JLabel judulAlmatHal;
    private javax.swing.JTextField kabupatenDefaultField;
    private javax.swing.JTextField kabupatenField;
    private javax.swing.JTextField kabupatenOptionField1;
    private javax.swing.JTextField kabupatenOptionField2;
    private javax.swing.JTextField kabupatenOptionField3;
    private javax.swing.JTextField kabupatenOptionField4;
    private javax.swing.JTextField kabupatenOptionField5;
    private javax.swing.JTextField kecamatanDefaultField;
    private javax.swing.JTextField kecamatanField;
    private javax.swing.JTextField kecamatanOptionField1;
    private javax.swing.JTextField kecamatanOptionField2;
    private javax.swing.JTextField kecamatanOptionField3;
    private javax.swing.JTextField kecamatanOptionField4;
    private javax.swing.JTextField kecamatanOptionField5;
    private javax.swing.JTextField kodePosDefaultField;
    private javax.swing.JTextField kodePosField;
    private javax.swing.JTextField kodePosOptionField1;
    private javax.swing.JTextField kodePosOptionField2;
    private javax.swing.JTextField kodePosOptionField3;
    private javax.swing.JTextField kodePosOptionField4;
    private javax.swing.JTextField kodePosOptionField5;
    private javax.swing.JTextField kotaDefaultField;
    private javax.swing.JTextField kotaField;
    private javax.swing.JTextField kotaOptionField1;
    private javax.swing.JTextField kotaOptionField2;
    private javax.swing.JTextField kotaOptionField3;
    private javax.swing.JTextField kotaOptionField4;
    private javax.swing.JTextField kotaOptionField5;
    private javax.swing.JLabel label;
    private javax.swing.JTextField levelProfilField;
    private javax.swing.JButton logOutBtn;
    private javax.swing.JButton mainDataAlamatOption1;
    private javax.swing.JButton mainDataAlamatOption2;
    private javax.swing.JButton mainDataAlamatOption3;
    private javax.swing.JButton mainDataAlamatOption4;
    private javax.swing.JButton mainDataAlamatOption5;
    private javax.swing.JTextField namaLengkapDefaultField;
    private javax.swing.JTextField namaLengkapField;
    private javax.swing.JTextField namaLengkapOptionField1;
    private javax.swing.JTextField namaLengkapOptionField2;
    private javax.swing.JTextField namaLengkapOptionField3;
    private javax.swing.JTextField namaLengkapOptionField4;
    private javax.swing.JTextField namaLengkapOptionField5;
    private javax.swing.JTextField namaProfilField;
    private javax.swing.JTextField noHpProfilField;
    private javax.swing.JTextField noTelpDefaultField;
    private javax.swing.JTextField noTelpField;
    private javax.swing.JTextField noTelpOptionField1;
    private javax.swing.JTextField noTelpOptionField2;
    private javax.swing.JTextField noTelpOptionField3;
    private javax.swing.JTextField noTelpOptionField4;
    private javax.swing.JTextField noTelpOptionField5;
    private javax.swing.JPanel panelAlamat;
    private javax.swing.JPanel panelDataAlamat;
    private javax.swing.JLabel pointsLabel;
    private javax.swing.JTextField pointsProfilField;
    private javax.swing.JTextField provinsiDefaultField;
    private javax.swing.JTextField provinsiField;
    private javax.swing.JTextField provinsiOptionField1;
    private javax.swing.JTextField provinsiOptionField2;
    private javax.swing.JTextField provinsiOptionField3;
    private javax.swing.JTextField provinsiOptionField4;
    private javax.swing.JTextField provinsiOptionField5;
    private javax.swing.JButton simpanDataAlamatOption1;
    private javax.swing.JButton simpanDataAlamatOption2;
    private javax.swing.JButton simpanDataAlamatOption3;
    private javax.swing.JButton simpanDataAlamatOption4;
    private javax.swing.JButton simpanDataAlamatOption5;
    private javax.swing.JButton tambahAlamatBt;
    private javax.swing.JButton tambahAlamatButton;
    private javax.swing.JTextField tglLahirProfilField;
    private javax.swing.JTextField usernameProfilField;
    private javax.swing.JButton wishBtn;
    private javax.swing.JPanel wishList;
    private javax.swing.JTable wishlistTabel;
    // End of variables declaration//GEN-END:variables
}
