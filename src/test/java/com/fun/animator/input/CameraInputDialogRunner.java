package com.fun.animator.input;

import javax.swing.*;

public class CameraInputDialogRunner {

    public static void main(String [] argv) {
        SimpleCameraInputDialog dialog = new SimpleCameraInputDialog(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setVisible(true);
    }
}
