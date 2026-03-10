package com.example.ContactHub.service;

import com.example.ContactHub.dto.ClientCreateDto;
import com.example.ContactHub.exception.ResourceNotFoundException;
import com.example.ContactHub.model.Client;
import com.example.ContactHub.model.Contact;
import com.example.ContactHub.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ContactService contactService;

    @InjectMocks
    private ClientService clientService;

    private Contact contact1;
    private Contact contact2;
    private Client client1;
    private Client client2;
    private ClientCreateDto clientCreateDto;

    @BeforeEach
    void setUp() {
        // Подготовка контактов
        contact1 = new Contact();
        contact1.setId(1L);
        contact1.setPhone("+79601112222");
        contact1.setEmail("ivan@example.com");

        contact2 = new Contact();
        contact2.setId(2L);
        contact2.setPhone("+79602223344");
        contact2.setEmail("petr@example.com");

        // Подготовка клиентов
        client1 = new Client();
        client1.setClientId(1L);
        client1.setName("Иван");
        client1.setLastName("Петров");
        client1.setContact(contact1);

        client2 = new Client();
        client2.setClientId(2L);
        client2.setName("Петр");
        client2.setLastName("Иванов");
        client2.setContact(contact2);

        // Подготовка DTO для создания
        clientCreateDto = new ClientCreateDto();
        clientCreateDto.setName("Новый");
        clientCreateDto.setLastName("Клиент");
        clientCreateDto.setContactId(1L);
    }

    @Test
    void getAllClients_ShouldReturnAllClients() {
        // given
        List<Client> expectedClients = Arrays.asList(client1, client2);
        when(clientRepository.findAll()).thenReturn(expectedClients);

        // when
        List<Client> actualClients = clientService.getAllClients();

        // then
        assertThat(actualClients).hasSize(2);
        assertThat(actualClients).isEqualTo(expectedClients);
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    void getClientById_WhenClientExists_ShouldReturnClient() {
        // given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client1));

        // when
        Optional<Client> result = clientService.getClientById(1L);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Иван");
        assertThat(result.get().getLastName()).isEqualTo("Петров");
        assertThat(result.get().getContact().getEmail()).isEqualTo("ivan@example.com");
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    void getClientById_WhenClientNotExists_ShouldReturnEmpty() {
        // given
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        // when
        Optional<Client> result = clientService.getClientById(99L);

        // then
        assertThat(result).isEmpty();
        verify(clientRepository, times(1)).findById(99L);
    }

    @Test
    void createClient_WithValidContactId_ShouldCreateClient() {
        // given
        when(contactService.getContactById(1L)).thenReturn(Optional.of(contact1));

        Client savedClient = new Client();
        savedClient.setClientId(3L);
        savedClient.setName("Новый");
        savedClient.setLastName("Клиент");
        savedClient.setContact(contact1);

        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        // when
        Client result = clientService.createClient(clientCreateDto);

        // then
        assertThat(result.getClientId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("Новый");
        assertThat(result.getLastName()).isEqualTo("Клиент");
        assertThat(result.getContact().getId()).isEqualTo(1L);

        verify(contactService, times(1)).getContactById(1L);
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void createClient_WithNonExistentContactId_ShouldThrowException() {
        // given
        when(contactService.getContactById(99L)).thenReturn(Optional.empty());
        clientCreateDto.setContactId(99L);

        // when/then
        assertThatThrownBy(() -> clientService.createClient(clientCreateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Контакт не найден");

        verify(contactService, times(1)).getContactById(99L);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void createClient_WithNullContactId_ShouldCreateClientWithoutContact() {
        // given
        clientCreateDto.setContactId(null);

        Client savedClient = new Client();
        savedClient.setClientId(3L);
        savedClient.setName("Новый");
        savedClient.setLastName("Клиент");
        savedClient.setContact(null);

        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        // when
        Client result = clientService.createClient(clientCreateDto);

        // then
        assertThat(result.getClientId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("Новый");
        assertThat(result.getLastName()).isEqualTo("Клиент");
        assertThat(result.getContact()).isNull();

        verify(contactService, never()).getContactById(anyLong());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void updateClient_WhenClientExists_ShouldUpdateFields() {
        // given
        Client existingClient = new Client();
        existingClient.setClientId(1L);
        existingClient.setName("Иван");
        existingClient.setLastName("Петров");
        existingClient.setContact(contact1);

        Client updateDetails = new Client();
        updateDetails.setName("Иван_Updated");
        updateDetails.setLastName("Петров_Updated");

        Contact newContact = new Contact();
        newContact.setId(2L);
        newContact.setPhone("+79999999999");
        newContact.setEmail("updated@example.com");
        updateDetails.setContact(newContact);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existingClient));
        when(contactService.updateContact(eq(2L), any(Contact.class))).thenReturn(newContact);

        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client savedClient = invocation.getArgument(0);
            savedClient.setClientId(1L); // гарантируем, что ID не потерялся
            return savedClient;
        });

        // when
        Client result = clientService.updateClient(1L, updateDetails);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getClientId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Иван_Updated");
        assertThat(result.getLastName()).isEqualTo("Петров_Updated");
        assertThat(result.getContact()).isNotNull();
        assertThat(result.getContact().getId()).isEqualTo(2L);
        assertThat(result.getContact().getPhone()).isEqualTo("+79999999999");
        assertThat(result.getContact().getEmail()).isEqualTo("updated@example.com");

        verify(clientRepository, times(1)).findById(1L);
        verify(contactService, times(1)).updateContact(eq(2L), any(Contact.class));
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void updateClient_WhenClientNotExists_ShouldThrowException() {
        // given
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> clientService.updateClient(99L, new Client()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("не найден");

        verify(clientRepository, times(1)).findById(99L);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void deleteClient_WhenClientExists_ShouldDeleteClientAndContact() {
        // given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client1));
        doNothing().when(contactService).deleteContact(1L);
        doNothing().when(clientRepository).deleteById(1L);

        // when
        clientService.deleteClient(1L);

        // then
        verify(clientRepository, times(1)).findById(1L);
        verify(contactService, times(1)).deleteContact(1L);
        verify(clientRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteClient_WhenClientNotExists_ShouldThrowException() {
        // given
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> clientService.deleteClient(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("не найден");

        verify(clientRepository, times(1)).findById(99L);
        verify(contactService, never()).deleteContact(anyLong());
        verify(clientRepository, never()).deleteById(anyLong());
    }

    @Test
    void getClientsCount_ShouldReturnCount() {
        // given
        when(clientRepository.count()).thenReturn(5L);

        // when
        long result = clientService.getClientsCount();

        // then
        assertThat(result).isEqualTo(5L);
        verify(clientRepository, times(1)).count();
    }

    @Test
    void getClientsByName_ShouldReturnMatchingClients() {
        // given
        List<Client> expected = Arrays.asList(client1);
        when(clientRepository.findByName("Иван")).thenReturn(expected);

        // when
        List<Client> result = clientService.getClientsByName("Иван");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Иван");
        verify(clientRepository, times(1)).findByName("Иван");
    }

    @Test
    void getClientsByLastName_ShouldReturnMatchingClients() {
        // given
        List<Client> expected = Arrays.asList(client1);
        when(clientRepository.findByLastName("Петров")).thenReturn(expected);

        // when
        List<Client> result = clientService.getClientsByLastName("Петров");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Петров");
        verify(clientRepository, times(1)).findByLastName("Петров");
    }

    @Test
    void getClientByContactEmail_WhenExists_ShouldReturnClient() {
        // given
        when(clientRepository.findByContactEmail("ivan@example.com")).thenReturn(Optional.of(client1));

        // when
        Optional<Client> result = clientService.getClientByContactEmail("ivan@example.com");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getContact().getEmail()).isEqualTo("ivan@example.com");
        verify(clientRepository, times(1)).findByContactEmail("ivan@example.com");
    }
}
