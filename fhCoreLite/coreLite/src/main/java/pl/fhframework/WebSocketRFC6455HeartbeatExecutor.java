package pl.fhframework;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.WebSocketSession;
import pl.fhframework.core.util.EventEmitter;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

/**
 * Created by krzysztof.kobylarek on 2017-03-15.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebSocketRFC6455HeartbeatExecutor implements EventEmitter<Long> {

    public static final String key = WebSocketRFC6455HeartbeatExecutor.class.getName();

    private static long DEFAULT_PING_INTERVAL = 30; // seconds

    @Autowired @Qualifier("webSocketTaskScheduler")
    private ThreadPoolTaskScheduler taskScheduler;

    @Value("${websocket.ping_interval}") // seconds
    private Long pingInterval;

    private ScheduledFuture<?> pingFuture;

    private WebSocketSession webSocketSession;

    private PingTask pingTask = null;

    private boolean cancelScheduling = false;

    private Object pingTaskGuard = new Object();

    private Random random = new Random();

    @Getter
    private List<Consumer<Long>> listeners = new ArrayList<>();

    @PreDestroy
    private void onDestroy(){
        if (pingTask!=null) {
            pingTask.cancel();
        }
        if (pingFuture!=null){
            pingFuture.cancel(true);
        }
    }

    public void setWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public synchronized void schedulePing(){
        if (canSchedule()) {
            if (pingTask == null)
                pingTask = new PingTask();

            if (!pingTask.isScheduled()){
                synchronized (pingTaskGuard) {
                    if (!pingTask.isScheduled()) {
                        pingTask.reset();
                        Date timeToPing = new Date(System.currentTimeMillis()
                                + coalesce(pingInterval, DEFAULT_PING_INTERVAL) * 1000) ;

                        pingFuture = taskScheduler.schedule(pingTask, timeToPing);
                        pingTask.setScheduled(true);
                    }
                }
            }
        }
    }

    public synchronized void cancelPing() {
        if (pingFuture!=null && !pingFuture.isCancelled()){
            pingFuture.cancel(false); // try to complete task
            pingFuture = null;
        }

        if (pingTask!=null){
            pingTask.cancel();
            pingTask = null;
        }
        cancelScheduling = true;
    }

    public void sendPing() {
        try {
            long pingTimestamp = System.currentTimeMillis();
            // ByteBuffer.putLong() -> WebSocket -> ByteBuffer.getLong() gives totally other long value (other bytes, not only order)
            ByteBuffer timestampBuffer = ByteBuffer.wrap(String.valueOf(pingTimestamp).getBytes());
            fireEvent(pingTimestamp);
            webSocketSession.sendMessage(new PingMessage(timestampBuffer));
        } catch (IOException ex){
            cancelPing();
        }
    }

    boolean canSchedule(){
        return canSend() && !cancelScheduling;
    }

    boolean canSend(){
        return webSocketSession.isOpen();
    }

    private class PingTask implements Runnable {

        private boolean sendFlag;
        private boolean scheduled;

        void reset(){
            sendFlag=true; scheduled=false;
        }

        @Override
        public void run() {

            try {
                if (sendFlag && canSend()) {
                    sendPing();
                }
            }  finally {
                sendFlag=false;
                scheduled=false;
                schedulePing();
            }
        }

        void cancel(){
            sendFlag=false;
        }

        void setScheduled(boolean flag){
            scheduled=flag;
        }

        public boolean isScheduled() {
            return scheduled;
        }
    }

    static private <T> T coalesce(T... ts){
        for (T t : ts){
            if (t!=null)
                return t;
        }
        return null;
    }
}
