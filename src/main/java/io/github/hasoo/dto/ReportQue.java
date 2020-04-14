package io.github.hasoo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportQue {
  String key;
  String code;
  String data;
  String date;
  String net;
}
