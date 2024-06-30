import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminPage extends JFrame {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Practise";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "Password";

    private JTextArea votesTextArea;

    public AdminPage() {
        setTitle("Admin Page");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel viewVotesPanel = new JPanel(new BorderLayout());
        viewVotesPanel.setBackground(new Color(135, 206, 235)); // Sky blue background color

        votesTextArea = new JTextArea();
        votesTextArea.setEditable(false);
        votesTextArea.setFont(new Font("Arial", Font.BOLD, 16)); // Set font size and style
        JScrollPane scrollPane = new JScrollPane(votesTextArea);
        viewVotesPanel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("<html><b style='font-size:16px;color:black;'>Refresh</b></html>");
        viewVotesPanel.add(refreshButton, BorderLayout.SOUTH);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayVotes();
            }
        });

        tabbedPane.addTab("View Votes", viewVotesPanel);

        JPanel registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setBackground(new Color(135, 206, 235)); // Sky blue background color

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel userIdLabel = new JLabel("<html><b style='color:black;'>User ID:</b></html>");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        registerPanel.add(userIdLabel, gbc);

        JTextField userIdField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        registerPanel.add(userIdField, gbc);

        JLabel passwordLabel = new JLabel("<html><b style='color:black;'>Password:</b></html>");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        registerPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        registerPanel.add(passwordField, gbc);

        JLabel sectionLabel = new JLabel("<html><b style='color:black;'>Section:</b></html>");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        registerPanel.add(sectionLabel, gbc);

        JTextField sectionField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        registerPanel.add(sectionField, gbc);

        JButton registerButton = new JButton("<html><b style='color:black;'>Register</b></html>");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        registerPanel.add(registerButton, gbc);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = userIdField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String section = sectionField.getText().trim();

                if (!userId.isEmpty() && !password.isEmpty() && !section.isEmpty()) {
                    if (registerVoter(userId, password, section)) {
                        JOptionPane.showMessageDialog(AdminPage.this, "Voter registered successfully!");
                    } else {
                        JOptionPane.showMessageDialog(AdminPage.this, "Failed to register voter.");
                    }
                } else {
                    JOptionPane.showMessageDialog(AdminPage.this, "Please enter User ID, Password, and Section.");
                }
            }
        });

        tabbedPane.addTab("Register Voter", registerPanel);

        // President Voting button
        JButton presidentButton = new JButton("<html><b style='font-size:16px;color:black;'>President Voting</b></html>");
        presidentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Replace with your logic to open President File or perform actions
                JOptionPane.showMessageDialog(AdminPage.this, "Opening President File...");
                // Example: new PresidentFile().setVisible(true);
                new president(); // Assuming 'president' is a class to handle president voting
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(135, 206, 235)); // Sky blue background color
        bottomPanel.add(presidentButton);

        // Add bottom panel with President Voting button
        add(tabbedPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add a button to go back to the login page
        JButton backButton = new JButton("<html><b style='font-size:12px;color:black;'>Back to Login</b></html>");
        backButton.setBounds(getWidth() - 160, 10, 130, 30); // Set the button's position
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new loginpage().setVisible(true);
                dispose(); // Close the admin page
            }
        });
        add(backButton, BorderLayout.NORTH);

        setVisible(true);

        // Create the table if it doesn't exist
        createNewTable();
    }

    private void createNewTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS new_data (usn VARCHAR(50), name VARCHAR(50));";
            stmt.executeUpdate(createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayVotes() {
        votesTextArea.setText(""); // Clear existing text
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Query for ASEC candidates
            String asecQuery = "SELECT usn, name FROM aseccandidates ORDER BY votingcount DESC LIMIT 2";
            appendToVotesTextArea("Top ASEC Candidates:\n");
            writeCandidatesToNewTable(conn, asecQuery);

            // Query for BSEC candidates
            String bsecQuery = "SELECT usn, name FROM bseccandidate ORDER BY votingcount DESC LIMIT 2";
            appendToVotesTextArea("\nTop BSEC Candidates:\n");
            writeCandidatesToNewTable(conn, bsecQuery);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void writeCandidatesToNewTable(Connection conn, String query) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String candidateId = rs.getString("usn");
                String name = rs.getString("name");

                // Insert into new_data table
                String insertStatement = "INSERT INTO new_data (usn, name) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertStatement)) {
                    pstmt.setString(1, candidateId);
                    pstmt.setString(2, name);
                    pstmt.executeUpdate();
                }

                // Append to text area
                appendToVotesTextArea("Candidate ID: " + candidateId + ", Name: " + name + "\n");
            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(AdminPage.this, "Candidates data inserted into table: new_data");
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void appendToVotesTextArea(String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                votesTextArea.append(text);
            }
        });
    }

    private boolean registerVoter(String userId, String password, String section) {
        boolean isRegistered = false;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO ise (usn, name, section, voted) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, userId);
                pstmt.setString(2, password);
                pstmt.setString(3, section);
                pstmt.setString(4, "no");  // Assuming 'voted' should default to 'no'
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    isRegistered = true;
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return isRegistered;
    }

    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        new AdminPage();
    }
}
