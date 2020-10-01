package crawler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler extends JFrame {
    JTextField textField;
    JLabel titleValueLabel;
    JTable table;
    JTextField exportFilePath;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            URLRecord urlRecord = (URLRecord) o;

            if (!url.equals(urlRecord.url)) return false;
            return title.equals(urlRecord.title);
        }

        @Override
        public int hashCode() {
            int result = url.hashCode();
            result = 31 * result + title.hashCode();
            return result;
        }
    }

    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setTitle("Web Crawler");

        JPanel panel = new JPanel();

        JLabel urlLabel = new JLabel("URL:");
        JLabel exportLabel = new JLabel("Export:");
        textField = new JTextField("https://www.wikipedia.org", 20);
        textField.setName("UrlTextField");

        exportFilePath = new JTextField("d:\\temp\\last.txt");
        exportFilePath.setName("ExportUrlTextField");

        JButton button = new JButton("Parse");
        button.setName("RunButton");
        button.addActionListener(e -> parseClicked());

        JButton exportButton = new JButton("Save");
        exportButton.setName("ExportButton");
        exportButton.addActionListener(e -> exportClicked());

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
        panel.add(exportLabel);
        panel.add(exportFilePath);
        panel.add(exportButton);

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
        layout.putConstraint(SpringLayout.SOUTH, scrollPane, -5, SpringLayout.NORTH, exportButton);

        layout.putConstraint(SpringLayout.SOUTH, exportButton, -5, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.EAST, exportButton, -5, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, exportFilePath, 0, SpringLayout.VERTICAL_CENTER, exportButton);
        layout.putConstraint(SpringLayout.EAST, exportFilePath, -5, SpringLayout.WEST, button);
        layout.putConstraint(SpringLayout.WEST, exportLabel, 5, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.WEST, exportFilePath, 5, SpringLayout.EAST, exportLabel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, exportLabel, 0, SpringLayout.VERTICAL_CENTER, exportButton);

        add(panel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void exportClicked() {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        int columns = tableModel.getColumnCount();
        int rows = tableModel.getRowCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                sb.append(tableModel.getValueAt(i, j)).append(System.lineSeparator());
            }
        }
        System.out.println(sb);
        saveToFile(exportFilePath.getText(), sb.toString());
    }

    private void saveToFile(String fileName, String content) {
        File file = new File(fileName);
        try (FileWriter writer = new FileWriter(file);) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseClicked() {
        String address = textField.getText();
        String siteText = getWebPage(address);
//        System.out.println(siteText);
//        System.out.println("siteText:\n" + siteText);
        String title = findTitle(siteText);
//        System.out.println("Title: " + title);
        titleValueLabel.setText(title);
        List<String> aTags = getATags(siteText);
        List<String> hrefAttributes = getHrefAttributes(aTags);
        System.out.println("hrefAttributes: " + hrefAttributes);
        List<String> links = fixLinks(address, hrefAttributes);
        System.out.println("links: " + links);
        List<URLRecord> urls = crawlLinks(links);
        URLRecord current = new URLRecord(address, title);
        if (!urls.contains(current)) {
            urls.add(current);
        }
        clearTable();
        fillTable(urls);
    }

    private List<URLRecord> crawlLinks(List<String> links) {
        List<URLRecord> result = new ArrayList<>();
        for (String link : links) {
            Optional<URLRecord> res = visitPage(link);
            if (res.isPresent()) {
                result.add(res.get());
                System.out.println(res.get());
            }
        }
        return result;
    }

    private Optional<URLRecord> visitPage(String link) {
        try {
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
            String contentType = connection.getContentType();
            System.out.println(link + " content type: " + contentType);
            if (contentType != null && contentType.startsWith("text/html")) {
                InputStream inputStream = connection.getInputStream();
                String siteText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                return Optional.of(new URLRecord(link, findTitle(siteText)));
            }
        } catch (MalformedURLException e) {
            System.out.println("bad link: " + link);
//            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("unavailable page: " + link);
//            e.printStackTrace();
        }
        return Optional.empty();
    }

    private List<String> fixLinks(String address, List<String> hrefAttributes) {
        String protocol = getProtocol(address);
        String rootAddress = getRootAddress(address);
        System.out.println(protocol);
        System.out.println(rootAddress);
        List<String> result = new ArrayList<>();
        for (String hrefAttribute : hrefAttributes) {
            String fixedLink = "";
            if (!hrefAttribute.contains("/")) {
                fixedLink = rootAddress + "/" + hrefAttribute;
            } else {
                String hrefProtocol = getProtocol(hrefAttribute);
                if (hrefProtocol.isEmpty()) {
                    if (hrefAttribute.startsWith("//")) {
                        fixedLink = protocol + hrefAttribute;
                    } else if (hrefAttribute.equals("/")) {
                        fixedLink = rootAddress;
                    } else {
                        fixedLink = protocol + "//" + hrefAttribute;
                    }
                } else {
                    fixedLink = hrefAttribute;
                }
            }
            System.out.println(hrefAttribute + " -> " + fixedLink);
            result.add(fixedLink);
        }
        return result;
    }

    private String getRootAddress(String address) {
        String protocol = getProtocol(address);
        int lastSlashPosition = address.lastIndexOf("/");
        System.out.println(lastSlashPosition);
        return address.substring(0, (lastSlashPosition > protocol.length() + 2) ? lastSlashPosition : address.length());
    }

    private String getProtocol(String address) {
        return address.substring(0, address.indexOf(":") + 1);
    }

    private List<String> getHrefAttributes(List<String> aTags) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\s*href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");
        for (String aTag : aTags) {
//            System.out.println(aTag);
            Matcher matcher = pattern.matcher(aTag);
            if (matcher.find()) {
                String url = matcher.group(1);
                result.add(url.substring(1, url.length() - 1));
//                System.out.println(matcher.group(1));
            } else {
//                System.out.println("not found");
            }
        }
        return result;
    }

    private void clearTable() {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);
    }

    private void fillTable(List<URLRecord> urls) {
        final String baseUrl = textField.getText();
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        for (URLRecord url : urls) {
            if (validUrl(url.url)) {
                tableModel.addRow(new String[]{url.url, url.title});
            }
            System.out.println("url: " + url);
        }
    }

    private boolean validUrl(String url) {
//        URLConnection =
        return true;
    }

    private List<String> getATags(String siteText) {
        List<String> result = new ArrayList<>();

        Pattern pattern = Pattern.compile("<a([^>]+)>(.+?)</a>");
        Matcher matcher = pattern.matcher(siteText);
        while (matcher.find()) {
            result.add(matcher.group(1));
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

    private String getWebPage(String url) {
//        final String url = textField.getText();
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
