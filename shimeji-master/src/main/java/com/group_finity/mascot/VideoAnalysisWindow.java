package com.group_finity.mascot;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class VideoAnalysisWindow extends JFrame {

    private JPanel videoPanel;
    private JPanel controlPanel;
    private JPanel chatPanel;
    private JButton selectVideoButton;
    private JButton analyzeButton;
    private JButton thirdButton;
    private JTextArea chatArea;
    private JTextArea inputField;
    private JButton sendButton;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private JFXPanel jfxPanel;
    private Button playPauseButton;
    private Slider progressBar;
    private ComboBox<String> speedSelector;

    public VideoAnalysisWindow() {
        setTitle("Video Analysis Window");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main panel to hold video and control panels
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // Video panel
        videoPanel = new JPanel(new BorderLayout());
        videoPanel.setPreferredSize(new Dimension(640, 360)); // 16:9 aspect ratio
        videoPanel.setBorder(BorderFactory.createTitledBorder("Video Player"));
        mainPanel.add(videoPanel, BorderLayout.CENTER);

        // Control panel
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1, 3, 10, 0)); // 1 row, 3 columns, 10px horizontal gap
        selectVideoButton = createButton("选择视频", "icons/select.png");
        analyzeButton = createButton("分析视频", "icons/analyze.png");
        thirdButton = createButton("无", "icons/third.png");
        controlPanel.add(selectVideoButton);
        controlPanel.add(analyzeButton);
        controlPanel.add(thirdButton);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        // Chat panel
        chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setPreferredSize(new Dimension(300, 0));
        chatPanel.setBorder(BorderFactory.createTitledBorder("Chat"));
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14)); // Set font to support Chinese characters
        chatArea.setBackground(Color.LIGHT_GRAY);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputField = new JTextArea();
        inputField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14)); // Set font to support Chinese characters
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);
        inputField.setRows(1);
        inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, inputField.getPreferredSize().height * 4));
        inputField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    sendButton.doClick();
                }
            }
        });
        inputField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                adjustInputFieldHeight();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                adjustInputFieldHeight();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                adjustInputFieldHeight();
            }

            private void adjustInputFieldHeight() {
                int lines = inputField.getLineCount();
                if (lines > 4) {
                    lines = 4;
                }
                inputField.setRows(lines);
                inputField.revalidate();
            }
        });

        sendButton = createButton("发送", "icons/send.png");
        sendButton.setPreferredSize(new Dimension(80, 30)); // Smaller button size
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        add(chatPanel, BorderLayout.EAST);

        // Add action listener to send button
        sendButton.addActionListener(e -> {
            String inputText = inputField.getText().trim();
            if (!inputText.isEmpty()) {
                chatArea.append("User: " + inputText + "\n");
                inputField.setText("");
                // Here you can add code to handle the input text
            }
        });

        // Add action listener to select video button
        selectVideoButton.addActionListener(e -> selectAndPlayVideo());

        // Initialize JavaFX
        jfxPanel = new JFXPanel();
        videoPanel.add(jfxPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 14)); // Set font to support Chinese characters
        button.setIcon(new ImageIcon(iconPath));
        button.setPreferredSize(new Dimension(150, 30)); // Increase button width
        return button;
    }

    private void selectAndPlayVideo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            playVideo(selectedFile);
        }
    }

    private void playVideo(File file) {
        Platform.runLater(() -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaView = new MediaView(mediaPlayer);
            mediaView.setPreserveRatio(true);

            // Bind MediaView size to JFXPanel size
            jfxPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    mediaView.setFitWidth(jfxPanel.getWidth());
                    mediaView.setFitHeight(jfxPanel.getHeight());
                }
            });

            // Create control panel for video
            HBox videoControlPanel = new HBox(10);
            videoControlPanel.setAlignment(javafx.geometry.Pos.CENTER);
            playPauseButton = new Button("暂停");
            playPauseButton.setOnAction(e -> {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause();
                    playPauseButton.setText("播放");
                } else {
                    mediaPlayer.play();
                    playPauseButton.setText("暂停");
                }
            });

            progressBar = new Slider();
            progressBar.setMin(0);
            progressBar.setMax(100);
            progressBar.setValue(0);
            progressBar.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (progressBar.isValueChanging()) {
                    mediaPlayer.seek(Duration.seconds(newVal.doubleValue()));
                }
            });

            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (!progressBar.isValueChanging()) {
                    progressBar.setValue(newTime.toSeconds());
                }
            });

            mediaPlayer.setOnReady(() -> {
                progressBar.setMax(mediaPlayer.getTotalDuration().toSeconds());
            });

            speedSelector = new ComboBox<>();
            speedSelector.getItems().addAll("0.5x", "1x", "1.5x", "2x");
            speedSelector.setValue("1x");
            speedSelector.setOnAction(e -> {
                String speed = speedSelector.getValue();
                mediaPlayer.setRate(Double.parseDouble(speed.replace("x", "")));
            });

            videoControlPanel.getChildren().addAll(playPauseButton, progressBar, speedSelector);

            BorderPane root = new BorderPane();
            root.setCenter(mediaView);
            root.setBottom(videoControlPanel);
            Scene scene = new Scene(root, 640, 360);
            jfxPanel.setScene(scene);

            mediaPlayer.play();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VideoAnalysisWindow window = new VideoAnalysisWindow();
            window.setVisible(true);
        });
    }
}