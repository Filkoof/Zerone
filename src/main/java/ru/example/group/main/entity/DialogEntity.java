package ru.example.group.main.entity;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "sender_id")
  private UserEntity senderId;

  @ManyToOne
  @JoinColumn(name = "recipient_id")
  private UserEntity recipientId;

  @OneToMany(mappedBy = "dialog")
  private Set<MessageEntity> messageSet;


//  @ManyToMany
//  @JoinTable(name = "dialog_user_entities",
//      joinColumns = @JoinColumn(name = "dialog_id"),
//      inverseJoinColumns = @JoinColumn(name = "user_id"))
//  private Set<UserEntity> recipientId = new LinkedHashSet<>();

}
