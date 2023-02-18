package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.File;

import java.util.Optional;

public interface FileService {

    File save(FileDto fIleDto);

    Optional<FileDto> getFileById(int id);

    boolean deleteById(int id);
}
