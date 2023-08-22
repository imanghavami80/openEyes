package com.example.openeyes.recorder;

import java.io.File;

public interface AudioPlayer {
    void playFile(File file);
    void stop();
}
