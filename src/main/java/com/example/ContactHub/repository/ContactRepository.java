package com.example.ContactHub.repository;

import com.example.ContactHub.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    /**
     * Найти контакт по email (точное совпадение)
     *
     * @param email email контакта
     * @return Optional с контактом или пустой Optional
     */
    Optional<Contact> findByEmail(String email);

    /**
     * Найти контакты по части номера телефона (содержит указанную строку)
     *
     * @param phone часть номера телефона
     * @return список контактов, у которых телефон содержит указанную строку
     */
    List<Contact> findByPhoneContaining(String phone);

    /**
     * Проверить существование контакта по ID
     *
     * @param id идентификатор контакта
     * @return true если контакт существует
     */
    boolean existsById(Long id);

    /**
     * Найти контакт по номеру телефона (точное совпадение)
     *
     * @param phone номер телефона
     * @return Optional с контактом или пустой Optional
     */
    Optional<Contact> findByPhone(String phone);

    /**
     * Найти контакты по email (частичное совпадение, без учета регистра)
     *
     * @param email часть email
     * @return список контактов
     */
    List<Contact> findByEmailContainingIgnoreCase(String email);

    /**
     * Удалить контакт по email
     *
     * @param email email контакта
     */
    void deleteByEmail(String email);

}
