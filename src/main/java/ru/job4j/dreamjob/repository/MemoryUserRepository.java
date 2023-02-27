package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryUserRepository implements UserRepository {

    private final Map<String, User> users = new HashMap<>();

    private final AtomicInteger nextId = new AtomicInteger(0);

    public MemoryUserRepository() {
        save(new User(0, "mail@index.ru", "Ivan", "password"));
    }

    @Override
    synchronized public Optional<User> save(User user) {
        Optional<User> userOptional = Optional.empty();
        if (user == null || users.containsKey(user.getEmail())) {
            return userOptional;
        }
        user.setId(nextId.incrementAndGet());
        users.put(user.getEmail(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        Optional<User> user = Optional.ofNullable(users.get(email));
        if (user.isPresent() && password.equals(user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteById(int id) {
        boolean rsl = false;
        for (var entry : users.entrySet()) {
            if (entry.getValue().getId() == id) {
                return users.remove(entry.getValue().getEmail()) != null;
            }
        }
        return rsl;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }
}
