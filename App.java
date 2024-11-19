import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class App {

    // Database Connection
    private static final String URL = "jdbc:mysql://localhost:3306/inventory_db";
    private static final String USER = "root";
    private static final String PASSWORD = "mbk@2005";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Inventory Management Operations
    public static class InventoryManagementDB {
        public void addProduct(String productID, String name, int quantity, double price) {
            String sql = "INSERT INTO products(product_id, name, quantity, price) VALUES(?, ?, ?, ?)";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, productID);
                stmt.setString(2, name);
                stmt.setInt(3, quantity);
                stmt.setDouble(4, price);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Added product: " + name);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public String viewInventory() {
            StringBuilder inventoryData = new StringBuilder();
            String sql = "SELECT * FROM products";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    inventoryData.append(rs.getString("product_id"))
                                 .append(": ")
                                 .append(rs.getString("name"))
                                 .append(", Quantity: ")
                                 .append(rs.getInt("quantity"))
                                 .append(", Price: ")
                                 .append(rs.getDouble("price"))
                                 .append("\n");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return inventoryData.toString();
        }

        public void updateStock(String productId, int quantity) {
            String sql = "UPDATE products SET quantity = quantity + ? WHERE product_id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, quantity);
                stmt.setString(2, productId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Updated stock for product: " + productId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void removeProduct(String productId) {
            String sql = "DELETE FROM products WHERE product_id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, productId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Removed product: " + productId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public String searchProduct(String name) {
            StringBuilder productData = new StringBuilder();
            String sql = "SELECT * FROM products WHERE name = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    productData.append(rs.getString("product_id"))
                               .append(": ")
                               .append(rs.getString("name"))
                               .append(", Quantity: ")
                               .append(rs.getInt("quantity"))
                               .append(", Price: ")
                               .append(rs.getDouble("price"));
                } else {
                    productData.append("Product not found.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return productData.toString();
        }
    }

    // Login GUI Frame
    public static class LoginFrame extends JFrame {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JButton loginButton;

        public LoginFrame() {
            setTitle("Login");
            setLayout(new GridLayout(3, 2));

            add(new JLabel("Username:"));
            usernameField = new JTextField();
            add(usernameField);

            add(new JLabel("Password:"));
            passwordField = new JPasswordField();
            add(passwordField);

            loginButton = new JButton("Login");
            add(loginButton);

            loginButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (usernameField.getText().equals("admin") && new String(passwordField.getPassword()).equals("password")) {
                        dispose();
                        new InventoryFrame();
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid login credentials");
                    }
                }
            });

            setSize(300, 150);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    // Inventory Management GUI Frame
    public static class InventoryFrame extends JFrame {
        private InventoryManagementDB db = new InventoryManagementDB();

        public InventoryFrame() {
            setTitle("Inventory Management");
            setLayout(new GridLayout(6, 2, 10, 10));

            // Fields for CRUD operations
            JTextField productIdField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField quantityField = new JTextField();
            JTextField priceField = new JTextField();

            JButton addButton = new JButton("Add Product");
            JButton viewButton = new JButton("View Inventory");
            JButton updateButton = new JButton("Update Stock");
            JButton removeButton = new JButton("Remove Product");
            JButton searchButton = new JButton("Search Product");

            // Adding components to the frame
            add(new JLabel("Product ID:"));
            add(productIdField);
            add(new JLabel("Name:"));
            add(nameField);
            add(new JLabel("Quantity:"));
            add(quantityField);
            add(new JLabel("Price:"));
            add(priceField);

            add(addButton);
            add(viewButton);
            add(updateButton);
            add(removeButton);
            add(searchButton);

            // Action Listeners
            addButton.addActionListener(e -> {
                String productId = productIdField.getText();
                String name = nameField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());
                db.addProduct(productId, name, quantity, price);
            });

            viewButton.addActionListener(e -> {
                String inventory = db.viewInventory();
                JOptionPane.showMessageDialog(null, inventory, "Inventory", JOptionPane.INFORMATION_MESSAGE);
            });

            updateButton.addActionListener(e -> {
                String productId = productIdField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                db.updateStock(productId, quantity);
            });

            removeButton.addActionListener(e -> {
                String productId = productIdField.getText();
                db.removeProduct(productId);
            });

            searchButton.addActionListener(e -> {
                String name = nameField.getText();
                String productInfo = db.searchProduct(name);
                JOptionPane.showMessageDialog(null, productInfo, "Product Search", JOptionPane.INFORMATION_MESSAGE);
            });

            setSize(1440, 1024);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
