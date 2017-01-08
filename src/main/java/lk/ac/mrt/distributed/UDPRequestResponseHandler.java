package lk.ac.mrt.distributed;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;
import lk.ac.mrt.distributed.api.messages.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Chathura Widanage
 */
public class UDPRequestResponseHandler {
    private final Logger logger = LogManager.getLogger(UDPRequestResponseHandler.class);

    private CountDownLatch countDownLatch;
    private Message request;
    private String response;
    private Node recipient;
    private TimerTask timerTask;
    private Timer timer;
    private AtomicBoolean handled = new AtomicBoolean(false);
    private AtomicBoolean failed = new AtomicBoolean(false);
    private int retries;
    private int retryCount = 0;
    private int timeout = 10000;//10 seconds
    private NodeOpsUDPImpl udp;

    /**
     * This method will set the response and also will count the latch down
     *
     * @param response
     */
    public void setResponse(String response) {
        this.response = response;
        this.countDownLatch.countDown();
    }

    public String getResponse() {
        return response;
    }

    public Node getRecipient() {
        return recipient;
    }

    public UDPRequestResponseHandler(final Node recipient, final Message request, final NodeOpsUDPImpl udpImpl, final int retires, final int timeout) {
        this.udp = udpImpl;
        this.retries = retires;
        this.timeout = timeout;
        this.countDownLatch = new CountDownLatch(1);
        this.request = request;
        this.recipient = recipient;
        this.timer = new Timer();
        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!handled.get()) {
                    if (retryCount < retires) {
                        logger.info("Retrying '{}' to {}. {} of {} time", request.getSendableString(), recipient.toString(), retryCount + 1, retires);
                        retryCount++;
                        try {
                            udp.send(recipient, request);
                        } catch (CommunicationException e) {
                            logger.error("Retrying failed", e);
                        }
                    } else {
                        //fail
                        logger.info("All({}) retries of {} to {} failed.", retires, request.getSendableString(), recipient.toString());
                        failed.set(true);
                        timer.cancel();
                        timerTask.cancel();
                        countDownLatch.countDown();
                    }
                }
            }
        };
        timer.schedule(this.timerTask, timeout, timeout);
    }

    public UDPRequestResponseHandler(Node recipient, Message request, NodeOpsUDPImpl udp) {
        this(recipient, request, udp, 3, 10000);
    }

    public void send() throws CommunicationException, InterruptedException {
        udp.send(recipient, request);
        this.countDownLatch.await();
        if (failed.get()) {
            throw new CommunicationException("Request timeout");
        }
        handled.set(true);
        timerTask.cancel();
        timer.cancel();
    }
}
