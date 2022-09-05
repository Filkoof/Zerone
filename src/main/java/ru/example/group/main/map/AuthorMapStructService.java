package ru.example.group.main.map;

import org.springframework.stereotype.Service;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.MessagesPermission;
@Service
public class AuthorMapStructService {
  public MessagesPermission getMessagePermission(UserEntity user){
    return user.isMessagePermissions()?MessagesPermission.ALL:MessagesPermission.FRIENDS;
  }
}
