package br.com.rafaelmenezes.todolist.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data // Define tanto os Getters e Setters destas instâncias
@Entity(name="tb_users")
public class UserModel {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id; // modelo de geração de IDs aleatórios

    @Column(unique = true, nullable = false)
    private String username;

    private String name;
    private String password;

    @CreationTimestamp/**
     Ela instrui o Hibernate a gerar automaticamente a data e a hora
     atual no momento exato em que o registro for inserido no banco
     de dados pela primeira vez.
     */
    private LocalDateTime createdAt;
}
