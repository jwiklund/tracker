package so.born.tracker;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import com.codahale.metrics.health.HealthCheck;

public class TrackerConfigHealh extends HealthCheck {
    private TrackerConfig config;

    public TrackerConfigHealh(TrackerConfig config) {
        this.config = config;
    }

    @Override
    protected Result check() throws Exception {
        Path dump = FileSystems.getDefault().getPath(config.getAnimeDBDump());
        if (!Files.exists(dump)) {
            return Result.unhealthy("AnimeDB dump file " + dump.toString() + " does not exist");
        }
        if (!Files.isReadable(dump) || !Files.isRegularFile(dump)) {
            return Result.unhealthy("AnimeDB dump file '" + dump.toString() + "' is not readable");
        }
        return Result.healthy();
    }
}
