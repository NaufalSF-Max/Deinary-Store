import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Registrasi extends javax.swing.JFrame {

    public Registrasi() {
        initComponents();
        
        registerButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String name = nameField.getText();
                String username = usernameField.getText();
                String email = emailField.getText();
                String password = String.valueOf(passwordField.getPassword()); // Mengonversi char[] ke String
                String phoneNumber = phoneField.getText();
                String address = addressField.getText();
                String birthDate = birthDateField.getText();

                // Validasi input
                if (!name.isEmpty() && !username.isEmpty() && !email.isEmpty() &&
                    !password.isEmpty() && !phoneNumber.isEmpty() && !birthDate.isEmpty()) {
                    registerUser(name, username, email, password, phoneNumber, address, birthDate);
                } else {
                    JOptionPane.showMessageDialog(null, "Please fill in all required fields.");
                }
            }
        });
        
    }

    private void registerUser(String name, String username, String email, String password, 
                              String phoneNumber, String address, String birthDate) {
        String sql = "INSERT INTO users (name, username, email, password, phone_number, address, birth_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, username);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setString(5, phoneNumber);
            pstmt.setString(6, address);
            pstmt.setString(7, birthDate);
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful!");
            Login loginPage = new Login(); // Membuat instance halaman login
            loginPage.setVisible(true);    // Tampilkan halaman login
            this.dispose();                // Tutup halaman registrasi
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        nameField = new javax.swing.JTextField();
        usernameField = new javax.swing.JTextField();
        emailField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        phoneField = new javax.swing.JTextField();
        addressField = new javax.swing.JTextField();
        birthDateField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        registerButton = new javax.swing.JButton();
        passwordField = new javax.swing.JPasswordField();
        backButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(75, 119, 141));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/deinarystore/img/Artboard 1_1.png"))); // NOI18N

        jLabel8.setBackground(new java.awt.Color(61, 73, 43));
        jLabel8.setFont(new java.awt.Font("Poppins ExtraBold", 0, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(61, 73, 43));
        jLabel8.setText("LET'S GET STARTED NOW!");

        jLabel10.setFont(new java.awt.Font("Poppins ExtraBold", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(61, 73, 43));
        jLabel10.setText("DEINARY STORE");

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/deinarystore/img/icon.png"))); // NOI18N
        jLabel11.setText("jLabel11");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)))
                        .addGap(0, 61, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addGap(64, 64, 64)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        nameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameFieldActionPerformed(evt);
            }
        });
        jPanel3.add(nameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 58, 247, -1));

        usernameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameFieldActionPerformed(evt);
            }
        });
        jPanel3.add(usernameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 108, 247, -1));

        emailField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailFieldActionPerformed(evt);
            }
        });
        jPanel3.add(emailField, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 164, 247, -1));

        jLabel1.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel1.setText("Nama");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 36, 50, -1));

        jLabel2.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel2.setText("Username");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 86, 65, -1));

        jLabel3.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel3.setText("Email");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 142, 68, -1));

        phoneField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phoneFieldActionPerformed(evt);
            }
        });
        jPanel3.add(phoneField, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 276, 247, -1));

        addressField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addressFieldActionPerformed(evt);
            }
        });
        jPanel3.add(addressField, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 326, 247, -1));

        birthDateField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                birthDateFieldActionPerformed(evt);
            }
        });
        jPanel3.add(birthDateField, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 376, 247, -1));

        jLabel4.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel4.setText("Password");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 198, 65, -1));

        jLabel5.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel5.setText("No. Telp");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 254, 72, -1));

        jLabel6.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel6.setText("Alamat");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 304, 60, -1));

        jLabel7.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel7.setText("Tanggal Lahir");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 354, -1, -1));

        registerButton.setBackground(new java.awt.Color(61, 73, 43));
        registerButton.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        registerButton.setForeground(new java.awt.Color(255, 255, 255));
        registerButton.setText("Daftar");
        registerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerButtonActionPerformed(evt);
            }
        });
        jPanel3.add(registerButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 420, -1, -1));

        passwordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordFieldActionPerformed(evt);
            }
        });
        jPanel3.add(passwordField, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 220, 250, -1));

        backButton.setBackground(new java.awt.Color(61, 73, 43));
        backButton.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        backButton.setForeground(new java.awt.Color(255, 255, 255));
        backButton.setText("Kembali");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        jPanel3.add(backButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 420, -1, -1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

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
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void nameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameFieldActionPerformed
      
    }//GEN-LAST:event_nameFieldActionPerformed

    private void emailFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailFieldActionPerformed

    }//GEN-LAST:event_emailFieldActionPerformed

    private void phoneFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phoneFieldActionPerformed

    }//GEN-LAST:event_phoneFieldActionPerformed

    private void addressFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addressFieldActionPerformed

    }//GEN-LAST:event_addressFieldActionPerformed

    private void birthDateFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_birthDateFieldActionPerformed

    }//GEN-LAST:event_birthDateFieldActionPerformed

    private void usernameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameFieldActionPerformed

    }//GEN-LAST:event_usernameFieldActionPerformed

    private void passwordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordFieldActionPerformed
        
    }//GEN-LAST:event_passwordFieldActionPerformed

    private void registerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_registerButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        Login loginPage = new Login(); // Membuat instance halaman Login
        loginPage.setVisible(true);    // Tampilkan halaman Login
        this.dispose();                // Tutup halaman Registrasi
    }//GEN-LAST:event_backButtonActionPerformed
    
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
            java.util.logging.Logger.getLogger(Registrasi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Registrasi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Registrasi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Registrasi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Registrasi().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField addressField;
    private javax.swing.JButton backButton;
    private javax.swing.JTextField birthDateField;
    private javax.swing.JTextField emailField;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField nameField;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JTextField phoneField;
    private javax.swing.JButton registerButton;
    private javax.swing.JTextField usernameField;
    // End of variables declaration//GEN-END:variables
}
