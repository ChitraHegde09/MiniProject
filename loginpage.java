import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class loginpage extends JFrame {
    private static final String ADMIN_USER_ID = "admin";
    private static final String ADMIN_PASSWORD = "adminpass";

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Practise";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "Password"; // Change to your PostgreSQL password

    public loginpage() {
        setTitle("College Voting System Login");
        setSize(600, 400); // Increased size for better visibility
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load the background image
        ImageIcon backgroundImage = new ImageIcon("d:\\Downloads\\nie.jpg");

        // Create a panel with a background image
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Create a title label with a white background
        JLabel titleLabel = new JLabel("<html><h1 style='font-size:36px;color:black;'>College Voting System</h1></html>");
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding inside the border
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 20, 0);
        panel.add(titleLabel, gbc);

        JButton adminButton = new JButton("<html><b style='font-size:18px;color:black;'>Admin</b></html>");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(adminButton, gbc);

        JButton voterButton = new JButton("<html><b style='font-size:18px;color:black;'>Voter</b></html>");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(voterButton, gbc);

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginDialog("Admin Login", true);
            }
        });

        voterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginDialog("Voter Login", false);
            }
        });

        add(panel);
        setVisible(true);
    }

    private void showLoginDialog(String title, boolean isAdmin) {
        JDialog loginDialog = new JDialog(this, title, true);
        loginDialog.setSize(400, 300);

        // Load the background image for the dialog
        ImageIcon dialogBackgroundImage = new ImageIcon("d:\\Downloads\\nie3.jpg");

        // Create a panel with a background image for the dialog
        JPanel dialogPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(dialogBackgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        dialogPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel userIdLabel = new JLabel("<html><b style='color:black;'>User ID:</b></html>");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        dialogPanel.add(userIdLabel, gbc);

        JTextField userIdField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        dialogPanel.add(userIdField, gbc);

        JLabel passwordLabel = new JLabel("<html><b style='color:black;'>Password:</b></html>");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        dialogPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        dialogPanel.add(passwordField, gbc);

        JLabel sectionLabel = null;
        JComboBox<String> sectionComboBox = null;

        if (!isAdmin) {
            sectionLabel = new JLabel("<html><b style='color:black;'>Section:</b></html>");
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.EAST;
            dialogPanel.add(sectionLabel, gbc);

            sectionComboBox = new JComboBox<>(new String[]{"Section A", "Section B"});
            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.WEST;
            dialogPanel.add(sectionComboBox, gbc);
        }

        JButton loginButton = new JButton("<html><b style='color:black;'>Login</b></html>");
        gbc.gridx = 1;
        gbc.gridy = isAdmin ? 2 : 3;
        gbc.anchor = GridBagConstraints.CENTER;
        dialogPanel.add(loginButton, gbc);

        JComboBox<String> finalSectionComboBox = sectionComboBox;
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = userIdField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String selectedSection = isAdmin ? null : (String) finalSectionComboBox.getSelectedItem();

                if (isAdmin) {
                    if (userId.equals(ADMIN_USER_ID) && password.equals(ADMIN_PASSWORD)) {
                        JOptionPane.showMessageDialog(loginDialog, "Admin login successful!");
                        AdminPage admin = new AdminPage();
                        admin.setVisible(true);
                        loginDialog.dispose();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(loginDialog, "Invalid admin credentials.");
                    }
                } else {
                    if (validateVoterCredentials(userId, password)) {
                        JOptionPane.showMessageDialog(loginDialog, "Voter login successful!");
                        if ("Section A".equals(selectedSection)) {
                            VoterPage voterA = new VoterPage();
                            voterA.setVisible(true);
                        } else {
                            VPB voterB = new VPB();
                            voterB.setVisible(true);
                        }
                        loginDialog.dispose();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(loginDialog, "Invalid voter credentials.");
                    }
                }
            }
        });

        loginDialog.add(dialogPanel);
        loginDialog.setLocationRelativeTo(this);
        loginDialog.setVisible(true);
    }

    private boolean validateVoterCredentials(String username, String password) {
        boolean isValid = false;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM ise WHERE usn = ? AND name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Database returned a match for user: " + username);
                        isValid = true;
                    } else {
                        System.out.println("No match found for user: " + username);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isValid;
    }

    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        new loginpage();
    }
}
