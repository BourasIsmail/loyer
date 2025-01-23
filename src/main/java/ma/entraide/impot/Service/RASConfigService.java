package ma.entraide.impot.Service;

import ma.entraide.impot.Entity.RASConfig;
import ma.entraide.impot.Repository.RASConfigRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RASConfigService {

    @Autowired
    private RASConfigRepo rasConfigRepo;

    public RASConfig getConfig() {
        return rasConfigRepo.findFirstByOrderByIdDesc();
    }

    public Optional<RASConfig> getConfigById(Long id) {
        return rasConfigRepo.findById(id);
    }

    public RASConfig saveConfig(RASConfig rasConfig) {
        return rasConfigRepo.save(rasConfig);
    }

    public void deleteConfig(Long id) {
        rasConfigRepo.deleteById(id);
    }

    public RASConfig addSpecialCase(Long localId, int percentage) {
        RASConfig config = getConfig();
        if (config != null) {
            config.getSpecialCasePercentages().put(localId, percentage);
            return rasConfigRepo.save(config);
        }
        return null;
    }

    public RASConfig removeSpecialCase(Long localId) {
        RASConfig config = getConfig();
        if (config != null) {
            config.getSpecialCasePercentages().remove(localId);
            return rasConfigRepo.save(config);
        }
        return null;
    }
}

