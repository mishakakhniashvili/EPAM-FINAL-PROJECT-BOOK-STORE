package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ModelMapper mapper;

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
        client.setPassword(dto.getPassword());
        client.setBalance(dto.getBalance());

        Client saved = clientRepository.save(client);
        return mapper.map(saved, ClientDTO.class);
    }

    @Transactional
    @Override
    public void deleteClientByEmail(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client email not found: " + email));
        clientRepository.delete(client);
    }


    @Override
    @Transactional
    public ClientDTO addClient(ClientDTO dto) {
        if (clientRepository.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistException("Client already exists:" + dto.getEmail());
        }
        Client client = mapper.map(dto, Client.class);
        Client saved = clientRepository.save(client);
        return mapper.map(saved, ClientDTO.class);
    }
}
