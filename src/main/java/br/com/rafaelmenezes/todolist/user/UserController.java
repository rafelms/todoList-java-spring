package br.com.rafaelmenezes.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @org.springframework.stereotype.Controller
 * geralmente utilizada para retorno
 * de páginas ou diversas coisas, maior flexibilidade.
 */

@RestController /** usada qnd o foco é a criação de uma API */
@RequestMapping("/users")
public class UserController {
    /**
     * Métodos de acesso do HTTP
     * GET - Buscar uma informação
     * POST - Adicionar um dado/informação
     * PUT - Alterar dado/info
     * DELETE - Remover um dado
     * PATCH - Alterar somente uma parte da info/dado
     */

    @Autowired //Faz com que o Spring gerencie todo o ciclo de vida desta instância
    private IUserRepository userRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel){
        /**
         *  A anotação @RequestBody indica ao Spring que ele
         *  deve converter o corpo da requisição HTTP
         *  (que geralmente vem em formato JSON) em um objeto
         *  Java automaticamente.
         *  Esse processo é chamado de Desserialização.
         * */

        var user = this.userRepository.findByUsername(userModel.getUsername());/**
         Faz a verificação se já existe o username criado dentro da tabela*/
        
        //Condição quando houver erro na validação
        if(user != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe.");
        }

        var passwordHashred = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());/**
         cost = força da senha - 12 é o indicado na documentação
         para usar o método hashToString é necessário que seja passado um char Array,
         logo transformamos a String ao final da linha.
         */

        userModel.setPassword(passwordHashred);

        //Condição quando a validação der certo e cadastra o usuário no DB.
        var userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);

    }
}
