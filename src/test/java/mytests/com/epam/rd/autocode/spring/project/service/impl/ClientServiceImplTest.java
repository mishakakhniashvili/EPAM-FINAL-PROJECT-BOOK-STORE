package mytests.com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock private ClientRepository clientRepository;
    @Mock private ModelMapper mapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private ClientServiceImpl service;

    @Test
    void getClientByEmail_found_returnsDto() {
        String email = "c@mail.com";
        Client entity = new Client();
        entity.setEmail(email);

        ClientDTO dto = new ClientDTO();
        dto.setEmail(email);

        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(mapper.map(entity, ClientDTO.class)).thenReturn(dto);

        ClientDTO res = service.getClientByEmail(email);

        assertEquals(email, res.getEmail());
        verify(clientRepository).findByEmail(email);
        verify(mapper).map(entity, ClientDTO.class);
    }

    @Test
    void getClientByEmail_missing_throwsNotFound() {
        when(clientRepository.findByEmail("x@mail.com")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getClientByEmail("x@mail.com"));
    }

    @Test
    void addClient_whenExists_throwsAlreadyExist() {
        ClientDTO dto = new ClientDTO();
        dto.setEmail("c@mail.com");

        when(clientRepository.existsByEmail("c@mail.com")).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> service.addClient(dto));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void addClient_encodesPassword_andSaves() {
        ClientDTO dto = new ClientDTO();
        dto.setEmail("c@mail.com");
        dto.setPassword("plain");
        dto.setName("N");
        dto.setBalance(BigDecimal.TEN);

        Client mapped = new Client();
        mapped.setEmail(dto.getEmail());
        mapped.setPassword(dto.getPassword());

        when(clientRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(mapper.map(dto, Client.class)).thenReturn(mapped);
        when(passwordEncoder.encode("plain")).thenReturn("ENC");
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.map(any(Client.class), eq(ClientDTO.class))).thenReturn(dto);

        service.addClient(dto);

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        assertEquals("ENC", captor.getValue().getPassword());
        verify(passwordEncoder).encode("plain");
    }

    @Test
    void addClient_doesNotDoubleEncode_bcryptPassword() {
        ClientDTO dto = new ClientDTO();
        dto.setEmail("c@mail.com");
        dto.setPassword("$2a$alreadyHashed");

        Client mapped = new Client();
        mapped.setEmail(dto.getEmail());
        mapped.setPassword(dto.getPassword());

        when(clientRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(mapper.map(dto, Client.class)).thenReturn(mapped);
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.map(any(Client.class), eq(ClientDTO.class))).thenReturn(dto);

        service.addClient(dto);

        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateClientByEmail_updatesAndEncodes() {
        String email = "c@mail.com";

        Client existing = new Client();
        existing.setEmail(email);
        existing.setPassword("old");

        ClientDTO update = new ClientDTO();
        update.setName("NewName");
        update.setPassword("newPlain");
        update.setBalance(new BigDecimal("123.45"));

        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newPlain")).thenReturn("ENC_NEW");
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.map(any(Client.class), eq(ClientDTO.class))).thenReturn(update);

        service.updateClientByEmail(email, update);

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        Client saved = captor.getValue();

        assertEquals(email, saved.getEmail());
        assertEquals("NewName", saved.getName());
        assertEquals("ENC_NEW", saved.getPassword());
        assertEquals(new BigDecimal("123.45"), saved.getBalance());
    }
}