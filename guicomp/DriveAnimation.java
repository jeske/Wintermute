package guicomp;

import simpleimap.Debug;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


//// Shrink/Grow Animation timer

public class DriveAnimation implements ActionListener {
    private final int LVL = 3;
    Timer t;
    int count = 0;
    CollapsePanel cp;
    DriveAnimation(CollapsePanel cp) {
        this.t = new Timer(5,this);
        this.cp = cp;
    }
    public void wakeup() {
        if (!t.isRunning()) {
            Debug.debug(LVL,"Timer Start");
            t.start();
            count = 0;
        }
    }

    public void actionPerformed(ActionEvent evt) {
        count++;
        Debug.debug(LVL,"Count: " + count);

        // if there is no work to do, shutoff the timer...
        if (!cp.doAnimate()) {
            t.stop();
            Debug.debug(LVL,"Timer Stop");
        }

    }

}
