package com.example.ContactHub.repository;

import com.example.ContactHub.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Найти клиентов по имени (точное совпадение)
     * @param name имя клиента
     * @return список клиентов с указанным именем
     */
    List<Client> findByName(String name);

    /**
     * Найти клиентов по фамилии (точное совпадение)
     * @param lastName фамилия клиента
     * @return список клиентов с указанной фамилией
     */
    List<Client> findByLastName(String lastName);


    /**
     * Найти клиента по email его контакта
     * @param email email для поиска
     * @return Optional с клиентом
     */
    Optional<Client> findByContactEmail(String email);


}
