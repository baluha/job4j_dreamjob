package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;

    private UserController userController;

    private HttpSession session;

    private HttpServletRequest request;

    @BeforeEach
    public void init() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        session = mock(HttpSession.class);
        request = mock(HttpServletRequest.class);
    }

    @Test
    public void whenGetRegistrationPage() {
        var model = new ConcurrentModel();
        var view = userController.getRegistrationPage(model, session);

        assertThat(view).isEqualTo("/users/register");
    }

    @Test
    public void whenLogin() {
        var view = userController.getLoginPage();

        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenLogOut() {
        var view = userController.logout(session);

        assertThat(view).isEqualTo("redirect:/users/login");
    }

    @Test
    public void whenRegisterThenError() {
        User user = new User(1, "mail@mail.ru", "name", "password");
        when(userService.save(user)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.register(user, model);
        var actualMsg = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMsg).isEqualTo("Пользователь с такой почтой уже существует");
    }

    @Test
    public void whenRegister() {
        User user = new User(1, "mail@mail.ru", "name", "password");
        when(userService.save(user)).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(user, model);

        assertThat(view).isEqualTo("index");
    }

    @Test
    public void whenLoginAndUserDoNotExist() {
        User user = new User(1, "mail@mail.ru", "name", "password");
        when(userService.findByEmailAndPassword(user.getEmail(),
                user.getPassword())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, request);
        var message = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(message).isEqualTo("Почта или пароль введены неверно");
    }

    @Test
    public void whenLoginAndUserExist() {
        User user = new User(1, "mail@mail.ru", "name", "password");
        when(userService.findByEmailAndPassword(user.getEmail(),
                user.getPassword())).thenReturn(Optional.of(user));
        when(request.getSession()).thenReturn(session);

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, request);

        assertThat(view).isEqualTo("redirect:/vacancies");
        verify(session).setAttribute("user", user);
    }
}