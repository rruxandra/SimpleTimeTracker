package com.github.rruxandra.simpletimetracker;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.Instant;

public class SimpleTimeTracker extends JFrame {

    private JButton buttonStartStop;
    private JLabel labelTime;
    private Timer timer, inactivityTimer;
    private Instant startTime;
    private Duration totalTime = Duration.ZERO;
    private boolean timing;

    private static final int INACTIVITY_LIMIT_MS = 3000;

    public SimpleTimeTracker() {
        createUI();
        setupTimers();
    }

    private void createUI() {
        setTitle("Time Tracker");
        setSize(300, 100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buttonStartStop = new JButton("Start");
        labelTime = new JLabel("Total Time: 0 seconds");

        buttonStartStop.addActionListener(e -> toggleTiming());

        JPanel panel = new JPanel();
        panel.add(buttonStartStop);
        panel.add(labelTime);
        add(panel);

        // TODO fix listeners
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                resetInactivityTimer();
            }
        });
    }

    private void setupTimers() {
        timer = new Timer(1000, e -> updateTimer());
        inactivityTimer = new Timer(INACTIVITY_LIMIT_MS, e -> onInactivity());
        inactivityTimer.setRepeats(false);
    }

    private void toggleTiming() {
        if (!timing) {
            startTiming();
        } else {
            stopTiming();
        }
    }

    private void startTiming() {
        timing = true;
        startTime = Instant.now();
        buttonStartStop.setText("Stop");
        timer.start();
        inactivityTimer.start();
    }

    private void stopTiming() {
        timing = false;
        totalTime = totalTime.plus(Duration.between(startTime, Instant.now()));
        buttonStartStop.setText("Start");
        timer.stop();
        inactivityTimer.stop();
        updateDisplayTime();
    }

    private void resetInactivityTimer() {
        if (timing) {
            inactivityTimer.restart();
        }
    }

    private void updateTimer() {
        updateDisplayTime();
        resetInactivityTimer();
    }

    private void updateDisplayTime() {
        Duration currentDuration = Duration.ZERO;
        if (timing) {
            currentDuration = Duration.between(startTime, Instant.now());
        }
        Duration displayTime = totalTime.plus(currentDuration);
        labelTime.setText("Total Time: " + displayTime.getSeconds() + " seconds");
    }

    private void onInactivity() {
        if (timing) {
            stopTiming();
            int option = JOptionPane.showConfirmDialog(this,
                    "You've been inactive for 5 minutes. Do you want to continue tracking this time?",
                    "Inactivity Detected", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                totalTime = totalTime.plus(Duration.ofMinutes(5));
                startTiming();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimpleTimeTracker tracker = new SimpleTimeTracker();
            tracker.setVisible(true);
        });
    }
}