package io.github.leungkay.keykit.log;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>此工具类用来减轻统计执行时长的代码量，不能用来替代正常日志打印代码。</p>
 * <p>ping和pong必须成对出现，否则会造成内存泄漏；如果因为程序执行过程中报错造成的ping未闭合，则120分钟（默认）后在执行下次ping时清除。
 * 即：ping和pong可以统计的最大时差为120分钟。</p>
 * <p>此工具是线程隔离的，多线程执行时长的统计需要自行实现。</p>
 */
public class PingPong {
    private static final Object LOCK = new Object();
    private final Logger log;
    private final LogLevel level;
    private final Map<String, Life> lives;
    private final long maxCharSize;
    private long currentCharSize;

    /**
     * @param log            打印日志的对象
     * @param level          日志打印的级别，默认DEBUG级别
     * @param charSize       要打印的内容缓存在内存中，最多缓存多少个字符，默认10MB
     */
    public PingPong(Logger log, LogLevel level, long charSize) {
        this.log = log;
        this.level = level;
        this.maxCharSize = charSize;
        this.lives = new HashMap<>();
    }

    public PingPong(Logger log, LogLevel level) {
        this(log, level, 10_000_000L);
    }

    public PingPong(Logger log) {
        this(log, LogLevel.DEBUG);
    }

    /**
     * <p><i>顺序执行</i>的情况下ping用在开始计时的语句前，打印开始执行时间。</p>
     * <p><i>循环执行</i>的情况下ping用在循环内，标记计时开始，会被多次调用但只有第一次执行才打印。</p>
     * <p>为避免break或者报错等异常情况，在ping方法中存在清理过期数据的逻辑，默认会清理120分钟未更新的key。</p>
     *
     * @param message 作为key，同一对ping和pong一致，不同对的ping和pong不能相同
     * @return 当前执行时间
     */
    public LocalDateTime ping(String message) {
        return ping(message, false);
    }

    public LocalDateTime ping(String message, boolean silent) {
        String key = message + Thread.currentThread().getName();
        // 某个线程中第一次标记此message 则打印start...
        // 如果是循环语句中 只有第一次执行才打印start...
        Life life = lives.get(key);
        if (life != null) {
            life.setNow(LocalDateTime.now());
        } else {
            // 保证计算总长度的原子性
            synchronized (LOCK) {
                // 初始化之前先判断容量
                if (currentCharSize + key.length() <= maxCharSize) {
                    lives.put(key, (life = new Life()));
                    currentCharSize += key.length();
                    if (!silent) {
                        log(message, life.getNow(), null, null);
                    }
                }
            }
        }
        if (life != null) {
            // 统计循环执行次数
            life.increase();
            return life.getNow();
        }
        return null;
    }

    /**
     * <p><i>顺序执行</i>的情况下<b>*不应*</b>使用此方法。</p>
     * <p><i>循环执行</i>的情况下用在循环内，ping方法之后，标记计时结束，
     * 会被多次调用但只有指定执行次数后才打印（如果每次都打印可以执行pongCycle=1）。</p>
     *
     * @param message   作为key，同一对ping和pong一致，不同对的ping和pong不能相同
     * @param pongCycle 指定执行多少次打印一次日志
     * @return 累加的执行总时长
     */
    public Duration pong(String message, int pongCycle) {
        return pong(message, pongCycle, false);
    }

    public Duration pong(String message, int pongCycle, boolean silent) {
        String key = message + Thread.currentThread().getName();
        Life life = lives.get(key);
        if (life != null) {
            // 有存储过的duration则累加
            life.increase(LocalDateTime.now());
            // 满足打印的循环次数才打印
            Integer cycle = life.getCycle();
            if (!silent && pongCycle > 0 && cycle != null && cycle > 0 && cycle % pongCycle == 0) {
                // 这里传now是为了区分循环中的日志打印和结束时的日志打印
                log(message, life.getNow(), life.getBetween(), life.getCycle());
            }
            return life.getBetween();
        } else {
            log(message, null, null, null);
            return null;
        }
    }

    /**
     * <p><i>顺序执行</i>的情况下pong用在结束计时的语句后，打印执行时长。</p>
     * <p><i>循环执行</i>的情况下pong用在循环体外，紧跟在循环体后面，打印执行总时长和执行总次数。</p>
     * <p>pong是用来闭合ping方法，必须与ping成对出现（循环执行的情况下会执行多次ping之后执行一次pong），并清理计时相关数据。</p>
     * <p><b>注意：*如不闭合会存在内存泄漏*</b></p>
     *
     * @param message 作为key，同一对ping和pong一致，不同对的ping和pong不能相同
     * @return 累加的执行总时长
     */
    public Duration pong(String message) {
        return pong(message, false);
    }

    public Duration pong(String message, boolean silent) {
        String key = message + Thread.currentThread().getName();
        // 如果存储了duration说明是循环执行的 直接使用
        Life life = lives.get(key);
        if (life != null) {
            Duration between = life.getBetween();
            // 否则是顺序执行的 用当前时间减开始时间作为duration
            if (between == null) {
                // 没有执行for循环但是调了pong
                between = Duration.between(life.getNow(), LocalDateTime.now());
            }
            if (!silent) {
                Integer cycle = life.getCycle();
                // 大于1次作为循环执行的end...打印
                if (cycle != null && cycle > 1) {
                    log(message, null, between, cycle);
                }
                // 否则作为顺序执行的end...打印
                else {
                    log(message, null, between, null);
                }
            }
            synchronized (LOCK) {
                currentCharSize -= key.length();
                lives.remove(key);
            }
            return between;
        } else {
            log(message, null, null, null);
            return null;
        }
    }

    private void log(String message, LocalDateTime now, Duration between, Integer cycle) {
        // 堆栈前3个方法 getStackTrace log ping 下一个是调用的方法
        StackTraceElement element = Thread.currentThread().getStackTrace()[4];
        // 只取类名和方法名
        String[] classPath = element.getClassName().split("\\.");
        String className = classPath[classPath.length - 1];
        String methodName = element.getMethodName();
        // 打印的日志主体
        String text;
        if (between != null) {
            if (now != null) {
                text = String.format("%s PingPong processed [%d] in %s.%s: %s", message, cycle, className, methodName, between);
            } else {
                if (cycle != null) {
                    text = String.format("%s PingPong end [%d] in %s.%s: %s", message, cycle, className, methodName, between);
                } else {
                    text = String.format("%s PingPong end in %s.%s: %s", message, className, methodName, between);
                }
            }
        } else if (now != null) {
            text = String.format("%s PingPong start in %s.%s: %s", message, className, methodName, now);
        } else {
            text = String.format("%s PingPong in %s.%s", message, className, methodName);
        }
        // 日志打印的类型
        switch (level) {
            case INFO:
                log.info(text);
                break;
            case WARN:
                log.warn(text);
                break;
            default:
                log.debug(text);
        }
    }

    public enum LogLevel {
        DEBUG,
        INFO,
        WARN
    }

    @Data
    @Accessors(chain = true)
    static class Life {
        protected LocalDateTime now;
        protected Integer cycle;
        protected Duration between;

        Life() {
            this.now = LocalDateTime.now();
        }

        int increase() {
            if (cycle == null) cycle = 1;
            else cycle++;
            return cycle;
        }

        Duration increase(@NonNull LocalDateTime currentTime) {
            Duration duration = Duration.between(now, currentTime);
            if (between == null) between = duration;
            else between = between.plus(duration);
            return between;
        }
    }
}
