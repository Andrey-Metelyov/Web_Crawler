package crawler;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebCrawler extends JFrame {
    JTextField textField;
    JTextArea textArea;

    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setTitle("WebCrawler");

        textField = new JTextField("http://exmple.com", 20);
        textField.setName("UrlTextField");

        JButton button = new JButton("Get text!");
        button.setName("RunButton");
        button.addActionListener(e -> getWebPage());

        JPanel upperPanel = new JPanel();
        upperPanel.add(textField);
        upperPanel.add(button);

        textArea = new JTextArea();
        textArea.setName("HtmlTextArea");
//        textArea.setBounds(10, 10, 200, 200);
        textArea.setEnabled(false);
        textArea.setText("HTML code?");

        JPanel panel = new JPanel();

        panel.setLayout(new BorderLayout());

        panel.add(upperPanel, BorderLayout.NORTH);
        panel.add(textArea, BorderLayout.CENTER);

        add(panel);

        setVisible(true);
    }

    private void getWebPage() {
        final String url = textField.getText();
        try (InputStream inputStream = new BufferedInputStream(new URL(url).openStream())) {
            String siteText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            textArea.setText(siteText);
            System.out.println(siteText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
