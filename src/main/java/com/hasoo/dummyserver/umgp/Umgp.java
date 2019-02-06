package com.hasoo.dummyserver.umgp;

import lombok.Data;

@Data
public class Umgp {
  /* @formatter:off */
  public static final String BEGIN          = "BEGIN";
  public static final String CONNECT        = "CONNECT";
  public static final String SEND           = "SEND";
  public static final String MMS            = "MMS";
  public static final String REPORT         = "REPORT";
  public static final String PING           = "PING";
  public static final String PONG           = "PONG";
  public static final String ACK            = "ACK";
  public static final String END            = "END";
  
  public static final String ID             = "ID";
  public static final String PASSWORD       = "PASSWORD";
  public static final String REPORTLINE     = "REPORTLINE";
  public static final String VERSION        = "VERSION";
  public static final String SUBJECT        = "SUBJECT";
  public static final String RECEIVERNUM    = "RECEIVERNUM";
  public static final String CALLBACK       = "CALLBACK";
  public static final String CODE           = "CODE";
  public static final String DATA           = "DATA";
  public static final String KEY            = "KEY";
  public static final String DATE           = "DATE";
  public static final String NET            = "NET";
  public static final String IMAGE          = "IMAGE";
  public static final String CONTENTTYPE    = "CONTENTTYPE";
  /* @formatter:on */

  public enum HType {
    CONNECT, SEND, MMS, REPORT, PING, PONG, ACK
  }

  private String id;
  private String password;
  private String reportline;
  private String version;
  private String subject;
  private String receivernum;
  private String callback;
  private StringBuffer data = new StringBuffer();
  private String key;
  private String date;
  private String net;
  private String code;
  private StringBuffer image = new StringBuffer();
  private String contenttype;
  private String agent;
  private String ttl;

  private boolean completedBegin = false;
  private boolean completedEnd = false;

  public void reset() {
    this.id = "";
    this.password = "";
    this.reportline = "";
    this.version = "";
    this.subject = "";
    this.receivernum = "";
    this.callback = "";
    this.data.setLength(0);
    this.key = "";
    this.date = "";
    this.net = "";
    this.code = "";
    this.image.setLength(0);
    this.contenttype = "";
    this.agent = "";
    this.ttl = "";

    this.completedBegin = false;
    this.completedEnd = false;
  }

  public HType parseHeaderPart(String buf) {
    String[] headerPart = buf.split(" ");
    if (2 != headerPart.length) {
      throw new RuntimeException(String.format("invalid packet[%s]", buf));
    }

    String umgpSeperator = headerPart[0], umgpMethod = headerPart[1];
    if (true != umgpSeperator.equals(BEGIN)) {
      throw new RuntimeException(String.format("invalid packet[%s]", buf));
    }

    HType ret;
    if (umgpMethod.equals(CONNECT)) {
      ret = HType.CONNECT;
    } else if (umgpMethod.equals(SEND)) {
      ret = HType.SEND;
    } else if (umgpMethod.equals(REPORT)) {
      ret = HType.REPORT;
    } else if (umgpMethod.equals(MMS)) {
      ret = HType.MMS;
    } else if (umgpMethod.equals(PING)) {
      ret = HType.PING;
    } else if (umgpMethod.equals(PONG)) {
      ret = HType.PONG;
    } else if (umgpMethod.equals(ACK)) {
      ret = HType.ACK;
    } else {
      throw new RuntimeException(String.format("invalid packet[%s]", buf));
    }

    this.completedBegin = true;

    return ret;
  }

  public void parseDataPart(String buf) {
    if (buf.equals(END)) {
      if (0 < this.data.length()) {
        this.data.deleteCharAt(this.data.length() - 1);
      }
      this.completedEnd = true;
      return;
    }

    String[] dataPart = buf.split(":");
    if (2 != dataPart.length) {
      throw new RuntimeException(String.format("invalid packet[%s]", buf));
    }

    String umgpFieldName = dataPart[0], umgpValue = dataPart[1];
    if (umgpFieldName.equals(ID)) {
      this.id = umgpValue;
    } else if (umgpFieldName.equals(PASSWORD)) {
      this.password = umgpValue;
    } else if (umgpFieldName.equals(REPORTLINE)) {
      if (!umgpValue.equals("Y") && !umgpValue.equals("N")) {
        throw new RuntimeException(String.format("invalid packet[%s]", buf));
      }
      this.reportline = umgpValue;
    } else if (umgpFieldName.equals(VERSION)) {
      this.version = umgpValue;
    } else if (umgpFieldName.equals(SUBJECT)) {
      this.subject = umgpValue;
    } else if (umgpFieldName.equals(RECEIVERNUM)) {
      this.receivernum = umgpValue;
    } else if (umgpFieldName.equals(CALLBACK)) {
      this.callback = umgpValue;
    } else if (umgpFieldName.equals(CODE)) {
      this.code = umgpValue;
    } else if (umgpFieldName.equals(KEY)) {
      this.key = umgpValue;
    } else if (umgpFieldName.equals(DATE)) {
      this.date = umgpValue;
    } else if (umgpFieldName.equals(NET)) {
      this.net = umgpValue;
    } else if (umgpFieldName.equals(CONTENTTYPE)) {
      this.contenttype = umgpValue;
    } else if (umgpFieldName.equals(DATA)) {
      this.data.append(umgpValue);
      this.data.append('\n');
    } else if (umgpFieldName.equals(IMAGE)) {
      this.image.append(umgpValue);
    } else {
      throw new RuntimeException(String.format("invalid packet[%s]", buf));
    }
  }

  public static String headerPart(String part) {
    return Umgp.BEGIN + " " + part + "\r\n";
  }

  public static String dataPart(String field, String value) {
    return field + ":" + value + "\r\n";
  }

  public static String end() {
    return Umgp.END + "\r\n";
  }
}
