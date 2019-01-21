package com.hasoo.message.dummyserver.util;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileSequence {
  private FileChannel fileChannel = null;
  private File sequenceFile;
  int maxValue;

  public FileSequence(File sequenceFile, int maxValue) {
    this.sequenceFile = sequenceFile;
    if (1_000_000_000 < maxValue) {
      maxValue = 1_000_000_000;
    } else {
      this.maxValue = maxValue;
    }
  }

  public String getSequence() {
    sequenceFile.getParentFile().mkdirs();
    try (FileChannel fileChannel = FileChannel.open(this.sequenceFile.toPath(),
        StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)) {

      FileLock lock = fileChannel.lock();
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
        currentSeq = 0;
      }
      putSequence(fileChannel, currentSeq + 1);

      return Integer.valueOf(sequenceValue).toString();
    } catch (IOException ex) {
      log.error(ex.getMessage());
    }

    return null;
  }

  private void putSequence(FileChannel fileChannel, int value) throws IOException {
    String updatedValue = String.format("%010d", value);
    ByteBuffer buffer = ByteBuffer.wrap(updatedValue.getBytes());
    fileChannel.position(0);
    fileChannel.write(buffer);
  }

  public synchronized String readSequence() throws IOException {
    sequenceFile.getParentFile().mkdirs();
    if (null == this.fileChannel) {
      this.fileChannel = FileChannel.open(this.sequenceFile.toPath(), StandardOpenOption.CREATE,
          StandardOpenOption.READ, StandardOpenOption.WRITE);
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
      currentSeq = 0;
    }
    writeSequence(currentSeq + 1);

    return Integer.valueOf(sequenceValue).toString();
  }

  public synchronized void close() throws IOException {
    if (null != this.fileChannel) {
      this.fileChannel.close();
    }
  }

  private synchronized String writeSequence(int value) throws IOException {
    String updatedValue = String.format("%010d", value);
    ByteBuffer buffer = ByteBuffer.wrap(updatedValue.getBytes());
    this.fileChannel.position(0);
    this.fileChannel.write(buffer);
    return updatedValue;
  }
}
