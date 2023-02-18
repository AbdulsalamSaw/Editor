package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

@SuppressWarnings("serial")
public class Editor extends JFrame implements ActionListener, DocumentListener {

    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 500;
    private static final int APPROVE_OPTION = 0;
    private static final int CANCEL_OPTION = 1;
    private static final int MESSAGE_ERROR = 0;
    private static final int MESSAGE_WARNING = 2;
    private static final int OPTION_TYPE = 0;

    private static final String VK_X = "KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK";
    private static final String VK_C= "KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK";
    private static final String VK_V="KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK";
    private static final String VK_F= "KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK";
    private static final String VK_A= "KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK";
    private static final String VK_N = "KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK";
    private static final String VK_O= "KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK";
    private static final String VK_S="KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK";
    private static final String VK_Q= "KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK";
    private static final int NO_OPTION = 1;

    public JEditorPane textPane;
    private JMenuBar menuBar;
    private JMenuItem copy, paste, cut, move;
    public boolean isChanged = false;
    private File file;

    public static void main(String[] args) {
        new Editor();
    }

    public Editor() {
        //Editor the name of our application
        super("Editor");
        textPane = new JEditorPane();
        // center means middle of container.
        add(new JScrollPane(textPane), "Center");
        textPane.getDocument().addDocumentListener(this);
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        buildMenu();
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void buildMenu() {
        buildFileMenu();
        buildEditMenu();
    }

       private void extracted(JMenu file,JMenuItem n ,char key, String KEY_ ) {
           n.setMnemonic(key);
           n.setAccelerator(KeyStroke.getKeyStroke(KEY_));
           n.addActionListener(this);
           file.add(n);
        }

    private void buildFileMenu() {
        JMenu file = new JMenu("File");
        file.setMnemonic('F');
        menuBar.add(file);

        JMenuItem n = new JMenuItem("New");
        extracted(file,n,'N',VK_N);

        JMenuItem open = new JMenuItem("Open");
        extracted(file,open,'O',VK_O);

        JMenuItem save = new JMenuItem("Save");
        extracted(file,save,'S',VK_S);

        JMenuItem saveas = new JMenuItem("Save as...");
        extracted(file,saveas,'A',VK_S);

        JMenuItem quit = new JMenuItem("Quit");
        extracted(file,quit,'Q',VK_Q);

    }

    private void buildEditMenu() {
        JMenu edit = new JMenu("Edit");
        menuBar.add(edit);
        edit.setMnemonic('E');

        cut = new JMenuItem("Cut");
        extracted(edit,cut,'T',VK_X);

        copy = new JMenuItem("Copy");
        extracted(edit,copy,'C',VK_C);

        paste = new JMenuItem("Paste");
        extracted(edit,paste,'P',VK_V);

        JMenuItem find = new JMenuItem("Find");
        extracted(edit,find,'F',VK_F);

        JMenuItem sall = new JMenuItem("Select All");
        extracted(edit,sall,'A',VK_A);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        int ans = 0;
        switch (action) {
            case "Quit":
                System.exit(0);
                break;
            case "Open":
                loadFile();
                break;
            case "Save":
                if (isChanged) {
                    ans = JOptionPane.showConfirmDialog(null, "The file has changed. You want to save it?", "Save file",
                            OPTION_TYPE, MESSAGE_WARNING);
                }
            if(ans != NO_OPTION &&file==null)
                saveAs("Save");
                  else if(ans !=NO_OPTION&&file!=null)
                     extracted();
                break;

            case "New":
                if (isChanged) {
                    ans = JOptionPane.showConfirmDialog(null,
                                  "The file has changed. You want to save it?", "Save file",
                                OPTION_TYPE, MESSAGE_WARNING);
                        if (ans == NO_OPTION)
                            return;
                    if (file == null) {
                        saveAs("Save");
                        return;
                    }
                    extracted();
                }
                file = null;
                textPane.setText("");
                isChanged = false;
                setTitle("Editor");
                 break;
            case "Save as...":
                saveAs("Save as...");
                break;
            case "Select All":
                textPane.selectAll();
                break;
            case "Copy":
                textPane.copy();
                break;
            case "Cut":
                textPane.cut();
                break;
            case "Paste":
                textPane.paste();
                break;
            case "Find":
                //FindDialog find = new FindDialog(this, true);
                //find.showDialog();
                break;
            default:
        }
    }

    private void extracted() {
        String text = textPane.getText();
        System.out.println(text);
        try (PrintWriter writer = new PrintWriter(file);) {
            if (!file.canWrite())
                throw new Exception("Cannot write file!");
            writer.write(text);
            isChanged = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void loadFile() {
        JFileChooser dialog = new JFileChooser(System.getProperty("user.home"));
        dialog.setMultiSelectionEnabled(false);
        try {
            int result = dialog.showOpenDialog(this);
            if (result == CANCEL_OPTION)
                return;
            if (result == APPROVE_OPTION) {
                if (isChanged) {
                    //Save file
                    int ans = JOptionPane.showConfirmDialog(null, "The file has changed. You want to save it?"
                                , "Save file",
                                OPTION_TYPE, MESSAGE_WARNING);
                        System.out.print(ans);
                        if (ans == NO_OPTION)
                            return;
                    if (file == null) {
                        saveAs("Save");
                        return;
                    }
                    extracted();
                }
                file = dialog.getSelectedFile();
                //Read file
                StringBuilder rs = new StringBuilder();
                try (FileReader fr = new FileReader(file);
                     BufferedReader reader = new BufferedReader(fr);) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        rs.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Cannot read file !", "Error !", MESSAGE_ERROR);
                }

                textPane.setText(rs.toString());
                isChanged = false;
                setTitle("Editor - " + file.getName());
            }
        } catch (Exception error) {
            error.printStackTrace();
            JOptionPane.showMessageDialog(null, error, "Error", MESSAGE_ERROR);
        }
    }



    private void setDialogTitles(String dialogTitle, String message) {
        JFileChooser dialog = new JFileChooser(System.getProperty("user.home"));
        dialog.setDialogTitle(dialogTitle);
        int result = dialog.showSaveDialog(this);
        if (result != APPROVE_OPTION)
            return;
        file = dialog.getSelectedFile();
        try (PrintWriter writer = new PrintWriter(file);) {
            writer.write(textPane.getText());
            isChanged = false;
            setTitle(message + file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveAs(String dialogTitle) {
        setDialogTitles(dialogTitle, "Editor - ");
    }

    private void saveAsText(String dialogTitle) {
        setDialogTitles(dialogTitle, "Save as Text Editor - ");
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        isChanged = true;
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        isChanged = true;
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        isChanged = true;
    }
}