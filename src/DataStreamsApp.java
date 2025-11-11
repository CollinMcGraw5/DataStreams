import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreamsApp extends JFrame {
    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchField;
    private File loadedFile;

    public DataStreamsApp() {
        setTitle("Data Stream Search");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Text areas
        originalTextArea = new JTextArea();
        filteredTextArea = new JTextArea();
        originalTextArea.setEditable(false);
        filteredTextArea.setEditable(false);

        JScrollPane originalScroll = new JScrollPane(originalTextArea);
        JScrollPane filteredScroll = new JScrollPane(filteredTextArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, originalScroll, filteredScroll);
        splitPane.setDividerLocation(400);

        // Search field
        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        topPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton loadButton = new JButton("Load File");
        JButton searchButton = new JButton("Search");
        JButton quitButton = new JButton("Quit");

        buttonPanel.add(loadButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(quitButton);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load file
        loadButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                loadedFile = chooser.getSelectedFile();
                try (Stream<String> lines = Files.lines(loadedFile.toPath())) {
                    List<String> allLines = lines.collect(Collectors.toList());
                    originalTextArea.setText(String.join("\n", allLines));
                    filteredTextArea.setText("");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error reading file.");
                }
            }
        });

        // Search
        searchButton.addActionListener(e -> {
            if (loadedFile == null) {
                JOptionPane.showMessageDialog(this, "Please load a file first.");
                return;
            }
            String query = searchField.getText().toLowerCase();
            try (Stream<String> lines = Files.lines(loadedFile.toPath())) {
                List<String> matches = lines
                        .filter(line -> line.toLowerCase().contains(query))
                        .collect(Collectors.toList());
                filteredTextArea.setText(String.join("\n", matches));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error filtering file.");
            }
        });

        // Quit
        quitButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DataStreamsApp::new);
    }
}