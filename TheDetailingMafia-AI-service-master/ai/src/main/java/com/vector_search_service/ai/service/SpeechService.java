package com.vector_search_service.ai.service;

import com.google.cloud.speech.v1.*;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class SpeechService {

    private static final String AUDIO_OUTPUT = "output.mp3";

    // 🔊 Text-to-Speech
    public String textToSpeech(String text) throws IOException {
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Input text
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

            // Voice configuration
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US")
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();

            // Use MP3 encoding (playable in browser)
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            // Request synthesis
            SynthesizeSpeechResponse response =
                    textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Save to file
            try (FileOutputStream out = new FileOutputStream(AUDIO_OUTPUT)) {
                out.write(response.getAudioContent().toByteArray());
                System.out.println("✅ Speech synthesized to file: " + AUDIO_OUTPUT);
            }

            return AUDIO_OUTPUT;
        }
    }


    public byte[] recordFromMicrophone(int seconds) throws Exception {
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            throw new RuntimeException("Microphone not supported");
        }

        TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(format);
        microphone.start();

        System.out.println("🎤 Recording... Speak now!");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;

        long end = System.currentTimeMillis() + seconds * 1000;
        while (System.currentTimeMillis() < end) {
            bytesRead = microphone.read(buffer, 0, buffer.length);
            out.write(buffer, 0, bytesRead);
        }

        microphone.stop();
        microphone.close();
        System.out.println("🎤 Recording stopped");

        return out.toByteArray();
    }

    public String speechBytesToText(byte[] audioBytes) throws Exception {
        ByteString audioData = ByteString.copyFrom(audioBytes);

        // 🔍 Detect WebM/Opus (browser recording) vs WAV/PCM
        boolean isWebM = false;
        if (audioBytes.length > 12) {
            String header = new String(audioBytes, 0, 12);
            if (header.contains("webm") || header.contains("matroska")) {
                isWebM = true;
            }
        }

        RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder()
                .setContent(audioData)
                .build();

        RecognitionConfig.Builder configBuilder = RecognitionConfig.newBuilder()
                .setLanguageCode("en-US");

        // 🎧 Adjust based on detected format
        if (isWebM) {
            System.out.println("🎧 Detected WebM/Opus audio (from browser)");
            configBuilder
                    .setEncoding(RecognitionConfig.AudioEncoding.WEBM_OPUS)
                    .setSampleRateHertz(48000);
        } else {
            System.out.println("🎧 Detected WAV/PCM audio");
            configBuilder
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000);
        }

        RecognitionConfig config = configBuilder.build();

        try (SpeechClient speechClient = SpeechClient.create()) {
            RecognizeResponse response = speechClient.recognize(config, recognitionAudio);
            StringBuilder transcript = new StringBuilder();
            for (SpeechRecognitionResult result : response.getResultsList()) {
                transcript.append(result.getAlternativesList().get(0).getTranscript());
            }

            String text = transcript.toString().trim();
            System.out.println("📝 Google Speech result: " + text);
            return text.isEmpty() ? "No speech recognized." : text;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Speech-to-text failed: " + e.getMessage());
        }
    }
}