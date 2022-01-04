package com.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OnASchedule {

	private static final Log LOGGER = LogFactory.getLog(OnASchedule.class);

	private int i = 0;
	@Scheduled(fixedDelayString = "5000")
	public void publishHeartbeat() {
		++i;
		LOGGER.info("Message #" + i);
	}
}
