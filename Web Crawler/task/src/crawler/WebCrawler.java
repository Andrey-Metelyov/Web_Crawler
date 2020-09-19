package crawler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler extends JFrame {
    JTextField textField;
    JLabel titleValueLabel;
    JTable table;

    class URLRecord {
        String url;
        String title;

        public URLRecord(String url, String title) {
            this.url = url;
            this.title = title;
        }

        @Override
        public String toString() {
            return "URLRecord{" +
                    "url='" + url + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setTitle("Web Crawler");

        JPanel panel = new JPanel();

        JLabel urlLabel = new JLabel("URL:");
        textField = new JTextField("https://www.wikipedia.org", 20);
        textField.setName("UrlTextField");

        JButton button = new JButton("Parse");
        button.setName("RunButton");
        button.addActionListener(e -> parseClicked());

        JLabel titleLabel = new JLabel("Title:");

        titleValueLabel = new JLabel("Example text");
        titleValueLabel.setName("TitleLabel");

        table = createTable();

        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        table.setEnabled(false);

        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);
        panel.add(urlLabel);
        panel.add(textField);
        panel.add(button);
        panel.add(titleLabel);
        panel.add(titleValueLabel);
        panel.add(scrollPane);

        layout.putConstraint(SpringLayout.NORTH, button, 5, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.EAST, button, -5, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, textField, 0, SpringLayout.VERTICAL_CENTER, button);
        layout.putConstraint(SpringLayout.EAST, textField, -5, SpringLayout.WEST, button);
        layout.putConstraint(SpringLayout.WEST, urlLabel, 5, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.WEST, textField, 5, SpringLayout.EAST, urlLabel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, urlLabel, 0, SpringLayout.VERTICAL_CENTER, button);

        layout.putConstraint(SpringLayout.WEST, titleLabel, 5, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, titleLabel, 5, SpringLayout.SOUTH, textField);
        layout.putConstraint(SpringLayout.WEST, titleValueLabel, 5, SpringLayout.EAST, titleLabel);
        layout.putConstraint(SpringLayout.NORTH, titleValueLabel, 5, SpringLayout.SOUTH, textField);

        layout.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.NORTH, scrollPane, 5, SpringLayout.SOUTH, titleLabel);
        layout.putConstraint(SpringLayout.SOUTH, scrollPane, -5, SpringLayout.SOUTH, panel);

        add(panel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void parseClicked() {

        String siteText = getWebPage();
//        System.out.println(siteText);
//        System.out.println("siteText:\n" + siteText);
        String title = findTitle(siteText);
//        System.out.println("Title: " + title);
        titleValueLabel.setText(title);
        List<URLRecord> urls = getURLs(siteText);
        clearTable();
        fillTable(urls);
    }

    private void clearTable() {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);
    }

    private void fillTable(List<URLRecord> urls) {
        final String baseUrl = textField.getText();
        for (URLRecord url : urls) {
            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
            if (valudUrl(url.url)) {
                tableModel.addRow(new String[]{url.url, url.title});
            }
            System.out.println(url);
        }
    }

    private boolean valudUrl(String url) {
//        URLConnection =
        return true;
    }

    private List<URLRecord> getURLs(String siteText) {
        List<URLRecord> result = new ArrayList<>();

        Pattern pattern = Pattern.compile("<a\\s+href=['\\\"](.+)['\\\"]>(.+)</a>");
        Matcher matcher = pattern.matcher(siteText);
        while (matcher.find()) {
            result.add(new URLRecord(matcher.group(1), matcher.group(2)));
        }

        return result;
    }

    private JTable createTable() {
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"URL", "Title"});
        JTable table = new JTable(tableModel);
        table.setName("TitlesTable");

        return table;
    }

    private String getWebPage() {
        final String url = textField.getText();
        System.out.println(url);
        try (InputStream inputStream = new BufferedInputStream(new URL(url).openStream())) {
            String siteText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
//            textArea.setText(siteText);
//            System.out.println(siteText);
            return siteText;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private String findTitle(String siteText) {
//        <a\s+href=['\"](.+)['\"]>(.+)</a>
        Pattern pattern = Pattern.compile("<title>(.+)</title>");
        Matcher matcher = pattern.matcher(siteText);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }
}
