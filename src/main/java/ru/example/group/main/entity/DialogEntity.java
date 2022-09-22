package ru.example.group.main.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "dialogs")
public class DialogEntity {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "sender_id")
  private UserEntity sender;

  @ManyToOne
  @JoinColumn(name = "recipient_id")
  private UserEntity recipient;

  @OneToMany(mappedBy = "dialog")
  private List<MessageEntity> messageSet=new ArrayList<>();

}
