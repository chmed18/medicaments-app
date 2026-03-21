package mr.anetat.medicamentsapp.controller;

import java.util.List;

import mr.anetat.medicamentsapp.domain.UniteDosage;
import mr.anetat.medicamentsapp.service.UniteDosageService;
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
@RequestMapping("/admin/unites-dosage")
public class UniteDosageAdminRestController {

    private final UniteDosageService uniteDosageService;

    public UniteDosageAdminRestController(UniteDosageService uniteDosageService) {
        this.uniteDosageService = uniteDosageService;
    }

    @GetMapping
    public List<UniteDosage> findAll() {
        return uniteDosageService.findAll();
    }

    @GetMapping("/{id}")
    public UniteDosage findById(@PathVariable Long id) {
        return uniteDosageService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UniteDosage create(@RequestBody UniteDosage uniteDosage) {
        return uniteDosageService.create(uniteDosage);
    }

    @PutMapping("/{id}")
    public UniteDosage update(@PathVariable Long id, @RequestBody UniteDosage uniteDosage) {
        return uniteDosageService.update(id, uniteDosage);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        uniteDosageService.delete(id);
    }
}

