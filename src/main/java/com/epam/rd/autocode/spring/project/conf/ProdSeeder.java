package com.epam.rd.autocode.spring.project.conf;

import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class ProdSeeder implements ApplicationRunner {

    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedEmployees();
        seedClients();
        seedBooks();
    }

    private void seedEmployees() {
        String adminEmail = System.getenv().getOrDefault("DEMO_EMPLOYEE_EMAIL", "admin@example.com");
        String empPass = System.getenv().getOrDefault("DEMO_EMPLOYEE_PASSWORD", "admin123");

        // 1) admin from env
        if (!employeeRepository.existsByEmail(adminEmail)) {
            Employee e = new Employee();
            e.setEmail(adminEmail);
            e.setName("Admin");
            e.setBirthDate(LocalDate.of(1990, 1, 1));
            e.setPhone("000");
            e.setPassword(passwordEncoder.encode(empPass));
            employeeRepository.save(e);
        }

        // 2) some demo employees (you can add more or remove)
        List<EmployeeSeed> demo = List.of(
                new EmployeeSeed("john.doe@email.com", "John Doe", LocalDate.of(1990, 5, 15), "555-123-4567"),
                new EmployeeSeed("jane.smith@email.com", "Jane Smith", LocalDate.of(1985, 9, 20), "555-987-6543"),
                new EmployeeSeed("bob.jones@email.com", "Bob Jones", LocalDate.of(1978, 3, 8), "555-321-6789"),
                new EmployeeSeed("alice.white@email.com", "Alice White", LocalDate.of(1982, 11, 25), "555-876-5432"),
                new EmployeeSeed("mike.wilson@email.com", "Mike Wilson", LocalDate.of(1995, 7, 12), "555-234-5678")
        );

        for (EmployeeSeed s : demo) {
            if (!employeeRepository.existsByEmail(s.email)) {
                Employee e = new Employee();
                e.setEmail(s.email);
                e.setName(s.name);
                e.setBirthDate(s.birthDate);
                e.setPhone(s.phone);
                e.setPassword(passwordEncoder.encode(empPass)); // same password for all demo employees
                employeeRepository.save(e);
            }
        }
    }

    private void seedClients() {
        String clientPass = System.getenv().getOrDefault("DEMO_CLIENT_PASSWORD", "client123");

        List<ClientSeed> demo = List.of(
                new ClientSeed("client1@example.com", "Medelyn Wright", new BigDecimal("1000.00")),
                new ClientSeed("client2@example.com", "Landon Phillips", new BigDecimal("1500.50")),
                new ClientSeed("client3@example.com", "Harmony Mason", new BigDecimal("800.75")),
                new ClientSeed("client4@example.com", "Archer Harper", new BigDecimal("1200.25")),
                new ClientSeed("client5@example.com", "Kira Jacobs", new BigDecimal("900.80"))
        );

        for (ClientSeed s : demo) {
            if (!clientRepository.existsByEmail(s.email)) {
                Client c = new Client();
                c.setEmail(s.email);
                c.setName(s.name);
                c.setBalance(s.balance);
                c.setPassword(passwordEncoder.encode(clientPass));
                clientRepository.save(c);
            }
        }
    }

    private void seedBooks() {
        // name is used as key in your API (/books/{name}) → typically id/unique
        List<BookSeed> demo = List.of(
                new BookSeed("The Hidden Treasure", "Adventure", "ADULT", "24.99", "2018-05-15", "Emily White", 400,
                        "Mysterious journey", "An enthralling adventure of discovery", "ENGLISH"),
                new BookSeed("Echoes of Eternity", "Fantasy", "TEEN", "16.50", "2011-01-15", "Daniel Black", 350,
                        "Magical realms", "A spellbinding tale of magic and destiny", "ENGLISH"),
                new BookSeed("Whispers in the Shadows", "Mystery", "ADULT", "29.95", "2018-08-11", "Sophia Green", 450,
                        "Intriguing suspense", "A gripping mystery that keeps you guessing", "ENGLISH"),
                new BookSeed("The Starlight Sonata", "Romance", "ADULT", "21.75", "2011-05-15", "Michael Rose", 320,
                        "Heartwarming love story", "A beautiful journey of love and passion", "ENGLISH"),
                new BookSeed("Beyond the Horizon", "Science Fiction", "CHILD", "18.99", "2004-05-15", "Alex Carter", 280,
                        "Interstellar adventure", "An epic sci-fi adventure beyond the stars", "ENGLISH"),
                new BookSeed("Dancing with Shadows", "Thriller", "ADULT", "26.50", "2015-05-15", "Olivia Smith", 380,
                        "Suspenseful twists", "A thrilling tale of danger and intrigue", "ENGLISH"),
                new BookSeed("Voices in the Wind", "Historical Fiction", "ADULT", "32.00", "2017-05-15", "William Turner", 500,
                        "Rich historical setting", "A compelling journey through time", "ENGLISH"),
                new BookSeed("Serenade of Souls", "Fantasy", "TEEN", "15.99", "2013-05-15", "Isabella Reed", 330,
                        "Enchanting realms", "A magical fantasy filled with wonder", "ENGLISH"),
                new BookSeed("Silent Whispers", "Mystery", "ADULT", "27.50", "2021-05-15", "Benjamin Hall", 420,
                        "Intricate detective work", "A mystery that keeps you on the edge", "ENGLISH"),
                new BookSeed("Whirlwind Romance", "Romance", "OTHER", "23.25", "2022-05-15", "Emma Turner", 360,
                        "Passionate love affair", "A romance that sweeps you off your feet", "ENGLISH")
        );

        for (BookSeed s : demo) {
            // If your BookRepository uses name as @Id:
            if (!bookRepository.existsByName(s.name)) {
                Book b = new Book();
                b.setName(s.name);
                b.setGenre(s.genre);
                b.setAgeGroup(AgeGroup.valueOf(s.ageGroup));
                b.setPrice(new BigDecimal(s.price));
                b.setPublicationDate(LocalDate.parse(s.publicationDate));
                b.setAuthor(s.author);
                b.setPages(s.pages);
                b.setCharacteristics(s.characteristics);
                b.setDescription(s.description);
                b.setLanguage(Language.valueOf(s.language));

                bookRepository.save(b);
            }
        }
    }

    private record EmployeeSeed(String email, String name, LocalDate birthDate, String phone) {}
    private record ClientSeed(String email, String name, BigDecimal balance) {}
    private record BookSeed(
            String name, String genre, String ageGroup, String price,
            String publicationDate, String author, int pages,
            String characteristics, String description, String language
    ) {}
}