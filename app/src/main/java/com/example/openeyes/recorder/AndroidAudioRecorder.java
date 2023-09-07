package com.example.openeyes.recorder;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AndroidAudioRecorder implements AudioRecorder {

    private Context context;
    private MediaRecorder recorder;

    public AndroidAudioRecorder(Context context) {
        this.context = context;
        this.recorder = null;

    }

    private MediaRecorder createRecorder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            return new MediaRecorder(this.context);
        return new MediaRecorder();

    }

    @Override
    public void start(File outputFile) throws IOException {
        recorder = createRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(new FileOutputStream(outputFile).getFD());
        recorder.prepare();
        recorder.start();

    }

    @Override
    public void stop() {
        if (recorder != null) {
            recorder.stop();
            recorder.reset();
            recorder = null;

        }
    }
}
