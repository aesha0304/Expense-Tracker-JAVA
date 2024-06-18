package project;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
class User {
    private String username;
    private String password;
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() {
        return username;
    } 
    public String getPassword() {
        return password;
    }
}
class Trip {
    private String tripName;
    private List<User> members;
    private Map<User, Double> expenses;
    public Trip(String tripName) {
        this.tripName = tripName;
        members = new ArrayList<>();
        expenses = new HashMap<>();
    }  
    public String getTripName() {
        return tripName;
    }
    public List<User> getMembers() {
        return members;
    }
    public void addMember(User user) {
        members.add(user);
        expenses.put(user, 0.0);
    }
    public void addExpense(User user, double amount) {
        if (expenses.containsKey(user)) {
            double currentExpense = expenses.get(user);
            expenses.put(user, currentExpense + amount);
        } else {
            System.out.println("User is not a member of this trip.");
        }
    }
    public double getTotalExpense() {
        double total = 0.0;
        for (double expense : expenses.values()) {
            total += expense;
        }
        return total;
    }
    public double getExpensePerMember() {
        return getTotalExpense() / members.size();
    }
}
public class Main extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/trip_expenses";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "4@Komalparag";
    private static Connection connection = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;
    private static User currentUser;
    private JPanel mainPanel;
    private JButton registerButton;
    private JButton loginButton;
    private JButton exitButton;
    private JLabel systemLabel;
    private JLabel titleLabel;
    private JLabel developerLabel;

    public Main() {
        setTitle("\uD83D\uDCB8 Expense Tracker System \uD83D\uDCB8");
        setSize(800, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        Font titleFont = new Font("Arial", Font.BOLD | Font.ITALIC, 28);
        Color titleColor = new Color(0, 0, 0); 
        Color backgroundColor = new Color(135, 206, 250);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));
        panel.setBackground(backgroundColor); 
        systemLabel = new JLabel("Expense Tracker System");
        systemLabel.setHorizontalAlignment(SwingConstants.CENTER);
        systemLabel.setFont(titleFont);
        systemLabel.setForeground(titleColor);
        panel.add(systemLabel);
        Font buttonFont = new Font("Arial", Font.BOLD, 18);
        loginButton = new JButton("Login");
        loginButton.setFont(buttonFont);
        loginButton.setPreferredSize(new Dimension(200, 50)); 
        loginButton.setBackground(Color.LIGHT_GRAY);
        loginButton.setForeground(titleColor);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        panel.add(loginButton);

        registerButton = new JButton("Register");
        registerButton.setFont(buttonFont);
        registerButton.setPreferredSize(new Dimension(200, 50)); 
        registerButton.setBackground(Color.LIGHT_GRAY);
        registerButton.setForeground(titleColor); 
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });
        panel.add(registerButton);

        exitButton = new JButton("Exit");
        exitButton.setFont(buttonFont);
        exitButton.setPreferredSize(new Dimension(200, 50)); 
        exitButton.setBackground(Color.LIGHT_GRAY);
        exitButton.setForeground(titleColor);
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeDatabaseResources();
                System.exit(0);
            }
        });
        panel.add(exitButton);

        add(panel, BorderLayout.CENTER);

        developerLabel = new JLabel("Developed by Aesha,Aakash,Siddhesh");
        developerLabel.setFont(new Font("Arial",Font.ITALIC,20));
        developerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(developerLabel, BorderLayout.SOUTH); 
        
        connectToDatabase();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
    private static void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
            System.exit(1);
        }
    }
    private static void closeDatabaseResources() {
        try {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            System.out.println("Error closing database resources: " + e.getMessage());
        }
    }

    private void register() {
    JFrame registerFrame = new JFrame("Register");
    registerFrame.setSize(800, 600); 
    registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    registerFrame.setLocationRelativeTo(null);
    registerFrame.setLayout(new BorderLayout());
    registerFrame.getContentPane().setBackground(new Color(135, 206, 250)); 
    JPanel registerPanel = new JPanel();
    registerPanel.setLayout(new GridLayout(5, 1)); 
    registerPanel.setBackground(new Color(135, 206, 250)); 
    JLabel headingLabel = new JLabel("Register");
    headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
    headingLabel.setFont(new Font("Verdana", Font.ITALIC, 27));
    registerPanel.add(headingLabel);
    JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    usernamePanel.setBackground(new Color(135, 206, 250)); 
    JLabel usernameLabel = new JLabel("Username:");
    usernameLabel.setFont(new Font("Arial",Font.ITALIC,21));
    JTextField usernameField = new JTextField(15);
    usernameField.setBackground(Color.LIGHT_GRAY);
    usernamePanel.add(usernameLabel);
    usernamePanel.add(usernameField);
    registerPanel.add(usernamePanel);
    JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    passwordPanel.setBackground(new Color(135, 206, 250));
    JLabel passwordLabel = new JLabel("Password:");
    passwordLabel.setFont(new Font("Arial",Font.ITALIC,21));
    JPasswordField passwordField = new JPasswordField(15);
    passwordField.setBackground(Color.LIGHT_GRAY);
    passwordPanel.add(passwordLabel);
    passwordPanel.add(passwordField);
    registerPanel.add(passwordPanel);
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setBackground(new Color(135, 206, 250)); 
    JButton registerButton = new JButton("Register");
    registerButton.setBackground(Color.WHITE);
    registerButton.setPreferredSize(new Dimension(100, 40)); 
    buttonPanel.add(registerButton);
    registerPanel.add(buttonPanel);
    registerFrame.add(registerPanel, BorderLayout.CENTER);
    registerButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            try {
                String query = "INSERT INTO users (username, password) VALUES (?, ?)";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Registration successful!");
                registerFrame.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error registering user: " + ex.getMessage());
            }
        }
    });
    registerFrame.setVisible(true);
}

  private void login() {
    JFrame loginFrame = new JFrame("Login");
    loginFrame.setSize(800, 600); 
    loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    loginFrame.setLocationRelativeTo(null);
    loginFrame.setLayout(new BorderLayout());
    loginFrame.getContentPane().setBackground(new Color(135, 206, 250));
    JPanel loginPanel = new JPanel();
    loginPanel.setLayout(new GridLayout(5, 1)); 
    loginPanel.setBackground(new Color(135, 206, 250)); 
    JLabel headingLabel = new JLabel("Login");
    headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
    headingLabel.setFont(new Font("Verdana", Font.ITALIC, 27));
    loginPanel.add(headingLabel);
    JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    usernamePanel.setBackground(new Color(135, 206, 250));
    JLabel usernameLabel = new JLabel("Username:");
    usernameLabel.setFont(new Font("Arial",Font.ITALIC,21));
    JTextField usernameField = new JTextField(15);
    usernameField.setBackground(Color.LIGHT_GRAY); 
    usernamePanel.add(usernameLabel);
    usernamePanel.add(usernameField);
    loginPanel.add(usernamePanel);
    JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    passwordPanel.setBackground(new Color(135, 206, 250));
    JLabel passwordLabel = new JLabel("Password:");
    passwordLabel.setFont(new Font("Arial",Font.ITALIC,21));
    JPasswordField passwordField = new JPasswordField(15);
    passwordField.setBackground(Color.LIGHT_GRAY); 
    passwordPanel.add(passwordLabel);
    passwordPanel.add(passwordField);
    loginPanel.add(passwordPanel);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setBackground(new Color(135, 206, 250)); 
    JButton loginButton = new JButton("Login");
    loginButton.setBackground(Color.WHITE); 
    loginButton.setPreferredSize(new Dimension(100, 40));
    buttonPanel.add(loginButton);
    loginPanel.add(buttonPanel);

    loginFrame.add(loginPanel, BorderLayout.CENTER);
    loginButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            try {
                String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    currentUser = new User(username, password);
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    loggedInMenu();
                    loginFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error logging in: " + ex.getMessage());
            }
        }
    });
    loginFrame.setVisible(true);
}
private void loggedInMenu() {
    JFrame loggedInFrame = new JFrame("Logged In Menu");
    loggedInFrame.setSize(800, 600); 
    loggedInFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    loggedInFrame.setLocationRelativeTo(null);
    loggedInFrame.setLayout(new BorderLayout());
    loggedInFrame.getContentPane().setBackground(new Color(135, 206, 250));
    JPanel loggedInPanel = new JPanel();
    loggedInPanel.setLayout(new GridLayout(8, 1)); 
    loggedInPanel.setBackground(new Color(135, 206, 250));
    loggedInPanel.add(Box.createVerticalGlue());
    
    JLabel headingLabel = new JLabel("Logged In Menu");
    headingLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
    headingLabel.setAlignmentY(Component.CENTER_ALIGNMENT); 
    headingLabel.setFont(new Font("Verdana", Font.ITALIC, 24));
    headingLabel.setBorder(BorderFactory.createEmptyBorder(0, 300, 50, 0));
    
    loggedInPanel.add(headingLabel);
    
    JButton createTripButton = new JButton("Create Trip");
    JButton addMembersButton = new JButton("Add Members to Trip");
    JButton addExpenseButton = new JButton("Add Expense");
    JButton calculateExpenseButton = new JButton("Calculate Total Expense");
    JButton displayMembersButton = new JButton("Display Members");
    JButton logoutButton = new JButton("Logout");
    createTripButton.setFont(new Font("Arial", Font.BOLD, 16));
    addMembersButton.setFont(new Font("Arial", Font.BOLD, 16));
    addExpenseButton.setFont(new Font("Arial", Font.BOLD, 16));
    calculateExpenseButton.setFont(new Font("Arial", Font.BOLD, 16));
    displayMembersButton.setFont(new Font("Arial", Font.BOLD, 16));
    logoutButton.setFont(new Font("Arial", Font.BOLD, 16));

    Color buttonColor = Color.LIGHT_GRAY;
    createTripButton.setBackground(buttonColor);
    addMembersButton.setBackground(buttonColor);
    addExpenseButton.setBackground(buttonColor);
    calculateExpenseButton.setBackground(buttonColor);
    displayMembersButton.setBackground(buttonColor); 
    logoutButton.setBackground(buttonColor);
    createTripButton.setForeground(Color.BLACK);
    addMembersButton.setForeground(Color.BLACK);
    addExpenseButton.setForeground(Color.BLACK);
    calculateExpenseButton.setForeground(Color.BLACK);
    displayMembersButton.setForeground(Color.BLACK); 
    logoutButton.setForeground(Color.BLACK);

    loggedInPanel.add(createTripButton);
    loggedInPanel.add(addMembersButton);
    loggedInPanel.add(addExpenseButton);
    loggedInPanel.add(calculateExpenseButton);
    loggedInPanel.add(displayMembersButton); 
    loggedInPanel.add(logoutButton);

    loggedInFrame.add(loggedInPanel, BorderLayout.CENTER);

    createTripButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            createTrip();
        }
    });
    addMembersButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            addMembersToTrip();
        }
    });
    addExpenseButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            addExpense();
        }
    });
    calculateExpenseButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            calculateTotalExpense();
        }
    });
    displayMembersButton.addActionListener(new ActionListener() { 
        public void actionPerformed(ActionEvent e) {
            String tripName = JOptionPane.showInputDialog(loggedInFrame, "Enter the trip name:");
            if (tripName != null && !tripName.isEmpty()) {
                displayTripMembers(tripName);
            }
        }
    });
    logoutButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            currentUser = null;
            loggedInFrame.dispose();
        }
    });
    loggedInFrame.setVisible(true);
}
private void displayTripMembers(String tripName) {
    JFrame displayMembersFrame = new JFrame("Trip Members");
    displayMembersFrame.setSize(800, 600); 
    displayMembersFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    displayMembersFrame.setLocationRelativeTo(null);

    JPanel displayMembersPanel = new JPanel(new BorderLayout());
    displayMembersPanel.setBackground(new Color(135, 206, 250));

    JLabel titleLabel = new JLabel("Members of Trip: " + tripName);
    titleLabel.setFont(new Font("Verdana", Font.BOLD, 20));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    displayMembersPanel.add(titleLabel, BorderLayout.NORTH);

    JTextArea membersTextArea = new JTextArea();
    membersTextArea.setEditable(false);
    membersTextArea.setFont(new Font("Arial", Font.PLAIN, 20)); 
    membersTextArea.setBackground(Color.LIGHT_GRAY); 
    membersTextArea.setMargin(new Insets(10, 10, 10, 10)); 

    try {
        int tripId = getTripId(tripName);
        if (tripId != -1) {
            String query = "SELECT username FROM users INNER JOIN trip_members ON users.user_id = trip_members.user_id WHERE trip_id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, tripId);
            resultSet = preparedStatement.executeQuery();

            StringBuilder membersText = new StringBuilder();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                membersText.append("->").append(username).append("\n");
            }
            membersTextArea.setText(membersText.toString()); 
        } else {
            JOptionPane.showMessageDialog(null, "Trip not found.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error retrieving trip members: " + e.getMessage());
    }

    JScrollPane scrollPane = new JScrollPane(membersTextArea);
    displayMembersPanel.add(scrollPane, BorderLayout.CENTER);

    displayMembersFrame.add(displayMembersPanel);
    displayMembersFrame.setVisible(true);
}

private void createTrip() {
    JFrame createTripFrame = new JFrame("Create a Trip");
    createTripFrame.setSize(800, 600); 
    createTripFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    createTripFrame.setLocationRelativeTo(null);
    createTripFrame.setLayout(new BorderLayout());
    createTripFrame.getContentPane().setBackground(new Color(135, 206, 250)); 

    JPanel createTripPanel = new JPanel();
    createTripPanel.setLayout(new GridLayout(3, 1));
    createTripPanel.setBackground(new Color(135, 206, 250)); 

    JLabel headingLabel = new JLabel("Create a Trip");
    headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
    headingLabel.setFont(new Font("Verdana", Font.ITALIC, 27)); 
    createTripPanel.add(headingLabel);

    JPanel tripNamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    tripNamePanel.setBackground(new Color(135, 206, 250)); 
    JLabel tripNameLabel = new JLabel("Trip Name:");
    tripNameLabel.setFont(new Font("Arial",Font.ITALIC,21));
    JTextField tripNameField = new JTextField(20);
    tripNameField.setBackground(Color.LIGHT_GRAY); 
    tripNamePanel.add(tripNameLabel);
    tripNamePanel.add(tripNameField);
    createTripPanel.add(tripNamePanel);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setBackground(new Color(135, 206, 250)); 
    JButton createTripButton = new JButton("Create Trip");
    createTripButton.setBackground(Color.WHITE); 
    createTripButton.setPreferredSize(new Dimension(150, 40));
    buttonPanel.add(createTripButton);
    createTripPanel.add(buttonPanel);

    createTripFrame.add(createTripPanel, BorderLayout.CENTER);

    createTripButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String tripName = tripNameField.getText().trim();
            try {
                String query = "INSERT INTO trips (trip_name) VALUES (?)";
                preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, tripName);
                preparedStatement.executeUpdate();
                ResultSet rs = preparedStatement.getGeneratedKeys();
                int tripId = -1;
                if (rs.next()) {
                    tripId = rs.getInt(1);
                }
                if (tripId != -1) {
                    JOptionPane.showMessageDialog(null, "Trip created successfully.");
                    addMemberToTrip(tripId, currentUser.getUsername());
                    createTripFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to create trip.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error creating trip: " + ex.getMessage());
            }
        }
    });

    createTripFrame.setVisible(true);
}

private void addMembersToTrip() {
    JFrame addMembersFrame = new JFrame("Add Members to Trip");
    addMembersFrame.setSize(800, 600);
    addMembersFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    addMembersFrame.setLocationRelativeTo(null);
    addMembersFrame.setLayout(new BorderLayout());
    addMembersFrame.getContentPane().setBackground(new Color(135, 206, 250)); 

    JPanel addMembersPanel = new JPanel();
    addMembersPanel.setLayout(new GridLayout(4, 1)); 
    addMembersPanel.setBackground(new Color(135, 206, 250)); 

    JLabel headingLabel = new JLabel("Add Members to Trip");
    headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
    headingLabel.setFont(new Font("Verdana", Font.ITALIC, 27)); 
    addMembersPanel.add(headingLabel);

    JPanel inputPanel = new JPanel(new GridLayout(2, 2));
    inputPanel.setBackground(new Color(135, 206, 250)); 
    JLabel tripNameLabel = new JLabel("Trip Name:");
    tripNameLabel.setFont(new Font("Arial",Font.ITALIC,21));
    JTextField tripNameField = new JTextField(15);
    JLabel numMembersLabel = new JLabel("Number of Members:");
    numMembersLabel.setFont(new Font("Arial",Font.ITALIC,21));
    JTextField numMembersField = new JTextField(5);
    tripNameField.setBackground(Color.LIGHT_GRAY); 
    numMembersField.setBackground(Color.LIGHT_GRAY); 
    inputPanel.add(tripNameLabel);
    inputPanel.add(tripNameField);
    inputPanel.add(numMembersLabel);
    inputPanel.add(numMembersField);
    addMembersPanel.add(inputPanel);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setBackground(new Color(135, 206, 250)); 
    JButton addMemberButton = new JButton("Add Members");
    addMemberButton.setBackground(Color.WHITE); 
    addMemberButton.setPreferredSize(new Dimension(150, 40)); 
    buttonPanel.add(addMemberButton);
    addMembersPanel.add(buttonPanel);

    addMembersFrame.add(addMembersPanel, BorderLayout.CENTER);

    addMemberButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String tripName = tripNameField.getText().trim();
            int tripId = getTripId(tripName);
            if (tripId != -1) {
                int numMembers = Integer.parseInt(numMembersField.getText().trim());
                for (int i = 0; i < numMembers; i++) {
                    String memberUsername = JOptionPane.showInputDialog(addMembersFrame, "Enter member " + (i + 1) + " username:");
                    addMemberToTrip(tripId, memberUsername);
                }
                addMembersFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Trip not found.");
            }
        }
    });
    addMembersFrame.setVisible(true);
}
private void addExpense() {
    JFrame addExpenseFrame = new JFrame("Add Expense");
    addExpenseFrame.setSize(800, 600);
    addExpenseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    addExpenseFrame.setLocationRelativeTo(null);
    addExpenseFrame.setLayout(new BorderLayout());
    addExpenseFrame.getContentPane().setBackground(new Color(135, 206, 250));

    JPanel addExpensePanel = new JPanel();
    addExpensePanel.setLayout(new GridLayout(4, 1));
    addExpensePanel.setBackground(new Color(135, 206, 250));

    JLabel headingLabel = new JLabel("Add Expense");
    headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
    headingLabel.setFont(new Font("Verdana", Font.ITALIC, 27)); 
    addExpensePanel.add(headingLabel);

    JPanel tripNamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    tripNamePanel.setBackground(new Color(135, 206, 250)); 
    JLabel tripNameLabel = new JLabel("Trip Name:");
    tripNameLabel.setFont(new Font("Arial",Font.ITALIC,21));
    JTextField tripNameField = new JTextField(20);
    tripNameField.setBackground(Color.LIGHT_GRAY); 
    tripNamePanel.add(tripNameLabel);
    tripNamePanel.add(tripNameField);
    addExpensePanel.add(tripNamePanel);

    JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    amountPanel.setBackground(new Color(135, 206, 250)); 
    JLabel amountLabel = new JLabel("Amount Spent:");
    amountLabel.setFont(new Font("Arial",Font.ITALIC,21));
    JTextField amountField = new JTextField(20);
    amountField.setBackground(Color.LIGHT_GRAY); 
    amountPanel.add(amountLabel);
    amountPanel.add(amountField);
    addExpensePanel.add(amountPanel);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setBackground(new Color(135, 206, 250)); 
    JButton addExpenseButton = new JButton("Add Expense");
    addExpenseButton.setBackground(Color.WHITE);
    addExpenseButton.setPreferredSize(new Dimension(150, 40));
    buttonPanel.add(addExpenseButton);
    addExpensePanel.add(buttonPanel);

    addExpenseFrame.add(addExpensePanel, BorderLayout.CENTER);

    addExpenseButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String tripName = tripNameField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());
            int tripId = getTripId(tripName);

            if (tripId != -1) {
                try {
                    String query = "INSERT INTO expenses (trip_id, amount) VALUES (?, ?)";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, tripId);
                    preparedStatement.setDouble(2, amount);
                    preparedStatement.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Expense added successfully.");
                    addExpenseFrame.dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error adding expense: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Trip not found.");
            }
        }
    });

    addExpenseFrame.setVisible(true);
}
private void calculateTotalExpense() {
    JFrame calculateExpenseFrame = new JFrame("Calculate Total Expense");
    calculateExpenseFrame.setSize(800, 600); // Adjusted window size
    calculateExpenseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    calculateExpenseFrame.setLocationRelativeTo(null);
    calculateExpenseFrame.setLayout(new BorderLayout());
    calculateExpenseFrame.getContentPane().setBackground(new Color(135, 206, 250));

    JPanel calculateExpensePanel = new JPanel();
    calculateExpensePanel.setLayout(new GridLayout(3, 1)); 
    calculateExpensePanel.setBackground(new Color(135, 206, 250));

    JLabel headingLabel = new JLabel("Calculate Total Expense");
    headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
    headingLabel.setFont(new Font("Verdana", Font.ITALIC, 27)); 
    calculateExpensePanel.add(headingLabel);

    JPanel tripNamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    tripNamePanel.setBackground(new Color(135, 206, 250)); 
    JLabel tripNameLabel = new JLabel("Trip Name:");
    tripNameLabel.setFont(new Font("Arial",Font.ITALIC,21));
    JTextField tripNameField = new JTextField(20);
    tripNameField.setBackground(Color.LIGHT_GRAY); 
    tripNamePanel.add(tripNameLabel);
    tripNamePanel.add(tripNameField);
    calculateExpensePanel.add(tripNamePanel);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setBackground(new Color(135, 206, 250)); 
    JButton calculateButton = new JButton("Calculate");
    calculateButton.setBackground(Color.WHITE);
    calculateButton.setPreferredSize(new Dimension(150, 40)); 
    buttonPanel.add(calculateButton);
    calculateExpensePanel.add(buttonPanel);

    calculateExpenseFrame.add(calculateExpensePanel, BorderLayout.CENTER);

    calculateButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String tripName = tripNameField.getText().trim();
            int tripId = getTripId(tripName);
            if (tripId != -1) {
                try {
                    String query = "SELECT COUNT(*) AS num_members FROM trip_members WHERE trip_id = ?";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, tripId);
                    resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        int numMembers = resultSet.getInt("num_members");
                        query = "SELECT SUM(amount) AS total_expense FROM expenses WHERE trip_id = ?";
                        preparedStatement = connection.prepareStatement(query);
                        preparedStatement.setInt(1, tripId);
                        resultSet = preparedStatement.executeQuery();
                        if (resultSet.next()) {
                            double totalExpense = resultSet.getDouble("total_expense");
                            double expensePerMember = totalExpense / numMembers;
                            JOptionPane.showMessageDialog(null, "Total expense for trip '" + tripName + "': $" + totalExpense + "\nExpense per member: $" + expensePerMember);
                            calculateExpenseFrame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "No expenses found for this trip.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No members found for this trip.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error calculating total expense: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Trip not found.");
            }
        }
    });

    calculateExpenseFrame.setVisible(true);
}
    private static void addMemberToTrip(int tripId, String username) {
        try {
            String query = "SELECT user_id FROM users WHERE username = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                query = "INSERT INTO trip_members (trip_id, user_id) VALUES (?, ?)";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, tripId);
                preparedStatement.setInt(2, userId);
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Member added to trip successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Username not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding member to trip: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private static int getTripId(String tripName) {
        try {
            String query = "SELECT trip_id FROM trips WHERE trip_name = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, tripName);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("trip_id");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error getting trip ID: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }

    private static int getUserId(String username) {
        try {
            String query = "SELECT user_id FROM users WHERE username = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error getting user ID: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }
}