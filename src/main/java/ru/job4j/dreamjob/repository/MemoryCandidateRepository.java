package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemoryCandidateRepository implements CandidateRepository {

    private Map<Integer, Candidate> candidates = new HashMap<>();

    private int nextId = 1;

    private static final MemoryCandidateRepository INSTANCE = new MemoryCandidateRepository();

    public MemoryCandidateRepository() {
        save(new Candidate(0, "Petr", "Some description", LocalDateTime.now()));
        save(new Candidate(0, "Ivan", "Some description", LocalDateTime.now()));
        save(new Candidate(0, "Sergey", "Some description", LocalDateTime.now()));
        save(new Candidate(0, "Vasiliy", "Some description", LocalDateTime.now()));
    }

    @Override
    public Candidate save(Candidate candidate) {
        return candidates.put(nextId++, candidate);
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(), (id, oldCandidate)
                -> new Candidate(id, candidate.getName(), candidate.getDescription(), oldCandidate.getCreationDate())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }

    public static MemoryCandidateRepository getInstance() {
        return INSTANCE;
    }
}
