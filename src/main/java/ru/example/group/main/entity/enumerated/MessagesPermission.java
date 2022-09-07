package ru.example.group.main.entity.enumerated;

public enum MessagesPermission {
  ALL,
  FRIENDS;

  public static MessagesPermission getFromBoolean(boolean isMessagePermissions) {
    return isMessagePermissions ? MessagesPermission.ALL : MessagesPermission.FRIENDS;
  }
}
