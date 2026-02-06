package org.winterfell.misc.timer.job;

import org.winterfell.misc.timer.TimerTask;

import java.util.Objects;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since v1.0, 2021/1/27
 */
public abstract class TimerAbstractJob implements TimerJob {

    protected String name;

    protected TimerJobType type;

    protected TimerTask task;

    public TimerAbstractJob(String name, TimerJobType type, TimerTask task) {
        this.name = name;
        this.type = type;
        this.task = task;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TimerJobType getType() {
        return type;
    }

    @Override
    public TimerTask getTask() {
        return task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimerAbstractJob that = (TimerAbstractJob) o;
        return name.equals(that.name) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
