package utils;
import javax.swing.*;

import gameplay.*;
import input.*;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.awt.*;

public abstract class ParentFrame extends JFrame {

    // properties of the JFrame
    private int width, height;
    private Container contentPane;

    // input
    private KeyInput keyInput;
    private MouseInput mouseInput;

    // update loop
    public int FPS = 60;
    private Timer updateLoopTimer;

    // draw to window
    DrawingComponent dc;

    public ParentFrame(int width, int height) {
        this.width = width;
        this.height = height;

        contentPane = new Container();
    }

    public void setupWindow() {
        contentPane = this.getContentPane();
        contentPane.setPreferredSize(new Dimension(width, height));
        
        // allow using Graphics2D to draw to window
        dc = new DrawingComponent();
        contentPane.add(dc);

        // setup the GUI for window
        setupGUI();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }
    public abstract void setupGUI();
    
    // adds input listeners to the window
    public void setupKeyboardListener() {
        keyInput = new KeyInput();
        this.addKeyListener(keyInput);
        this.setFocusable(true);
    }
    public void setupMouseListener() {
        mouseInput = new MouseInput();
        this.addMouseListener(mouseInput);
        this.setFocusable(true);
    }

    // update loop
    public void startUpdateLoop() {
        // in milliseconds (ms) (60 FPS = 1000/60)
        int interval = (int) (1000/FPS);
        int secondsInterval = interval * 1000;

        // function that gets called once every [interval] ms
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                // update input
                if (keyInput != null) {
                    keyInput.update();
                }
                if (mouseInput != null) {
                    mouseInput.update();
                }

                // call update function
                update(secondsInterval);
            }
        };

        // timer class calls the actionPerformed function in ActionListener class once every [interval] ms
        updateLoopTimer = new Timer(interval, al);
        updateLoopTimer.start();
    }
    public abstract void update(int dt);

    // draw loop
    private class DrawingComponent extends JComponent {
         
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            draw(g2);
        }
    }
    public abstract void draw(Graphics2D g2);

}
