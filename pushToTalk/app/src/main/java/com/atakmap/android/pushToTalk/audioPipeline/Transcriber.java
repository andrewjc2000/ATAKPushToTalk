package com.atakmap.android.pushToTalk.audioPipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.concurrent.LinkedBlockingQueue;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

public class Transcriber extends Thread {

    private File file;
    private InputStream dataStream;
    private LinkedBlockingQueue<String> queue;

    public Transcriber(File file, LinkedBlockingQueue<String> queue) {
        this.file = file;
        this.queue = queue;
    }

    public Transcriber(InputStream dataStream, LinkedBlockingQueue<String> queue) {
        this.dataStream = dataStream;
        this.queue = queue;
    }

    @Override
    public void run() {
        transcribe(dataStream);
    }

    public String transcribe(File file) {
        System.out.println("Hello World!");
        Logger cmRootLogger = Logger.getLogger("default.config");
        cmRootLogger.setLevel(java.util.logging.Level.OFF);
        String conFile = System.getProperty("java.util.logging.config.file");
        if (conFile == null) {
            System.setProperty("java.util.logging.config.file", "ignoreAllSphinx4LoggingOutput");
        }
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
        try {
            StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
            InputStream stream = new FileInputStream(file);
            recognizer.startRecognition(stream);
            SpeechResult result;
            System.out.println("Printing Results!");
            int counter = 0;
            String text = "";
            while ((result = recognizer.getResult()) != null && counter++ <= 10) {
                String temp = result.getHypothesis();
                System.out.format("Hypothesis: %s\n", temp);
                queue.put(temp + " ");
                text += temp + " ";
            }
            recognizer.stopRecognition();
            return text;
        } catch (Throwable ioe) {
            ioe.printStackTrace();
            return "";
        }
    }

    public String transcribe(InputStream stream) {
        System.out.println("Hello World!");
        Logger cmRootLogger = Logger.getLogger("default.config");
        cmRootLogger.setLevel(java.util.logging.Level.OFF);
        String conFile = System.getProperty("java.util.logging.config.file");
        if (conFile == null) {
            System.setProperty("java.util.logging.config.file", "ignoreAllSphinx4LoggingOutput");
        }
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
        try {
            StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);

            recognizer.startRecognition(stream);
            SpeechResult result;
            System.out.println("Printing Results!");
            int counter = 0;
            String text = "";
            while ((result = recognizer.getResult()) != null && counter++ <= 10) {
                String temp = result.getHypothesis();
                System.out.format("Hypothesis: %s\n", temp);
                queue.put(temp + " ");
                text += temp + " ";
            }
            recognizer.stopRecognition();
            return text;
        } catch (Throwable ioe) {
            ioe.printStackTrace();
            return "";
        }
    }
}
