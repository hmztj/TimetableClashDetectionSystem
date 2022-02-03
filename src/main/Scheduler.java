package main;

import com.formdev.flatlaf.FlatDarculaLaf;
import gui.ActivityController;
import gui.GUI;
import gui.ModuleController;
import gui.ProgramController;
import persistence.ActivityHandler;
import persistence.ModuleHandler;
import persistence.ProgramHandler;

import javax.swing.*;
import java.awt.*;

public class Scheduler {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        SwingUtilities.invokeLater(() -> {

            ProgramHandler programHandler = new ProgramHandler();
            ModuleHandler moduleHandler = new ModuleHandler();
            ActivityHandler activityHandler = new ActivityHandler();

            GUI app = new GUI();
            app.setMinimumSize(new Dimension(955, 655));
            app.pack();
            app.setLocationRelativeTo(null);
            app.setVisible(true);

            new ProgramController(app, programHandler, activityHandler);
            new ModuleController(app, moduleHandler, activityHandler);
            new ActivityController(app, activityHandler, moduleHandler);

        });
    }
}
