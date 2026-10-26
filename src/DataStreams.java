
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreams {

    private JFrame frame;
    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchTextField;
    private JButton loadButton;
    private JButton searchButton;
    private JButton quitButton;

    private Path filePath;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DataStreams().createGUI();
        });
    }

    private void createGUI() {
        frame = new JFrame("Java Data Streams - File Search");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);

        // Create the text areas.
        originalTextArea = new JTextArea();
        originalTextArea.setEditable(false);
        originalTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        filteredTextArea = new JTextArea();
        filteredTextArea.setEditable(false);
        filteredTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        // Place each text area inside a scrolling pane.
        JScrollPane originalScrollPane =
                new JScrollPane(originalTextArea);

        JScrollPane filteredScrollPane =
                new JScrollPane(filteredTextArea);

        // Create labels for the two text areas.
        JPanel originalPanel = new JPanel(new BorderLayout());
        originalPanel.add(new JLabel("Original File"), BorderLayout.NORTH);
        originalPanel.add(originalScrollPane, BorderLayout.CENTER);

        JPanel filteredPanel = new JPanel(new BorderLayout());
        filteredPanel.add(new JLabel("Filtered Results"), BorderLayout.NORTH);
        filteredPanel.add(filteredScrollPane, BorderLayout.CENTER);

        // Display the text areas side by side.
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                originalPanel,
                filteredPanel
        );

        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(0.5);

        // Create the search field and buttons.
        searchTextField = new JTextField(20);

        loadButton = new JButton("Load File");
        searchButton = new JButton("Search File");
        quitButton = new JButton("Quit");

        // Searching is not allowed until a file is loaded.
        searchButton.setEnabled(false);

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(loadButton);
        controlPanel.add(new JLabel("Search String:"));
        controlPanel.add(searchTextField);
        controlPanel.add(searchButton);
        controlPanel.add(quitButton);

        // Add the components to the window.
        frame.setLayout(new BorderLayout(5, 5));
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(splitPane, BorderLayout.CENTER);

        // Load a text file.
        loadButton.addActionListener(e -> loadFile());

        // Search the loaded file.
        searchButton.addActionListener(e -> searchFile());

        // Close the application.
        quitButton.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }

    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a Text File");

        int result = fileChooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            Path selectedPath = fileChooser.getSelectedFile().toPath();

            try {
                String fileContents = String.join(
                        System.lineSeparator(),
                        Files.readAllLines(selectedPath)
                );

                filePath = selectedPath;
                originalTextArea.setText(fileContents);
                filteredTextArea.setText("");
                searchTextField.setText("");

                searchButton.setEnabled(true);

                frame.setTitle(
                        "Java Data Streams - " +
                        filePath.getFileName()
                );

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Unable to load the file:\n" + ex.getMessage(),
                        "File Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void searchFile() {
        if (filePath == null) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please load a file first."
            );
            return;
        }

        String searchString = searchTextField.getText();

        if (searchString.isEmpty()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please enter a search string."
            );
            return;
        }

        try (Stream<String> lines = Files.lines(filePath)) {

            // Use a Stream and lambda expression to filter matching lines.
            List<String> matchingLines = lines
                    .filter(line -> line.toLowerCase()
                            .contains(searchString.toLowerCase()))
                    .collect(Collectors.toList());

            // Display the matching lines in the right text area.
            filteredTextArea.setText(
                    String.join(System.lineSeparator(), matchingLines)
            );

            if (matchingLines.isEmpty()) {
                filteredTextArea.setText(
                        "No matching lines found."
                );
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Unable to search the file:\n" + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
