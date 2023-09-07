package com.example.openeyes.recorder;

import java.io.File;
import java.io.IOException;

public interface AudioPlayer {
    void playFile(File file);

    void playUrl(String url) throws IOException;

    void stop();
}
