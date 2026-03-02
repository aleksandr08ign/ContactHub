package com.example.ContactHub.controller;

import com.example.ContactHub.dto.ClientCreateDto;
import com.example.ContactHub.model.Client;
import com.example.ContactHub.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Клиенты", description = "Управление клиентами")
    public class ClientController {

    public final ClientService clientService;

    @GetMapping
    @Operation(summary = "Получить всех клиентов",
            description = "Возвращает список всех клиентов с их контактами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены"),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить клиента по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиент найден"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<Client> getClientById(
            @Parameter(description = "ID клиента") @PathVariable Long id) {
        return clientService.getClientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать нового клиента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Клиент создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<Client> createClient(
            @Parameter(description = "Данные клиента") @RequestBody ClientCreateDto clientDto) {
        Client createdClient = clientService.createClient(clientDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить существующего клиента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиент обновлен"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден", content = @Content),
            @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<Client> updateClient(
            @Parameter(description = "ID клиента") @PathVariable Long id,
            @Parameter(description = "Обновленные данные клиента") @RequestBody Client client) {
        try {
            Client updatedClient = clientService.updateClient(id, client);
            return ResponseEntity.ok(updatedClient);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить клиента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Клиент удален"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<Void> deleteClient(
            @Parameter(description = "ID клиента") @PathVariable Long id) {
        try {
            clientService.deleteClient(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search/name")
    @Operation(summary = "Найти клиентов по имени")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиенты найдены"),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<List<Client>> getClientsByName(
            @Parameter(description = "Имя для поиска") @RequestParam String name) {
        List<Client> clients = clientService.getClientsByName(name);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/search/lastname")
    @Operation(summary = "Найти клиентов по фамилии")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиенты найдены"),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<List<Client>> getClientsByLastName(
            @Parameter(description = "Фамилия для поиска") @RequestParam String lastName) {
        List<Client> clients = clientService.getClientsByLastName(lastName);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/search/email")
    @Operation(summary = "Найти клиента по email контакта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиент найден"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<Client> getClientByContactEmail(
            @Parameter(description = "Email для поиска") @RequestParam String email) {
        return clientService.getClientByContactEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats/count")
    @Operation(summary = "Получить количество клиентов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Количество получено"),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<Long> getClientsCount() {
        return ResponseEntity.ok(clientService.getClientsCount());
    }
}
