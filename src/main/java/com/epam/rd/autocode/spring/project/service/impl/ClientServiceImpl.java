package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ModelMapper mapper;
    private final OrderRepository orderRepository; //
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(c -> mapper.map(c, ClientDTO.class))
                .toList();
    }
    @Override
    public ClientDTO getClientByEmail(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));
        return mapper.map(client, ClientDTO.class);
    }
    @Transactional
    @Override
    public ClientDTO updateClientByEmail(String email, ClientDTO dto) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));

        client.setEmail(email);
        client.setName(dto.getName());
        client.setPassword(encodeIfNeeded(dto.getPassword()));
        client.setBalance(dto.getBalance());

        Client saved = clientRepository.save(client);
        return mapper.map(saved, ClientDTO.class);
    }

    @Transactional
    @Override
    public void deleteClientByEmail(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client email not found: " + email));
        orderRepository.deleteByClient_Email(email);
        clientRepository.delete(client);
    }

    @Transactional
    @Override
    public ClientDTO addClient(ClientDTO dto) {
        if (clientRepository.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistException("Client already exists:" + dto.getEmail());
        }
        Client client = mapper.map(dto, Client.class);
        client.setPassword(encodeIfNeeded(dto.getPassword()));
        Client saved = clientRepository.save(client);
        return mapper.map(saved, ClientDTO.class);
    }

    private String encodeIfNeeded(String password) {
        if (password == null) return null;

        // Prevent double encoding if password is already bcrypt hash
        if (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$")) {
            return password;
        }
        return passwordEncoder.encode(password);
    }

    @Transactional
    @Override
    public ClientDTO setClientBlocked(String email, boolean blocked) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found: " + email));

        client.setBlocked(blocked);
        Client saved = clientRepository.save(client);
        return mapper.map(saved, ClientDTO.class);
    }
}
