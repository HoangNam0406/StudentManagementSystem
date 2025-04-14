import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class StudentManagementSystem {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtID, txtName, txtClass, txtGender, txtSearch;
    private JComboBox<String> cbMajor;
    private JLabel lblSub1, lblSub2, lblSub3;
    private JTextField txtScore1, txtScore2, txtScore3;
    private JLabel lblAverage;
    private ArrayList<HashMap<String, String>> students;

    public StudentManagementSystem() {
        students = new ArrayList<>();
        frame = new JFrame("Student Management");
        frame.setSize(1000, 500);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLayout(null);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveToFile();
                System.exit(0);
            }
        });

        // Labels and Fields
        JLabel lblID = new JLabel("Student ID:");
        lblID.setBounds(20, 20, 100, 20);
        frame.add(lblID);
        txtID = new JTextField();
        txtID.setBounds(120, 20, 150, 20);
        frame.add(txtID);

        JLabel lblName = new JLabel("Student Name:");
        lblName.setBounds(20, 50, 100, 20);
        frame.add(lblName);
        txtName = new JTextField();
        txtName.setBounds(120, 50, 150, 20);
        frame.add(txtName);

        JLabel lblClass = new JLabel("Class:");
        lblClass.setBounds(20, 80, 100, 20);
        frame.add(lblClass);
        txtClass = new JTextField();
        txtClass.setBounds(120, 80, 150, 20);
        frame.add(txtClass);

        JLabel lblGender = new JLabel("Gender:");
        lblGender.setBounds(20, 110, 100, 20);
        frame.add(lblGender);
        txtGender = new JTextField();
        txtGender.setBounds(120, 110, 150, 20);
        frame.add(txtGender);

        JLabel lblMajor = new JLabel("Major:");
        lblMajor.setBounds(20, 140, 100, 20);
        frame.add(lblMajor);
        cbMajor = new JComboBox<>(new String[]{"BIZ", "GD", "IT"});
        cbMajor.setBounds(120, 140, 150, 20);
        frame.add(cbMajor);

        lblSub1 = new JLabel("Subject 1:");
        lblSub1.setBounds(20, 170, 150, 20);
        frame.add(lblSub1);
        txtScore1 = new JTextField();
        txtScore1.setBounds(180, 170, 50, 20);
        frame.add(txtScore1);

        lblSub2 = new JLabel("Subject 2:");
        lblSub2.setBounds(20, 200, 150, 20);
        frame.add(lblSub2);
        txtScore2 = new JTextField();
        txtScore2.setBounds(180, 200, 50, 20);
        frame.add(txtScore2);

        lblSub3 = new JLabel("Subject 3:");
        lblSub3.setBounds(20, 230, 150, 20);
        frame.add(lblSub3);
        txtScore3 = new JTextField();
        txtScore3.setBounds(180, 230, 50, 20);
        frame.add(txtScore3);

        JLabel lblAvg = new JLabel("Average:");
        lblAvg.setBounds(20, 260, 100, 20);
        frame.add(lblAvg);
        lblAverage = new JLabel("0.0");
        lblAverage.setBounds(120, 260, 150, 20);
        frame.add(lblAverage);

        cbMajor.addActionListener(e -> updateSubjects());

        // Buttons
        JButton btnAdd = new JButton("Add");
        btnAdd.setBounds(20, 290, 80, 25);
        btnAdd.addActionListener(e -> addStudent());
        frame.add(btnAdd);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(120, 290, 80, 25);
        btnUpdate.addActionListener(e -> updateStudent());
        frame.add(btnUpdate);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(20, 320, 80, 25);
        btnDelete.addActionListener(e -> deleteStudent());
        frame.add(btnDelete);

        JButton btnSave = new JButton("Save");
        btnSave.setBounds(220, 320, 80, 25);
        btnSave.addActionListener(e -> saveToFile());
        frame.add(btnSave);

        JButton btnClear = new JButton("Clear");
        btnClear.setBounds(120, 320, 80, 25);
        btnClear.addActionListener(e -> clearFields());
        frame.add(btnClear);

        // Search
        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setBounds(400, 20, 60, 20);
        frame.add(lblSearch);
        txtSearch = new JTextField();
        txtSearch.setBounds(460, 20, 150, 20);
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchStudent(txtSearch.getText());
            }
        });
        frame.add(txtSearch);

        // Sort Button (aligned with Search)
        JButton btnSort = new JButton("Sort");
        btnSort.setBounds(620, 20, 80, 20);
        btnSort.addActionListener(e -> showSortOptions());
        frame.add(btnSort);

        // Table
        model = new DefaultTableModel(new String[]{"Student ID", "Name", "Class", "Gender", "Major", "Avg", "Rank"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(350, 50, 600, 400);
        frame.add(scrollPane);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    txtID.setText(model.getValueAt(selectedRow, 0).toString());
                    txtName.setText(model.getValueAt(selectedRow, 1).toString());
                    txtClass.setText(model.getValueAt(selectedRow, 2).toString());
                    txtGender.setText(model.getValueAt(selectedRow, 3).toString());
                    cbMajor.setSelectedItem(model.getValueAt(selectedRow, 4).toString());
                    updateSubjects();
                    txtScore1.setText("");
                    txtScore2.setText("");
                    txtScore3.setText("");
                    lblAverage.setText(model.getValueAt(selectedRow, 5).toString());
                }
            }
        });

        updateSubjects();
        loadFromFile();
        frame.setVisible(true);
    }

    private void updateSubjects() {
        String major = (String) cbMajor.getSelectedItem();
        if ("BIZ".equals(major)) {
            lblSub1.setText("Business Law:");
            lblSub2.setText("Leadership and Management:");
            lblSub3.setText("Accounting Principles:");
        } else if ("GD".equals(major)) {
            lblSub1.setText("Photoshop:");
            lblSub2.setText("3D Model Link:");
            lblSub3.setText("Graphic Design Principles:");
        } else if ("IT".equals(major)) {
            lblSub1.setText("Security:");
            lblSub2.setText("IoT:");
            lblSub3.setText("Database:");
        }
    }

    private String validateInput(String id, String name, String className, String gender, String major, boolean isUpdate) {
        if (id.trim().isEmpty()) return "Student ID cannot be empty!";
        if (name.trim().isEmpty()) return "Student Name cannot be empty!";
        if (className.trim().isEmpty()) return "Class cannot be empty!";
        if (gender.trim().isEmpty()) return "Gender cannot be empty!";
        if (major == null || major.trim().isEmpty()) return "Major cannot be empty!";
        if (!gender.equalsIgnoreCase("Male") && !gender.equalsIgnoreCase("Female"))
            return "Gender must be 'Male' or 'Female'!";
        if (!isUpdate) {
            for (HashMap<String, String> student : students) {
                if (student.get("ID").equals(id)) return "Student ID '" + id + "' already exists!";
            }
        }
        return null;
    }

    private String validateScores() {
        try {
            double score1 = txtScore1.getText().isEmpty() ? 0 : Double.parseDouble(txtScore1.getText());
            double score2 = txtScore2.getText().isEmpty() ? 0 : Double.parseDouble(txtScore2.getText());
            double score3 = txtScore3.getText().isEmpty() ? 0 : Double.parseDouble(txtScore3.getText());
            if (score1 < 0 || score1 > 10 || score2 < 0 || score2 > 10 || score3 < 0 || score3 > 10) {
                return "Scores must be between 0 and 10!";
            }
            return null;
        } catch (NumberFormatException e) {
            return "Invalid score format! Please enter numeric values.";
        }
    }

    private double calculateAverage() {
        try {
            double score1 = Double.parseDouble(txtScore1.getText().isEmpty() ? "0" : txtScore1.getText());
            double score2 = Double.parseDouble(txtScore2.getText().isEmpty() ? "0" : txtScore2.getText());
            double score3 = Double.parseDouble(txtScore3.getText().isEmpty() ? "0" : txtScore3.getText());
            return (score1 + score2 + score3) / 3.0;
        } catch (NumberFormatException e) {
            showMessage("Error calculating average: Invalid score format!");
            return 0.0;
        }
    }

    private String getRank(double average) {
        if (average < 0 || average > 10) {
            showMessage("Error: Average score out of valid range (0-10)!");
            return "Invalid";
        }
        if (average < 6.5) return "Fail";
        else if (average < 8.0) return "Pass";
        else if (average < 9.0) return "Merit";
        else return "Distinction";
    }

    private void addStudent() {
        String id = txtID.getText();
        String name = txtName.getText();
        String className = txtClass.getText();
        String gender = txtGender.getText();
        String major = (String) cbMajor.getSelectedItem();

        String inputError = validateInput(id, name, className, gender, major, false);
        if (inputError != null) {
            showMessage(inputError);
            return;
        }

        String scoreError = validateScores();
        if (scoreError != null) {
            showMessage(scoreError);
            return;
        }

        double avg = calculateAverage();
        if (avg == 0.0 && (!txtScore1.getText().isEmpty() || !txtScore2.getText().isEmpty() || !txtScore3.getText().isEmpty())) {
            return;
        }
        lblAverage.setText(String.format("%.2f", avg));
        String rank = getRank(avg);

        HashMap<String, String> student = new HashMap<>();
        student.put("ID", id);
        student.put("Name", name);
        student.put("Class", className);
        student.put("Gender", gender);
        student.put("Major", major);
        student.put("Average", String.valueOf(avg));
        student.put("Rank", rank);
        students.add(student);
        model.addRow(new Object[]{id, name, className, gender, major, avg, rank});
        showMessage("Student added successfully!");
    }

    private void deleteStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Operational Error: Please select a student to delete!");
            return;
        }
        students.remove(selectedRow);
        model.removeRow(selectedRow);
        showMessage("Student deleted successfully!");
    }

    private void updateStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Operational Error: Please select a student to update!");
            return;
        }

        String id = txtID.getText();
        String name = txtName.getText();
        String className = txtClass.getText();
        String gender = txtGender.getText();
        String major = (String) cbMajor.getSelectedItem();

        String inputError = validateInput(id, name, className, gender, major, true);
        if (inputError != null) {
            showMessage(inputError);
            return;
        }

        String scoreError = validateScores();
        if (scoreError != null) {
            showMessage(scoreError);
            return;
        }

        double avg = calculateAverage();
        if (avg == 0.0 && (!txtScore1.getText().isEmpty() || !txtScore2.getText().isEmpty() || !txtScore3.getText().isEmpty())) {
            return;
        }
        lblAverage.setText(String.format("%.2f", avg));
        String rank = getRank(avg);

        HashMap<String, String> student = students.get(selectedRow);
        student.put("ID", id);
        student.put("Name", name);
        student.put("Class", className);
        student.put("Gender", gender);
        student.put("Major", major);
        student.put("Average", String.valueOf(avg));
        student.put("Rank", rank);

        model.setValueAt(id, selectedRow, 0);
        model.setValueAt(name, selectedRow, 1);
        model.setValueAt(className, selectedRow, 2);
        model.setValueAt(gender, selectedRow, 3);
        model.setValueAt(major, selectedRow, 4);
        model.setValueAt(avg, selectedRow, 5);
        model.setValueAt(rank, selectedRow, 6);
        showMessage("Student updated successfully!");
    }

    private void searchStudent(String id) {
        if (id.trim().isEmpty()) {
            refreshTable();
            return;
        }
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString().equals(id)) {
                table.setRowSelectionInterval(i, i);
                return;
            }
        }
        showMessage("Operational Error: Student with ID '" + id + "' not found!");
    }

    private void clearFields() {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete all data you are entering?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            txtID.setText("");
            txtName.setText("");
            txtClass.setText("");
            txtGender.setText("");
            cbMajor.setSelectedIndex(0);
            txtScore1.setText("");
            txtScore2.setText("");
            txtScore3.setText("");
            lblAverage.setText("0.0");
            updateSubjects();
            txtSearch.setText("");
            refreshTable();
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (HashMap<String, String> student : students) {
            model.addRow(new Object[]{
                    student.get("ID"),
                    student.get("Name"),
                    student.get("Class"),
                    student.get("Gender"),
                    student.get("Major"),
                    Double.parseDouble(student.get("Average")),
                    student.get("Rank")
            });
        }
    }

    // QuickSort Implementation
    private void quickSortStudents(ArrayList<HashMap<String, String>> list, int low, int high, String key) {
        if (low < high) {
            int pi = partition(list, low, high, key);
            quickSortStudents(list, low, pi - 1, key);
            quickSortStudents(list, pi + 1, high, key);
        }
    }

    private int partition(ArrayList<HashMap<String, String>> list, int low, int high, String key) {
        String pivot = list.get(high).get(key);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (compare(list.get(j).get(key), pivot, key) <= 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    private int compare(String val1, String val2, String key) {
        if (key.equals("ID")) {
            String num1 = val1.replaceAll("[^0-9]", "");
            String num2 = val2.replaceAll("[^0-9]", "");
            int n1 = num1.isEmpty() ? 0 : Integer.parseInt(num1);
            int n2 = num2.isEmpty() ? 0 : Integer.parseInt(num2);
            return Integer.compare(n1, n2);
        }
        return val1.compareTo(val2); // For Name
    }

    private void showSortOptions() {
        String[] options = {"Sort by Student ID (Ascending)", "Sort by Name (A-Z)"};
        int choice = JOptionPane.showOptionDialog(frame,
                "Select sorting option:",
                "Sort Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) { // Sort by Student ID
            quickSortStudents(students, 0, students.size() - 1, "ID");
            refreshTable();
        } else if (choice == 1) { // Sort by Name
            quickSortStudents(students, 0, students.size() - 1, "Name");
            refreshTable();
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("students.csv"))) {
            writer.write("Student ID,Student Name,Class,Gender,Major,Average,Rank\n");
            for (HashMap<String, String> student : students) {
                String id = escapeCSV(student.get("ID"));
                String name = escapeCSV(student.get("Name"));
                String className = escapeCSV(student.get("Class"));
                String gender = escapeCSV(student.get("Gender"));
                String major = escapeCSV(student.get("Major"));
                String avg = escapeCSV(student.get("Average"));
                String rank = escapeCSV(student.get("Rank"));
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s\n", id, name, className, gender, major, avg, rank));
            }
            showMessage("Data saved successfully to students.csv!");
        } catch (IOException ex) {
            showMessage("System Error: Failed to save data - " + ex.getMessage());
        }
    }

    private void loadFromFile() {
        students.clear();
        model.setRowCount(0);
        try (BufferedReader reader = new BufferedReader(new FileReader("students.csv"))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = parseCSVLine(line);
                if (data.length == 7) {
                    HashMap<String, String> student = new HashMap<>();
                    student.put("ID", data[0]);
                    student.put("Name", data[1]);
                    student.put("Class", data[2]);
                    student.put("Gender", data[3]);
                    student.put("Major", data[4]);
                    student.put("Average", data[5]);
                    student.put("Rank", data[6]);
                    students.add(student);
                    model.addRow(new Object[]{data[0], data[1], data[2], data[3], data[4], Double.parseDouble(data[5]), data[6]});
                }
            }
            System.out.println("Data loaded successfully from students.csv");
        } catch (IOException ex) {
            showMessage("System Error: Failed to load data - " + ex.getMessage());
        }
    }

    private String escapeCSV(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }
        result.add(field.toString());
        return result.toArray(new String[0]);
    }

    public static void main(String[] args) {
        try {
            new StudentManagementSystem();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Critical Error: Application failed to start - " + e.getMessage());
        }
    }
}