package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.service.CandidatesService;
import ru.job4j.dreamjob.service.CityService;

import java.io.IOException;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class CandidateControllerTest {

    private CandidateController candidateController;

    private CandidatesService candidatesService;

    private CityService cityService;

    private MultipartFile multipartFile;

    @BeforeEach
    public void initServices() {
        candidatesService = mock(CandidatesService.class);
        cityService = mock(CityService.class);
        candidateController = new CandidateController(candidatesService, cityService);
        multipartFile = new MockMultipartFile("testFile1.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenGetAll() {
        var candidate1 = new Candidate(0, "name", "description", now(), 0, 0);
        var candidate2 = new Candidate(0, "name", "description", now(), 0, 0);
        var expected = List.of(candidate1, candidate2);
        when(candidatesService.findAll()).thenReturn(expected);

        var model  = new ConcurrentModel();
        var view = candidateController.getAll(model);
        var actualCandidates = model.getAttribute("candidates");

        assertThat(view).isEqualTo("candidates/list");
        assertThat(actualCandidates).isEqualTo(expected);
    }

    @Test
    public void whenRequestCandidatesCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Воркута");
        var city2 = new City(2, "Мухосранск");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        var view = candidateController.getCreationPage(model);
        var actualCandidates = model.getAttribute("cities");

        assertThat(view).isEqualTo("candidates/create");
        assertThat(actualCandidates).isEqualTo(expectedCities);
    }

    @Test
    public void whenPostCandidateWithFileThenSameDataAndRedirectToCandidates() throws Exception {
        var candidate = new Candidate(0, "name", "description", now(), 0, 0);
        var fileDto = new FileDto(multipartFile.getOriginalFilename(), multipartFile.getBytes());
        var candidateArgumentCapture = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCapture = ArgumentCaptor.forClass(FileDto.class);
        when(candidatesService.save(candidateArgumentCapture.capture(),
                fileDtoArgumentCapture.capture())).thenReturn(candidate);

        var model = new ConcurrentModel();
        var view = candidateController.create(candidate, multipartFile, model);
        var actualCandidate = candidateArgumentCapture.getValue();
        var actualFileDto = fileDtoArgumentCapture.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(fileDto);
    }

    @Test
    public void whenTryCreateThenError() {
        var expectedException = new RuntimeException();
        when(candidatesService.save(any(), any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = candidateController.create(new Candidate(), multipartFile, model);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenUpdateThenRedirectToCandidatesPage() throws IOException {
        var updatedCandidate = new Candidate(0, "candidate", "desc", now(), 1, 1);
        var fileDto = new FileDto(multipartFile.getOriginalFilename(), multipartFile.getBytes());
        var candidateArgumentCapture = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCapture = ArgumentCaptor.forClass(FileDto.class);
        when(candidatesService.update(candidateArgumentCapture.capture(),
                fileDtoArgumentCapture.capture())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = candidateController.update(updatedCandidate, multipartFile, model);
        var actualCandidate = candidateArgumentCapture.getValue();
        var actualFileDto = fileDtoArgumentCapture.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).usingRecursiveComparison().isEqualTo(updatedCandidate);
        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(fileDto);
    }

    @Test
    public void whenErrorWhenTryUpdating() {
        when(candidatesService.update(any(), any())).thenReturn(false);

        var model = new ConcurrentModel();
        var view = candidateController.update(new Candidate(), multipartFile, model);
        var actualMessage = model.getAttribute("massage");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Кандидат с указанным идентификатором не найден");
    }

    @Test
    public void whenDeleteCandidateByIdThenRedirectCandidatesPage() {
        int id = 1;
        when(candidatesService.deleteById(id)).thenReturn(true);

        var model = new ConcurrentModel();
        var view = candidateController.delete(id, model);

        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenDeleteThenReturnToErrorPage() {
        int id = 1;
        when(candidatesService.deleteById(id)).thenReturn(false);

        var model = new ConcurrentModel();
        var view = candidateController.delete(id, model);
        var actualMassage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMassage).isEqualTo("Резюме с указанным идентификатором не найдено");
    }

    @Test
    public void whenDeleteById() {
        int id = 1;
        when(candidatesService.deleteById(id)).thenReturn(true);

        var model = new ConcurrentModel();
        var view = candidateController.delete(id, model);

        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenDeleteByIdButReturnException() {
        int id = 1;
        when(candidatesService.deleteById(id)).thenReturn(false);

        var model = new ConcurrentModel();
        var view = candidateController.delete(id, model);
        var message = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(message).isEqualTo("Резюме с указанным идентификатором не найдено");

    }
}