package com.example.commonmodule.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AiStatus {
  READY,
  RUNNING,
  ERROR
}