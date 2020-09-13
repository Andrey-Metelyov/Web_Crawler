package crawler;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler extends JFrame {
    JTextField textField;
    JTextArea textArea;
    JLabel titleLabel;

    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setTitle("WebCrawler");

        JPanel panel = new JPanel();

        textField = new JTextField("http://exmple.com", 20);
        textField.setName("UrlTextField");

        JButton button = new JButton("Get text!");
        button.setName("RunButton");
        button.addActionListener(e -> getWebPage());

        JLabel label = new JLabel("Title:");
        titleLabel = new JLabel();
        titleLabel.setName("TitleLabel");
        titleLabel.setText("Example text");

        textArea = new JTextArea();
        textArea.setName("HtmlTextArea");
//        textArea.setBounds(10, 10, 200, 200);
        textArea.setEnabled(false);
        textArea.setText("HTML code?");

        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);
        panel.add(textField);
        panel.add(button);
        panel.add(label);
        panel.add(titleLabel);
        panel.add(textArea);

        layout.putConstraint(SpringLayout.WEST, textField, 5, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, textField, 8, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.EAST, button, 5, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.NORTH, button, 5, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.EAST, textField, -5, SpringLayout.WEST, button);

        layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, label, 5, SpringLayout.SOUTH, textField);
        layout.putConstraint(SpringLayout.WEST, titleLabel, 5, SpringLayout.EAST, label);
        layout.putConstraint(SpringLayout.NORTH, titleLabel, 5, SpringLayout.SOUTH, textField);

        layout.putConstraint(SpringLayout.WEST, textArea, 5, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.EAST, textArea, -5, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.NORTH, textArea, 5, SpringLayout.SOUTH, label);
        layout.putConstraint(SpringLayout.SOUTH, textArea, -5, SpringLayout.SOUTH, panel);

        add(panel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void getWebPage() {
        final String url = textField.getText();
        try (InputStream inputStream = new BufferedInputStream(new URL(url).openStream())) {
            String siteText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            textArea.setText(siteText);
            String title = findTitle(siteText);
            titleLabel.setText(title);
//            System.out.println(siteText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String findTitle(String siteText) {
        Pattern pattern = Pattern.compile("<title>(.+)</title>");
        Matcher matcher = pattern.matcher(siteText);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "not found";
        }
    }
}
