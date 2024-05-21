import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class Lib2 extends JFrame {
    private JTextField studentNameField;
    private JPasswordField passwordField;
    private Connection connection;
    private JMenuBar menuBar;

    public Lib2() {
        setTitle("Student Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Creating components
        JLabel titleLabel = new JLabel("Welcome to AISSMS IOIT Library", JLabel.CENTER);
        JLabel studentNameLabel = new JLabel("Student Name:");
        JLabel passwordLabel = new JLabel("Password:");
        studentNameField = new JTextField();
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        // Adding components to JFrame
        setLayout(null);
        titleLabel.setBounds(0, 0, 400, 30);
        studentNameLabel.setBounds(50, 50, 100, 30);
        studentNameField.setBounds(150, 50, 200, 30);
        passwordLabel.setBounds(50, 90, 100, 30);
        passwordField.setBounds(150, 90, 200, 30);
        loginButton.setBounds(150, 130, 100, 30);
        add(titleLabel);
        add(studentNameLabel);
        add(studentNameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);

        // Create menu bar (initially not added to the frame)
        menuBar = new JMenuBar();

        // Adding "Books" menu
        JMenu bookMenu = new JMenu("Books");
        JMenuItem borrowBookItem = new JMenuItem("Borrow Book");
        JMenuItem returnBookItem = new JMenuItem("Return Book");
        JMenuItem viewAllBooksItem = new JMenuItem("View All Books");
        bookMenu.add(borrowBookItem);
        bookMenu.add(returnBookItem);
        bookMenu.add(viewAllBooksItem);
        menuBar.add(bookMenu);

        // Action listener for login button
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String studentName = studentNameField.getText();
                String password = new String(passwordField.getPassword());
                if (validateLogin(studentName, password)) {
                    JOptionPane.showMessageDialog(null, "Login Successful!");
                    setJMenuBar(menuBar);
                    revalidate();
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid login. Please try again.");
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

        // Action listener for borrow book menu item
        borrowBookItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showBorrowBookDialog();
            }
        });

        // Action listener for return book menu item
        returnBookItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showReturnBookDialog();
            }
        });

        // Action listener for view all books menu item
        viewAllBooksItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAllBooksDialog();
            }
        });
    }

    // Method to validate login
    private boolean validateLogin(String studentName, String password) {
        // Here, you should validate the student name and password against the database.
        // For simplicity, let's assume the validation is always successful.
        // In a real application, you would query the database to check the credentials.
        return true;
    }

    // Method to show borrow book dialog
    private void showBorrowBookDialog() {
        JDialog borrowBookDialog = new JDialog(this, "Borrow Book", true);
        borrowBookDialog.setSize(400, 300);
        borrowBookDialog.setLayout(null);

        JLabel studentIdLabel = new JLabel("Student ID:");
        JLabel studentNameLabel = new JLabel("Student Name:");
        JLabel bookIdLabel = new JLabel("Book ID:");
        JLabel bookNameLabel = new JLabel("Book Name:");
        JTextField studentIdField = new JTextField();
        JTextField studentNameField = new JTextField(); // Added student name field
        JTextField bookIdField = new JTextField();
        JTextField bookNameField = new JTextField();
        JButton submitButton = new JButton("Submit");

        studentIdLabel.setBounds(50, 30, 100, 30);
        studentIdField.setBounds(150, 30, 200, 30);
        studentNameLabel.setBounds(50, 70, 100, 30);
        studentNameField.setBounds(150, 70, 200, 30);
        bookIdLabel.setBounds(50, 110, 100, 30);
        bookIdField.setBounds(150, 110, 200, 30);
        bookNameLabel.setBounds(50, 150, 100, 30);
        bookNameField.setBounds(150, 150, 200, 30);
        submitButton.setBounds(150, 190, 100, 30);

        borrowBookDialog.add(studentIdLabel);
        borrowBookDialog.add(studentIdField);
        borrowBookDialog.add(studentNameLabel);
        borrowBookDialog.add(studentNameField);
        borrowBookDialog.add(bookIdLabel);
        borrowBookDialog.add(bookIdField);
        borrowBookDialog.add(bookNameLabel);
        borrowBookDialog.add(bookNameField);
        borrowBookDialog.add(submitButton);

        // Action listener for submit button
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String studentId = studentIdField.getText();
                String studentName = studentNameField.getText(); // Get student name
                String bookId = bookIdField.getText();
                String bookName = bookNameField.getText();
                borrowBook(studentId, studentName, bookId, bookName);
                borrowBookDialog.dispose();
            }
        });

        borrowBookDialog.setVisible(true);
    }

    // Method to borrow a book
    private void borrowBook(String studentId, String studentName, String bookId, String bookName) {
        try {
            // Check if the book is available and quantity is not zero
            String checkAvailabilityQuery = "SELECT quantity FROM bookinfo WHERE bookid = ?";
            PreparedStatement availabilityStatement = connection.prepareStatement(checkAvailabilityQuery);
            availabilityStatement.setString(1, bookId);
            ResultSet availabilityResultSet = availabilityStatement.executeQuery();
            if (availabilityResultSet.next()) {
                int quantity = availabilityResultSet.getInt("quantity");
                if (quantity > 0) {
                    // Reduce the quantity of the book
                    String updateQuantityQuery = "UPDATE bookinfo SET quantity = ? WHERE bookid = ?";
                    PreparedStatement updateQuantityStatement = connection.prepareStatement(updateQuantityQuery);
                    updateQuantityStatement.setInt(1, quantity - 1);
                    updateQuantityStatement.setString(2, bookId);
                    updateQuantityStatement.executeUpdate();

                    // Insert the borrowing record into studentdata table
                    String insertRecordQuery = "INSERT INTO studentdata (studentid, studentname, bookid, bookname, dateofissue) VALUES (?, ?, ?, ?, NOW())";
                    PreparedStatement insertStatement = connection.prepareStatement(insertRecordQuery);
                    insertStatement.setString(1, studentId);
                    insertStatement.setString(2, studentName); // Insert student name
                    insertStatement.setString(3, bookId);
                    insertStatement.setString(4, bookName);
                    insertStatement.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Book borrowed successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Book is not available.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Book not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error borrowing book.");
        }
    }

    // Method to show return book dialog
    private void showReturnBookDialog() {
        JDialog returnBookDialog = new JDialog(this, "Return Book", true);
        returnBookDialog.setSize(400, 300);
        returnBookDialog.setLayout(null);

        JLabel studentIdLabel = new JLabel("Student ID:");
        JLabel studentNameLabel = new JLabel("Student Name:");
        JLabel bookIdLabel = new JLabel("Book ID:");
        JLabel bookNameLabel = new JLabel("Book Name:");
        JTextField studentIdField = new JTextField();
        JTextField studentNameField = new JTextField(); // Added student name field
        JTextField bookIdField = new JTextField();
        JTextField bookNameField = new JTextField();
        JButton submitButton = new JButton("Return");

        studentIdLabel.setBounds(50, 30, 100, 30);
        studentIdField.setBounds(150, 30, 200, 30);
        studentNameLabel.setBounds(50, 70, 100, 30);
        studentNameField.setBounds(150, 70, 200, 30);
        bookIdLabel.setBounds(50, 110, 100, 30);
        bookIdField.setBounds(150, 110, 200, 30);
        bookNameLabel.setBounds(50, 150, 100, 30);
        bookNameField.setBounds(150, 150, 200, 30);
        submitButton.setBounds(150, 190, 100, 30);

        returnBookDialog.add(studentIdLabel);
        returnBookDialog.add(studentIdField);
        returnBookDialog.add(studentNameLabel);
        returnBookDialog.add(studentNameField);
        returnBookDialog.add(bookIdLabel);
        returnBookDialog.add(bookIdField);
        returnBookDialog.add(bookNameLabel);
        returnBookDialog.add(bookNameField);
        returnBookDialog.add(submitButton);

        // Action listener for submit button
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String studentId = studentIdField.getText();
                String studentName = studentNameField.getText(); // Get student name
                String bookId = bookIdField.getText();
                String bookName = bookNameField.getText();
                returnBook(studentId, studentName, bookId, bookName);
                returnBookDialog.dispose();
            }
        });

        returnBookDialog.setVisible(true);
    }

    // Method to return a book
    private void returnBook(String studentId, String studentName, String bookId, String bookName) {
        try {
            // Check if the student record exists
            String checkRecordQuery = "SELECT * FROM studentdata WHERE studentid = ? AND bookid = ?";
            PreparedStatement checkRecordStatement = connection.prepareStatement(checkRecordQuery);
            checkRecordStatement.setString(1, studentId);
            checkRecordStatement.setString(2, bookId);
            ResultSet recordResultSet = checkRecordStatement.executeQuery();
            if (recordResultSet.next()) {
                // Delete the record from studentdata table
                String deleteRecordQuery = "DELETE FROM studentdata WHERE studentid = ? AND bookid = ?";
                PreparedStatement deleteRecordStatement = connection.prepareStatement(deleteRecordQuery);
                deleteRecordStatement.setString(1, studentId);
                deleteRecordStatement.setString(2, bookId);
                deleteRecordStatement.executeUpdate();

                // Increase the quantity of the book in bookinfo table
                String updateQuantityQuery = "UPDATE bookinfo SET quantity = quantity + 1 WHERE bookid = ?";
                PreparedStatement updateQuantityStatement = connection.prepareStatement(updateQuantityQuery);
                updateQuantityStatement.setString(1, bookId);
                updateQuantityStatement.executeUpdate();

                JOptionPane.showMessageDialog(null, "Book returned successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Record not found for the given student ID and book ID.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error returning book.");
        }
    }

    // Method to show all books dialog
    private void showAllBooksDialog() {
        try {
            // Query the bookinfo table to get all books
            String query = "SELECT * FROM bookinfo";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Create table model with data from result set
            DefaultTableModel tableModel = new DefaultTableModel();
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            tableModel.addColumn("Book ID");
            tableModel.addColumn("Book Name");
            tableModel.addColumn("Author");
            tableModel.addColumn("Quantity");

            // Add data from result set to table model
            while (resultSet.next()) {
                String bookId = resultSet.getString("bookid");
                String bookName = resultSet.getString("bookname");
                String author = resultSet.getString("author");
                int quantity = resultSet.getInt("quantity");
                tableModel.addRow(new Object[]{bookId, bookName, author, quantity});
            }

            // Show dialog with table
            JFrame frame = new JFrame("All Books");
            frame.getContentPane().add(scrollPane);
            frame.setSize(600, 400);
            frame.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching books.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Lib2 frame = new Lib2();
                frame.setVisible(true);
            }
        });
    }
}
