package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.VacancyService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@ThreadSafe
@Controller
@RequestMapping("/vacancies") /* Работать с кандидатами будем по URI /vacancies/** */
public class VacancyController {

    private final VacancyService vacancyRepository; /*Отвяжем контроллер от реализации MemoryVacancyRepository
             и создадим прослойку в виде SimpleVacancyService*/

    private final CityService cityService;

    public VacancyController(VacancyService vacancyRepository, CityService cityService) {
        this.vacancyRepository = vacancyRepository;
        this.cityService = cityService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("vacancies", vacancyRepository.findAll());
        model.addAttribute("cities", cityService.findAll());
        return "vacancies/list";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model) {
        model.addAttribute("cities", cityService.findAll());
        return "vacancies/create";
    }

    @PostMapping("/create")
    public String create(HttpServletRequest request) {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        boolean isVisible = Boolean.parseBoolean(request.getParameter("visible"));
        int city = Integer.parseInt(request.getParameter("cityId"));
        vacancyRepository.save(new Vacancy(0, title, description, LocalDateTime.now(), isVisible, city));
        return "redirect:/vacancies";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        var vacancyOptional = vacancyRepository.findById(id);
        if (vacancyOptional.isEmpty()) {
            model.addAttribute("message", "Вакансия с указанным идентификатором не найдена");
            return "errors/404";
        }
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("vacancy", vacancyOptional.get());
        return "vacancies/one";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Vacancy vacancy, Model model) {
        var isUpdated = vacancyRepository.update(vacancy);
        if (!isUpdated) {
            model.addAttribute("message", "Вакансия с указанным идентификатором не найдена");
            return "errors/404";
        }
        return "redirect:/vacancies";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        var isDeleted = vacancyRepository.deleteById(id);
        if (!isDeleted) {
            model.addAttribute("message", "Вакансия с указанным идентификатором не найдена");
            return "errors/404";
        }
        return "redirect:/vacancies";
    }

}