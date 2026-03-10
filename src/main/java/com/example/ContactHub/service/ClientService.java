package com.example.ContactHub.service;

import com.example.ContactHub.dto.ClientCreateDto;
import com.example.ContactHub.exception.ResourceNotFoundException;
import com.example.ContactHub.model.Client;
import com.example.ContactHub.model.Contact;
import com.example.ContactHub.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;
    private final ContactService contactService;

    /**
     * Создание нового клиента на основе данных из DTO
     *
     * @param clientDto DTO с данными клиента (имя, фамилия и опционально contactId)
     * @return сохраненный клиент с автоматически сгенерированным ID
     * @throws RuntimeException если указанный contactId не существует в базе данных
     */
    @Transactional
    public Client createClient(ClientCreateDto clientDto) {
        // Создаем нового клиента
        Client client = new Client();
        client.setName(clientDto.getName());
        client.setLastName(clientDto.getLastName());
        // Если передан contactId, находим контакт и привязываем
        if (clientDto.getContactId() != null) {
            Contact contact = contactService.getContactById(clientDto.getContactId())
                    .orElseThrow(() -> new ResourceNotFoundException("Контакт не найден с id: " + clientDto.getContactId()));
            client.setContact(contact);
        }
        return clientRepository.save(client);
    }

    /**
     * Получить всех клиентов
     * @return список всех клиентов
     */
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    /**
     * Получить клиента по ID
     * @param id идентификатор клиента
     * @return Optional с клиентом или пустой Optional
     */
    public Optional<Client>  getClientById(Long id) {
        return clientRepository.findById(id);
    }

    /**
     * Обновить существующего клиента
     */
    @Transactional
    public Client updateClient(Long id, Client clientDetails) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не найден с id: " + id));

        // Обновляем основные поля
        if (clientDetails.getName() != null) {
            existingClient.setName(clientDetails.getName());
        }

        if (clientDetails.getLastName() != null) {
            existingClient.setLastName(clientDetails.getLastName());
        }

        // Обработка контакта
        if (clientDetails.getContact() != null) {
            if (clientDetails.getContact().getId() != null) {
                // Обновляем существующий контакт
                Contact updatedContact = contactService.updateContact(
                        clientDetails.getContact().getId(),
                        clientDetails.getContact()
                );
                existingClient.setContact(updatedContact);
            } else {
                // Создаем новый контакт
                Contact newContact = contactService.createContact(clientDetails.getContact());
                existingClient.setContact(newContact);
            }
        }

        return clientRepository.save(existingClient);
    }

    /**
     * Удалить клиента по ID
     */
    @Transactional
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не найден с id: " + id));

        // Решаем, что делать с контактом при удалении клиента
        if (client.getContact() != null) {
            contactService.deleteContact(client.getContact().getId());
        }

        clientRepository.deleteById(id);
    }

    /**
     * Получить количество клиентов
     */
    public long getClientsCount() {
        return clientRepository.count();
    }

    /**
     * Найти клиентов по имени
     */
    public List<Client> getClientsByName(String name) {
        return clientRepository.findByName(name);
    }

    /**
     * Найти клиентов по фамилии
     */
    public List<Client> getClientsByLastName(String lastName) {
        return clientRepository.findByLastName(lastName);
    }

    /**
     * Найти клиента по email его контакта
     */
    public Optional<Client> getClientByContactEmail(String email) {
        return clientRepository.findByContactEmail(email);
    }

}
