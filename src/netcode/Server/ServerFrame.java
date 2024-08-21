package netcode.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import utils.*;

public class ServerFrame extends JFrame {

    private Server server;
    private int width, height;
    
    private Container contentPane;

    JTextField portInput;
    JLabel errorMessageLabel;
    String errorMessage = "Error when connecting to port - try a different port from 1024-49151";
    JButton startServerButton;

    public ServerFrame(Server server, int width, int height) {
        this.server = server;
        this.width = width;
        this.height = height;

        contentPane = this.getContentPane();
        this.setTitle("Server");
        contentPane.setPreferredSize(new Dimension(width, height));

        setupFrameLayout();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    // setup the gui layout
    private void setupFrameLayout() {
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // setup port text input
        gbc.gridx = 2; gbc.gridy = 1; gbc.insets = new Insets(10, 10, 10, 10);
        JLabel portLabel = new JLabel("Enter a port to create a server");
        contentPane.add(portLabel, gbc);

        // setup error message label
        gbc.gridx = 2; gbc.gridy = 2; gbc.insets = new Insets(0, 0, 0, 0);
        errorMessageLabel = new JLabel();
        errorMessageLabel.setForeground(Color.RED);
        errorMessageLabel.setVisible(true); // Initially hidden
        contentPane.add(errorMessageLabel, gbc);

        // actual text input
        gbc.gridx = 2; gbc.gridy = 3; gbc.insets = new Insets(10, 10, 10, 10);
        portInput = new JTextField("1234");
        portInput.setPreferredSize(new Dimension(75, 25));
        ((AbstractDocument) portInput.getDocument()).setDocumentFilter(new NumberFilter());
        portInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onTextChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onTextChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}

            private void onTextChanged() {
                errorMessageLabel.setText("");
            }
        });
        contentPane.add(portInput, gbc);

        // setup start server button
        gbc.gridx = 2; gbc.gridy = 4; gbc.insets = new Insets(10, 10, 10, 10);
        startServerButton = new JButton("Start Server!");
        startServerButton.setPreferredSize(new Dimension(60, 35));
        startServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int portNumber = Integer.parseInt(portInput.getText());
                boolean successful = server.createServer(portNumber);
                if (successful) {
                    server.start();
                } else {
                    errorMessageLabel.setText(errorMessage);
                }
            }
        });
        contentPane.add(startServerButton, gbc);

        // setup spacers
        gbc.gridx = 5; gbc.gridy = 5;
        contentPane.add(new JPanel(), gbc);
    }
}