package ui;

import static javax.swing.LayoutStyle.ComponentPlacement.UNRELATED;

import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginUI extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(LoginUI.class);

    private static final String DEFAULT_COMPANY = "https://wix.net.hilan.co.il";
    private static final String FOLDER_IMG_PATH = "/open_folder.png";

    private JPanel panel;
    private JTextField companyField;
    private JTextField usernameField;
    private JPasswordField passField;
    private JTextField folderPathField;


    public LoginUI() {
        initUI();
    }

    private void initUI() {

        panel = (JPanel) getContentPane();

        JLabel lbl0 = new JLabel("Company Link");
        JLabel lbl1 = new JLabel("Employee Number");
        JLabel lbl2 = new JLabel("Password");
        JLabel lbl3 = new JLabel("Folder Path");

        companyField = new JTextField(DEFAULT_COMPANY, 15);
        usernameField = new JTextField(15);
        passField = new JPasswordField(15);
        folderPathField = new JTextField(15);

        ImageIcon imageIcon = new ImageIcon(getClass().getResource(FOLDER_IMG_PATH));
        Image img = imageIcon.getImage().getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH);
        ImageIcon open = new ImageIcon(img);

        JButton folderButton = new JButton(open);
        folderButton.addActionListener(new OpenFileAction());

        JButton submitButton = new JButton("Go! Go! Go!");
        submitButton.addActionListener(new SubmitAction());

        createLayout(lbl0, companyField, lbl1, usernameField, lbl2, passField, lbl3, folderPathField, folderButton, submitButton);

        setTitle("Login");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void createLayout(Component... arg) {

        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGap(50)
                .addGroup(gl.createParallelGroup()
                        .addComponent(arg[0])
                        .addComponent(arg[1])
                        .addComponent(arg[2])
                        .addComponent(arg[3])
                        .addComponent(arg[4])
                        .addComponent(arg[5])
                        .addComponent(arg[5])
                        .addComponent(arg[6])
                        .addGroup(gl.createSequentialGroup()
                                .addComponent(arg[7])
                                .addComponent(arg[8]))
                        .addComponent(arg[9])
                )
                .addGap(50)
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGap(50)
                .addComponent(arg[0])
                .addComponent(arg[1])
                .addComponent(arg[2])
                .addComponent(arg[3])
                .addComponent(arg[4])
                .addComponent(arg[5])
                .addComponent(arg[6])
                .addGroup(gl.createParallelGroup()
                        .addComponent(arg[7])
                        .addComponent(arg[8]))
                .addPreferredGap(UNRELATED)
                .addGap(20)
                .addComponent(arg[9])
                .addGap(50)
        );

        pack();
    }


    private class OpenFileAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {

            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Select folder for downloaded payslips");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                logger.info("Selected file: " + chooser.getSelectedFile().getAbsolutePath());
                String absolutePath = chooser.getSelectedFile().getAbsolutePath() + File.separator;
                folderPathField.setText(absolutePath);
            } else {
                logger.info("No Selection");
            }
        }
    }

    private class SubmitAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            String username = usernameField.getText();
            char[] passwd = passField.getPassword();
            String folderPath = folderPathField.getText();
            String baseUrl = companyField.getText();

            if (!username.isEmpty() && !folderPath.isEmpty() && !baseUrl.isEmpty() && passwd.length != 0) {

                try {

                    main.Processor$.MODULE$.downloadAndParse(folderPath, baseUrl, username, String.valueOf(passwd));

                    Desktop.getDesktop().open(new File(folderPath));
                    System.exit(0);

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }

            }
        }
    }
}
