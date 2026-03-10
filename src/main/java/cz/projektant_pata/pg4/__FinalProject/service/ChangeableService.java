package cz.projektant_pata.pg4.__FinalProject.service;

import cz.projektant_pata.pg4.__FinalProject.shared.entity.Changeable;
import cz.projektant_pata.pg4.__FinalProject.repository.ChangeableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChangeableService {

    @Autowired
    private ChangeableRepository changeableRepository;

    public List<Changeable> findAll() {
        return changeableRepository.findAll();
    }

    public Optional<Changeable> findById(Long id) {
        return changeableRepository.findById(id);
    }

    public Changeable save(Changeable changeable) {
        return changeableRepository.save(changeable);
    }

    public void deleteById(Long id) {
        changeableRepository.deleteById(id);
    }
}
