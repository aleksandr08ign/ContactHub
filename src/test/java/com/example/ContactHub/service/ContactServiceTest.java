package com.example.ContactHub.service;

import com.example.ContactHub.model.Contact;
import com.example.ContactHub.repository.ContactRepository;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    private Contact contact1;
    private Contact contact2;

    @BeforeEach
    void seUp() {
        // подготовка тестовых данных
        contact1 = new Contact();
        contact1.setId(1L);
        contact1.setPhone("+79601112222");
        contact1.setEmail("ivan@example.com");

        contact2 = new Contact();
        contact2.setId(2L);
        contact2.setPhone("+79602223344");
        contact2.setEmail("petr@example.com");
    }

    @Test
    void getAllContacts_ShouldReturnAllContacts() {
        // given (подготовка)
        List<Contact> expectedContacts = Arrays.asList(contact1, contact2);
        when(contactRepository.findAll()).thenReturn(expectedContacts);

        // when (действие)
        List<Contact> actualContacts = contactService.getAllContacts();

        // then (проверка)
        assertThat(actualContacts).hasSize(2);
        assertThat(actualContacts).isEqualTo(expectedContacts);
        verify(contactRepository, times(1)).findAll();
    }

    @Test
    void getContactById_WhenContactExists_ShouldReturnContact() {
        // given
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact1));

        // when
        Optional<Contact> result = contactService.getContactById(1L);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getPhone()).isEqualTo("+79601112222");
        assertThat(result.get().getEmail()).isEqualTo("ivan@example.com");
        verify(contactRepository, times(1)).findById(1L);
    }

    @Test
    void getContactById_WhenContactNotExists_ShouldReturnEmpty() {
        // given
        when(contactRepository.findById(99L)).thenReturn(Optional.empty());

        // when
        Optional<Contact> result = contactService.getContactById(99L);

        // then
        assertThat(result).isEmpty();
        verify(contactRepository, times(1)).findById(99L);
    }

    @Test
    void createContact_ShouldSaveAndReturnContact() {
        // given
        Contact newContact = new Contact();
        newContact.setPhone("+79031112233");
        newContact.setEmail("new@example.com");

        Contact savedContact = new Contact();
        savedContact.setId(3L);
        savedContact.setPhone("+79031112233");
        savedContact.setEmail("new@example.com");

        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        // when
        Contact result = contactService.createContact(newContact);

        // then
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getPhone()).isEqualTo("+79031112233");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    void createContact_WithExistingId_ShouldThrowException() {
        // given
        Contact contactWithId = new Contact();
        contactWithId.setId(1L);
        contactWithId.setPhone("+79031112233");
        contactWithId.setEmail("new@example.com");

        // when/then
        assertThatThrownBy(() -> contactService.createContact(contactWithId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID должен быть null");

        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void updateContact_WhenContactExists_ShouldUpdateAndReturn() {
        // given
        Contact existingContact = new Contact();
        existingContact.setId(1L);
        existingContact.setPhone("+79601112222");
        existingContact.setEmail("ivan@example.com");

        Contact contactDetails = new Contact();
        contactDetails.setPhone("+79999999999");
        contactDetails.setEmail("ivan_updated@example.com");

        when(contactRepository.findById(1L)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(any(Contact.class))).thenReturn(existingContact);

        // when
        Contact result = contactService.updateContact(1L, contactDetails);

        // then
        assertThat(result.getPhone()).isEqualTo("+79999999999");
        assertThat(result.getEmail()).isEqualTo("ivan_updated@example.com");
        verify(contactRepository, times(1)).findById(1L);
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    void updateContact_WhenContactNotExists_ShouldThrowException() {
        // given
        when(contactRepository.findById(99L)).thenReturn(Optional.empty());

        Contact contactDetails = new Contact();
        contactDetails.setPhone("+79999999999");

        // when/then
        assertThatThrownBy(() -> contactService.updateContact(99L, contactDetails))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("не найден");

        verify(contactRepository, times(1)).findById(99L);
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void deleteContact_WhenContactExists_ShouldDelete() {
        // given
        when(contactRepository.existsById(1L)).thenReturn(true);
        doNothing().when(contactRepository).deleteById(1L);

        // when
        contactService.deleteContact(1L);

        // then
        verify(contactRepository, times(1)).existsById(1L);
        verify(contactRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteContact_WhenContactNotExists_ShouldThrowException() {
        // given
        when(contactRepository.existsById(99L)).thenReturn(false);

        // when/then
        assertThatThrownBy(() -> contactService.deleteContact(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("не найден");

        verify(contactRepository, times(1)).existsById(99L);
        verify(contactRepository, never()).deleteById(anyLong());
    }

    @Test
    void getContactByEmail_WhenExists_ShouldReturnContact() {
        // given
        when(contactRepository.findByEmail("ivan@example.com")).thenReturn(Optional.of(contact1));

        // when
        Optional<Contact> result = contactService.getContactByEmail("ivan@example.com");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("ivan@example.com");
        verify(contactRepository, times(1)).findByEmail("ivan@example.com");
    }

    @Test
    void getContactsByPhoneContaining_ShouldReturnMatchingContacts() {
        // given
        List<Contact> expected = Arrays.asList(contact1);
        when(contactRepository.findByPhoneContaining("123")).thenReturn(expected);

        // when
        List<Contact> result = contactService.getContactsByPhoneContaining("123");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPhone()).isEqualTo("+79601112222");
        verify(contactRepository, times(1)).findByPhoneContaining("123");
    }

    @Test
    void existsById_WhenContactExists_ShouldReturnTrue() {
        // given
        when(contactRepository.existsById(1L)).thenReturn(true);

        // when
        boolean result = contactService.existsById(1L);

        // then
        assertThat(result).isTrue();
        verify(contactRepository, times(1)).existsById(1L);
    }

    @Test
    void getContactsCount_ShouldReturnCount() {
        // given
        when(contactRepository.count()).thenReturn(5L);

        // when
        long result = contactService.getContactsCount();

        // then
        assertThat(result).isEqualTo(5L);
        verify(contactRepository, times(1)).count();
    }
}
