

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;


public class Log extends JPanel {
    JTextArea textPath, textSearch, fileContent, textTab;
    JButton openButton, searchButton, saveButton;
    JFileChooser fc, fc1;
    JSplitPane splitPane;
    JComboBox<String> extTitles = new JComboBox<String>();
    List directoryList = new List(10, false);
    static int lineNumber = 0;


    public static void main(String[] args) {
        Log gui = new Log();
        gui.go();
    }

    public void go() {
        JFrame frame = new JFrame("Поисковик");
        JPanel panelNorth = new JPanel();
        JLabel labelPath = new JLabel("Путь:");
        JLabel labelExt = new JLabel("Разрешение:");
        JLabel labelSearch = new JLabel("Поиск:");
        Box northBox = new Box(BoxLayout.X_AXIS);

        extTitles.addItem(".log");
        extTitles.addItem(".txt");


        //Добавляем в центральную область две панели правую и левую

        textPath = new JTextArea(1, 11);
        textSearch = new JTextArea(1, 11);
        fileContent = new JTextArea(20, 30);
        fileContent.setLineWrap(true);
        openButton = new JButton("Открыть папку");
        saveButton = new JButton("Сохранить");
        searchButton = new JButton("Найти");

        JScrollPane pathScrollPane = new JScrollPane(textPath);
        pathScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pathScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        JScrollPane searchScrollPane = new JScrollPane(textSearch);
        searchScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        searchScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        JScrollPane contentScrollPane = new JScrollPane(fileContent);
        contentScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        fc = new JFileChooser();
        fc1 = new JFileChooser();
        fc1.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, directoryList, contentScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(150);



        openButton.addActionListener(new MyOpenFolderListener());
        saveButton.addActionListener(new MySaveFileListener());
        directoryList.addActionListener(new MySelectedItemListener());
        searchButton.addActionListener(new MySearchListener());

        northBox.add(labelPath);
        northBox.add(Box.createRigidArea(new Dimension(10, 0)));
        northBox.add(pathScrollPane);
        northBox.add(Box.createRigidArea(new Dimension(10, 0)));
        northBox.add(openButton);
        northBox.add(Box.createRigidArea(new Dimension(10, 0)));
        northBox.add(labelSearch);
        northBox.add(Box.createRigidArea(new Dimension(10, 0)));
        northBox.add(searchScrollPane);
        northBox.add(Box.createRigidArea(new Dimension(10, 0)));
        northBox.add(labelExt);
        northBox.add(Box.createRigidArea(new Dimension(10, 0)));
        northBox.add(extTitles);
        northBox.add(Box.createRigidArea(new Dimension(10, 0)));
        northBox.add(searchButton);
        northBox.add(Box.createRigidArea(new Dimension(10, 0)));
        northBox.add(saveButton);
        panelNorth.add(northBox);



        frame.getContentPane().add(BorderLayout.CENTER, splitPane);
        frame.getContentPane().add(BorderLayout.NORTH, panelNorth);
        frame.setSize(900, 500);
        frame.setVisible(true);

    }
    public class Searcher implements Runnable{
        @Override
        public void run() {
            File dir = new File(textPath.getText());
            searchForTextFiles(dir);
        }
        public void searchForText(File f, String ext) {
            String toFind = textSearch.getText();
            String filePath = "";
            try {
                filePath = f.getCanonicalPath();
                if (filePath.endsWith(ext)){
                    try {
                        FileReader reader = new FileReader(filePath);
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        String s;
                        while ((s = bufferedReader.readLine()) != null) {
                            lineNumber++;
                            if (s.contains(toFind)) {
                                directoryList.add(filePath);
                            }
                            break;
                        }
                        reader.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        public void searchForTextFiles(File dir) {
            String fileExt = extTitles.getSelectedItem().toString();
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    searchForTextFiles(file);
                } else {
                    searchForText(file, fileExt);
                }
            }

        }
    }

    public class MySearchListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            fileContent.removeAll();
            directoryList.removeAll();
            Thread searcherThread = new Thread(new Searcher());
            searcherThread.start();
        }
    }

    public class MyOpenFolderListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == openButton) {
                int returnVal = fc.showOpenDialog(Log.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    textPath.setText(file.getPath());
                }
            }
        }
    }

    public class MySaveFileListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == saveButton) {
                int returnVal = fc1.showSaveDialog(Log.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try{
                        FileWriter fileWriter = new FileWriter(fc1.getSelectedFile());
                        fileWriter.write(fileContent.getText());
                        fileWriter.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
            }
        }
    }

    public class MySelectedItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String filePath = directoryList.getItem(directoryList.getSelectedIndex());
            try {
                FileReader reader = new FileReader(filePath);
                BufferedReader bufferedReader = new BufferedReader(reader);
                fileContent.read(bufferedReader, null);
                bufferedReader.close();
                fileContent.requestFocus();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
