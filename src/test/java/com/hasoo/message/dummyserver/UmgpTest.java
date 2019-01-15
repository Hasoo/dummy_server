package com.hasoo.message.dummyserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.hasoo.message.dummyserver.umgp.Umgp;

public class UmgpTest {
  private Umgp umgp = new Umgp();

  @Test
  public void testHeader() {
    Assertions.assertFalse(umgp.isCompletedBegin());
    Assertions.assertTrue(Umgp.HType.CONNECT == umgp.parseHeaderPart("BEGIN CONNECT"));
    Assertions.assertTrue(Umgp.HType.SEND == umgp.parseHeaderPart("BEGIN SEND"));
    Assertions.assertTrue(Umgp.HType.MMS == umgp.parseHeaderPart("BEGIN MMS"));
    Assertions.assertTrue(Umgp.HType.PING == umgp.parseHeaderPart("BEGIN PING"));
    Assertions.assertTrue(Umgp.HType.PONG == umgp.parseHeaderPart("BEGIN PONG"));
    Assertions.assertTrue(Umgp.HType.ACK == umgp.parseHeaderPart("BEGIN ACK"));
    Assertions.assertTrue(umgp.isCompletedBegin());
  }

  @Test
  public void testData() {
    Assertions.assertFalse(umgp.isCompletedEnd());

    umgp.parseDataPart("ID:test");
    assertEquals("test", umgp.getId());

    umgp.parseDataPart("PASSWORD:test123");
    assertEquals("test123", umgp.getPassword());

    umgp.parseDataPart("REPORTLINE:Y");
    assertEquals("Y", umgp.getReportline());

    umgp.parseDataPart("REPORTLINE:N");
    assertEquals("N", umgp.getReportline());

    assertThrows(RuntimeException.class, () -> {
      umgp.parseDataPart("REPORTLINE:y");
    });

    umgp.parseDataPart("END");
    Assertions.assertTrue(umgp.isCompletedEnd());
  }

}
