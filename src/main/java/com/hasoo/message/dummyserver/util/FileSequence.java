package com.hasoo.message.dummyserver.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileSequence {
  FileChannel fileChannel = null;
  String path;
  String filename;
  int maxValue;

  public FileSequence(String path, String filename, int maxValue) {
    this.path = path;
    this.filename = filename;
    if (1_000_000_000 < maxValue) {
      maxValue = 1_000_000_000;
    } else {
      this.maxValue = maxValue;

    }
  }

  public String getSequence() {

    String sequence = null;

    Path p = Util.getFilePath(this.path, this.filename);
    try (FileChannel fileChannel = FileChannel.open(p, StandardOpenOption.CREATE,
        StandardOpenOption.READ, StandardOpenOption.WRITE)) {

      FileLock lock = fileChannel.lock();
      // log.debug("lock status:{} shared:{}", lock.isValid(), lock.isShared());
      ByteBuffer buf = ByteBuffer.allocate(10);
      fileChannel.position(0);
      int readByte = fileChannel.read(buf, 0);
      if (0 > readByte) {
        putSequence(fileChannel, 2);
        return "1";
      }

      String sequenceValue = Util.convert(buf);
      int currentSeq = Integer.parseInt(sequenceValue);
      if (currentSeq == maxValue) {
        log.debug("max:{} current:{}", maxValue, currentSeq);
        currentSeq = 0;
      }
      putSequence(fileChannel, currentSeq + 1);

      sequence = Integer.valueOf(sequenceValue).toString();
    } catch (IOException ex) {
      log.error(ex.getMessage());
    }

    return sequence;
  }

  private void putSequence(FileChannel fileChannel, int value) throws IOException {
    String updatedValue = String.format("%010d", value);
    ByteBuffer buffer = ByteBuffer.wrap(updatedValue.getBytes());
    fileChannel.position(0);
    fileChannel.write(buffer);
  }

  public String readSequence() throws IOException {
    Path p = Util.getFilePath(this.path, this.filename);
    if (null == this.fileChannel) {
      this.fileChannel = FileChannel.open(p, StandardOpenOption.CREATE, StandardOpenOption.READ,
          StandardOpenOption.WRITE);
    }

    ByteBuffer buf = ByteBuffer.allocate(10);
    this.fileChannel.position(0);
    int readByte = this.fileChannel.read(buf, 0);
    if (0 > readByte) {
      writeSequence(2);
      return "1";
    }

    String sequenceValue = Util.convert(buf);
    int currentSeq = Integer.parseInt(sequenceValue);
    if (currentSeq == maxValue) {
      log.debug("max:{} current:{}", maxValue, currentSeq);
      currentSeq = 0;
    }
    writeSequence(currentSeq + 1);

    return Integer.valueOf(sequenceValue).toString();
  }

  public void close() throws IOException {
    if (null != this.fileChannel) {
      this.fileChannel.close();
    }
  }

  private String writeSequence(int value) throws IOException {
    String updatedValue = String.format("%010d", value);
    ByteBuffer buffer = ByteBuffer.wrap(updatedValue.getBytes());
    this.fileChannel.position(0);
    this.fileChannel.write(buffer);
    return updatedValue;
  }
}
