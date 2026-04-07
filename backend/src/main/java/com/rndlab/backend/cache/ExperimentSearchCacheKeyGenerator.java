package com.rndlab.backend.cache;

import com.rndlab.backend.dto.ExperimentSearchDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * Stable, bounded-length cache keys for search queries (hot-key mitigation: avoids huge raw DTO keys).
 */
@Component
public class ExperimentSearchCacheKeyGenerator {

    public String digest(ExperimentSearchDTO search) {
        return DigestUtils.md5DigestAsHex(canonical(search).getBytes(StandardCharsets.UTF_8));
    }

    private static String canonical(ExperimentSearchDTO s) {
        return String.join("|",
                nz(s.getKeyword()),
                nz(s.getRecordNumber()),
                nz(s.getStatus()),
                nz(s.getTeamId()),
                nz(s.getCompositionId()),
                nz(s.getProcessId()),
                nz(s.getCreatedBy()),
                nz(s.getStartDate()),
                nz(s.getEndDate()),
                nz(s.getMinTemperature()),
                nz(s.getMaxTemperature()),
                nz(s.getMinPressure()),
                nz(s.getMaxPressure()),
                nz(s.getPropertyName()),
                nz(s.getMinPropertyValue()),
                nz(s.getMaxPropertyValue()),
                nz(s.getPropertyType()),
                nz(s.getSortBy()),
                nz(s.getSortOrder()),
                nz(s.getPage()),
                nz(s.getPageSize()));
    }

    private static String nz(Object o) {
        return o == null ? "" : String.valueOf(o);
    }
}
