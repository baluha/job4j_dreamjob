package ru.job4j.dreamjob;

import ru.job4j.dreamjob.model.User;
import java.util.Map;
import java.util.Optional;

public class TestClass {
    public static void main(String[] args) {
        String password = "password";
        User user1 = new User(0, "mail@inbox.ru", "Ivan", "password");
        Map<String, User> users = Map.of("mail@inbox.ru", user1);
        Optional<User> user = Optional.of(users.get("mail@inbox.ru"));
        if (password.equals(user.get().getPassword())) {
            System.out.println(user.get().getName());
        }
    }
}
