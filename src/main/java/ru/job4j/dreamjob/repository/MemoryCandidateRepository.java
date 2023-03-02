package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class MemoryCandidateRepository implements CandidateRepository {

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private final AtomicInteger nextId = new AtomicInteger(1);

    public MemoryCandidateRepository() {
        save(new Candidate(0, "Petr", "Some description", LocalDateTime.now(), 1, 0));
        save(new Candidate(0, "Ivan", "Some description", LocalDateTime.now(), 2, 0));
        save(new Candidate(0, "Sergey", "Some description", LocalDateTime.now(), 3, 0));
        save(new Candidate(0, "Vasiliy", "Some description", LocalDateTime.now(), 4, 0));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.incrementAndGet());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(), (id, oldCandidate)
                -> new Candidate(id, candidate.getName(), candidate.getDescription(),
                oldCandidate.getCreationDate(), candidate.getCityId(), candidate.getFileId())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }

}
