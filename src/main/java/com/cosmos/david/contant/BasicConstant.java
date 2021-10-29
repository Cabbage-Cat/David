package com.cosmos.david.contant;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.cosmos.david.contant.Interval.INTERVALS;
import static com.cosmos.david.contant.Interval.INTERVALS_LST;

@Component
public class BasicConstant implements InitializingBean {
    public static final int WEIGHT_LIMIT_TYPE_BY_API_IP = 1;
    public static final int WEIGHT_LIMIT_TYPE_BY_SAPI_IP = 2;
    public static final int WEIGHT_LIMIT_TYPE_BY_SAPI_UID = 3;

    // Default fetch 1 year KLines
    public static final Duration DEFAULT_DURATION = Duration.ofDays(365);

    // X-MBX-USED-WEIGHT-(intervalNum)(intervalLetter)
    public static final int API_WEIGHT_LIMIT_PER_MIN_BY_IP = 1200;

    // X-SAPI-USED-IP-WEIGHT-1M=<value>
    public static final int SAPI_WEIGHT_LIMIT_PER_MIN_BY_IP = 12000;

    // X-SAPI-USED-UID-WEIGHT-1M=<value>
    public static final int SAPI_WEIGHT_LIMIT_PER_MIN_BY_UID = 180000;

    // X-MBX-ORDER-COUNT-(intervalNum)(intervalLetter)

    public static Map<String, Long> INTERVAL_TO_MS = new HashMap<>();

    public static int FETCH_KLINE_MAX_LIMIT_PER_REQ = 1000;
    @Override
    public void afterPropertiesSet() throws Exception {
        for (int i = 0; i < INTERVALS_LST.size(); i++) {
            long ms = 0;
            String s = INTERVALS_LST.get(i);
            long base = Long.parseLong(s.substring(0, s.length() - 1));
            if (i < 5) {
                ms = base * 60000L;
            }
            else if (i < 11) {
                ms = base * 3600000L;
            }
            else if (i < 13) {
                ms = base * 86400000L;
            }
            else if (i == 13) {
                ms = base * 604800016L;
            }
            else {
                ms = base * 2629800000L;
            }
            INTERVAL_TO_MS.put(s, ms);
        }
    }
}
