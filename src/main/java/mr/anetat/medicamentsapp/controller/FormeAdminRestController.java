package mr.anetat.medicamentsapp.controller;

import java.util.List;

import mr.anetat.medicamentsapp.domain.Forme;
import mr.anetat.medicamentsapp.service.FormeService;
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
@RequestMapping("/admin/formes")
public class FormeAdminRestController {

    private final FormeService formeService;

    public FormeAdminRestController(FormeService formeService) {
        this.formeService = formeService;
    }

    @GetMapping
    public List<Forme> findAll() {
        return formeService.findAll();
    }

    @GetMapping("/{id}")
    public Forme findById(@PathVariable Long id) {
        return formeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Forme create(@RequestBody Forme forme) {
        return formeService.create(forme);
    }

    @PutMapping("/{id}")
    public Forme update(@PathVariable Long id, @RequestBody Forme forme) {
        return formeService.update(id, forme);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        formeService.delete(id);
    }
}

