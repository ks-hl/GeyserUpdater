package com.projectg.geyserupdater.standalone.utils;

import javax.annotation.Nullable;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtil {
    public static String checksum(File file) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("SHA-256 algorithm not found");
        }
        try (FileInputStream fis = new FileInputStream(file)) {

            byte[] byteArray = new byte[1024];

            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }

        byte[] bytes = digest.digest();
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Nullable
    public static String readZipFile(File zip, String file) throws IOException {
        try (ZipFile zipFile = new ZipFile(zip)) {
            Enumeration<? extends ZipEntry> e = zipFile.entries();

            while (e.hasMoreElements()) {
                ZipEntry entry = e.nextElement();
                if (entry.isDirectory() || !entry.getName().equals(file)) continue;

                try (BufferedReader br = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                        sb.append('\n');
                    }
                    return sb.toString();
                }
            }
        }
        return null;
    }

    @Nullable
    public static String getPluginVersion(File jar) {
        String contents = null;
        try {
            contents = readZipFile(jar, "plugin.yml");
        } catch (IOException ignored) {
        }
        if (contents == null) try {
            contents = readZipFile(jar, "resources/plugin.yml");
        } catch (IOException ignored) {
        }
        if (contents == null) return null;
        Matcher matcher = Pattern.compile("version: ([^\n\r]+)").matcher(contents);
        if (!matcher.find()) return null;
        return matcher.group(1);
    }

    @Nullable
    public static String getCommitID(File jar) {
        String contents = null;
        try {
            contents = readZipFile(jar, "git.properties");
        } catch (IOException ignored) {
        }
        if (contents == null) return null;
        Matcher matcher = Pattern.compile("git\\.commit\\.id=([0-9a-f]+)").matcher(contents);
        if (!matcher.find()) return null;
        return matcher.group(1);
    }
}
