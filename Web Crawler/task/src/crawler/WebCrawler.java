package crawler;

import javax.swing.*;

public class WebCrawler extends JFrame {
    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setTitle("WebCrawler");

        JTextArea textArea = new JTextArea();
        textArea.setName("TextArea");
        textArea.setBounds(10, 10, 200, 200);
        textArea.setEnabled(false);
        textArea.setText("HTML code?");
        add(textArea);

        setVisible(true);
    }
}