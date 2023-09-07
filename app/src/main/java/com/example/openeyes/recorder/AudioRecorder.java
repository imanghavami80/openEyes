package com.example.openeyes.recorder;

import java.io.File;
import java.io.IOException;

public interface AudioRecorder {
    void start(File outputFile) throws IOException;

    void stop();
}
