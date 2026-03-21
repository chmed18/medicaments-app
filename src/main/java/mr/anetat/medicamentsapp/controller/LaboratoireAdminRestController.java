package mr.anetat.medicamentsapp.controller;

import java.util.List;

import mr.anetat.medicamentsapp.domain.Laboratoire;
import mr.anetat.medicamentsapp.service.LaboratoireService;
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
@RequestMapping("/admin/laboratoires")
public class LaboratoireAdminRestController {

    private final LaboratoireService laboratoireService;

    public LaboratoireAdminRestController(LaboratoireService laboratoireService) {
        this.laboratoireService = laboratoireService;
    }

    @GetMapping
    public List<Laboratoire> findAll() {
        return laboratoireService.findAll();
    }

    @GetMapping("/{id}")
    public Laboratoire findById(@PathVariable Long id) {
        return laboratoireService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Laboratoire create(@RequestBody Laboratoire laboratoire) {
        return laboratoireService.create(laboratoire);
    }

    @PutMapping("/{id}")
    public Laboratoire update(@PathVariable Long id, @RequestBody Laboratoire laboratoire) {
        return laboratoireService.update(id, laboratoire);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        laboratoireService.delete(id);
    }
}

