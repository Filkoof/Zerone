package ru.example.group.main.entity.enumerated;


public enum FriendshipStatusType {
    REQUEST,
    FRIEND,
    BLOCKED,
    DECLINED,
    SUBSCRIBED,
    WASBLOCKEDBY,
    DEADLOCK;

    public static Long getIntFromEnum(FriendshipStatusType friendshipStatusType) {
        Long result;
        switch (friendshipStatusType) {
            case REQUEST:
                result = 1L;
            break;
            case FRIEND:
                result = 2L;
                break;
            case BLOCKED:
                result = 3L;
                break;
            case DECLINED:
                result = 4L;
                break;
            case SUBSCRIBED:
                result = 5L;
                break;
            case WASBLOCKEDBY:
                result = 6L;
                break;
            case DEADLOCK:
                result = 7L;
                break;
            default:
                result = 0L;
                break;
        };
        return result;
    }

    public static Long getFriendshipFromName(String friendshipStatusId) {
        Long result;
        switch (friendshipStatusId) {
            case "REQUEST":
                result = 1L;
                break;
            case "FRIEND":
                result = 2L;
                break;
            case "BLOCKED":
                result = 3L;
                break;
            case "DECLINED":
                result = 4L;
                break;
            case "SUBSCRIBED":
                result = 5L;
                break;
            case "WASBLOCKEDBY":
                result = 6L;
                break;
            case "DEADLOCK":
                result = 7L;
                break;
            default:
                result = null;
                break;
        };
        return result;
    }
}
