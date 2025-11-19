package com.example.commonmodule.enums;

public enum GameType {
  OTHELLO("오셀로"),
  CHESS("체스"),
  OMOKU("오목");

  private final String description;

  GameType(String description) {
    this.description = description;
  }
}
