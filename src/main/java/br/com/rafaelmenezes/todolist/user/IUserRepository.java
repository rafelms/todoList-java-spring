package br.com.rafaelmenezes.todolist.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IUserRepository extends JpaRepository<UserModel, UUID> {
    /**
     * Defino JpaRepository qual objeto estou passando para
     * a criação de seus métodos e seu tipo, aqui o UUID.
     * */

    UserModel findByUsername(String username);/**
     Instanciada esse método que permite a verificação do username na tabela
     antes de cadastrar o mesmo username (validação)*/


}
