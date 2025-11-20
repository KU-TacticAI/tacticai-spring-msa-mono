package com.example.commonmodule.enums;

public enum GameType {
  OTHELLO("오셀로"),
  CHESS("체스"),
  TICTACTOE("틱택토"),
  OMOK("오목");

  private final String description;

  GameType(String description) {
    this.description = description;
  }
}
