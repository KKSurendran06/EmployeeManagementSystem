import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.text.*;

@SuppressWarnings("unused")
abstract class Employee 
{
    protected String name;
    protected String dob;
    protected String email;
    protected String position;
    protected String employeeId;
    protected String employeeSalary;
    protected String employeeContact;

    public abstract JPanel displayEmployeeDetails();
    public abstract void getEmployeeInfoFromForm(JPanel panel);
}

class ConcreteEmployee extends Employee 
{
    @Override
    public JPanel displayEmployeeDetails() 
    {
        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(createLabel("Employee ID:"));
        panel.add(createLabel(employeeId));
        panel.add(createLabel("Name:"));
        panel.add(createLabel(name));
        panel.add(createLabel("DOB:"));
        panel.add(createLabel(dob));
        panel.add(createLabel("Email:"));
        panel.add(createLabel(email));
        panel.add(createLabel("Position:"));
        panel.add(createLabel(position));
        panel.add(createLabel("Contact:"));
        panel.add(createLabel(employeeContact));
        panel.add(createLabel("Salary:"));
        panel.add(createLabel(employeeSalary));
        return panel;
    }

    @Override
    public void getEmployeeInfoFromForm(JPanel panel) 
    {
        JTextField nameField = (JTextField) panel.getComponent(3);
        JSpinner dobField = (JSpinner) panel.getComponent(5);
        JTextField emailField = (JTextField) panel.getComponent(7);
        JTextField positionField = (JTextField) panel.getComponent(9);
        JTextField contactField = (JTextField) panel.getComponent(11);
        JTextField salaryField = (JTextField) panel.getComponent(13);
        JTextField idField = (JTextField) panel.getComponent(1);

        name = nameField.getText();
        dob = new SimpleDateFormat("yyyy-MM-dd").format(dobField.getValue());
        email = emailField.getText();
        position = positionField.getText();
        employeeContact = contactField.getText();
        employeeSalary = salaryField.getText();
        employeeId = idField.getText();
    }

    private JLabel createLabel(String text) 
    {
        JLabel label = new JLabel(text);
        label.setForeground(Color.CYAN);
        return label;
    }
}

class EmployeeDataHandler 
{
    private final String dbUrl = "jdbc:mysql://localhost:3306/EmployeeDB";
    private final String dbUser = "root1234";
    private final String dbPassword = "root1234"; 

    public EmployeeDataHandler() 
    {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) 
        {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS employees (" +
                    "employeeId VARCHAR(20) PRIMARY KEY, " +
                    "name VARCHAR(50), " +
                    "dob DATE, " +
                    "email VARCHAR(50), " +
                    "position VARCHAR(50), " +
                    "employeeContact VARCHAR(20), " +
                    "employeeSalary VARCHAR(20))";
            try (Statement stmt = connection.createStatement()) 
            {
                stmt.execute(createTableSQL);
            }
        } 
        
        catch (SQLException e) 
        {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
        }
    }

    public void addEmployee(ConcreteEmployee emp) 
    {
        String insertSQL = "INSERT INTO employees (employeeId, name, dob, email, position, employeeContact, employeeSalary) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = connection.prepareStatement(insertSQL)) 
            {
            pstmt.setString(1, emp.employeeId);
            pstmt.setString(2, emp.name);
            pstmt.setDate(3, Date.valueOf(emp.dob));
            pstmt.setString(4, emp.email);
            pstmt.setString(5, emp.position);
            pstmt.setString(6, emp.employeeContact);
            pstmt.setString(7, emp.employeeSalary);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Employee added successfully!");
        } 
        
        catch (SQLException e) 
        {
            JOptionPane.showMessageDialog(null, "Error adding employee: " + e.getMessage());
        }
    }

    public void viewEmployee(String employeeId, JTextArea displayArea) 
    {
        String selectSQL = "SELECT * FROM employees WHERE employeeId = ?";
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = connection.prepareStatement(selectSQL)) 
            {
            pstmt.setString(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) 
            {
                if (rs.next()) 
                {
                    displayArea.setText("");
                    displayArea.append("Employee ID: " + rs.getString("employeeId") + "\n");
                    displayArea.append("Name: " + rs.getString("name") + "\n");
                    displayArea.append("DOB: " + rs.getDate("dob") + "\n");
                    displayArea.append("Email: " + rs.getString("email") + "\n");
                    displayArea.append("Position: " + rs.getString("position") + "\n");
                    displayArea.append("Contact: " + rs.getString("employeeContact") + "\n");
                    displayArea.append("Salary: " + rs.getString("employeeSalary") + "\n");
                }
                
                else 
                {
                    JOptionPane.showMessageDialog(null, "Employee does not exist!");
                }
            }
        } 
        
        catch (SQLException e) 
        {
            JOptionPane.showMessageDialog(null, "Error viewing employee: " + e.getMessage());
        }
    }

    public void removeEmployee(String employeeId) 
    {
        String deleteSQL = "DELETE FROM employees WHERE employeeId = ?";
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, employeeId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) 
            {
                JOptionPane.showMessageDialog(null, "Employee removed successfully!");
            }
            
            else 
            {
                JOptionPane.showMessageDialog(null, "Employee does not exist!");
            }
        } 
        
        catch (SQLException e) 
        {
            
            JOptionPane.showMessageDialog(null, "Error removing employee: " + e.getMessage());
        }
    }

    public void updateEmployee(String employeeId, JPanel employeePanel) 
    {
        ConcreteEmployee emp = new ConcreteEmployee();
        emp.getEmployeeInfoFromForm(employeePanel);
        String updateSQL = "UPDATE employees SET name = ?, dob = ?, email = ?, position = ?, employeeContact = ?, employeeSalary = ? WHERE employeeId = ?";
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setString(1, emp.name);
            pstmt.setDate(2, Date.valueOf(emp.dob));
            pstmt.setString(3, emp.email);
            pstmt.setString(4, emp.position);
            pstmt.setString(5, emp.employeeContact);
            pstmt.setString(6, emp.employeeSalary);
            pstmt.setString(7, emp.employeeId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Employee updated successfully!");
        } 
        
        catch (SQLException e) 
        {
            JOptionPane.showMessageDialog(null, "Error updating employee: " + e.getMessage());
        }
    }
}

class EmployeeManagementFrame extends JFrame 
{
    private EmployeeDataHandler dataHandler;
    private JTextField employeeIdField;

    public EmployeeManagementFrame() 
    {
        super("Employee Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        dataHandler = new EmployeeDataHandler();

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel addPanel = createEmployeeForm("Add Employee");
        JButton addButton = new JButton("Add Employee");
        addButton.addActionListener(e -> handleAddEmployee(addPanel));
        addPanel.add(addButton);
        tabbedPane.addTab("Add", addPanel);

        JPanel viewPanel = new JPanel(new BorderLayout());
        employeeIdField = new JTextField(20);
        JButton viewButton = new JButton("View Employee");
        viewButton.addActionListener(e -> handleViewEmployee());
        JPanel topPanel = new JPanel();
        topPanel.add(createLabel("Employee ID :"));
        topPanel.add(employeeIdField);
        topPanel.add(viewButton);
        JTextArea viewArea = new JTextArea();
        viewArea.setEditable(false);
        viewPanel.add(topPanel, BorderLayout.NORTH);
        viewPanel.add(new JScrollPane(viewArea), BorderLayout.CENTER);
        tabbedPane.addTab("View", viewPanel);

        JPanel removePanel = new JPanel();
        JTextField removeIdField = new JTextField(20);
        JButton removeButton = new JButton("Remove Employee");
        removeButton.addActionListener(e -> handleRemoveEmployee(removeIdField.getText().trim()));
        removePanel.add(createLabel("Employee ID :"));
        removePanel.add(removeIdField);
        removePanel.add(removeButton);
        tabbedPane.addTab("Remove", removePanel);

        JPanel updatePanel = createEmployeeForm("Update Employee");
        JButton updateButton = new JButton("Update Employee");
        updateButton.addActionListener(e -> handleUpdateEmployee(updatePanel));
        updatePanel.add(updateButton);
        tabbedPane.addTab("Update", updatePanel);

        add(tabbedPane, BorderLayout.CENTER);

        applyDarkTheme(this);
    }

    private JPanel createEmployeeForm(String title) 
    {
        JPanel panel = new JPanel(new GridLayout(8, 2));
        panel.add(createLabel("Employee ID :"));
        JTextField idField = new JTextField();
        panel.add(idField);
        panel.add(createLabel("Name :"));
        JTextField nameField = new JTextField();
        nameField.setDocument(new JTextFieldLimit(20, true));
        panel.add(nameField);
        panel.add(createLabel("DOB :"));
        JSpinner dobSpinner = new JSpinner(new SpinnerDateModel());
        dobSpinner.setEditor(new JSpinner.DateEditor(dobSpinner, "yyyy-MM-dd"));
        panel.add(dobSpinner);
        panel.add(createLabel("Email :"));
        JTextField emailField = new JTextField();
        panel.add(emailField);
        panel.add(createLabel("Position :"));
        JTextField positionField = new JTextField();
        panel.add(positionField);
        panel.add(createLabel("Contact :"));
        JTextField contactField = new JTextField();
        contactField.setDocument(new JTextFieldLimit(20, false));
        panel.add(contactField);
        panel.add(createLabel("Salary :"));
        JTextField salaryField = new JTextField();
        salaryField.setDocument(new JTextFieldLimit(20, false));
        panel.add(salaryField);
        return panel;
    }

    private void handleAddEmployee(JPanel panel) 
    {
        ConcreteEmployee emp = new ConcreteEmployee();
        emp.getEmployeeInfoFromForm(panel)  ;
        dataHandler.addEmployee(emp);
    }

    private void handleViewEmployee() 
    {
        String employeeId = employeeIdField.getText().trim();

        if (!employeeId.isEmpty()) 
        {
            JTextArea displayArea = new JTextArea();
            dataHandler.viewEmployee(employeeId, displayArea);
            JOptionPane.showMessageDialog(null, new JScrollPane(displayArea), "Employee Details", JOptionPane.INFORMATION_MESSAGE);
        } 
        
        else 
        {
            JOptionPane.showMessageDialog(null, "Please enter an Employee ID");
        }
    }

    private void handleRemoveEmployee(String employeeId) 
    {
        if (!employeeId.isEmpty()) 
        {
            dataHandler.removeEmployee(employeeId);
        }
        
        else 
        {
            JOptionPane.showMessageDialog(null, "Please enter an Employee ID");
        }
    }

    private void handleUpdateEmployee(JPanel panel) 
    {
        JTextField idField = (JTextField) panel.getComponent(1);
        String employeeId = idField.getText().trim();

        if (!employeeId.isEmpty()) 
        {
            dataHandler.updateEmployee(employeeId, panel);
        }
        
        else 
        {
            JOptionPane.showMessageDialog(null, "Please enter an Employee ID");
        }
    }

    private void applyDarkTheme(Component component) 
    {
        component.setBackground(Color.DARK_GRAY);
        component.setForeground(Color.CYAN);

        if (component instanceof Container) 
        {
            for (Component child : ((Container) component).getComponents()) 
            {
                applyDarkTheme(child);
            }
        }
    }

    private JLabel createLabel(String text) 
    {
        JLabel label = new JLabel(text);
        label.setForeground(Color.CYAN);
        return label;
    }

    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> 
        {
            new EmployeeManagementFrame().setVisible(true);
        });
    }
}

class JTextFieldLimit extends PlainDocument 
{
    private final int limit;
    private final boolean isAlpha;

    JTextFieldLimit(int limit, boolean isAlpha)
    {
        super();
        this.limit = limit;
        this.isAlpha = isAlpha;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException 
    {
        if (str == null) 
        {
            return;
        }

        if ((getLength() + str.length()) <= limit) 
        {
            boolean isValid = true;

            if (isAlpha) 
            {
                for (char c : str.toCharArray()) 
                {
                    if (!Character.isLetter(c) && !Character.isWhitespace(c)) 
                    {
                        isValid = false;
                        break;
                    }
                }
            } 
            
            else 
            {
                for (char c : str.toCharArray()) 
                {
                    if (!Character.isDigit(c)) 
                    {
                        isValid = false;
                        break;
                    }
                }
            }

            if (isValid) 
            {
                super.insertString(offset, str, attr);
            }
        }
    }
}
