package com.example.commonmodule.enums;

public enum Tier {
  BRONZE(1),
  SILVER(2),
  GOLD(3),
  PLATINUM(4),
  DIAMOND(5);

  private final int rank;

  Tier(int rank) {
    this.rank = rank;
  }

  public int getRank() {
    return rank;
  }

  public static Tier fromString(String value) {
    for (Tier tier : values()) {
      if (tier.name().equalsIgnoreCase(value)) {
        return tier;
      }
    }
    throw new IllegalArgumentException("Invalid tier: " + value);
  }
}
