package com.medtracker.controller;

import com.medtracker.dto.MedicineDTO;
import com.medtracker.model.Medicine;
import com.medtracker.service.MedicineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    private String getUserId(Authentication auth) {
        return (String) auth.getCredentials();
    }

    @PostMapping
    public ResponseEntity<Medicine> addMedicine(@Valid @RequestBody MedicineDTO dto, Authentication auth) {
        Medicine medicine = medicineService.addMedicine(dto, getUserId(auth));
        return ResponseEntity.status(HttpStatus.CREATED).body(medicine);
    }

    @GetMapping
    public List<Medicine> getAllMedicines(Authentication auth) {
        return medicineService.getAllMedicines(getUserId(auth));
    }

    @GetMapping("/{id}")
    public Medicine getMedicine(@PathVariable String id, Authentication auth) {
        return medicineService.getMedicine(id, getUserId(auth));
    }

    @PutMapping("/{id}")
    public Medicine updateMedicine(@PathVariable String id,
                                   @Valid @RequestBody MedicineDTO dto,
                                   Authentication auth) {
        return medicineService.updateMedicine(id, dto, getUserId(auth));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMedicine(@PathVariable String id, Authentication auth) {
        medicineService.deleteMedicine(id, getUserId(auth));
    }

    @GetMapping("/search")
    public List<Medicine> searchMedicines(@RequestParam("keyword") String keyword, Authentication auth) {
        return medicineService.searchMedicines(getUserId(auth), keyword);
    }

    @GetMapping("/stats")
    public Map<String, Long> getDashboardStats(Authentication auth) {
        return medicineService.getDashboardStats(getUserId(auth));
    }
}
