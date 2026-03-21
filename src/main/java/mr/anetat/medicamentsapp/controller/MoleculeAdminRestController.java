package mr.anetat.medicamentsapp.controller;

import java.util.List;

import mr.anetat.medicamentsapp.domain.Molecule;
import mr.anetat.medicamentsapp.service.MoleculeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/molecules")
public class MoleculeAdminRestController {

    private final MoleculeService moleculeService;

    public MoleculeAdminRestController(MoleculeService moleculeService) {
        this.moleculeService = moleculeService;
    }

    @GetMapping
    public List<Molecule> findAll() {
        return moleculeService.findAll();
    }

    @GetMapping("/{id}")
    public Molecule findById(@PathVariable Long id) {
        return moleculeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Molecule create(@RequestBody Molecule molecule) {
        return moleculeService.create(molecule);
    }

    @PutMapping("/{id}")
    public Molecule update(@PathVariable Long id, @RequestBody Molecule molecule) {
        return moleculeService.update(id, molecule);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        moleculeService.delete(id);
    }
}

