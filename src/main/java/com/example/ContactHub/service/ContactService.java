package com.example.ContactHub.service;

import com.example.ContactHub.model.Contact;
import com.example.ContactHub.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactService {

    private final ContactRepository contactRepository;

    /**
     * Получить все контакты
     *
     * @return список всех контактов
     */
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    /**
     * Получить контакт по ID
     *
     * @param id идентификатор контакта
     * @return Optional с контактом или пустой Optional
     */
    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    /**
     * Создать новый контакт
     *
     * @param contact данные нового контакта
     * @return сохраненный контакт (с присвоенным ID)
     */
    @Transactional
    public Contact createContact(Contact contact) {
        if (contact.getId() != null) {
            throw new IllegalArgumentException("ID должен быть null для нового контакта");
        }
        return contactRepository.save(contact);
    }

    /**
     * Обновить существующий контакт
     *
     * @param id             идентификатор обновляемого контакта
     * @param contactDetails новые данные контакта
     * @return обновленный контакт
     * @throws RuntimeException если контакт не найден
     */
    @Transactional
    public Contact updateContact(Long id, Contact contactDetails) {
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Контакт не найден с id: " + id));
        existingContact.setPhone(contactDetails.getPhone());
        existingContact.setEmail(contactDetails.getEmail());

        return contactRepository.save(existingContact);
    }

    /**
     * Удалить контакт по ID
     * @param id идентификатор контакта
     */
    @Transactional
    public void deleteContact(Long id) {
        if (!contactRepository.existsById(id)) {
            throw new RuntimeException("Контакт не найден с id: " + id);
        }
        contactRepository.deleteById(id);
    }

    /**
     * Проверить существование контакта
     * @param id идентификатор контакта
     * @return true если контакт существует
     */
    public boolean existsById(Long id) {
        return contactRepository.existsById(id);
    }

    /**
     * Получить контакт по email
     * @param email email контакта
     * @return Optional с контактом или пустой Optional
     */
    public Optional<Contact> getContactByEmail(String email) {
        return contactRepository.findByEmail(email);
    }

    /**
     * Получить все контакты с телефоном, содержащим указанную строку
     * @param phonePart часть номера телефона
     * @return список контактов
     */
    public List<Contact> getContactsByPhoneContaining(String phonePart) {
        return contactRepository.findByPhoneContaining(phonePart);
    }

    /**
     * Удалить все контакты (осторожно!)
     */
    @Transactional
    public void deleteAllContacts() {
        contactRepository.deleteAll();
    }

    /**
     * Получить количество контактов
     * @return количество контактов в БД
     */
    public Long getContactsCount() {
        return contactRepository.count();
    }

    /**
     * Обновить только email контакта
     */
    @Transactional
    public Contact updateContactEmail(Long id, String newEmail) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Контакт не найден"));
        contact.setEmail(newEmail);
        return contact;
    }

    /**
     * Обновить только телефон контакта
     */
    @Transactional
    public Contact updateContactPhone(Long id, String newPhone) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Контакт не найден"));
        contact.setPhone(newPhone);
        return contact;
    }
}
