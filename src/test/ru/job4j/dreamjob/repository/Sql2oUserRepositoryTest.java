package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2oException;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        var users = sql2oUserRepository.findAll();
        for (var user: users) {
            sql2oUserRepository.deleteById(user.getId());
        }
    }

    @Test
    public void whenTheSameEmail() {
        User user = new User(0, "mail@index.ru", "name", "password");
        User user1 = new User(0, "mail@index.ru", "anotherName", "password");
        sql2oUserRepository.save(user);
        assertThrows(Sql2oException.class, () -> {
            sql2oUserRepository.save(user1);
        });
    }

    @Test
    public void whenSaveThenGetAll() {
        var user = sql2oUserRepository.save(new User(0, "mail@index.ru", "name", "password")).get();
        var user1 = sql2oUserRepository.save(new User(0, "mail@bundex.ru", "name", "password")).get();
        var user2 = sql2oUserRepository.save(new User(0, "mail@lundex.ru", "name", "password")).get();
        var rsl = sql2oUserRepository.findAll();
        assertThat(rsl).usingRecursiveComparison().isEqualTo(List.of(user, user1, user2));
    }

    @Test
    public void whenFindByEmailAndPassword() {
        var user = sql2oUserRepository.save(new User(0, "mail@index.ru", "name", "password")).get();
        var user1 = sql2oUserRepository.save(new User(0, "mail@bundex.ru", "name", "password")).get();
        var user2 = sql2oUserRepository.save(new User(0, "mail@lundex.ru", "name", "password")).get();
        assertThat(user).usingRecursiveComparison().isEqualTo(
                sql2oUserRepository.findByEmailAndPassword(user.getEmail(), user.getPassword()).get());
    }

    @Test
    public void whenDeleteById() {
        var user = sql2oUserRepository.save(new User(0, "mail@index.ru", "name", "password")).get();
        assertThat(sql2oUserRepository.deleteById(user.getId())).isTrue();
    }
}