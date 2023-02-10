package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.service.CandidatesService;
import ru.job4j.dreamjob.service.CityService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@ThreadSafe
@Controller
@RequestMapping("/candidates")
public class CandidateController {

    private final CityService cityService;
    private final CandidatesService candidateRepository; /*Отвяжем контроллер от реализации MemoryCandidateRepository
             и создадим прослойку в виде SimpleCandidateService*/

    public CandidateController(CandidatesService candidateRepository, CityService cityService) {
        this.candidateRepository = candidateRepository;
        this.cityService = cityService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("candidates", candidateRepository.findAll());
        model.addAttribute("cities", cityService.findAll());
        return "candidates/list";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model) {

        model.addAttribute("cities", cityService.findAll());
        return "candidates/create";
    }

    @PostMapping("/create")
    public String create(HttpServletRequest request) {
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        int city = Integer.parseInt(request.getParameter("cityId"));
        candidateRepository.save(new Candidate(0, name, description, LocalDateTime.now(), city));
        return "redirect:/candidates";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        var candidateOptional = candidateRepository.findById(id);
        if (candidateOptional.isEmpty()) {
            model.addAttribute("message", "Кандадат с указанным идентификатором не найден");
            return "errors/404";
        }

        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("candidate", candidateOptional.get());
        return "candidates/one";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Candidate candidate, Model model) {
        var isUpdated = candidateRepository.update(candidate);
        if (!isUpdated) {
            model.addAttribute("message", "Резюме с указанным идентификатором не найдено");
            return "errors/404";
        }
        return "redirect:/candidates";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        var isDeleted = candidateRepository.deleteById(id);
        if (!isDeleted) {
            model.addAttribute("message", "Резюме с указанным идентификатором не найдено");
            return "errors/404";
        }
        return "redirect:/candidates";
    }
}
