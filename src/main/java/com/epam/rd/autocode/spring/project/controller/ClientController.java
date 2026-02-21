package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public List<ClientDTO> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/{email:.+}")
    public ClientDTO getClientByEmail(@PathVariable String email) {
        return clientService.getClientByEmail(email);
    }
    @Valid
    @PostMapping
    public ClientDTO addClient(@RequestBody @Valid ClientDTO dto) {
        return clientService.addClient(dto);
    }
    @Valid
    @PutMapping("/{email:.+}")
    public ClientDTO updateClientByEmail(@PathVariable String email, @RequestBody @Valid ClientDTO dto) {
        return clientService.updateClientByEmail(email, dto);
    }

    @DeleteMapping("/{email:.+}")
    public void deleteClientByEmail(@PathVariable String email) {
        clientService.deleteClientByEmail(email);
    }

    @PatchMapping("/{email:.+}/block")
    public ClientDTO block(@PathVariable String email) {
        return clientService.setClientBlocked(email, true);
    }

    @PatchMapping("/{email:.+}/unblock")
    public ClientDTO unblock(@PathVariable String email) {
        return clientService.setClientBlocked(email, false);
    }

    @PatchMapping("/{email:.+}")
    public ClientDTO patchClient(@PathVariable String email, @RequestBody ClientDTO dto) {
        return clientService.patchClientByEmail(email, dto);
    }
}
