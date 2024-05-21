import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Lib1 extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Connection connection;

    public Lib1() {
        setTitle("AISSMS IOIT LIBRARY Admin Page"); // Set the title for the JFrame
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Creating components
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        // Adding components to JFrame
        setLayout(null);
        usernameLabel.setBounds(50, 50, 100, 30);
        usernameField.setBounds(160, 50, 200, 30);
        passwordLabel.setBounds(50, 100, 100, 30);
        passwordField.setBounds(160, 100, 200, 30);
        loginButton.setBounds(250, 150, 100, 30);
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);

        // Action listener for login button
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                boolean isValid = validateUser(username, password);
                if (isValid) {
                    JOptionPane.showMessageDialog(null, "Login Successful!");
                    createMenuBar();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.");
                }
            }
        });

        // Establish database connection
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library1", "root", "SQL@Ketan2354");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to database.");
        }
    }

    // Method to validate user credentials against the database
    private boolean validateUser(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Returns true if there is at least one matching record
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error validating user.");
            return false;
        }
    }

    // Method to create the menu bar
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Adding "Books" menu
        JMenu bookMenu = new JMenu("Books");
        JMenuItem addBookItem = new JMenuItem("Add Book");
        JMenuItem removeBookItem = new JMenuItem("Remove Book");
        bookMenu.add(addBookItem);
        bookMenu.add(removeBookItem);
        menuBar.add(bookMenu);

        // Adding "Catalogue" menu
        JMenu catalogueMenu = new JMenu("Catalogue");
        JMenuItem viewCatalogueItem = new JMenuItem("View Catalogue");
        catalogueMenu.add(viewCatalogueItem);
        menuBar.add(catalogueMenu);

        // Adding "Student Details" menu
        JMenu studentDetailsMenu = new JMenu("Student Details");
        JMenuItem viewStudentDataItem = new JMenuItem("View Student Data");
        studentDetailsMenu.add(viewStudentDataItem);
        menuBar.add(studentDetailsMenu);

        setJMenuBar(menuBar);

        // Action listeners for menu items
        addBookItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddBookDialog();
            }
        });

        removeBookItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRemoveBookDialog();
            }
        });

        viewCatalogueItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCatalogue();
            }
        });

        viewStudentDataItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showStudentData();
            }
        });

        // Refresh the frame to display the menu bar
        revalidate();
        repaint();
    }

    // Method to show Add Book dialog
    private void showAddBookDialog() {
        JDialog addBookDialog = new JDialog(this, "Add Book", true);
        addBookDialog.setSize(400, 300);
        addBookDialog.setLayout(null);

        JLabel bookIdLabel = new JLabel("Book ID:");
        JLabel bookNameLabel = new JLabel("Book Name:");
        JLabel authorLabel = new JLabel("Author:");
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField bookIdField = new JTextField();
        JTextField bookNameField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField quantityField = new JTextField();
        JButton addButton = new JButton("Add Book");

        bookIdLabel.setBounds(50, 30, 100, 30);
        bookIdField.setBounds(150, 30, 200, 30);
        bookNameLabel.setBounds(50, 70, 100, 30);
        bookNameField.setBounds(150, 70, 200, 30);
        authorLabel.setBounds(50, 110, 100, 30);
        authorField.setBounds(150, 110, 200, 30);
        quantityLabel.setBounds(50, 150, 100, 30);
        quantityField.setBounds(150, 150, 200, 30);
        addButton.setBounds(150, 200, 100, 30);

        addBookDialog.add(bookIdLabel);
        addBookDialog.add(bookIdField);
        addBookDialog.add(bookNameLabel);
        addBookDialog.add(bookNameField);
        addBookDialog.add(authorLabel);
        addBookDialog.add(authorField);
        addBookDialog.add(quantityLabel);
        addBookDialog.add(quantityField);
        addBookDialog.add(addButton);

        // Action listener for add button
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bookId = bookIdField.getText();
                String bookName = bookNameField.getText();
                String author = authorField.getText();
                String quantity = quantityField.getText();
                addOrUpdateBookInDatabase(bookId, bookName, author, quantity);
                addBookDialog.dispose();
            }
        });

        addBookDialog.setVisible(true);
    }

    // Method to add or update book in the database
    private void addOrUpdateBookInDatabase(String bookId, String bookName, String author, String quantity) {
        try {
            String checkQuery = "SELECT * FROM bookinfo WHERE bookid = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setString(1, bookId);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                // Book exists, update quantity
                String updateQuery = "UPDATE bookinfo SET quantity = quantity + ? WHERE bookid = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setInt(1, Integer.parseInt(quantity));
                updateStatement.setString(2, bookId);
                updateStatement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Book quantity updated successfully!");
            } else {
                // Book does not exist, insert new book
                String insertQuery = "INSERT INTO bookinfo (bookid, bookname, author, quantity) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setString(1, bookId);
                insertStatement.setString(2, bookName);
                insertStatement.setString(3, author);
                insertStatement.setInt(4, Integer.parseInt(quantity));
                insertStatement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Book added successfully!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding or updating book.");
        }
    }

    // Method to show Remove Book dialog
    private void showRemoveBookDialog() {
        JDialog removeBookDialog = new JDialog(this, "Remove Book", true);
        removeBookDialog.setSize(400, 200);
        removeBookDialog.setLayout(null);

        JLabel bookIdLabel = new JLabel("Book ID:");
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField bookIdField = new JTextField();
        JTextField removeQuantityField = new JTextField();
        JButton removeButton = new JButton("Remove Book");

        bookIdLabel.setBounds(50, 30, 100, 30);
        bookIdField.setBounds(150, 30, 200, 30);
        quantityLabel.setBounds(50, 70, 100, 30);
        removeQuantityField.setBounds(150, 70, 200, 30);
        removeButton.setBounds(150, 110, 150, 30);

        removeBookDialog.add(bookIdLabel);
        removeBookDialog.add(bookIdField);
        removeBookDialog.add(quantityLabel);
        removeBookDialog.add(removeQuantityField);
        removeBookDialog.add(removeButton);

        // Action listener for remove button
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bookId = bookIdField.getText();
                String quantity = removeQuantityField.getText();
                removeBookFromDatabase(bookId, quantity);
                removeBookDialog.dispose();
            }
        });

        removeBookDialog.setVisible(true);
    }

    // Method to remove book quantity from the database
    private void removeBookFromDatabase(String bookId, String quantity) {
        try {
            String checkQuery = "SELECT * FROM bookinfo WHERE bookid = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setString(1, bookId);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                int currentQuantity = resultSet.getInt("quantity");
                int quantityToRemove = Integer.parseInt(quantity);

                if (quantityToRemove > currentQuantity) {
                    JOptionPane.showMessageDialog(this, "Not enough books in stock to remove the requested quantity.");
                } else {
                    String updateQuery = "UPDATE bookinfo SET quantity = quantity - ? WHERE bookid = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setInt(1, quantityToRemove);
                    updateStatement.setString(2, bookId);
                    updateStatement.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Book quantity updated successfully!");

                    if (currentQuantity - quantityToRemove == 0) {
                        String deleteQuery = "DELETE FROM bookinfo WHERE bookid = ?";
                        PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                        deleteStatement.setString(1, bookId);
                        deleteStatement.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Book removed from inventory as quantity reached zero.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Book ID not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error removing book.");
        }
    }

    // Method to show the book catalogue
    private void showCatalogue() {
        JFrame catalogueFrame = new JFrame("Book Catalogue");
        catalogueFrame.setSize(600, 400);

        String[] columnNames = { "Book ID", "Book Name", "Author", "Quantity" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        try {
            String query = "SELECT * FROM bookinfo";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String bookId = resultSet.getString("bookid");
                String bookName = resultSet.getString("bookname");
                String author = resultSet.getString("author");
                int quantity = resultSet.getInt("quantity");
                model.addRow(new Object[] { bookId, bookName, author, quantity });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching catalogue.");
        }

        JScrollPane scrollPane = new JScrollPane(table);
        catalogueFrame.add(scrollPane);
        catalogueFrame.setVisible(true);
    }

    // Method to show the student data
    private void showStudentData() {
        JFrame studentDataFrame = new JFrame("Student Data");
        studentDataFrame.setSize(600, 400);

        String[] columnNames = { "Student ID", "Student Name", "Book ID", "Book Name", "Date of Issue" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        try {
            String query = "SELECT * FROM studentdata";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String studentId = resultSet.getString("studentid");
                String studentName = resultSet.getString("studentname");
                String bookId = resultSet.getString("bookid");
                String bookName = resultSet.getString("bookname");
                String dateOfIssue = resultSet.getString("dateofissue");
                model.addRow(new Object[] { studentId, studentName, bookId, bookName, dateOfIssue });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching student data.");
        }

        JScrollPane scrollPane = new JScrollPane(table);
        studentDataFrame.add(scrollPane);
        studentDataFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Lib1 frame = new Lib1();
                frame.setVisible(true);
            }
        });
    }
}
