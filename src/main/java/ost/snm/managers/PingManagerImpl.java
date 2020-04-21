package ost.snm.managers;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ost.snm.configuration.ConfigurationRepository;
import ost.snm.contracts.PingManager;
import ost.snm.exceptions.SegmentDataException;
import ost.snm.model.Segment;
import ost.snm.model.Server;
import ost.snm.model.wrappers.PingWrapper;
import ost.snm.model.wrappers.SegmentWrapper;
import ost.snm.model.wrappers.ServerWrapper;
import ost.snm.model.wrappers.SynchronizedPingObjects;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

@Component
public class PingManagerImpl implements PingManager {
    final private ConfigurationRepository configurationRepository;
    private SynchronizedPingObjects synchronizedPingObjects ;
    private ExecutorService pingExec;
    private Timer scanTimer;
    private ScanTask scanTask;

    @Autowired
    public PingManagerImpl(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Override
    public Collection<PingWrapper> getInProgressObjects() {
        return null;
    }

    @Override
    public void add(@NonNull Segment segment) {

    }

    @Override
    public void add(@NonNull Server server, String segment) throws SegmentDataException {

    }

    @Override
    public void update(@NonNull Segment segment) {

    }

    @Override
    public void update(@NonNull Server server, String segment) throws SegmentDataException {

    }

    @Override
    public void delete(@NonNull Segment segment) {

    }

    @Override
    public void delete(@NonNull Server server, String segment) throws SegmentDataException {

    }

    @Override
    public void init() {
        System.out.println("Ping manager is on");
        this.synchronizedPingObjects = new SynchronizedPingObjects();
        this.pingExec = Executors.newFixedThreadPool(this.configurationRepository.getPingThreads());
        this.scanTask = new ScanTask(synchronizedPingObjects, pingExec);
        this.scanTimer = new Timer("scannerTimer");
        this.scanTimer.scheduleAtFixedRate(this.scanTask, 0, this.configurationRepository.getPingDelay());
    }

    private static class ScanTask extends TimerTask {
        final private SynchronizedPingObjects synchronizedPingObjects;
        final private Executor executor;

        public ScanTask(SynchronizedPingObjects synchronizedPingObjects,
                        Executor executor
        ) {
            this.synchronizedPingObjects = synchronizedPingObjects;
            this.executor = executor;
        }

        private boolean isPingAllowed(PingWrapper pingWrapper) {
            return pingWrapper.getTimestamp() <= System.currentTimeMillis();
        }

        public void handlePing(String hash , PingWrapper wrapper) {
            synchronizedPingObjects.markInProcess(hash , wrapper);
            executor.execute(wrapper);
        }

        @Override
        public void run() {
            synchronizedPingObjects.getAvailablePingSnapshot().forEach((entry) -> {
                if(isPingAllowed(entry.getValue()))
                    handlePing(entry.getKey(),entry.getValue());
            });
        }
    }
}
