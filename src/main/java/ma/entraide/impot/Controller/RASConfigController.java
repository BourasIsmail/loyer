package ma.entraide.impot.Controller;

import ma.entraide.impot.Entity.RASConfig;
import ma.entraide.impot.Service.RASConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ras-config")
@CrossOrigin("*")
public class RASConfigController {

    @Autowired
    private RASConfigService rasConfigService;

    @GetMapping
    public ResponseEntity<RASConfig> getConfig() {
        RASConfig config = rasConfigService.getConfig();
        if (config != null) {
            return ResponseEntity.ok(config);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<RASConfig> createConfig(@RequestBody RASConfig rasConfig) {
        RASConfig savedConfig = rasConfigService.saveConfig(rasConfig);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedConfig);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RASConfig> updateConfig(@PathVariable Long id, @RequestBody RASConfig rasConfig) {
        RASConfig existingConfig = rasConfigService.getConfig();
        if (existingConfig != null && existingConfig.getId().equals(id)) {
            rasConfig.setId(id);
            RASConfig updatedConfig = rasConfigService.saveConfig(rasConfig);
            return ResponseEntity.ok(updatedConfig);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfig(@PathVariable Long id) {
        RASConfig existingConfig = rasConfigService.getConfig();
        if (existingConfig != null && existingConfig.getId().equals(id)) {
            rasConfigService.deleteConfig(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/special-case")
    public ResponseEntity<RASConfig> addSpecialCase(@RequestBody Map<String, Object> payload) {
        Long localId = Long.parseLong(payload.get("localId").toString());
        int percentage = Integer.parseInt(payload.get("percentage").toString());
        RASConfig updatedConfig = rasConfigService.addSpecialCase(localId, percentage);
        if (updatedConfig != null) {
            return ResponseEntity.ok(updatedConfig);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/special-case/{localId}")
    public ResponseEntity<RASConfig> removeSpecialCase(@PathVariable Long localId) {
        RASConfig updatedConfig = rasConfigService.removeSpecialCase(localId);
        if (updatedConfig != null) {
            return ResponseEntity.ok(updatedConfig);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

