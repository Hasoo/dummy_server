package com.hasoo.message.dummyserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.hasoo.dummyserver.umgp.Umgp;

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

    assertThrows(RuntimeException.class, () -> {
      umgp.parseDataPart("BGIN CONNECT");
    });

    assertThrows(RuntimeException.class, () -> {
      umgp.parseDataPart("BIGINCONNECT");
    });
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

    umgp.parseDataPart("VERSION:UMGP/1.0");
    assertEquals("UMGP/1.0", umgp.getVersion());

    umgp.parseDataPart("SUBJECT:MMSSUBJECT");
    assertEquals("MMSSUBJECT", umgp.getSubject());

    umgp.parseDataPart("RECEIVERNUM:01012341234");
    assertEquals("01012341234", umgp.getReceivernum());

    umgp.parseDataPart("CALLBACK:01022222222");
    assertEquals("01022222222", umgp.getCallback());

    umgp.parseDataPart("CODE:100");
    assertEquals("100", umgp.getCode());

    umgp.parseDataPart("KEY:1");
    assertEquals("1", umgp.getKey());

    umgp.parseDataPart("DATE:20190101091011");
    assertEquals("20190101091011", umgp.getDate());

    umgp.parseDataPart("NET:SKT");
    assertEquals("SKT", umgp.getNet());

    umgp.parseDataPart("CONTENTTYPE:TXT");
    assertEquals("TXT", umgp.getContenttype());

    umgp.parseDataPart("DATA:testmessage");
    assertEquals("testmessage\n", umgp.getData().toString());

    umgp.parseDataPart("DATA:hi");
    assertEquals("testmessage\nhi\n", umgp.getData().toString());

    umgp.parseDataPart("IMAGE:base64");
    assertEquals("base64", umgp.getImage().toString());

    umgp.parseDataPart("END");
    Assertions.assertTrue(umgp.isCompletedEnd());

    assertEquals("testmessage\nhi", umgp.getData().toString());
  }

  @Test
  public void testCreationPacket() {
    StringBuilder packet = new StringBuilder();
    packet.append(Umgp.headerPart(Umgp.CONNECT));
    packet.append(Umgp.dataPart(Umgp.ID, "test"));
    packet.append(Umgp.dataPart(Umgp.PASSWORD, "testpwd"));
    packet.append(Umgp.dataPart(Umgp.REPORTLINE, "N"));
    packet.append(Umgp.dataPart(Umgp.VERSION, "SMGP/2.0.5"));
    packet.append(Umgp.end());
    assertEquals(
        "BEGIN CONNECT\r\nID:test\r\nPASSWORD:testpwd\r\nREPORTLINE:N\r\nVERSION:SMGP/2.0.5\r\nEND\r\n",
        packet.toString());
  }

}
