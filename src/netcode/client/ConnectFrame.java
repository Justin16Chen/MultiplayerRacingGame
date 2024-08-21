package netcode.client;

import utils.*;

import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class ConnectFrame extends ParentFrame {

    // GUI
    private JTextField portInput;
    private JLabel errorMessageLabel;
    private String errorMessage = "Unnable to connect to server - make sure you have the right port number";
    private JButton joinServerButton;

    // networking
    protected Socket socket;
    protected ReadFromServer readFromServerRunnable;
    protected WriteToServer writeToServerRunnable;
    protected int networkInterval;
    
    // game window
    PlayerFrame playerFrame;

    public ConnectFrame(int width, int height) {
        super("Connect to Server", width, height);
    }

    @Override
    public void setupGUI() {

        // allow keyboard and mouse inputs
        this.setupKeyboardListener();
        this.setupMouseListener();

        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // setup port text input
            // port label
        gbc.gridx = 2; gbc.gridy = 1; gbc.insets = new Insets(10, 10, 10, 10);
        JLabel portLabel = new JLabel("Enter a port to join a server");
        contentPane.add(portLabel, gbc);

            // error message label
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
        joinServerButton = new JButton("Join Server!");
        joinServerButton.setPreferredSize(new Dimension(60, 35));
        joinServerButton.addActionListener(new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e) {
                int port = Integer.valueOf(portInput.getText());
                int playerID = connectToServer(port);

                // could not connect to server
                if (playerID == -1) {
                    errorMessageLabel.setText(errorMessage);
                } else {

                    // create the player frame
                    playerFrame = new PlayerFrame(readFromServerRunnable, writeToServerRunnable, networkInterval, playerID, 640, 480);

                    // close the old window
                    
                    // update the reading and writing runnables
                    readFromServerRunnable.setPlayerFrame(playerFrame);
                    writeToServerRunnable.setPlayerFrame(playerFrame);

                    // read in the starting data
                    playerFrame.getPlayerInfo();

                    // setup the window GUI
                    playerFrame.setupWindow();

                    // start the game loop
                    playerFrame.startUpdateLoop();

                    // start reading and writing data to the server
                    playerFrame.startInputOutputStreams();
                }
            }
        }); 
        contentPane.add(joinServerButton, gbc);

        // setup spacers
        gbc.gridx = 5; gbc.gridy = 5;
        contentPane.add(new JPanel(), gbc);


    }

    @Override
    public void update(int dt) {
        if (playerFrame != null) {
            dispose();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.fillRect(50, 50, 100, 50);
    }

    // connects client to server
    private int connectToServer(int port) {
        try {
            // create socket to connect to server
            socket = new Socket("localhost", port);
            System.out.println("created socket");

            // create input and output streams
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            System.out.println("created input and output streams");

            // read playerID
            int playerID = in.readInt();
            System.out.println("You are player #" + playerID);

            // read network interval
            networkInterval = in.readInt();

            // instantiate reading and writing runnables
            readFromServerRunnable = new ReadFromServer(this, in);
            writeToServerRunnable = new WriteToServer(this, out, networkInterval);

            return playerID;
        } catch (IOException e) {
            //e.printStackTrace();
            return -1;
        }
    }

    // close socket
    public void closeEverything() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (playerFrame != null) {
            playerFrame.dispose();
        }
        dispose();
    }
    public static void main(String[] args) {
        ConnectFrame client = new ConnectFrame(640, 480);
        client.setupWindow();
    }
}