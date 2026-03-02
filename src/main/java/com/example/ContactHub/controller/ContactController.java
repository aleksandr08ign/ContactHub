package com.example.ContactHub.controller;

import com.example.ContactHub.model.Contact;
import com.example.ContactHub.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@Tag(name = "Контакты", description = "Управление контактами клиентов")
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    @Operation(summary = "Получить все контакты", description = "Возвращает список всех контактов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены"),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<List<Contact>> getAllContacts() {
        List<Contact> contacts = contactService.getAllContacts();
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить контакт по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Контакт найден"),
            @ApiResponse(responseCode = "404", description = "Контакт не найден", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<Contact> getContactById(
            @Parameter(description = "ID контакта") @PathVariable Long id) {
        return contactService.getContactById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать новый контакт")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Контакт создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<Contact> createContact(
            @Parameter(description = "Данные контакта") @RequestBody Contact contact) {
        Contact createdContact = contactService.createContact(contact);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContact);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить существующий контакт")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Контакт обновлен"),
            @ApiResponse(responseCode = "404", description = "Контакт не найден", content = @Content),
            @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<Contact> updateContact(
            @Parameter(description = "ID контакта") @PathVariable Long id,
            @Parameter(description = "Обновленные данные контакта") @RequestBody Contact contact) {
        try {
            Contact updatedContact = contactService.updateContact(id, contact);
            return ResponseEntity.ok(updatedContact);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить контакт")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Контакт удален"),
            @ApiResponse(responseCode = "404", description = "Контакт не найден", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<Void> deleteContact(
            @Parameter(description = "ID контакта") @PathVariable Long id) {
        try {
            contactService.deleteContact(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search/email")
    @Operation(summary = "Найти контакт по email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Контакт найден"),
            @ApiResponse(responseCode = "404", description = "Контакт не найден", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<Contact> getContactByEmail(
            @Parameter(description = "Email для поиска") @RequestParam String email) {
        return contactService.getContactByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/phone")
    @Operation(summary = "Найти контакты по части номера телефона")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Контакты найдены"),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<List<Contact>> getContactsByPhone(
            @Parameter(description = "Часть номера телефона") @RequestParam String phone) {
        List<Contact> contacts = contactService.getContactsByPhoneContaining(phone);
        return ResponseEntity.ok(contacts);
    }

    /**
     * Получить количество всех контактов
     */
    @GetMapping("/count")
    @Operation(summary = "Получить количество всех контактов")
    public ResponseEntity<Long> getContactsCount() {
        Long count = contactService.getContactsCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Обновить только email контакта
     */
    @PatchMapping("/{id}/email")
    @Operation(summary = "Обновить только email контакта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email обновлен"),
            @ApiResponse(responseCode = "404", description = "Контакт не найден", content = @Content)
    })
    public ResponseEntity<Contact> updateContactEmail(
            @Parameter(description = "ID контакта") @PathVariable Long id,
            @Parameter(description = "Новый email") @RequestParam String email) {
        try {
            Contact updatedContact = contactService.updateContactEmail(id, email);
            return ResponseEntity.ok(updatedContact);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Обновить только телефон контакта
     */
    @PatchMapping("/{id}/phone")
    @Operation(summary = "Обновить только телефон контакта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Телефон обновлен"),
            @ApiResponse(responseCode = "404", description = "Контакт не найден", content = @Content)
    })
    public ResponseEntity<Contact> updateContactPhone(
            @Parameter(description = "ID контакта") @PathVariable Long id,
            @Parameter(description = "Новый телефон") @RequestParam String phone) {
        try {
            Contact updatedContact = contactService.updateContactPhone(id, phone);
            return ResponseEntity.ok(updatedContact);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
