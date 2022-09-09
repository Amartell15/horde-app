package Horde;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;

class TrayIconClass {


    public static void setupTray(Controller controller) throws AWTException {
        // Start Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        ImageIcon imageIcon = null;
        try {
            imageIcon = new ImageIcon((new File("src/main/resources/HordeUI.jpg")).toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        final TrayIcon trayIcon = new TrayIcon(imageIcon.getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT));
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a pop-up menu components
        MenuItem aboutItem = new MenuItem("About");
        MenuItem stop = new MenuItem("Stop");
        MenuItem start = new MenuItem("Start");
        MenuItem pause = new MenuItem("Pause");
        MenuItem skip = new MenuItem("Skip");
        MenuItem random = new MenuItem("Random");

        //Add components to pop-up menu
        popup.add(aboutItem);
        popup.addSeparator();
        popup.addSeparator();
        popup.add(start);
        popup.add(stop);
        popup.add(pause);
        popup.add(skip);
        popup.add(random);

        trayIcon.setPopupMenu(popup);

        //set command names and actionListener
        aboutItem.setActionCommand("About");
        aboutItem.addActionListener(actionEvent -> System.out.println(actionEvent.getActionCommand()));
        start.addActionListener(actionEvent -> {
            controller.startTimer(null);
        });
        stop.addActionListener(actionEvent -> {
            controller.stopTimer(null);
        });
        pause.addActionListener(actionEvent -> {
            controller.pauseTimer(null);

        });
        skip.addActionListener(actionEvent -> {
            controller.skipButton(null);

        });
        random.addActionListener(actionEvent -> {
            controller.randomButton(null);

        });

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }

    //Obtain the image URL
    protected static java.awt.Image createImage(String path, String description) {
        return (new ImageIcon(path, description)).getImage();
    }
    //end copyright material
}