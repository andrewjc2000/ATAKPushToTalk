/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * NOTE: This class is hidden usually, but it will be
 * helpful in creating a wave header. I found this file at:
 * https://android.googlesource.com/platform/frameworks/base/+/android-4.4_r1/core/java/android/speech/srec/WaveHeader.java
 */
//package android.speech.srec;

package com.atakmap.android.pushToTalk.audioPipeline;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

/**
 * This class represents the header of a WAVE format audio file, which usually
 * have a .wav suffix.  The following integer valued fields are contained:
 * <ul>
 * <li> format - usually PCM, ALAW or ULAW.
 * <li> numChannels - 1 for mono, 2 for stereo.
 * <li> sampleRate - usually 8000, 11025, 16000, 22050, or 44100 hz.
 * <li> bitsPerSample - usually 16 for PCM, 8 for ALAW, or 8 for ULAW.
 * <li> numBytes - size of audio data after this header, in bytes.
 * </ul>
 *
 * Not yet ready to be supported, so
 */
public class WaveHeader {

    // follows WAVE format in http://ccrma.stanford.edu/courses/422/projects/WaveFormat
    private static final String TAG = "WaveHeader";

    private static final int HEADER_LENGTH = 44;

    /** Indicates PCM format. */
    public static final short FORMAT_PCM = 1;

    private short mFormat;
    private short mNumChannels;
    private int mSampleRate;
    private short mBitsPerSample;
    private int mNumBytes;

    /**
     * Construct a WaveHeader, with fields initialized.
     * @param numChannels 1 for mono, 2 for stereo.
     * @param sampleRate typically 8000, 11025, 16000, 22050, or 44100 hz.
     * @param bitsPerSample usually 16 for PCM, 8 for ULAW or 8 for ALAW.
     * @param numBytes size of audio data after this header, in bytes.
     */
    public WaveHeader(short numChannels, int sampleRate, short bitsPerSample, int numBytes) {
        mFormat = FORMAT_PCM;
        mSampleRate = sampleRate;
        mNumChannels = numChannels;
        mBitsPerSample = bitsPerSample;
        mNumBytes = numBytes;
    }

    /**
     * Write a WAVE file header.
     * @param out {@link java.io.OutputStream} to receive the header.
     * @return number of bytes written.
     * @throws IOException
     */
    public int write(OutputStream out) throws IOException {
        /* RIFF header */
        writeId(out, "RIFF");
        writeInt(out, 36 + mNumBytes);
        writeId(out, "WAVE");
        /* fmt chunk */
        writeId(out, "fmt ");
        writeInt(out, 16);
        writeShort(out, mFormat);
        writeShort(out, mNumChannels);
        writeInt(out, mSampleRate);
        writeInt(out, mNumChannels * mSampleRate * mBitsPerSample / 8);
        writeShort(out, (short)(mNumChannels * mBitsPerSample / 8));
        writeShort(out, mBitsPerSample);
        /* data chunk */
        writeId(out, "data");
        writeInt(out, mNumBytes);

        return HEADER_LENGTH;
    }
    private static void writeId(OutputStream out, String id) throws IOException {
        for (int i = 0; i < id.length(); i++) {
            out.write(id.charAt(i));
        }
    }
    private static void writeInt(OutputStream out, int val) throws IOException {
        out.write(val);
        out.write(val >> 8);
        out.write(val >> 16);
        out.write(val >> 24);
    }
    private static void writeShort(OutputStream out, short val) throws IOException {
        out.write(val);
        out.write(val >> 8);
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH,
            "WaveHeader format=%d numChannels=%d sampleRate=%d bitsPerSample=%d numBytes=%d",
            mFormat, mNumChannels, mSampleRate, mBitsPerSample, mNumBytes
        );
    }
}
