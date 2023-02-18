package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
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
    private final CandidatesService candidatesService; /*Отвяжем контроллер от реализации MemoryCandidateRepository
             и создадим прослойку в виде SimpleCandidateService*/

    public CandidateController(CandidatesService candidatesService, CityService cityService) {
        this.candidatesService = candidatesService;
        this.cityService = cityService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("candidates", candidatesService.findAll());
        model.addAttribute("cities", cityService.findAll());
        return "candidates/list";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model) {

        model.addAttribute("cities", cityService.findAll());
        return "candidates/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Candidate candidate, @RequestParam MultipartFile file, Model model) {
        try {
            candidatesService.save(candidate, new FileDto(file.getOriginalFilename(), file.getBytes()));
            return "redirect:/candidates";
        } catch (Exception exception) {
            model.addAttribute("massage", exception.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        var candidateOptional = candidatesService.findById(id);
        if (candidateOptional.isEmpty()) {
            model.addAttribute("message", "Кандадат с указанным идентификатором не найден");
            return "errors/404";
        }

        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("candidate", candidateOptional.get());
        return "candidates/one";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Candidate candidate, @RequestParam MultipartFile file, Model model) {
        try {
            var isUpdated = candidatesService.update(candidate, new FileDto(file.getOriginalFilename(), file.getBytes()));
            if (!isUpdated) {
                model.addAttribute("massage", "Кандидат с указанным идентификатором не найден");
                return "errors/404";
            }
            return "redirect:/candidates";
        } catch (Exception exception) {
            model.addAttribute("massage", exception.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        var isDeleted = candidatesService.deleteById(id);
        if (!isDeleted) {
            model.addAttribute("message", "Резюме с указанным идентификатором не найдено");
            return "errors/404";
        }
        return "redirect:/candidates";
    }
}
